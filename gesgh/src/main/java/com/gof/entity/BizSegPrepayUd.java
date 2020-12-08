package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.interfaces.BaseValue;
import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;

@Entity
@IdClass(BizSegPrepayUdId.class)
@Table(name ="EAS_USER_SEG_PREP_RATE")
@Getter
@Setter
public class BizSegPrepayUd implements Serializable, EntityIdentifier, BaseValue {
	private static final long serialVersionUID = 2360652480675748510L;
	
	@Id
	@Column(name="APPL_ST_YYMM", nullable=false)
	private String applyStartYymm;

	@Id
	@Column(name="APPL_BIZ_DV", nullable=false)
	private String applyBizDv;
	@Id
	private String segId;	
	
	@Column(name ="APPL_ED_YYMM")
	private String applyEndYymm;
	
	@Column(name="APPL_PREP_RATE")	
    private Double applyPrepRate;
	
//	private Double vol;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public BizSegPrepayUd() {}


	@Override
	public Double getBasicValue() {
		return applyPrepRate;
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
