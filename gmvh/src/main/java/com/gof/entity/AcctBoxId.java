package com.gof.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.gof.enums.EBoolean;
import com.gof.enums.ELiabType;
import com.gof.enums.ETiming;

import lombok.Getter;

/**
 * AcctBoxId generated by hbm2java
 */
@Embeddable
@Getter
public class AcctBoxId implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;
	
	private String baseYymm;
	private String gocId;
	
	@Enumerated(EnumType.STRING)
	private ELiabType liabType;
	
	private String stStatus;
	private String endStatus;
	
	@Enumerated(EnumType.STRING)
	private EBoolean newContYn;
	private String cfKeyId;
	
//	private Integer cfColSeq;
//	private String cfId;
	
//	@Enumerated(EnumType.STRING)
//	private ECfType cfType;

	@Enumerated(EnumType.STRING)
	private ETiming cfTiming;
	
	@Enumerated(EnumType.STRING)
	private EBoolean outflowYn;
	
	private Double cfMonthNum;
	
	private String runsetId;
	private String journalId;
	private String rollFwdType;
	
//	@Enumerated(EnumType.STRING)
//	private EBoxModel boxId;
	
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
	 	 	.append(stStatus).append(",")
	 	 	.append(endStatus).append(",")
	 	 	.append(newContYn).append(",")
	 	 	.append(cfKeyId).append(",")
	 	 	.append(cfTiming).append(",")
	 	 	.append(outflowYn).append(",")
	 	 	.append(cfMonthNum).append(",")
	 	 	.append(runsetId).append(",")
	 	 	.append(journalId).append(",")
	 	 	.append(rollFwdType).append(",")
	 	 	.toString();
	}
	
}