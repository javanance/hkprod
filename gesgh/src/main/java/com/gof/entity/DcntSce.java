package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(DcntSceId.class)
@Table(name ="EAS_DCNT_SCE")
@Getter
@Setter
public class DcntSce implements Serializable {

	private static final long serialVersionUID = -4252300668894647002L;
	
	@Id
	private String baseYymm;
	
	@Id
	private String irCurveId;
	
	@Id
	private String sceNo;

	@Id
	private String matCd;
	
	private Double rfRate;
	
	private Double liqPrem;
	
	private Double refYield;
	
	private Double crdSpread;
	
	private Double riskAdjRfRate;
	
	private Double riskAdjRfFwdRate;
	
	private Double vol;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	
	public DcntSce() {}


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
//			   .append(bizDv).append(delimeter)
			   .append(irCurveId).append(delimeter)
			   .append(sceNo).append(delimeter)
			   .append(matCd).append(delimeter)
			   
			   
			   .append(rfRate).append(delimeter)
			   .append(liqPrem).append(delimeter)
			   
			   .append(refYield).append(delimeter)
			   .append(crdSpread).append(delimeter)
			   
			   .append(riskAdjRfRate).append(delimeter)
			   .append(riskAdjRfFwdRate).append(delimeter)
			   
			   .append(0.0).append(delimeter)
			   .append(lastModifiedBy).append(delimeter)
			   .append(lastUpdateDate)
			   ;
		
//		return builder.append("\n").toString();
		return builder.toString();
	}
	
	public String toStringWithBizDv(String delimeter, String bizDv) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(baseYymm).append(delimeter)
			   .append(bizDv).append(delimeter)
			   .append(irCurveId).append(delimeter)
			   .append(sceNo).append(delimeter)
			   .append(matCd).append(delimeter)
			   
			   
			   .append(rfRate).append(delimeter)
			   .append(liqPrem).append(delimeter)
			   
			   .append(refYield).append(delimeter)
			   .append(crdSpread).append(delimeter)
			   
			   .append(riskAdjRfRate).append(delimeter)
			   .append(riskAdjRfFwdRate).append(delimeter)
			   
			   .append(0.0).append(delimeter)
			   .append(lastModifiedBy).append(delimeter)
			   .append(lastUpdateDate)
			   ;
		
//		return builder.append("\n").toString();
		return builder.toString();
	}
	
	public BizDiscountRateSce convertToBizDcntSce(String bizDv) {
		BizDiscountRateSce rst = new BizDiscountRateSce();
		rst.setBaseYymm(this.baseYymm);
		rst.setApplyBizDv(bizDv);
		rst.setIrCurveId(irCurveId);
		rst.setSceNo(sceNo);
		rst.setMatCd(matCd);
		rst.setRfRate(rfRate);
		rst.setLiqPrem(liqPrem);
		rst.setRefYield(refYield);		
		rst.setCrdSpread(crdSpread);
		rst.setRiskAdjRfRate(riskAdjRfRate);
		rst.setRiskAdjRfFwdRate(riskAdjRfFwdRate);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
}



