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
public class CreditSpreadId implements Serializable, EntityIdentifier {	
	
	private static final long serialVersionUID = -5962004839804687117L;

	@Column(name="BASE_YYMM", nullable=false)
	private String baseYymm;

	@Column(name="CRD_GRD_CD", nullable=false)
	private String crdGrdCd;


	@Column(name="MAT_CD", nullable=false)
	private String matCd;	
	
	public CreditSpreadId() {}

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
		
}
