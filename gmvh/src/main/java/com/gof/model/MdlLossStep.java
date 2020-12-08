package com.gof.model;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.gof.dao.CfDao;
import com.gof.dao.MapDao;
import com.gof.dao.RaDao;
import com.gof.entity.CfLv4Df;
import com.gof.entity.MapCfClass;
import com.gof.entity.MapCfGroup;
import com.gof.entity.MstRunset;
import com.gof.entity.RaLv1;
import com.gof.entity.RstLossStep;
import com.gof.enums.EBoxModel;
import com.gof.enums.ECfType;
import com.gof.enums.ECoa;
import com.gof.enums.ELiabType;
import com.gof.enums.ELossStep;
import com.gof.enums.ERunsetType;
import com.gof.enums.ESlidingType;
import com.gof.factory.FacLossStep;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MdlLossStep {
	private static	String bssd				=GmvConstant.BSSD;
	private static	String stBssd			=GmvConstant.ST_BSSD;
//	private static	String lossCalcType		=GmvConstant.CALC_TYPE_LOSS_RATIO;
	private static	String lossCfGroup		=GmvConstant.LOSS_CF_GROUP;					//G_LOSS
	private static	String gocGroup			=GmvConstant.GOC_GROUP;
	
	private static	String prevRunsetId		= GmvConstant.RUNSET_PREV;
	private static	String currRunsetId		= GmvConstant.RUNSET_CURR;
	private static	String deltaGroup		= GmvConstant.DELTA_GROUP_LOSS;

//	private static List<ECfType> cfTypeList = new ArrayList<ECfType>();
	private static List<String> cfKeyIdList = new ArrayList<String>();
	
//	private static Predicate<CfLv4Df> cfTypePredicate 	= s-> getCfTypeList().contains(s.getCfType());
	private static Predicate<CfLv4Df> cfKeyIdPredicate 	= s-> getCfKeyIdList().contains(s.getCfKeyId());
	
//	private static Function<CfLv4Df, Double> 	  valueFn 		= CfLv4Df::getEpvAmt;
//	private static	Function<CfLv4Df, Double> tenorAdjFn 		= s-> s.getCfMonthNum();
	
	public static Stream<RstLossStep> createConversion(){
		List<RstLossStep> rstList = new ArrayList<RstLossStep>();
		bssd =stBssd;
		for(String gocId : PrvdMst.getGocIdList()) {
			rstList.addAll(createCurr(gocId));
		}
		return rstList.stream();
	}

	public static Stream<RstLossStep> create(){
		List<RstLossStep> rstList = new ArrayList<RstLossStep>();
		
		for(String gocId : PrvdMst.getGocIdList()) {
			rstList.addAll(createPrev(gocId));
			rstList.addAll(createInitClose(gocId));
			rstList.addAll(createReversalClose(gocId));
			rstList.addAll(createCurr(gocId));
		}
		return rstList.stream();
	}
	
	public static Stream<RstLossStep> create(String gocId){
		List<RstLossStep> rstList = new ArrayList<RstLossStep>();
		
		rstList.addAll(createPrev(gocId));
		rstList.addAll(createInitClose(gocId));
		rstList.addAll(createReversalClose(gocId));
		rstList.addAll(createCurr(gocId));
		
		return rstList.stream();
	}

	private static List<RstLossStep> createPrev(String gocId){
		List<RstLossStep> rstList = new ArrayList<RstLossStep>();
			
		Map<String, Double> epvMap	= CfDao.getCfLv4DfByRunsetStream(bssd, prevRunsetId, gocId)
											.filter(s->s.getLiabType().equals(ELiabType.LRC))
											.filter(cfKeyIdPredicate)
											.collect(toMap(CfLv4Df::getGocId, cf->cf.getBoxValue(EBoxModel.p0), (s,u)->s+u));	  // TODO : cf lv4 tenor adj

		Map<String, Double> cfMap	= CfDao.getCfLv4DfByRunsetStream(bssd, prevRunsetId, gocId)
											.filter(s->s.getLiabType().equals(ELiabType.LRC))
											.filter(cfKeyIdPredicate)
											.collect(toMap(CfLv4Df::getGocId, CfLv4Df::getCfAmt, (s,u)->s+u));	  // TODO : cf lv4 tenor adj
		
//		String prevRunsetId = PrvdMst.getMstRunsetMap(ECoa.RA, ERunsetType.PREV_CLOSING).getRunsetId();
		List<String> prevRaRunsetList = PrvdMst.getMstRunsetList(ECoa.RA).stream()
												.filter(s->s.getRunsetType().equals(ERunsetType.PREV_CLOSING))
												.map(MstRunset::getRunsetId)
												.collect(toList());
		
		Map<String, Double> raMap  = RaDao.getRaLv1(bssd, gocId).stream()
											.filter(s->s.getLiabType().equals(ELiabType.LRC))
//											.filter(s-> prevRunsetId.equals(s.getRunsetId()))
											.filter(s-> prevRaRunsetList.contains(s.getRunsetId()))
											.collect(toMap(RaLv1::getGocId, RaLv1::getRaAmt, (s,u)->s+u));
			
		String remark ="RUNSET: PREV CLOSING , CF : ALL CF OF " + lossCfGroup;
		
		double cfAmt =  cfMap.getOrDefault(gocId, 0.0	);
		double epvAmt = epvMap.getOrDefault(gocId, 0.0	);
		double raAmt  = raMap.getOrDefault(gocId, 0.0	);
//		log.info("L1 LOSS : {},{},{}", gocId, epvAmt, raAmt);
		rstList.add(FacLossStep.build(bssd, gocId, ELossStep.L1, lossCfGroup, cfAmt,  epvAmt, raAmt, remark));
			
		return rstList;
	}

	private static List<RstLossStep> createInitClose(String gocId){
		List<RstLossStep> rstList = new ArrayList<RstLossStep>();
		
		Map<String, Double> epvMap	= CfDao.getCfLv4DfByDeltaGroupStream(bssd, deltaGroup, gocId)
										   .filter(s->s.getLiabType().equals(ELiabType.LRC))
										   .filter(cfKeyIdPredicate)
										   .collect(toMap(CfLv4Df::getGocId, cf->cf.getBoxValue(EBoxModel.p0), (s,u)->s+u));	  // TODO : cf lv4 tenor adj
		
		Map<String, Double> cfMap	= CfDao.getCfLv4DfByDeltaGroupStream(bssd, deltaGroup, gocId)
										   .filter(s->s.getLiabType().equals(ELiabType.LRC))
										   .filter(cfKeyIdPredicate)
										   .collect(toMap(CfLv4Df::getGocId, CfLv4Df::getCfAmt, (s,u)->s+u));	  // TODO : cf lv4 tenor adj
		
		List<String> raInitRunset = PrvdMst.getMstRunsetList(ECoa.RA).stream()
											.filter(s->s.getRunsetType().isNewContYn() || s.getRunsetType().equals(ERunsetType.PREV_CLOSING))
											.map(s ->s.getRunsetId())
											.collect(toList());
		
		raInitRunset.forEach(s->log.info("zzzzz : {}", s));
		
		Map<String, Double> raMap	= RaDao.getRaLv1(bssd, gocId).stream()
											.filter(s->s.getLiabType().equals(ELiabType.LRC))							
											.filter(s-> raInitRunset.contains(s.getRunsetId()))
											.collect(toMap(RaLv1::getGocId, RaLv1::getRaAmt, (s,u)-> s+u));
		
		raMap.entrySet().forEach(s-> log.info("aaaa : {},{}", s.getKey(), s.getValue()));
		
		String remark ="RUNSET: PREV CLOSING + INIT, CF : ALL CF " ;
		
		double cfAmt  = cfMap.getOrDefault(gocId, 0.0	);
		double epvAmt = epvMap.getOrDefault(gocId, 0.0	);
		double raAmt  = raMap.getOrDefault(gocId, 0.0	);
		log.info("L2 LOSS : {},{},{},{}", gocId, epvAmt, raAmt, deltaGroup);	
		
		rstList.add(FacLossStep.build(bssd, gocId, ELossStep.L2, lossCfGroup, cfAmt, epvAmt, raAmt, remark));
		
		return rstList;
	}
	
	private static List<RstLossStep> createReversalClose(String gocId){
		List<RstLossStep> rstList = new ArrayList<RstLossStep>();
		
		Map<String, Double>  epvMap = CfDao.getCfLv4DfByDeltaGroupStream(bssd, deltaGroup, gocId)
											.filter(s->s.getLiabType().equals(ELiabType.LRC))
											.filter(cfKeyIdPredicate)	
											.filter(s->s.isFutureCf(0.0))
											.collect(toMap(CfLv4Df::getGocId, cf->cf.getBoxValue(EBoxModel.C0), (s,u)->s+u))  // TODO : cf lv4 tenor adj
											;
		
		Map<String, Double>  cfMap = CfDao.getCfLv4DfByDeltaGroupStream(bssd, deltaGroup, gocId)
											.filter(s->s.getLiabType().equals(ELiabType.LRC))
											.filter(cfKeyIdPredicate)	
											.filter(s->s.isFutureCf(0.0))
											.collect(toMap(CfLv4Df::getGocId, CfLv4Df::getCfAmt, (s,u)->s+u))  				// TODO : cf lv4 tenor adj
											;
//		String serviceCloseRunsetId = PrvdMst.getMstRunsetMap(ECoa.RA, ERunsetType.SERVICE_CLOSING).getRunsetId();
		List<String> serviceCloseRunsetList = PrvdMst.getMstRunsetList(ECoa.RA).stream()
													.filter(s->s.getRunsetType().equals(ERunsetType.SERVICE_CLOSING))
													.map(MstRunset::getRunsetId)
													.collect(toList());
		
		Map<String, Double> raMap	= RaDao.getRaLv1(bssd, gocId).stream()
											.filter(s->s.getLiabType().equals(ELiabType.LRC))
											.filter(s-> serviceCloseRunsetList.contains(s.getRunsetId()))
											.collect(toMap(RaLv1::getGocId, RaLv1::getRaAmt, (s,u)-> s+u));
		 
		String remark ="RUNSET: Prev+init, service_closing, CF : future CF of";
		
		double cfAmt = cfMap.getOrDefault(gocId, 0.0	);
		double epvAmt = epvMap.getOrDefault(gocId, 0.0	);
		double raAmt  = raMap.getOrDefault(gocId, 0.0	);
			
		log.info("L3 LOSS : {},{},{}", gocId, epvAmt, raAmt);
		
		rstList.add(FacLossStep.build(bssd, gocId, ELossStep.L3, lossCfGroup, cfAmt, epvAmt, raAmt, remark));
		rstList.add(FacLossStep.build(bssd, gocId, ELossStep.L4, lossCfGroup, cfAmt, epvAmt, raAmt, remark));
		
		return rstList;
	}

	
	private static List<RstLossStep> createCurr(String gocId){
		List<RstLossStep> rstList = new ArrayList<RstLossStep>();
		
		Map<String, Double>  epvMap = CfDao.getCfLv4DfByRunsetStream(bssd, currRunsetId, gocId)
											.filter(s->s.getLiabType().equals(ELiabType.LRC))
											.filter(cfKeyIdPredicate)	
											.filter(s->s.isFutureCf(0.0))
											.collect(toMap(CfLv4Df::getGocId, cf->cf.getBoxValue(EBoxModel.C0), (s,u)->s+u))  // TODO : cf lv4 tenor adj
											;
		
		Map<String, Double>  cfMap = CfDao.getCfLv4DfByRunsetStream(bssd, currRunsetId, gocId)
											.filter(s->s.getLiabType().equals(ELiabType.LRC))
											.filter(cfKeyIdPredicate)	
											.filter(s->s.isFutureCf(0.0))
											.collect(toMap(CfLv4Df::getGocId, CfLv4Df::getCfAmt, (s,u)->s+u))  				// TODO : cf lv4 tenor adj
											;

		getCfKeyIdList().forEach(s-> log.info("cf Type :  {},{}", s));
		
//		TODO :Check!!!!!!!
//		String currRunsetId = PrvdMst.getMstRunsetMap(ECoa.RA, ERunsetType.CURR_CLOSING).getRunsetId();
		
		List<String> currRunsetList = PrvdMst.getMstRunsetList(ECoa.RA).stream()
											.filter(s->s.getRunsetType().equals(ERunsetType.CURR_CLOSING))
											.map(MstRunset::getRunsetId)
											.collect(toList());
		
		Map<String, Double> raMap	= RaDao.getRaLv1(bssd, gocId).stream()
											.filter(s->s.getLiabType().equals(ELiabType.LRC))
											.filter(s-> currRunsetList.contains(s.getRunsetId()))
											.collect(toMap(RaLv1::getGocId, RaLv1::getRaAmt, (s,u)-> s+u));
		
		String remark ="RUNSET: CURR_CLOSING, CF:  FUTURE CF OF "+ lossCfGroup;
		
		double cfAmt =  cfMap.getOrDefault(gocId, 0.0	);
		double epvAmt = epvMap.getOrDefault(gocId, 0.0	);
		double raAmt  = raMap.getOrDefault(gocId, 0.0	);
		
//		log.info("L5 LOSS : {},{},{}", gocId, epvAmt, raAmt);
		
		rstList.add(FacLossStep.build(bssd, gocId, ELossStep.L5, lossCfGroup, cfAmt, epvAmt, raAmt, remark));
		
		return rstList;
	}
	
//	private static List<ECfType> getCfTypeList() {
//		if(cfTypeList.isEmpty()) {
//			cfTypeList = MapDao.getMapCfClass().stream().filter(s->s.getCfClassId().equals(lossCfGroup)).map(MapCfClass::getCfType).collect(toList());
//		}
//		return cfTypeList;
//	}
	
	private static List<String> getCfKeyIdList() {
		if(cfKeyIdList.isEmpty()) {
			cfKeyIdList = MapDao.getMapCfGroup().stream().filter(s->s.getCfGroupId().equals(lossCfGroup)).map(MapCfGroup::getCfKeyId).collect(toList());
		}
		return cfKeyIdList;
	}
}