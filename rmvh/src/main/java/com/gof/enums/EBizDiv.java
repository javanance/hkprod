package com.gof.enums;

import java.util.function.Function;
import java.util.function.Predicate;

import com.gof.interfaces.INcontRst;

public enum EBizDiv {
//,	G_TRUE	(0, false, true)
//,	G_FALSE	(0, false, true)
	SETTLE	(1, s-> s.getCtrPolno() )
,   ANAL	(2, s-> s.getProdCd())	
;
	private int order;
	private Function<INcontRst, String> keyFn;
	private Predicate<INcontRst>		keyPredicate;
	
	private EBizDiv(int order, Function<INcontRst, String> keyFn) {
		this.order = order;
		this.keyFn = keyFn;
//		this.keyPredicate = keyPredicate;
	}
	
	public int getOrder() {
		return order;
	}

	public Function<INcontRst, String> getKeyFn() {
		return keyFn;
	}

	public Predicate<INcontRst> getKeyPredicate() {
		return keyPredicate;
	}
}
