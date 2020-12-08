package com.gof.setup;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao.CfDao;
import com.gof.dao.DfDao;
import com.gof.dao.MstDao;
import com.gof.entity.CfLv1Goc;
import com.gof.entity.CfLv2Delta;
import com.gof.entity.DfLv1CurrRate;
import com.gof.entity.DfLv2Eir;
import com.gof.entity.DfLv2EirNewgoc;
import com.gof.entity.DfLv2InitRate;
import com.gof.entity.MstRunset;
import com.gof.enums.ECoa;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;
import com.gof.util.EirUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupDfEirOld {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	
	private static	String irCurveId			= GmvConstant.IR_CURVE_ID;
	private static	String eirSlAdj				= GmvConstant.EIR_SL_ADJ;
	
	private static	int	 maxIterNum				= GmvConstant.EIR_ITER_NUM;
	private static	double startRate			= GmvConstant.EIR_START_RATE;
	private static	double errorTolerance		= GmvConstant.EIR_ERROR_TOLERANCE;
	
	private static	String targetRunset			= GmvConstant.EIR_TARGET_RUNSET;
	private static	String eirCfRunset			= GmvConstant.EIR_CF_RUNSET;
	private static	String currClosingRunsetId	= GmvConstant.RUNSET_CURR;
	
//	private static Function<CfLv1Goc, String> newGocPk  = CfLv1Goc::getPkForEir;

	private static List<String> newRunsetList 			= new ArrayList<String>();

	private static Map<String, Double> targetEpvMap 	= new HashMap<String, Double>();
	private static Map<String, Double> targetSysEpvMap 	= new HashMap<String, Double>();
	private static Map<String, Double> deltaSysMap 		= new HashMap<String, Double>();
	private static Map<String, Double> deltaFincMap 	= new HashMap<String, Double>();
	private static Map<String, Double> deltaPubChangeMap 	= new HashMap<String, Double>();
	
	private static Map<String, Double> prevSysMap 		= new HashMap<String, Double>();
	private static Map<String, Map<Double, Double>> initCuvreMap = new HashMap<String, Map<Double,Double>>();
	
	public static Stream<DfLv2Eir> create(){
		List<DfLv2Eir> rstList = new ArrayList<DfLv2Eir>();
		for(String gocId : PrvdMst.getGocIdList()) {
			rstList.addAll(create(gocId).collect(toList()));
		}
		return rstList.stream();
//		return create(null);
	}
	
	public static Stream<DfLv2EirNewgoc> createNewgoc(){
		List<DfLv2EirNewgoc> rstList = new ArrayList<DfLv2EirNewgoc>();
		for(String gocId : PrvdMst.getGocIdList()) {
			rstList.addAll(createNewgoc(gocId).collect(toList()));
		}
		return rstList.stream();
//		return createNewgoc(null);
	}

	public static Stream<DfLv2Eir> create(String gocId){
		List<DfLv2Eir> rstList = new ArrayList<DfLv2Eir>();

		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getCfMonthNum;
		
		List<CfLv1Goc> eirCfList = CfDao.getCfLv1GocByRunsetStream(bssd, eirCfRunset, gocId).collect(toList());
		
		if(!eirCfList.isEmpty()) {
			double targetRunEpv 	= getTargetEpvMap(gocId);
			double targetRunSysEpv 	= getTargetSysMap(gocId);
			
			double targetRunOci		= 1.0* (targetRunEpv - targetRunSysEpv) ;					//TODO : Check sign
			double fincOci 	 		= getFincOci(gocId)	;
			double pubChangeOci 	= getPubChangeOci(gocId)	;
			fincOci = fincOci + pubChangeOci;
			
//			double fincOci 	 		= eirSlAdj.equals("Y") ?  getFincOci(gocId)	 : 0.0; 		//TODO : Check sign
			double detlaOci  		= eirSlAdj.equals("Y") ?  getDeltaOci(gocId) : 0.0;			//TODO : Check sign
			
//			log.info("aaa : {}, {}, {},{}, {},{},{},{}", gocId, eirCfRunset, targetRunset, eirCfList.size(),  targetRunSysEpv, targetRunOci, detlaOci, fincOci);
			
			rstList.add(EirUtil.createEir(eirCfList, startRate,	targetRunSysEpv, targetRunOci, detlaOci, fincOci, errorTolerance, maxIterNum, tenorAdjFn));
		}
		
		return rstList.stream();
	}
	
//	public static Stream<DfLv2Eir> create(String gocId){
//		List<DfLv2Eir> rstList = new ArrayList<DfLv2Eir>();
//
//		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getCfMonthNum;
//		
//		Map<String, List<CfLv1Goc>> eirCfMap = CfDao.getCfLv1GocByRunsetStream(bssd, eirCfRunset, gocId).collect(groupingBy(CfLv1Goc::getGocId, toList()));
//		
//		log.info("aaa : {}, {},{}", eirCfRunset, targetRunset, eirCfMap.size());
//		
//		double targetEpv =0.0;
//		double targetOci =0.0;
//		for(Map.Entry<String, List<CfLv1Goc>> entry : eirCfMap.entrySet()) {
//			
//			targetOci = getDeltaOci(entry.getKey());
//			
//			targetEpv = getTargetEpvMap(entry.getKey()) - targetOci;
//			
//			rstList.add(EirUtil.createEir(entry.getValue(), startRate,	targetEpv, errorTolerance, maxIterNum, tenorAdjFn));
//		}
//		return rstList.stream();
//	}

	public static Stream<DfLv2EirNewgoc> createNewgoc(String gocId){
		List<DfLv2EirNewgoc> rstList = new ArrayList<DfLv2EirNewgoc>();
		
		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getGenCfMonthNumForEir;
		
		List<CfLv1Goc> eirCfList = CfDao.getCfLv1GocStream(bssd, gocId)
													.filter(s-> getNewRunsetList().contains(s.getRunsetId()))
//													.peek(s-> log.info("aaaaaa : {},{}", s.getRunsetId()))
													.filter(s->s.getLiabType().equals(ELiabType.LRC))
													.filter(s->s.getStStatus().equals(EContStatus.NEW))				//TODO ::: NEW
//													.filter(s->s.getEndStatus().equals(EContStatus.NORMAL))			//TODO :: ==> ALL ????
													.collect(toList());
		log.info("EIR CF : {}", eirCfList.size());
		
		if(! eirCfList.isEmpty()) {
			double targetEpv = eirCfList.stream().map(s->s.getEpvAmt()).reduce(0.0, (s,u)->s+u);
			
			rstList.add(EirUtil.createEirNewgoc(eirCfList, startRate, targetEpv, errorTolerance, maxIterNum, tenorAdjFn));
		}
		
		return rstList.stream();
	}
	
//	public static Stream<DfLv2EirNewgoc> createNewgoc(String gocId){
//		List<DfLv2EirNewgoc> rstList = new ArrayList<DfLv2EirNewgoc>();
//		
//		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getGenCfMonthNumForEir;
//		
//		Map<String, List<CfLv1Goc>> eirCfMap = CfDao.getCfLv1GocStream(bssd, gocId)
//													.filter(s-> getNewRunsetList().contains(s.getRunsetId()))
//													.filter(s->s.getStStatus().equals(EContStatus.NEW))				//TODO ::: new cont ????
//													.filter(s->s.getEndStatus().equals(EContStatus.NORMAL))
//													.filter(s->s.getLiabType().equals(ELiabType.LRC))
//													.collect(groupingBy(newGocPk, toList()));
//		
//		for(Map.Entry<String, List<CfLv1Goc>> entry : eirCfMap.entrySet()) {
//			double targetEpv = entry.getValue().stream().map(s->s.getEpvAmt()).reduce(0.0, (s,u)->s+u);
//			rstList.add(EirUtil.createEirNewgoc(entry.getValue(), startRate, targetEpv, errorTolerance, maxIterNum, tenorAdjFn));
//		}
//		
//		rstList.sort((s,u)-> s.getGocId().compareTo(u.getGocId()));
//
//		return rstList.stream();
//	}

	

	public static Stream<DfLv2Eir> createConversion(){
		List<DfLv2Eir> rstList = new ArrayList<DfLv2Eir>();
		
		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getCfMonthNum;

		Map<String, List<CfLv1Goc>> eirCfMap = CfDao.getCfLv1GocByRunsetStream(stBssd, currClosingRunsetId).collect(groupingBy(CfLv1Goc::getGocId, toList()));
		
		log.info("eir : {},{}", eirCfMap.size());
		for(Map.Entry<String, List<CfLv1Goc>> entry : eirCfMap.entrySet()) {
			double targetEpv = entry.getValue().stream().map(s->s.getEpvAmt()).reduce(0.0, (s,u)->s+u);
			rstList.add(EirUtil.createEir(entry.getValue(), startRate, targetEpv, 0.0, 0.0, 0.0, errorTolerance, maxIterNum, tenorAdjFn));
		}
		
		return rstList.stream();
	}
	
	
	public static Stream<DfLv2EirNewgoc> createNewGocConversion(){
		List<DfLv2EirNewgoc> rstList = new ArrayList<DfLv2EirNewgoc>();
		
		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getGenCfMonthNumForEir;
		
		Map<String, List<CfLv1Goc>> eirCfMap = CfDao.getCfLv1GocByRunsetStream(stBssd, currClosingRunsetId).collect(groupingBy(CfLv1Goc::getGocId, toList()));
		log.info("eir : {},{}", eirCfMap.size());
		
		for(Map.Entry<String, List<CfLv1Goc>> entry : eirCfMap.entrySet()) {
			double targetEpv = entry.getValue().stream().map(s->s.getEpvAmt()).reduce(0.0, (s,u)->s+u);
			rstList.add(EirUtil.createEirNewgoc(entry.getValue(), startRate, targetEpv, errorTolerance, maxIterNum, tenorAdjFn));
		}
		
		return rstList.stream();
	}
	
	//	BOX_SI on Delta CF prior to FINC_CHANGE : EIR_SL_ADJ =
	private static double getDeltaOci(String gocId) {
		if(deltaSysMap.isEmpty()) {
			deltaSysMap = CfDao.getCfLv2DeltaStream(bssd)
								.filter(s->!s.getRunsetId().equals(eirCfRunset))
								.collect(toMap(CfLv2Delta::getGocId, s->s.getDeltaEpv(getInitRate(s.getGocId(),s.getCfMonthNum()), getPrevSysRate(s.getGocId())), (s,u)->s+u));
			
			deltaSysMap.entrySet().forEach(s->log.info("Delta Oci  : {}, {}, {}, {}", s.getKey(), s.getValue()));
		}
		return deltaSysMap.getOrDefault(gocId, 0.0);
	}

	private static double getFincOci(String gocId) {
		if(deltaFincMap.isEmpty()) {
			
			Map<Double, Double> curveMap = DfDao.getDfLv1CurrRate(bssd, irCurveId).stream().collect(toMap(DfLv1CurrRate::getCfMonthNum, DfLv1CurrRate::getAppliedRate));
			
			deltaFincMap = CfDao.getCfLv2DeltaStream(bssd)
								.filter(s->s.getRunsetId().equals(eirCfRunset))
								.collect(toMap(CfLv2Delta::getGocId, s->s.getDeltaEpv(curveMap.getOrDefault(s.getCfMonthNum(), 0.0)),  (s,u)->s+u));
			
			deltaFincMap.entrySet().forEach(s->log.info("Finc Delta Oci  : {}, {}", s.getKey(), s.getValue()));
		}
		
		return deltaFincMap.getOrDefault(gocId, 0.0);
	}
	
//	TODO :!!!!
	private static double getPubChangeOci(String gocId) {
		if(deltaPubChangeMap.isEmpty()) {
			
			Map<Double, Double> curveMap = DfDao.getDfLv1CurrRate(stBssd, irCurveId).stream().collect(toMap(DfLv1CurrRate::getCfMonthNum, DfLv1CurrRate::getAppliedRate));
			
			deltaPubChangeMap = CfDao.getCfLv2DeltaStream(bssd)
								.filter(s->s.getRunsetId().equals("PCHG_110"))			//TODO:
								.collect(toMap(CfLv2Delta::getGocId, s->s.getDeltaEpv(curveMap.getOrDefault(s.getCfMonthNum()+3, 0.0), s.getCfMonthNum()+3),  (s,u)->s+u));
			
			deltaPubChangeMap.entrySet().forEach(s->log.info("Finc Delta Oci  : {}, {}", s.getKey(), s.getValue()));
		}
		
		return deltaPubChangeMap.getOrDefault(gocId, 0.0);
	}
	
	
	private static double getTargetEpvMap(String gocId) {
		if(targetEpvMap.isEmpty()) {
			
			targetEpvMap = CfDao.getCfLv1GocByRunsetStream(bssd, targetRunset).collect(toMap(CfLv1Goc::getGocId, CfLv1Goc::getEpvAmt, (s,u)-> s+u));
//			targetEpvMap.entrySet().forEach(s->log.info("Target Run Curr Epv  : {}, {}", s.getKey(), s.getValue()));
		}
		
		if(!targetEpvMap.containsKey(gocId)) {
			log.info("Null goc  In target EPV : {}, {}", gocId, targetRunset);
		}
		
		return targetEpvMap.getOrDefault(gocId, 0.0);
	}
	
	private static double getTargetSysMap(String gocId) {
		if(targetSysEpvMap.isEmpty()) {
			Map<String, Double> prevSysMap = getPrevSysMap();
			targetSysEpvMap = CfDao.getCfLv1GocByRunsetStream(bssd, targetRunset).collect(toMap(CfLv1Goc::getGocId, s-> s.getEirAplyEpv(prevSysMap), (s,u)-> s+u));
			
//			targetSysEpvMap.entrySet().forEach(s->log.info("Target Run Sys Epv  : {}, {}", s.getKey(), s.getValue()));
		}
		
		if(!targetEpvMap.containsKey(gocId)) {
			log.info("Null goc  In target EPV : {}, {}", gocId, targetRunset);
		}
		
		return targetSysEpvMap.getOrDefault(gocId, 0.0);
	}
	
	private static Map<String, Double> getPrevSysMap(){
		if(prevSysMap.isEmpty()) {
			prevSysMap 	  = DfDao.getLv2Eir(stBssd).stream().filter(s->!s.getNewContYn().isTrueFalse()).collect(toMap(DfLv2Eir::getGocId, DfLv2Eir::getEir, (s,u)->s));
			
			Map<String, Double> newCurrSysMap = DfDao.getEirNewgoc(bssd).stream().collect(toMap(DfLv2EirNewgoc::getGocId, DfLv2EirNewgoc::getEir, (s,u)->s));

			for(Map.Entry<String, Double> entry : newCurrSysMap.entrySet()) {
				prevSysMap.putIfAbsent(entry.getKey(), entry.getValue());
				log.info("Sys Int Rate update with New Goc  {} :  {}", entry.getKey(), entry.getValue());
			}
		}
		return prevSysMap;
	}
	
	private static double getPrevSysRate(String gocId){
		return getPrevSysMap().getOrDefault(gocId, 0.0);
	}
	
	private static double getInitRate(String gocId, Double cfMonthNum) {
		if(initCuvreMap.isEmpty()) {
			initCuvreMap = DfDao.getDfLv2InitRate().stream().collect(groupingBy(DfLv2InitRate::getGocId
																		, toMap(DfLv2InitRate::getCfMonthNum, DfLv2InitRate::getInitRate)));	
		}
		if(!initCuvreMap.containsKey(gocId)) {
			log.error("There are no InitCurve for {}. Check INIT_RATE table ", gocId);
			System.exit(1);
		}
		return initCuvreMap.get(gocId).getOrDefault(cfMonthNum, 0.0);
	}
	
	
	private static List<String> getNewRunsetList() {
		if(newRunsetList.isEmpty()) {
			newRunsetList = MstDao.getMstRunset().stream()
//								  .filter(s->s.getNewContYn().isTrueFalse())
								  .filter(s->s.getRunsetType().isNewContYn())
								  .filter(s->s.getCoaId().equals(ECoa.EPV))
								  .map(MstRunset::getRunsetId).collect(toList());
		}
		
		return newRunsetList;
	}
}