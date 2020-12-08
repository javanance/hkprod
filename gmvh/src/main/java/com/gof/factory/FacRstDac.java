package com.gof.factory;

import java.time.LocalDateTime;

import com.gof.entity.MstCalc;
import com.gof.entity.MstRollFwd;
import com.gof.entity.RstDac;
import com.gof.enums.ECoa;
import com.gof.enums.EOperator;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FacRstDac {
	
	public static RstDac build(String bssd, String gocId,  MstRollFwd mstRollFwd, String runsetId,  MstCalc mstCalc, double boxAmt, double delta, double dacAmt, String remark){
		return RstDac.builder().baseYymm(bssd)
						.gocId(gocId)
						.mstRollFwd(mstRollFwd)
						.runsetId(runsetId)
						.mstCalc(mstCalc)
						.seq(mstRollFwd.getRollFwdType().getOrder())
						.operatorType(EOperator.PLUS)
						.boxAmt(boxAmt)
						.deltaDacAmt(delta)
						.dacAmt(dacAmt)
						.remark(remark)
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build()
						;
	}
	
	public static RstDac build(String bssd, String gocId, ECoa coa, MstRollFwd mstRollFwd, String runsetId,  MstCalc mstCalc, double boxAmt, double delta, double dacAmt, String remark){
		return RstDac.builder().baseYymm(bssd)
						.gocId(gocId)
						.coaId(coa)
						.mstRollFwd(mstRollFwd)
						.runsetId(runsetId)
						.mstCalc(mstCalc)
						.seq(mstRollFwd.getRollFwdType().getOrder())
						.operatorType(EOperator.PLUS)
						.boxAmt(boxAmt)
						.deltaDacAmt(delta)
						.dacAmt(dacAmt)
						.remark(remark)
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build()
						;
	}

	public static RstDac buildDelta(String bssd, String gocId,  MstRollFwd mstRollFwd, String runsetId,  MstCalc mstCalc, double boxAmt, double delta, String remark){
		if(mstRollFwd.getRollFwdType().isClose()) {
			return build(bssd, gocId, mstRollFwd, runsetId, mstCalc, boxAmt, 0.0, delta, "CLOSE"+remark);
		}
		
		return build(bssd, gocId, mstRollFwd, runsetId, mstCalc, boxAmt, delta, 0.0, "DELTA"+ remark);
	}

	public static RstDac buildDelta(String bssd, String gocId, ECoa coa,  MstRollFwd mstRollFwd, String runsetId,  MstCalc mstCalc, double boxAmt, double delta, String remark){
		if(mstRollFwd.getRollFwdType().isClose()) {
			return build(bssd, gocId, coa, mstRollFwd, runsetId, mstCalc, boxAmt, 0.0, delta, "CLOSE"+remark);
		}
		
		return build(bssd, gocId, coa, mstRollFwd, runsetId, mstCalc, boxAmt, delta, 0.0, "DELTA"+ remark);
	}
}
