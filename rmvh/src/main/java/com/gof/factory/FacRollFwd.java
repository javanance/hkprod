package com.gof.factory;

import java.time.LocalDateTime;

import com.gof.entity.MstCalc;
import com.gof.entity.RstCsm;
import com.gof.entity.RstRollFwd;
import com.gof.enums.ECoa;
import com.gof.enums.EOperator;
import com.gof.enums.ERollFwdType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FacRollFwd {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	
	public static RstRollFwd convertFromPrev(String bssd, RstRollFwd rst) {
		String remark ="FROM PREV_ROLL_FWD";
		double boxAmt = rst.getBoxAmt();
		double deltaAmt = rst.getDeltaAmt();
		double closeAmt = rst.getCloseAmt();
		
		return build(bssd, rst.getGocId(), rst.getCoaId(), ERollFwdType.PREV_CLOSE , "CLOSE_STEP", PrvdMst.getMstCalcDeltaSum(), 1,  boxAmt, deltaAmt, closeAmt,  remark);
	}
	
//	public static RstRollFwd build(String bssd,  String gocId, ECoa coa, MapJournalRollFwd journal, String runsetId, double boxAmt, double deltaAmt, String remark) {
//		double closeAmt =0.0;
//		double delta 	=0.0;
//		
//		if(journal.getRollFwdType().isClose()) {
//			closeAmt = deltaAmt;
//			delta	 = 0.0;
//		}
//		return build(bssd, gocId, coa, journal.getRollFwdType(), runsetId, journal.getMstCalc(), 1, boxAmt, delta, closeAmt, remark);
//	}
	
	public static RstRollFwd buildFromCsm(String bssd, String gocId, RstCsm rstCsm) {
		String remark ="FROM RST_CSM";
		double boxAmt = rstCsm.getBoxAmt();
		double deltaAmt = rstCsm.getDeltaCsmAmt();
		double closeAmt = rstCsm.getCsmAmt();
		
		return build(bssd, gocId, ECoa.CSM,  rstCsm.getRollFwdType(),rstCsm.getRunsetId(), rstCsm.getMstCalc(), 1,  boxAmt, deltaAmt, closeAmt,  remark);
	}
	
	
	public static RstRollFwd buildClose(String bssd,  String gocId,  ECoa coa, ERollFwdType rollFwd, MstCalc mstCalc, double boxAmt,  double rollFwdAmt, double closeAmt, String remark) {

		return build(bssd, gocId, coa, rollFwd, "CLOSE", mstCalc, 1,  boxAmt, rollFwdAmt, closeAmt,  remark);
	}
	
	public static RstRollFwd build(String bssd,  String gocId, ECoa coa, ERollFwdType rollFwd, String runsetId,  MstCalc mstCalc, int seq, double boxAmt,  double rollFwdAmt, double closeAmt, String remark) {
		return RstRollFwd.builder()
				.baseYymm(bssd)
				.gocId(gocId)
				.coaId(coa)
				.rollFwdType(rollFwd)
				.runsetId(runsetId)							
				.mstCalc(mstCalc)				
				.rollFwdSeq(rollFwd.getOrder())
				.runsetSeq(seq)
				.operatorType(EOperator.PLUS)
				.boxAmt(boxAmt)
				.deltaAmt(rollFwdAmt)
				.closeAmt(closeAmt)
				.remark(remark)
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
	}
	
//	private static RstRollFwd build(String bssd,  String gocId, String runsetId, MapJournalRollFwd journal,  ECoa coa, double boxAmt, double delta, double closeAmt, String remark) {
//			return RstRollFwd.builder()
//					.baseYymm(bssd)
//					.gocId(gocId)
//					.coaId(coa)
//					.rollFwdType(journal.getRollFwdType())
//					.runsetId(runsetId)							
//					.mstCalc(journal.getMstCalc())				
//					.rollFwdSeq(journal.getRollFwdType().getOrder())
//	//				.rollFwdType(rollFwdType)
//	//				.mstCalc(mstCalc)				
//	//				.rollFwdSeq(rollFwdType.getOrder())
//					.runsetSeq(1)
//					.operatorType(EOperator.PLUS)
//					.boxAmt(boxAmt)
//					.deltaAmt(delta)
//					.closeAmt(closeAmt)
//					.remark(remark)
//					.lastModifiedBy("GMV:"+ GmvConstant.JOB_NO)
//					.lastModifiedBy(GmvConstant.getLastModifier())
//					.lastModifiedDate(LocalDateTime.now())
//					.build();
//		}
	
}
