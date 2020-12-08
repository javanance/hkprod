package com.gof.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import java.time.LocalDateTime;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.enums.EBoolean;
import com.gof.enums.EBoxModel;
import com.gof.enums.ECfType;
import com.gof.enums.ELiabType;
import com.gof.enums.ETiming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CfLv3Real generated by hbm2java
 */
@Entity
@IdClass(RawCashId.class)
@Table(name = "RAW_GOC_CASH")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawCash implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;
	
	@Id	private String baseYymm;
	@Id	private String gocId;
	
//	@Id private String runsetId;
	@Id private String rsDivId;
	
	@Enumerated(EnumType.STRING)
	@Id	private ELiabType liabType;
	@Id	private String stStatus;
	@Id	private String endStatus;
	
	@Enumerated(EnumType.STRING)
	@Id	private EBoolean newContYn;
	@Id	private String cfKeyId;
	@Id	private String cfId;
	
	@Enumerated(EnumType.STRING)
	private ECfType cfType;
	
	@Enumerated(EnumType.STRING)
	@Id	private EBoolean outflowYn;
	
	private String cfStartYymm;
	private String cfEndYymm;
	
	private Double cfAmt;
	
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
	
	
	public Double getPrevCfAmt() {
		return 0.0;
	}
	public Double getDeltaCfAmt() {
		return cfAmt;
	}
	public Double getAppiedCfAmt() {
		return cfAmt;
	}
	
	public Integer getCfColSeq() {
		return 0;
	}
	public ETiming getCfTiming() {
		return ETiming.END;
	}
	public Double getCfMonthNum() {
		return 0.0;
	}
	public boolean isFutureCf(double forwardingNum) {
		return false;
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
	 	 	.append(cfType).append(",")
	 	 	.append(cfAmt).append(",")
	 	 	.toString();
	}
	public Map<EBoxModel, Double> getBoxValueMap(DfLv3Flat df) {
		Map<EBoxModel, Double> rstMap = df.getBoxMap();
		
//		rstMap.put(EBoxModel.U0, getAppiedCfAmt());
		return rstMap;
	}
	public RawCash(String baseYymm, String gocId, String rsDivId, ELiabType liabType, String stStatus, String endStatus,
			EBoolean newContYn, String cfKeyId, ECfType cfType, EBoolean outflowYn, Double cfAmt) {
		this.baseYymm = baseYymm;
		this.gocId = gocId;
		this.rsDivId = rsDivId;
		this.liabType = liabType;
		this.stStatus = stStatus;
		this.endStatus = endStatus;
		this.newContYn = newContYn;
		this.cfKeyId = cfKeyId;
		this.cfType = cfType;
		this.outflowYn = outflowYn;
		this.cfAmt = cfAmt;
	}
	
	
	
	
}
