package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class InvestManageCostUdId implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;
	
	@Column(name ="APPL_ST_YYMM")
	private String applStYymm; 

	@Column(name ="MGT_ASST_TYP")
	private String mgtAsstTyp;
	
	public InvestManageCostUdId() {
	}
	
	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}
	
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}



	
}
