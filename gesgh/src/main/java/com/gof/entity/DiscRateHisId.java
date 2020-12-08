package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;


@Embeddable
@Getter
@Setter
public class DiscRateHisId implements Serializable {	

	private static final long serialVersionUID = 8465878386389748580L;

	@Column(name="BASE_YYMM", nullable=false)
	private String baseYymm;	
	
	@Column(name="INT_RATE_CD", nullable=false)
	private String intRateCd;
	
	@Column(name="DISC_RATE_CALC_DTL", nullable=false)
	private String acctDvCd;
	
	public DiscRateHisId() {}

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
		return "DiscRateHisId [baseYymm=" + baseYymm + ", intRateCd=" + intRateCd + ", acctDvCd=" + acctDvCd + "]";
	}
	
}
