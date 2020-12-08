package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import com.gof.interfaces.IIntRate;

import lombok.Getter;
import lombok.Setter;

@Entity
@IdClass(LiqPremiumId.class)
@Table(name ="EAS_LIQ_PREM")
@FilterDef(name="eqBaseYymm", parameters= @ParamDef(name ="bssd",  type="string"))
@Getter
@Setter
public class LiqPremium implements Serializable, IIntRate{
	private static final long serialVersionUID = -8151467682976876533L;
	@Id
	@Column(name ="BASE_YYMM")
	private String baseYymm; 
	
	@Id
	@Column(name ="DCNT_APPL_MODEL_CD")
	private String modelId;

	@Id
	@Column(name ="MAT_CD")
	private String matCd;
	
	@Column(name ="LIQ_PREM")
	private Double liqPrem;
	
	private Double vol;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public LiqPremium() {
	}

	public LiqPremium(double liqPrem) {
		this.liqPrem = liqPrem;
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
			   .append(modelId).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(liqPrem).append(delimeter)
			   .append(vol).append(delimeter)
//			   .append(lastModifiedBy).append(delimeter)
//			   .append(lastUpdateDate)
			   ;
		return builder.toString();
	}

	@Override
	public String getIrCurveId() {
		return null;
	}

	@Override
	public Double getIntRate() {
		return liqPrem;
	}
	@Override
	public Double getSpread() {
		return liqPrem;
	}
	
	public int getMatNum() {
		return Integer.parseInt(matCd.substring(1));
	}
	public BizLiqPremium convertTo(String bizDv) {
		BizLiqPremium rst = new BizLiqPremium();
		rst.setBaseYymm(this.baseYymm);
		rst.setApplyBizDv(bizDv);
		rst.setMatCd(this.matCd);
		rst.setLiqPrem(this.liqPrem);
		rst.setApplyLiqPrem(this.liqPrem);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		return rst;
		
	}
}
