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
@IdClass(RawRaGocId.class)
@Table(name = "RAW_GOC_EL")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawElGoc implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;

	@Id private String baseYymm;
	@Id private String gocId;
	
//	@Enumerated(EnumType.STRING)
//	@Id private ERollFwdRa rollFwdType;
//	@Id private String runsetId;
	@Id private String rsDivId;
	
	private Double elAmt;
//	private String remark;
	
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
//	public String getRDivId() {
//		return rollFwdType.name();
//	}
}
