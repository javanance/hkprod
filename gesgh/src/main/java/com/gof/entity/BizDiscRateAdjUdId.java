package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;


@Embeddable
@Getter
@Setter
public class BizDiscRateAdjUdId implements Serializable {	

	private static final long serialVersionUID = 8896041712907223964L;

	@Column(name="APPL_ST_YYMM", nullable=false)
	private String applStYymm;	
	
	@Column(name="APPL_BIZ_DV", nullable=false)
	private String applBizDv;
	
	@Column(name="INT_RATE_CD", nullable=false)
	private String intRateCd;
	
	public BizDiscRateAdjUdId() {}


	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
