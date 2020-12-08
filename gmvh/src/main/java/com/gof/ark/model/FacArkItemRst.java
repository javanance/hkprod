package com.gof.ark.model;

import java.time.LocalDateTime;
import java.util.Map;

import com.gof.ark.entity.ArkFutureCf;
import com.gof.ark.entity.ArkFwdEpv;
import com.gof.ark.entity.ArkItemRst;
import com.gof.ark.entity.ArkReleaseCf;
import com.gof.infra.GmvConstant;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class FacArkItemRst {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	
	
	public static ArkItemRst createFromFwdEpv(String bssd, ArkFwdEpv epv) {
		String itemId  = epv.getFwdNum() > 0 ? "FWD_EPV": "EPV";
		return buildItemRst(bssd, epv, itemId, epv.getEpvAmt());
		
	}
	
	public static ArkItemRst createFromReleaseCf(String bssd, ArkReleaseCf releaseCf) {
		String itemId = "RELEASE_" + releaseCf.getCfType().name();
		
		return buildItemRst(bssd, releaseCf, itemId, releaseCf.getCfAmt());
	}
	
	public static ArkItemRst createFromReleaseCfAll(String bssd, ArkReleaseCf releaseCf) {
		String itemId = "RELEASE_CF";
		
		return buildItemRst(bssd, releaseCf, itemId, releaseCf.getCfAmt());
	}
	
	
	public static ArkItemRst createFromFutureCf(String bssd, ArkFutureCf futureCf) {
		String itemId = "FUTURE_" + futureCf.getCfType().name();
		
		return buildItemRst(bssd, futureCf, itemId, futureCf.getCfAmt());
	}
	
	public static ArkItemRst createEpvFromFutureCf(String bssd, ArkFutureCf futureCf) {
		String itemId = "EPV_" + futureCf.getCfType().name();
		
		return buildItemRst(bssd, futureCf, itemId, futureCf.getEpvAmt());
	}
	
	
	public static ArkItemRst createAccreteFromFutureCf(String bssd, ArkFutureCf futureCf , Map<String, Double> eirMap) {
		String itemId = "ACCRETE_" + futureCf.getCfType().name();
		double eir = eirMap.getOrDefault(futureCf.getGocId(), 0.0);
		
		int monthInterval = DateUtil.monthBetween(futureCf.getSetlYm(), bssd) ;
		
		double timeFactor = futureCf.getRunsetId().contains("201-1")? ( monthInterval +1 )/12.0 : monthInterval/12.0;
		
		double intFactor = Math.pow(1+eir, timeFactor) -1  ;
		
		return buildItemRst(bssd, futureCf, itemId, futureCf.getEpvAmt() * intFactor, "EPV&EIR & MONTH/12:"+ futureCf.getEpvAmt() +"_" +eir +"_"+ timeFactor);
	}
	
	public static ArkItemRst createFromFutureCfAll(String bssd, ArkFutureCf futureCf) {
		String itemId = "FUTURE_CF" ;
		
		return buildItemRst(bssd, futureCf, itemId, futureCf.getCfAmt());
	}
	
	

	private static ArkItemRst buildItemRst(String bssd, ArkFwdEpv epv, String itemId, double itemAmt) {
		return ArkItemRst.builder()
					.baseYymm(bssd)
					.gocId(epv.getGocId())
					.liabType(epv.getLiabType())
					.stStatus(epv.getStStatus())
					.endStatus(epv.getEndStatus())
					.newContYn(epv.getNewContYn())
					.arkRunsetId(epv.getArkRunsetId())
					.runsetId(epv.getRunsetId())
					.itemId(itemId)
					.itemAmt(itemAmt)
					.coaId("EPV")
					.seqNum(epv.getFwdNum())
					.lastModifiedBy(GmvConstant.getLastModifier())
					.lastModifiedDate(LocalDateTime.now())
					.build()
					;
	}
	
	private static ArkItemRst buildItemRst(String bssd, ArkReleaseCf cf, String itemId, double itemAmt) {
		return ArkItemRst.builder()
					.baseYymm(bssd)
					.gocId(cf.getGocId())
					.liabType(cf.getLiabType())
					.stStatus(cf.getStStatus())
					.endStatus(cf.getEndStatus())
					.newContYn(cf.getNewContYn())
					.arkRunsetId(cf.getArkRunsetId())
					.runsetId(cf.getRunsetId())
					.itemId(itemId)
					.itemAmt(itemAmt)
					.coaId("CF")
					.seqNum(0)
					.lastModifiedBy(GmvConstant.getLastModifier())
					.lastModifiedDate(LocalDateTime.now())
					.build()
					;
	}
	
	private static ArkItemRst buildItemRst(String bssd, ArkFutureCf cf, String itemId, double itemAmt) {
		return ArkItemRst.builder()
					.baseYymm(bssd)
					.gocId(cf.getGocId())
					.liabType(cf.getLiabType())
					.stStatus(cf.getStStatus())
					.endStatus(cf.getEndStatus())
					.newContYn(cf.getNewContYn())
					.arkRunsetId(cf.getArkRunsetId())
					.runsetId(cf.getRunsetId())
					.itemId(itemId)
					.itemAmt(itemAmt)
					.coaId("CF")
					.seqNum(0)
					.lastModifiedBy(GmvConstant.getLastModifier())
					.lastModifiedDate(LocalDateTime.now())
					.build()
					;
	}
	
	private static ArkItemRst buildItemRst(String bssd, ArkFutureCf cf, String itemId, double itemAmt, String remark) {
		return ArkItemRst.builder()
					.baseYymm(bssd)
					.gocId(cf.getGocId())
					.liabType(cf.getLiabType())
					.stStatus(cf.getStStatus())
					.endStatus(cf.getEndStatus())
					.newContYn(cf.getNewContYn())
					.arkRunsetId(cf.getArkRunsetId())
					.runsetId(cf.getRunsetId())
					.itemId(itemId)
					.itemAmt(itemAmt)
					.coaId("CF")
					.seqNum(0)
					.remark(remark)
					.lastModifiedBy(GmvConstant.getLastModifier())
					.lastModifiedDate(LocalDateTime.now())
					.build()
					;
	}
}
