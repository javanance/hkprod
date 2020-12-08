package com.gof.process;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.gof.entity.BizDiscRateFwdSce;
import com.gof.interfaces.IIntRate;
import com.gof.util.EsgConstant;
import com.gof.util.FinUtils;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> Curve Driver 의 이동평균을 기반으로 공시이율 통계모형이 산출하므로 공시이율 통계모형의 적용 단계에서도 이동평균을 적용하는 게 타당함.
 *  <p> 이를 위해 Curve Driver 의 현재 이동평균, 미래의 이동평균 값을 산출함. 
 *  <p>  1. Curve Driver 의 특정 Tenor 의 과거 데이터 추출  ( 예: 24개월 이동평균이 적용되는 경우 24개월이전~ 현재까지 데이터 추출)
 *  <p>  2. 미래의 이동평균은 과거 데이터와 현재 Term Structure 의 월별 Tenor 의 선도금리를 적용하여 산출 
 *  <p>  2.1  1개월후의 이동평균 : 과거 23개월 ~현재 , 1개월 선도금리의 평균으로 산출함.  
 *  <p>  2.2  2개월후의 이동평균 : 과거 22개월 ~현재 , 1개월 선도금리, 2개월 선도금리의 평균으로 산출함.  
 *  <p>  2.3  24개월후의 이동평균 : 1개월 선도금리, 2개월 선도금리, ... 24개월 선도금리의 평균으로 산출함.  
 *  <p>  이후의 이동평균 금리는 미래 선도금리들의 이동평균임...
 *   
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job33_DiscRateFwdGen {
	
	public static List<BizDiscRateFwdSce> createBizDiscRateFwdSce(String bssd, String bizDv, String irCurveId, String sceNo, String matCd, int monNum, List<? extends IIntRate> termStructure, List<? extends IIntRate> pastTs){
		log.info("Async :  {}, {}", sceNo, Thread.currentThread().getName());
		List<BizDiscRateFwdSce> rst = new ArrayList<BizDiscRateFwdSce>();
		
//		Map<String, Double> tempMap = convertToFwdTimeSeriesFromTermStructure(bssd, matCd, termStructure.stream().collect(toMap(s->s.getMatCd(), s-> s.getIntRate())));

		Map<String, Double> termStrMap = termStructure.stream().collect(toMap(s->s.getMatCd(), s-> s.getIntRate()));
		Map<String, Double> fwdTsMap = convertToFwdTsByBaseYymm(bssd, matCd, termStrMap);
		
		Map<String, Double> fullFwdTsMap = new TreeMap<String, Double>(fwdTsMap); 		//key 로 Sorting

		for(IIntRate aa : pastTs) {
			fullFwdTsMap.put(aa.getBaseYymm(), aa.getIntRate());
		}
			
		rst.addAll(createDiscFwd(bssd, bizDv, irCurveId, sceNo, matCd, monNum, fullFwdTsMap));
		return rst;
	}

	
	/**
	 * TermStructure 로 부터 Forward rate 산출 : Map<ForwardTerm, ForwardRate>
	 * curveMap 은 1~1000 회차의 Full Tenor 를 가진 TermStructure 임 : 모든 회차의 선도금리 산출을 위해!!!
	 *  ==> 과거 Tenor 금리를 위해 Forward Term 을 baseYymm 으로 변환함.
	 */
		private static Map<String, Double> convertToFwdTsByBaseYymm(String bssd,String matCd,  Map<String, Double> curveMap) {
			Map<String, Double> rstMap = new HashMap<String, Double>();
			
			double intRate =0.0;
			double nearIntFactor =0.0;
			double farIntFactor  =0.0;
			double intFactor  =0.0;
			int matNum  = Integer.valueOf(matCd.substring(1)) ;
			int farNum  ; 
			
			for(int i =0; i<=curveMap.size(); i++) {
				farNum = matNum + i;
				String nearMatCd =  "M" + String.format("%04d", i);
				String farMatCd  =  "M" + String.format("%04d", farNum);
				
				nearIntFactor = Math.pow(1+ curveMap.getOrDefault(nearMatCd, 0.0), i/12.0);
					
				if(curveMap.containsKey(farMatCd)) {
					farIntFactor  = Math.pow(1+ curveMap.get(farMatCd), farNum/12.0);
					intFactor = nearIntFactor==0.0? farIntFactor: farIntFactor / nearIntFactor;
					intRate= Math.pow(intFactor, 12.0/matNum) - 1.0 ;
				}
				else {
	//				intRate = curveMap.get(nearMatCd);
				}
	
					
	//			logger.info("near Factor : {},{},{},{},{},{},{}", curveMap.get(nearMatCd),nearIntFactor, curveMap.get(farMatCd), farIntFactor, intFactor, intRate, matNum);
	//			rstMap.put(nearMatCd, intRate ); 
				rstMap.put(FinUtils.addMonth(bssd, i), intRate );  // Convert to nearTerm BaseYymm
			}
	//			rstMap.entrySet().forEach(entry -> logger.info("forwardMap : {},{}", entry.getKey(), entry.getValue()));
			return rstMap;
		}


	/**
	 * 과거 Tenor 별금리 & 미래 선도금리 Map<String, Double> 의 이동평균 산출
	 */
	
	private static List<BizDiscRateFwdSce> createDiscFwd(String bssd, String bizDv, String irCurveId, String sceNo, String matCd,  int monNum, Map<String, Double> timeSeriesMap){
			List<BizDiscRateFwdSce> rst = new ArrayList<BizDiscRateFwdSce>();
			BizDiscRateFwdSce temp;
			
			int projYear = Integer.valueOf(EsgConstant.getStrConstant().getOrDefault("PROJECTION_YEAR", "101"));
			
			String keyBssd="";
			String stBssd="";
			double fwdRate=0.0;
			double sumfwdRate=0.0;
			double avgFwdRate=0.0;
			int cnt = 1;
			
			for(int i=0; i< projYear * 12 ; i++) {
				keyBssd = FinUtils.addMonth(bssd, i);
				stBssd  = FinUtils.addMonth(keyBssd, monNum);
				fwdRate = timeSeriesMap.get(keyBssd);
				cnt =1;
				sumfwdRate =0.0;
				for(Map.Entry<String, Double> entry : timeSeriesMap.entrySet()) {
					if(stBssd.compareTo(entry.getKey()) < 0 && entry.getKey().compareTo(keyBssd) <= 0) {
	//					log.info("aaa :  {},{},{},{},{},{}", cnt, entry.getKey(), keyBssd, stBssd, stBssd.compareTo(entry.getKey()), entry.getKey().compareTo(keyBssd));
						sumfwdRate = sumfwdRate + entry.getValue(); 
						if(cnt == -1*monNum) {
	//						log.info("bbb :  {},{},{},{},{}",cnt,  sumfwdRate, sumfwdRate /cnt);
							break;
						}
						cnt++;
					}
				}
				avgFwdRate = sumfwdRate /cnt;
				
				temp = new BizDiscRateFwdSce();
				temp.setBaseYymm(bssd);
				temp.setApplyBizDv(bizDv);
				temp.setIrCurveId(irCurveId);
				temp.setSceNo(sceNo);
				temp.setMatCd(matCd);
				temp.setFwdNo(String.valueOf(i));
				temp.setFwdRate(fwdRate);
				temp.setAvgFwdRate(avgFwdRate);
//				temp.setAvgFwdRate(0);
				temp.setAvgMonNum(-1.0* monNum);
				temp.setLastModifiedBy("ESG");
				temp.setLastUpdateDate(LocalDateTime.now());
				
				rst.add(temp);
			}
			return rst;
	}
	
	
	
	
//	private static Map<String, Double> convertToFwdTimeSeriesFromTermStructure(String bssd, String matCd, Map<String, Double> curveMap){
//		Map<String, Double> rstMap = new HashMap<String, Double>();
//		
//		Map<String, Double> fwdMap =getForwardRateByMaturity(bssd, matCd, curveMap);
//		
//		for(Map.Entry<String, Double> entry : fwdMap.entrySet()) {
//			int fwdNum = Integer.parseInt(entry.getKey().substring(1));
//			rstMap.put(FinUtils.addMonth(bssd, fwdNum), entry.getValue());
//		}
//		return rstMap;
//	}
	
	/**
	 * TermStructure 로 부터 Forward rate 산출 : Map<ForwardTerm, ForwardRate>
	 * curveMap 은 1~1000 회차의 Full Tenor 를 가진 TermStructure 임 : 모든 회차의 선도금리 산출을 위해!!!
	 *  ==> 과거 Tenor 금리를 위해 Forward Term 을 baseYymm 으로 변환함.
	 */
	
//	private static Map<String, Double> getForwardRateByMaturity(String bssd,String matCd,  Map<String, Double> curveMap) {
//		Map<String, Double> rstMap = new HashMap<String, Double>();
//		
//		double intRate =0.0;
//		double nearIntFactor =0.0;
//		double farIntFactor  =0.0;
//		double intFactor  =0.0;
//		int matNum  = Integer.valueOf(matCd.substring(1)) ;
//		int farNum  ; 
//		
//		for(int i =0; i<=curveMap.size(); i++) {
//			farNum = matNum + i;
//			String nearMatCd =  "M" + String.format("%04d", i);
//			String farMatCd  =  "M" + String.format("%04d", farNum);
//			
//			nearIntFactor = Math.pow(1+ curveMap.getOrDefault(nearMatCd, 0.0), i/12.0);
//				
//			if(curveMap.containsKey(farMatCd)) {
//				farIntFactor  = Math.pow(1+ curveMap.get(farMatCd), farNum/12.0);
//				intFactor = nearIntFactor==0.0? farIntFactor: farIntFactor / nearIntFactor;
//				intRate= Math.pow(intFactor, 12.0/matNum) - 1.0 ;
//			}
//			else {
////				intRate = curveMap.get(nearMatCd);
//			}
//
//				
////			logger.info("near Factor : {},{},{},{},{},{},{}", curveMap.get(nearMatCd),nearIntFactor, curveMap.get(farMatCd), farIntFactor, intFactor, intRate, matNum);
//			rstMap.put(nearMatCd, intRate );
//		}
////			rstMap.entrySet().forEach(entry -> logger.info("forwardMap : {},{}", entry.getKey(), entry.getValue()));
//		return rstMap;
//	}
}
