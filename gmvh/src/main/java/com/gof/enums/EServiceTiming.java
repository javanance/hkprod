package com.gof.enums;

import java.util.function.Predicate;

import com.gof.entity.CfLv4Df;

import lombok.Getter;

@Getter
public enum EServiceTiming {

	ALL			(true, true, 	cf-> true)
,	CURRENT		(true, false, 	cf -> !cf.isFutureCf() )
,   FUTURE		(false, true,	cf -> cf.isFutureCf() )
;
	
	private boolean isCurrent;
	private boolean isFuture;
	private Predicate<CfLv4Df>	predicate;
	
	private EServiceTiming(boolean isCurrent, boolean isFuture, Predicate<CfLv4Df> predicate) {
		this.isFuture = isFuture;
		this.isCurrent =isCurrent;
		this.predicate = predicate;
	}
	
	public boolean isApplied(CfLv4Df cf) {
		return predicate.test(cf);
	}
	
}
