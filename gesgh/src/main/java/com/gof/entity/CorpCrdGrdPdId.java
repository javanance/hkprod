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
public class CorpCrdGrdPdId implements Serializable, EntityIdentifier {	
	
	private static final long serialVersionUID = 7758100265355791143L;

	@Column(name="BASE_YYMM", nullable=false)
	private String baseYymm;

	@Column(name="CRD_EVAL_AGNCY_CD", nullable=false)
	private String crdEvalAgncyCd;

	@Column(name="CRD_GRD_CD", nullable=false)
	private String crdGrdCd;	
	
	public CorpCrdGrdPdId() {}

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
		return "CorpCrdGrdPdId [baseYymm=" + baseYymm + ", crdEvalAgncyCd=" + crdEvalAgncyCd + ", crdGrdCd=" + crdGrdCd
				+ "]";
	}
		
}
