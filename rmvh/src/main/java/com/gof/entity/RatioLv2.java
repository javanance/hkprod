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
 * RstRaRatio generated by hbm2java
 */
@Entity
@IdClass(RatioLv2Id.class)
@Table(name = "RATIO_LV2")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatioLv2 implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;

	@Id private String baseYymm;
	@Id private String gocId;
	@Id private String ratioId;
	
	private Double prevServiceUnit;
	private Double serviceRelease;
	private Double currServiceUnit;
	private Double releaseRatio;
	
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
}
