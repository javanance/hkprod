package com.gof.ark.entity;


import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.enums.ETiming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CfLv1Raw generated by hbm2java
 */
@Entity
@IdClass(ArkRawCfEirId.class)
@Table(name = "RAW_ARK_EIR_CF")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArkRawCfEir implements Serializable {
	private static final long serialVersionUID = -8151467682976876533L;
	
	@Id	private String driveYm;
	@Id	private String setlYm;
	@Id	private String exeIdno;
	@Id	private String rsDivId;
	@Id	private String csmGrpCd;
	@Id	private String bemmStcd;
	@Id	private String emmStcd;
	@Id	private String ctrDvcd;
	@Id	private Integer setlAftPassMmcnt;					
	
	@Enumerated(EnumType.STRING)
	@Id private ETiming cfTiming;
	
	private Double cfAmt;
	
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
	 		.append(setlYm).append(",")
	 		.append(exeIdno).append(",")
	 	 	.append(rsDivId).append(",")
	 	 	.append(csmGrpCd).append(",")
	 	 	.append(bemmStcd).append(",")
	 	 	.append(emmStcd).append(",")
	 	 	.append(ctrDvcd).append(",")
	 	 	.append(setlAftPassMmcnt).append(",")
	 	 	.append(cfTiming).append(",")
	 	 	.toString();
	}
	
	public ArkRawCfEir(String setlYm, String rsDivId, String csmGrpCd, String bemmStcd, String emmStcd, String ctrDvcd,
			Integer setlAftPassMmcnt, ETiming cfTiming,	Double cfAmt) {
		this.setlYm = setlYm;
		this.rsDivId = rsDivId;
		this.csmGrpCd = csmGrpCd;
		this.bemmStcd = bemmStcd;
		this.emmStcd = emmStcd;
		this.ctrDvcd = ctrDvcd;
		this.setlAftPassMmcnt = setlAftPassMmcnt;
		this.cfTiming = cfTiming;
		this.cfAmt = cfAmt;
	}

	
	
}
