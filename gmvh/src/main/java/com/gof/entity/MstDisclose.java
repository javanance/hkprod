package com.gof.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.enums.EBoolean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * MstDisclose generated by hbm2java
 */
@Entity
@Table(name = "MST_DISCLOSE")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MstDisclose implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;

	@Id private String discId;
	private String discNm;
	private String discGroup;
	private String upperDiscId;
	private String upperDiscNm;
	
	@Enumerated(EnumType.STRING)
	private EBoolean leafYn;
	
	@Enumerated(EnumType.STRING)
	private EBoolean rootYn;
	
	private String useYn;
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
