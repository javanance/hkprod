package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(DiscRateSpreadId.class)
@Table(name ="EAS_DISC_RATE_SPREAD")
@FilterDef(name="FILTER", parameters= { @ParamDef(name="baseYymm", type="string") })
@Filters( { @Filter(name ="FILTER", condition="BASE_YYMM = :baseYymm") } )
@Getter
@Setter
public class DiscRateSpread implements Serializable {

	private static final long serialVersionUID = -348383584338312083L;
	
	@Id
	private String baseYymm;	
	
	@Id
	private String intRateCd;		

	private Double discRateSpread;	
	
	public DiscRateSpread() {}

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
		return "DiscRateSpread [baseYymm=" + baseYymm + ", intRateCd=" + intRateCd + ", discRateSpread="
				+ discRateSpread + "]";
	}
	
}


