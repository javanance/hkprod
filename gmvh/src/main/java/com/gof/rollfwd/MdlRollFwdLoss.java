package com.gof.rollfwd;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.gof.dao.BoxDao;
import com.gof.dao.RstDao;
import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MstCalc;
import com.gof.entity.MstRollFwd;
import com.gof.entity.RstBoxGoc;
import com.gof.entity.RstCsm;
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
public class MdlRollFwdLoss {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	
	private static Map<String, List<RstRollFwd>> rstRollFwdMap 	 = new HashMap<String, List<RstRollFwd>>();
	private static Map<String, List<RstBoxGoc>> rstBoxMap 		 = new HashMap<String, List<RstBoxGoc>>();
	
	private static Map<String, List<RstCsm>> lossRatioMap 		 = new HashMap<String, List<RstCsm>>();
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
//			if(coa.equals(ECoa.LOSS_RA)) {
				rstList.addAll(createByCoa(gocId, coa).collect(toList()));
//			}
		}
		return rstList.stream();
	}
	
	public static Stream<RstRollFwdLoss> createByCoa(String gocId, ECoa coa){
		List<RstRollFwdLoss> rstList = new ArrayList<RstRollFwdLoss>();
		
		log.info("aaaa : {}", coa);
		List<MstRollFwd> rollFwdList = PrvdMst.getMstRollFwdList().stream().filter(s-> s.getCoaMap().get(coa)).collect(toList());
		
		Map<ERollFwdType, ERollFwdType> priorCloseMap = PrvdMst.getPriorCloseStepMap(rollFwdList);

		Map<ERollFwdType, List<MapJournalRollFwd>> journalMap = PrvdMst.getJournalRollFwdList().stream()
																		.filter(s->s.hasCoa(coa))
																		.collect(groupingBy(MapJournalRollFwd::getRollFwdType, toList()));
		
		Map<String, List<IBoxRst>> rstMap 	  = getRstBoxMap(gocId).stream().collect(groupingBy(RstBoxGoc::getCalcId, toList()));
		Map<String, List<IBoxRst>> rollFwdMap = getRstRollFwd(gocId).stream().collect(groupingBy(RstRollFwd::getCalcId, toList()));

		for(Map.Entry<String, List<IBoxRst>> entry: rollFwdMap.entrySet()) {
			if(!rstMap.containsKey(entry.getKey())) {
				rstMap.put(entry.getKey(), entry.getValue());
			}
		}
		
//		rstMap.entrySet().stream().filter(s->s.getKey().equals("RA_RELEASE")).forEach(s-> log.info("ccc : {},{},{},{}", coa, s.getKey(), s.getValue()));
		
		Map<ELossStep, Double> lossMap= getLossRatioMap(gocId).stream().collect(toMap(RstCsm::getLossStep, RstCsm::getAppliedLossRatio, (s,u)-> Math.max(s, u)));
		MstCalc deltaSumCalc = PrvdMst.getMstCalcDeltaSum();
		
		for(MstRollFwd rollFwd  : rollFwdList) {
			ERollFwdType currRollFwd = rollFwd.getRollFwdType();
			ERollFwdType prevRollFwd = priorCloseMap.get(currRollFwd);
//			ERollFwdType prevRollFwd = priorCloseMap.getOrDefault(currRollFwd, ERollFwdType.PREV_CLOSE);
			
//			double deltaAmt = getDetlaAmt(gocId, currRollFwd, prevRollFwd, rstList);
			
			if(journalMap.containsKey(currRollFwd)) {
				for(MapJournalRollFwd journal : journalMap.get(currRollFwd)) {	
					MstCalc mstCalc = journal.getMstCalc();
//					log.info("qqqq :  {},{},{}", mstCalc.getAppliedCalcId(), rstMap.containsKey(mstCalc.getAppliedCalcId()));
					
					double lossRatio = lossMap.getOrDefault(mstCalc.getLossStep(), 0.0);
	
					switch (mstCalc.getCalcType()) {
					case BOX:
					case WATERFALL:	
					case REF:	
					case NATIVE:
						if(rstMap.containsKey(mstCalc.getAppliedCalcId())) {
							log.info("calcId : {},{}", mstCalc.getCalcId(), mstCalc.getAppliedCalcId());
							for(IBoxRst boxRst : rstMap.get(mstCalc.getAppliedCalcId())) {
								rstList.add(createFromRollFwd(gocId, coa, journal, boxRst, lossRatio));		
							}
						}
						
//						else if(rstMap.containsKey(mstCalc.getCalcId())){
//							for(IBoxRst boxRst : rstMap.get(mstCalc.getAppliedCalcId())) {
//								rstList.add(createFromRollFwd(gocId, coa, journal, boxRst, lossRatio));		
//							}
//						}
						else if(mstCalc.getCalcType().equals(ECalcType.NATIVE) && !mstCalc.getCalcMethod().getUpdateYn()) {
							rstList.add(createFromNative(gocId, coa, journal));
						}
						
						else if(!mstCalc.getCalcType().equals(ECalcType.NATIVE)){
							log.error("There are no result in RST_BOX or RST_ROLL_FWD for {}", mstCalc.getAppliedCalcId());
//							System.exit(1);
						}
						break;
						
//					case NATIVE:
//						rstList.add(createFromNative(gocId, journal, mstCalc, priorClose, initClose));
//						break;
						
					case DELTA_SUM:
						RstRollFwdLoss tempRst = createCloseStep(gocId, coa, rollFwd, rstList, mstCalc,  prevRollFwd);
						rstList.add(tempRst);
						break;
	
					default:
						break;
					}
				}
			}
			
			else if(currRollFwd.isClose()) {
//				log.info("ggg :  {},{},{}", currRollFwd, prevRollFwd );
				RstRollFwdLoss tempRst = createCloseStep(gocId, coa, rollFwd, rstList, deltaSumCalc, prevRollFwd); 
				rstList.add(tempRst);
			}
		}
		
		Map<ERollFwdType, Double> colseRstMap = rstList.stream().collect(toMap(RstRollFwdLoss::getRollFwdType, RstRollFwdLoss::getCloseAmt, (s,u)->s+u));
		Map<ERollFwdType, Double> deltaRstMap = rstList.stream().collect(toMap(RstRollFwdLoss::getRollFwdType, RstRollFwdLoss::getDeltaAmt, (s,u)->s+u));
		
		Optional<MapJournalRollFwd> tempMapJournal = PrvdMst.getJournalRollFwdList().stream()
															.filter(s-> s.hasCoa(coa))
															.filter(s-> s.getMstCalc().getCalcMethod()!=null)
															.filter(s-> s.getMstCalc().getCalcMethod().getUpdateYn())
															.findFirst();
		
//		rstList.forEach(s-> log.info("aaa : {},{},{},{},{},{},{}", s.getRollFwdType(), s.getCalcId(), s.getRunsetId(), s.getCoaId(), s.getDeltaAmt(), s.getCloseAmt()));
		
//		For Native Method to UpdateYn !!! (CURR LOSS ALLOCATION & LOSS REVERSAL )
		if(tempMapJournal.isPresent()) {
			MapJournalRollFwd journal = tempMapJournal.get();
			String runsetId = journal.getCalcId();
			String remark 	= "UPDATED BY " + journal.getMstCalc().getCalcMethod();
			
			double targetClose 	= deltaRstMap.getOrDefault(ERollFwdType.CURR_LOSS_ALLO, 0.0);
			double priorClose 	= colseRstMap.getOrDefault(ERollFwdType.LOSS_RELEASE_CLOSE, 0.0);
			
			double deltaAmt 	= targetClose - priorClose;
//			double boxAmt 		= coa.getSign() * deltaAmt; 
//			double boxAmt 		= journal.getMstCalc().getSignAdjust().getAdj() * coa.getSign() * deltaAmt; 
			double boxAmt 		= PrvdAcct.getCoaValue(coa, journal, deltaAmt);
			
			rstList.add(FacRollFwdLoss.buildDelta(bssd, gocId, coa, journal, runsetId, boxAmt, deltaAmt, remark));
//			log.info("Loss add :  {}, {}, {},{}, {}, {}", gocId, coa, journal.getRollFwdType(), targetClose, priorClose, deltaAmt);
			
			rstList.removeIf(s->s.getRollFwdType().equals(ERollFwdType.LOSS_REVERSAL_CLOSE));
			rstList.add(FacRollFwdLoss.buildClose(bssd, gocId, coa, ERollFwdType.LOSS_REVERSAL_CLOSE, deltaSumCalc, 0.0, deltaAmt, targetClose, remark));

			rstList.removeIf(s->s.getRollFwdType().equals(ERollFwdType.CURR_CLOSE));
			rstList.add(FacRollFwdLoss.buildClose(bssd, gocId, coa, ERollFwdType.CURR_CLOSE, deltaSumCalc, 0.0, 0.0, targetClose, remark));
			
//			log.info("Loss add :  {}, {}, {},{}, {}, {}", gocId, coa, ERollFwdType.LOSS_REVERSAL_CLOSE, targetClose, priorClose, deltaAmt);
		}
		
		return rstList.stream();
	}
	
	private static RstRollFwdLoss createFromRollFwd(String gocId, ECoa coa, MapJournalRollFwd journal, IBoxRst rstBox, double lossRatio) {
		String remark 			= "FROM ROLLFWD";

		MstCalc mstCalc 		= journal.getMstCalc();
		double boxValue 		= rstBox.getBoxValue();											//TODO !!!!!
		double lossAdjBoxValue 	= mstCalc.getLossAdjust().applyAsDouble(boxValue,lossRatio);
		double coaValue 		= PrvdAcct.getCoaValue(coa, journal, lossAdjBoxValue);
		
		log.info("mstRollfwd1 : {},{},{},{},{},{}", rstBox.getRunsetId(),journal.getRollFwdType(),  boxValue, lossRatio, coaValue, mstCalc, mstCalc.getLossAdjust());
		
		return FacRollFwdLoss.buildDelta(bssd, gocId, coa, journal, rstBox.getRunsetId(), lossAdjBoxValue, coaValue, remark);
	}

	private static RstRollFwdLoss createCloseStep(String gocId, ECoa coa, MstRollFwd mstRollFwd, List<RstRollFwdLoss> rstList, MstCalc mstCalc, ERollFwdType prevRollFwd) {
		String remark ="CLOSE_STEP";
		ERollFwdType currRollFwd = mstRollFwd.getRollFwdType();
		
//		log.info("RstRlowFwdLoss : {},{},{}",rstList.size(), currRollFwd, prevRollFwd);

		Map<String, Double> prevMap = rstList.stream()
//										.filter(s->!s.getRollFwdType().isClose())
										.filter(s->s.getRollFwdType().getOrder() <= currRollFwd.getOrder())
										.filter(s->s.getRollFwdType().getOrder() > prevRollFwd.getOrder())
										.collect(toMap(RstRollFwdLoss::getGocId , RstRollFwdLoss::getDeltaAmt, (s,u)->s+u));
	
		Map<String, Double> prevClosMap = rstList.stream()
										 .filter(s->s.getRollFwdType().isClose())
										 .filter(s->s.getRollFwdType().equals(prevRollFwd))
										 .collect(toMap(RstRollFwdLoss::getGocId , RstRollFwdLoss::getCloseAmt, (s,u)->s+u));
		
		double deltaAmt = prevMap.getOrDefault(gocId, 0.0) ;
		double closeAmt = prevClosMap.getOrDefault(gocId,0.0) + deltaAmt;
		
		RstRollFwdLoss tempRst =FacRollFwdLoss.buildClose(bssd, gocId,coa,  mstRollFwd.getRollFwdType(), mstCalc, 0.0, deltaAmt, closeAmt, remark);
		return tempRst;
	}
	
	private static List<RstRollFwd> getRstRollFwd(String gocId) {
		if(rstRollFwdMap.isEmpty()) {
			rstRollFwdMap  = RstDao.getRollFwdRst(bssd).stream()
					.filter(s->!s.getCoaId().equals(ECoa.IREV))		//TODO !!!!!
					.filter(s->!s.getCoaId().equals(ECoa.ICOST))
					.filter(s->!s.getCoaId().equals(ECoa.FCOST))
					.collect(groupingBy(RstRollFwd::getGocId, toList()));
		}
		return rstRollFwdMap.getOrDefault(gocId, new ArrayList<RstRollFwd>());
	}
	
	private static List<RstCsm> getLossRatioMap(String gocId) {
		if(lossRatioMap.isEmpty()) {
			lossRatioMap  = RstDao.getRstCsm(bssd).stream()
								  .filter(s->s.getLossStep()!=null)
								  .collect(groupingBy(RstCsm::getGocId, toList()));
		}
		return lossRatioMap.getOrDefault(gocId, new ArrayList<RstCsm>());
	}

	private static List<RstBoxGoc> getRstBoxMap(String gocId) {
		if(rstBoxMap.isEmpty()) {
			rstBoxMap  = BoxDao.getRstBoxGocGroup(bssd).collect(groupingBy(RstBoxGoc::getGocId, toList()));
		}
		return rstBoxMap.getOrDefault(gocId, new ArrayList<RstBoxGoc>());
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
	
	private static RstRollFwdLoss createFromNative(String gocId, ECoa coa, MapJournalRollFwd journal) {
		RstRollFwdLoss temp= new RstRollFwdLoss();

		double boxValue = 0.0;
		double coaValue = 0.0;
		String remark 	= "FROM Native";
		
		MstCalc mstCalc = journal.getMstCalc();
		String runsetId = journal.getMstCalc().getCalcMethod().name();
		
//		log.info("MstCalc : {},{}", mstCalc, mstCalc.getCalcMethod());
			
		switch (mstCalc.getCalcMethod()) {
		case LOSS_PREV_ALLO:
//			boxValue = getPrevCloseMap().getOrDefault(gocId, 0.0);
			boxValue = getPrevLossAllocationMap(gocId).getOrDefault(ECoa.LOSS, 0.0);
			coaValue = PrvdAcct.getCoaValue(coa, journal, boxValue);
			
			temp 	 = FacRollFwdLoss.buildDelta(bssd, gocId, coa, journal, runsetId, boxValue, coaValue, remark);
			break;
			
		case LOSS_FACE_PREV_ALLO:
			boxValue = getPrevLossAllocationMap(gocId).getOrDefault(ECoa.LOSS_FACE,0.0);
			coaValue = PrvdAcct.getCoaValue(coa, journal, boxValue);
			
			temp 	 = FacRollFwdLoss.buildDelta(bssd, gocId, coa, journal, runsetId, boxValue, coaValue, remark);
			break;
			
		case LOSS_TVOM_PREV_ALLO:
			boxValue = getPrevLossAllocationMap(gocId).getOrDefault(ECoa.LOSS_TVOM, 0.0);
			coaValue = PrvdAcct.getCoaValue(coa, journal, boxValue);
			
			temp 	 = FacRollFwdLoss.buildDelta(bssd, gocId, coa, journal, runsetId, boxValue, coaValue, remark);
			break;
			
		case LOSS_RA_PREV_ALLO:
			boxValue = getPrevLossAllocationMap(gocId).getOrDefault(ECoa.LOSS_RA, 0.0);
			coaValue = PrvdAcct.getCoaValue(coa, journal, boxValue);
			
			temp 	 = FacRollFwdLoss.buildDelta(bssd, gocId, coa, journal, runsetId, boxValue, coaValue, remark);
			break;
				
		case CURR_LOSS_CLOSE:
//			 boxValue = Math.max(0.0, -1.0* lossRatioMap.get(gocId).stream().filter(s->s.getLossStep().equals("L5")).map(s->s.getCalcCsmAmt()).reduce(0.0, (s,u)->s+u));
//			 coaValue = PrvdAcct.getCoaValue(coa, journal, boxValue);
//			 
//			 temp 	 = FacRollFwdLoss.buildDelta(bssd, gocId, coa, journal, runsetId, boxValue, coaValue, remark);
			break;
		default:
			break;
		}
		
		return temp;
	}
}