package com.gof.enums;

import java.util.function.Function;
import java.util.function.Predicate;

import com.gof.interfaces.INcontRst;

public enum ECalcMethod {
	   	RA_INT					(2, false, v-> v, 					s-> s.getProdCd())
   	,   RA_PREV_REC				(2, false, v-> v, 					s-> s.getProdCd())
   	,   RA_PREV_RAW				(2, false, v-> v, 					s-> s.getProdCd())
	
   	,   TVOG_INT				(2, false, v-> v, 					s-> s.getProdCd())
	,   TVOG_PREV_REC			(2, false, v-> v, 					s-> s.getProdCd())
	,   TVOG_PREV_RAW			(2, false, v-> v, 				  	s-> s.getProdCd())
	
	,   AOCI_PREV				(2, true,  v-> v, 					s-> s.getProdCd())
	
	,	CSM_PREV				(1, false, v-> Math.max(v, 0.0), 	s-> s.getCtrPolno() )
	,	CSM_INIT				(1, false, v-> v, 					s-> s.getCtrPolno() )
	,   CSM_INT					(2, false, v-> v,					s-> s.getProdCd())
	,   CSM_LOSS_ALLO			(2, false, v-> v, 					s-> s.getProdCd())
	,   CSM_REVERSAL			(2, false, v-> v, 					s-> s.getProdCd())
	,   CSM_RESERVE				(2, false, v-> v, 					s-> s.getProdCd())
	,   CSM_RELEASE_1			(2, false, v-> v, 					s-> s.getProdCd())
	
	, 	DAC_PREV				(2, false, v-> v,					s-> s.getProdCd())
	,   DAC_RELEASE_1			(2, false, v-> v, 					s-> s.getProdCd())
	,   DAC_CLOSE				(2, false, v-> v, 					s-> s.getProdCd())				//TODO :!!!!
	
	,   LOSS_PREV_ALLO			(2, false,  v-> v, 					s-> s.getProdCd())
	,   LOSS_FACE_PREV_ALLO		(2, false,  v-> v, 					s-> s.getProdCd())
	,   LOSS_TVOM_PREV_ALLO		(2, false,  v-> v, 					s-> s.getProdCd())
	,   LOSS_RA_PREV_ALLO		(2, false,  v-> v, 					s-> s.getProdCd())
	
	,   DELTA_LOSS_ALLO			(2, true,  v-> v, 					s-> s.getProdCd())
	,   DELTA_LOSS_ALLO_FACE	(2, true,  v-> v, 					s-> s.getProdCd())
	,   DELTA_LOSS_ALLO_TVOM	(2, true,  v-> v, 					s-> s.getProdCd())
	,   DELTA_LOSS_ALLO_RA		(2, true,  v-> v, 					s-> s.getProdCd())
	,   CURR_LOSS_CLOSE			(2, true,  v-> v, 					s-> s.getProdCd())
	
	
;
	
	
	private int order;
	private boolean updateYn;
	private Function<INcontRst, String> keyFn;
	private Function<Double, Double> adjFn;
	private Predicate<INcontRst>		keyPredicate;
	
	private ECalcMethod(int order, boolean updateYn, Function<Double, Double> adjFn, Function<INcontRst, String> keyFn) {
		this.order = order;
		this.updateYn = updateYn;
		this.adjFn = adjFn;
		this.keyFn = keyFn;
//		this.keyPredicate = keyPredicate;
	}
	
	public int getOrder() {
		return order;
	}

	public Function<Double, Double> getAdjFn() {
		return adjFn;
	}
	
	public boolean getUpdateYn() {
		return updateYn;
	}

	public Function<INcontRst, String> getKeyFn() {
		return keyFn;
	}

	public Predicate<INcontRst> getKeyPredicate() {
		return keyPredicate;
	}
}
