package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;


@Embeddable
@Getter
@Setter
public class DiscRateWghtId implements Serializable {

	private static final long serialVersionUID = 8207684050620145464L;

	@Column(name="BASE_YYMM", nullable=false)
	private String baseYymm;
	
	@Column(name="INT_RATE_CD", nullable=false)
	private String intRateCd;
	
	public DiscRateWghtId() {}

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
		return "DiscRateWghtUdId [baseYymm=" + baseYymm + ", intRateCd=" + intRateCd + "]";
	}	
	
}
