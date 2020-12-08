package com.gof.conv;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.dao.RstDao;
import com.gof.dao.WaterfallDao;
import com.gof.entity.ElLv1;
import com.gof.entity.FvFlat;
import com.gof.entity.MstCalc;
import com.gof.entity.MstGoc;
import com.gof.entity.MstRollFwd;
import com.gof.entity.RaLv1;
import com.gof.entity.RstCsm;
import com.gof.entity.RstEpvNgoc;
import com.gof.entity.RstLossRcv;
import com.gof.entity.RstLossStep;
import com.gof.enums.ECoa;
import com.gof.enums.EConvType;
import com.gof.enums.ELiabType;
import com.gof.enums.ELossStep;
import com.gof.enums.ERollFwdType;
import com.gof.enums.ERunsetType;
import com.gof.factory.FacRstCsmReins;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MdlInitCsmReins {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	
	private static Map<String, Double> rstLossStepMap		   = new HashMap<String, Double>();
	
	public static Stream<RstCsm> createConversion(){
		List<RstCsm> rstList = new ArrayList<RstCsm>();
		
		MstCalc mstCalc		  		= PrvdMst.getMstCalcDeltaSum();
		String runsetId				= mstCalc.getCalcId();
		
		MstRollFwd mstRollFwd 		= PrvdMst.getMstRollFwd(ERollFwdType.CURR_CLOSE);
		
		String raCloseRunsetId  	= PrvdMst.getMstRunsetMap(ECoa.RA, ERunsetType.CURR_CLOSING).getRunsetId();
		String elCloseRunsetId  	= PrvdMst.getMstRunsetMap(ECoa.EL, ERunsetType.CURR_CLOSING).getRunsetId();
		
//		손실회복요소 add
//		String lossRcvCloseRunsetId = PrvdMst.getMstRunsetMap(ECoa.LOSS_RCV, ERunsetType.CURR_CLOSING).getRunsetId();
		
//		String tvogCloseRunsetId 	= PrvdMst.getMstRunsetMap(ECoa.TVOG, ERunsetType.CURR_CLOSING).getRunsetId();
		
		Map<String, Double> fairValueMap = RstDao.getFvFlat(stBssd).stream().collect(toMap(FvFlat::getGocId, FvFlat::getFvAmt, (s,u)->s+u));
		
		Map<String, Double> epvMap = RstDao.getRstEpvNgoc(stBssd).stream()
											.filter(s->s.getLiabType().equals(ELiabType.LRC))
											.collect(toMap(RstEpvNgoc::getGocId, RstEpvNgoc::getEpvAmt, (s,u)->s+u));
		
		Map<String, Double> raMap  	= WaterfallDao.getRaLv1(stBssd).stream()
										   .filter(s->s.getRunsetId().equals(raCloseRunsetId))
										   .collect(toMap(RaLv1::getGocId, RaLv1::getRaAmt, (s,u)->s+u));

		Map<String, Double> elMap  	= WaterfallDao.getElLv1(stBssd).stream()
											.filter(s->s.getRunsetId().equals(elCloseRunsetId))
											.collect(toMap(ElLv1::getGocId, ElLv1::getElAmt, (s,u)->s+u));
		
//		손실회복요소 add waterfallDao에서 가져올것인가 ?????? -> 별도로 정의해야 하나  ?
		Map<String, Double> lossRcvMap  	= RstDao.getRstLossRcvGroupBy(stBssd).stream()
											.collect(toMap(RstLossRcv::getGocId, RstLossRcv::getBoxValue, (s,u)->s+u));
		
		
//		Map<String, Double> tvogMap = TvogDao.getTvogLv1(stBssd).stream()
//											.filter(s->s.getRunsetId().equals(tvogCloseRunsetId))
//											.collect(toMap(TvogLv1::getGocId, TvogLv1::getTvogAmt, (s,u)->s+u));
		
//		for(String gocId: PrvdMst.getGocIdList()) {
		for(MstGoc goc: PrvdMst.getGocList().stream().filter(s->s.getConvType().equals(EConvType.FV)).collect(toList())) {
			String gocId 	= goc.getGocId();
			double calcCsm  = 0.0;
			double csmAmt   = 0.0;
			double epvAmt 	= epvMap.getOrDefault(gocId, 0.0);
			double raAmt 	= raMap.getOrDefault(gocId, 0.0);
			double elAmt 	= elMap.getOrDefault(gocId, 0.0);
//			double tvogAmt 	= tvogMap.getOrDefault(gocId, 0.0);
//			double tvogAmt 	= 0.0;
			double lossRcvAmt =lossRcvMap.getOrDefault(gocId, 0.0);
			
//			전환시점의 csm 결정 시, 전환시점 loss rcv만크 차감해야해야 함 
			double fulfillAmt 	  = epvAmt + raAmt + elAmt + lossRcvAmt ; 
 
//			double lossFulfillAmt = getRstLossStepMap(stBssd, gocId);
			
			log.info("init Csm :  {},{},{},{},{},{},{}", gocId, epvAmt, raAmt,elAmt, lossRcvAmt, fulfillAmt, fairValueMap.get(gocId));
			
//			if(fairValueMap.containsKey(gocId)) {
				calcCsm = fairValueMap.getOrDefault(gocId,0.0) - fulfillAmt;
//				����� ���� csm ���
//				csmAmt = calcCsm > 0 ? calcCsm:0.0;
				csmAmt = calcCsm;
//				rstList.add(FacRstCsm.buildClose(bssd,   gocId, mstRollFwd, runsetId, mstCalc, calcCsm, 0.0, calcCsm, 0.0,  csmAmt, lossFulfillAmt, "From conversion"));
				rstList.add(FacRstCsmReins.buildClose(stBssd, gocId, mstRollFwd, runsetId, mstCalc, calcCsm, calcCsm, csmAmt, "From conversion"));
//			}
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