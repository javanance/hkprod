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
public class IrSceId implements Serializable, EntityIdentifier {
	
	private static final long serialVersionUID = 205371050298889931L;

	@Column(name="BASE_DATE", nullable=false)
	private String baseDate;
	
	@Column(name="IR_MODEL_ID", nullable=false)
	private String irModelId;

	@Column(name="MAT_CD", nullable=false)
	private String matCd;

	@Column(name="SCE_NO", nullable=false)
	private String sceNo;

	@Column(name="IR_CURVE_ID", nullable=false)
	private String irCurveId;	
	
	public IrSceId() {}


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
		return "IrSceId [baseDate=" + baseDate + ", irModelId=" + irModelId + ", matCd=" + matCd + ", sceNo=" + sceNo
				+ ", irCurveId=" + irCurveId + "]";
	}
			
}
