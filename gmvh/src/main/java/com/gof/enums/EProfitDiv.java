package com.gof.enums;

import java.util.function.Predicate;

import lombok.Getter;

@Getter
public enum EProfitDiv {

	PROF	(s-> s >= 0.03)
,   ETC		(s-> s >= 0.0 && s < 0.03)	
,   LOSS	(s-> s <  0.0)	
;
	
	private Predicate<Double> predicate;
	
	private EProfitDiv(Predicate<Double> predicate) {
		this.predicate = predicate;
	}
	
	
	public String getProfitDiv(double profitRatio) {
		for(EProfitDiv ee : EProfitDiv.values()) {
			if( ee.getPredicate().test(profitRatio)) {
				return ee.name(); 
			}
		}
		return ETC.name();
	}
	
}
