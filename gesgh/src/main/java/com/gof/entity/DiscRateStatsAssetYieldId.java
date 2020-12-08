package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;


@Embeddable
@Getter
@Setter
public class DiscRateStatsAssetYieldId implements Serializable {
	private static final long serialVersionUID = 6207498187147536428L;

	@Id
	private String baseYymm;	
	
	@Id
	private String discRateCalcTyp;
	
	@Id
	private String acctDvCd;

	@Id
	private String indpVariable;
	

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
}


