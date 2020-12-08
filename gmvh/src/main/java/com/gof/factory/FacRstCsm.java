package com.gof.factory;

import java.time.LocalDateTime;

import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MstCalc;
import com.gof.entity.MstRollFwd;
import com.gof.entity.RstCsm;
import com.gof.enums.EOperator;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FacRstCsm {

//	public static RstCsm buildDeltaLoss(String bssd, String gocId,  MapJournalRollFwd journal, String runsetId, double deltaCalcCsm, int seq){
//		return buildDelta(bssd, gocId, journal, runsetId, deltaCalcCsm, 0.0, seq);
//		
//	}
	
//	Case For Change the same amount on both deltaCalcCsm and deltaCsm.  Builde From BOX..
//	public static RstCsm buildDelta(String bssd, String gocId,  MapJournalRollFwd journal, MstRunset mstRunset, double deltaCalcCsmAmt){
//		String runsetId = mstRunset.getRunsetId();
////		int seq = journal.getRollFwdType().getOrder() *10 + mstRunset.getSeq();
//		int seq = journal.getRollFwdType().getOrder() ;
//		
//		return buildDelta(bssd, gocId, journal, runsetId, deltaCalcCsmAmt, deltaCalcCsmAmt, seq);
//	}
	
	
//	Case For Change only deltaCsm.  Builde From Native..
	public static RstCsm buildDeltaCsm(String bssd, String gocId,  MapJournalRollFwd journal, String runsetId, double boxAmt, double deltaCsm, String remark){
		return buildDelta(bssd, gocId, journal, runsetId, boxAmt, 0.0, deltaCsm, remark);
	}
	
	public static RstCsm buildDelta(String bssd, String gocId,  MapJournalRollFwd journal, String runsetId, double boxAmt, double deltaCalcCsm, double deltaCsm, String remark){
		double closeCalcCsm =0.0;
		double closeCsm =0.0;
		int seq = journal.getRollFwdType().getOrder();
		
		if(journal.getRollFwdType().isClose()) {
			closeCalcCsm = deltaCalcCsm;
			closeCsm= deltaCsm;
			deltaCalcCsm =0.0;
			deltaCsm =0.0;
		}
		
		if( !journal.getBookingYn().isTrueFalse()) {
			deltaCsm = 0.0;
		}
		
		return build(bssd, gocId, journal.getMstRollFwd(), runsetId, journal.getMstCalc(), seq, boxAmt, deltaCalcCsm, closeCalcCsm, deltaCsm, closeCsm, 0.0, 0.0, 0.0, remark);
	}
	
	public static RstCsm buildClose(String bssd, String gocId,  MstRollFwd mstRollFwd, String runsetId, MstCalc mstCalc
										, double boxAmt, double deltaCalcCsm, double calcCsmAmt, double deltaCsm, double csmAmt, double fulfillAmt, String remark){
		double lossRatio 	=0.0;
		double deltaLossRatio =0.0;
		int seq = mstRollFwd.getRollFwdType().getOrder();
		
//		양수--> 음수, 음수--> 음수인 경우 
		if(calcCsmAmt < 0 && fulfillAmt > 0) {
			lossRatio	= calcCsmAmt >= 0 	? 0.0: -1.0* calcCsmAmt / fulfillAmt ;
			deltaLossRatio	= Math.max(calcCsmAmt, deltaCalcCsm ) / fulfillAmt ;
		}
//		음수 --> 양수로 전환된 경우임  	
		if(calcCsmAmt > 0 && deltaCalcCsm > calcCsmAmt && fulfillAmt > 0) {
			deltaLossRatio	= ( deltaCalcCsm-calcCsmAmt) / fulfillAmt ;
		}
								
		return build(bssd, gocId, mstRollFwd, runsetId, mstCalc, seq, boxAmt, deltaCalcCsm, calcCsmAmt, deltaCsm, csmAmt, fulfillAmt, deltaLossRatio, lossRatio, remark);
		
	}

	private static RstCsm build(String bssd, String gocId,  MstRollFwd mstRollFwd, String runsetId, MstCalc mstCalc, int seq, 
									double boxAmt, double deltaCalcCsm, double calcCsmAmt, double deltaCsm, double csmAmt, 
									double fulfillAmt, double deltaLossRatio, double lossRatio, String remark){
		
		return RstCsm.builder().baseYymm(bssd)
				.gocId(gocId)
				.mstRollFwd(mstRollFwd)
				.runsetId(runsetId)
				.mstCalc(mstCalc)
				.seq(seq)
				.operatorType(EOperator.PLUS)
				.boxAmt(boxAmt)
				.deltaCalcCsmAmt(deltaCalcCsm)
				.calcCsmAmt(calcCsmAmt)
				.deltaCsmAmt(deltaCsm)					//TODO
				.csmAmt(csmAmt)
				.lossStep(mstRollFwd.getLossStep())
				.lossFulfillAmt(fulfillAmt)
				.deltaLossRatio(deltaLossRatio)
				.lossRatio(lossRatio)
				.remark(remark)
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build()
				;
	}
	
	
//	public static RstCsm merge(RstCsm base, RstCsm other) {
//		return RstCsm.builder().baseYymm(base.b)
//				.gocId(gocId)
//				.mstRollFwd(mstRollFwd)
//				.runsetId(runsetId)
//				.mstCalc(mstCalc)
//				.seq(seq)
//				.operatorType(EOperator.PLUS)
//				.boxAmt(boxAmt)
//				.deltaCalcCsmAmt(deltaCalcCsm)
//				.calcCsmAmt(calcCsmAmt)
//				.deltaCsmAmt(deltaCsm)					//TODO
//				.csmAmt(csmAmt)
//				.lossStep(mstRollFwd.getLossStep())
//				.lossFulfillAmt(fulfillAmt)
//				.deltaLossRatio(deltaLossRatio)
//				.lossRatio(lossRatio)
//				.remark(remark)
//				.lastModifiedBy(GmvConstant.getLastModifier())
//				.lastModifiedDate(LocalDateTime.now())
//				.build()
//				;
//	}

	
	
}
