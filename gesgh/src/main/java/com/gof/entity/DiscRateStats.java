package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(DiscRateStatsId.class)
@Table(name ="EAS_DISC_RATE_STATS")
@FilterDef(name="discRateStatEqApplStYymm", parameters= { @ParamDef(name="applStYymm", type="string") })
@Getter
@Setter
public class DiscRateStats implements Serializable {

	private static final long serialVersionUID = 6207498187147536428L;

	@Id	private String baseYymm;	
	@Id	private String discRateCalcTyp;
	@Id	private String intRateCd;
	@Id	private String depnVariable;
	@Id	private String indpVariable;
	
	private Double regrConstant;
	private Double regrCoef;
	private String remark;	
	
	@Column(name ="AVG_MON_NUM")			// mismatch column
	private Double avgNum;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	
	public DiscRateStats() {}
	
	public BizDiscRateStat convertToBizDiscRateStst(String bizDv) {
		BizDiscRateStat rst = new BizDiscRateStat();
		double invCostRate = this.getRemark().contains("INV_COST")? Double.valueOf(this.getRemark().split(":")[1]): 0.0; 
		
		rst.setBaseYymm(this.getBaseYymm());
		rst.setApplyBizDv(bizDv);
		rst.setIntRateCd(this.getIntRateCd());
		rst.setAvgMonNum(this.getAvgNum());
		rst.setDepnVariable("BASE_DISC");
		rst.setIndpVariable(this.getIndpVariable());
		rst.setRegrCoef(this.getRegrCoef());
		rst.setRegrConstant(this.getRegrConstant());
		rst.setAdjRate(1.0);
		rst.setInvCostRate(invCostRate);
		rst.setRemark("");
		rst.setLastModifiedBy(this.getLastModifiedBy());
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}

	@Transient
	public String getIndiVariableMatCd() {
		if(indpVariable.contains("M")) {
			return "M" +String.format("%04d", Integer.parseInt(indpVariable.replace("KTB","").replace("M", "").trim()));
		}else {
			return "M" +String.format("%04d", 12 * Integer.parseInt(indpVariable.replace("KTB","").replace("Y", "").trim()));
		}
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
		return "DiscRateStats [baseYymm=" + baseYymm + ", discRateCalcTyp=" + discRateCalcTyp + ", intRateCd="
				+ intRateCd + ", depnVariable=" + depnVariable + ", indpVariable=" + indpVariable + ", avgNum=" + avgNum  
				+ ", regrConstant=" + regrConstant + ", regrCoef=" + regrCoef + ", remark=" + remark + "]";
	}
}


