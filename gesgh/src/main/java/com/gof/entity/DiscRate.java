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
@IdClass(DiscRateId.class)
@Table( name ="EAS_DISC_RATE")
@Getter
@Setter
public class DiscRate implements Serializable {

	private static final long serialVersionUID = -8062063262761022307L;

	@Id
	private String baseYymm;
	
	@Id
	private String intRateCd;

	@Id
	private String discRateCalcTyp;

	@Id
	private String matCd;
	private Double mgtYield;
	private Double exBaseIr;
	private Double baseDiscRate;
	private Double exBaseIrWght;
	private Double adjRate;
	private Double discRate;
	
	private Double avgFwdRate;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public DiscRate() {}
	
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
			   .append(intRateCd).append(delimeter)
			   .append(discRateCalcTyp).append(delimeter)
			   .append(matCd).append(delimeter)
			   
			   .append(mgtYield).append(delimeter)
			   .append(exBaseIr).append(delimeter)
			   
			   .append(baseDiscRate).append(delimeter)
			   .append(exBaseIrWght).append(delimeter)
			   .append(adjRate).append(delimeter)
			   .append(discRate).append(delimeter)
			   
			   .append(avgFwdRate).append(delimeter)
			   .append(lastModifiedBy).append(delimeter)
			   .append(lastUpdateDate);
		
		return builder.toString();
	}
	
	public BizDiscRate convertTo() {
		BizDiscRate temp = new BizDiscRate();

		temp.setBaseYymm(this.baseYymm);
		temp.setApplyBizDv(this.discRateCalcTyp);
		temp.setIntRateCd(this.intRateCd);
		temp.setMatCd(this.matCd);
		
//		temp.setBaseDiscRate(aa.getBaseDiscRate());
		temp.setBaseDiscRate(this.baseDiscRate);
		
		temp.setAdjRate(this.adjRate);
		temp.setDiscRate(this.discRate);
		temp.setAvgFwdRate(this.avgFwdRate);
		
		temp.setLastModifiedBy("ESG");
		temp.setLastUpdateDate(LocalDateTime.now());
		
		return temp;
	}
}