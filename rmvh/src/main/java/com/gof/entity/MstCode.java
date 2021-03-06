package com.gof.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.enums.EContStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * MstCode generated by hbm2java
 */
@Entity
@Table(name = "MST_CODE")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MstCode implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;

	@Id private String codeGroup;
	@Id private String codeId;
	
	private String codeName;
	private String codeOrigin;
	private String comments;
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
	
	public EContStatus getContStatus() {
		return EContStatus.valueOf(codeId);
	}
}
