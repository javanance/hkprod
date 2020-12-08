package com.gof.model;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.dao.BoxDao;
import com.gof.dao.MstDao;
import com.gof.dao.RatioDao;
import com.gof.dao.RstDao;
import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MapRunsetCalc;
import com.gof.entity.MstCalc;
import com.gof.entity.MstCalcRfwdDetail;
import com.gof.entity.MstRollFwd;
import com.gof.entity.RatioLv2;
import com.gof.entity.RstBoxGoc;
import com.gof.entity.RstCsm;
import com.gof.entity.RstRollFwd;
import com.gof.enums.EBoolean;
import com.gof.enums.ECoa;
import com.gof.enums.ERollFwdType;
import com.gof.factory.FacRstCsmReins;
import com.gof.infra.GmvConstant;
import com.gof.interfaces.IBoxRst;
import com.gof.provider.PrvdAcct;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MdlRstCsmReins {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;

	private static List<MapRunsetCalc> runsetCalcList = new ArrayList<MapRunsetCalc>();
	
	private static Map<String, List<RstBoxGoc>> rstBoxMap 	= new HashMap<String, List<RstBoxGoc>>();
	
	private static Map<String, Map<String, Double>> ratioMap 	= new HashMap<String, Map<String,Double>>();
	private static Map<String, Map<ECoa, Double>> prevRfwdRstMap 	= new HashMap<String, Map<ECoa,Double>>();
	
	public static Stream<RstCsm> create() {
//		List<String> gocIdList = PrvdMst.getGocIdList();
//		return BoxDao.getRstBoxGroupBy(bssd).stream().filter(s->gocIdList.contains(s.getGocId()));
		return PrvdMst.getGocIdList().stream().flatMap(s-> create(s));
	}
	
	
	public static Stream<RstCsm> create(String gocId){
		List<RstCsm> rstList = new ArrayList<RstCsm>();
		ECoa coa = ECoa.CSM;
										
		Map<String, List<IBoxRst>> rstMap = getRstBoxMap(gocId).stream().collect(groupingBy(RstBoxGoc::getCalcId, toList()));
		Map<String, Double> ratioMap 	  = getRatioMap(gocId);
		
		Map<String, List<MstCalcRfwdDetail>> calcDetailMap = MstDao.getMstCalcRfwdDetail().stream().collect(groupingBy(MstCalcRfwdDetail::getCalcId , toList()));

		Map<ERollFwdType, List<MapJournalRollFwd>> journalMap 
		= PrvdMst.getJournalRollFwdList().stream().filter(s->s.hasCoa(coa)).collect(groupingBy(MapJournalRollFwd::getRollFwdType, toList()));
		
		
		for(ERollFwdType currRollfwd : ERollFwdType.values()) {
			if(journalMap.containsKey(currRollfwd)) {
				for(MapJournalRollFwd jour: journalMap.get(currRollfwd)) {
					MstCalc mstCalc = jour.getMstCalc();
					
					switch (mstCalc.getCalcType()) {
					case BOX:
					case UL_REF:
					case WATERFALL:
//						log.info("qqq : {}", mstCalc.getCalcId());
						if(rstMap.containsKey(mstCalc.getCalcId())) {
							for(IBoxRst box : rstMap.get(mstCalc.getCalcId())) {
								rstList.add(createFromBox(gocId, coa, jour, box, 0.0));		
							}
						}
						break;
					case ROLL_FWD:
						if(calcDetailMap.containsKey(mstCalc.getCalcId())) {
							for(MstCalcRfwdDetail calcRfwd : calcDetailMap.get(mstCalc.getCalcId())) {
//								log.info("aaa : {},{}", mstCalc.getCalcId(), calcRfwd.getCalcSubType());
								rstList.add(createFromRfwd(gocId, coa, jour, calcRfwd, rstList, ratioMap));
							}
						}	
						break;
					}
					
				}
			}
			else if(currRollfwd.isClose()) {
				MstRollFwd mstRollFwd =MstRollFwd.builder().rollFwdType(currRollfwd).build();
				MstCalc mstDeltaSum = PrvdMst.getMstCalcDeltaSum();
				
				MapJournalRollFwd defaultJour = MapJournalRollFwd.builder().mstRollFwd(mstRollFwd).mstCalc(mstDeltaSum).bookingYn(EBoolean.Y).build();
				
				MstCalcRfwdDetail calcRfwd = calcDetailMap.get("CLOSE_STEP").get(0);
				
				rstList.add(createFromRfwd(gocId, coa, defaultJour, calcRfwd, rstList, ratioMap));
			}
		}
		
		List<ERollFwdType> rollFwdList = PrvdMst.getMstRollFwdList().stream().filter(s-> s.getCoaMap().get(coa)).map(MstRollFwd::getRollFwdType).collect(toList());
		
		return rstList.stream().filter(s-> rollFwdList.contains(s.getRollFwdType()));
//		return rstList.stream().peek(s-> log.info("aaa : {},{},{}", s.getGocId(), s.getRollFwdType(), s.toString()));
	}
	
	private static RstCsm createFromBox(String gocId, ECoa coa, MapJournalRollFwd journal, IBoxRst rstBox, double lossRatio) {
		MstCalc mstCalc = journal.getMstCalc();
		
		double closeAmt =0.0;
		double boxValue = rstBox.getBoxValue();
		double rollFwdAmt = PrvdAcct.getCoaValue(coa, journal, boxValue * (1-lossRatio));
		
		if(journal.getRollFwdType().isClose()) {
			closeAmt = rollFwdAmt;
			rollFwdAmt = 0.0;
		}
		return FacRstCsmReins.buildDelta(bssd, gocId, journal, rstBox.getRunsetId(), boxValue, rollFwdAmt, closeAmt, "");
	}

	private static RstCsm createFromRfwd(String gocId, ECoa coa, MapJournalRollFwd journal, MstCalcRfwdDetail calcRfwd, List<RstCsm> rstList, Map<String, Double> ratioMap ){
			double closeAmt=0.0;
			double rollFwdAmt=0.0;
			double boxValue=0.0;
			
			double adj   = calcRfwd.getSignAdjust().getAdj() * journal.getMstCalc().getSignAdjust().getAdj();
			double ratio = calcRfwd.getRatioAdjustYn().isTrueFalse()?  ratioMap.getOrDefault(calcRfwd.getRatioId(), 0.0): 1.0;
			
			switch (calcRfwd.getCalcSubType()) {
			case PREV_RST:
				closeAmt = adj * ratio * createCloseRst(gocId, coa);
//				log.info("close prev : {},{},{},{}", gocId, coa, closeAmt, createCloseRst(gocId, coa));
				break;
			case DELTA_SUM:
				boxValue   = getCurrentDeltaAmt(journal, rstList);
				rollFwdAmt = boxValue;
				closeAmt   = getPriorCloseAmt(journal, rstList) + rollFwdAmt;
				
				log.info("delta sum : {},{},{},{},{},{}", journal.getRollFwdType(), rstList.size(), rollFwdAmt, closeAmt,getPriorCloseAmt(journal, rstList), gocId);
				break;
				
			case PRIOR_CLOSE:
				boxValue   =  ratio* getPriorCloseAmt(journal, rstList) ; 	   
				rollFwdAmt =  adj * boxValue ;
				break;
			default:
				break;
			}
			
//			boxValue   = adj * ratio * boxValue;
//			rollFwdAmt = adj * ratio * rollFwdAmt;
			
//			log.info("aaaa : {},{},{}", journal.getMstRollFwd().getRollFwdType(), journal.getCalcId());
			return FacRstCsmReins.buildDelta(bssd, gocId, journal, "ROLL_FWD", boxValue, rollFwdAmt, closeAmt, "");
	}


	private static double getPriorCloseAmt( MapJournalRollFwd journal, List<RstCsm> rstList ){
		ERollFwdType prevRollFwd = journal.getRollFwdType().getPriorCloseStep();
		return rstList.stream().collect(toMap(RstCsm::getRollFwdType, RstCsm::getCsmAmt, (s,u)->s+u)).getOrDefault(prevRollFwd, 0.0);
	}
	
	
	private static double getCurrentDeltaAmt( MapJournalRollFwd journal, List<RstCsm> rstList ){
		ERollFwdType currRollFwd = journal.getRollFwdType();
		return rstList.stream().filter(s->!s.getRollFwdType().isClose())
							   .filter(s-> s.getRollFwdType().getMyCloseStep().equals(currRollFwd))	
//							   .peek(s->log.info("qqqqqq : {},{},{}", s.getRollFwdType(), s.getBoxAmt(), s.getBoxValue()))
							   .collect(toMap(s-> currRollFwd, RstCsm::getDeltaCsmAmt, (s,u)->s+u))
							   .getOrDefault(currRollFwd, 0.0);
	}
	
	
	private static double createCloseRst(String gocId, ECoa coaId){
		if(prevRfwdRstMap.isEmpty()) {
			prevRfwdRstMap = RstDao.getRollFwdRst(stBssd).stream()
								   .filter(s->s.getRollFwdType().equals(ERollFwdType.CURR_CLOSE))
								   .collect(groupingBy(RstRollFwd::getGocId, toMap(RstRollFwd::getCoaId, RstRollFwd::getCloseAmt, (s,u)->s+u)));
		}
		return prevRfwdRstMap.getOrDefault(gocId, new HashMap<ECoa, Double>()).getOrDefault(coaId, 0.0);
	}

	private static List<RstBoxGoc> getRstBoxMap(String gocId) {
		if(rstBoxMap.isEmpty()) {
			rstBoxMap  = BoxDao.getRstBoxGocGroup(bssd)
//					.filter(s->s.getLiabType().equals(ELiabType.LRC))
					.collect(groupingBy(RstBoxGoc::getGocId, toList()));
		}
		return rstBoxMap.getOrDefault(gocId, new ArrayList<RstBoxGoc>());
	}

	private static Map<String, Double> getRatioMap(String gocId) {
		if(ratioMap.isEmpty()) {
			ratioMap = RatioDao.getRatioLv2(bssd).stream().collect(groupingBy(RatioLv2::getGocId, toMap(RatioLv2::getRatioId, RatioLv2::getReleaseRatio)));
		}
		return ratioMap.getOrDefault(gocId, new HashMap<String, Double>());
	}

}