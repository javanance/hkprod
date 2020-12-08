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
 * CfLv1RawId generated by hbm2java
 */
@Embeddable
@Getter
public class RawCfId implements java.io.Serializable {

	private static final long serialVersionUID = -8151467682976876533L;
	
	private String driveYm;
	private String setlYm;
	@Enumerated(EnumType.STRING)
	private ELiabType liabType;
	private String exeIdno;
	private String rsDivId;
	private String csmGrpCd;
	private String bemmStcd;
	private String emmStcd;
	private String ctrDvcd;
	private String subKey;
	private Integer setlAftPassMmcnt;
	
	private String cfKeyId;
	
	@Enumerated(EnumType.STRING)
	private ETiming cfTiming;
	
	@Enumerated(EnumType.STRING)
	private EBoolean outflowYn;
    
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
	 		 .append(driveYm).append(",")
	 		.append(setlYm).append(",")
	 		.append(liabType).append(",")
	 		.append(exeIdno).append(",")
	 	 	.append(rsDivId).append(",")
	 	 	.append(csmGrpCd).append(",")
	 	 	.append(bemmStcd).append(",")
	 	 	.append(emmStcd).append(",")
	 	 	.append(ctrDvcd).append(",")
	 	 	.append(setlAftPassMmcnt).append(",")
	 	 	.append(cfKeyId).append(",")
	 	 	.append(cfTiming).append(",")
	 	 	.append(outflowYn).append(",")
	 	 	.toString();
	}
	
	
}
