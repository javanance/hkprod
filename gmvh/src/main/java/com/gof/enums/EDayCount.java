package com.gof.enums;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAdjusters;

public enum EDayCount {
		ACT_ACT(1){
			public double getTf(LocalDate baseDate, LocalDate cfDate) {
				double totalDayNum =0.0;
				if(baseDate.getYear()==cfDate.getYear()) {
					totalDayNum = baseDate.isLeapYear()? 365.0: 366.0;		
				}
				else {
					totalDayNum = Period.between(baseDate, baseDate.with(TemporalAdjusters.firstDayOfNextYear())).getDays() 
									+ Period.between(baseDate.with(TemporalAdjusters.firstDayOfNextYear()), cfDate).getDays() ;
				}
				return (double) Math.max(Period.between(baseDate, cfDate).getDays(), 0.0) / totalDayNum;
			}
		} 
	,	ACT_365(2)
	, 	ACT_360(3)
	,	E30_360(4){
			public double getTf(LocalDate baseDate, LocalDate cfDate) {
				return (double) Math.max(Period.between(baseDate, cfDate).getDays(), 0.0) / 360;
			}
		} 
	,	A30_360(5)
	, 	MONTH(6){
			public double getTf(LocalDate baseDate, EMonth matCd) {
				return matCd.getTimeFactor();
			}
//			public double getTf(LocalDate baseDate, EBiMonth matCd) {
//				return matCd.getTimeFactor();
//			}
		}
	;
	
	private int frequency;
	
	private EDayCount(int freq) {
		this.frequency = freq;
	}
	
//	public int getFrequency() {
//		return frequency;
//	}

	public double getTf(LocalDate baseDate, LocalDate cfDate) {
		return (double) Math.max(Period.between(baseDate, cfDate).getDays(), 0.0) / 365;
	}
	
	public double getTf(LocalDate baseDate, EMonth matCd) {
		return getTf(baseDate, baseDate.plusMonths(matCd.getMonthNum()));
	}
	
//	public double getTf(LocalDate baseDate, EBiMonth matCd) {
//		return getTf(baseDate, baseDate.plusMonths((long)matCd.getMonthNum()));
//	}
	
//	public double getTf(String matCd) {
//		return 0.0;
//	}
	
	
}
