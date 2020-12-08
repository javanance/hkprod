package com.gof.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * RstTvog generated by hbm2java
 */
@Entity
@IdClass(RawModifiedRetorRstId.class)
@Table(name = "RAW_GOC_MRA")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawModifiedRetroRst implements java.io.Serializable{
	private static final long serialVersionUID = -8151467682976876533L;

	@Id private String baseYymm;
	@Id private String gocId;

	private Double csmAmt;
	private Double lossAmt;
	
	@Column(name = "DAC1_AMT")
	private Double dac1Amt;
	
	@Column(name = "DAC2_AMT")
	private Double dac2Amt;
	
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
						.toString();
	}
}
