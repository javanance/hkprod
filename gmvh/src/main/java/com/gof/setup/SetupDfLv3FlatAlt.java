package com.gof.setup;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.dao.DfDao;
import com.gof.entity.DfLv1CurrRate;
import com.gof.entity.DfLv2Eir;
import com.gof.entity.DfLv2EirNewgoc;
import com.gof.entity.DfLv2InitRate;
import com.gof.entity.DfLv2WghtRate;
import com.gof.entity.DfLv3Flat;
import com.gof.entity.MstGoc;
import com.gof.enums.EBoolean;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupDfLv3FlatAlt {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	private static	String irCurveId	=GmvConstant.IR_CURVE_ID;
	private static int maxRateTenor		=GmvConstant.MAX_RATE_TENOR;
	
//	private static 	Map<String, MstGoc> gocMap = new HashMap<String, MstGoc>();
	
	private static Map<String, Double> currSysMap 		= new HashMap<String, Double>();
	private static Map<String, Double> prevSysMap   	= new HashMap<String, Double>();
	private static Map<String, Double> newCurrSysMap	= new HashMap<String, Double>();
	
	private static Map<String, Map<Double, Double>> irCurveMap = new HashMap<String, Map<Double, Double>>();
	private static Map<String, Map<Double, Double>> initRateMap = new HashMap<String, Map<Double, Double>>();
	private static Map<String, Map<Double, Double>> wgthRateMap = new HashMap<String, Map<Double, Double>>();

	public static Stream<DfLv3Flat> create(){
		List<DfLv3Flat> rstList = new ArrayList<DfLv3Flat>();

		for(MstGoc aa : PrvdMst.getGocList()) {
			rstList.addAll(create(aa));
		}
		return rstList.stream();
	}
	
	public static Stream<DfLv3Flat> create(String gocId){
		MstGoc goc = PrvdMst.getMstGoc(gocId);
		return create(goc).stream();
	}
	
	private static List<DfLv3Flat> create(MstGoc goc){
		List<DfLv3Flat> rstList = new ArrayList<DfLv3Flat>();

		String gocId = goc.getGocId();
		
		Map<Double, Double> gocInitMap 		= getInitRateMap(gocId);
		Map<Double, Double> gocWghtMap 		= getWghtRateMap(gocId);
		
		Map<Double, Double> prevSettleMap 	 = getIrCurve(stBssd);
		
		Map<Double, Double>	secondNewRateMap = getIrCurve(DateUtil.addMonthToString(stBssd, 1));
		Map<Double, Double>	thirdNewRateMap  = getIrCurve(DateUtil.addMonthToString(stBssd, 2));
		
		Map<Double, Double> currSettleMap 	 = getIrCurve(bssd);
		
		Map<String, Double> currSysMap    	 = getCurrSysMap();
		Map<String, Double> prevSysMap    	 = getMergedPrevSysMap();
		
	//	preveSysMap.entrySet().forEach(s->log.info("zzz : {}, {}", s.getKey(), s.getValue()));
	 
	//	log.info("zzzzzz : {},{},{},{}", bssd, stBssd, gocId, irCurveId);
	//	log.info("zzzzzz : {},{}", currEsgRate.size(), prevEsgRate.size());
	//	currEsgRate.entrySet().forEach(s->log.info("entry :  {},{}", s.getKey(), s.getValue()));
			
		
		boolean eirFlag = goc==null? false : goc.getEirYn().isTrueFalse();
		boolean ociFlag = goc==null? false : goc.getOciYn().isTrueFalse();

		double currRate=0.0;
		double prevRate=0.0;
		double initRate=0.0;
		double currWghtRate=0.0;
		double prevSysRate=0.0;
		double currSysRate=0.0;
		
		double firstNewRate=0.0;
		double secondNewRate=0.0;
		double thirdNewRate=0.0;
		
		double tenor 				=0.0;
		double prevTenor 			=0.0;
		double firstNewContTenor 	=0.0;
		double secontNewContTenor 	=0.0;
		double thirdNewContTenor 	=0.0;
		
		double slidingNum 	 = (double)DateUtil.monthBetween(stBssd, vBssd);
		int stIndex  = (int)slidingNum * -2;
//		int addIndex = (int)slidingNum * 2;
		
		
		for(int i= stIndex; i <= maxRateTenor*2  ; i++) {
			tenor = i/2.0;
			prevTenor 		   = tenor + slidingNum;
			firstNewContTenor  = tenor + slidingNum;
			secontNewContTenor = tenor + slidingNum - 1.0;
			thirdNewContTenor  = tenor + slidingNum - 2.0;
			
			currRate = currSettleMap.getOrDefault(tenor, currRate);
			prevRate = prevSettleMap.getOrDefault(prevTenor, prevRate);
			
			initRate 	 = gocInitMap.getOrDefault(tenor, initRate);
			currWghtRate = gocWghtMap.getOrDefault(tenor, currWghtRate);
			
			if(DateUtil.isGreaterThan(bssd, DateUtil.addMonthToString(stBssd, 0))) {
				if(GmvConstant.NEW_CONT_RATE.equals("CURR")) {
					firstNewRate = prevSettleMap.getOrDefault(firstNewContTenor, firstNewRate);
				}
				else if(GmvConstant.NEW_CONT_RATE.equals("PREV")) {
					firstNewRate = prevSettleMap.getOrDefault(firstNewContTenor, firstNewRate);
				}
				else if(GmvConstant.NEW_CONT_RATE.equals("INIT")) {
					firstNewRate = gocInitMap.getOrDefault(firstNewContTenor, firstNewRate);
				}
				else {
					firstNewRate = prevSettleMap.getOrDefault(firstNewContTenor, firstNewRate);
				}
			}
			
			if(DateUtil.isGreaterThan(bssd, DateUtil.addMonthToString(stBssd, 1))) {
				if(GmvConstant.NEW_CONT_RATE.equals("CURR")) {
					secondNewRate = secondNewRateMap.getOrDefault(secontNewContTenor, secondNewRate);
				}
				else if(GmvConstant.NEW_CONT_RATE.equals("PREV")) {
					secondNewRate = prevSettleMap.getOrDefault(secontNewContTenor, secondNewRate);
				}
				else if(GmvConstant.NEW_CONT_RATE.equals("INIT")) {
					secondNewRate = gocInitMap.getOrDefault(secontNewContTenor, secondNewRate);
				}
				else {
					secondNewRate = secondNewRateMap.getOrDefault(secontNewContTenor, secondNewRate);
				}
			}
			
			if(DateUtil.isGreaterThan(bssd, DateUtil.addMonthToString(stBssd, 2))) {
				if(GmvConstant.NEW_CONT_RATE.equals("CURR")) {
					thirdNewRate = thirdNewRateMap.getOrDefault(thirdNewContTenor, thirdNewRate);
				}
				else if(GmvConstant.NEW_CONT_RATE.equals("PREV")) {
					thirdNewRate = prevSettleMap.getOrDefault(thirdNewContTenor, thirdNewRate);
				}
				else if(GmvConstant.NEW_CONT_RATE.equals("INIT")) {
					thirdNewRate = gocInitMap.getOrDefault(thirdNewContTenor, thirdNewRate);
				}
				else {
					thirdNewRate = thirdNewRateMap.getOrDefault(thirdNewContTenor, thirdNewRate);
				}
			}	
			
			if(eirFlag) {
				prevSysRate = prevSysMap.getOrDefault(gocId, 0.0);
//				currSysRate = currSysMap.getOrDefault(gocId, 0.0);
				currSysRate = 0.0;
			}
			else {
				prevSysRate =currWghtRate;					//TODO : Non Par 
				currSysRate =currWghtRate;					//TODO : Non Par 
				currSysRate = 0.0;
			}
			
			rstList.add(DfLv3Flat.builder()
							.baseYymm(bssd)
							.evalYymm(vBssd)										//TODO : column 
							.gocId(gocId)
							.cfMonthNum(tenor)
							.eirYn(eirFlag? EBoolean.Y : EBoolean.N)
							.ociYn(ociFlag? EBoolean.Y : EBoolean.N)
							.initCurveYymm(goc.getInitCurveYymm())
							.initRate(initRate)
							.prevCurveYymm(stBssd)
							.prevRate(prevRate)
							.firstNewContRate(firstNewRate)
							.secondNewContRate(secondNewRate)
							.thirdNewContRate(thirdNewRate)
							.prevSysRate(prevSysRate)
							.currRate(currRate)
							.currSysRate(currSysRate)
							.lastModifiedBy(GmvConstant.getLastModifier())
							.lastModifiedDate(LocalDateTime.now())
							.build()
						);	
		}
		return rstList;
	}
	
	
	private static Map<String, Double> getMergedPrevSysMap(){
		Map<String, Double> mergedPrevSysMap = getPrevSysMap();
		Map<String, Double> newCurrSysMap 	 = getNewCurrSysMap();
		
		for(Map.Entry<String, Double> entry : newCurrSysMap.entrySet()) {
			mergedPrevSysMap.putIfAbsent(entry.getKey(), entry.getValue());
		}	
		return mergedPrevSysMap;
	}
	
	private static Map<String, Double> getPrevSysMap(){
		if(prevSysMap.isEmpty()) {
			prevSysMap =DfDao.getLv2Eir(stBssd).stream().filter(s->!s.getNewContYn().isTrueFalse()).collect(toMap(DfLv2Eir::getGocId, DfLv2Eir::getEir));
		}
		return prevSysMap ;
	}
	
	private static Map<String, Double> getNewCurrSysMap(){
		if(newCurrSysMap.isEmpty()) {
			newCurrSysMap = DfDao.getEirNewgoc(bssd).stream().filter(s->s.getNewContYn().isTrueFalse()).collect(toMap(DfLv2EirNewgoc::getGocId, DfLv2EirNewgoc::getEir));
		}
		return newCurrSysMap ;
	}

	private static Map<String, Double> getCurrSysMap(){
		if(currSysMap.isEmpty()) {
			currSysMap = DfDao.getLv2Eir(bssd).stream().filter(s-> !s.getNewContYn().isTrueFalse()).collect(toMap(DfLv2Eir::getGocId, DfLv2Eir::getEir));
		}
		return currSysMap ;
	}

	private static Map<Double, Double> getInitRateMap(String gocId){
		if(initRateMap.isEmpty()) {
			initRateMap = DfDao.getDfLv2InitRate().stream().collect(groupingBy(DfLv2InitRate::getGocId, toMap(DfLv2InitRate::getCfMonthNum, DfLv2InitRate::getInitRate)));
		}
		return initRateMap.getOrDefault(gocId, new HashMap<Double, Double>()) ;
	}
	
	private static Map<Double, Double> getWghtRateMap(String gocId){
		if(wgthRateMap.isEmpty()) {
			wgthRateMap = DfDao.getDfLv2WghtRate().stream().collect(groupingBy(DfLv2WghtRate::getGocId, toMap(DfLv2WghtRate::getCfMonthNum, DfLv2WghtRate::getWghtRate)));
		}
		return wgthRateMap.getOrDefault(gocId, new HashMap<Double, Double>()) ;
	}
	
	private static Map<Double, Double> getIrCurve(String curveYymm){
		if(!irCurveMap.containsKey(curveYymm)) {
			irCurveMap.put(curveYymm, DfDao.getDfLv1CurrRate(curveYymm, irCurveId).stream().collect(toMap(DfLv1CurrRate::getCfMonthNum, DfLv1CurrRate::getAppliedRate)));
		}
		return irCurveMap.get(curveYymm);
	}


}