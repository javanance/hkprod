package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import com.gof.interfaces.IIntRate;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(BottomupDcntId.class)
@Table(name ="EAS_BOTTOMUP_DCNT")
@FilterDef(name="IR_FILTER", parameters= { @ParamDef(name="baseYymm", type="string"), @ParamDef(name="irCurveId", type="string") })
@Filters( { @Filter(name ="IR_FILTER", condition="BASE_YYMM = :baseYymm"),  @Filter(name ="IR_FILTER", condition="IR_CURVE_ID like :irCurveId") } )
@Getter
@Setter
public class BottomupDcnt implements Serializable , IIntRate{

	private static final long serialVersionUID = -8105176349509184506L;

	@Id	private String baseYymm;
	@Id	private String irCurveId;
	@Id	private String matCd;	
	
	@Transient
	private String sceNo;
	
	private Double rfRate;
	private Double liqPrem;
	private Double riskAdjRfRate;
	private Double riskAdjRfFwdRate;
	private Double vol;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public BottomupDcnt() {}

	@Override
	public Double getSpread() {
		return liqPrem;
	}
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
		return toString(",");
	}

	public String toString(String delimeter) {
		StringBuilder builder = new StringBuilder();
		builder.append(baseYymm).append(delimeter)
				.append(irCurveId).append(delimeter)
				.append(matCd).append(delimeter)
				.append(rfRate).append(delimeter)
				.append(liqPrem).append(delimeter)
				.append(riskAdjRfRate).append(delimeter)
				.append(riskAdjRfFwdRate).append(delimeter)
				.append(vol)
				;

		return builder.toString();

	}
//	@Transient
	@Override
	public Double getIntRate() {
		return getRiskAdjRfRate();
	}

	public BizDiscountRate convertTo(String bizDv) {
		BizDiscountRate tempIr = new BizDiscountRate();
		
		tempIr.setBaseYymm(this.getBaseYymm());
		tempIr.setApplyBizDv(bizDv);
		tempIr.setIrCurveId(this.getIrCurveId());
		tempIr.setMatCd(this.getMatCd());
		tempIr.setRfRate(this.getRfRate());
		tempIr.setLiqPrem(this.getLiqPrem());
		
		tempIr.setRefYield(0.0);
		tempIr.setCrdSpread(0.0);
		
		tempIr.setRiskAdjRfRate(this.getRiskAdjRfRate());
		tempIr.setRiskAdjRfFwdRate(this.getRiskAdjRfFwdRate());
		
		tempIr.setVol(0.0);
		
		tempIr.setLastModifiedBy("ESG");
		tempIr.setLastUpdateDate(LocalDateTime.now());
		
		return tempIr;
	}

	
	public BizDiscountRateSce convertToSce(String bizDv) {
		BizDiscountRateSce tempIr = new BizDiscountRateSce();
		
		tempIr.setBaseYymm(this.getBaseYymm());
		tempIr.setApplyBizDv(bizDv);
		tempIr.setIrCurveId(this.getIrCurveId());
		tempIr.setMatCd(this.getMatCd());
		tempIr.setSceNo(this.getSceNo());
		tempIr.setRfRate(this.getRfRate());
		tempIr.setLiqPrem(this.getLiqPrem());
		
		tempIr.setRefYield(0.0);
		tempIr.setCrdSpread(0.0);
		
		tempIr.setRiskAdjRfRate(this.getRiskAdjRfRate());
		tempIr.setRiskAdjRfFwdRate(this.getRiskAdjRfFwdRate());
		
		tempIr.setLastModifiedBy("ESG");
		tempIr.setLastUpdateDate(LocalDateTime.now());
		
		return tempIr;
	}

	
}


