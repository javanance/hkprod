package com.gof.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.gof.enums.EBoolean;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;
import com.gof.enums.ETiming;

import lombok.Getter;

/**
 * CfLv2GocId generated by hbm2java
 */
@Embeddable
@Getter
public class CfLv1GocId implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;
	
	private String baseYymm;
	private String gocId;
	
	@Enumerated(EnumType.STRING)
	private ELiabType liabType;
	
	@Enumerated(EnumType.STRING)
	private EContStatus stStatus;
	
	@Enumerated(EnumType.STRING)
	private EContStatus endStatus;
	
	@Enumerated(EnumType.STRING)
	private EBoolean newContYn;
	
	private String subKey;
	private String runsetId;
	private String cfKeyId;
	
	@Enumerated(EnumType.STRING)
	private ETiming cfTiming;
	
	@Enumerated(EnumType.STRING)
	private EBoolean outflowYn;
	
//	private Double cfMonthNum;
	private Integer setlAftPassMmcnt;
	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder 	builder = new StringBuilder();
	 	 return builder
	 		.append(baseYymm).append(",")
	 	 	.append(gocId).append(",")
	 	 	.append(runsetId).append(",")
	 	 	.append(stStatus).append(",")
	 	 	.append(endStatus).append(",")
	 	 	.append(newContYn).append(",")
	 	 	.append(cfKeyId).append(",")
	 	 	.append(cfTiming).append(",")
//	 	 	.append(cfType).append(",")
	 	 	.append(outflowYn).append(",")
	 	 	.append(setlAftPassMmcnt).append(",")
	 	 	.toString();
	}
	
}
