package com.gof.enums;

import java.util.function.Function;
import java.util.function.Predicate;

import com.gof.interfaces.INcontRst;

public enum ECalcType {

		BOX			(10, true,  	s-> s.getCtrPolno() )
//	,	REF			(20, true,	s-> s.getProdCd())
	,	 UL_REF		(21, false,	s-> s.getProdCd())
	, 	WATERFALL	(30, true,	s-> s.getProdCd())
	, 	ROLL_FWD	(40, true,	s-> s.getProdCd())
	, 	PREV_RST	(41, true,	s-> s.getProdCd())
	, 	PRIOR_CLOSE	(42, true,	s-> s.getProdCd())
	, 	DELTA_SUM	(43, false,	s-> s.getProdCd())
;
	private int order;
	private boolean boxYn;
	private Function<INcontRst, String> keyFn;
	private Predicate<INcontRst>		keyPredicate;
	
	private ECalcType(int order, boolean boxYn,Function<INcontRst, String> keyFn) {
		this.order = order;
		this.boxYn = boxYn;
		this.keyFn = keyFn;
//		this.keyPredicate = keyPredicate;
	}
	
	public int getOrder() {
		return order;
	}
	public boolean isBoxYn() {
		return boxYn;
	}

	public Function<INcontRst, String> getKeyFn() {
		return keyFn;
	}

	public Predicate<INcontRst> getKeyPredicate() {
		return keyPredicate;
	}
}
