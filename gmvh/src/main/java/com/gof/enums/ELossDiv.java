package com.gof.enums;

import java.util.function.Function;

import com.gof.entity.RstCsm;

public enum ELossDiv {

	
    ALLO			(-1.0	, EAlloDiv.RATIO, 		s->s.getCalcCsmAmt())
  , REV_ALLO		(1.0	, EAlloDiv.RATIO, 		s->s.getCalcCsmAmt())
  , CLOSE			(-1.0	, EAlloDiv.NA, 			s->s.getCalcCsmAmt())
  , DELTA_ALLO		(-1.0	, EAlloDiv.RATIO, 		s->s.getDeltaCalcCsmAmt())
  , LOSS_TVOM		(-1.0	, EAlloDiv.LOSS_TVOM, 	s->s.getDeltaCalcCsmAmt())
  , LOSS_FACE		(-1.0	, EAlloDiv.LOSS_FACE, 	s->s.getDeltaCalcCsmAmt())
  , LOSS_RA			(-1.0	, EAlloDiv.LOSS_RA,  	s->s.getDeltaCalcCsmAmt())
  , REVERSE_CLOSE	(1.0	, EAlloDiv.NA, 			s->s.getBoxAmt())
  , REVERSE_DELTA	(1.0	, EAlloDiv.NA, 			s->s.getBoxAmt())
  , NA				(1.0	, EAlloDiv.NA, 			s-> 0.0)
;

	private double signAdj;
	private EAlloDiv alloDiv;
	private Function<RstCsm, Double> fn;

	private ELossDiv(double signAdj, EAlloDiv alloDiv, Function<RstCsm, Double> fn) {
		this.signAdj = signAdj;
		this.alloDiv = alloDiv;
		
		this.fn = fn;
	}

	public double getSignAdj() {
		return signAdj;
	}

	public Function<RstCsm, Double> getFn() {
		return fn;
	}

	public EAlloDiv getAlloDiv() {
		return alloDiv;
	}
	
	
}
