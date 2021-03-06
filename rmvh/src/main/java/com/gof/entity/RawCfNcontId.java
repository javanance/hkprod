package com.gof.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.gof.enums.EBoolean;
import com.gof.enums.ETiming;

import lombok.Getter;

/**
 * CfLv3RealId generated by hbm2java
 */
@Embeddable
@Getter
public class RawCfNcontId implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;
	
	private String setlYm;
	private String exeIdno;
	private String rsDivId;
	private String ctrPolno;
	private String prodCd;
	private Integer setlAftPassMmcnt;					
	private Integer cfColSeq;
	private String cfId;
	
	@Enumerated(EnumType.STRING)
	private EBoolean outflowYn;
	
	@Enumerated(EnumType.STRING)
	private ETiming cfTiming;
	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
