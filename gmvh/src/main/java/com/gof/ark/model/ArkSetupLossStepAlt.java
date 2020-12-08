package com.gof.ark.model;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.ark.dao.ArkDao;
import com.gof.ark.entity.ArkFutureCf;
import com.gof.ark.entity.ArkMstRunset;
import com.gof.dao.CfDao;
import com.gof.dao.DfDao;
import com.gof.dao.MapDao;
import com.gof.dao.RaDao;
import com.gof.entity.CfLv1Goc;
import com.gof.entity.CfLv4Df;
import com.gof.entity.DfLv1CurrRate;
import com.gof.entity.MapCfGroup;
import com.gof.entity.MstRunset;
import com.gof.entity.RaLv1;
import com.gof.entity.RstLossStep;
import com.gof.enums.EBoxModel;
import com.gof.enums.ECoa;
import com.gof.enums.ELiabType;
import com.gof.enums.ELossStep;
import com.gof.enums.ERunsetType;
import com.gof.enums.ESlidingType;
import com.gof.factory.FacLossStep;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ArkSetupLossStepAlt {
	private static	String bssd				=GmvConstant.BSSD;
	private static	String stBssd			=GmvConstant.ST_BSSD;
	private static	String lossCfGroup		=GmvConstant.LOSS_CF_GROUP;
	
//	private static	String irCurveId		=GmvConstant.IR_CURVE_ID;
//	private static	String newContRateDiv	=GmvConstant.NEW_CONT_RATE;
	
//	private static	String prevRunsetId		= GmvConstant.RUNSET_PREV;
//	private static	String currRunsetId		= GmvConstant.RUNSET_CURR;
//	private static	String deltaGroup		= GmvConstant.DELTA_GROUP_LOSS;
	
//	private static Map<String, Map<Double, Double>> curveByYymmMap = new HashMap<String, Map<Double,Double>>();
	
	private static Map<String, List<String>> cfGroupMap = new HashMap<String, List<String>>();
	
	public static Stream<RstLossStep> createConversion() {
			bssd= stBssd;
			return create();
	}
	
	public static Stream<RstLossStep> create(){
//		List<RstLossStep> rstList = new ArrayList<RstLossStep>();
		
//		for(String gocId : PrvdMst.getGocIdList()) {
//			rstList.addAll(createPrev(gocId));
//			rstList.addAll(createInitClose(gocId));
////			rstList.addAll(createReversalClose(gocId));
//			rstList.addAll(createCurr(gocId));
//		}
//		return rstList.stream();
		return PrvdMst.getGocIdList().stream().flatMap(gocId->create(gocId));
	}
	
	public static Stream<RstLossStep> create(String gocId){
		List<RstLossStep> rstList = new ArrayList<RstLossStep>();
		
		rstList.addAll(createPrev(gocId));
		rstList.addAll(createInitClose(gocId));
		rstList.addAll(createUnwindClose(gocId));
//		rstList.addAll(createReversalClose(gocId));
		rstList.addAll(createCurr(gocId));
		
		return rstList.stream();
	}
	
	private static List<RstLossStep> createPrev(String gocId){
		List<RstLossStep> rstList = new ArrayList<RstLossStep>();
		
		List<String> lossCfGroupList = getLossCfGroupList();
		
		List<String> epvRunsetList = PrvdMst.getMstRunsetList(ECoa.EPV).stream()
												.filter(s->s.getRunsetType().equals(ERunsetType.PREV_CLOSING))
												.map(MstRunset::getRunsetId)
												.collect(toList());
		
		Map<String, Double> epvMap = ArkDao.getArkFutureCfStream(bssd, gocId)
//											.filter(s->s.getRunsetId().equals(prevRunsetId))
											.filter(s->epvRunsetList.contains(s.getRunsetId()))
//											.filter(s->lossCfGroupList.contains(s.getCfKeyId()))			//TODO : cfKeyId 
											.filter(s->lossCfGroupList.contains(s.getCfType().name()))
											.collect(toMap(ArkFutureCf::getGocId, ArkFutureCf::getEpvAmt, (s,u)-> s+u))
											;
		
		Map<String, Double> cfMap = ArkDao.getArkFutureCfStream(bssd, gocId)
//				.filter(s->s.getRunsetId().equals(prevRunsetId))
				.filter(s->epvRunsetList.contains(s.getRunsetId()))
//				.filter(s->lossCfGroupList.contains(s.getCfKeyId()))			//TODO : cfKeyId 
				.filter(s->lossCfGroupList.contains(s.getCfType().name()))
				.collect(toMap(ArkFutureCf::getGocId, ArkFutureCf::getCfAmt, (s,u)-> s+u))
				;
		
		List<String> raRunsetList = PrvdMst.getMstRunsetList(ECoa.RA).stream()
												.filter(s->s.getRunsetType().equals(ERunsetType.PREV_CLOSING))
												.map(MstRunset::getRunsetId)
												.collect(toList());
		
		Map<String, Double> raMap  = RaDao.getRaLv1(bssd, gocId).stream()
											.filter(s->s.getLiabType().equals(ELiabType.LRC))
//											.filter(s-> prevRunsetId.equals(s.getRunsetId()))
											.filter(s-> raRunsetList.contains(s.getRunsetId()))
											.collect(toMap(RaLv1::getGocId, RaLv1::getRaAmt, (s,u)->s+u));
			
		String remark ="RUNSET: PREV CLOSING , CF : ALL CF OF " + lossCfGroup;
			
		double cfAmt = cfMap.getOrDefault(gocId, 0.0	);
		double epvAmt = epvMap.getOrDefault(gocId, 0.0	);
		double raAmt  = raMap.getOrDefault(gocId, 0.0	);
		
//		log.info("L1 LOSS : {},{},{}", gocId, epvAmt, raAmt);
		rstList.add(FacLossStep.build(bssd, gocId, ELossStep.L1, lossCfGroup, cfAmt, epvAmt, raAmt, remark));
		
		return rstList;
	}
	
	
	private static List<RstLossStep> createInitClose(String gocId){
		List<RstLossStep> rstList = new ArrayList<RstLossStep>();
		
		List<String> lossCfGroupList = getLossCfGroupList();
		
		List<String> epvRunsetList = PrvdMst.getMstRunsetList(ECoa.EPV).stream()
											.filter(s->s.getRunsetType().equals(ERunsetType.PREV_CLOSING)|| s.getRunsetType().isNewContYn())
											.map(MstRunset::getRunsetId)
											.collect(toList());

		Map<String, Double> epvMap = ArkDao.getArkFutureCfStream(bssd, gocId)
											.filter(s->epvRunsetList.contains(s.getRunsetId()))
//											.filter(s->lossCfGroupList.contains(s.getCfKeyId()))			//TODO : cfKeyId 
//											.filter(s->lossCfGroupList.contains(s.getCfType().name()))							//TODO : ALL CF FOR FCOST RELEASE
											.collect(toMap(ArkFutureCf::getGocId, ArkFutureCf::getEpvAmt, (s,u)-> s+u))
											;
		
		Map<String, Double> cfMap = ArkDao.getArkFutureCfStream(bssd, gocId)
					.filter(s->epvRunsetList.contains(s.getRunsetId()))
//					.filter(s->lossCfGroupList.contains(s.getCfKeyId()))			//TODO : cfKeyId 
//					.filter(s->lossCfGroupList.contains(s.getCfType().name()))		//TODO : ALL CF FOR FCOST RELEASE
					.collect(toMap(ArkFutureCf::getGocId, ArkFutureCf::getCfAmt, (s,u)-> s+u))
					;
		
		List<String> raInitRunset = PrvdMst.getMstRunsetList(ECoa.RA).stream()
											.filter(s->s.getRunsetType().isNewContYn() || s.getRunsetType().equals(ERunsetType.PREV_CLOSING))
											.map(s ->s.getRunsetId())
											.collect(toList());
		
		raInitRunset.forEach(s->log.info("zzzzz : {}", s));
		
		Map<String, Double> raMap	= RaDao.getRaLv1(bssd, gocId).stream()
											.filter(s->s.getLiabType().equals(ELiabType.LRC))							
											.filter(s-> raInitRunset.contains(s.getRunsetId()))
											.collect(toMap(RaLv1::getGocId, RaLv1::getRaAmt, (s,u)-> s+u));
		
		raMap.entrySet().forEach(s-> log.info("aaaa : {},{},{}", gocId, s.getKey(), s.getValue()));
		
		String remark ="RUNSET: PREV CLOSING + INIT, CF : ALL CF " ;
		double cfAmt = cfMap.getOrDefault(gocId, 0.0	);
		double epvAmt = epvMap.getOrDefault(gocId, 0.0	);
		double raAmt  = raMap.getOrDefault(gocId, 0.0	);
		
		rstList.add(FacLossStep.build(bssd, gocId, ELossStep.L2_1, "ALL", cfAmt, epvAmt, raAmt, remark));
		
		return rstList;
	}
	
	
	private static List<RstLossStep> createUnwindClose(String gocId){
		List<RstLossStep> rstList = new ArrayList<RstLossStep>();
		
		List<String> lossCfGroupList = getLossCfGroupList();
		
		List<String> epvRunsetList = PrvdMst.getMstRunsetList(ECoa.EPV).stream()
											.filter(s->s.getRunsetType().equals(ERunsetType.PREV_CLOSING)|| s.getRunsetType().isNewContYn())
											.map(MstRunset::getRunsetId)
											.collect(toList());

		Map<String, Double> epvMap = ArkDao.getArkFutureCfStream(bssd, gocId)
											.filter(s->epvRunsetList.contains(s.getRunsetId()))
//											.filter(s->lossCfGroupList.contains(s.getCfKeyId()))			//TODO : cfKeyId 
											.filter(s->lossCfGroupList.contains(s.getCfType().name()))						
											.collect(toMap(ArkFutureCf::getGocId, ArkFutureCf::getEpvAmt, (s,u)-> s+u))
											;
		
		Map<String, Double> cfMap = ArkDao.getArkFutureCfStream(bssd, gocId)
					.filter(s->epvRunsetList.contains(s.getRunsetId()))
//					.filter(s->lossCfGroupList.contains(s.getCfKeyId()))			//TODO : cfKeyId 
					.filter(s->lossCfGroupList.contains(s.getCfType().name()))		
					.collect(toMap(ArkFutureCf::getGocId, ArkFutureCf::getCfAmt, (s,u)-> s+u))
					;
		
		List<String> raInitRunset = PrvdMst.getMstRunsetList(ECoa.RA).stream()
											.filter(s->s.getRunsetType().isNewContYn() || s.getRunsetType().equals(ERunsetType.PREV_CLOSING))
											.map(s ->s.getRunsetId())
											.collect(toList());
		
		raInitRunset.forEach(s->log.info("zzzzz : {}", s));
		
		Map<String, Double> raMap	= RaDao.getRaLv1(bssd, gocId).stream()
											.filter(s->s.getLiabType().equals(ELiabType.LRC))							
											.filter(s-> raInitRunset.contains(s.getRunsetId()))
											.collect(toMap(RaLv1::getGocId, RaLv1::getRaAmt, (s,u)-> s+u));
		
		raMap.entrySet().forEach(s-> log.info("aaaa : {},{},{}", gocId, s.getKey(), s.getValue()));
		
		String remark ="RUNSET: PREV CLOSING + INIT, CF : ALL CF OF"+ lossCfGroup ;
		double cfAmt = cfMap.getOrDefault(gocId, 0.0	);
		double epvAmt = epvMap.getOrDefault(gocId, 0.0	);
		double raAmt  = raMap.getOrDefault(gocId, 0.0	);
		
		rstList.add(FacLossStep.build(bssd, gocId, ELossStep.L2_2, lossCfGroup, cfAmt, epvAmt, raAmt, remark));
		
		return rstList;
	}
	
//	private static List<RstLossStep> createReversalClose(String gocId){
//		List<RstLossStep> rstList = new ArrayList<RstLossStep>();
//		
//		List<String> lossCfGroupList = getLossCfGroupList();
//		
//		List<String> epvRunsetList = PrvdMst.getMstRunsetList(ECoa.EPV).stream()
//										.filter(s->s.getRunsetType().equals(ERunsetType.PREV_CLOSING)|| s.getRunsetType().isNewContYn())
//										.map(MstRunset::getRunsetId)
//										.collect(toList());
//
//		Map<String, Double> epvMap = ArkDao.getArkFutureCfStream(bssd)
//										.filter(s->epvRunsetList.contains(s.getRunsetId()))
//										.filter(s->lossCfGroupList.contains(s.getCfKeyId()))
//										.collect(toMap(ArkFutureCf::getGocId, ArkFutureCf::getEpvAmt, (s,u)-> s+u))
//										;
//		
////		String serviceCloseRunsetId = PrvdMst.getMstRunsetMap(ECoa.RA, ERunsetType.SERVICE_CLOSING).getRunsetId();
//		List<String> serviceCloseRunsetList = PrvdMst.getMstRunsetList(ECoa.RA).stream()
//													.filter(s->s.getRunsetType().equals(ERunsetType.SERVICE_CLOSING))
//													.map(MstRunset::getRunsetId)
//													.collect(toList());
//		
//		Map<String, Double> raMap	= RaDao.getRaLv1(bssd, gocId).stream()
//											.filter(s->s.getLiabType().equals(ELiabType.LRC))
////											.filter(s-> serviceCloseRunsetId.equals(s.getRunsetId()))
//											.filter(s-> serviceCloseRunsetList.contains(s.getRunsetId()))
//											.collect(toMap(RaLv1::getGocId, RaLv1::getRaAmt, (s,u)-> s+u));
//		
//		String remark ="EPV: future CF of Prev+init, RA: service_closing";
//		
//		double epvAmt = epvMap.getOrDefault(gocId, 0.0	);
//		double raAmt  = raMap.getOrDefault(gocId, 0.0	);
//			
//		log.info("L3 LOSS : {},{},{}", gocId, epvAmt, raAmt);
//		
//		rstList.add(FacLossStep.build(bssd, gocId, ELossStep.L3, lossCfGroup, epvAmt, raAmt, remark));
//		rstList.add(FacLossStep.build(bssd, gocId, ELossStep.L4, lossCfGroup, epvAmt, raAmt, remark));
//		
//		return rstList;
//	}

	
	private static List<RstLossStep> createCurr(String gocId){
		List<RstLossStep> rstList = new ArrayList<RstLossStep>();
		List<String> lossCfGroupList = getLossCfGroupList();
		
		List<String> epvRunsetList = PrvdMst.getMstRunsetList(ECoa.EPV).stream()
											.filter(s->s.getRunsetType().equals(ERunsetType.CURR_CLOSING))
											.map(MstRunset::getRunsetId)
											.collect(toList());

		Map<String, Double> epvMap = ArkDao.getArkFutureCfStream(bssd, gocId)
											.filter(s->epvRunsetList.contains(s.getRunsetId()))
//											.filter(s->lossCfGroupList.contains(s.getCfKeyId()))			//TODO : cfKeyId 
											.filter(s->lossCfGroupList.contains(s.getCfType().name()))
											.collect(toMap(ArkFutureCf::getGocId, ArkFutureCf::getEpvAmt, (s,u)-> s+u))
											;
		
		Map<String, Double> cfMap = ArkDao.getArkFutureCfStream(bssd, gocId)
				.filter(s->epvRunsetList.contains(s.getRunsetId()))
//				.filter(s->lossCfGroupList.contains(s.getCfKeyId()))			//TODO : cfKeyId 
				.filter(s->lossCfGroupList.contains(s.getCfType().name()))
				.collect(toMap(ArkFutureCf::getGocId, ArkFutureCf::getCfAmt, (s,u)-> s+u))
				;
		
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
		double cfAmt = cfMap.getOrDefault(gocId, 0.0	);
		double epvAmt = epvMap.getOrDefault(gocId, 0.0	);
		double raAmt  = raMap.getOrDefault(gocId, 0.0	);
		
		rstList.add(FacLossStep.build(bssd, gocId, ELossStep.L5, lossCfGroup, cfAmt, epvAmt, raAmt, remark));
		
		return rstList;
	}
	
//	private static List<String> getCfKeyIdList() {
//		if(cfKeyIdList.isEmpty()) {
//			cfKeyIdList = MapDao.getMapCfGroup().stream().filter(s->s.getCfGroupId().equals(lossCfGroup)).map(MapCfGroup::getCfKeyId).collect(toList());
//		}
//		return cfKeyIdList;
//	}
	
	private static List<String> getLossCfGroupList(){
		if(cfGroupMap.isEmpty()) {
			cfGroupMap = MapDao.getMapCfGroup().stream().collect(groupingBy(MapCfGroup::getCfGroupId, mapping(MapCfGroup::getCfKeyId, toList())));
		}
//		return cfGroupMap.get(lossCfGroup);
//		TODO!!!!!!
		return Arrays.asList("INS", "DMC", "DCE");
		
	}
	
}
