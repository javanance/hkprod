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
@IdClass(BizDiscRateId.class)
@Table(name ="EAS_BIZ_APLY_DISC_RATE")
@Getter
@Setter
public class BizDiscRate implements Serializable {

	private static final long serialVersionUID = -8062063262761022307L;

	@Id	private String baseYymm;
	@Id
	@Column(name = "APPL_BIZ_DV", nullable=false)
	private String applyBizDv;
	
	@Id	private String intRateCd;
	@Id	private String matCd;
	
	private Double baseDiscRate;
	private Double adjRate;
	private Double discRate;
	private Double avgFwdRate;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public BizDiscRate() {}

	public String getBaseYymm() {
		return baseYymm;
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
			   .append(applyBizDv).append(delimeter)
			   .append(intRateCd).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(baseDiscRate).append(delimeter)
			   .append(adjRate).append(delimeter)
			   .append(discRate).append(delimeter)
			   
			   .append(avgFwdRate).append(delimeter)
			   .append(lastModifiedBy).append(delimeter)
			   .append(lastUpdateDate);
		
		return builder.toString();
	}
}