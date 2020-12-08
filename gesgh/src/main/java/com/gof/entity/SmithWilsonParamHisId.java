package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class SmithWilsonParamHisId implements Serializable, EntityIdentifier {
	
	private static final long serialVersionUID = -574128152521871580L;

	@Column(name="APPL_ST_YYMM", nullable=false)
	private String applStartYymm;
	
	
	@Column(name="APPL_ED_YYMM", nullable=false)
	private String applEndYymm;
	
	@Column(name="CUR_CD", nullable=false)
	private String curCd;	
	
	public SmithWilsonParamHisId() {}

	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
