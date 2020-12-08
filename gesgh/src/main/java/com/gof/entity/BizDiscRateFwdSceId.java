package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class BizDiscRateFwdSceId implements Serializable {
	private static final long serialVersionUID = 5021899302460181074L;

	@Column(name="BASE_YYMM", nullable=false)
	private String baseYymm;
	
	@Column(name="APPL_BIZ_DV", nullable=false)
	private String applyBizDv;
	
	@Column(name="IR_CURVE_ID", nullable=false)
	private String irCurveId;
	
	@Column(name="SCE_NO", nullable=false)
	private String sceNo;
	
	@Column(name="MAT_CD", nullable=false)
	private String matCd;	

	@Column(name="AVG_MON_NUM", nullable=false)
	private Double avgMonNum;
	
	@Column(name="FWD_NO", nullable=false)
	private String fwdNo;	
	
	public BizDiscRateFwdSceId() {}

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
		
}
