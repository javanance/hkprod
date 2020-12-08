package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;


@Embeddable
@Getter
@Setter
public class DiscRateId implements Serializable {

	private static final long serialVersionUID = -8075451385628396380L;

	@Column(name="BASE_YYMM", nullable=false)
	private String baseYymm;
	
	@Column(name="INT_RATE_CD", nullable=false)
	private String intRateCd;

	@Column(name="DISC_RATE_CALC_TYP", nullable=false)
	private String discRateCalcTyp;

	@Column(name="MAT_CD", nullable=false)
	private String matCd;
	
	public DiscRateId() {}	

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
		return "DiscRateId [baseYymm=" + baseYymm + ", intRateCd=" + intRateCd + ", discRateCalcTyp=" + discRateCalcTyp
				+ ", matCd=" + matCd + "]";
	}	
	
}
