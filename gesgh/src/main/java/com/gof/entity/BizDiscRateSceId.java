package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;


@Embeddable
@Getter
@Setter
public class BizDiscRateSceId implements Serializable {
	
	private static final long serialVersionUID = 7346541519373980852L;

	private String baseYymm;	
	
	private String intRateCd;
	
	private String applBizDv;
	
	private String sceNo;
	
	private String matCd;	
	
	public BizDiscRateSceId() {}

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
				+ applBizDv + ", sceNo=" + sceNo + ", matCd=" + matCd + "]";
	}
	
}
