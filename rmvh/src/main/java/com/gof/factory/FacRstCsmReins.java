package com.gof.factory;

import java.time.LocalDateTime;

import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MstCalc;
import com.gof.entity.MstRollFwd;
import com.gof.entity.RstCsm;
import com.gof.enums.ELossStep;
import com.gof.enums.EOperator;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FacRstCsmReins {

	public static RstCsm build(String bssd, String gocId,  MapJournalRollFwd journal, String runsetId, double boxAmt, double deltaCsm, double closeAmt, String remark){
		int seq = journal.getRollFwdType().getOrder();

		if(!journal.getMstRollFwd().isCloseStep()) {
			closeAmt = 0.0;
		}
		
		return build(bssd, gocId, journal.getMstRollFwd(), runsetId, journal.getMstCalc(), seq, boxAmt, deltaCsm, closeAmt, remark);
	}
	
	public static RstCsm buildDelta(String bssd, String gocId,  MapJournalRollFwd journal, String runsetId, double boxAmt, double deltaCsm, double closeAmt, String remark){
		int seq = journal.getRollFwdType().getOrder();
//		if( !journal.getBookingYn().isTrueFalse()) {
//			deltaCsm = 0.0;
//		}
//		log.info("zzzzzz : {},{},{},{}", journal.getRollFwdType(), boxAmt, deltaCsm, closeAmt);
		if(journal.getMstRollFwd().isCloseStep()) {
			boxAmt  = 0.0;
		}else {
			closeAmt = 0.0;
		}
		return build(bssd, gocId, journal.getMstRollFwd(), runsetId, journal.getMstCalc(), seq, boxAmt, deltaCsm, closeAmt, remark);
	}
	
	public static RstCsm buildClose(String bssd, String gocId,  MstRollFwd mstRollFwd, String runsetId, MstCalc mstCalc, double boxAmt, double deltaCsm, double csmAmt, String remark){
		
		int seq = mstRollFwd.getRollFwdType().getOrder();
		return build(bssd, gocId, mstRollFwd, runsetId, mstCalc, seq, boxAmt, deltaCsm, csmAmt, remark);
		
	}

	private static RstCsm build(String bssd, String gocId,  MstRollFwd mstRollFwd, String runsetId, MstCalc mstCalc, int seq, double boxAmt, double deltaCsm, double csmAmt, String remark){
		if(mstRollFwd.getRollFwdType()==null) {
			log.info("roll fww :  {}, {}", mstRollFwd, mstRollFwd.getRollFwdType());
			
		}
		return RstCsm.builder().baseYymm(bssd)
				.gocId(gocId)
				.mstRollFwd(mstRollFwd)
				.runsetId(runsetId)
				.mstCalc(mstCalc)
				.seq(seq)
				.operatorType(EOperator.PLUS)
				.boxAmt(boxAmt)
				.deltaCsmAmt(deltaCsm)					//TODO
				.csmAmt(csmAmt)
				.lossStep(ELossStep.L1)
				.remark(remark)
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build()
				;
	}
	
	

	
	
}
