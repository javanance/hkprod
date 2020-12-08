package com.gof.enums;

import java.util.function.DoubleBinaryOperator;

import lombok.Getter;
@Getter
public enum EValueDeco implements DoubleBinaryOperator {
	 
	 UNIT			(1 , "Unit"					, (value, lossRatio) -> value		 			)
//	,MINUS_UNIT		(2 , "-1 * Unit"			, (value, lossRatio) -> -1.0 * value			)
	,LOSS_RATIO 	(3 , "apply Loss_Ratio "	, (value, lossRatio) -> value * lossRatio		)
	,LOSS_RATIO_EX	(4 , "apply 1- Loss_Ratio"	, (value, lossRatio) -> value * (1 - lossRatio) )  
	,ZERO			(5 , "Zero"					, (value, lossRatio) -> 0.0						)
	;
	
	private int order;
	private String desc;
	private DoubleBinaryOperator binaryOperator;
//	private Function<AccountResult, Double> applyLossRatioFun;
	
	private EValueDeco(int order, String desc, DoubleBinaryOperator operator) {
		this.order =order;
		this.desc = desc;
		this.binaryOperator = operator;
//		this.applyLossRatioFun = fun;
	}

	@Override
    public double applyAsDouble(final double left, final double right) {
        return binaryOperator.applyAsDouble(left, right);
    }
	
//	@Override
//	public Double apply(AccountResult t) {
//		return applyLossRatioFun.apply(t);
//	}
	  
}
