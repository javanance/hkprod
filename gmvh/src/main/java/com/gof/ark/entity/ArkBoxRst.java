package com.gof.ark.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.gof.entity.MstRunset;
import com.gof.enums.EBoolean;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;
import com.gof.infra.GmvConstant;
import com.gof.interfaces.IBoxRst;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@IdClass(ArkBoxRstId.class)
@Table(name = "ARK_BOX_RST")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArkBoxRst implements java.io.Serializable, IBoxRst {
	private static final long serialVersionUID = -8151467682976876533L;

	@Id	private String baseYymm;
	@Id	private String gocId;
	
	@Enumerated(EnumType.STRING)
	@Id	private ELiabType liabType;
	
	@Enumerated(EnumType.STRING)
	@Id private EContStatus stStatus;
	
	@Enumerated(EnumType.STRING)
	@Id private EContStatus endStatus;
	
	@Enumerated(EnumType.STRING)
	@Id	private EBoolean newContYn;
	
	@Id	private String arkRunsetId;
//	private String runsetId;
	
	@ManyToOne
	@JoinColumn(name = "RUNSET_ID")
	@Id	private MstRunset mstRunset;
	
	
	
	
	@Id private String calcId;
	@Id private String itemId;
	
	private Integer slidingNum;	 
	private Double cfAmt;
	private Double prevCfAmt;
	private Double deltaCfAmt;
	private Double boxValue;
	private Double appliedRate;
	
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
	 	 StringBuilder builder = new StringBuilder();
	 	 return builder
	 	 	.append(baseYymm).append(",")
	 	 	.append(gocId).append(",")
	 	 	.append(liabType).append(",")
	 	 	.append(arkRunsetId).append(",")
	 	 	.append(calcId).append(",")
	 	 	.append(stStatus).append(",")
	 	 	.append(endStatus).append(",")
	 	 	.append(newContYn).append(",")
	 	 	.append(cfAmt).append(",")
	 	 	.toString(); 
	}
	
	
	public String getPk() {
	 	 StringBuilder builder = new StringBuilder();
	 	 return builder
	 	 	.append(baseYymm).append(",")
	 	 	.append(gocId).append(",")
	 	 	.append(liabType).append(",")
	 	 	.append(arkRunsetId).append(",")
	 	 	.append(calcId).append(",")
	 	 	.append(stStatus).append(",")
	 	 	.append(endStatus).append(",")
	 	 	.append(newContYn).append(",")
	 	 	.toString(); 
	}
	public ArkBoxRst(String baseYymm, String gocId, ELiabType liabType, String arkRunsetId, MstRunset mstRunset, String calcId, EContStatus stStatus,
			EContStatus endStatus, EBoolean newContYn, Double cfAmt, Double prevCfAmt, Double deltaCfAmt, Double boxValue) {
		
		this.baseYymm = baseYymm;
		this.gocId = gocId;
		this.liabType = liabType;
		this.arkRunsetId = arkRunsetId;
		this.mstRunset	= mstRunset;
		this.calcId = calcId;
		this.stStatus = stStatus;
		this.endStatus = endStatus;
		this.newContYn = newContYn;
		this.cfAmt = cfAmt;
		this.prevCfAmt = prevCfAmt;
		this.deltaCfAmt = deltaCfAmt;
		this.boxValue = boxValue;
		
		this.slidingNum=0;
		this.appliedRate=0.0;
		this.lastModifiedBy=GmvConstant.getLastModifier();
		this.lastModifiedDate= LocalDateTime.now();
	}
	
	public ArkBoxRst(String baseYymm, String gocId, ELiabType liabType, String arkRunsetId, MstRunset mstRunset, String calcId, Double boxValue) {
		this.baseYymm = baseYymm;
		this.liabType =liabType;
		this.gocId = gocId;
//		this.mstRunset = mstRunset;
		this.arkRunsetId = arkRunsetId;
		this.mstRunset	= mstRunset;
		this.calcId = calcId;
		this.boxValue = boxValue;
	}
	
	@Override
	public String getRunsetId() {
		return mstRunset.getRunsetId();
	}
	
}
