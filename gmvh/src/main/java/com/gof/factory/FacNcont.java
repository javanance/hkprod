package com.gof.factory;

import java.time.LocalDateTime;
import java.util.Map;

import com.gof.entity.NcontRstEpv;
import com.gof.entity.NcontRstFlat;
import com.gof.entity.NcontRstRa;
import com.gof.entity.NcontRstTvog;
import com.gof.entity.RawRa;
import com.gof.entity.RawTvog;
import com.gof.infra.GmvConstant;

public class FacNcont {

	public static NcontRstFlat build(NcontRstEpv ncont, Map<String, Double> raMap, Map<String, Double> tvogMap) {
		
		double raAmt = raMap.getOrDefault(ncont.getCtrPolno(), 0.0) ;
		double tvogAmt = tvogMap.getOrDefault(ncont.getCtrPolno(), 0.0);
		double calcCsm = -1.0*(ncont.getEpvAmt() + raAmt + tvogAmt);
		double csmAmt  = Math.max(calcCsm, 0.0);
		double lossAmt = -1.0* Math.min(calcCsm, 0.0);
		
		return NcontRstFlat.builder()
						.baseYymm(ncont.getBaseYymm())
						.ctrPolno(ncont.getCtrPolno())
						.prodCd(ncont.getProdCd())
						.outCfAmt(ncont.getOutCfAmt())
						.inCfAmt(ncont.getInCfAmt())
						.cfAmt(ncont.getCfAmt())
						.outEpvAmt(ncont.getOutEpvAmt())
						.inEpvAmt(ncont.getInEpvAmt())
						.epvAmt(ncont.getEpvAmt())
						.tvom(ncont.getCfAmt() - ncont.getEpvAmt())
						.raAmt(raAmt)
						.tvogAmt(tvogAmt)
						.csmAmt(csmAmt)
						.lossAmt(lossAmt)
						.fvAmt(0.0)
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}
	
	
	public static NcontRstFlat buildConv(NcontRstEpv ncont, Map<String, Double> raMap, Map<String, Double> tvogMap, Map<String, Double> fvMap) {
		
		double raAmt = raMap.getOrDefault(ncont.getCtrPolno(), 0.0) ;
		double tvogAmt = tvogMap.getOrDefault(ncont.getCtrPolno(), 0.0);
		double fvAmt = fvMap.getOrDefault(ncont.getCtrPolno(), 0.0);
		
		double fulfillAmt = ncont.getEpvAmt() + raAmt + tvogAmt;
		double calcCsm = fvAmt - fulfillAmt;
		
		return NcontRstFlat.builder()
						.baseYymm(ncont.getBaseYymm())
						.ctrPolno(ncont.getCtrPolno())
						.prodCd(ncont.getProdCd())
						.outCfAmt(ncont.getOutCfAmt())
						.inCfAmt(ncont.getInCfAmt())
						.cfAmt(ncont.getCfAmt())
						.outEpvAmt(ncont.getOutEpvAmt())
						.inEpvAmt(ncont.getInEpvAmt())
						.epvAmt(ncont.getEpvAmt())
						.tvom(ncont.getCfAmt() - ncont.getEpvAmt())
						.csmAmt(Math.max(calcCsm, 0.0))
						.lossAmt(-1.0 * Math.min(calcCsm, 0.0))
						.raAmt(raAmt)
						.tvogAmt(tvogAmt)
						.fvAmt(fvAmt)
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}
	
	public static NcontRstRa buildRa(RawRa ra, String calcType) {
		return NcontRstRa.builder()
						.baseYymm(ra.getBaseYymm())
						.ctrPolno(ra.getCtrPolno())
						.prodCd(ra.getProdCd())
						.raCalcType(calcType)
						.raRatio(0.0)
						.raAmt(ra.getRaAmt())
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}

	
	public static NcontRstRa buildRa(NcontRstRa ra, String calcType) {
		return NcontRstRa.builder()
						.baseYymm(ra.getBaseYymm())
						.ctrPolno(ra.getCtrPolno())
						.prodCd(ra.getProdCd())
						.raCalcType(calcType)
						.raRatio(0.0)
						.raAmt(ra.getRaAmt())
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}

	public static NcontRstTvog buildTvog(RawTvog tvog, String calcType) {
		return NcontRstTvog.builder()
						.baseYymm(tvog.getBaseYymm())
						.ctrPolno(tvog.getCtrPolno())
						.prodCd("ALL")
						.tvogCalcType(calcType)
						.tvogRatio(0.0)
						.tvogAmt(tvog.getTvogAmt())
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}
	
	public static NcontRstTvog buildTvog(NcontRstTvog tvog, String calcType) {
		return NcontRstTvog.builder()
						.baseYymm(tvog.getBaseYymm())
						.ctrPolno(tvog.getCtrPolno())
						.prodCd("ALL")
						.tvogCalcType(calcType)
						.tvogRatio(0.0)
						.tvogAmt(tvog.getTvogAmt())
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}
	
	public static NcontRstEpv buildEpv(NcontRstEpv epv) {
		return NcontRstEpv.builder()
						.baseYymm(epv.getBaseYymm())
						.ctrPolno(epv.getCtrPolno())
						.prodCd(epv.getProdCd())
						.cfAmt(epv.getCfAmt())
						.epvAmt(epv.getEpvAmt())
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}
	
	public static NcontRstEpv buildEpv(NcontRstEpv epv, NcontRstEpv outEpv) {
		return NcontRstEpv.builder()
						.baseYymm(epv.getBaseYymm())
						.ctrPolno(epv.getCtrPolno())
						.prodCd(epv.getProdCd())
						.outCfAmt(outEpv.getCfAmt())
						.cfAmt(epv.getCfAmt())
						.outEpvAmt(outEpv.getEpvAmt())
						.epvAmt(epv.getEpvAmt())
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}
}
