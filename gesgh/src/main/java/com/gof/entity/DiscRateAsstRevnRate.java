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
@IdClass(DiscRateAsstRevnRateId.class)
@Table(name ="EAS_DISC_RATE_ASST_REVN_RATE")
@FilterDef(name="FILTER", parameters= { @ParamDef(name="baseYymm", type="string") })
@Filters( { @Filter(name ="FILTER", condition="BASE_YYMM = :baseYymm") } )
@Getter
@Setter
public class DiscRateAsstRevnRate implements Serializable {

	private static final long serialVersionUID = 1134677128445417422L;
	
	@Id
	private String baseYymm;	
	
	@Id
	private String acctDvCd;
	
	private Double mgtAsstAmt;
	private Double invRevnAmt;
	private Double ociAssetEvalPl;
	private Double ociRelCorpStkEvalPl;
	private Double ociFxRevalPl;
	private Double ociRevalRevnAmt;
	private Double ociOtherAmt;
	private Double unrealizedPlSumAmt;
	private Double mgtAsstRevnRate;
	private Double invCostAmt;
	private Double invCostRate;
	private Double mgtAsstYield;
	
	public DiscRateAsstRevnRate() {}

	
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
		return "DiscRateAsstRevnRate [baseYymm=" + baseYymm + ", acctDvCd=" + acctDvCd + ", mgtAsstAmt=" + mgtAsstAmt
				+ ", invRevnAmt=" + invRevnAmt + ", ociAssetEvalPl=" + ociAssetEvalPl + ", ociRelCorpStkEvalPl="
				+ ociRelCorpStkEvalPl + ", ociFxRevalPl=" + ociFxRevalPl + ", ociRevalRevnAmt=" + ociRevalRevnAmt
				+ ", ociOtherAmt=" + ociOtherAmt + ", unrealizedPlSumAmt=" + unrealizedPlSumAmt + ", mgtAsstRevnRate="
				+ mgtAsstRevnRate + ", invCostAmt=" + invCostAmt + ", invCostRate=" + invCostRate + ", mgtAsstYield="
				+ mgtAsstYield + "]";
	}
	
}


