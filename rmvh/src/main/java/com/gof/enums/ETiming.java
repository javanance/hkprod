package com.gof.enums;

import lombok.Getter;

@Getter
public enum ETiming {

	START	(-1)
,   MID		(-0.5)	
,	END		(0)

;
	private double adj;
	
	
	private ETiming(double adj) {
		this.adj = adj;
	}
	
}
