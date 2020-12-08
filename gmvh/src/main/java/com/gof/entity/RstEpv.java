package com.gof.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.enums.EBoolean;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;
import com.gof.interfaces.IBaseRst;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * RstEpv generated by hbm2java
 */
@Entity
@IdClass(RstEpvId.class)
@Table(name = "RST_EPV")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RstEpv implements java.io.Serializable, IBaseRst {
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
	
	private Double outCfAmt;
	private Double outEpvAmt;
	private Double inCfAmt;
	private Double inEpvAmt;
	private Double cfAmt;
	private Double epvAmt;
	
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
	 	 	.append(stStatus).append(",")
	 	 	.append(endStatus).append(",")
	 	 	.append(newContYn).append(",")
	 	 	.append(cfAmt).append(",")
	 	 	.append(epvAmt).append(",")
	 	 	.toString(); 
	}
	
}
