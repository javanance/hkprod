package com.gof.factory;

import java.time.LocalDateTime;

import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MstCalc;
import com.gof.entity.RstRollFwdLoss;
import com.gof.enums.ECoa;
import com.gof.enums.EOperator;
import com.gof.enums.ERollFwdType;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FacRollFwdLoss {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;


	public static RstRollFwdLoss buildDelta(String bssd,  String gocId, ECoa coa,  MapJournalRollFwd journal, String runsetId, double boxAmt, double deltaAmt, String remark) {
		return build(bssd, gocId, coa,  journal.getRollFwdType(), runsetId, journal.getMstCalc(), boxAmt, deltaAmt, 0.0, remark) ;
	}
	
	public static RstRollFwdLoss buildClose(String bssd, String gocId, ECoa coa, ERollFwdType rollFwd, MstCalc mstCalc, String runsetId, double boxAmt, double deltaAmt, double closeAmt, String remark){
		return build(bssd, gocId, coa,  rollFwd, runsetId, mstCalc, boxAmt, deltaAmt, closeAmt, remark) ;
	}
	
	public static RstRollFwdLoss buildClose(String bssd, String gocId, ECoa coa, ERollFwdType rollFwd, MstCalc mstCalc, double boxAmt, double deltaAmt, double closeAmt, String remark){
		String runsetId ="CLOSE";
		return build(bssd, gocId, coa,  rollFwd, runsetId, mstCalc, boxAmt, deltaAmt, closeAmt, remark) ;
	}
	
	private static RstRollFwdLoss build(String bssd,  String gocId, ECoa coa,  ERollFwdType rollFwd, String runsetId, MstCalc mstCalc, double boxAmt, double deltaAmt, double closeAmt, String remark) {
		return RstRollFwdLoss.builder()
						.baseYymm(bssd)
						.gocId(gocId)
						.coaId(coa)
						.rollFwdType(rollFwd)
						.runsetId(runsetId)							
						.mstCalc(mstCalc)				
						.rollFwdSeq(rollFwd.getOrder())
						.runsetSeq(1)
						.operatorType(EOperator.PLUS)
						.boxAmt(boxAmt)
						.deltaAmt(deltaAmt)
						.closeAmt(closeAmt)
						.remark(remark)
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}
	
//	public static RstRollFwdLoss buildDelta(String bssd,  String gocId, ECoa coa,  MapJournalRollFwd journal, String runsetId, double boxAmt, double deltaAmt, String remark) {
//	return RstRollFwdLoss.builder()
//					.baseYymm(bssd)
//					.gocId(gocId)
//					.coaId(coa)
//					.rollFwdType(journal.getRollFwdType())
//					.runsetId(runsetId)							
//					.mstCalc(journal.getMstCalc())				
//					.rollFwdSeq(journal.getRollFwdType().getOrder())
//					.runsetSeq(1)
//					.operatorType(EOperator.PLUS)
//					.boxAmt(boxAmt)
//					.deltaAmt(deltaAmt)
//					.closeAmt(0.0)
//					.remark(remark)
//					.lastModifiedBy(GmvConstant.getLastModifier())
//					.lastModifiedDate(LocalDateTime.now())
//					.build();
//}
	
//	public static RstRollFwdLoss buildClose(String bssd, String gocId, ECoa coa, ERollFwdType rollFwd, MstCalc mstCalc, double boxAmt, double deltaAmt, double closeAmt, String remark){
//		
//		return RstRollFwdLoss.builder()
//						.baseYymm(bssd)
//						.gocId(gocId)
//						.coaId(coa)
//						.rollFwdType(rollFwd)
//						.runsetId("CLOSE")							
////						.calcId("DELTA_SUM")						
//						.mstCalc(mstCalc)								
//						.rollFwdSeq(rollFwd.getOrder())
//						.runsetSeq(1)
//						.operatorType(EOperator.PLUS)
//						.boxAmt(boxAmt)
//						.deltaAmt(deltaAmt)
//						.closeAmt(closeAmt)
//						.remark(remark)
//						.lastModifiedBy(GmvConstant.getLastModifier())
//						.lastModifiedDate(LocalDateTime.now())
//						.build();
//	}

	
	
	
}
