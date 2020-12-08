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
import com.gof.dao.DfDao;
import com.gof.dao.RatioDao;
import com.gof.dao.RstDao;
import com.gof.entity.DfLv2WghtRate;
import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MstCalc;
import com.gof.entity.MstRollFwd;
import com.gof.entity.MstRunset;
import com.gof.entity.RatioCovUnit;
import com.gof.entity.RstBoxGoc;
import com.gof.entity.RstCsm;
import com.gof.entity.RstLossStep;
import com.gof.enums.ECalcMethod;
import com.gof.enums.ECoa;
import com.gof.enums.ELossStep;
import com.gof.enums.ERollFwdType;
import com.gof.factory.FacRstCsm;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdAcct;
import com.gof.provider.PrvdMst;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MdlRstCsmAlt {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	
	private static	String covUnitCalcType				= GmvConstant.CALC_TYPE_COV_UNIT;			//Default : DEFAULT
	
	private static Map<String, Double> prevCloseMap 	= new HashMap<String, Double>();
	private static Map<String, Double> intRateMap 		= new HashMap<String, Double>(); 
	private static Map<String, Double> covUnitMap 		= new HashMap<String, Double>();

	
	public static Stream<RstCsm> create() {
		List<RstCsm> rstList = new ArrayList<RstCsm>();
		for(String gocId : PrvdMst.getGocIdList()) {
			rstList.addAll(create(gocId).collect(toList()));
		}
		return rstList.stream();
	}
	
	public static Stream<RstCsm> create(String gocId) {
		List<RstCsm> rstList = new ArrayList<RstCsm>();
		Map<ELossStep, Double> lossRatioMap = new HashMap<ELossStep, Double>();

		List<MstRollFwd> rollFwdList = PrvdMst.getMstRollFwdList().stream().filter(s->s.getCsmAplyYn().isTrueFalse()).collect(toList());	
		Map<ERollFwdType, ERollFwdType> priorCloseMap =PrvdMst.getPriorCloseStepMap(rollFwdList);
		
//		rollFwdList.forEach(s->log.info("zzz : {}", s.getRollFwdType()));
//		priorCloseMap.entrySet().forEach(s->log.info("aaa : {},{}", s.getKey(), s.getValue()));
		
		Map<ELossStep, Double> fulfillMap = RstDao.getRstEpvLossStep(bssd, gocId).stream().collect(toMap(RstLossStep::getLossStep, RstLossStep::getFulfillAmt, (s,u)->s+u));
		
		Map<String, List<RstBoxGoc>> rstBoxMap = BoxDao.getRstBoxGoc(bssd,gocId).collect(groupingBy(RstBoxGoc::getCalcId, toList()));
		
		RstCsm priorClose = new RstCsm();
		RstCsm initClose  = new RstCsm();
		
		for(MstRollFwd aa  : rollFwdList) {
			ERollFwdType currRollFwd = aa.getRollFwdType();
			ERollFwdType prevRollFwd = priorCloseMap.get(currRollFwd);
			
			double fulfillAmt 		 = aa.getLossStep()==null? 0.0: fulfillMap.getOrDefault(aa.getLossStep(), 0.0);
			
//			getJournalList(currRollFwd).forEach(s->log.info("aaaz : {},{},{},{}", s.getRollFwdType(), s.getCalcId(), s.getDebitCoa(), s.getCreditCoa()));
			
			for(MapJournalRollFwd journal : getJournalList(currRollFwd)) {
				MstCalc mstCalc = journal.getMstCalc();
				
				double lossRatio = lossRatioMap.getOrDefault(mstCalc.getLossStep(),0.0);
				
				switch (mstCalc.getCalcType()) {
				case BOX:
				case WATERFALL:	
				case REF:	
					rstList.addAll(createFromBox(gocId, journal, rstBoxMap.getOrDefault(journal.getAppliedCalcId(), new ArrayList<RstBoxGoc>()), lossRatio));
					break;
					
				case NATIVE:
					rstList.add(createFromNative(gocId, journal, mstCalc, priorClose, initClose));
					break;
					
				case DELTA_SUM:
					RstCsm tempRst = createCloseStep(gocId, aa, journal.getMstCalc(), rstList, prevRollFwd, fulfillAmt);
					rstList.add(tempRst);
					priorClose = tempRst;
					break;

				default:
					break;
				}
			}
			
			if(getJournalList(currRollFwd).size()==0 && currRollFwd.isClose()) {
				MstCalc mstDelaSum = PrvdMst.getMstCalcDeltaSum();
				RstCsm tempRst = createCloseStep(gocId, aa, mstDelaSum,  rstList, prevRollFwd, fulfillAmt); 
				priorClose = tempRst;
				
//				Loss Ratio put to Map!!!!
				if(aa.getLossStep()!=null ) {
					lossRatioMap.put(aa.getLossStep(), tempRst.getLossRatio());
				}
//				Init Close Set!!!
				if(aa.getRollFwdType().equals(ERollFwdType.INIT_CLOSE)) {
					initClose = tempRst;
				}
				
				rstList.add(tempRst);
			}
		}

		return rstList.stream();
	}
	
	private static List<RstCsm> createFromBox(String gocId, MapJournalRollFwd journal, List<RstBoxGoc> rstBoxList, double lossRatio) {
		List<RstCsm> rstList = new ArrayList<RstCsm>();
		double boxValue  	= 0.0;
		double lossAdjBoxValue = 0.0;
		double coaValue  	= 0.0;
		MstCalc mstCalc 	= journal.getMstCalc();
		int seq 			= journal.getRollFwdType().getOrder();
		String remark 		="FROM BOX";
//		rstBoxList.forEach(s-> log.info("aaa : {}", s.toString()));
		
		Map<MstRunset, Double> runsetRst = rstBoxList.stream().collect(toMap(RstBoxGoc::getMstRunset, RstBoxGoc::getBoxValue, (s,u)->s+u));
		
		for(Map.Entry<MstRunset, Double> entry : runsetRst.entrySet()) {
//			log.info("entry : {},{},{},{}", journal.getRollFwdType(), journal.getCalcId(),entry.getKey(), entry.getValue());
			
			String runsetId     = entry.getKey().getRunsetId();
			boxValue = entry.getValue();
			lossAdjBoxValue = mstCalc.getLossAdjust().applyAsDouble(boxValue,lossRatio);
			
			coaValue = PrvdAcct.getCoaValue(ECoa.CSM, journal, lossAdjBoxValue);
			rstList.add(FacRstCsm.buildDelta(bssd, gocId, journal,  runsetId, lossAdjBoxValue, coaValue, coaValue, remark));
		}
		return rstList;
	}
	
	private static RstCsm createFromNative(String gocId, MapJournalRollFwd journal, MstCalc mstCalc, RstCsm priorClose, RstCsm initClose) {
		double boxValue =0.0;
		double coaValue =0.0;
		double adjCoaValue =0.0;
		double slidingNum = (double)DateUtil.monthBetween(stBssd, bssd);
		double wghtRate =0.0;
		double intFactor =0.0;
//		double closeCsm =0.0;
//		double calcCloseCsm =0.0;
		
		RstCsm temp= new RstCsm();
		String remark 		="FROM Native";
		String runsetId = journal.getMstCalc().getCalcMethod().name();
//		String runsetId = journal.getCalcId();
		
//		log.info("MstCalc : {},{}", mstCalc, mstCalc.getCalcMethod());
			
		switch (mstCalc.getCalcMethod()) {
		case CSM_PREV:
			boxValue = getPrevCloseMap().getOrDefault(gocId, 0.0);
			coaValue = PrvdAcct.getCoaValue(ECoa.CSM, journal, boxValue) ;
			
			adjCoaValue = ECalcMethod.CSM_PREV.getAdjFn().apply(coaValue);
			
			temp 	 = FacRstCsm.buildClose(bssd, gocId, journal.getMstRollFwd(), runsetId, mstCalc, boxValue, 0.0, coaValue, 0.0, adjCoaValue, 0.0, remark);
			break;

		case CSM_INIT: 
			
			double initDeltaAmt = priorClose.getDeltaCalcCsmAmt();
			double initCloseAmt = priorClose.getCalcCsmAmt();
			
			boxValue = csmAdj(initDeltaAmt, initCloseAmt);
			coaValue = PrvdAcct.getCoaValue(ECoa.CSM, journal, boxValue);
			
			temp = FacRstCsm.buildDeltaCsm(bssd, gocId, journal, runsetId, boxValue, coaValue, remark);
			break;
				
		case CSM_INT:
			if(initClose!=null) {
//				boxValue = csmInt(initClose);
				
				Map<String, Double> intRateMap = getIntRateMap();
				
				wghtRate  = intRateMap.getOrDefault(initClose.getGocId(), 0.0);
				intFactor = Math.pow( 1 + wghtRate, slidingNum /12.0) - 1.0;
				
				boxValue 		 = initClose.getCsmAmt() <= 0 ? 0.0: initClose.getCsmAmt() * intFactor ;
				
			}
			coaValue = PrvdAcct.getCoaValue(ECoa.CSM, journal, boxValue);
			temp = FacRstCsm.buildDelta(bssd, gocId, journal, runsetId, boxValue, coaValue,coaValue, remark+"_"+wghtRate);
			break;
				
		case CSM_LOSS_ALLO:
			
			break;
			
		case CSM_REVERSAL:
			double moveDeltaAmt = priorClose.getDeltaCalcCsmAmt();
			double moveCloseAmt = priorClose.getCalcCsmAmt();
			
			log.info("reversal : {},{},{}", journal.getRollFwdType(), mstCalc.getCalcMethod(), moveDeltaAmt, moveCloseAmt);
			
			boxValue = csmAdj(moveDeltaAmt, moveCloseAmt);
			coaValue = PrvdAcct.getCoaValue(ECoa.CSM, journal, boxValue);

			temp = FacRstCsm.buildDeltaCsm(bssd, gocId, journal, runsetId, boxValue, coaValue, remark);
			
			break;
			
//		case CSM_RESERVE:
//			moveDeltaAmt = priorClose.getDeltaCalcCsmAmt();
//			moveCloseAmt = priorClose.getCalcCsmAmt();
//			if(moveDeltaAmt < 0 && moveCloseAmt <0) {
//				boxValue = -1.0* Math.max(moveDeltaAmt, moveCloseAmt);
//			}
//			temp = FacRstCsm.buildDeltaCsm(bssd, gocId, journal, journal.getMstCalc().getCalcMethod().name(), boxValue, remark);
//			break;
			
		case CSM_RELEASE_1:
			boxValue= release(priorClose);
			coaValue = PrvdAcct.getCoaValue(ECoa.CSM, journal, boxValue);
			
			temp = FacRstCsm.buildDelta(bssd, gocId, journal, runsetId, boxValue, coaValue, coaValue, remark);
			break;
		default:
			break;
		}
		
		return temp;
	}

	private static RstCsm createCloseStep(String gocId, MstRollFwd mstRollFwd, MstCalc mstCalc, List<RstCsm> rstList, ERollFwdType prevRollFwd, double fulfillAmt) {

		String runsetId = mstCalc.getCalcId();
		String remark ="FROM CLOSE";
		
		ERollFwdType currRollFwd = mstRollFwd.getRollFwdType();
		
		Map<String, Double> prevDeltaCalcCsm = rstList.stream()
													.filter(s->!s.getRollFwdType().isClose())
													.filter(s->s.getRollFwdType().getOrder() < currRollFwd.getOrder())
													.filter(s->s.getRollFwdType().getOrder() > prevRollFwd.getOrder())
													.collect(toMap(RstCsm::getGocId , RstCsm::getDeltaCalcCsmAmt, (s,u)->s+u));
		
		Map<String, Double> precDeltaCsm = rstList.stream()
													.filter(s->!s.getRollFwdType().isClose())
													.filter(s->s.getRollFwdType().getOrder() < currRollFwd.getOrder())
													.filter(s->s.getRollFwdType().getOrder() > prevRollFwd.getOrder())
													.collect(toMap(RstCsm::getGocId , RstCsm::getDeltaCsmAmt, (s,u)->s+u));
	
		Map<String, Double> prevCalcCsmClosMap = rstList.stream()
													 .filter(s->s.getRollFwdType().isClose())
													 .filter(s->s.getRollFwdType().equals(prevRollFwd))
													 .collect(toMap(RstCsm::getGocId , RstCsm::getCalcCsmAmt, (s,u)->s+u));
		
	
		Map<String, Double> prevCsmClosMap = rstList.stream()
													 .filter(s->s.getRollFwdType().isClose())
													 .filter(s->s.getRollFwdType().equals(prevRollFwd))
													 .collect(toMap(RstCsm::getGocId , RstCsm::getCsmAmt, (s,u)->s+u));
		
		double deltaCalcCsm =  prevDeltaCalcCsm.getOrDefault(gocId, 0.0);
		double deltaCsm 	=  precDeltaCsm.getOrDefault(gocId, 0.0);
		
		double caclCsm  = prevCalcCsmClosMap.getOrDefault(gocId,0.0) + deltaCalcCsm;
		double csmAmt   = prevCsmClosMap.getOrDefault(gocId,0.0) + deltaCsm;
		
		RstCsm tempRst =FacRstCsm.buildClose(bssd, gocId, mstRollFwd, runsetId, mstCalc, 0.0,  deltaCalcCsm, caclCsm, deltaCsm,  csmAmt, fulfillAmt, remark);
		return tempRst;
	}


	private static Map<String, Double> getPrevCloseMap() {
		if(prevCloseMap.isEmpty()) {
			prevCloseMap = RstDao.getRstCsm(stBssd).stream()
										.filter(s->s.getRollFwdType().equals(ERollFwdType.CURR_CLOSE))
										.collect(toMap(RstCsm::getGocId, RstCsm::getCalcCsmAmt))
										;
		}
		return prevCloseMap;
	}
	
//	private static double csmInt(RstCsm initClose) {
//		Map<String, Double> intRateMap = getIntRateMap();
//		
////		log.info("int  :  {},{}", initClose);
//		double intFactor = intRateMap.getOrDefault(initClose.getGocId(), 0.0);
//		double csmInt =  initClose.getCsmAmt() <= 0 ? 0.0: initClose.getCsmAmt() * intFactor ;
//		return csmInt;
//	} 
	
	
	private static double csmAdj(double deltaAmt, double closeAmt) {

		if(deltaAmt >=0 && deltaAmt >=closeAmt) {
			return -1.0* ( deltaAmt - Math.max(closeAmt, 0)); 
		}

		else if(deltaAmt < 0 && closeAmt <0) {
			return -1.0* Math.max(deltaAmt, closeAmt);
		}
		else {
			return 0.0;
		}
		
//		�씠�씡�쑀吏�
//		�씠�씡�쟾�솚 ( �씠�쟾 close <0, �떦湲곕��룞 >0 , �떦湲캽lsoe > 0 ==> delta >0, close >0, delta>close) 
//		if(deltaAmt >=0 && closeAmt > 0 && deltaAmt > closeAmt ) {		
//			return -1.0 * (deltaAmt - closeAmt);
//		}
//		�넀�떎�쑀吏�_異뺤냼 ( �씠�쟾 close <0, �떦湲캽lose <0, �떦湲곕��룞 > 0 ==> close < 0, delta > 0   )
//		else if(deltaAmt >=0 && close < 0) {
//			return -1.0 * deltaAmt;			
//		}
//		
//		�넀�떎�쟾�솚 ( �씠�쟾 close >0, �떦湲곕��룞< 0, �떦湲캽lose <0 ==> delta < 0, close <0 , delta < close)
//		else if (deltaAmt < 0 && closeAmt < 0 && deltaAmt  < closeAmt ) {		
//			return -1.0 * closeAmt;
//		}
//		�넀�떎�쑀吏�_�솗�� ( �씠�쟾 close <0, �떦湲캽lose <0, �떦湲곕��룞 < 0  ==> close < 0, delta <0 )
//		if(deltaAmt >=0 && closeAmt < 0 && deltaAmt > closeAmt ) {		
//			return -1.0 * deltaAmt;
//		}
//		else {
//			return 0.0;
//		}

	}
	private static double release(RstCsm priorClose) {	
		Map<String, Double> covUnitMap = getCovUnitMap();
		
		double covUnit = covUnitMap.getOrDefault(priorClose.getGocId(), 0.03);
		double csmRelease = priorClose.getCalcCsmAmt() > 0 ?  priorClose.getCalcCsmAmt() * covUnit : 0.0;
			
//		return -1.0* csmRelease;
		return csmRelease;
	}
	
	
	private static List<MapJournalRollFwd> getJournalList(ERollFwdType rollFwd) {
		return PrvdMst.getJournalRollFwdList().stream()
							.filter(s->s.hasCoa(ECoa.CSM))
							.filter(s->s.getRollFwdType().equals(rollFwd))
							.collect(toList());
	}
	
	private static Map<String, Double> getIntRateMap() {
		if(intRateMap.isEmpty()) {
			int slidingNum = DateUtil.monthBetween(stBssd, bssd);	
//			intRateMap = DfDao.getDfLv2WghtRateByTenor(slidingNum).stream().collect(toMap(DfLv2WghtRate::getGocId, DfLv2WghtRate::getIntFactor));
			intRateMap = DfDao.getDfLv2WghtRateByTenor(slidingNum).stream().collect(toMap(DfLv2WghtRate::getGocId, DfLv2WghtRate::getWghtRate));
		}
		return intRateMap;
	}
	
	private static Map<String, Double> getCovUnitMap() {
		if(covUnitMap.isEmpty()) {
			covUnitMap = RatioDao.getCovUnit(bssd).stream()
								.filter(s->s.getCalcType().equals(covUnitCalcType))
								.collect(toMap(RatioCovUnit::getGocId, RatioCovUnit::getReleaseRatio));
		}
		return covUnitMap;
	}
}