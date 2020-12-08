package com.gof.setup;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao.CfDao;
import com.gof.dao.MstDao;
import com.gof.entity.CfLv1Goc;
import com.gof.entity.DfLv2EirNewgoc;
import com.gof.entity.MstRunset;
import com.gof.enums.ECoa;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;
import com.gof.util.EirUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupDfEir {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	
	
	private static	int	 maxIterNum				= GmvConstant.EIR_ITER_NUM;
	private static	double startRate			= GmvConstant.EIR_START_RATE;
	private static	double errorTolerance		= GmvConstant.EIR_ERROR_TOLERANCE;
	
	private static List<String> newRunsetList 			= new ArrayList<String>();

	public static Stream<DfLv2EirNewgoc> createNewgoc(){
		List<DfLv2EirNewgoc> rstList = new ArrayList<DfLv2EirNewgoc>();
		for(String gocId : PrvdMst.getGocIdList()) {
			rstList.addAll(createNewgoc(gocId).collect(toList()));
		}
		return rstList.stream();
//		return createNewgoc(null);
	}

	public static Stream<DfLv2EirNewgoc> createNewgoc(String gocId){
		List<DfLv2EirNewgoc> rstList = new ArrayList<DfLv2EirNewgoc>();
		
		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getGenCfMonthNumForEir;
		
		List<CfLv1Goc> eirCfList = CfDao.getCfLv1GocStream(bssd, gocId)
													.filter(s-> getNewRunsetList().contains(s.getRunsetId()))
//													.peek(s-> log.info("aaaaaa : {},{}", s.getRunsetId()))
													.filter(s->s.getLiabType().equals(ELiabType.LRC))
//													.filter(s->s.getStStatus().equals(EContStatus.NEW))				//TODO ::: NEW
//													.filter(s->s.getEndStatus().equals(EContStatus.NORMAL))			//TODO :: ==> ALL ????
													.collect(toList());
		log.info("EIR CF : {}", eirCfList.size());
//		getNewRunsetList().forEach(s-> log.info("aaa :  {}", s));
		
		if(! eirCfList.isEmpty()) {
			double targetEpv = eirCfList.stream().map(s->s.getEpvAmt()).reduce(0.0, (s,u)->s+u);
			
			rstList.add(EirUtil.createEirNewgoc(eirCfList, startRate, targetEpv, errorTolerance, maxIterNum, tenorAdjFn));
		}
		
		return rstList.stream();
	}
	

//	public static Stream<DfLv2Eir> createConversion(){
//		List<DfLv2Eir> rstList = new ArrayList<DfLv2Eir>();
//		
//		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getCfMonthNum;
//
//		Map<String, List<CfLv1Goc>> eirCfMap = CfDao.getCfLv1GocByRunsetStream(stBssd, currClosingRunsetId).collect(groupingBy(CfLv1Goc::getGocId, toList()));
//		
//		log.info("eir : {},{}", eirCfMap.size());
//		for(Map.Entry<String, List<CfLv1Goc>> entry : eirCfMap.entrySet()) {
//			double targetEpv = entry.getValue().stream().map(s->s.getEpvAmt()).reduce(0.0, (s,u)->s+u);
//			rstList.add(EirUtil.createEir(entry.getValue(), startRate, targetEpv, 0.0, 0.0, 0.0, errorTolerance, maxIterNum, tenorAdjFn));
//		}
//		
//		return rstList.stream();
//	}
//	
//	
//	public static Stream<DfLv2EirNewgoc> createNewGocConversion(){
//		List<DfLv2EirNewgoc> rstList = new ArrayList<DfLv2EirNewgoc>();
//		
//		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getGenCfMonthNumForEir;
//		
//		Map<String, List<CfLv1Goc>> eirCfMap = CfDao.getCfLv1GocByRunsetStream(stBssd, currClosingRunsetId).collect(groupingBy(CfLv1Goc::getGocId, toList()));
//		log.info("eir : {},{}", eirCfMap.size());
//		
//		for(Map.Entry<String, List<CfLv1Goc>> entry : eirCfMap.entrySet()) {
//			double targetEpv = entry.getValue().stream().map(s->s.getEpvAmt()).reduce(0.0, (s,u)->s+u);
//			rstList.add(EirUtil.createEirNewgoc(entry.getValue(), startRate, targetEpv, errorTolerance, maxIterNum, tenorAdjFn));
//		}
//		
//		return rstList.stream();
//	}
	

	
//	private static Map<String, Double> getPrevSysMap(){
//		if(prevSysMap.isEmpty()) {
//			prevSysMap 	  = DfDao.getEir(stBssd).stream().filter(s->!s.getNewContYn().isTrueFalse()).collect(toMap(DfLv2Eir::getGocId, DfLv2Eir::getEir, (s,u)->s));
//			
//			Map<String, Double> newCurrSysMap = DfDao.getEirNewgoc(bssd).stream().collect(toMap(DfLv2EirNewgoc::getGocId, DfLv2EirNewgoc::getEir, (s,u)->s));
//
//			for(Map.Entry<String, Double> entry : newCurrSysMap.entrySet()) {
//				prevSysMap.putIfAbsent(entry.getKey(), entry.getValue());
//				log.info("Sys Int Rate update with New Goc  {} :  {}", entry.getKey(), entry.getValue());
//			}
//		}
//		return prevSysMap;
//	}
	
	
	
	
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