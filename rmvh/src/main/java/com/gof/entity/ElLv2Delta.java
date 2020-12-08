package com.gof.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * RstRa generated by hbm2java
 */
@Entity
@IdClass(ElLv2DeltaId.class)
@Table(name = "EL_LV2_DELTA")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElLv2Delta implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;

	@Id private String baseYymm;
	@Id private String gocId;
	
	
//	@ManyToOne
//	@JoinColumn(name = "RUNSET_ID")
//	@Id private MstRunsetOther runset;
	@Id private String runsetId;
	private String deltaGroup;
	
	
	private Double elAmt;
	private Double prevElAmt;
	private Double deltaElAmt;
	
	private String priorDeltaGroup;
	
	private String lastModifiedBy;
	private LocalDateTime lastModifiedDate;
	
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
