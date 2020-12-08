package com.gof.ark.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.gof.ark.entity.ArkBoxMap;
import com.gof.ark.entity.ArkBoxRst;
import com.gof.ark.entity.ArkItemRst;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class FacArkBoxRst {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	
	public static List<ArkBoxRst> createFromItemRst(String bssd, ArkItemRst cf, List<ArkBoxMap> boxList) {
		List<ArkBoxRst> rstList = new ArrayList<ArkBoxRst>();
		
		if(boxList!=null) {
			for(ArkBoxMap boxMap :boxList) {
				rstList.add(ArkBoxRst.builder()
						.baseYymm(cf.getBaseYymm())
						.gocId(cf.getGocId())
						.liabType(cf.getLiabType())
						.arkRunsetId(cf.getArkRunsetId())
						.mstRunset(boxMap.getArkMstRunset().getMstRunset())
//						.runsetId(cf.getRunsetId())
						.calcId(boxMap.getMstCalc().getCalcId())
						.itemId(cf.getItemId())
						.stStatus(cf.getStStatus())
						.endStatus(cf.getEndStatus())
						.newContYn(cf.getNewContYn())
						.slidingNum(0)
						.cfAmt(cf.getItemAmt())
						.prevCfAmt(0.0)
						.deltaCfAmt(cf.getItemAmt())
						.boxValue(boxMap.getSignAdjust().getAdj() * cf.getItemAmt())
						.appliedRate(0.0)
						.remark("")
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build()
						);
				
			}
		}
		
		return rstList;
	}
	
}
