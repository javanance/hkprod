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
public class ParamCalcHisId implements Serializable, EntityIdentifier {

	private static final long serialVersionUID = -5314286751496179811L;

	@Column(name="BASE_YYMM", nullable=false)
	private String baseYymm;
	
	@Column(name="IR_MODEL_TYP", nullable=false)
	private String irModelTyp;
	
	@Column(name="PARAM_CALC_CD", nullable=false)		
	private String paramCalcCd; 

	
	@Column(name="PARAM_TYP_CD", nullable=false)
	private String paramTypCd;

	@Column(name="MAT_CD", nullable=false)
	private String matCd;	
	
	public ParamCalcHisId() {}

	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
}
