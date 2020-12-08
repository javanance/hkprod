package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdentifier;


@Entity
@IdClass(CashFlowMatchAdjId.class)
@Table(name ="EAS_CF_MATCH_MAT_WGHT_ADJ")

public class CashFlowMatchAdj implements Serializable, EntityIdentifier {

	private static final long serialVersionUID = -8158796295253534107L;
	
	@Id
	private String baseYymm;	
	
	@Id
	private String matCd;
	
	@Column(name ="WGHT_ADJ_COEF")
	private Double weightAdjCoef;	
	
	private Double vol;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate; 
	
	public CashFlowMatchAdj() {}

	public String getBaseYymm() {
		return baseYymm;
	}

	public void setBaseYymm(String baseYymm) {
		this.baseYymm = baseYymm;
	}

	public String getMatCd() {
		return matCd;
	}

	public void setMatCd(String matCd) {
		this.matCd = matCd;
	}

	public Double getWeightAdjCoef() {
		return weightAdjCoef;
	}

	public void setWeightAdjCoef(Double weightAdjCoef) {
		this.weightAdjCoef = weightAdjCoef;
	}

	public Double getVol() {
		return vol;
	}

	public void setVol(Double vol) {
		this.vol = vol;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public LocalDateTime getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

}


