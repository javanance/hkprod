package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(BizDiscRateStatId.class)
@Table(name ="EAS_BIZ_APLY_DISC_RATE_STAT")
@Getter
@Setter
public class BizDiscRateStat implements Serializable {

	private static final long serialVersionUID = -8062063262761022307L;

	@Id	private String baseYymm;
	@Id
	@Column(name = "APPL_BIZ_DV", nullable=false)
	private String applyBizDv;
	
	@Id	private String intRateCd;
	@Id	private String indpVariable;
	
//	@Id
	@Transient
	private String depnVariable;
	
	private Double avgMonNum;
	private Double regrConstant;
	private Double regrCoef;
	private Double invCostRate;
	private Double adjRate;
	
	private String remark;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public BizDiscRateStat() {}


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
		
		builder.append(baseYymm).append(delimeter)
			   .append(applyBizDv).append(delimeter)
			   .append(intRateCd).append(delimeter)
			   .append(indpVariable).append(delimeter)
			   .append(regrConstant).append(delimeter)
			   .append(regrCoef).append(delimeter)
			   .append(adjRate).append(delimeter)
			   .append(remark).append(delimeter)
			   .append(lastModifiedBy).append(delimeter)
			   .append(lastUpdateDate);
		
		return builder.toString();
	}
	
	public DiscRateStats convertToDiscRateStst() {
		DiscRateStats rst = new DiscRateStats();
		
		rst.setBaseYymm(this.baseYymm);
		rst.setDiscRateCalcTyp(this.applyBizDv);
		rst.setIntRateCd(this.intRateCd);
		rst.setAvgNum(this.avgMonNum);
		rst.setDepnVariable("BASE_DISC");
		rst.setIndpVariable(this.indpVariable);
		rst.setRegrCoef(this.regrCoef);
		rst.setRegrConstant(this.regrConstant);
		rst.setRemark("");
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
	
	public BizDiscRateStat decorate(BizDiscRateStatUd statUd) {
		if(statUd==null) {
			return this;
		}
		
		BizDiscRateStat rst = new BizDiscRateStat();
		
		rst.setBaseYymm(this.baseYymm);
		rst.setApplyBizDv(statUd.getApplyBizDv());
		rst.setIntRateCd(this.intRateCd);
		rst.setAvgMonNum(statUd.getAvgMonNum());
		rst.setDepnVariable("BASE_DISC");
		rst.setIndpVariable(statUd.getIndpVariable());
		rst.setRegrCoef(statUd.getRegrCoef());
		rst.setRegrConstant(statUd.getRegrConstant());
		rst.setAdjRate(statUd.getAdjRate());
		rst.setRemark("");
		rst.setLastModifiedBy("USER");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
	public BizDiscRateStat decorate(BizDiscRateAdjUd adjRateUd) {
		if(adjRateUd==null) {
			return this;
		}
		BizDiscRateStat rst = new BizDiscRateStat();
		
		rst.setBaseYymm(this.baseYymm);
		rst.setApplyBizDv(this.getApplyBizDv());
		rst.setIntRateCd(this.intRateCd);
		rst.setAvgMonNum(this.getAvgMonNum());
		rst.setDepnVariable("BASE_DISC");
		rst.setIndpVariable(this.getIndpVariable());
		rst.setRegrCoef(this.getRegrCoef());
		rst.setRegrConstant(this.getRegrConstant());
		rst.setAdjRate(adjRateUd.getApplAdjRate());
		rst.setRemark("");
		rst.setLastModifiedBy("USER");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
	public BizDiscRateStat decorate(double invCost) {
		BizDiscRateStat rst = new BizDiscRateStat();
		
		rst.setBaseYymm(this.baseYymm);
		rst.setApplyBizDv(this.getApplyBizDv());
		rst.setIntRateCd(this.intRateCd);
		rst.setAvgMonNum(this.getAvgMonNum());
		rst.setDepnVariable("BASE_DISC");
		rst.setIndpVariable(this.getIndpVariable());
		rst.setRegrCoef(this.getRegrCoef());
		rst.setRegrConstant(this.getRegrConstant()- this.getRegrCoef() * invCost);
		rst.setAdjRate(this.adjRate);
		rst.setRemark("");
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
}