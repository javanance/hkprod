package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
public class BizStockParamId implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;
	
	private String baseYymm;
	private String applBizDv;
	private String stdAsstCd;
	private String paramTypCd;	
	private Integer matDayNum;	
	
	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
}
