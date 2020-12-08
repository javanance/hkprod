package com.gof.setup;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao.DfDao;
import com.gof.entity.DfLv1CurrRate;
import com.gof.entity.DfLv2EirNewgoc;
import com.gof.entity.DfLv2InitRate;
import com.gof.entity.DfLv2WghtRate;
import com.gof.entity.DfLv3Flat;
import com.gof.entity.DfLv4Eir;
import com.gof.entity.MstGoc;
import com.gof.enums.EBoolean;
import com.gof.enums.ELiabType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupDfLv3Flat {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	private static	String irCurveId	=GmvConstant.IR_CURVE_ID;
	private static int maxRateTenor		=GmvConstant.MAX_RATE_TENOR;
	private static String initCurveYn	=GmvConstant.INIT_CURVE_YN;
	
//	private static 	Map<String, MstGoc> gocMap = new HashMap<String, MstGoc>();
	
	private static Map<String, Double> newCurrSysMap				= new HashMap<String, Double>();
	private static Map<String, Map<Double, Double>> irCurveMap 		= new HashMap<String, Map<Double, Double>>();
	private static Map<String, Map<Double, Double>> initRateMap 	= new HashMap<String, Map<Double, Double>>();
//	private static Map<String, Map<ELiabType, Double>> initEirMap 	= new HashMap<String, Map<ELiabType, Double>>();
	
	private static Map<String, DfLv2InitRate> initEirMap 	= new HashMap<String, DfLv2InitRate>();
	
	private static Map<String, Map<Double, Double>> wgthRateMap 	= new HashMap<String, Map<Double, Double>>();
	private static Map<String, Map<ELiabType, Double>> prevSysMap   = new HashMap<String, Map<ELiabType,Double>>();

	
	public static Stream<DfLv3Flat> createConversion(){
		bssd = stBssd;
		vBssd = stBssd;
		return create();
	}
	
	public static Stream<DfLv3Flat> create(){
		List<DfLv3Flat> rstList = new ArrayList<DfLv3Flat>();

		for(MstGoc aa : PrvdMst.getGocList()) {
			rstList.addAll(build(aa));
		}
		return rstList.stream();
	}
	
	public static Stream<DfLv3Flat> create(String gocId){
		MstGoc goc = PrvdMst.getMstGoc(gocId);
		return build(goc).stream();
	}
	
	
	private static List<DfLv3Flat> build(MstGoc goc){
		List<DfLv3Flat> rstList = new ArrayList<DfLv3Flat>();

		String gocId = goc.getGocId();
		
		Map<Double, Double> prevSettleMap 	 = getIrCurve(stBssd);
		Map<Double, Double> currSettleMap 	 = getIrCurve(bssd);

		Map<Double, Double> gocInitMap 		 = getInitRateMap(gocId);
		Map<ELiabType, Double> gocInitEirMap = getInitEirMap(gocId);
		Map<Double, Double> gocWghtMap 		 = getWghtRateMap(gocId);
		
		
		Map<Double, Double>	secondNewRateMap = getIrCurve(DateUtil.addMonthToString(stBssd, 1));
		Map<Double, Double>	thirdNewRateMap  = getIrCurve(DateUtil.addMonthToString(stBssd, 2));
		
		Map<ELiabType, Double> prevSysMap     = getMergedPrevSysMap(gocId);
		
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
		double prevLrcSysRate=0.0;
		double prevLicSysRate=0.0;
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
			
//			initRate 	 = gocInitMap.getOrDefault(tenor, initRate);
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
				prevSysRate 	= prevSysMap.getOrDefault(ELiabType.ALL, 0.0);
				prevLrcSysRate 	= prevSysMap.getOrDefault(ELiabType.LRC, 0.0);
				prevLicSysRate 	= prevSysMap.getOrDefault(ELiabType.LIC, prevSysRate);
//				currSysRate 	= currSysMap.getOrDefault(gocId, 0.0);
				currSysRate 	= 0.0;
			}
			else {
				prevSysRate 	= currWghtRate;					//TODO : Non Par
				prevLrcSysRate  = currWghtRate;					//TODO : Non Par
				prevLicSysRate  = currWghtRate;					//TODO : Non Par
				currSysRate 	= currWghtRate;					//TODO : Non Par 
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
							.initEir(gocInitEirMap.get(ELiabType.ALL))
							.initLrcEir(gocInitEirMap.get(ELiabType.LRC))
							.initLicEir(gocInitEirMap.get(ELiabType.LIC))
							.prevCurveYymm(stBssd)
							.prevRate(prevRate)
							.firstNewContRate(firstNewRate)
							.secondNewContRate(secondNewRate)
							.thirdNewContRate(thirdNewRate)
							.prevSysRate(prevSysRate)
							.prevLrcSysRate(prevLrcSysRate)
							.prevLicSysRate(prevLicSysRate)
							.currRate(currRate)
							.currSysRate(currSysRate)
							.lastModifiedBy(GmvConstant.getLastModifier())
							.lastModifiedDate(LocalDateTime.now())
							.build()
						);	
		}
		return rstList;
	}
	
	private static Map<ELiabType, Double> getMergedPrevSysMap(String gocId){

		Map<ELiabType, Double> mergedPrevSysMap = getPrevSysMap(gocId);
		double  newContEir 	 = getNewCurrSysMap().getOrDefault(gocId, 0.0);
		
		if(mergedPrevSysMap.isEmpty()) {
			mergedPrevSysMap.putIfAbsent(ELiabType.ALL, newContEir);
			mergedPrevSysMap.putIfAbsent(ELiabType.LRC, newContEir);
			mergedPrevSysMap.putIfAbsent(ELiabType.LIC, newContEir);
		}
		return mergedPrevSysMap;
	}
	
	private static Map<ELiabType, Double> getPrevSysMap(String gocId){
		if(prevSysMap.isEmpty()) {
			prevSysMap =DfDao.getEir(stBssd).stream().collect(groupingBy(DfLv4Eir::getGocId, toMap(DfLv4Eir::getLiabType, DfLv4Eir::getEir)));
		}
		return prevSysMap.getOrDefault(gocId, new HashMap<ELiabType, Double>()) ;
	}
	
	
	private static Map<String, Double> getNewCurrSysMap(){
		if(newCurrSysMap.isEmpty()) {
			newCurrSysMap = DfDao.getEirNewgoc(bssd).stream().filter(s->s.getNewContYn().isTrueFalse()).collect(toMap(DfLv2EirNewgoc::getGocId, DfLv2EirNewgoc::getEir));
		}
		return newCurrSysMap ;
	}

	private static Map<Double, Double> getInitRateMap(String gocId){
		if(initRateMap.isEmpty()) {
			if(initCurveYn.contentEquals("Y")) {
				initRateMap = DfDao.getDfLv2InitRate().stream().collect(groupingBy(DfLv2InitRate::getGocId, toMap(DfLv2InitRate::getCfMonthNum, DfLv2InitRate::getInitRate)));
			}
			else {
				initRateMap = DfDao.getDfLv2InitRate().stream().collect(groupingBy(DfLv2InitRate::getGocId, toMap(DfLv2InitRate::getCfMonthNum, DfLv2InitRate::getInitEir)));
			}
		}
		return initRateMap.getOrDefault(gocId, new HashMap<Double, Double>()) ;
	}
	
	private static Map<String, DfLv2InitRate> getInitEirMap(){
		if(initEirMap.isEmpty()) {
			initEirMap = DfDao.getDfLv2InitRate().stream().filter(s->s.getCfMonthNum()==1.0).collect(toMap(DfLv2InitRate::getGocId, Function.identity()));
		}
		return initEirMap;
	}
	
	private static Map<ELiabType, Double> getInitEirMap(String gocId){
		Map<ELiabType,Double> rstMap = new HashMap<ELiabType, Double>();
		
		DfLv2InitRate dfLv2InitRate = getInitEirMap().get(gocId);
		if(dfLv2InitRate==null) {
			rstMap.put(ELiabType.ALL, 0.0);
			rstMap.put(ELiabType.LRC, 0.0);
			rstMap.put(ELiabType.LIC, 0.0);
		}
		else {
			rstMap.put(ELiabType.ALL, dfLv2InitRate.getInitEir());
			rstMap.put(ELiabType.LRC, dfLv2InitRate.getInitLrcEir());
			rstMap.put(ELiabType.LIC, dfLv2InitRate.getInitLicEir());
		}
		return rstMap;
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