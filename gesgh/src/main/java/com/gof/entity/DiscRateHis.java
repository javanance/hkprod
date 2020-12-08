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
@IdClass(DiscRateHisId.class)
@Table(name ="EAS_DISC_RATE_HIS")
@FilterDef(name="discRateHisEqBaseYymm", parameters= { @ParamDef(name="baseYymm",  type="string")} )
@Getter
@Setter
public class DiscRateHis implements Serializable {
	private static final long serialVersionUID = 5044731109927550489L;

	@Id
	private String baseYymm;	
	
	@Id
	private String intRateCd;
	
	@Id
	@Column(name = "DISC_RATE_CALC_DTL")
	private String acctDvCd;

	private Double exBaseIr;

	@Column(name = "MGT_ASST_REVN_RATE")
	private Double mgtAsstYield;
	
	private Double baseDiscRate;
	private Double applDiscRate;

	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	
	public double getDiscRateAdjRate() {
		if(baseDiscRate==null || baseDiscRate==0.0) {
			return 1.0;
		}
		if(applDiscRate==null || applDiscRate==0.0) {
			return 1.0;
		}
		return applDiscRate/ baseDiscRate ;
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
		return "DiscRateHis [baseYymm=" + baseYymm + ", intRateCd=" + intRateCd   
				+ ", acctDvCd="	+ acctDvCd 
				+ ", mgtAsstYield=" + mgtAsstYield
				+ ", exBaseIr=" + exBaseIr 
				+ ", baseDiscRate=" + baseDiscRate
				+ ", applDiscRate=" + applDiscRate + "]";
	}	
	
}


