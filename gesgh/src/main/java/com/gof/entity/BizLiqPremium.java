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
@IdClass(BizLiqPremiumId.class)
@Table(name ="EAS_BIZ_APLY_LIQ_PREM")
@FilterDef(name="eqBaseYymm", parameters= @ParamDef(name ="bssd",  type="string"))
@Getter
@Setter
public class BizLiqPremium implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;

	@Id
	private String baseYymm;
	@Id
	private String applyBizDv;
	@Id
	private String matCd;
//	private String applyEndYymm;
	
	@Column(name ="APPL_LIQ_PREM")
	private Double applyLiqPrem;
	private Double liqPrem;
	private Double vol;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	
	public BizLiqPremium() {
	
	}
	public BizLiqPremium(double liqPrem) {
		this.liqPrem = liqPrem;
	}
	
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
		return toString(",");
	}
	
	public String toString(String delimeter) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(baseYymm).append(delimeter)
			   .append(applyBizDv).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(applyLiqPrem).append(delimeter)
			   .append(liqPrem).append(delimeter)
			   .append(vol)
//			   .append(lastModifiedBy).append(delimeter)
//			   .append(lastUpdateDate)
			   ;
		return builder.toString();
	}
	
}
