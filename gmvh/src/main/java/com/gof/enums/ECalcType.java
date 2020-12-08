package com.gof.enums;

import java.util.function.Function;
import java.util.function.Predicate;

import com.gof.interfaces.INcontRst;

public enum ECalcType {

	BOX			(1, true,  	s-> s.getCtrPolno() )
,	REF			(2, true,	s-> s.getProdCd())
, 	WATERFALL	(2, true,	s-> s.getProdCd())
, 	NATIVE		(2, true,	s-> s.getProdCd())
, 	DELTA_SUM	(2, false,	s-> s.getProdCd())
//,   DEFAULT		(2, false, 	s-> s.getProdCd())	

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
