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
public class RefPortYieldId implements Serializable, EntityIdentifier {
	
	private static final long serialVersionUID = 4759550982761000572L;

	@Column(name="BASE_YYMM", nullable=false)
	private String baseYymm;
	
	@Column(name="ASST_CLASS_TYP_CD", nullable=false)
	private String asstClassTypCd;
	
	@Column(name="MAT_CD", nullable=false)
	private String matCd;
	
	public RefPortYieldId() {}

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
		return "RefPortYieldId [baseYymm=" + baseYymm + ", asstClassTypCd=" + asstClassTypCd + ", matCd=" + matCd + "]";
	}
	
}
