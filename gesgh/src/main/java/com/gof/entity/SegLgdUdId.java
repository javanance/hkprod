package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;


@Embeddable
@Getter
@Setter
public class SegLgdUdId implements Serializable, EntityIdentifier {
	
	private static final long serialVersionUID = -4017379086329605634L;

	@Column(name="APPL_ST_YYMM", nullable=false)
	private String applStYymm;
	
	@Column(name="LGD_CALC_TYP_CD", nullable=false)
	private String lgdCalcTypCd;

	@Column(name="SEG_ID", nullable=false)
	private String segId;	
	
	public SegLgdUdId() {}

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
		return "SegLgdUdId [applStYymm=" + applStYymm + ", lgdCalcTypCd=" + lgdCalcTypCd + ", segId=" + segId + "]";
	}
	
}
