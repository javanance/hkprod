package com.gof.factory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.gof.entity.MapRunsetCalc;
import com.gof.entity.MstRunset;
import com.gof.entity.RaLv2Delta;
import com.gof.entity.RstBoxGoc;
import com.gof.entity.TvogLv2Delta;
import com.gof.enums.EBoolean;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FacBoxGoc {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;

	public static List<RstBoxGoc> build(String bssd, RaLv2Delta box, List<MapRunsetCalc> runsetList){
		List<RstBoxGoc> rstList = new ArrayList<RstBoxGoc>();
		double boxValue =0.0;
		for(MapRunsetCalc calc : runsetList) {
			boxValue = calc.getMstCalc().getSignAdjust().getAdj() * box.getDeltaRaAmt();
			//TODO : Changet colum!!!!!
			rstList.add(build(bssd, box.getGocId(), ELiabType.LRC, calc.getMstRunset(), calc.getCalcId(), boxValue));
		}
		return rstList;
	}
	
	public static List<RstBoxGoc> build(String bssd, TvogLv2Delta box, List<MapRunsetCalc> runsetList){
		List<RstBoxGoc> rstList = new ArrayList<RstBoxGoc>();
		double boxValue =0.0;
		for(MapRunsetCalc calc : runsetList) {
			boxValue = calc.getMstCalc().getSignAdjust().getAdj() * box.getDeltaTvogAmt();
			//TODO : Change column!!!!!
			rstList.add(build(bssd, box.getGocId(), ELiabType.LRC, calc.getMstRunset(), calc.getCalcId(), boxValue));
		}
		return rstList;
	}
	
	private static RstBoxGoc build(String bssd, String gocId, ELiabType liabType, MstRunset mstRunset, String calcId, double boxValue){
		return RstBoxGoc.builder()
						.baseYymm(bssd)
						.gocId(gocId)
						.liabType(liabType)
						.mstRunset(mstRunset)
						.calcId(calcId)
						.stStatus(EContStatus.NORMAL)
						.endStatus(EContStatus.NORMAL)		//TODO :Check Default!!!!
						.newContYn(EBoolean.N)
						.slidingNum(0)
						.cfAmt(0.0)
						.prevCfAmt(0.0)
						.deltaCfAmt(0.0)
						.boxValue(boxValue)
						.appliedRate(0.0)
						.remark("")
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}
}
