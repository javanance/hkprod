package com.gof.factory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

import com.gof.entity.CfLv1Goc;
import com.gof.entity.RstEpvNgoc;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FacRstEpv {
	
	public static RstEpvNgoc createNewgoc( CfLv1Goc cf, Map<Double, Double> currMap, Function<CfLv1Goc, Double> tenorAdjFn){
		String remark =tenorAdjFn.apply(cf) +"_"+ currMap.get(tenorAdjFn.apply(cf));
		
//		log.info("Remark Newgoc : {},{},{}", remark, currMap.size());
		return buildNewgoc(cf, cf.getEpvWithFn(currMap, tenorAdjFn), remark);
	}
		
	private static RstEpvNgoc buildNewgoc(CfLv1Goc cf, double epv, String remark){
        return	RstEpvNgoc.builder()
        		.baseYymm(cf.getBaseYymm())
        		.gocId(cf.getGocId())
        		.liabType(cf.getLiabType())
        		.stStatus(cf.getStStatus())
        		.endStatus(cf.getEndStatus())
        		.newContYn(cf.getNewContYn())
        		.runsetId(cf.getRunsetId())
//        		.cfKeyId(cf.getCfKeyId())
//        		.cfTiming(cf.getCfTiming())
//        		.outflowYn(cf.getOutflowYn())
//        		.cfMonthNum(cf.getCfMonthNum())
//        		.cfColSeq(cf.getCfColSeq())
//        		.cfId(cf.getCfId())
//        		.cfType(cf.getCfType())
        		.cfAmt(cf.getCfAmt())
        		.epvAmt(epv)
        		.remark(remark)
        		.lastModifiedBy(GmvConstant.getLastModifier())
        		.lastModifiedDate(LocalDateTime.now())
        		.build();
	}
	
}
