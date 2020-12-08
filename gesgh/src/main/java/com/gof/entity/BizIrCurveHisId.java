package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class BizIrCurveHisId implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;
	@Column(name ="BASE_YYMM")
	private String baseYymm;
	
	@Column(name ="APPL_BIZ_DV")
	private String applBizDv;
	

	@Column(name ="IR_CURVE_ID")
	private String irCurveId;
	
	@Column(name ="MAT_CD")
	private String matCd;
	
		
	public BizIrCurveHisId() {
	}


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

	
}
