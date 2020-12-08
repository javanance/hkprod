package com.gof.model;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.gof.dao.BoxDao;
import com.gof.dao.CfDao;
import com.gof.entity.CfLv1Goc;
import com.gof.entity.DfLv4Eir;
import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MstRunset;
import com.gof.entity.RstBoxGoc;
import com.gof.enums.ECoa;
import com.gof.enums.ELiabType;
import com.gof.enums.ERollFwdType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdAcct;
import com.gof.provider.PrvdMst;
import com.gof.util.EirUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MdlDfLv4Eir {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	
	private static	int	 maxIterNum				= GmvConstant.EIR_ITER_NUM;
	private static	double startRate			= GmvConstant.EIR_START_RATE;
	private static	double errorTolerance		= GmvConstant.EIR_ERROR_TOLERANCE;
	
	private static Map<String, Double> ociMap 				= new HashMap<String, Double>();
	private static Map<String, Double> licOciMap 			= new HashMap<String, Double>();
	
	private static List<MstRunset> eirRunset 				= new ArrayList<MstRunset>();
	private static List<MstRunset> beforeEirUpdateRunset 	= new ArrayList<MstRunset>();
		
	public static Stream<DfLv4Eir> createConversion(){
		bssd =stBssd;
		ociMap = new HashMap<String, Double>();
//		String gocId ="0291_0000_2";
//		return create(gocId);
		return create();
	}
	
	public static Stream<DfLv4Eir> create(){
		return PrvdMst.getGocIdList().stream().flatMap(s->create(s));
	}
	public static Stream<DfLv4Eir> create(String gocId){
		return Stream.concat(createAllEir(gocId), Stream.concat(createLrcEir(gocId), createLicEir(gocId)));
	}
	
	
	public static Stream<DfLv4Eir> createAllEir(){
		return PrvdMst.getGocIdList().stream().flatMap(s->createAllEir(s));
	}
	
	public static Stream<DfLv4Eir> createAllEir(String gocId){
		return create(gocId, ELiabType.ALL);
	}
	
	public static Stream<DfLv4Eir> createLrcEir(){
		return PrvdMst.getGocIdList().stream().flatMap(s->createLrcEir(s));
	}
	
	public static Stream<DfLv4Eir> createLrcEir(String gocId){
		return create(gocId, ELiabType.LRC);
	}
	
	public static Stream<DfLv4Eir> createLicEir(){
		return PrvdMst.getGocIdList().stream().flatMap(s->createLicEir(s));
	}
	
	public static Stream<DfLv4Eir> createLicEir(String gocId){
		return create(gocId, ELiabType.LIC);
	}
	
	private static Stream<DfLv4Eir> create(String gocId, ELiabType liabType){
		List<DfLv4Eir> rstList = new ArrayList<DfLv4Eir>();

		Predicate<MstRunset> predicate = liabType.equals(ELiabType.ALL)? s->true: s-> s.getLiabType().equals(liabType);
		
		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getCfMonthNum;
		List<String> eirRunset = getEirUpdateRunset().stream()
											.filter(predicate)				
											.map(MstRunset::getRunsetId).collect(toList()); 	

		String runsetList = eirRunset.stream().collect(joining("#"));
//		log.info("aaa : {}", runsetList);
		
		List<CfLv1Goc> eirCfList = CfDao.getCfLv1GocStream(bssd, gocId)
										.filter(s->eirRunset.contains(s.getRunsetId()))
										.collect(toList());
		
		double targetOci = getOciAmt(gocId, liabType);
		
		if(!eirCfList.isEmpty()) {
			log.info("TargetOci : {},{},{}", gocId, liabType, targetOci);
			rstList.add(EirUtil.createDfLv4Eir(eirCfList, liabType, runsetList, startRate,	targetOci, errorTolerance, maxIterNum, tenorAdjFn));
		}
		
		return rstList.stream();
	}
	
//	public static Stream<DfLv4Eir> createLrcEir(String gocId){
//		List<DfLv4Eir> rstList = new ArrayList<DfLv4Eir>();
//	
//		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getCfMonthNum;
//		
//		List<String> eirRunset = getEirUpdateRunset().stream()
//									.filter(s->s.getLiabType().equals(ELiabType.LRC))			//ONLY LIC
//									.map(MstRunset::getRunsetId).collect(toList()); 	
//
//		String runsetList = eirRunset.stream().collect(joining("#"));
//		
//		List<CfLv1Goc> eirCfList = CfDao.getCfLv1GocStream(bssd, gocId)
//										.filter(s->eirRunset.contains(s.getRunsetId()))
//										.collect(toList());
//		
//		
//		if(!eirCfList.isEmpty()) {
//			double targetOci = -1.0* getLicOciMap().getOrDefault(gocId, 0.0);
//			
//			log.info("TargetOci : {},{}", gocId, targetOci);
//			
//			rstList.add(EirUtil.createDfLv4Eir(eirCfList, ELiabType.LIC, runsetList, startRate,	targetOci, errorTolerance, maxIterNum, tenorAdjFn));
//		}
//		
//		return rstList.stream();
//	}
		
		
//	public static Stream<DfLv4Eir> createLrcEir(String gocId){
	//		List<DfLv4Eir> rstList = new ArrayList<DfLv4Eir>();
	//	
	//		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getCfMonthNum;
	//		
	//		List<String> eirRunset = getEirUpdateRunset().stream()
	//									.filter(s->s.getLiabType().equals(ELiabType.LRC))			//ONLY LIC
	//									.map(MstRunset::getRunsetId).collect(toList()); 	
	//
	//		String runsetList = eirRunset.stream().collect(joining("#"));
	//		
	//		List<CfLv1Goc> eirCfList = CfDao.getCfLv1GocStream(bssd, gocId)
	//										.filter(s->eirRunset.contains(s.getRunsetId()))
	//										.collect(toList());
	//		
	//		
	//		if(!eirCfList.isEmpty()) {
	//			double targetOci = -1.0* getLicOciMap().getOrDefault(gocId, 0.0);
	//			
	//			log.info("TargetOci : {},{}", gocId, targetOci);
	//			
	//			rstList.add(EirUtil.createDfLv4Eir(eirCfList, ELiabType.LIC, runsetList, startRate,	targetOci, errorTolerance, maxIterNum, tenorAdjFn));
	//		}
	//		
	//		return rstList.stream();
	//	}
			
//	public static Stream<DfLv4Eir> createLicEir(String gocId){
//		List<DfLv4Eir> rstList = new ArrayList<DfLv4Eir>();
//	
//		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getCfMonthNum;
//		List<String> eirRunset = getEirUpdateRunset().stream()
//									.filter(s->s.getLiabType().equals(ELiabType.LIC))			//ONLY LIC
//									.map(MstRunset::getRunsetId).collect(toList()); 	
//	
//		String runsetList = eirRunset.stream().collect(joining("#"));
//		
//		List<CfLv1Goc> eirCfList = CfDao.getCfLv1GocStream(bssd, gocId)
//										.filter(s->eirRunset.contains(s.getRunsetId()))
//										.collect(toList());
//		
//		
//		if(!eirCfList.isEmpty()) {
//			double targetOci = -1.0* getLicOciMap().getOrDefault(gocId, 0.0);
//			
//			log.info("TargetOci : {},{}", gocId, targetOci);
//			
//			rstList.add(EirUtil.createDfLv4Eir(eirCfList, ELiabType.LIC, runsetList, startRate,	targetOci, errorTolerance, maxIterNum, tenorAdjFn));
//		}
//		
//		return rstList.stream();
//	}
	
//	public static Stream<DfLv4Eir> createLrcEir(String gocId){
	//		List<DfLv4Eir> rstList = new ArrayList<DfLv4Eir>();
	//	
	//		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getCfMonthNum;
	//		
	//		List<String> eirRunset = getEirUpdateRunset().stream()
	//									.filter(s->s.getLiabType().equals(ELiabType.LRC))			//ONLY LIC
	//									.map(MstRunset::getRunsetId).collect(toList()); 	
	//
	//		String runsetList = eirRunset.stream().collect(joining("#"));
	//		
	//		List<CfLv1Goc> eirCfList = CfDao.getCfLv1GocStream(bssd, gocId)
	//										.filter(s->eirRunset.contains(s.getRunsetId()))
	//										.collect(toList());
	//		
	//		
	//		if(!eirCfList.isEmpty()) {
	//			double targetOci = -1.0* getLicOciMap().getOrDefault(gocId, 0.0);
	//			
	//			log.info("TargetOci : {},{}", gocId, targetOci);
	//			
	//			rstList.add(EirUtil.createDfLv4Eir(eirCfList, ELiabType.LIC, runsetList, startRate,	targetOci, errorTolerance, maxIterNum, tenorAdjFn));
	//		}
	//		
	//		return rstList.stream();
	//	}
//	public static Stream<DfLv4Eir> create(String gocId){
//		List<DfLv4Eir> rstList = new ArrayList<DfLv4Eir>();
//		Function<CfLv1Goc, Double> tenorAdjFn = CfLv1Goc::getCfMonthNum;
//		List<String> eirRunset = getEirUpdateRunset().stream()
//									.map(MstRunset::getRunsetId).collect(toList()); 	
//		
//		String runsetList = eirRunset.stream().collect(joining("#"));
//		
//		List<CfLv1Goc> eirCfList = CfDao.getCfLv1GocStream(bssd, gocId)
//										.filter(s->eirRunset.contains(s.getRunsetId()))
//										.collect(toList());
//		if(!eirCfList.isEmpty()) {
//			double targetOci = -1.0* getOciMap().getOrDefault(gocId, 0.0);
//			log.info("TargetOci : {},{}", gocId, targetOci);
//			
//			rstList.add(EirUtil.createDfLv4Eir(eirCfList, ELiabType.LRC, runsetList, startRate,	targetOci, errorTolerance, maxIterNum, tenorAdjFn));
//		}
//		
//		return rstList.stream();
//	}
	
	private static List<MstRunset> getEirUpdateRunset() {
		if(eirRunset.isEmpty()) {
			eirRunset = PrvdMst.getMstRunsetList(ECoa.EPV).stream()
								.filter(s->s.getEirAplyYn().isTrueFalse())
								.collect(toList());
		}
		return eirRunset;
	}
	
	private static List<MstRunset> getBeforeEirUpdateRunset() {
		if(beforeEirUpdateRunset.isEmpty()) {
			beforeEirUpdateRunset = PrvdMst.getMstRunsetList(ECoa.EPV).stream()
								.filter(s->s.getBeforeEirUpdateYn().isTrueFalse())
								.collect(toList());
		}
		return beforeEirUpdateRunset;
	}
	
	private static double getOciAmt(String gocId, ELiabType liabType){
		double lrcOci = -1.0* getLrcOciMap().getOrDefault(gocId, 0.0);
		double licOci = -1.0* getLicOciMap().getOrDefault(gocId, 0.0);
		
		switch (liabType) {
		case ALL:
			return lrcOci + licOci;
		case LIC:
			return licOci;
		case LRC:
			return lrcOci ;

		default:
			return 0.0;
		}
	}
	
	private static Map<String, Double> getLrcOciMap(){
		if(ociMap.isEmpty() && !stBssd.equals(bssd)) {
			ociMap = getBeforeEirUpdateOciMap(ECoa.AOCI);
		}
		return ociMap;
	}
	
	private static Map<String, Double> getLicOciMap(){
		if(licOciMap.isEmpty() && !stBssd.equals(bssd) ) {
			licOciMap = getBeforeEirUpdateOciMap(ECoa.AOCI_LIC);
		}
		return licOciMap;
	}
	
	private static Map<String, Double> getBeforeEirUpdateOciMap(ECoa coa){
		Map<String, Double> rstMap = new HashMap<String, Double>();
		
		List<MapJournalRollFwd> journalMap = PrvdMst.getJournalRollFwdList().stream()
													.filter(s->s.hasCoa(coa))											// ECoa.AOCI, ECoa.AOCI_LIC
//														.filter(s->s.getMstCalc().getLiabType().equals(ELiabType.LRC))		//TODO : AAAAA
													.filter(s->!s.getRollFwdType().equals(ERollFwdType.CURR_CLOSE))
													.collect(toList());
		
		Set<String> calcIdSet = journalMap.stream().map(MapJournalRollFwd::getCalcId).collect(toSet());
		
		List<String> beforeRunset = getBeforeEirUpdateRunset().stream().map(MstRunset::getRunsetId).collect(toList());
		
		Map<String, List<RstBoxGoc>> aaa = BoxDao.getRstBoxGroupBy(bssd).stream()		
												 .filter(s-> calcIdSet.contains(s.getCalcId()))
												 .filter(s-> beforeRunset.contains(s.getRunsetId()))
												 .collect(groupingBy(RstBoxGoc::getGocId, toList()));
		
		for( Map.Entry<String, List<RstBoxGoc>> entry : aaa.entrySet()) {
			double aociAmt=0.0;
			for(RstBoxGoc rstBox : entry.getValue()) {
				for(MapJournalRollFwd journal : journalMap) {
					if(journal.getCalcId().equals(rstBox.getCalcId())) {
						aociAmt = aociAmt + PrvdAcct.getCoaValue(coa, journal,rstBox.getBoxValue() , 0.0);
					}
				}
				
			}
			rstMap.put(entry.getKey(), aociAmt);
		}
		
		return rstMap;
	}
}