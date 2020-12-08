package com.gof.interfaces;

import com.gof.enums.EBoolean;
import com.gof.enums.EContStatus;

public interface IGocKey {
	public String getBaseYymm();
	public String getGocId();
	public default EContStatus getStStatus() {return EContStatus.NORMAL ;};
	public default EContStatus getEndStatus(){return EContStatus.NORMAL ;};
	public default EBoolean getNewContYn(){return EBoolean.N ;};
	
	
	public default String getGocPk() {
		StringBuilder 	builder = new StringBuilder();
	 	 return builder
	 		.append(getBaseYymm()).append(",")
	 	 	.append(getGocId()).append(",")
	 	 	.append(getStStatus()).append(",")
	 	 	.append(getEndStatus()).append(",")
	 	 	.append(getNewContYn()).append(",")
	 	 	.toString();
	}
}
