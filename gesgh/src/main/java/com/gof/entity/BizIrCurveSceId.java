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
public class BizIrCurveSceId implements Serializable, EntityIdentifier {
	
	private static final long serialVersionUID = 205371050298889931L;

	@Column(name="BASE_YYMM", nullable=false)
	private String baseYymm;
	
	@Column(name ="APPL_BIZ_DV")
	private String applBizDv;
	
	@Column(name="IR_CURVE_ID", nullable=false)
	private String irCurveId;	

	@Column(name="MAT_CD", nullable=false)
	private String matCd;

	@Column(name="SCE_NO", nullable=false)
	private String sceNo;

	
	public BizIrCurveSceId() {}


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
		return "IrSceId [baseYymm=" + baseYymm + ", irModelId=" + applBizDv + ", matCd=" + matCd + ", sceNo=" + sceNo
				+ ", irCurveId=" + irCurveId + "]";
	}
			
}
