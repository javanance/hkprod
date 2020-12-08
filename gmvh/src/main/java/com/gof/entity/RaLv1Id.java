package com.gof.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.gof.enums.ELiabType;

import lombok.Getter;

/**
 * RstRa generated by hbm2java
 */
@Embeddable
@Getter
public class RaLv1Id implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;

	private String baseYymm;
	private String gocId;
	
	@Enumerated(EnumType.STRING)
	private ELiabType liabType;
	
	
//	@ManyToOne
//	@JoinColumn(name = "RUNSET_ID")
//	private MstRunsetOther runset;
	private String runsetId;
	
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
		StringBuilder builder = new StringBuilder();
		return builder.append(baseYymm).append(",")
						.append(gocId).append(",")
						.append(runsetId).append(",")
						.toString();
	}
	
}
