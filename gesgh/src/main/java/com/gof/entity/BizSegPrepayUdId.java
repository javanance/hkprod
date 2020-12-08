package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class BizSegPrepayUdId implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;
	@Column(name ="APPL_ST_YYMM")
	private String applyStartYymm;
	
	@Column(name ="APPL_BIZ_DV")
	private String applyBizDv;
	
	@Column(name ="SEG_ID")
	private String segId;
	
	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
