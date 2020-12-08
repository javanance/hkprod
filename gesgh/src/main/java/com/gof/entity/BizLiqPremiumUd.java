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

import lombok.Getter;
import lombok.Setter;

@Entity
@IdClass(BizLiqPremiumUdId.class)
@Table(name ="EAS_USER_LIQ_PREM")
@FilterDef(name="eqBaseYymm", parameters= @ParamDef(name ="bssd",  type="string"))
@Getter
@Setter
public class BizLiqPremiumUd implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;
	@Id
	@Column(name ="APPL_ST_YYMM")
	private String applyStartYymm;
	@Id
	@Column(name ="APPL_BIZ_DV")
	private String applyBizDv;
	@Id
	private String matCd;
	
	@Column(name ="APPL_ED_YYMM")
	private String applyEndYymm;
	
	@Column(name ="APPL_LIQ_PREM")
	private Double applyLiqPrem;
	
	@Column(name ="LIQ_PREM")
	private Double liqPrem;
	
	private Double vol;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public BizLiqPremiumUd() {}
	
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
		
		builder.append(applyStartYymm).append(delimeter)
			   .append(applyBizDv).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(applyEndYymm).append(delimeter)
			   .append(applyLiqPrem).append(delimeter)
			   .append(liqPrem).append(delimeter)
			   .append(vol)
//			   .append(lastModifiedBy).append(delimeter)
//			   .append(lastUpdateDate)
			   ;
		return builder.toString();
	}
	
	public LiqPremium convertToLiqPreminum(String bssd) {
		LiqPremium rst = new LiqPremium();
		rst.setBaseYymm(bssd);
		rst.setModelId("COVERED_BOND_KDB");
		rst.setMatCd(this.matCd);
		rst.setLiqPrem(this.applyLiqPrem);
		return rst;
	}
	
	public BizLiqPremium convertToBizLiqPremium(String bssd) {
		BizLiqPremium rst = new BizLiqPremium();
		
		rst.setBaseYymm(bssd);
		rst.setApplyBizDv(this.applyBizDv);
		rst.setMatCd(this.matCd);
		rst.setApplyLiqPrem(this.applyLiqPrem);
		rst.setLiqPrem(this.liqPrem);
		rst.setVol(0.0);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
}
