package com.gof.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.gof.enums.EBoolean;
import com.gof.enums.EBoxModel;
import com.gof.enums.ECfType;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;
import com.gof.enums.ETiming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * RstEpv generated by hbm2java
 */
@Entity
@IdClass(RstBoxDetailId.class)
@Table(name = "RST_BOX_DETAIL")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RstBoxDetail implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;
	
	
//	@Id
//	@SequenceGenerator(name = "BOX_DETAIL_SEQ", sequenceName = "BOX_DETAIL_SEQ", initialValue = 1, allocationSize = 50)
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOX_DETAIL_SEQ" )
//	private Long seqId;
	
	@Id private String baseYymm;
	@Id private String gocId;
	
	@Enumerated(EnumType.STRING)
	@Id private ELiabType liabType;

	@ManyToOne
	@JoinColumn(name = "RUNSET_ID")
	@Id private MstRunset mstRunset;
	@Id private String calcId;
		private String deltaGroup;
	
	@Enumerated(EnumType.STRING)
	@Id private EContStatus stStatus;
	
	@Enumerated(EnumType.STRING)
	@Id private EContStatus endStatus;
	
	@Enumerated(EnumType.STRING)
	@Id private EBoolean newContYn;
	
	@Id private String cfKeyId;
	
	private Integer cfColSeq;
	private String cfId;
	
	@Enumerated(EnumType.STRING)
	private ECfType cfType;
	
	@Enumerated(EnumType.STRING)
	@Id private ETiming cfTiming;
	
	@Enumerated(EnumType.STRING)
	private EBoolean outflowYn;
	
	@Id private Double cfMonthNum;
//	private Double cfMonthNum;
	
	private Integer setlAftPassMmcnt;		//Change for update 61st cf !!!!
	
	@Enumerated(EnumType.STRING)
	private EBoxModel boxId;				//Change for update 61st cf !!!! 
	
	private Integer slidingNum;	 
	private Double cfAmt;
	private Double prevCfAmt;
	private Double deltaCfAmt;
	private Double boxValue;
	private Double appliedRate;
	
	private String remark;
	private String lastModifiedBy;
	private LocalDateTime lastModifiedDate;
	
	
	public Double getSignBoxValue(Double sign) {
		return boxValue * sign;
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
	 	 StringBuilder builder = new StringBuilder();
	 	 return builder
	 	 	.append(baseYymm).append(",")
	 	 	.append(gocId).append(",")
	 	 	.append(liabType).append(",")
	 	 	.append(stStatus).append(",")
	 	 	.append(endStatus).append(",")
	 	 	.append(newContYn).append(",")
	 	 	.append(boxId).append(",")
	 	 	.append(cfAmt).append(",")
	 	 	.toString(); 
	}
	
}
