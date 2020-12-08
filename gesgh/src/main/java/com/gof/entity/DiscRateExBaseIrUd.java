package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name ="EAS_USER_DISC_RATE_EX_BASE_IR")
@Getter
@Setter
public class DiscRateExBaseIrUd implements Serializable, EntityIdentifier {
	
	private static final long serialVersionUID = -295414795997288198L;

	@Id
	@Column(name="BASE_YYMM", nullable=false)
	private String baseYymm;	
	
	@Column(name="KTB_Y5_IR")
	private Double ktbY5Ir;
	
	@Column(name="CORP_Y3_IR")
	private Double corpY3Ir;
	
	@Column(name="MNSB_Y1_IR")
	private Double mnsbY1Ir;
	
	@Column(name="CD_91_IR")
	private Double cd91Ir;

	public DiscRateExBaseIrUd() {}


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
		return "UserDiscRateExBaseIr [baseYymm=" + baseYymm + ", ktbY5Ir=" + ktbY5Ir + ", corpY3Ir=" + corpY3Ir
				+ ", mnsbY1Ir=" + mnsbY1Ir + ", cd91Ir=" + cd91Ir + "]";
	}

}


