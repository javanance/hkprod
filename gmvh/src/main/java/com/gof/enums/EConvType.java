package com.gof.enums;

public enum EConvType {
	
    FULL_RETRO			(1, true)	
,	MODIFIED_RETRO		(2, false)
,	FV					(3, false)
,   AFTER_TRANSITION	(4, true)
;

	private int order;
	private boolean afterTransYn;
	
	private EConvType(int order, boolean afterTransYn ) {
		this.order = order;
		this.afterTransYn = afterTransYn;
	}
	
	public int getOrder() {
		return order;
	}

	public boolean isAfterTransYn() {
		return afterTransYn;
	}
	
}
