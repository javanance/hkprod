package com.gof.process;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.gof.dao.DiscRateDao;
import com.gof.dao.DiscRateStatsDao;
import com.gof.dao.IrCurveHisDao;
import com.gof.entity.BizDiscRateAdjUd;
import com.gof.entity.BizDiscRateStat;
import com.gof.entity.BizDiscRateStatUd;
import com.gof.entity.DiscRateHis;
import com.gof.entity.DiscRateStats;
import com.gof.entity.IrCurveHis;
import com.gof.util.EsgConstant;
import com.gof.util.FinUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job52_BizDiscRateStat {
	
	public static List<BizDiscRateStat> createIfrsDiscRateStat(String bssd, boolean isFitting) {
		String bizDv ="I";
		String irCurveId = EsgConstant.getStrConstant().getOrDefault("ESG_RF_KRW_ID", "1010000");
		String bizMatCd  = EsgConstant.getStrConstant().getOrDefault("IFRS_DICS_MAT", "M0084");
		String bizAvgNum = EsgConstant.getStrConstant().getOrDefault("IFRS_DICS_AVG", "24");
		
		if(isFitting) {
			return createBizDiscRateStatFitting(bssd, bizDv, irCurveId, bizMatCd, bizAvgNum);
		}
		else {
			return createBizDiscRateStat(bssd, bizDv, irCurveId, bizMatCd, bizAvgNum);
		}
		
	}
	
//	public static List<BizDiscRateStat> createIfrsDiscRateStatFitting(String bssd) {
//		String bizDv ="I";
//		String irCurveId = EsgConstant.getStrConstant().getOrDefault("ESG_RF_KRW_ID", "1010000");
//		String bizMatCd  = EsgConstant.getStrConstant().getOrDefault("IFRS_DICS_MAT", "M0084");
//		String bizAvgNum = EsgConstant.getStrConstant().getOrDefault("IFRS_DICS_AVG", "24");
//		
//		return createBizDiscRateStatFitting(bssd, bizDv, irCurveId, bizMatCd, bizAvgNum);
//	}
	
	public static List<BizDiscRateStat> createKicsDiscRateStat(String bssd, boolean isFitting) {
		String bizDv ="K";
		String irCurveId = EsgConstant.getStrConstant().getOrDefault("ESG_RF_KRW_ID", "1010000");
		String bizMatCd  = EsgConstant.getStrConstant().getOrDefault("KICS_DICS_MAT", "M0001");
		String bizAvgNum = EsgConstant.getStrConstant().getOrDefault("KICS_DICS_AVG", "1");
		
		if(isFitting) {
			return createBizDiscRateStatFitting(bssd, bizDv, irCurveId, bizMatCd, bizAvgNum);
		}
		else {
			return createBizDiscRateStat(bssd, bizDv, irCurveId, bizMatCd, bizAvgNum);
		}
	}
	
//	public static List<BizDiscRateStat> createKicsDiscRateStatFitting(String bssd) {
//		String bizDv ="K";
//		String irCurveId = EsgConstant.getStrConstant().getOrDefault("ESG_RF_KRW_ID", "1010000");
//		String bizMatCd  = EsgConstant.getStrConstant().getOrDefault("KICS_DICS_MAT", "M0001");
//		String bizAvgNum = EsgConstant.getStrConstant().getOrDefault("KICS_DICS_AVG", "1");
//		
//		return createBizDiscRateStatFitting(bssd, bizDv, irCurveId, bizMatCd, bizAvgNum);
//	}
	
	

	private static List<BizDiscRateStat> createBizDiscRateStat(String bssd, String bizDv, String irCurveId, String matCd, String avgNum) {
//		List<BizDiscRateStat> rstList = new ArrayList<BizDiscRateStat>();
//		BizDiscRateStat rst = new BizDiscRateStat();
		
		Map<String, DiscRateStats> ifrsStat =  DiscRateStatsDao.getDiscRateStats(bssd).stream()
																.filter(s->s.getIndiVariableMatCd().equals(matCd))
																.filter(s->s.getAvgNum().equals(Double.valueOf(avgNum)))
																.collect(Collectors.toMap(s->s.getIntRateCd(), Function.identity()));

//		사용자 입력 통계분석 결과가 있으면 우선 처리됨, 사용자 입력이 없으면 경험 통계값으로 설정함. 
		List<BizDiscRateStat> udAppliedRst = decorateBizDiscRateStatUd(bssd, bizDv, ifrsStat);

		return udAppliedRst;		
	}
	
	private static List<BizDiscRateStat> createBizDiscRateStatFitting(String bssd, String bizDv, String irCurveId, String matCd, String avgNum) {
		List<BizDiscRateStat> udAppliedRst = createBizDiscRateStat(bssd, bizDv,irCurveId, matCd, avgNum);
		return  fittingCurrent(bssd, bizDv, irCurveId, matCd, avgNum, udAppliedRst);
		
	}
	
	private static double getPastCurveAvg(String bssd, int monthNum,  String irCurveId, String matCd){
		List<IrCurveHis> curveList = IrCurveHisDao.getCurveHisBetween(bssd, FinUtils.addMonth(bssd, monthNum+1), irCurveId);		//avgNum 갯수만큼 추출
		Map<String, String> eomDate = IrCurveHisDao.getEomMap(bssd, irCurveId);
		
		//TODO : 이동평균 검증을 위한 데이터 print
//		curveList.stream().filter(s -> eomDate.containsValue(s.getBaseDate())).filter(s-> matCd.equals(s.getMatCd()))
//											   .forEach(s-> log.info("rate : {},{}", s.getBaseYymm(), s.getIntRate()));
		Double aaa= curveList.stream().filter(s -> eomDate.containsValue(s.getBaseDate()))
									.filter(s-> matCd.equals(s.getMatCd()))
									.map(s-> s.getIntRate())
									.collect(Collectors.averagingDouble(s->s));

		log.info("zzz : {}", aaa);
		return aaa;
	}
	
	
	private static List<BizDiscRateStat> decorateBizDiscRateStatUd(String bssd, String bizDv, Map<String, DiscRateStats> discRateStatMap) {
		List<BizDiscRateStat> rstList = new ArrayList<BizDiscRateStat>();
		String intRateCd="";

		Map<String, BizDiscRateStatUd> userStatMap = DiscRateStatsDao.getUserDiscRateStat(bssd).stream()
																	 .filter(s->s.getApplyBizDv().equals(bizDv))
																	 .collect(Collectors.toMap(s->s.getIntRateCd(), Function.identity()));

		Map<String, BizDiscRateAdjUd> userAdjtMap = DiscRateStatsDao.getUserDiscRateAdj(bssd).stream()
																	 .filter(s->s.getApplBizDv().equals(bizDv))
																	 .collect(Collectors.toMap(s->s.getIntRateCd(), Function.identity()));
		
		for(Map.Entry<String, DiscRateStats> entry : discRateStatMap.entrySet()) {
			intRateCd = entry.getKey(); 
			BizDiscRateStat tempRst = entry.getValue().convertToBizDiscRateStst(bizDv).decorate(userStatMap.get(intRateCd)).decorate(userAdjtMap.get(intRateCd));
			
			rstList.add(tempRst);
		}
		
		return rstList;
	}	
	
	private static List<BizDiscRateStat> fittingCurrent(String bssd, String bizDv, String irCurveId, String matCd, String avgNum, List<BizDiscRateStat> bizDiscRateStat) {
//		List<BizDiscRateStat> rstList = new ArrayList<BizDiscRateStat>();
		
//		통계모형으로 산출된 결과가 현재의 공시이율과 일치하도록 상수항을 조정함. 다만 사용자 입력 통계분석 or 조정값 입력시 Fitting 하지 않음. (USER)
//		Fitting : 산출된 공시이율 기준이율 = 상수항 + 계수항 * 현행 금리 수준 ( 통계모형에서 적용한 만기 및 이동평균 적용) 
//		<==> 실제 공시기준이율이 같아지도록 상수항 조정... 조정된 상수항 = 실제 공시이율기준이율 - 계수항 * 현행금리 수준
//		TODO : 결과값 일치하도록 fwdNo 조정 필요

		Map<String, DiscRateHis> discRateHisMap = DiscRateDao.getDiscRateHis(bssd).stream().collect(toMap(s-> s.getIntRateCd(), Function.identity()));
		
//		bizDiscRateStat.forEach(s-> log.info("stat : {},{}, {}", s.getIntRateCd(), s.getAdjRate(), s.getLastModifiedBy()));
//		discRateHisMap.entrySet().forEach(s -> log.info("zzz : {},{}", s.getKey(), s.getValue().getBaseDiscRate()));
		
		double avgRate = getPastCurveAvg(bssd, -1*Integer.valueOf(avgNum), irCurveId, matCd);
		
		for(BizDiscRateStat stat : bizDiscRateStat) {
			
			if(!stat.getLastModifiedBy().equals("USER")){
//				log.info("int rate : {},{}", stat.getIntRateCd());
				if(discRateHisMap.containsKey(stat.getIntRateCd()) && discRateHisMap.get(stat.getIntRateCd()).getBaseDiscRate()!=null) {
					double currDiscRate = discRateHisMap.get(stat.getIntRateCd()).getApplDiscRate();
					
					double currBaseRate = discRateHisMap.get(stat.getIntRateCd()).getBaseDiscRate();
					double calcBaseRate = avgRate * stat.getRegrCoef(); 
					
					stat.setRegrConstant(currBaseRate - calcBaseRate);
					stat.setAdjRate(currBaseRate==0? 1.0: currDiscRate /currBaseRate);
					stat.setRemark(String.valueOf(avgRate));
//					log.info("aaa 11:  {},{},{},{}",  currBaseRate, calcBaseRate, rst.getRegrConstant(), avgRate);
				}
			}
			else {
				stat.setAdjRate(1.0);
				log.info("discMap:  {},{},{},{}",  stat.getIntRateCd());
			}
		}
		return bizDiscRateStat;
	}	

}
