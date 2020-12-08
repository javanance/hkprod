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
public class BizSegLgdId implements Serializable, EntityIdentifier {
	
	private static final long serialVersionUID = -574128152521871580L;

	@Column(name="BASE_YYMM", nullable=false)
	private String baseYymm;
	
	@Column(name="SEG_ID", nullable=false)
	private String segId;	

	@Column(name="APPL_BIZ_DV", nullable=false)
	private String applyBizDv;
	
	@Column(name="LGD_CALC_TYP_CD", nullable=false)
	private String lgdCalcTypCd;

	public BizSegLgdId() {}

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

	@Override
	public String toString() {
		return "SegLgdId [baseYymm=" + baseYymm + ", lgdCalcTypCd=" + lgdCalcTypCd + ", segId=" + segId + "]";
	}
	
}
