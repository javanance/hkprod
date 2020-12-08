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
@IdClass(BizDiscRateAdjUdId.class)
@Table( name ="EAS_USER_DISC_RATE_ADJ")
@Getter
@Setter
public class BizDiscRateAdjUd implements Serializable {

	private static final long serialVersionUID = 6207498187147536428L;

	@Id
	private String applStYymm;	
	
	@Id
	private String applBizDv;

	
	@Id
	private String intRateCd;
	
	
	private String applEdYymm;
	
	private Double applAdjRate;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public BizDiscRateAdjUd() {}


	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
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
//		rst.setRegrConstant(stat.getRegrConstant());
//		rst.setAdjRate(this.applAdjRate);
//		rst.setRemark(stat.getRemark());
//		rst.setAvgMonNum(stat.getAvgMonNum());
//		rst.setLastModifiedBy("ESG");
//		rst.setLastUpdateDate(LocalDateTime.now());
//		
//		return rst;
//	}
}


