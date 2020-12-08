package com.gof.enums;

import lombok.Getter;

@Getter
public enum ECfType {

      PREM		(1, true)
	, INS		(2, false)
	, INV		(3, true)
	, DAC		(4, true)
	, DAC1		(5, true)
	, DAC2		(6, false)
	, DMC		(7, false)
	, DCE		(8, false)
	, IDAC		(9, false)
	, IDMC		(10, false)
	, LOAN_NEW 	(11, true)
	, LOAN_RPAY (12, true)
	, LOAN_INT 	(13, true)
	, NA		(99, false)
	, ALL		(99, false)
	;
	
	private int order;
	private boolean isMoveToCsm;
	
	private ECfType(int order, boolean isMoveToCsm) {
		this.order = order;
		this.isMoveToCsm = isMoveToCsm;
	}
	
	
}
