package com.gof.enums;

import lombok.Getter;

@Getter
public enum EGocStatus {
	
	 STAY_PROFIT	(false, false, false, true)
	,PROFIT_TO_LOSS (false, true, true, false)
	,LOSS_TO_PROFIT (true, false, true, true)
	,STAY_LOSS		(true, true, true, false)
;
	
	private boolean fromLoss;
	private boolean toLoss;
	private boolean isCsmReversal;
//	private boolean isLossReversal;
	private boolean isCsmRelease;
	
	
//	private EEventType eventType;
	
	private EGocStatus(boolean fromLoss, boolean toLoss, boolean isCsmReversal, boolean isCsmRelease) {
		this.fromLoss =fromLoss;
		this.toLoss=toLoss;
		this.isCsmReversal = isCsmReversal;
		this.isCsmRelease = isCsmRelease;
	}
	
	
	public boolean isLoss() {
		return toLoss;
	}
	
	
}
