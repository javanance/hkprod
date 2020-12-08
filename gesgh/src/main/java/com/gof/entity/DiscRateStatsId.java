package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;


@Embeddable
@Getter
@Setter
public class DiscRateStatsId implements Serializable {	

	private static final long serialVersionUID = 8896041712907223964L;

	@Column(name="BASE_YYMM", nullable=false)
	private String baseYymm;	
	
	@Column(name="DISC_RATE_CALC_TYP", nullable=false)
	private String discRateCalcTyp;
	
	@Column(name="INT_RATE_CD", nullable=false)
	private String intRateCd;

	@Column(name="DEPN_VARIABLE", nullable=false)
	private String depnVariable;

	@Column(name="INDP_VARIABLE", nullable=false)
	private String indpVariable;
	
	public DiscRateStatsId() {}

	

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
		return "DiscRateStatsId [baseYymm=" + baseYymm + ", discRateCalcTyp=" + discRateCalcTyp + ", intRateCd="
				+ intRateCd + ", depnVariable=" + depnVariable + ", indpVariable=" + indpVariable + "]";
	}
	
	

	
}
