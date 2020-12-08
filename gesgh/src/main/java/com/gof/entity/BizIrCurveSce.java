package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdentifier;
import com.gof.interfaces.IIntRate;

import lombok.Getter;
import lombok.Setter;



@Entity
@IdClass(BizIrCurveSceId.class)
@Table(name ="EAS_BIZ_APLY_IR_CURVE_SCE")
@Getter
@Setter
public class BizIrCurveSce implements Serializable, EntityIdentifier, IIntRate {

	private static final long serialVersionUID = 4458482460359847563L;

	@Id	private String baseYymm;
    @Id	private String applBizDv;
    @Id private String irCurveId;	
    @Id	private String matCd;
    @Id	private String sceNo;

	private Double rfRate;
	private Double forwardRate;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public BizIrCurveSce() {}

	@Override
	public Double getIntRate() {
		return getRfRate();
	}
	
	@Override
	public Double getSpread() {
		return 0.0;
	}
	
	public boolean isBaseTerm() {
		if(matCd.equals("M0003") 
				|| matCd.equals("M0006") 
				|| matCd.equals("M0009")
				|| matCd.equals("M0012")
				|| matCd.equals("M0024")
				|| matCd.equals("M0036")
				|| matCd.equals("M0060")
				|| matCd.equals("M0084")
				|| matCd.equals("M0120")
				|| matCd.equals("M0240")
				) {
			return true;
		}
		return false;	
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
			   .append(matCd).append(delimeter)
			   .append(sceNo).append(delimeter)
			   .append(irCurveId).append(delimeter)
			   .append(rfRate).append(delimeter)
			   .append(forwardRate).append(delimeter)
			   .append("0.0").append(delimeter)
			   .append(lastModifiedBy).append(delimeter)
			   .append(lastUpdateDate);
		
		return builder.append("\n").toString();
	}

	public IrSce convertTo() {
		IrSce rst = new IrSce();
		
		rst.setBaseDate(this.baseYymm);
		rst.setIrCurveId(this.irCurveId);
		rst.setIrModelId(this.applBizDv);
		rst.setMatCd(this.matCd);
		rst.setSceNo(this.sceNo);
		rst.setRfRate(this.rfRate);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
	
}


