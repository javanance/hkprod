package com.gof.conv;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.RstCsm;
import com.gof.enums.ERollFwdType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MdlInitLoss {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static  String currRunsetId	=GmvConstant.RUNSET_CURR;
	
	private static Map<String, Double> rstLossStepMap		   = new HashMap<String, Double>();
	
	public static Stream<RstCsm> createConversion(){
		List<RstCsm> rstList = new ArrayList<RstCsm>();
		
		List<MapJournalRollFwd> journalList = PrvdMst.getJournalRollFwdList().stream().filter(s->s.getRollFwdType().equals(ERollFwdType.CURR_LOSS_ALLO)).collect(toList());
		
		
//		String runsetId				= mstCalc.getCalcId();
//		
//		MstRollFwd mstRollFwd 		= PrvdMst.getMstRollFwd(ERollFwdType.CURR_CLOSE);
//		
//		String raCloseRunsetId  	= PrvdMst.getMstRunsetMap(ECoa.RA, ERsDivType.CURR_CLOSING).getRunsetId();
//		
//		Map<String, Double> raMap  	= RaDao.getRaLv1(stBssd).stream()
//										   .filter(s->s.getRunsetId().equals(raCloseRunsetId))
//										   .collect(toMap(RaLv1::getGocId, RaLv1::getRaAmt, (s,u)->s+u));
//		
//		Map<String, Double> currEpvMap 	= CfDao.getCfLv1GocByRunsetStream(stBssd, currRunsetId)
//												   .collect(toMap(CfLv1Goc::getGocId, CfLv1Goc::getEpvAmt, (s,u)->s+u));
//		
//		Map<String, Double> cfMap 	= CfDao.getCfLv1GocByRunsetStream(stBssd, currRunsetId)
//				   									.collect(toMap(CfLv1Goc::getGocId, CfLv1Goc::getCfAmt, (s,u)->s+u));
//		
//		Map<String, RstCsm> csmMap = RstDao.getRstCsm(stBssd).stream()
//											.filter(s->s.getRollFwdType().equals(ERollFwdType.CURR_CLOSE))
//											.collect(toMap(RstCsm::getGocId, Function.identity()));
//		
//		
//		for(String gocId: PrvdMst.getGocIdList()) {
//			if(csmMap.containsKey(gocId)) {
//				double calcCsm = csmMap.get(gocId).getCalcCsmAmt();
//				double epvAmt 	= currEpvMap.getOrDefault(gocId, 0.0);
//				double raAmt 	= raMap.getOrDefault(gocId, 0.0);
//				double tvomAmt 	= cfMap.getOrDefault(gocId, 0.0) - epvAmt;
//				
//				rstList.add(FacRollFwdLoss.buildDelta(stBssd, gocId, ECoa.LOSS_FACE, mstRollFwd, runsetId, mstCalc, boxamt, deltacma, "From conversion"));
//				rstList.add(FacRollFwdLoss.buildDelta(stBssd, gocId, ECoa.LOSS_TVOM, mstRollFwd, runsetId, mstCalc, boxamt, deltacma, "From conversion"));
//				rstList.add(FacRollFwdLoss.buildDelta(stBssd, gocId, ECoa.LOSS_RA,   mstRollFwd, runsetId, mstCalc, boxamt, deltacma, "From conversion"));
//										   
//			}
//		}
		
		return rstList.stream();
	}
}