package com.gof.model;

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
import com.gof.dao.CfDao;
import com.gof.dao.MapDao;
import com.gof.dao.MstDao;
import com.gof.dao.RatioDao;
import com.gof.dao.RawDao;
import com.gof.dao.RstDao;
import com.gof.entity.CfLv1Goc;
import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MstCalc;
import com.gof.entity.MstGoc;
import com.gof.entity.MstRollFwd;
import com.gof.entity.MstRunset;
import com.gof.entity.RatioDac;
import com.gof.entity.RawModifiedRetroRst;
import com.gof.entity.RstBoxGoc;
import com.gof.entity.RstCsm;
import com.gof.entity.RstDac;
import com.gof.enums.ECfType;
import com.gof.enums.ECoa;
import com.gof.enums.EConvType;
import com.gof.enums.ERollFwdType;
import com.gof.enums.ERunsetType;
import com.gof.factory.FacRstCsm;
import com.gof.factory.FacRstDac;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdAcct;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MdlRstDacSplit {
	private static	String bssd				=GmvConstant.BSSD;
	private static	String stBssd			=GmvConstant.ST_BSSD;
	private static	String vBssd			=GmvConstant.V_BSSD;
	private static	String dacCalcType		=GmvConstant.CALC_TYPE_DAC_RATIO;		//TODO :!!!! change to DAC				
//	private static	String dacCfGroup		=GmvConstant.DAC_CF_GROUP;				//Default : G_DAC		
	private static	double defaultDacRatio	=GmvConstant.DEFAULT_DAC_RATIO;		
	
	private static Map<String, Double> dacRatioMap 		= new HashMap<String, Double>();
	
	private static Map<ECoa, Map<String, Double>> prevCloseMap 	= new HashMap<ECoa, Map<String,Double>>();
	private static Map<ERollFwdType, List<MapJournalRollFwd>> dac1JournalMap = new HashMap<ERollFwdType, List<MapJournalRollFwd>>();
	private static Map<ERollFwdType, List<MapJournalRollFwd>> dac2JournalMap = new HashMap<ERollFwdType, List<MapJournalRollFwd>>();
	
	
	public static Stream<RstDac> createConversion() {
		return Stream.concat(convertFvApproach(), convertModifiedRetroApproach());
	}
	
	public static Stream<RstDac> convertModifiedRetroApproach() {
		List<RstDac> rstList = new ArrayList<RstDac>();
		
		MstCalc mstCalc		  		= PrvdMst.getMstCalcDeltaSum();
		String runsetId				= mstCalc.getCalcId();
		MstRollFwd mstRollFwd 		= PrvdMst.getMstRollFwd(ERollFwdType.CURR_CLOSE);
		
		Map<String, RawModifiedRetroRst> convRstMap = RawDao.getRawModifiedRetroRst(stBssd).stream().collect(toMap(RawModifiedRetroRst::getGocId, Function.identity())); 
		
		for(MstGoc goc: PrvdMst.getGocList().stream().filter(s->s.getConvType().equals(EConvType.MODIFIED_RETRO)).collect(toList())) {
			String gocId 	= goc.getGocId();
			
			double dac1Amt  = convRstMap.containsKey(gocId)? convRstMap.get(gocId).getDac1Amt(): 0.0;
			double dac2Amt  = convRstMap.containsKey(gocId)? convRstMap.get(gocId).getDac2Amt(): 0.0;
			
			rstList.add( FacRstDac.build(stBssd, gocId, ECoa.DAC1, mstRollFwd, runsetId, mstCalc, 0.0, 0.0, dac1Amt, "From conversion") );
			rstList.add( FacRstDac.build(stBssd, gocId, ECoa.DAC2, mstRollFwd, runsetId, mstCalc, 0.0, 0.0, dac2Amt, "From conversion") );
		}
		return rstList.stream();
	}
	
	
	public static Stream<RstDac> convertFvApproach() {
		List<RstDac> rstList = new ArrayList<RstDac>();
		
		rstList.addAll(createConvert(ECoa.DAC1, ECfType.DAC1).collect(toList()));
		rstList.addAll(createConvert(ECoa.DAC2, ECfType.DAC2).collect(toList()));
		
		return rstList.stream();
	}
	
	public static Stream<RstDac> create() {
		List<RstDac> rstList = new ArrayList<RstDac>();
		
		for(String gocId : PrvdMst.getGocIdList()) {
			rstList.addAll(create(gocId).collect(toList()));
		}
		
		return rstList.stream(); 
	}
	
	public static Stream<RstDac> create(String gocId) {
		List<RstDac> rstList = new ArrayList<RstDac>();
		
		rstList.addAll(create(gocId, ECoa.DAC1).collect(toList()));
		rstList.addAll(create(gocId, ECoa.DAC2).collect(toList()));
		
		return rstList.stream(); 
	}
	
//	public static Stream<RstDac> createDac1(String gocId) {
//		List<RstDac> rstList = new ArrayList<RstDac>();
//		rstList.addAll(create(gocId, ECoa.DAC1).collect(toList()));
//		return rstList.stream(); 
//	}
//	
//	public static Stream<RstDac> createDac2(String gocId) {
//		List<RstDac> rstList = new ArrayList<RstDac>();
//		rstList.addAll(create(gocId, ECoa.DAC2).collect(toList()));
//		return rstList.stream(); 
//	}
	
	
	public static Stream<RstDac> create(String gocId, ECoa coaId) {
		List<RstDac> rstList = new ArrayList<RstDac>();

		List<MstRollFwd> rollFwdList = PrvdMst.getMstRollFwdList().stream().filter(s-> s.getDacAplyYn().isTrueFalse()).collect(toList());
		
		Map<ERollFwdType, ERollFwdType> priorCloseMap =PrvdMst.getPriorCloseStepMap(rollFwdList);

		Map<String, List<RstBoxGoc>> rstBoxMap = BoxDao.getRstBoxGoc(bssd,gocId).collect(groupingBy(RstBoxGoc::getCalcId, toList()));
			
		RstDac priorClose = new RstDac();
			
		
		
		for(MstRollFwd aa  : rollFwdList) {
			ERollFwdType currRollFwd = aa.getRollFwdType();
			ERollFwdType prevRollFwd = priorCloseMap.get(currRollFwd);
			
			
			for(MapJournalRollFwd journal : getJournalList(currRollFwd, coaId)) {
				MstCalc mstCalc = journal.getMstCalc();
				
//				log.info("zzzz : {},{},{}", journal.getCalcId(), coaId, journal.getRollFwdType());
				switch (mstCalc.getCalcType()) {
				case BOX:
				case WATERFALL:	
				case REF:	
					rstList.addAll(createFromBox(gocId, coaId,journal, rstBoxMap.getOrDefault(journal.getAppliedCalcId(), new ArrayList<RstBoxGoc>())));
					break;
					
				case NATIVE:
					rstList.add(createFromNative(gocId, coaId,journal, mstCalc, priorClose));
					break;
					
				case DELTA_SUM:
					RstDac tempRst = createCloseStep(gocId, coaId, aa, journal.getMstCalc(), rstList, prevRollFwd);
					rstList.add(tempRst);
					priorClose = tempRst;
					break;

				default:
					break;
				}
			}
			
			if(getJournalList(currRollFwd, coaId).size()==0 && currRollFwd.isClose()) {
				MstCalc mstDeltaSum = PrvdMst.getMstCalcDeltaSum();
				RstDac tempRst = createCloseStep(gocId, coaId, aa, mstDeltaSum,  rstList, prevRollFwd); 
				priorClose = tempRst;
				
				rstList.add(tempRst);
			}

		}
//		rstList.forEach(s->log.info("zzz : {},{}", s.getGocId(), s.getRollFwdType()));
		return rstList.stream();
		
	}
	
	
	private static Stream<RstDac> createConvert(ECoa coa, ECfType dacCfType) {
		List<RstDac> rstList = new ArrayList<RstDac>();
		
//		MstRunset mstRunset = PrvdMst.getMstRunsetMap(ECoa.EPV, ERunsetType.CURR_CLOSING);
		
		List<String> currRunsetList = PrvdMst.getMstRunsetList(ECoa.EPV).stream()
											.filter(s->ERunsetType.CURR_CLOSING.equals(s.getRunsetType()))
											.map(MstRunset::getRunsetId).collect(toList());
		
//		Map<String, Double> dacMap =  CfDao.getCfLv1GocByRunsetStream(stBssd, mstRunset.getRunsetId())
		Map<String, Double> dacMap =  CfDao.getCfLv1GocStream(stBssd)
											.filter(s-> currRunsetList.contains(s.getRunsetId()))		
											.filter(s->dacCfType.equals(s.getCfType()))
											.filter(s->s.getGocId().contains("0000"))					//TODO :!!!!!
											.collect(toMap(CfLv1Goc::getGocId, CfLv1Goc::getCfAmt, (s,u)->s+u))
											;
		
		MstRollFwd mstRollFwd 	= PrvdMst.getMstRollFwd(ERollFwdType.CURR_CLOSE);
		
		
		String remark 			= "Conversion";
//		MstCalc	mstCalc 	  	= PrvdMst.getMstCalcDeltaSum();
		MstCalc	mstCalc 	  	= getJournalList(ERollFwdType.CURR_CLOSE, coa).isEmpty()	?	PrvdMst.getMstCalcDeltaSum(): 
											getJournalList(mstRollFwd.getRollFwdType(), coa).get(0).getMstCalc();
		
		for(Map.Entry<String, Double> entry : dacMap.entrySet()) {
			rstList.add( FacRstDac.build(stBssd, entry.getKey(), coa, mstRollFwd, mstCalc.getCalcId(), mstCalc, 0.0, 0.0, entry.getValue(), remark) );
		}
		
		return rstList.stream();
	}

	
//	private static Stream<RstDac> createConversion(ECoa coa, String cfGroup) {
//		List<RstDac> rstList = new ArrayList<RstDac>();
//		
//		List<ECfType> dacCfTypeList = MstDao.getMstCfClass().stream()
//											.filter(s->s.getCfClassId().equals(cfGroup))
//											.map(s->s.getCfTypeList())
//											.flatMap(s-> s.stream())
//											.collect(toList())
//											;
//		
//		Map<String, Double> dacMap =  CfDao.getCfLv1GocByRunsetStream(stBssd, currRunsetId)
//											.filter(s->dacCfTypeList.contains(s.getCfType()))
//											.collect(toMap(CfLv1Goc::getGocId, CfLv1Goc::getCfAmt, (s,u)->s+u))
//											;
//		
//		MstRollFwd mstRollFwd 	= PrvdMst.getMstRollFwd(ERollFwdType.CURR_CLOSE);
//		
//		
//		String remark 			= "Conversion";
////		MstCalc	mstCalc 	  	= PrvdMst.getMstCalcDeltaSum();
//		MstCalc	mstCalc 	  	= getJournalList(ERollFwdType.CURR_CLOSE, coa).isEmpty()	?	PrvdMst.getMstCalcDeltaSum(): 
//											getJournalList(mstRollFwd.getRollFwdType(), coa).get(0).getMstCalc();
//		
//		for(Map.Entry<String, Double> entry : dacMap.entrySet()) {
//			rstList.add( FacRstDac.build(stBssd, entry.getKey(), coa, mstRollFwd, mstCalc.getCalcId(), mstCalc, 0.0, 0.0, entry.getValue(), remark) );
//		}
//		
//		return rstList.stream();
//	}
	private static List<RstDac> createFromBox(String gocId, ECoa coa, MapJournalRollFwd journal, List<RstBoxGoc> rstBoxList) {
		List<RstDac> rstList = new ArrayList<RstDac>();
		double boxValue  	= 0.0;
		double coaValue  	= 0.0;
		String remark 		= "FROM BOX";
		
		MstCalc mstCalc 		= journal.getMstCalc();
		MstRollFwd mstRollFwd 	= journal.getMstRollFwd();
		
		Map<MstRunset, Double> runsetRst = rstBoxList.stream().collect(toMap(RstBoxGoc::getMstRunset, RstBoxGoc::getBoxValue, (s,u)->s+u));
		
		for(Map.Entry<MstRunset, Double> entry : runsetRst.entrySet()) {
//			log.info("entry : {},{},{},{}", journal.getRollFwdType(), journal.getCalcId(),entry.getKey(), entry.getValue());
			
			boxValue = entry.getValue();
			coaValue = PrvdAcct.getCoaValue(coa, journal, boxValue, 0.0);
			
			rstList.add(FacRstDac.buildDelta(bssd, gocId, coa, mstRollFwd, entry.getKey().getRunsetId(), mstCalc, boxValue, coaValue, remark));
		}
		return rstList;
	}
	
	private static RstDac createFromNative(String gocId, ECoa coa, MapJournalRollFwd journal, MstCalc mstCalc, RstDac priorClose) {
		double boxValue =0.0;
		double coaValue =0.0;
		String runsetId = journal.getMstCalc().getCalcMethod().name();
		String remark ="FROM NATIVE";
		
		RstDac temp= new RstDac();
		
		switch (mstCalc.getCalcMethod()) {
		case DAC_PREV:
			boxValue = getPrevCloseMap(coa).getOrDefault(gocId, 0.0);
			coaValue = PrvdAcct.getCoaValue(coa, journal, boxValue, 0.0);
			
//			log.info("qqqq : {},{},{}",gocId, coa, getPrevCloseMap(coa).get(gocId));
			temp = FacRstDac.buildDelta(bssd, gocId, coa, journal.getMstRollFwd(), runsetId, journal.getMstCalc(), boxValue, coaValue, remark);
			break;

		case DAC_RELEASE_1:
			boxValue= release(priorClose);
			coaValue = PrvdAcct.getCoaValue(coa, journal, boxValue, 0.0);
			
			temp = FacRstDac.buildDelta(bssd, gocId, coa, journal.getMstRollFwd(), runsetId, journal.getMstCalc(), boxValue, coaValue,remark);
			break;
		default:
			break;
		}
		
		return temp;
	}
	
	private static RstDac createCloseStep(String gocId, ECoa coa, MstRollFwd mstRollFwd, MstCalc mstCalc, List<RstDac> rstList, ERollFwdType prevRollFwd) {
		String remark ="FROM CLOSE";
		ERollFwdType currRollFwd = mstRollFwd.getRollFwdType();
		
		Map<String, Double> prevDeltaDac = rstList.stream()
												.filter(s->s.getCoaId().equals(coa))
												.filter(s->!s.getRollFwdType().isClose())
												.filter(s->s.getRollFwdType().getOrder() < currRollFwd.getOrder())
												.filter(s->s.getRollFwdType().getOrder() > prevRollFwd.getOrder())
												.collect(toMap(RstDac::getGocId , RstDac::getDeltaDacAmt, (s,u)->s+u));
		
	
		Map<String, Double> prevClosMap = rstList.stream()
												 .filter(s->s.getCoaId().equals(coa))	
												 .filter(s->s.getRollFwdType().isClose())
												 .filter(s->s.getRollFwdType().equals(prevRollFwd))
												 .collect(toMap(RstDac::getGocId , RstDac::getDacAmt, (s,u)->s+u));
		
	
		
		double deltaDacAmt = prevDeltaDac.getOrDefault(gocId, 0.0);
		double closeDacAmt = prevClosMap.getOrDefault(gocId,0.0) + deltaDacAmt;

		RstDac tempRst =FacRstDac.build(bssd, gocId, coa, mstRollFwd, mstCalc.getCalcId(), mstCalc, 0.0, deltaDacAmt, closeDacAmt, remark);
		return tempRst;
	}
	
	private static Map<String, Double> getPrevCloseMap(ECoa coa) {
		if(prevCloseMap.isEmpty()) {
			prevCloseMap = RstDao.getRstDac(stBssd).stream()
//									    .filter(s->s.getCoaId().equals(coa))
										.filter(s->s.getRollFwdType().equals(ERollFwdType.CURR_CLOSE))
										.collect(groupingBy(RstDac::getCoaId, toMap(RstDac::getGocId, RstDac::getDacAmt)))
										;
		}
		return prevCloseMap.getOrDefault(coa, new HashMap<String, Double>());
	}
	
	private static double release(RstDac priorClose) {	

		double covUnit 	  = getDacRatioMap().getOrDefault(priorClose.getGocId(), defaultDacRatio);
		double releaseAmt = priorClose.getDacAmt() > 0 ?  priorClose.getDacAmt() * covUnit : 0.0;
			
		return releaseAmt;
	}
	
	private static Map<String, Double> getDacRatioMap() {
		if(dacRatioMap.isEmpty()) {
			dacRatioMap = RatioDao.getRatioDac(bssd).stream()
								.filter(s->s.getCalcType().equals(dacCalcType))
								.collect(toMap(RatioDac::getGocId, RatioDac::getReleaseRatio));
		}
		return dacRatioMap;
	}
	
	private static List<MapJournalRollFwd> getJournalList(ERollFwdType rollFwd, ECoa coa) {	
		if(dac1JournalMap.isEmpty()) {
			dac1JournalMap =MapDao.getMapJournalRollFwd().stream()
								.filter(s->s.getUseYn().isTrueFalse())
								.filter(s->s.hasCoa(ECoa.DAC1))
								.collect(groupingBy(MapJournalRollFwd::getRollFwdType,toList()));
		}
		
		if(dac2JournalMap.isEmpty()) {
			dac2JournalMap =MapDao.getMapJournalRollFwd().stream()
								.filter(s->s.getUseYn().isTrueFalse())
								.filter(s->s.hasCoa(ECoa.DAC2))
								.collect(groupingBy(MapJournalRollFwd::getRollFwdType,toList()));
								
		}
		
		if(coa.equals(ECoa.DAC2)) {
			return dac2JournalMap.getOrDefault(rollFwd, new ArrayList<MapJournalRollFwd>());
		}
		else {
			return dac1JournalMap.getOrDefault(rollFwd, new ArrayList<MapJournalRollFwd>());
		}
	}
	
//	private static List<MapJournalRollFwd> getJournalList(ERollFwdType rollFwd, ECoa coa) {	
//		Map<ERollFwdType,List<MapJournalRollFwd>> tempMap =  MapDao.getMapJournalRollFwd().stream()
//								.filter(s->s.getUseYn().isTrueFalse())
//								.filter(s->s.hasCoa(coa))
//								.collect(groupingBy(MapJournalRollFwd::getRollFwdType,toList()));
//								;
//								
//		return tempMap.getOrDefault(rollFwd, new ArrayList<MapJournalRollFwd>());
//	}
}