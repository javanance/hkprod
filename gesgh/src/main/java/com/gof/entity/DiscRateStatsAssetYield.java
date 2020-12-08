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
@IdClass(DiscRateStatsAssetYieldId.class)
@Table(name ="EAS_DISC_RATE_STATS_ASST_YIELD")
@Getter
@Setter
public class DiscRateStatsAssetYield implements Serializable {

	private static final long serialVersionUID = 6207498187147536428L;

	@Id
	private String baseYymm;	
	
	@Id
	private String discRateCalcTyp;
	
	@Id
	private String acctDvCd;

	@Id
	private String indpVariable;
	
	private Double avgMonNum;
	
	private Double regrConstant;
	
	private Double regrCoef;
	
	private String remark;	
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	
	
	public DiscRateStatsAssetYield() {}

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
			   .append(discRateCalcTyp).append(delimeter)
			   .append(acctDvCd).append(delimeter)
			   .append(indpVariable).append(delimeter)
			   .append(avgMonNum).append(delimeter)
			   .append(regrConstant).append(delimeter)
			   .append(regrCoef).append(delimeter)
			   .append(remark)
//			   .append(lastUpdateDate)
			   ;

		return builder.toString();
	}
}


