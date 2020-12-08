package com.gof.factory;

import java.time.LocalDateTime;

import com.gof.entity.RstLossStep;
import com.gof.enums.EBoolean;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;
import com.gof.enums.ELossStep;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FacLossStep {

	public static RstLossStep build(String bssd, String gocId, ELossStep lossStep, String cfGroup, double cfAmt, double epvAmt, double raAmt, String remark){
        return	RstLossStep.builder()
						.baseYymm(bssd)
						.gocId(gocId)
						.liabType(ELiabType.LRC)
						.stStatus(EContStatus.NORMAL)
						.endStatus(EContStatus.NORMAL)			//TODO :Check!!! default
						.newContYn(EBoolean.N)
						.lossStep(lossStep)
						.cfGroupId(cfGroup)
//						.outCfAmt(cf)
//						.inCfAmt(inCfAmt)
						.cfAmt(cfAmt)
//						.inEpvAmt(inEpvAmt)
//						.outEpvAmt(outEpvAmt)
						.epvAmt(epvAmt)
						.raAmt(raAmt)
						.remark(remark)
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}
}
