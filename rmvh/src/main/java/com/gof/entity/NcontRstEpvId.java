package com.gof.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import javax.persistence.Embeddable;

import lombok.Getter;

/**
 * NewcontRstEpvId generated by hbm2java
 */
@Embeddable
@Getter
public class NcontRstEpvId implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;
	
	private String baseYymm;
	private String ctrPolno;
	private String prodCd;
		
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}