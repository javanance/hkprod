package com.gof.rollfwd;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.hibernate.mapping.Collection;

import com.gof.dao.RstDao;
import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MstCalc;
import com.gof.entity.MstRollFwd;
import com.gof.entity.RstBoxGoc;
import com.gof.entity.RstCsm;
import com.gof.entity.RstLoss;
import com.gof.entity.RstRollFwd;
import com.gof.entity.RstRollFwdLoss;
import com.gof.enums.ECalcType;
import com.gof.enums.ECoa;
import com.gof.enums.ELossStep;
import com.gof.enums.ERollFwdType;
import com.gof.factory.FacRollFwdLoss;
import com.gof.infra.GmvConstant;
import com.gof.interfaces.IBoxRst;
import com.gof.provider.PrvdAcct;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MdlRollFwdLossAlt {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	
	
	private static Map<String, List<RstLoss>> rstLossMap 		 = new HashMap<String, List<RstLoss>>();
	
	private static Map<String, Double> prevCloseMap 			 = new HashMap<String, Double>();
	private static Map<String, Map<ECoa,Double>> prevLossAlloMap = new HashMap<String, Map<ECoa,Double>>();
	
	public static Stream<RstRollFwdLoss> create(){
		List<RstRollFwdLoss> rstList = new ArrayList<RstRollFwdLoss>();
		for(String gocId :PrvdMst.getGocIdList()) {
			rstList.addAll(create(gocId).collect(toList()));
		}
		return rstList.stream();
	}
	
	public static Stream<RstRollFwdLoss> create(String gocId){
		List<RstRollFwdLoss> rstList = new ArrayList<RstRollFwdLoss>();
		for(ECoa coa : ECoa.getLossCoaList()) {
			rstList.addAll(createByCoa(gocId, coa).collect(toList()));
		}
		return rstList.stream();
	}
	
	public static Stream<RstRollFwdLoss> createByCoa(String gocId, ECoa coa){
		List<RstRollFwdLoss> rstList = new ArrayList<RstRollFwdLoss>();
		
		List<RstLoss> rstLossList = getRstLossMap(gocId).stream().collect(toList());
		
		for(RstLoss rstLoss : rstLossList) {	
			double deltaAmt =0.0;
			double closeAmt =0.0;
			ERollFwdType rollFwd = rstLoss.getRollFwdType();
			
			switch (coa){
				case LOSS_FACE:
					deltaAmt = rollFwd.isClose()? 0.0 : rstLoss.getLossFaceAmt();
					closeAmt = rollFwd.isClose()? rstLoss.getLossFaceAmt() : 0.0;
					
					break;
				case LOSS_TVOM:
					deltaAmt = rollFwd.isClose()? 0.0 : rstLoss.getLossTvom();
					closeAmt = rollFwd.isClose()? rstLoss.getLossTvom() : 0.0;
					break;

				case LOSS_RA:
					deltaAmt = rollFwd.isClose()? 0.0 : rstLoss.getLossRa();
					closeAmt = rollFwd.isClose()? rstLoss.getLossRa() : 0.0;
					break;
					
				case LOSS_EPV:
					deltaAmt = rollFwd.isClose()? 0.0 : rstLoss.getLossEpv();
					closeAmt = rollFwd.isClose()? rstLoss.getLossEpv() : 0.0;
					break;	
				case LOSS:
					deltaAmt = rollFwd.isClose()? 0.0 : rstLoss.getLossAmt();
					closeAmt = rollFwd.isClose()? rstLoss.getLossAmt() : 0.0;
					break;
				default:
					break;
			}
			rstList.add(FacRollFwdLoss.buildClose(bssd, gocId, coa, rollFwd, rstLoss.getMstCalc(), rstLoss.getRunsetId(), deltaAmt, deltaAmt, closeAmt, "FROM RstLoss"));
		}
		return rstList.stream();
	}
	
	

//		Map<ERollFwdType, List<RstLoss>> rstLossMap = getRstLossMap(gocId).stream().collect(groupingBy(RstLoss::getRollFwdType, toList()));
//for(Map.Entry<ERollFwdType, List<RstLoss>> entry : rstLossMap.entrySet()) {
////	for(RstLoss rstLoss : entry.getValue()){
//for(RstLoss rstLoss : rstLossList) {	
//	double deltaAmt =0.0;
//	double closeAmt =0.0;
//
//	log.info("in equation : {},{}", gocId, rstLoss.toString());
//		switch (coa) {
//			case LOSS_FACE:
//				deltaAmt = entry.getKey().isClose()? 0.0 : rstLoss.getLossFaceAmt();
//				closeAmt = entry.getKey().isClose()? rstLoss.getLossFaceAmt() : 0.0;
//				
//				break;
//			case LOSS_TVOM:
//				deltaAmt = entry.getKey().isClose()? 0.0 : rstLoss.getLossTvom();
//				closeAmt = entry.getKey().isClose()? rstLoss.getLossTvom() : 0.0;
//				break;
//
//			case LOSS_RA:
//				deltaAmt = entry.getKey().isClose()? 0.0 : rstLoss.getLossRa();
//				closeAmt = entry.getKey().isClose()? rstLoss.getLossRa() : 0.0;
//				break;
//			case LOSS:
//				deltaAmt = entry.getKey().isClose()? 0.0 : rstLoss.getLossAmt();
//				closeAmt = entry.getKey().isClose()? rstLoss.getLossAmt() : 0.0;
//				break;
//
//			default:
//				break;
//			}
//			rstList.add(FacRollFwdLoss.buildClose(bssd, gocId, coa, entry.getKey(), rstLoss.getMstCalc(), rstLoss.getRunsetId(), deltaAmt, deltaAmt, closeAmt, "FROM RstLoss"));
////			}
//	}
//}
//	
//	public static Stream<RstRollFwdLoss> createByCoa(String gocId, ECoa coa){
//		List<RstRollFwdLoss> rstList = new ArrayList<RstRollFwdLoss>();
//		
//		List<MstRollFwd> rollFwdList = PrvdMst.getMstRollFwdList().stream().filter(s-> s.getCoaMap().get(coa)).collect(toList());
//
//		Map<ERollFwdType, List<RstLoss>> rstLossMap = getRstLossMap(gocId).stream().collect(groupingBy(RstLoss::getRollFwdType, toList()));
//		
//		for(MstRollFwd mstRollFwd : rollFwdList) {
//			
//			double deltaAmt =0.0;
//			double closeAmt =0.0;
//			
//			log.info("zzzzzz : {},{},{}", mstRollFwd.getRollFwdType(), rstLossMap.containsKey(mstRollFwd.getRollFwdType()));
//			
//			if(rstLossMap.containsKey(mstRollFwd.getRollFwdType())) {
//				for(RstLoss rstLoss : rstLossMap.get(mstRollFwd.getRollFwdType())){
//
//						log.info("in equation : {},{}", gocId, rstLoss.toString());
//						switch (coa) {
//						case LOSS_FACE:
//							deltaAmt = mstRollFwd.isCloseStep()? 0.0 : rstLoss.getLossFaceAmt();
//							closeAmt = mstRollFwd.isCloseStep()? rstLoss.getLossFaceAmt() : 0.0;
//							
//							break;
//						case LOSS_TVOM:
//							deltaAmt = mstRollFwd.isCloseStep()? 0.0 : rstLoss.getLossTvom();
//							closeAmt = mstRollFwd.isCloseStep()? rstLoss.getLossTvom() : 0.0;
//							break;
//	
//						case LOSS_RA:
//							deltaAmt = mstRollFwd.isCloseStep()? 0.0 : rstLoss.getLossRa();
//							closeAmt = mstRollFwd.isCloseStep()? rstLoss.getLossRa() : 0.0;
//							break;
//						case LOSS:
//							deltaAmt = mstRollFwd.isCloseStep()? 0.0 : rstLoss.getLossAmt();
//							closeAmt = mstRollFwd.isCloseStep()? rstLoss.getLossAmt() : 0.0;
//							break;
//	
//						default:
//							break;
//						}
//						rstList.add(FacRollFwdLoss.buildClose(bssd, gocId, coa, mstRollFwd.getRollFwdType(), rstLoss.getMstCalc(), rstLoss.getRunsetId(), deltaAmt, deltaAmt, closeAmt, "FROM RstLoss"));
////					}
//				}
//			}
//		}
//		
//		return rstList.stream();
//	}
	
	
	
	

	private static List<RstLoss> getRstLossMap(String gocId) {
		if(rstLossMap.isEmpty()) {
			rstLossMap  = RstDao.getRstLoss(bssd).stream().collect(groupingBy(RstLoss::getGocId, toList()));
		}
		return rstLossMap.getOrDefault(gocId, new ArrayList<RstLoss>());
	}
	
	private static Map<ECoa, Double> getPrevLossAllocationMap(String gocId) {
		if(prevLossAlloMap.isEmpty()) {
			prevLossAlloMap = RstDao.getRstRollFwdLoss(stBssd).stream()
										.filter(s->s.getRollFwdType().equals(ERollFwdType.CURR_LOSS_ALLO))
										.collect(groupingBy(RstRollFwdLoss::getGocId, toMap(RstRollFwdLoss::getCoaId, RstRollFwdLoss::getAppliedAmt, (s,u)->s+u)))
										;
		}
		return prevLossAlloMap.getOrDefault(gocId, new HashMap<ECoa, Double>());
	}
	
	
}