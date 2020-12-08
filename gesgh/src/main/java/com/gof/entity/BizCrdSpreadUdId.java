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
public class BizCrdSpreadUdId implements Serializable, EntityIdentifier {	
	
	private static final long serialVersionUID = -5962004839804687117L;
	
	
	@Column(name="APPL_ST_YYMM", nullable=false)
	private String applStYymm;
	
	@Column(name="APPL_BIZ_DV", nullable=false)
	private String applyBizDv;
	private String crdGrdCd;	
	private String matCd;
	
	public BizCrdSpreadUdId() {}


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
