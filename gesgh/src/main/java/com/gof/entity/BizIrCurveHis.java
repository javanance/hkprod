package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.gof.interfaces.IIntRate;

import lombok.Getter;
import lombok.Setter;

@Entity
@IdClass(BizIrCurveHisId.class)
@Table(name ="EAS_BIZ_APLY_IR_CURVE_HIS")
@Access(AccessType.FIELD)
@Getter
@Setter
public class BizIrCurveHis implements Serializable, IIntRate{
	private static final long serialVersionUID = -8151467682976876533L;
	
	@Id	private String baseYymm; 
	@Id	private String applBizDv; 
	@Id	private String irCurveId;
	@Id	private String matCd;
	
	private Double intRate;
	
	private Double forwardRate;
	
	@Transient
	private int forwardNum;
	
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public BizIrCurveHis() {
	
	}
	
	@Override
	public Double getSpread() {
		return 0.0;
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
				.append(applBizDv).append(delimeter)
//			   .append(sceNo==null? "0":sceNo).append(delimeter)
			   .append(irCurveId).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(intRate).append(delimeter)
			   .append(forwardRate).append(delimeter)
			   .append(forwardNum)
//			   .append(lastUpdateDate)
			   ;

		return builder.toString();
	}
//******************************************************Biz Method**************************************
	public int getMatNum() {
		return Integer.parseInt(matCd.substring(1));
	}
	
//	public BottomupDcnt convertTo(String irCurveId, double lq) {
//		BottomupDcnt rst = new BottomupDcnt();
//		
//		rst.setBaseYymm(this.getBaseYymm());
//		rst.setIrCurveId(irCurveId);
//		rst.setMatCd(this.matCd);
//		rst.setRfRate();
//		rst.setLiqPrem(lq);
//		rst.setRiskAdjRfRate(riskAdjRfRate);
//		rst.setRiskAdjRfFwdRate(riskAdjRfFwdRate);
//		
//		rst.setLastModifiedBy("ESG");
//		rst.setLastUpdateDate(LocalDateTime.now());
//	}
	
	public double getDf() {
		return Math.pow(1+intRate, -1.0* getMatNum()/12);
	}
	
	public double getContForwardRate() {
		return Math.log(1+forwardRate);
	}
}
