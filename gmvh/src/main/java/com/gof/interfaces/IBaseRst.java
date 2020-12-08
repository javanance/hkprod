package com.gof.interfaces;

import com.gof.enums.EBoolean;
import com.gof.enums.EContStatus;

public interface IBaseRst extends IGocKey{
	public String getBaseYymm();
	public String getGocId();
	public EContStatus getStStatus();
	public EContStatus getEndStatus();
	public EBoolean getNewContYn();
	
	public Double getCfAmt();
	public Double getEpvAmt();
	
//	public default String getGocPk() {
//		StringBuilder 	builder = new StringBuilder();
//	 	 return builder
//	 		.append(getBaseYymm()).append(",")
//	 	 	.append(getGocId()).append(",")
//	 	 	.append(getStStatus()).append(",")
//	 	 	.append(getEndStatus()).append(",")
//	 	 	.append(getNewContYn()).append(",")
//	 	 	.toString();
//	}
}
