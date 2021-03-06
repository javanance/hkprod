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

import com.gof.enums.ECfType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * MapCfGroup generated by hbm2java
 */
@Entity
@IdClass(MapCfClassId.class)
@Table(name = "MAP_CF_CLASS")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MapCfClass implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;
	
//	@Id	private String cfGroupId;
	@ManyToOne()
	@JoinColumn(name = "CF_CLASS_ID")
	@Id	private MstCfClass cfClass;
	
	
	@Enumerated(EnumType.STRING)
	@Id	private ECfType cfType;
	
	private String lastModifiedBy;
	private LocalDateTime lastModifiedDate;
	
	public String getCfClassId() {
		return cfClass.getCfClassId();
	}
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}