package com.gof.rollfwd;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao.BoxDao;
import com.gof.dao.MstDao;
import com.gof.dao.RatioDao;
import com.gof.dao.RstDao;
import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MstCalc;
import com.gof.entity.MstCalcRfwdDetail;
import com.gof.entity.MstRollFwd;
import com.gof.entity.RatioLv2;
import com.gof.entity.RstBoxGoc;
import com.gof.entity.RstCsm;
import com.gof.entity.RstRollFwd;
import com.gof.enums.ECoa;
import com.gof.enums.ELossStep;
import com.gof.enums.ERollFwdType;
import com.gof.factory.FacRollFwd;
import com.gof.factory.FacRstCsmReins;
import com.gof.infra.GmvConstant;
import com.gof.interfaces.IBoxRst;
import com.gof.provider.PrvdAcct;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MdlRollFwd {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	
	private static Map<String, List<RstBoxGoc>> rstBoxMap 	= new HashMap<String, List<RstBoxGoc>>();
	private static List<RstCsm> rstCsmList 					= new ArrayList<RstCsm>();
	private static Map<String, List<RstCsm>> rstCsmMap 		= new HashMap<String, List<RstCsm>>();
	
	private static Map<String, Map<String, Double>> ratioMap 	= new HashMap<String, Map<String,Double>>();
	private static Map<String, Map<ECoa, Double>> prevRfwdRstMap 	= new HashMap<String, Map<ECoa,Double>>();
	private static List<MstCalcRfwdDetail> mstCalcRfwdDetailList = new ArrayList<MstCalcRfwdDetail>();
	
	public static Stream<RstRollFwd> createCsmConversion(){
		bssd=stBssd;
		return createCsm();
	}
	
	public static Stream<RstRollFwd> createConversion(){
		bssd=stBssd;
		return create();
	}
	public static Stream<RstRollFwd> createPrev(String gocId){
		return RstDao.getRollFwdRst(stBssd).stream()
					 .filter(s->s.getGocId().equals(gocId))
					 .filter(s->s.getRollFwdType().equals(ERollFwdType.CURR_CLOSE))
					 .map(s-> FacRollFwd.convertFromPrev(bssd, s))
					 ;
	}
	
	public static Stream<RstRollFwd> createPrev(){
		return RstDao.getRollFwdRst(stBssd).stream()
					 .filter(s->s.getRollFwdType().equals(ERollFwdType.CURR_CLOSE))
					 .map(s-> FacRollFwd.convertFromPrev(bssd, s))
					 ;
	}
	
	public static Stream<RstRollFwd> createCsm(){
		return PrvdMst.getGocIdList().stream().flatMap(gocId -> createCsm(gocId));
	}

	public static Stream<RstRollFwd> createCsm(String gocId){
	//		List<RstRollFwd> rstList = new ArrayList<RstRollFwd>();
	//		
	//		List<RstCsm> csmList = getRstCsmList().stream().filter(s->s.getGocId().equals(gocId)).collect(toList());
	//		for(RstCsm rstCsm : csmList) {
	////			log.info("aaaa : {},{},{}", rstCsm.toString());
	//			rstList.add(FacRollFwd.buildFromCsm(bssd, gocId, rstCsm));
	//		}
	//		return rstList.stream();
			
			return getRstCsmList().stream().filter(s->s.getGocId().equals(gocId)).map(rstCsm -> FacRollFwd.buildFromCsm(bssd, gocId, rstCsm));
		}

	public static Stream<RstRollFwd> create(){
//		List<RstRollFwd> rstList = new ArrayList<RstRollFwd>();
//		for(String gocId :PrvdMst.getGocIdList()) {
//			rstList.addAll(create(gocId).collect(toList()));
//		}
//		return rstList.stream();
		return PrvdMst.getGocIdList().stream().flatMap(gocId -> create(gocId));
	}
	
	public static Stream<RstRollFwd> create(String gocId){
	//		List<RstRollFwd> rstList = new ArrayList<RstRollFwd>();
	////		CSM,DAC excluded!!!!
	//		for(ECoa coa : ECoa.getCoaList()) {
	//			rstList.addAll(createByCoa(gocId, coa).collect(toList()));
	////			if(coa.equals(ECoa.IREV)) {
	////				
	////			}
	//		}
	//		return rstList.stream();
			return ECoa.getCoaList().stream()
//					.filter(s-> s.equals(ECoa.ICOST))
					.flatMap(coa-> createByCoa(gocId, coa));
		}

	public static Stream<RstRollFwd> createByCoa(String gocId, ECoa coa){
		List<RstRollFwd> rstList = new ArrayList<RstRollFwd>();
		
		Map<String, List<IBoxRst>> rstMap = getRstMapByCalcId(gocId);			//Key : calcId, values : List of runset's value;
		Map<String, Double> ratioMap 	  = getRatioMap(gocId);
		
		Map<String, List<MstCalcRfwdDetail>> calcDetailMap = getRfwdDetailList().stream().collect(groupingBy(MstCalcRfwdDetail::getCalcId , toList()));

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
					case ROLL_FWD:	
//						log.info("qqq : {}", mstCalc.getCalcId());
						if(rstMap.containsKey(mstCalc.getCalcId())) {
							for(IBoxRst box : rstMap.get(mstCalc.getCalcId())) {
								
//								if(mstCalc.getCalcId().equals("BOX_LIC_NEST_ACTU_I0")&&(coa.equals(ECoa.IREV))){
//										log.info("aaa : {}", mstCalc.getCalcId() );
//										};
								rstList.add(createFromBox(gocId, coa, jour, box, 0.0));		
							}
							break;
						}
//					case ROLL_FWD:
//						log.info("aaa : {}", mstCalc.getCalcId());
						if(calcDetailMap.containsKey(mstCalc.getCalcId())) {
							for(MstCalcRfwdDetail calcRfwd : calcDetailMap.get(mstCalc.getCalcId())) {
								rstList.add(createFromRfwd(gocId, coa, jour, calcRfwd, rstList, ratioMap));
							}
						}	
						break;
					default:
						break;
					}
				}
			}
			else if(currRollfwd.isClose()) {
				MstRollFwd mstRollFwd =MstRollFwd.builder().rollFwdType(currRollfwd).build();
				MstCalc mstDeltaSum = PrvdMst.getMstCalcDeltaSum();
				MapJournalRollFwd defaultJour = MapJournalRollFwd.builder().mstRollFwd(mstRollFwd).mstCalc(mstDeltaSum).build();
				MstCalcRfwdDetail calcRfwd = calcDetailMap.get("CLOSE_STEP").get(0);
				
				rstList.add(createFromRfwd(gocId, coa, defaultJour, calcRfwd, rstList, ratioMap));
			}
		}
		
		List<ERollFwdType> rollFwdList = PrvdMst.getMstRollFwdList().stream().filter(s-> s.getCoaMap().get(coa)).map(MstRollFwd::getRollFwdType).collect(toList());
		
		return rstList.stream().filter(s-> rollFwdList.contains(s.getRollFwdType()));
	}
	
	
	private static RstRollFwd createFromBox(String gocId, ECoa coa, MapJournalRollFwd journal, IBoxRst rstBox, double lossRatio) {
		MstCalc mstCalc = journal.getMstCalc();
		
		double closeAmt =0.0;
		double boxValue = rstBox.getBoxValue();
		double rollFwdAmt = PrvdAcct.getCoaValue(coa, journal, boxValue * (1-lossRatio));

//		log.info("zzzzzz : {},{},{},{}, {},{}", gocId, coa, rstBox.getBoxValue(), rstBox.getCalcId(), rollFwdAmt, journal.getCreditCoa());
		if(journal.getRollFwdType().isClose()) {
			closeAmt = rollFwdAmt;
			rollFwdAmt = 0.0;
		}
		
		return FacRollFwd.build(bssd, gocId, coa, journal.getRollFwdType(), rstBox.getRunsetId(), mstCalc, 1,  boxValue, rollFwdAmt, closeAmt,  "");
	}


	private static RstRollFwd createFromRfwd(String gocId, ECoa coa, MapJournalRollFwd journal, MstCalcRfwdDetail calcRfwd, List<RstRollFwd> rstList, Map<String, Double> ratioMap ){
			double closeAmt=0.0;
			double rollFwdAmt=0.0;
			double boxValue=0.0;
			
//			double adj   = calcRfwd.getSignAdjust().getAdj();
//			double ratio = ratioMap.getOrDefault(calcRfwd.getRatioId(), 1.0);
//			
//			switch (calcRfwd.getCalcSubType()) {
//			case PREV_RST:
//				closeAmt = createCloseRst(gocId, coa);
//				break;
//			case DELTA_SUM:
//				rollFwdAmt = getCurrentDeltaAmt(journal, rstList);
//				closeAmt   = getPriorCloseAmt(journal, rstList) + rollFwdAmt;
//				break;
//				
//			case PRIOR_CLOSE:
//				rollFwdAmt =  getPriorCloseAmt(journal, rstList) ;
//				boxValue   = rollFwdAmt; 	   
//			}
//			boxValue   = adj * ratio * boxValue;
//			rollFwdAmt = adj * ratio * rollFwdAmt;
//			closeAmt   = adj * ratio * closeAmt;
			
			double adj   = calcRfwd.getSignAdjust().getAdj() * journal.getMstCalc().getSignAdjust().getAdj();
			double ratio = calcRfwd.getRatioAdjustYn().isTrueFalse()?  ratioMap.getOrDefault(calcRfwd.getRatioId(), 0.0): 1.0;
			
			switch (calcRfwd.getCalcSubType()) {
			case PREV_RST:
				closeAmt = adj * ratio * createCloseRst(gocId, coa);
				break;
			case DELTA_SUM:
				boxValue   = getCurrentDeltaAmt(journal, rstList);
				rollFwdAmt = boxValue;
				closeAmt   = getPriorCloseAmt(journal, rstList) + rollFwdAmt;
				
//				log.info("delta sum : {},{},{},{},{},{}", journal.getRollFwdType(), rstList.size(), rollFwdAmt, closeAmt,getPriorCloseAmt(journal, rstList), gocId);
				break;
				
			case PRIOR_CLOSE:
				boxValue   =  adj * ratio* getPriorCloseAmt(journal, rstList) ; 	   
				rollFwdAmt =  boxValue ;
				break;
			default:
				break;
			}
			return FacRollFwd.build(bssd, gocId, coa, journal.getRollFwdType(), "ROLL_FWD", journal.getMstCalc(), 1,  boxValue, rollFwdAmt, closeAmt,  "");
		}


	private static double getPriorCloseAmt( MapJournalRollFwd journal, List<RstRollFwd> rstList ){
		ERollFwdType prevRollFwd = journal.getRollFwdType().getPriorCloseStep();
		return rstList.stream().collect(toMap(RstRollFwd::getRollFwdType, RstRollFwd::getCloseAmt, (s,u)->s+u)).getOrDefault(prevRollFwd, 0.0);
	}
	
	private static double getCurrentDeltaAmt( MapJournalRollFwd journal, List<RstRollFwd> rstList ){
		ERollFwdType currRollFwd = journal.getRollFwdType();
		return rstList.stream().filter(s->!s.getRollFwdType().isClose())
							   .filter(s-> s.getRollFwdType().getMyCloseStep().equals(currRollFwd))	
							   .collect(toMap(s-> currRollFwd, RstRollFwd::getDeltaAmt, (s,u)->s+u))
							   .getOrDefault(currRollFwd, 0.0);
	}
	
	private static double createCloseRst(String gocId, ECoa coaId){
		if(prevRfwdRstMap.isEmpty()) {
			prevRfwdRstMap = RstDao.getRollFwdRst(stBssd).stream()
								   .filter(s->s.getRollFwdType().equals(ERollFwdType.CURR_CLOSE))
								   .collect(groupingBy(RstRollFwd::getGocId, toMap(RstRollFwd::getCoaId, RstRollFwd::getCloseAmt, (s,u)->s+u)));
		}
		return prevRfwdRstMap.getOrDefault(gocId, new HashMap<ECoa, Double>()).getOrDefault(coaId, 0.0);
		
		
//		Map<ERollFwdType, Double> prevRfwd =  RstDao.getRollFwdRst(stBssd).stream()
//		.filter(s->s.getGocId().equals(gocId))
//		.filter(s->s.getCoaId().equals(coa))
//		.collect(toMap(RstRollFwd::getRollFwdType, RstRollFwd::getCloseAmt, (s,u)->s+u))
//		;
	}

	private static Map<String, List<IBoxRst>> getRstMapByCalcId(String gocId) {
		Map<String, List<IBoxRst>> rstMap = getRstBoxMap(gocId).stream().collect(groupingBy(RstBoxGoc::getCalcId, toList()));
		Map<String, List<IBoxRst>> csmMap = getRstCsmMap(gocId).stream().collect(groupingBy(RstCsm::getCalcId, toList()));
		
		for(Map.Entry<String, List<IBoxRst>> entry: csmMap.entrySet()) {
			if(!rstMap.containsKey(entry.getKey())) {
				rstMap.put(entry.getKey(), entry.getValue());
			}
		}
		return rstMap;
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


	private static List<RstCsm> getRstCsmMap(String gocId) {
		if(rstCsmMap.isEmpty()) {
			rstCsmMap  = RstDao.getRstCsm(bssd).stream().filter(s->!s.getCalcId().equals("CLOSE_STEP")).collect(groupingBy(RstCsm::getGocId, toList()));
		}
		return rstCsmMap.getOrDefault(gocId, new ArrayList<RstCsm>());
	}
	
	private static List<RstCsm> getRstCsmList() {
		if(rstCsmList.isEmpty()) {
			rstCsmList  = RstDao.getRstCsm(bssd);
		}
		return rstCsmList;
	}
	private static List<MstCalcRfwdDetail> getRfwdDetailList() {
		if(mstCalcRfwdDetailList.isEmpty()) {
			mstCalcRfwdDetailList  =  MstDao.getMstCalcRfwdDetail();
		}
		return mstCalcRfwdDetailList;
	}
	
	
}