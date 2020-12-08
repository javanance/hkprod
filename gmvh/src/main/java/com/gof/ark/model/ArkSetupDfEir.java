package com.gof.ark.model;

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
import com.gof.dao.MstDao;
import com.gof.dao.RstDao;
import com.gof.entity.CfLv1Goc;
import com.gof.entity.DfLv2Eir;
import com.gof.entity.DfLv2EirNewgoc;
import com.gof.entity.DfLv4Eir;
import com.gof.entity.MstRunset;
import com.gof.entity.RstRollFwd;
import com.gof.enums.ECoa;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;
import com.gof.enums.ERollFwdType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;
import com.gof.util.EirUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArkSetupDfEir {
	private static	String bssd					=GmvConstant.BSSD;
	private static	String stBssd				=GmvConstant.ST_BSSD;
	private static	String vBssd				=GmvConstant.V_BSSD;
	
	private static	int	 maxIterNum				= GmvConstant.EIR_ITER_NUM;
	private static	double startRate			= GmvConstant.EIR_START_RATE;
	private static	double errorTolerance		= GmvConstant.EIR_ERROR_TOLERANCE;
	
	private static	String currClosingRunsetId	= GmvConstant.RUNSET_CURR;
	private static List<String> newRunsetList 	= new ArrayList<String>();
	private static Map<String, Double> ociMap	= new HashMap<String, Double>();
	
	public static Stream<DfLv2Eir> create(){
//		List<DfLv2Eir> rstList = new ArrayList<DfLv2Eir>();
//		for(String gocId : PrvdMst.getGocIdList()) {
//			rstList.addAll(create(gocId).collect(toList()));
//		}
//		return rstList.stream();
		return PrvdMst.getGocIdList().stream().flatMap(gocId->create(gocId));
	}
	
	public static Stream<DfLv4Eir> createDfLv4(){
//		List<DfLv4Eir> rstList = new ArrayList<DfLv4Eir>();
//		for(String gocId : PrvdMst.getGocIdList()) {
//			rstList.addAll(createDfLv4(gocId).collect(toList()));
//		}
//		return rstList.stream();
		return PrvdMst.getGocIdList().stream().flatMap(gocId->createDfLv4(gocId));
	}
	
	public static Stream<DfLv2Eir> create(String gocId){
		List<DfLv2Eir> rstList = new ArrayList<DfLv2Eir>();
	
		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getCfMonthNum;
		
		List<CfLv1Goc> eirCfList = CfDao.getCfLv1GocByRunsetStream(bssd, currClosingRunsetId, gocId).collect(toList());
		
		if(!eirCfList.isEmpty()) {
			double targetOci 	=  getOciMap(gocId);
			rstList.add(EirUtil.createEir(eirCfList, startRate,	0.0, targetOci, 0.0, 0.0, errorTolerance, maxIterNum, tenorAdjFn));
		}
		return rstList.stream();
	}
	
	public static Stream<DfLv4Eir> createDfLv4(String gocId){
		List<DfLv4Eir> rstList = new ArrayList<DfLv4Eir>();
	
		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getCfMonthNum ;
		
		List<CfLv1Goc> eirCfList = CfDao.getCfLv1GocByRunsetStream(bssd, currClosingRunsetId, gocId).collect(toList()) ;
		
		if(!eirCfList.isEmpty()) {
			double targetOci 	= getOciMap(gocId) ;
			rstList.add(EirUtil.createDfLv4Eir(eirCfList, ELiabType.LRC, currClosingRunsetId, startRate,targetOci,  errorTolerance, maxIterNum, tenorAdjFn)) ;
		}
		
		return rstList.stream() ;
	}

	public static Stream<DfLv2EirNewgoc> createNewgoc(){
		List<DfLv2EirNewgoc> rstList = new ArrayList<DfLv2EirNewgoc>();
		for(String gocId : PrvdMst.getGocIdList()) {
			rstList.addAll(createNewgoc(gocId).collect(toList()));
		}
		return rstList.stream();
	}

	public static Stream<DfLv2EirNewgoc> createNewgoc(String gocId){
		List<DfLv2EirNewgoc> rstList = new ArrayList<DfLv2EirNewgoc>();
		
		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getGenCfMonthNumForEir;
		
		List<CfLv1Goc> eirCfList = CfDao.getCfLv1GocStream(bssd, gocId)
													.filter(s-> getNewRunsetList().contains(s.getRunsetId()))
													.filter(s->s.getLiabType().equals(ELiabType.LRC))
//													.filter(s->s.getStStatus().equals(EContStatus.NEW))				//TODO ::: NEW
//													.filter(s->s.getEndStatus().equals(EContStatus.NORMAL))			//TODO :: ==> ALL ????
													.collect(toList());
//		log.info("EIR CF : {}", eirCfList.size());
		
		if(!eirCfList.isEmpty()) {
			double targetEpv = eirCfList.stream().map(s->s.getEpvAmt()).reduce(0.0, (s,u)->s+u);
			rstList.add(EirUtil.createEirNewgoc(eirCfList, startRate, targetEpv, errorTolerance, maxIterNum, tenorAdjFn));
		}
		return rstList.stream();
	}
	
	
	public static Stream<DfLv2Eir> createConversion(){
		List<DfLv2Eir> rstList = new ArrayList<DfLv2Eir>();
		
		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getCfMonthNum;

		Map<String, List<CfLv1Goc>> eirCfMap = CfDao.getCfLv1GocByRunsetStream(stBssd, currClosingRunsetId).collect(groupingBy(CfLv1Goc::getGocId, toList()));
		
		for(Map.Entry<String, List<CfLv1Goc>> entry : eirCfMap.entrySet()) {
			double targetEpv = entry.getValue().stream().map(s->s.getEpvAmt()).reduce(0.0, (s,u)->s+u);
			rstList.add(EirUtil.createEir(entry.getValue(), startRate, targetEpv, 0.0, 0.0, 0.0, errorTolerance, maxIterNum, tenorAdjFn));
		}
		
		return rstList.stream();
	}
	
	public static Stream<DfLv4Eir> createDfLv4Conversion(String gocId){
		List<DfLv4Eir> rstList = new ArrayList<DfLv4Eir>();
	
		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getCfMonthNum ;
		
		List<CfLv1Goc> eirCfList = CfDao.getCfLv1GocByRunsetStream(bssd, currClosingRunsetId, gocId).collect(toList()) ;
		
		if(!eirCfList.isEmpty()) {
			double targetOci 	= 0.0 ;
			rstList.add(EirUtil.createDfLv4Eir(eirCfList, ELiabType.LRC, currClosingRunsetId, startRate,targetOci,  errorTolerance, maxIterNum, tenorAdjFn)) ;
		}
		
		return rstList.stream() ;
	}
	
	public static Stream<DfLv4Eir> createDfLv4Conversion(){
		return PrvdMst.getGocIdList().stream().flatMap(gocId->createDfLv4Conversion(gocId));
	}
	
	public static Stream<DfLv2EirNewgoc> createNewGocConversion(){
		List<DfLv2EirNewgoc> rstList = new ArrayList<DfLv2EirNewgoc>();
		
		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getGenCfMonthNumForEir;
		
		Map<String, List<CfLv1Goc>> eirCfMap = CfDao.getCfLv1GocByRunsetStream(stBssd, currClosingRunsetId).collect(groupingBy(CfLv1Goc::getGocId, toList()));
		
		for(Map.Entry<String, List<CfLv1Goc>> entry : eirCfMap.entrySet()) {
			double targetEpv = entry.getValue().stream().map(s->s.getEpvAmt()).reduce(0.0, (s,u)->s+u);
			rstList.add(EirUtil.createEirNewgoc(entry.getValue(), startRate, targetEpv, errorTolerance, maxIterNum, tenorAdjFn));
		}
		
		return rstList.stream();
	}
	
	private static double  getOciMap(String gocId){
		if(ociMap.isEmpty()) {
			ociMap =RstDao.getRollFwdRst(bssd).stream()
					 .filter(s->s.getCoaId().equals(ECoa.AOCI))
					 .filter(s->s.getRollFwdType().equals(ERollFwdType.CURR_CLOSE))
					 .collect(toMap(RstRollFwd::getGocId, RstRollFwd::getCloseAmt))
					 ;
		}
		return ociMap.get(gocId) ;
	}

	private static List<String> getNewRunsetList() {
		if(newRunsetList.isEmpty()) {
			newRunsetList = MstDao.getMstRunset().stream()
								  .filter(s->s.getRunsetType().isNewContYn())
								  .filter(s->s.getCoaId().equals(ECoa.EPV))
								  .map(MstRunset::getRunsetId).collect(toList());
		}
		return newRunsetList;
	}
}