package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(BizDiscountRateId.class)
@Table(name ="EAS_USER_APPL_DCNT")
@Getter
@Setter
public class BizDiscountRateUd implements Serializable {
	private static final long serialVersionUID = -4252300668894647002L;
	
	@Id	private String baseYymm;
	@Id
	@Column(name="APPL_BIZ_DV", nullable=false)
	private String applyBizDv;
	@Id	private String irCurveId;
	@Id	private String matCd;
	
	private Double rfRate;
	private Double liqPrem;
	private Double riskAdjRfRate;
	private Double riskAdjRfFwdRate;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	
	public BizDiscountRateUd() {}

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
			   .append("I").append(delimeter)
			   .append(irCurveId).append(delimeter)
			   .append(matCd).append(delimeter)
			   
			   .append(rfRate).append(delimeter)
			   .append(liqPrem).append(delimeter)
			   
			   .append(riskAdjRfRate).append(delimeter)
			   .append(riskAdjRfFwdRate).append(delimeter)
			   
			   .append(0.0)
			   ;
		
		return builder.toString();
	}
	public BizDiscountRate convertToBizDiscountRate() {
		BizDiscountRate rst = new BizDiscountRate();
		rst.setBaseYymm(baseYymm);
		rst.setApplyBizDv(applyBizDv);
		rst.setIrCurveId(irCurveId);
		rst.setMatCd(matCd);
		rst.setRfRate(rfRate);
		rst.setLiqPrem(liqPrem);
		rst.setRefYield(0.0);
		rst.setCrdSpread(0.0);
		rst.setRiskAdjRfRate(riskAdjRfRate);
		rst.setRiskAdjRfFwdRate(riskAdjRfFwdRate);
		rst.setVol(0.0);
		rst.setLastModifiedBy("USER");
		rst.setLastUpdateDate(LocalDateTime.now());
		return rst;
	}
}


