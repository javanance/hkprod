package com.gof.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.gof.dao.BizDiscountRateDao;
import com.gof.dao.DiscRateStatsDao;
import com.gof.entity.BizDiscRateFwdSce;
import com.gof.entity.BizDiscRateStat;
import com.gof.entity.BizDiscountRate;
import com.gof.util.FinUtils;

import lombok.extern.slf4j.Slf4j;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class BizDiscFwdRateModel {
	
	public static List<BizDiscRateFwdSce> getDiscFwdRateAsync(String bssd, String bizDv, String irCurveId, boolean isRiskFree, ExecutorService exe){
		
		List<BizDiscountRate> pastIntRateAll = BizDiscountRateDao.getTimeSeries(bssd, bizDv, irCurveId, -36);
		
		List<BizDiscountRate> dcntRateList = BizDiscountRateDao.getTermStructure(bssd, bizDv, irCurveId);
		
		Collection<BizDiscRateStat> bizStatList = DiscRateStatsDao.getBizDiscRateStat(bssd).stream()
												.filter(s-> s.getApplyBizDv().equals(bizDv))
												.collect(Collectors.toMap(s ->s.getIndpVariable(), Function.identity(), (p1, p2) -> p1))
												.values();
			
		bizStatList.forEach(s -> log.info("Stat : {}, {},{}", s.toString(), dcntRateList.size(), pastIntRateAll.size()));
		
		List<CompletableFuture<List<BizDiscRateFwdSce>>> sceJobFutures =  bizStatList.stream()
					.map(stat -> CompletableFuture.supplyAsync(() -> getDiscFwdRate(bssd, bizDv, stat, irCurveId, isRiskFree, "0", dcntRateList, pastIntRateAll), exe))
					.collect(Collectors.toList());

		List<BizDiscRateFwdSce> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
		
		log.info("BizDiscFwdRateSce for scenario {} is calculated. {} Results are inserted into BIZ_DISC_FWD_RATE_SCE table", bizDv, rst.size());
		return rst;
		
	}
	
	public static List<BizDiscRateFwdSce> getDiscFwdRate(String bssd, String bizDv, BizDiscRateStat stat, String irCurveId, boolean isRiskFree, String sceNo, List<BizDiscountRate> dcntRateList, List<BizDiscountRate> pastIntRateAll){
		List<BizDiscRateFwdSce> rstList = new ArrayList<BizDiscRateFwdSce>();
		Map<String, Double> fwdMap = new HashMap<String,  Double>();
		Map<String, Double> termStructureMap = new HashMap<String,  Double>();
		
		BizDiscRateFwdSce temp;
		
		int k = -1* stat.getAvgMonNum().intValue();
		String matCd =stat.getIndiVariableMatCd();
//		logger.info("MatCd : {},{},{}", matCd, stat.getIntRateCd(), k);
		
		if(isRiskFree) {
			termStructureMap = dcntRateList.stream().collect(Collectors.toMap(s->s.getMatCd(), s->s.getRfRate()));
		}
		else {
			termStructureMap = dcntRateList.stream().collect(Collectors.toMap(s->s.getMatCd(), s-> s.getRiskAdjRfRate()))	;
		}
		
		fwdMap = getForwardRateByMaturity(bssd, termStructureMap, matCd); 
		
		if(termStructureMap.containsKey(matCd)) {
			fwdMap.put("M0000", termStructureMap.get(matCd));
		}
		
		List<BizDiscountRate> pastIntRate = pastIntRateAll.stream().filter(s ->matCd.equals(s.getMatCd())).collect(Collectors.toList());
		
		String fwdMatCd ="";
		double avgFwdRate=0.0;
		double fwdRate =0.0;
		for(int i =0; i<= 1200; i++) {
			avgFwdRate =0.0;
			
			fwdMatCd = "M" +String.format("%04d", i);
			fwdRate = fwdMap.getOrDefault(fwdMatCd, 0.0);
//			avgFwdRate = getAvgFwdRate(bssd, i, k, fwdMap,pastIntRate, isRiskFree);
			if(k >= -1) {
				avgFwdRate = fwdRate;
			}
			else {
				avgFwdRate = getAvgFwdRate(bssd, fwdMatCd, k, fwdMap,pastIntRate, isRiskFree);
			}
			
			temp = new BizDiscRateFwdSce();
			temp.setBaseYymm(bssd);
			temp.setApplyBizDv(bizDv);
			temp.setIrCurveId(irCurveId);
			temp.setSceNo(sceNo);
			temp.setFwdNo(String.valueOf(i));
//			temp.setFwdNo("M" +String.format("%04d", i));
			
			
			temp.setMatCd(stat.getIndiVariableMatCd());
			
			temp.setFwdRate(fwdRate);
			temp.setAvgFwdRate(avgFwdRate);

			if(isRiskFree) {
				temp.setRiskAdjFwdRate(0.0);
			}else {
				temp.setRiskAdjFwdRate(fwdRate);
			}
			
			temp.setLastModifiedBy("ESG");
			temp.setLastUpdateDate(LocalDateTime.now());
			
			rstList.add(temp);
			
		}
		
//		rstList.stream().forEach(s->logger.info("Accrete  Rate for IFRS17 Result : {}", s.toString()));
		return rstList;
		
		
	}
	
	private static double getAvgFwdRate(String bssd, String fwdMatCd, int avgNum, Map<String, Double> fwdRateMap, List<BizDiscountRate> dcntRateSeries, boolean isRiskFree){
//	private static double getAvgFwdRate(String bssd, int fwdNo, int avgNum, Map<String, Double> fwdRateMap, List<BizDiscountRate> dcntRateList, boolean isRiskFree){
		Map<String, Double> pastRateMap = new HashMap<String,  Double>();
		Map<String, Double> avgMap = new HashMap<String,  Double>();
		
		int matNum  = Integer.valueOf(fwdMatCd.substring(1)) ;
		String avgStartMatCd = "M" +String.format("%04d", matNum + avgNum);
		String barrier = FinUtils.addMonth(bssd, avgNum + matNum +1);
//		String barrier = FinUtils.addMonth(bssd, avgNum + fwdNo);
		
//		logger.info("barrier : {},{}, {}", matNum, barrier, avgStartMatCd);
		
		if(isRiskFree) {
			pastRateMap = dcntRateSeries.stream().filter(s -> FinUtils.monthBetween(barrier, s.getBaseYymm()) >=0).collect(Collectors.toMap(s->s.getBaseYymm(), s->s.getRfRate()));
		}
		else {
			pastRateMap = dcntRateSeries.stream().filter(s -> FinUtils.monthBetween(barrier, s.getBaseYymm()) >=0).collect(Collectors.toMap(s->s.getBaseYymm(), s-> s.getRiskAdjRfRate()))	;
		}
		
		
		avgMap.putAll(pastRateMap);
//		avgMap.putAll(fwdRateMap.entrySet().stream().filter(s-> Integer.valueOf(s.getKey().substring(1)) <= fwdNo).collect(Collectors.toMap(s->s.getKey(), s-> s.getValue())));
		avgMap.putAll(fwdRateMap.entrySet().stream().filter(s-> s.getKey().compareTo(fwdMatCd)<=0)
													.filter(s-> s.getKey().compareTo(avgStartMatCd ) >0)
													.collect(Collectors.toMap(s->s.getKey(), s-> s.getValue())));
		
//		avgMap.entrySet().stream().forEach(entry -> logger.info("AvgMap :{},{}", entry.getKey(), entry.getValue()));
		
		int cnt =0;
		double sum =0.0;
		for(Map.Entry<String, Double> entry: avgMap.entrySet()) {
			sum = sum + entry.getValue();
			cnt= cnt+1;
		}
//		logger.info("avgMap2 : {},{},{},{},{}", pastRateMap.size(),fwdRateMap.size(), avgMap.size(), sum, cnt);
		return sum/ cnt;
		
	}
	
	private static Map<String, Double> getForwardRateByMaturity(String bssd, Map<String, Double> curveMap, String matCd) {
		Map<String, Double> rstMap = new HashMap<String, Double>();
		
		double intRate =0.0;
		double nearIntFactor =0.0;
		double farIntFactor  =0.0;
		double intFactor  =0.0;
		int matNum  = Integer.valueOf(matCd.substring(1)) ;
		int farNum  ; 
		
		for(int i =1; i<=1200; i++) {
			farNum = matNum + i;
			String nearMatCd =  "M" + String.format("%04d", i);
			String farMatCd  =  "M" + String.format("%04d", farNum);
			
			if(!curveMap.containsKey(nearMatCd)) {
				return rstMap;
			}
			else {
				nearIntFactor = Math.pow(1+ curveMap.get(nearMatCd), i/12.0);
				
				if(curveMap.containsKey(farMatCd)) {
					farIntFactor  = Math.pow(1+ curveMap.get(farMatCd), farNum/12.0);
					intFactor = nearIntFactor==0.0? farIntFactor: farIntFactor / nearIntFactor;
					intRate= Math.pow(intFactor, 12.0/matNum) - 1.0 ;
				}
				else {
//					intRate = curveMap.get(nearMatCd);
				}

				
//				logger.info("near Factor : {},{},{},{},{},{},{}", curveMap.get(nearMatCd),nearIntFactor, curveMap.get(farMatCd), farIntFactor, intFactor, intRate, matNum);
				rstMap.put(nearMatCd, intRate );
			}
		}
//		rstMap.entrySet().forEach(entry -> logger.info("forwardMap : {},{}", entry.getKey(), entry.getValue()));
		return rstMap;
	}
	
}
