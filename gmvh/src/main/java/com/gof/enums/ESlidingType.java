package com.gof.enums;

import java.util.function.BiFunction;

import com.gof.util.DateUtil;

public enum ESlidingType {
	BOP			(1,  0,	(s,e)-> s )
,   EOP			(2,  0,	(s,e)-> e)	
,   SLIDING_1	(11, 1,	(s,e)-> DateUtil.addMonthToString(s, 1))
,   SLIDING_2	(12, 2,	(s,e)-> DateUtil.addMonthToString(s, 2))
,   SLIDING_3	(13, 3,	(s,e)-> DateUtil.addMonthToString(s, 3))
,   SLIDING_4	(14, 4,	(s,e)-> DateUtil.addMonthToString(s, 4))
,   SLIDING_5	(15, 5,	(s,e)-> DateUtil.addMonthToString(s, 5))
,   SLIDING_6	(16, 6,	(s,e)-> DateUtil.addMonthToString(s, 6))
,   SLIDING_7	(17, 7,	(s,e)-> DateUtil.addMonthToString(s, 7))
,   SLIDING_8	(18, 8,	(s,e)-> DateUtil.addMonthToString(s, 8))
,   SLIDING_9	(19, 9,	(s,e)-> DateUtil.addMonthToString(s, 9))
,   SLIDING_10	(20, 10,(s,e)-> DateUtil.addMonthToString(s, 10))
,   SLIDING_11	(21, 11,(s,e)-> DateUtil.addMonthToString(s, 11))
,   SLIDING_12	(22, 12,(s,e)-> DateUtil.addMonthToString(s, 12))
,   NA			(99, 0,	(s,e)-> e)
,   GOC_INIT	(99, 0,	(s,e)-> e)
;
	private int order;
	private int slidingNum ;
	private BiFunction<String, String, String> keyFn;

	
	private ESlidingType(int order, int slidingNum, BiFunction<String, String, String> keyFn) {
		this.order 		= order;
		this.slidingNum = slidingNum;
		this.keyFn 		= keyFn;
	}
	
	public int getOrder() {
		return order;
	}

	public int getSlidingNum() {
		return slidingNum;
	}
	public BiFunction<String, String, String> getKeyFn() {
		return keyFn;
	}


}
