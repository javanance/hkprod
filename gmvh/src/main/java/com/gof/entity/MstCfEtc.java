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
 * MstCfEtc generated by hbm2java
 */
@Entity
@IdClass(MstCfEtcId.class)
@Table(name = "MST_CF_ETC")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MstCfEtc implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;

	@Id private String srcTbl;
	@Id private String srcCfColId;
	private String tblNm;
	private String colNm;
	
	private String cfId;
	private String cfIdNm;
	
	private String cfEstYn;
	private String cfGroup;
	private String cfGroupNm;
	
	private String cfType;
	private String cfTiming;
	private String outflowYn;
	
	private String pvColId;
	private String pvColNm;
	
	private String useYn;
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
}
