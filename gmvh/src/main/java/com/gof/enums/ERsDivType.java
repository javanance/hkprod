package com.gof.enums;

import java.util.function.Function;
import java.util.function.Predicate;

import com.gof.interfaces.INcontRst;

public enum ERsDivType {
	PREV_CLOSING	( 9, EBoolean.Y, s-> s.getCtrPolno() )
,   FIRST_NEW		(11, EBoolean.Y, s-> s.getProdCd())	
,   SECOND_NEW		(12, EBoolean.Y, s-> s.getProdCd())	
,   THIRD_NEW		(13, EBoolean.Y, s-> s.getProdCd())	
, 	PUB_CHANGE		(14, EBoolean.Y, s-> s.getProdCd())
,   PUB_CLOSING     (15, EBoolean.Y, s-> s.getProdCd())
, 	SERVICE_RELEASE (16, EBoolean.Y, s-> s.getProdCd())
,   SERVICE_CLOSING	(17, EBoolean.Y, s-> s.getProdCd())	
,   TIME_CHANGE		(21, EBoolean.N, s-> s.getProdCd())	
,   ACTU_CHANGE		(22, EBoolean.N, s-> s.getProdCd())	
,   INV_CHANGE		(23, EBoolean.N, s-> s.getProdCd())	
,   FINC_CHANGE		(24, EBoolean.N, s-> s.getProdCd())	
,   DISC_CHANGE		(25, EBoolean.N, s-> s.getProdCd())	
,   CURR_CLOSING	(99, EBoolean.N, s-> s.getProdCd())
, 	NA				(999, EBoolean.N, s-> s.getProdCd())
	;

	private int order;
	private EBoolean prevYn;
	private Function<INcontRst, String> keyFn;
	private Predicate<INcontRst>		keyPredicate;
	
	
	private ERsDivType(int order, EBoolean prevYn, Function<INcontRst, String> keyFn) {
		this.order = order;
		this.prevYn = prevYn;
		this.keyFn = keyFn;
//		this.keyPredicate = keyPredicate;
	}
	
	public int getOrder() {
		return order;
	}
//	TODO : deprecated!!!!
//	public EBoolean getPrevYn() {
//		return prevYn;
//	}

	public Function<INcontRst, String> getKeyFn() {
		return keyFn;
	}

	public Predicate<INcontRst> getKeyPredicate() {
		return keyPredicate;
	}
}
