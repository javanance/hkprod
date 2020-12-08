package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;


@Embeddable
@Getter
@Setter
public class DiscRateSceId implements Serializable {
	
	private static final long serialVersionUID = 7346541519373980852L;

	@Column(name="BASE_YYMM", nullable=false)
	private String baseYymm;	
	
	@Column(name="INT_RATE_CD", nullable=false)
	private String intRateCd;
	
	@Column(name="DISC_RATE_CALC_TYP", nullable=false)
	private String discRateCalcTyp;
	
	@Column(name="SCE_NO", nullable=false)
	private String sceNo;
	
	@Column(name="MAT_CD", nullable=false)
	private String matCd;	
	
	public DiscRateSceId() {}

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
		return "DiscRateSceId [baseYymm=" + baseYymm + ", intRateCd=" + intRateCd + ", discRateCalcTyp="
				+ discRateCalcTyp + ", sceNo=" + sceNo + ", matCd=" + matCd + "]";
	}
	
}
