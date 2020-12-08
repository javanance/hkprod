package com.gof.ark.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import javax.persistence.Embeddable;

import lombok.Getter;

/**
 * CfLv1RawId generated by hbm2java
 */
@Embeddable
@Getter
public class ArkRawEpvId implements java.io.Serializable {

	private static final long serialVersionUID = -8151467682976876533L;
	
	private String driveYm;
	private String setlYm;
	private String exeIdno;
	private String rsDivId;
	private String csmGrpCd;
	private String bemmStcd;
	private String emmStcd;
	private String ctrDvcd;
	private String subKey;

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
	 		.append(setlYm).append(",")
	 		.append(exeIdno).append(",")
	 	 	.append(rsDivId).append(",")
	 	 	.append(csmGrpCd).append(",")
	 	 	.append(bemmStcd).append(",")
	 	 	.append(emmStcd).append(",")
	 	 	.append(ctrDvcd).append(",")
	 	 	.append(subKey).append(",")
	 	 	.toString();
	}
	
	
}
