package com.gof.entity;
// Generated 2020. 1. 16 ���� 3:15:14 by Hibernate Tools 5.1.0.Beta1

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.enums.EBoolean;
import com.gof.enums.ECfType;
import com.gof.enums.ETiming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CfLv1Raw generated by hbm2java
 */
@Entity
@IdClass(RawCfLicId.class)
@Table(name = "RAW_GOC_CF_LIC")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawCfLic implements Serializable {
	private static final long serialVersionUID = -8151467682976876533L;
	
	@Id	private String driveYm;
	@Id	private String setlYm;
	@Id	private String exeIdno;
	@Id	private String rsDivId;
	@Id	private String csmGrpCd;
	@Id	private String bemmStcd;
	@Id	private String emmStcd;
	@Id	private String ctrDvcd;
	@Id	private String subKey;
	
	@Id	private Integer setlAftPassMmcnt;					
	
	@Id	private String cfKeyId;
	
	@Enumerated(EnumType.STRING)
	private ECfType cfType;
	
	@Enumerated(EnumType.STRING)
	@Id	private ETiming cfTiming;
	
	@Enumerated(EnumType.STRING)
	@Id	 private EBoolean outflowYn;
	
	private Double absCfAmt;
	private Double absPvAmt;
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
	 		.append(driveYm).append(",")
	 		.append(setlYm).append(",")
	 		.append(exeIdno).append(",")
	 	 	.append(rsDivId).append(",")
	 	 	.append(csmGrpCd).append(",")
	 	 	.append(bemmStcd).append(",")
	 	 	.append(emmStcd).append(",")
	 	 	.append(ctrDvcd).append(",")
	 	 	.append(setlAftPassMmcnt).append(",")
	 	 	.append(cfKeyId).append(",")
	 	 	.append(cfTiming).append(",")
	 	 	.append(outflowYn).append(",")
	 	 	.toString();
	}
}
