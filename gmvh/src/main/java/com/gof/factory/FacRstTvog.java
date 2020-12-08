package com.gof.factory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FacRstTvog {
	
//	public static RstTvog factory(String bssd, String gocId, ERollFwdTvog rollFwd, double tvogAmt){
//        return factory(bssd, gocId, rollFwd, 0.0, tvogAmt);
//	}
//	
//	public static RstTvog factoryDelta(String bssd, String gocId, ERollFwdTvog rollFwd, double deltaAmt){
//		 return factory(bssd, gocId, rollFwd, deltaAmt, 0.0);
//	}
//	
//
//	public static RstTvog factory(String bssd, String gocId, ERollFwdTvog rollFwd, double delta, double tvogAmt){
//        return	RstTvog.builder()
//        		.baseYymm(bssd)
//        		.gocId(gocId)
//    			.rollFwdType(rollFwd)
//    			.seq(rollFwd.getSeq())
//    			.operatorType(EOperator.PLUS)
//    			.deltaTvogAmt(delta)
//    			.tvogAmt(tvogAmt)
////    			.remark(remark)
//    			.lastModifiedBy("GMV")
//    			.lastModifiedDate(LocalDateTime.now())
//    			.build()
//    			;
//	}
}
