package com.gof.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import javax.persistence.Embeddable;

import lombok.Getter;

/**
 * CfLv1RawId generated by hbm2java
 */
@Embeddable
@Getter
public class RawCfDetailId implements java.io.Serializable {

	private static final long serialVersionUID = -8151467682976876533L;
	
	private String setlYm;
	private String exeIdno;
	private String rsDivId;
	private String csmGrpCd;
	private String bemmStcd;
	private String emmStcd;
	private String ctrDvcd;
	private String subKey;
	private Integer setlAftPassMmcnt;
	private Integer cfColSeq;
	private String cfId;
	
	private String cfTiming;

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
}
