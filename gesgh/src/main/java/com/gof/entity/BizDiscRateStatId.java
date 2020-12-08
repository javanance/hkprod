package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;


@Embeddable
@Getter
@Setter
public class BizDiscRateStatId implements Serializable {

	private static final long serialVersionUID = -8075451385628396380L;

	@Column(name="BASE_YYMM", nullable=false)
	private String baseYymm;
	
	@Column(name = "APPL_BIZ_DV", nullable=false)
	private String applyBizDv;
	
	@Column(name="INT_RATE_CD", nullable=false)
	private String intRateCd;

	@Column(name="INDP_VARIABLE", nullable=false)
	private String indpVariable;
	
	public BizDiscRateStatId() {}	

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

//	@Override
//	public String toString() {
//		return "DiscRateId [baseYymm=" + baseYymm + ", intRateCd=" + intRateCd + ", discRateCalcTyp=" + discRateCalcTyp
//				+ ", matCd=" + matCd + "]";
//	}	
	
}
