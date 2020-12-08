package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;

import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;


@Embeddable
@Getter
public class EsgRandomId implements Serializable {

	private static final long serialVersionUID = -9072807206431485429L;

	private String baseYymm;
	private String stdAsstCd;
	private String volCalcId;
	private Integer sceNo;
	private Integer matNum;
	
	public EsgRandomId() {}

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
		
}
