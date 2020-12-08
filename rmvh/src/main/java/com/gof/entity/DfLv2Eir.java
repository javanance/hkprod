package com.gof.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.gof.enums.EBoolean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * RstEir generated by hbm2java
 */
@Entity
@Table(name = "DF_LV2_EIR")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DfLv2Eir implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;
	
	@Id
	@SequenceGenerator(name = "COMMON_SEQ", sequenceName = "COMMON_SEQ", initialValue = 1, allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMMON_SEQ" )
	private Long seqId;

	private String baseYymm;
	private String gocId;
	
	@Enumerated(EnumType.STRING)
	private EBoolean newContYn;
	
	private Double eir;
	private Double fincEpvAmt;
	private Double targetRunSysAmt;
	private Double targetRunOci;
	private Double deltaOci;
	private Double fincOci;
	private Double targetOci;
	private Double errorAmt;
	
	private String remark;
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
		 StringBuilder 	builder = new StringBuilder();
	 	 return builder
	 		.append(baseYymm).append(",")
	 	 	.append(gocId).append(",")
	 	 	.append(newContYn).append(",")
	 	 	.append(eir).append(",")
	 	 	.append(fincEpvAmt).append(",")
	 	 	.append(targetRunSysAmt).append(",")
	 	 	.append(targetOci).append(",")
	 	 	.append(remark).append(",")
	 	 	.toString(); 
	}
}
