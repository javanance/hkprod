package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;


@Embeddable
@Getter
@Setter
public class DiscRateAsstRevnRateId implements Serializable {	

	private static final long serialVersionUID = -6622425144515595292L;

	@Column(name="BASE_YYMM", nullable=false)
	private String baseYymm;	
	
	@Column(name="ACCT_DV_CD", nullable=false)
	private String acctDvCd;
	
	public DiscRateAsstRevnRateId() {}

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
		return "DiscRateAsstRevnRateId [baseYymm=" + baseYymm + ", acctDvCd=" + acctDvCd + "]";
	}
	
}
