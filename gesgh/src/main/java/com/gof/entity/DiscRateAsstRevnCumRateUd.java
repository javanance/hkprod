package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(DiscRateAsstRevnCumRateUdId.class)
@Table(name ="EAS_USER_ASST_REVN_CUM_RATE")
@Getter
@Setter
public class DiscRateAsstRevnCumRateUd implements Serializable, EntityIdentifier {
	
	private static final long serialVersionUID = -4403425853956209330L;

	@Id
	private String baseYymm;	
	
	@Id
	private String acctDvCd;
	
	private Double mgtAsstRevnCumRate;
	
	private Double invCostCumRate;
	
	private Double mgtAsstCumYield;	
	
	public DiscRateAsstRevnCumRateUd() {}

	public String getBaseYymm() {
		return baseYymm;
	}

	public void setBaseYymm(String baseYymm) {
		this.baseYymm = baseYymm;
	}

	public String getAcctDvCd() {
		return acctDvCd;
	}

	public void setAcctDvCd(String acctDvCd) {
		this.acctDvCd = acctDvCd;
	}

	public Double getMgtAsstRevnCumRate() {
		return mgtAsstRevnCumRate;
	}

	public void setMgtAsstRevnCumRate(Double mgtAsstRevnCumRate) {
		this.mgtAsstRevnCumRate = mgtAsstRevnCumRate;
	}

	public Double getInvCostCumRate() {
		return invCostCumRate;
	}

	public void setInvCostCumRate(Double invCostCumRate) {
		this.invCostCumRate = invCostCumRate;
	}

	public Double getMgtAsstCumYield() {
		return mgtAsstCumYield;
	}

	public void setMgtAsstCumYield(Double mgtAsstCumYield) {
		this.mgtAsstCumYield = mgtAsstCumYield;
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

	@Override
	public String toString() {
		return "UserDiscRateAsstRevnCumRate [baseYymm=" + baseYymm + ", acctDvCd=" + acctDvCd + ", mgtAsstRevnCumRate="
				+ mgtAsstRevnCumRate + ", invCostCumRate=" + invCostCumRate + ", mgtAsstCumYield=" + mgtAsstCumYield
				+ "]";
	}

}


