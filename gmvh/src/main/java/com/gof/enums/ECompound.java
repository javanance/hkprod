package com.gof.enums;

import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ECompound {
		Simply(1){
			public double getDf(double irRate, double timeFactor) {
				return 1/ (1+irRate * timeFactor );
			}
		} 
	,	BiMonthly(24){
//			public double getDf(double irRate, LocalDate baseDate, EBiMonth matCd) {
//				return Math.pow((1+irRate /getFrequency()), -1*matCd.getTimeFactor()*getFrequency() );
//			}
		}
	
	,	Monthly(12) {
//			public double getDf(double irRate, LocalDate baseDate, EBiMonth matCd) {
//				return Math.pow((1+irRate / getFrequency()), -1*matCd.getMonthNum()) * (1+irRate/24.0);
//			}
		}
	,	Quarterly(4)
	, 	SemiAnnu(2)
	, 	Annualy(1)
	,	Continously(Integer.MAX_VALUE){
			public double getDf(double irRate, double timeFactor) {
				return Math.exp(-1*irRate * timeFactor);
			}
		}
	
	;
	
	private int frequency;
	
	private ECompound(int freq) {
		this.frequency = freq;
	}
	
	public int getFrequency() {
		return frequency;
	}
//	************************Core Method*************************
	public double getDf(double irRate, double timeFactor) {
		return Math.pow((1+irRate /getFrequency()), -1*timeFactor*getFrequency() );
	}
	
	
//	************************ Method Variation*************************
	public double getDf(double irRate, LocalDate baseDate, LocalDate cfDate, EDayCount dayCount) {
		return getDf(irRate, dayCount.getTf(baseDate, cfDate));
	}
	
	public double getDf(double irRate, LocalDate baseDate, LocalDate cfDate) {
		return getDf(irRate, EDayCount.ACT_365.getTf(baseDate, cfDate));
	}
	
	public double getDf( double irRate, LocalDate baseDate, EMonth matCd) {
		return getDf(irRate,  EDayCount.MONTH.getTf(baseDate, matCd));
	}
	
//	public double getDf(double irRate, LocalDate baseDate, EBiMonth matCd) {
//		return 0.0;
//	}
	
	
	
}
