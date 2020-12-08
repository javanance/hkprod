package com.gof.entity;

import java.io.Serializable;

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
@IdClass(DiscRateWghtId.class)
@Table(name ="EAS_DISC_RATE_WGHT")
@FilterDef(name="discRateWghtEqBaseYymm", parameters= { @ParamDef(name="baseYymm", type="string") })
@Getter
@Setter
public class DiscRateWght implements Serializable {

	private static final long serialVersionUID = -1745251535302343975L;
	
	@Id
	private String baseYymm;
	
	@Id
	private String intRateCd;
	
	@Column(name="KTB_Y5_WGHT")
	private Double ktbY5Wght;
	
	@Column(name="CORP_Y3_WGHT")
	private Double corpY3Wght;
	
	@Column(name="MNSB_Y1_WGHT")
	private Double mnsbY1Wght;
	
	@Column(name="CD_91_WGHT")
	private Double cd91Wght;
	
	private Double discRateSpread;
	
	@Column(name="EXTR_IR_WGHT")
	private Double extrIrWght;
	

	public DiscRateWght() {}

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
		return "DiscRateWghtUd [baseYymm=" + baseYymm + ", intRateCd=" + intRateCd + ", ktbY5Wght=" + ktbY5Wght
				+ ", corpY3Wght=" + corpY3Wght + ", mnsbY1Wght=" + mnsbY1Wght + ", cd91Wght=" + cd91Wght
				+ ", discRateSpread=" + discRateSpread + "]";
	}
	
}


