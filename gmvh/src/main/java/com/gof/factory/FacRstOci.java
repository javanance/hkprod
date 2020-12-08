package com.gof.factory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FacRstOci {
	
	
//	public static RstOci convert( CfLv2Goc cf, Map<Double, Double> currMap, Map<String, Double> currSysMap, Function<CfLv2Goc, Double> tenorAdjFn){
//		double sysAmt = cf.getEirAplyEpv(currSysMap, tenorAdjFn);
//		double epvAmt = cf.getEpvWithFn(currMap, tenorAdjFn);
//		double ociAmt = epvAmt - sysAmt;
//		return factoryDelta(cf.getBaseYymm(), cf.getGocId(), ERollFwdType.OCI_CURR_CLOSE, ociAmt, "Convert");  
//	}
//	
//	public static double calcOci( CfLv2Goc cf, Map<Double, Double> currMap, Map<String, Double> currSysMap, Function<CfLv2Goc, Double> tenorAdjFn){
//		double sysAmt = cf.getEirAplyEpv(currSysMap, tenorAdjFn);
//		double epvAmt = cf.getEpvWithFn(currMap, tenorAdjFn);
//		double ociAmt = epvAmt - sysAmt;
//		return ociAmt;  
//	}
//	
//	public static RstOci merge(RstOci base, RstOci other){
//		double ociAmt = base.getOciAmt() + other.getOciAmt();
//		return factoryDelta(base.getBaseYymm(), base.getGocId(), base.getRollFwdType(), ociAmt, "Merge");  
//	}
//	
//	public static RstOci factoryDelta(String bssd, String gocId,  ERollFwdOci rollFwd, double delta, String remark){
//		return factory(bssd, gocId, rollFwd, delta, 0.0, "DELTA"+ remark);
//	}
//	
//	public static RstOci factory(String bssd, String gocId,  ERollFwdOci rollFwd, double delta, double ociAmt, String remark){
//		return RstOci.builder().baseYymm(bssd)
//						.gocId(gocId)
//						.rollFwdType(rollFwd)
//						.seq(rollFwd.getSeq())
//						.operatorType(EOperator.PLUS)
//						.deltaOciAmt(delta)
//						.ociAmt(ociAmt)
//						.remark(remark)
//						.lastModifiedBy("GMV")
//						.lastModifiedDate(LocalDateTime.now())
//						.build()
//						;
//	}
	
	
}
