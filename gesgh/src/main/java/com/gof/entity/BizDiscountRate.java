package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.interfaces.IIntRate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@IdClass(BizDiscountRateId.class)
@Table(name ="EAS_BIZ_APLY_DCNT_RATE")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BizDiscountRate implements Serializable, IIntRate {

	private static final long serialVersionUID = -4252300668894647002L;
	
	@Id	private String baseYymm;
	@Id
	@Column(name="APPL_BIZ_DV", nullable=false)
	private String applyBizDv;
	@Id	private String irCurveId;
	@Id	private String matCd;
	
	private Double rfRate;
	private Double rfFwdRate;
	private Double liqPrem;
	private Double refYield;
	private Double crdSpread;
	private Double riskAdjRfRate;
	private Double riskAdjRfFwdRate;
	
	private Double vol;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	
//	public BizDiscountRate() {}

	@Override
	public Double getIntRate() {
		return riskAdjRfRate ;
	}
	
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
			   .append("I").append(delimeter)
			   .append(irCurveId).append(delimeter)
			   .append(matCd).append(delimeter)
			   
			   
			   .append(rfRate).append(delimeter)
			   .append(liqPrem).append(delimeter)
			   
			   .append(refYield).append(delimeter)
			   .append(crdSpread).append(delimeter)
			   
			   .append(riskAdjRfRate).append(delimeter)
			   .append(riskAdjRfFwdRate).append(delimeter)
			   
//			   .append(lastModifiedBy).append(delimeter)
//			   .append(lastUpdateDate)
			   ;
		
		return builder.toString();
	}

	
	
}


