package com.gof.ark.entity;


import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.enums.EBoolean;
import com.gof.enums.ECfType;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
 

@Entity
@IdClass(ArkFutureCfId.class)
@Table(name = "ARK_FUTURE_CF")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@Slf4j
public class ArkFutureCf implements java.io.Serializable {

	private static final long serialVersionUID = -8151467682976876533L;
	
	@Id	private String baseYymm;
	@Id	private String gocId;
	
	@Enumerated(EnumType.STRING)
	@Id	private ELiabType liabType;

	@Enumerated(EnumType.STRING)
	@Id	private EContStatus stStatus;
	
	@Enumerated(EnumType.STRING)
	@Id	private EContStatus endStatus;
	
	@Enumerated(EnumType.STRING)
	@Id	private EBoolean newContYn;
	
	@Id	private String arkRunsetId;
		private String runsetId;
	@Id	private String cfKeyId;
	
	@Enumerated(EnumType.STRING)
	private ECfType cfType;
	
	private double cfAmt;
	private double epvAmt;
	
	private String driveYm;
	private String setlYm;	
	private String rsDivId;
	private String csmGrpCd;
	private String bemmStcd;
	private String emmStcd;
	private String ctrDvcd;
	
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
	 	 	.append(liabType).append(",")
	 	 	.append(stStatus).append(",")
	 	 	.append(endStatus).append(",")
	 	 	.append(newContYn).append(",")
	 	 	.append(arkRunsetId).append(",")
	 	 	.append(cfKeyId).append(",")
	 	 	.toString();
	}
	
	public ArkFutureCf(String baseYymm, String gocId, ELiabType liabType, EContStatus stStatus, EContStatus endStatus,EBoolean newContYn, String arkRunsetId, ECfType cfType, Double cfAmt) {
		
		this.baseYymm = baseYymm;
		this.gocId = gocId;
		this.liabType = liabType;
		this.stStatus = stStatus;
		this.endStatus = endStatus;
		this.newContYn = newContYn;
		this.arkRunsetId = arkRunsetId;
		this.cfType = cfType;
		this.cfAmt = cfAmt;
		this.cfAmt = cfAmt;
	}
	
	public ArkFutureCf(String baseYymm, String gocId, ELiabType liabType, EContStatus stStatus, EContStatus endStatus,EBoolean newContYn, String arkRunsetId
			, String runsetId, double cfAmt, double epvAmt) {
		
		this.baseYymm = baseYymm;
		this.gocId = gocId;
		this.liabType = liabType;
		this.stStatus = stStatus;
		this.endStatus = endStatus;
		this.newContYn = newContYn;
		this.arkRunsetId = arkRunsetId;
		this.runsetId =runsetId;
		this.cfAmt = cfAmt;
		this.epvAmt = epvAmt;
	}
	
	public ArkFutureCf(String baseYymm, String gocId, ELiabType liabType, EContStatus stStatus, EContStatus endStatus,EBoolean newContYn, String arkRunsetId
			, String runsetId, ECfType cfType, double cfAmt, double epvAmt, String setlYm) {
		
		this.baseYymm = baseYymm;
		this.gocId = gocId;
		this.liabType = liabType;
		this.stStatus = stStatus;
		this.endStatus = endStatus;
		this.newContYn = newContYn;
		this.arkRunsetId = arkRunsetId;
		this.runsetId =runsetId;
		this.cfType=cfType;
		this.cfAmt = cfAmt;
		this.epvAmt = epvAmt;
		this.setlYm = setlYm;
	}
}
