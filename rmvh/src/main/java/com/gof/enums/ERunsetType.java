package com.gof.enums;

public enum ERunsetType {
//	PREV_CLOSING	( 9, EBoolean.Y, s-> s.getCtrPolno() )
//,   FIRST_NEW		(11, EBoolean.N, s-> s.getProdCd())	
//,   SECOND_NEW		(12, EBoolean.N, s-> s.getProdCd())	
//,   THIRD_NEW		(13, EBoolean.N, s-> s.getProdCd())	
//, 	PUB_CHANGE		(14, EBoolean.N, s-> s.getProdCd())
//,   PUB_CLOSING     (15, EBoolean.N, s-> s.getProdCd())
//, 	SERVICE_RELEASE (16, EBoolean.N, s-> s.getProdCd())
//,   SERVICE_CLOSING	(17, EBoolean.N, s-> s.getProdCd())	
//,   TIME_CHANGE		(21, EBoolean.N, s-> s.getProdCd())	
//,   ACTU_CHANGE		(22, EBoolean.N, s-> s.getProdCd())	
//,   INV_CHANGE		(23, EBoolean.N, s-> s.getProdCd())	
//,   FINC_CHANGE		(24, EBoolean.N, s-> s.getProdCd())	
//,   DISC_CHANGE		(25, EBoolean.N, s-> s.getProdCd())	
//,   CURR_CLOSING	(99, EBoolean.N, s-> s.getProdCd())
//, 	NA				(999, EBoolean.N, s-> s.getProdCd())
	
	PREV_CLOSING	( 9, false)
,   FIRST_NEW		(11, true)	
,   SECOND_NEW		(12, true)	
,   THIRD_NEW		(13, true)	
, 	PUB_CHANGE		(14, false)
,   PUB_CLOSING     (15, false)
, 	SERVICE_RELEASE (16, false)
,   SERVICE_CLOSING	(17, false)	
,   TIME_CHANGE		(21, false)	
,   ACTU_CHANGE		(22, false)	
,   INV_CHANGE		(23, false)	
,   FINC_CHANGE		(24, false)	
,   DISC_CHANGE		(25, false)	
,   CURR_CLOSING	(99, false)
, 	NA				(999, false)
	;

	private int order;
	private boolean newContYn;
//	private Function<INcontRst, String> keyFn;
//	private Predicate<INcontRst>		keyPredicate;
	
	private ERunsetType(int order,boolean newContYn) {
		this.order = order;
		this.newContYn = newContYn;
	}
	
	public int getOrder() {
		return order;
	}

	public boolean isNewContYn() {
		return newContYn;
	}
	
	
//	TODO : deprecated!!!!
//	public EBoolean getPrevYn() {
//		return prevYn;
//	}

//	public Function<INcontRst, String> getKeyFn() {
//		return keyFn;
//	}
//
//	public Predicate<INcontRst> getKeyPredicate() {
//		return keyPredicate;
//	}
}
