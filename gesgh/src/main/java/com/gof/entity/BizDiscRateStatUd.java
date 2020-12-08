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
@IdClass(BizDiscRateStatUdId.class)
@Table(name ="EAS_USER_DISC_RATE_STATS")
@Getter
@Setter
public class BizDiscRateStatUd implements Serializable {

	private static final long serialVersionUID = -8062063262761022307L;

	@Id
	@Column(name = "APPL_ST_YYMM", nullable=false)
	private String applyStartYymm;
	
	@Id
	@Column(name = "APPL_BIZ_DV", nullable=false)
	private String applyBizDv;
	
	@Id
	private String intRateCd;

	@Id
	private String indpVariable;
	
	@Id
	private String depnVariable;
	
	@Column(name = "APPL_ED_YYMM")
	private String applyEndYymm;
	
	private Double avgMonNum;
	private Double regrConstant;
	private Double regrCoef;
	private Double adjRate;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public BizDiscRateStatUd() {}

	@Override
	public String toString() {
		return toString(",");
	}
	
	public String getIndiVariableMatCd() {
		if(indpVariable.contains("M")) {
			return "M" +String.format("%04d", Integer.parseInt(indpVariable.replace("KTB","").replace("M", "").trim()));
		}else {
			return "M" +String.format("%04d", 12 * Integer.parseInt(indpVariable.replace("KTB","").replace("Y", "").trim()));
		}
	}
	
	public String toString(String delimeter) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(applyStartYymm).append(delimeter)
			   .append(applyBizDv).append(delimeter)
			   .append(intRateCd).append(delimeter)
			   .append(indpVariable).append(delimeter)
			   .append(applyEndYymm).append(delimeter)
			   .append(regrConstant).append(delimeter)
			   .append(regrCoef).append(delimeter)
			   .append(adjRate).append(delimeter)
			   .append(lastModifiedBy).append(delimeter)
			   .append(lastUpdateDate);
		
		return builder.toString();
	}
	
//	public BizDiscRateStat decorate(String bizDv, IStat stat) {
//		
//		BizDiscRateStat rst = new BizDiscRateStat();
//		
//		rst.setBaseYymm(stat.getBaseYymm());
//		rst.setApplyBizDv(bizDv);
//		rst.setIntRateCd(stat.getIntRateCd());
//		rst.setIndpVariable(this.getIndpVariable());
//		rst.setDepnVariable(this.getDepnVariable());
//		rst.setRegrCoef(this.getRegrCoef());
//		rst.setRegrConstant(this.getRegrConstant());
//		rst.setAdjRate(this.getAdjRate());
//		rst.setRemark("");
//		rst.setAvgMonNum(this.getAvgMonNum());
//		rst.setLastModifiedBy("ESG");
//		rst.setLastUpdateDate(LocalDateTime.now());
//		
//		return rst;
//	}	

	public BizDiscRateStat convert(String bssd) {
		BizDiscRateStat rst = new BizDiscRateStat();
		
		rst.setBaseYymm(bssd);
		rst.setApplyBizDv(this.applyBizDv);
		rst.setIntRateCd(this.intRateCd);
		rst.setIndpVariable(this.indpVariable);
		rst.setDepnVariable(this.depnVariable);
		rst.setAvgMonNum(this.avgMonNum);
		rst.setRegrCoef(this.regrCoef);
		rst.setRegrConstant(this.regrConstant);
		rst.setAdjRate(this.adjRate);
		rst.setRemark("");
		rst.setLastModifiedBy("USER");
		rst.setLastUpdateDate(LocalDateTime.now());

		return rst;
	}
	
	
	public BizDiscRateStatUd merge(String bssd, double adjRate, double invCostRate) {
		BizDiscRateStatUd rst = new BizDiscRateStatUd();
		
		rst.setApplyStartYymm(bssd);
		rst.setApplyEndYymm(bssd);
		rst.setApplyBizDv(this.applyBizDv);
		rst.setIntRateCd(this.intRateCd);
		rst.setIndpVariable(this.indpVariable);
		rst.setDepnVariable(this.depnVariable);
		rst.setAvgMonNum(this.avgMonNum);
		rst.setRegrCoef(this.regrCoef);
		rst.setRegrConstant(this.regrConstant - this.regrCoef*invCostRate);
		rst.setAdjRate(adjRate);
		rst.setLastModifiedBy("USER");
		rst.setLastUpdateDate(LocalDateTime.now());

		return rst;
	}
	
	
}