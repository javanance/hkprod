package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;


@Embeddable
@Getter
@Setter
public class BizStockSceId implements Serializable {
	
	private static final long serialVersionUID = 7346541519373980852L;

	private String baseYymm;
    private String applBizDv;
    private String stdAsstCd;	
    private String sceNo;
    private String matCd;
	
	public BizStockSceId() {}

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
