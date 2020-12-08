package com.gof.conv;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao.RaDao;
import com.gof.dao.RawDao;
import com.gof.dao.RstDao;
import com.gof.dao.TvogDao;
import com.gof.entity.FvFlat;
import com.gof.entity.MstCalc;
import com.gof.entity.MstGoc;
import com.gof.entity.MstRollFwd;
import com.gof.entity.MstRunset;
import com.gof.entity.RaLv1;
import com.gof.entity.RawModifiedRetroRst;
import com.gof.entity.RstCsm;
import com.gof.entity.RstEpvNgoc;
import com.gof.entity.RstLossStep;
import com.gof.entity.TvogLv1;
import com.gof.enums.ECoa;
import com.gof.enums.EConvType;
import com.gof.enums.ELiabType;
import com.gof.enums.ELossStep;
import com.gof.enums.ERollFwdType;
import com.gof.enums.ERunsetType;
import com.gof.factory.FacRstCsm;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MdlInitCsm {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	
	private static Map<String, Double> rstLossStepMap		   = new HashMap<String, Double>();
	
	public static Stream<RstCsm> createConversion(){
		return Stream.concat(convertFvApproach(), convertModifiedRetroApproach());
//		return convertFvApproach();
	}
	
	private static Stream<RstCsm> convertModifiedRetroApproach(){
		List<RstCsm> rstList = new ArrayList<RstCsm>();
		
		MstCalc mstCalc		  		= PrvdMst.getMstCalcDeltaSum();
		String runsetId				= mstCalc.getCalcId();
		MstRollFwd mstRollFwd 		= PrvdMst.getMstRollFwd(ERollFwdType.CURR_CLOSE);
		
		Map<String, RawModifiedRetroRst> convRstMap = RawDao.getRawModifiedRetroRst(stBssd).stream().collect(toMap(RawModifiedRetroRst::getGocId, Function.identity())); 
		
		List<String> modifiedRetroGocList  = PrvdMst.getGocList().stream()
													.filter(s->s.getConvType().equals(EConvType.MODIFIED_RETRO))
													.peek(s-> log.info("zzzzzzz  : {}", s.getGocId()))
													.map(MstGoc::getGocId)
													.collect(toList());
		
		for(Map.Entry<String, RawModifiedRetroRst> entry : convRstMap.entrySet()) {
			String gocId 	= entry.getKey();
			if(!modifiedRetroGocList.contains(entry.getKey())) {
				
			}
			else {
				double calcCsm  = 0.0;
				double csmAmt   = 0.0;
				double lossAmt   = 0.0;
				double lossFulfillAmt = getRstLossStepMap(stBssd, gocId);
				
				log.info("zzzz: {},{},{}", gocId,convRstMap.containsKey(gocId), convRstMap.get(gocId));
//				log.info("zzzz: {},{},{}", gocId,convRstMap.containsKey(gocId), convRstMap.get(gocId).getCsmAmt());
				
				csmAmt  = convRstMap.containsKey(gocId)? convRstMap.get(gocId).getCsmAmt() :0.0;
				lossAmt = convRstMap.containsKey(gocId)? convRstMap.get(gocId).getLossAmt():0.0;
				calcCsm = csmAmt > 0 ?  csmAmt: -1.0 * lossAmt;
				
				rstList.add(FacRstCsm.buildClose(stBssd, gocId, mstRollFwd, runsetId, mstCalc, calcCsm, 0.0, calcCsm, 0.0,  csmAmt, lossFulfillAmt, "From MRA"));
			}
			
		}
//		
//		for(MstGoc goc: PrvdMst.getGocList().stream().filter(s->s.getConvType().equals(EConvType.MODIFIED_RETRO)).collect(toList())) {
//			String gocId 	= goc.getGocId();
//			if(convRstMap.containsKey(gocId)) {
//				double calcCsm  = 0.0;
//				double csmAmt   = 0.0;
//				double lossAmt   = 0.0;
//				double lossFulfillAmt = getRstLossStepMap(stBssd, gocId);
//				
//				log.info("zzzz: {},{},{}", gocId,convRstMap.containsKey(gocId), convRstMap.get(gocId));
//				log.info("zzzz: {},{},{}", gocId,convRstMap.containsKey(gocId), convRstMap.get(gocId).getCsmAmt());
//				csmAmt  = convRstMap.containsKey(gocId)? convRstMap.get(gocId).getCsmAmt() :0.0;
//				lossAmt = convRstMap.containsKey(gocId)? convRstMap.get(gocId).getLossAmt():0.0;
//				calcCsm = csmAmt > 0 ?  csmAmt: -1.0 * lossAmt;
//				
//				rstList.add(FacRstCsm.buildClose(stBssd, gocId, mstRollFwd, runsetId, mstCalc, calcCsm, 0.0, calcCsm, 0.0,  csmAmt, lossFulfillAmt, "From conversion"));
//			}
//		}
		return rstList.stream();
	}
	
	private static Stream<RstCsm> convertFvApproach(){
		List<RstCsm> rstList = new ArrayList<RstCsm>();
		
		MstCalc mstCalc		  		= PrvdMst.getMstCalcDeltaSum();
		String runsetId				= mstCalc.getCalcId();
		
		MstRollFwd mstRollFwd 		= PrvdMst.getMstRollFwd(ERollFwdType.CURR_CLOSE);
		
//		String raCloseRunsetId  	= PrvdMst.getMstRunsetMap(ECoa.RA, ERunsetType.CURR_CLOSING).getRunsetId();
//		String tvogCloseRunsetId 	= PrvdMst.getMstRunsetMap(ECoa.TVOG, ERunsetType.CURR_CLOSING).getRunsetId();
		
		List<String> raCloseRunsetList  	= PrvdMst.getMstRunsetList(ECoa.RA).stream()
														.filter(s->s.getRunsetType().equals(ERunsetType.CURR_CLOSING))
														.map(MstRunset::getRunsetId).collect(toList());
		
		List<String> tvogCloseRunsetList 	=  PrvdMst.getMstRunsetList(ECoa.TVOG).stream()
														.filter(s->s.getRunsetType().equals(ERunsetType.CURR_CLOSING))
														.map(MstRunset::getRunsetId).collect(toList());

		
		Map<String, Double> fairValueMap = RstDao.getFvFlat(stBssd).stream().collect(toMap(FvFlat::getGocId, FvFlat::getFvAmt, (s,u)->s+u));
		
		Map<String, Double> epvMap = RstDao.getRstEpvNgoc(stBssd).stream()
										   .filter(s->s.getLiabType().equals(ELiabType.LRC))
										   .collect(toMap(RstEpvNgoc::getGocId, RstEpvNgoc::getEpvAmt, (s,u)->s+u));
		
		Map<String, Double> raMap  	= RaDao.getRaLv1(stBssd).stream()
										   .filter(s->s.getLiabType().equals(ELiabType.LRC))
//										   .filter(s->s.getRunsetId().equals(raCloseRunsetId))
										   .filter(s->raCloseRunsetList.contains(s.getRunsetId()))
										   .collect(toMap(RaLv1::getGocId, RaLv1::getRaAmt, (s,u)->s+u));
		
		Map<String, Double> tvogMap = TvogDao.getTvogLv1(stBssd).stream()
//											.filter(s->s.getRunsetId().equals(tvogCloseRunsetId))
											.filter(s->tvogCloseRunsetList.contains(s.getRunsetId()))
											.collect(toMap(TvogLv1::getGocId, TvogLv1::getTvogAmt, (s,u)->s+u));
		
		List<MstGoc> fvList = PrvdMst.getGocList().stream()
										.filter(s->s.getConvType().equals(EConvType.FV))           //TODO: !!!!!
										.collect(toList());
		
		for(MstGoc goc: fvList) {
			String gocId 	= goc.getGocId();
			double calcCsm  = 0.0;
			double csmAmt   = 0.0;
			double epvAmt 	= epvMap.getOrDefault(gocId, 0.0);
			double raAmt 	= raMap.getOrDefault(gocId, 0.0);
			double tvogAmt 	= tvogMap.getOrDefault(gocId, 0.0);
			
			double fulfillAmt 	  = epvAmt + raAmt + tvogAmt ;
			double lossFulfillAmt = getRstLossStepMap(stBssd, gocId);
			
			log.info("init Csm :  {},{},{},{},{},{}", gocId, epvAmt, raAmt, tvogAmt, fulfillAmt, fairValueMap.get(gocId));
			
			if(fairValueMap.containsKey(gocId)) {
				calcCsm = fairValueMap.get(gocId) - fulfillAmt;
				csmAmt = calcCsm > 0 ? calcCsm:0.0;
//				rstList.add(FacRstCsm.buildClose(bssd,   gocId, mstRollFwd, runsetId, mstCalc, calcCsm, 0.0, calcCsm, 0.0,  csmAmt, lossFulfillAmt, "From conversion"));
				rstList.add(FacRstCsm.buildClose(stBssd, gocId, mstRollFwd, runsetId, mstCalc, calcCsm, 0.0, calcCsm, 0.0,  csmAmt, lossFulfillAmt, "From FV"));
			}
		}
		
		return rstList.stream();
	}
	
	private static double getRstLossStepMap(String bssd, String gocId) {
		if(rstLossStepMap.isEmpty()) {
			rstLossStepMap =RstDao.getRstEpvLossStep(bssd).stream()
								  .filter(s->s.getLossStep().equals(ELossStep.L5))
								  .collect(toMap(RstLossStep::getGocId, RstLossStep::getFulfillAmt, (s,u)->s+u));
		}
//		log.info("GocId In RstLossStep :  {},{}", gocId, rstLossStepMap.size());
		return rstLossStepMap.get(gocId);
	}

}