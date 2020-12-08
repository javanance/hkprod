package com.gof.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.enums.EBoolean;
import com.gof.enums.ECfType;
import com.gof.enums.ETiming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * MstCfLv2 generated by hbm2java
 */
@Entity
@Table(name = "MST_CF_LV2")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MstCfLv2 implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;

	@Id
	private String cfId;
	
	private String cfIdNm;
	private String cfDvcd;
	private String cfDvcdNm;
	
	@Enumerated(EnumType.STRING)
	private EBoolean cfEstYn;
	private String cfGroup;
	private String cfGroupNm;
	
	@Enumerated(EnumType.STRING)
	private ECfType cfType;
	
	@Enumerated(EnumType.STRING)
	private ETiming cfTiming;
	
	@Enumerated(EnumType.STRING)
	private EBoolean outflowYn;
	
	private String useYn;
	private String realCdId;
	
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
