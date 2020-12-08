package com.gof.entity;

import com.gof.interfaces.Rrunnable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinearRegResult implements Rrunnable {

	private String baseYymm;
	private String depVariable;
	private String indepVariable;
	private Double avgMonNum;
	private Double regConstant;
	private Double regCoef;
	private Double regRsqr;
	

	@Override
	public String toString() {
		return toString(",");
	}
	
	public String toString(String delimeter) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(baseYymm).append(delimeter)
			   .append(depVariable).append(delimeter)
			   .append(indepVariable).append(delimeter)
			   .append(avgMonNum).append(delimeter)
			   .append(regConstant).append(delimeter)
			   
			   
			   .append(regCoef).append(delimeter)
			   .append(regRsqr).append(delimeter)
//			   .append(lastModifiedBy).append(delimeter)
//			   .append(lastUpdateDate)
			   ;
		
		return builder.append("\n").toString();
	}
}
