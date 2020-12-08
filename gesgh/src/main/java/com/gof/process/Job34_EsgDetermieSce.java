package com.gof.process;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao.BizDiscountRateDao;
import com.gof.entity.BizDiscountRate;
import com.gof.entity.BizDiscountRateSce;
import com.gof.entity.BizStockYield;
import com.gof.entity.IrCurve;
import com.gof.entity.StdAssetMst;

import lombok.extern.slf4j.Slf4j;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job34_EsgDetermieSce {
	
	public static Stream<BizStockYield> createDetermineSce(String bssd, IrCurve curveMst, List<StdAssetMst> asstCdList) {
		
		return BizDiscountRateDao.getTermStructure(bssd, curveMst.getApplBizDv(), curveMst.getIrCurveId()).stream()
								
								.flatMap(s->build(bssd, s, asstCdList))
								;
	}
	
	public static Stream<BizStockYield> createDetermineSce1(String bssd, IrCurve curveMst, List<StdAssetMst> asstCdList) {
		
		List<BizStockYield> rstList = new ArrayList<BizStockYield>();
		String bizDv = curveMst.getApplBizDv();
		
		Map<String, BizDiscountRate> bizRateMap = BizDiscountRateDao.getTermStructure(bssd, bizDv, curveMst.getIrCurveId()).stream()
												.collect(toMap(BizDiscountRate::getMatCd, Function.identity()));
		
		
		for(StdAssetMst asstCd : asstCdList) {
			int durationMonth =1;
			if(asstCd.getStdAsstCd().contains("1Y")){
				durationMonth =  12;
			}
			else if(asstCd.getStdAsstCd().contains("3Y")){
				durationMonth =  36;
			}
			else if(asstCd.getStdAsstCd().contains("5Y")){
				durationMonth =  60;
			}
			
//			int durationMonth =  asstCd.getStdAsstTypCd().contains("BOND")? 36	: 1;
			
			for(int i=0 ; i< bizRateMap.size(); i++) {
				List<BizDiscountRate> bizRateList = new ArrayList<BizDiscountRate>();
				String matCd = "M"+ String.format("%04d", i+1);
				int maxIndex = Math.min(bizRateMap.size(), i + durationMonth);
				
				for(int j=i  ; j < maxIndex ; j++) {
					String tempMatCd = "M"+ String.format("%04d", j+1);
//					log.info("aaaa: {},{}", j, tempMatCd);
					bizRateList.add(bizRateMap.get(tempMatCd));
				}
//				rstList.add(build(bssd, bizDv, matCd, bizRateList, asstCd, durationMonth));
				
				
				Map<Integer, Double> bizFwdRateMap  = new HashMap<Integer, Double>();
				for(int j=i  ; j < i+durationMonth ; j++) {
					int minIndex = Math.min(bizRateMap.size(), j+1);
					String tempMatCd = "M"+ String.format("%04d", minIndex);
					log.info("aaaa: {},{}", j, tempMatCd);
					bizFwdRateMap.put(j, bizRateMap.get(tempMatCd).getRiskAdjRfFwdRate());
				}
				
				
				rstList.add(build(bssd, bizDv, matCd, bizFwdRateMap, asstCd, durationMonth));
				log.info("aaaaaa : {},{},{},{},{}", asstCd.getStdAsstCd(),   bizRateList.size());
			}
		}
		
		return rstList.stream();
	}

	private static BizStockYield build(String bssd, String bizDv, String matCd, List<BizDiscountRate> bizRateList, StdAssetMst asstMst, int durationMonth){
		
		double df =1.0;
		
		for(BizDiscountRate aa : bizRateList) {
			df = df * Math.pow(1 + aa.getRiskAdjRfFwdRate(), -1.0 /12.0);
		}
		double ytm = Math.pow(df, -12.0 / durationMonth) -1;
//		log.info("aaaaaa : {},{},{},{},{}", asstMst.getStdAsstCd(),  matCd, bizRateList.size(), df, ytm);
		return BizStockYield.builder()
								.baseYymm(bssd)
								.applBizDv(bizDv)
								.stdAsstCd(asstMst.getStdAsstCd())
								.fwdMatCd(matCd)
								.asstYield(Math.pow(1+ ytm, 1.0/12.0) -1)
								.lastModifiedBy("ESG_34")
								.lastUpdateDate(LocalDateTime.now())
								.build()
						;
		

	}
	
	private static BizStockYield build(String bssd, String bizDv, String matCd, Map<Integer,Double> bizFwdRateMap, StdAssetMst asstMst, int durationMonth){
		
		double df =1.0;
		
		for(Map.Entry<Integer, Double> entry : bizFwdRateMap.entrySet()) {
			df = df * Math.pow(1 + entry.getValue(), -1.0 /12.0);
		}
		double ytm = Math.pow(df, -12.0 / durationMonth) -1;
//		log.info("aaaaaa : {},{},{},{},{}", asstMst.getStdAsstCd(),  matCd, bizRateList.size(), df, ytm);
		return BizStockYield.builder()
								.baseYymm(bssd)
								.applBizDv(bizDv)
								.stdAsstCd(asstMst.getStdAsstCd())
								.fwdMatCd(matCd)
								.asstYield(Math.pow(1+ ytm, 1.0/12.0) -1)
								.lastModifiedBy("ESG_34")
								.lastUpdateDate(LocalDateTime.now())
								.build()
						;
		

	}
	
	private static Stream<BizStockYield> build(String bssd, BizDiscountRate bottomupRate, List<StdAssetMst> asstCdList){
		List<BizStockYield> rstList = new ArrayList<BizStockYield>();
		for(StdAssetMst asst  : asstCdList) {
			rstList.add(BizStockYield.builder()
								.baseYymm(bssd)
								.applBizDv(bottomupRate.getApplyBizDv())
								.stdAsstCd(asst.getStdAsstCd())
								.fwdMatCd(bottomupRate.getMatCd())
								.asstYield(Math.pow(1+bottomupRate.getRiskAdjRfFwdRate(), 1.0/12.0) -1)
								.lastModifiedBy("ESG_34")
								.lastUpdateDate(LocalDateTime.now())
								.build()
						);
		}
		return rstList.stream();
	}
}


