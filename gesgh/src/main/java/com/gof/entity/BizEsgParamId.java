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
public class BizEsgParamId implements Serializable, EntityIdentifier {

	private static final long serialVersionUID = -1540161178787348772L;
	  
	@Column(name="BASE_YYMM", nullable=false)			//TODO column name change !!!
	private String baseYymm;
	
	@Column(name="APPL_BIZ_DV", nullable=false)		
	private String applyBizDv;
	
	@Column(name="IR_MODEL_ID", nullable=false)
	private String irModelId;
	
	@Column(name="PARAM_TYP_CD", nullable=false)
	private String paramTypCd;
	
	@Column(name="MAT_CD", nullable=false)
	private String matCd;
	
	public BizEsgParamId() {}
	
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
