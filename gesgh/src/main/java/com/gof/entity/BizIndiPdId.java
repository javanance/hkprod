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
public class BizIndiPdId implements Serializable, EntityIdentifier {	
	
	private static final long serialVersionUID = 3190211902934726894L;

	@Column(name="BASE_YYMM", nullable=false)
	private String baseYymm;
	
	@Column(name = "APPL_BIZ_DV", nullable=false)
	private String applyBizDv;
	
	@Column(name="CB_GRD_CD", nullable=false)
	private String cbGrdCd;
	
	@Column(name="MAT_CD", nullable=false)
	private String matCd;	

	@Column(name="CRD_EVAL_AGNCY_CD", nullable=false)
	private String crdEvalAgncyCd;
	
	public BizIndiPdId() {}

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
		return "IndiCrdGrdCumPdId [baseYymm=" + baseYymm + ", cbGrdCd=" + cbGrdCd + ", matCd=" + matCd
				+ ", crdEvalAgncyCd=" + crdEvalAgncyCd + "]";
	}
		
}
