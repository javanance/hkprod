package com.gof.rollfwd;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao.RstDao;
import com.gof.entity.AcctBoxGoc;
import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.RstRollFwd;
import com.gof.entity.RstRollFwdLoss;
import com.gof.enums.EBoolean;
import com.gof.enums.ECalcType;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MdlAcctBox {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	
	private static List<RstRollFwd> rstRollFwdList 	   = new ArrayList<RstRollFwd>();
	private static List<RstRollFwdLoss> rstRollFwdLoss = new ArrayList<RstRollFwdLoss>();
	
	public static Stream<AcctBoxGoc> createFromRollFwd(){
		List<AcctBoxGoc> rstList = new ArrayList<AcctBoxGoc>();
		for(String gocId: PrvdMst.getGocIdList()) {
//			rstList.addAll(createFromRollFwd(gocId).collect(toList()));
			rstList.addAll(createFromRollFwdTest(gocId).collect(toList()));
		}
		return rstList.stream();
	}
	
	public static Stream<AcctBoxGoc> createFromRollFwdLoss(){
		List<AcctBoxGoc> rstList = new ArrayList<AcctBoxGoc>();
		for(String gocId: PrvdMst.getGocIdList()) {
//			rstList.addAll(createFromRollFwdLoss(gocId).collect(toList()));
			rstList.addAll(createFromRollFwdLossTest(gocId).collect(toList()));
		}
		return rstList.stream();
	}
	
	public static Stream<AcctBoxGoc> createFromRollFwd(String gocId){
		List<AcctBoxGoc> rstList = new ArrayList<AcctBoxGoc>();
		
		List<MapJournalRollFwd> journalList		= PrvdMst.getJournalRollFwdList().stream().filter(s->s.getBookingYn().isTrueFalse()).collect( toList());
		
		Map<String, Map<String, List<RstRollFwd>>> rstRollFwdMap = getRstRollFwdList().stream()
																		.filter(s->s.getGocId().equals(gocId))
																		.collect(groupingBy(RstRollFwd::getPk, groupingBy(RstRollFwd::getRunsetId, toList())));
		
		double boxValue =0.0;
		double coaValue =0.0;
		
//		getPK: rollFwd + calcId !!!!
		for(MapJournalRollFwd journal :journalList) {
//			log.info("aaa : {}", journal.getPk() );
			
			if( rstRollFwdMap.containsKey(journal.getPk())){
//				log.info("size of rstRollfwd : {},{}", rstRollFwdMap.get(journal.getPk()).size(), rstRollFwdMap.get(journal.getPk()));
				for(Map.Entry<String, List<RstRollFwd>> entry : rstRollFwdMap.get(journal.getPk()).entrySet()) {
					for(RstRollFwd zz : entry.getValue()) {
						boxValue = zz.getMstCalc().getCalcType().equals(ECalcType.DELTA_SUM)? zz.getCloseAmt() : zz.getBoxAmt();
						
						coaValue = zz.getMstCalc().getCalcMethod()==null? boxValue : zz.getMstCalc().getCalcMethod().getAdjFn().apply(boxValue);
						
//						log.info("aaa : {},{}", journal.getPk(), boxValue );
					}
					rstList.add(build(bssd, gocId, entry.getKey(), journal, boxValue, coaValue));
				}
			}
		}
		return rstList.stream();
	}
	
	public static Stream<AcctBoxGoc> createFromRollFwdTest(String gocId){
		List<AcctBoxGoc> rstList = new ArrayList<AcctBoxGoc>();
		
		List<MapJournalRollFwd> journalList		= PrvdMst.getJournalRollFwdList().stream().filter(s->s.getBookingYn().isTrueFalse()).collect( toList());
		

//		BSSD, GOC 湲곗�, Roll Fwd, Runset, caclId �쓽 議고빀�씠 Unique �븳 boxValue 瑜� 媛�吏�..!!!
		Map<String, Map<String, RstRollFwd>> rstRollFwdMap = getRstRollFwdList().stream()
																.filter(s->s.getGocId().equals(gocId))
																.collect(groupingBy(RstRollFwd::getPk, toMap(RstRollFwd::getRunsetId, Function.identity(), (s,u)-> s)));
		
		double boxValue =0.0;
		double coaValue =0.0;
//		log.info("Rst Roll Fwd Map : {}", rstRollFwdMap.size());
		
//		getPK: rollFwd + calcId !!!!
		for(MapJournalRollFwd journal :journalList) {
//			log.info("aaa : {}", journal.getPk() );
			
			if( rstRollFwdMap.containsKey(journal.getPk())){

				for(Map.Entry<String, RstRollFwd> entry : rstRollFwdMap.get(journal.getPk()).entrySet()) {
					RstRollFwd rstTemp = entry.getValue();
				
					boxValue = rstTemp.getMstCalc().getCalcType().equals(ECalcType.DELTA_SUM)? rstTemp.getCloseAmt() : rstTemp.getBoxAmt();
					
					coaValue = rstTemp.getMstCalc().getCalcMethod()==null? boxValue : rstTemp.getMstCalc().getCalcMethod().getAdjFn().apply(boxValue);
					
//					log.info("bbb : {},{},{}", journal.getPk(), entry.getKey(), boxValue );
					
					rstList.add(build(bssd, gocId, entry.getKey(), journal, boxValue, coaValue));
				}
			}
		}
		return rstList.stream();
	}
	
	public static Stream<AcctBoxGoc> createFromRollFwdLoss(String gocId){
		List<AcctBoxGoc> rstList = new ArrayList<AcctBoxGoc>();
		List<MapJournalRollFwd> journalList		= PrvdMst.getJournalRollFwdList().stream().filter(s->s.getBookingYn().isTrueFalse()).collect( toList());
		
		Map<String, Map<String, List<RstRollFwdLoss>>> rstRollFwdMap = getRstRollFwdLoss().stream()
														.filter(s->s.getGocId().equals(gocId))
														.collect(groupingBy(RstRollFwdLoss::getPk, groupingBy(RstRollFwdLoss::getRunsetId, toList())));

		double boxValue =0.0;
		double coaValue =0.0;
		for(MapJournalRollFwd journal :journalList) {
			
//			log.info("aaa : {}", journal.getPk());
			
			if( rstRollFwdMap.containsKey(journal.getPk())){
				
				for(Map.Entry<String, List<RstRollFwdLoss>> entry : rstRollFwdMap.get(journal.getPk()).entrySet()) {
					for(RstRollFwdLoss zz : entry.getValue()) {
						boxValue = zz.getMstCalc().getCalcType().equals(ECalcType.DELTA_SUM)? zz.getCloseAmt() : zz.getBoxAmt();
						coaValue = zz.getMstCalc().getCalcMethod()==null? boxValue : zz.getMstCalc().getCalcMethod().getAdjFn().apply(boxValue);
					}
					rstList.add(build(bssd, gocId, entry.getKey(), journal, boxValue, coaValue));
				}
			}
		}
		return rstList.stream();
				
	}
	public static Stream<AcctBoxGoc> createFromRollFwdLossTest(String gocId){
		List<AcctBoxGoc> rstList = new ArrayList<AcctBoxGoc>();
		List<MapJournalRollFwd> journalList		= PrvdMst.getJournalRollFwdList().stream()
															.filter(s->s.getBookingYn().isTrueFalse()).collect( toList());
		
		Map<String, Map<String, RstRollFwdLoss>> rstRollFwdMap = getRstRollFwdLoss().stream()
														.filter(s->s.getGocId().equals(gocId))
														.filter(s->s.getBoxAmt()!=0)
														.collect(groupingBy(RstRollFwdLoss::getPk, toMap(RstRollFwdLoss::getRunsetId, Function.identity(), (s,u)-> s)));

		double boxValue =0.0;
		double coaValue =0.0;
		for(MapJournalRollFwd journal :journalList) {
			
//			log.info("aaa : {}", journal.getPk());
			
			if( rstRollFwdMap.containsKey(journal.getPk())){
				
				for(Map.Entry<String, RstRollFwdLoss> entry : rstRollFwdMap.get(journal.getPk()).entrySet()) {
					
					RstRollFwdLoss rstTemp = entry.getValue();
					boxValue = rstTemp.getMstCalc().getCalcType().equals(ECalcType.DELTA_SUM)? rstTemp.getCloseAmt() : rstTemp.getBoxAmt();
					coaValue = rstTemp.getMstCalc().getCalcMethod()==null? boxValue : rstTemp.getMstCalc().getCalcMethod().getAdjFn().apply(boxValue);
					
					rstList.add(build(bssd, gocId, entry.getKey(), journal, boxValue, coaValue));
				}
			}
		}
		return rstList.stream();
				
	}
	
	private static List<RstRollFwd> getRstRollFwdList() {
		if(rstRollFwdList.isEmpty()) {
			rstRollFwdList  = RstDao.getRollFwdRst(bssd);
		}
		return rstRollFwdList;
	}
	
	private static List<RstRollFwdLoss> getRstRollFwdLoss() {
		if(rstRollFwdLoss.isEmpty()) {
			rstRollFwdLoss  = RstDao.getRstRollFwdLoss(bssd);
		}
		return rstRollFwdLoss;
	}
	
	private static AcctBoxGoc build(String bssd,  String gocId, String runsetId, MapJournalRollFwd journal, double boxAmt, double coaAmt) {
		return AcctBoxGoc.builder()
						.baseYymm(bssd)
						.gocId(gocId)
						.liabType(ELiabType.LRC)
						.stStatus(EContStatus.NORMAL)
						.endStatus(EContStatus.NORMAL)
						.newContYn(EBoolean.N)
						.rollFwdType(journal.getRollFwdType().name())
						.runsetId(runsetId)		
						.calcId(journal.getCalcId())		
						.subSeq(journal.getSubSeq())		
						.rollFwdSeq(journal.getRollFwdType().getOrder())
						.debitCoa(journal.getDebitCoa())
						.creditCoa(journal.getCreditCoa())
						.boxAmt(boxAmt)
						.coaAmt(coaAmt)
						.remark("DELTA_")
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}
}