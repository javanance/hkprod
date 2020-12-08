package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class TransitionMatrixId implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;	
	@Column(name = "BASE_YYYY") 
	private String baseYyyy;
	
	@Column(name = "TM_TYPE") 
	private String tmType;

	@Column(name = "FROM_CRD_GRD_CD") 
	private String fromGrade;
	
	@Column(name = "TO_CRD_GRD_CD") 
	private String toGrade;
	
	
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
