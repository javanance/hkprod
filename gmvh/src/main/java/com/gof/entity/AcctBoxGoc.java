package com.gof.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.enums.EBoolean;
import com.gof.enums.ECoa;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AcctBox generated by hbm2java
 */
@Entity
@IdClass(AcctBoxGocId.class)
@Table(name = "ACCT_BOX_GOC")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AcctBoxGoc implements Serializable {
	private static final long serialVersionUID = -8151467682976876533L;

	@Id private String baseYymm;
	@Id private String gocId;
	
	@Enumerated(EnumType.STRING)
	@Id private ELiabType liabType;
	
	@Enumerated(EnumType.STRING)
	@Id private EContStatus stStatus;
	
	@Enumerated(EnumType.STRING)
	@Id private EContStatus endStatus;
	
	
	@Enumerated(EnumType.STRING)
	@Id private EBoolean newContYn;

	
	@Id	private String rollFwdType;
	@Id private String runsetId;
	@Id private String calcId;
	@Id private Integer subSeq;
	
	
	private Integer rollFwdSeq;
	
	@Enumerated(EnumType.STRING)
	private ECoa debitCoa;
	
	@Enumerated(EnumType.STRING)
	private ECoa creditCoa;

	private Double boxAmt;
	private Double coaAmt;
	private String remark;
	
	private String lastModifiedBy;
	private LocalDateTime lastModifiedDate;
	
	public double getSignCoaAmt(ECoa coa) {
		if(coa.equals(debitCoa)){ 
			return coaAmt * -1 * coa.getSign();
		}else {
			return coaAmt * coa.getSign();
		}
	}
	
	public boolean isRelateTo(ECoa coa) {
		return coa.equals(debitCoa) || coa.equals(creditCoa);
	}
	
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
	 	 	.append(stStatus).append(",")
	 	 	.append(endStatus).append(",")
	 	 	.append(newContYn).append(",")
	 	 	.append(rollFwdType).append(",")
	 	 	.append(runsetId).append(",")
	 	 	.append(calcId).append(",")
	 	 	.append(subSeq).append(",")
	 	 	.append(boxAmt).append(",")
	 	 	.append(coaAmt).append(",")
	 	 	.toString();
	}
}