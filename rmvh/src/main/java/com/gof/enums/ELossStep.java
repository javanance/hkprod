package com.gof.enums;

public enum ELossStep {
	L1		(1,  true, 		false,	false)		//기시
,	L1_A	(1,  true, 		false,	true)		//기시
,	L1_S	(1,  true, 		false,	false)		//기시

,   L2		(2,  true, 		false,	false)		// 기시 + 신계약
, 	L2_A	(2,  true, 		false,	true)		//true  : 모든 CF 를 기준으로 Ratio 계산
, 	L2_S	(2,  true,		false,	false)		//false : LOSS 배분 대상만으로 구성됨

,   L3		(3,  false, 	true,	false)	
,   L4		(4,  false, 	false,	false)

, 	L5		(5,  false, 	false,	false)		//기말
, 	L5_A	(5,  false, 	false,	true)		//기말
, 	L5_S	(5,  false, 	false,	false)		//기말
	;

	private int order;
	private boolean prevYn;
	private boolean deltaYn;
	private boolean allCfYn;
	
	private ELossStep(int order, boolean prevYn, boolean deltaYn) {
		this.order = order;
		this.prevYn = prevYn;
		this.deltaYn = deltaYn;
	}
	
	private ELossStep(int order, boolean prevYn, boolean deltaYn, boolean allCfYn) {
		this.order = order;
		this.prevYn = prevYn;
		this.deltaYn = deltaYn;
		this.allCfYn = allCfYn;
	}
	
	public int getOrder() {
		return order;
	}
	public boolean prevYn() {
		return prevYn;
	}
	public boolean deltaYn() {
		return deltaYn;
	}
	public boolean allCfYn() {
		return allCfYn;
	}
	
}
