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
@IdClass(InvestManageCostUdId.class)
@Table(name ="EAS_USER_INV_MGT_COST")
@Getter
@Setter
public class InvestManageCostUd implements Serializable {

	private static final long serialVersionUID = 3991701278375058071L;

	@Id
	private String applStYymm; 

	@Id
	private String mgtAsstTyp;
	
	private String applEdYymm; 
	private Double mgtAsstAmt;
	private Double invCostAmt;
	
	private Double invCostRate;
		
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
		
	public InvestManageCostUd() {}

	

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

//	@Transient
//	public Map<String, Double>  getInvCostRateByAccount(){
//		Map<String, Double> rst = new HashMap<>();
//		rst.put("8300_��⹫���" , getLongInvCostRate()==null? 0.0:getLongInvCostRate());
//		rst.put("8100_���ο���" , getPensInvCostRate() ==null? 0.0: getPensInvCostRate() );
//		return rst;		
//	}
	
//	public BizDiscRateStat decorate(String bizDv, IStat stat) {
//		
//		BizDiscRateStat rst = new BizDiscRateStat();
//		
//		rst.setBaseYymm(stat.getBaseYymm());
//		rst.setApplyBizDv(bizDv);
//		rst.setIntRateCd(stat.getIntRateCd());
//		rst.setIndpVariable(stat.getIndpVariable());
//		rst.setDepnVariable(stat.getDepnVariable());
//		rst.setRegrCoef(stat.getRegrCoef());
//		rst.setRegrConstant(stat.getRegrConstant()- stat.getRegrCoef() * this.getInvCostRate());
//		rst.setAdjRate(stat.getAdjRate());
//		rst.setRemark(stat.getRemark());
//		rst.setAvgMonNum(stat.getAvgMonNum());
//		rst.setLastModifiedBy("ESG");
//		rst.setLastUpdateDate(LocalDateTime.now());
//		
//		return rst;
//	}	
}


