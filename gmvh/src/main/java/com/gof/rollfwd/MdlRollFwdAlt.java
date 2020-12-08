package com.gof.rollfwd;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.dao.BoxDao;
import com.gof.dao.RstDao;
import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MstCalc;
import com.gof.entity.MstRollFwd;
import com.gof.entity.RstBoxGoc;
import com.gof.entity.RstCsm;
import com.gof.entity.RstDac;
import com.gof.entity.RstRollFwd;
import com.gof.enums.ECoa;
import com.gof.enums.ELossStep;
import com.gof.enums.ERollFwdType;
import com.gof.factory.FacRollFwd;
import com.gof.infra.GmvConstant;
import com.gof.interfaces.IBoxRst;
import com.gof.provider.PrvdAcct;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MdlRollFwdAlt {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	
	private static Map<String, List<RstBoxGoc>> rstBoxMap 	= new HashMap<String, List<RstBoxGoc>>();
	private static List<RstCsm> rstCsmList 					= new ArrayList<RstCsm>();
	private static List<RstDac> rstDacList 					= new ArrayList<RstDac>();
	private static Map<String, List<RstCsm>> rstCsmMap 		= new HashMap<String, List<RstCsm>>();
	private static Map<String, List<RstDac>> rstDacMap 		= new HashMap<String, List<RstDac>>();
	private static Map<String, List<RstCsm>> lossRatioMap 	= new HashMap<String, List<RstCsm>>();
	
	
	public static Stream<RstRollFwd> createPrev(){
		List<RstRollFwd> rstList = new ArrayList<RstRollFwd>();
		
		for(String gocId :PrvdMst.getGocIdList()) {
			rstList.addAll(createPrev(gocId).collect(toList()));
		}
		
		return rstList.stream();
	}
	
	public static Stream<RstRollFwd> create(){
		List<RstRollFwd> rstList = new ArrayList<RstRollFwd>();
		for(String gocId :PrvdMst.getGocIdList()) {
			rstList.addAll(create(gocId).collect(toList()));
		}
		return rstList.stream();
	}
	public static Stream<RstRollFwd> createCsm(){
		List<RstRollFwd> rstList = new ArrayList<RstRollFwd>();
		for(String gocId :PrvdMst.getGocIdList()) {
			rstList.addAll(createCsm(gocId).collect(toList()));
		}
		return rstList.stream();
	}
	
	public static Stream<RstRollFwd> createDac(){
		List<RstRollFwd> rstList = new ArrayList<RstRollFwd>();
		for(String gocId :PrvdMst.getGocIdList()) {
			rstList.addAll(createDac(gocId).collect(toList()));
		}
		return rstList.stream();
	}
	
	public static Stream<RstRollFwd> createPrev(String gocId){
		
		return RstDao.getRollFwdRst(stBssd).stream()
					 .filter(s->s.getRollFwdType().equals(ERollFwdType.CURR_CLOSE))
					 .map(s-> FacRollFwd.convertFromPrev(bssd, s))
					 ;
	}
	
	
	public static Stream<RstRollFwd> create(String gocId){
		List<RstRollFwd> rstList = new ArrayList<RstRollFwd>();
		
//		CSM,DAC excluded!!!!
		for(ECoa coa : ECoa.getCoaList()) {
			rstList.addAll(createByCoa(gocId, coa).collect(toList()));
//			if(coa.equals(ECoa.IREV)) {
//				
//			}
		}
		
		return rstList.stream();
	}
	
	public static Stream<RstRollFwd> createCsm(String gocId){
		List<RstRollFwd> rstList = new ArrayList<RstRollFwd>();
		
		List<RstCsm> csmList = getRstCsmList().stream().filter(s->s.getGocId().equals(gocId)).collect(toList());
		for(RstCsm rstCsm : csmList) {
//			log.info("aaaa : {},{},{}", rstCsm.toString());
			rstList.add(FacRollFwd.buildFromCsm(bssd, gocId, rstCsm));
		}
		return rstList.stream();
	}
	
	public static Stream<RstRollFwd> createDac(String gocId){
		List<RstRollFwd> rstList = new ArrayList<RstRollFwd>();
		
		List<RstDac> dacList = getRstDacList().stream().filter(s->s.getGocId().equals(gocId)).collect(toList());
		for(RstDac rstDac : dacList) {
//			log.info("aaaa : {},{},{}", rstCsm.toString());
			rstList.add(FacRollFwd.buildFromDac(bssd, gocId, rstDac));
		}
		return rstList.stream();
	}
	
	public static Stream<RstRollFwd> createByCoa(String gocId, ECoa coa){
		List<RstRollFwd> rstList = new ArrayList<RstRollFwd>();
		
		List<MstRollFwd> rollFwdList = PrvdMst.getMstRollFwdList().stream().filter(s-> s.getCoaMap().get(coa)).collect(toList());
		
		Map<ERollFwdType, ERollFwdType> priorCloseMap =getPriorCloseStepMap(rollFwdList);

		Map<ELossStep, Double> lossMap= getLossRatioMap(gocId).stream().collect(toMap(RstCsm::getLossStep, RstCsm::getAppliedLossRatio, (s,u)->s));

		Map<String, List<IBoxRst>> rstMap = getRstBoxMap(gocId).stream().collect(groupingBy(RstBoxGoc::getCalcId, toList()));
		
		Map<String, List<IBoxRst>> csmMap = getRstCsmMap(gocId).stream().collect(groupingBy(RstCsm::getCalcId, toList()));
		
		for(Map.Entry<String, List<IBoxRst>> entry: csmMap.entrySet()) {
			if(!rstMap.containsKey(entry.getKey())) {
				rstMap.put(entry.getKey(), entry.getValue());
			}
		}
		
		for(MstRollFwd rollFwd  : rollFwdList) {
			ERollFwdType currRollFwd = rollFwd.getRollFwdType();
			ERollFwdType prevRollFwd = priorCloseMap.get(currRollFwd);
			
			if(currRollFwd.isClose()) {
//				log.info("ggg :  {},{},{}", aa.getRollFwdType() );
				MstCalc mstDeltaSum = PrvdMst.getMstCalcDeltaSum();
				RstRollFwd tempRst = createCloseStep(gocId, coa, rollFwd, rstList, mstDeltaSum, prevRollFwd); 
				rstList.add(tempRst);
			}

			for(MapJournalRollFwd journal : PrvdMst.getJournalRollFwdList(currRollFwd, coa)) {	
					MstCalc mstCalc = journal.getMstCalc();
//					log.info("aaaaa : {},{},{},{}" , mstCalc.getCalcType(), journal.getRollFwdType(), journal.getCreditCoa(), journal.getDebitCoa());
					
					double lossRatio = lossMap.getOrDefault(mstCalc.getLossStep(), 0.0);
	
					switch (mstCalc.getCalcType()) {
					case BOX:
					case WATERFALL:	
					case REF:	
					case NATIVE:
						if(rstMap.containsKey(mstCalc.getAppliedCalcId())) {
							if(coa.equals(ECoa.AOCI) && PrvdMst.getEirUpdateErrorGocIdList().contains(gocId) && journal.getRollFwdType().equals(ERollFwdType.PREV_CLOSE)) {
								
							}
							else{
								for(IBoxRst zz : rstMap.get(mstCalc.getAppliedCalcId())) {
									rstList.add(createFromBox(gocId, coa, journal, zz, lossRatio));		
								}
							}
						}	
//						else {
//							rstList.add(createFromNative(gocId, journal, mstCalc, priorClose, initClose));
//						}
//						rstList.forEach(s->log.info("zzzz : {},{}", s.toString(), s.getBoxValue()));
						break;
//					case NATIVE:
//						rstList.add(createFromNative(gocId, journal, mstCalc, priorClose, initClose));
//						break;
						
					case DELTA_SUM:
						RstRollFwd tempRst = createCloseStep(gocId, coa, rollFwd, rstList, mstCalc,  prevRollFwd);
						rstList.add(tempRst);
						break;
	
					default:
						break;
					}
			}
		}
		
		return rstList.stream();
	}
	
	private static RstRollFwd createFromBox(String gocId, ECoa coa, MapJournalRollFwd journal, IBoxRst rstBox, double lossRatio) {
		MstCalc mstCalc = journal.getMstCalc();
		
		double coaValue =0.0;
		double closeAmt =0.0;
		
		double boxValue = rstBox.getBoxValue();
		double lossAdjValue = mstCalc.getLossAdjust().applyAsDouble(boxValue,lossRatio);
		
		coaValue = PrvdAcct.getCoaValue(coa, journal, boxValue, lossRatio);
		
		if(journal.getRollFwdType().isClose()) {
			closeAmt = coaValue;
			coaValue = 0.0;
		}
		
		return FacRollFwd.build(bssd, gocId, coa, journal.getRollFwdType(), rstBox.getRunsetId(), mstCalc, 1,  lossAdjValue, coaValue, closeAmt,  "");
		
	}
	
	private static RstRollFwd createCloseStep(String gocId, ECoa coa, MstRollFwd mstRollFwd, List<RstRollFwd> rstList, MstCalc mstCalc, ERollFwdType prevRollFwd) {
		String remark ="CLOSE_STEP";
		ERollFwdType currRollFwd = mstRollFwd.getRollFwdType();
		
//		log.info("RstRlowFwd : {},{}",rstList.size(), prevRollFwd);
		Map<String, Double> prevMap = rstList.stream()
											.filter(s->!s.getRollFwdType().isClose())
											.filter(s->s.getRollFwdType().getOrder() < currRollFwd.getOrder())
											.filter(s->s.getRollFwdType().getOrder() > prevRollFwd.getOrder())
											.collect(toMap(RstRollFwd::getGocId , RstRollFwd::getDeltaAmt, (s,u)->s+u));
	
		Map<String, Double> prevClosMap = rstList.stream()
											 .filter(s->s.getRollFwdType().isClose())
											 .filter(s->s.getRollFwdType().equals(prevRollFwd))
											 .collect(toMap(RstRollFwd::getGocId , RstRollFwd::getCloseAmt, (s,u)->s+u));
		
		double deltaAmt = prevMap.getOrDefault(gocId, 0.0);
		double closeAmt = prevClosMap.getOrDefault(gocId,0.0) + deltaAmt;
		
		RstRollFwd tempRst =FacRollFwd.buildClose(bssd, gocId,coa,  mstRollFwd.getRollFwdType(), mstCalc, 0.0, deltaAmt, closeAmt, remark);		//TODO :Check...
		return tempRst;
	}
	
	
	private static List<RstBoxGoc> getRstBoxMap(String gocId) {
		if(rstBoxMap.isEmpty()) {
			rstBoxMap  = BoxDao.getRstBoxGocGroup(bssd)
//					.filter(s->s.getLiabType().equals(ELiabType.LRC))
					.collect(groupingBy(RstBoxGoc::getGocId, toList()));
		}
		return rstBoxMap.getOrDefault(gocId, new ArrayList<RstBoxGoc>());
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
	
	private static List<RstDac> getRstDacList() {
		if(rstDacList.isEmpty()) {
			rstDacList  = RstDao.getRstDac(bssd);
		}
		return rstDacList;
	}
	
	private static List<RstCsm> getLossRatioMap(String gocId) {
		if(lossRatioMap.isEmpty()) {
			lossRatioMap  = RstDao.getRstCsm(bssd).stream()
								  .filter(s->s.getLossStep()!=null)
								  .collect(groupingBy(RstCsm::getGocId, toList()));
		}
		return lossRatioMap.getOrDefault(gocId, new ArrayList<RstCsm>());
	}

	private static  Map<ERollFwdType, ERollFwdType>  getPriorCloseStepMap(List<MstRollFwd> rollFwdList) {
		Map<ERollFwdType, ERollFwdType> rstMap = new HashMap<ERollFwdType, ERollFwdType>();
		
		ERollFwdType prior = ERollFwdType.PREV_CLOSE;

		for(MstRollFwd zz : rollFwdList) {
			rstMap.put(zz.getRollFwdType(), prior);
			if(zz.isCloseStep()) {
				prior = zz.getRollFwdType();
			}
			
		}
		return rstMap;
	}
}