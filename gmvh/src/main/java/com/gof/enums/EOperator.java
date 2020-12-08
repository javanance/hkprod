package com.gof.enums;

import lombok.Getter;

@Getter
public enum EOperator {

	PLUS	(1.0)
,   MINUS	(-1.0)	
;
	private double adj;
	
	private EOperator(double adj) {
		this.adj = adj;
	}
	
	
}
