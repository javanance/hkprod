package com.gof.ark.entity;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;

import com.gof.entity.MstCalc;

import lombok.Getter;

/**
 * 
 */
@Embeddable
@Getter
public class ArkBoxMapId implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;

//	private String runsetId;
//	private String calcId;
	
	
//	@JoinColumn(name = "CALC_ID")
	private MstCalc mstCalc;
	
	@JoinColumn(name = "ARK_RUNSET_ID")
	private ArkMstRunset arkMstRunset;
	
	private String itemId;
//	
//	private MstRunset mstRunset;
//	private MstCalc mstCalc;
	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
