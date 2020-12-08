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
@IdClass(DiscRateStatsExtIrId.class)
@Table( name ="EAS_DISC_RATE_STATS_EXT_IR")
@Getter
@Setter
public class DiscRateStatsExtIr implements Serializable {

	private static final long serialVersionUID = 6207498187147536428L;

	@Id
	private String baseYymm;	
	
	@Id
	private String discRateCalcTyp;
	
	@Id
	private String extIrCd;

	@Id
	private String indpVariable;
	
	private Double avgMonNum;
	
	private Double regrConstant;
	
	private Double regrCoef;
	
	private String remark;	
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	
	
	public DiscRateStatsExtIr() {}

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}


