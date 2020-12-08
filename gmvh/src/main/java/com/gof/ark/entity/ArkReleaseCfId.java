package com.gof.ark.entity;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.gof.enums.EBoolean;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;
import com.gof.enums.ETiming;

import lombok.Getter;


@Embeddable
@Getter
public class ArkReleaseCfId implements java.io.Serializable  {

	private static final long serialVersionUID = -8151467682976876533L;
	
	private String baseYymm;
	private String gocId;
	
	@Enumerated(EnumType.STRING)
	private ELiabType liabType;

	@Enumerated(EnumType.STRING)
	private EContStatus stStatus;
	
	@Enumerated(EnumType.STRING)
	private EContStatus endStatus;
	
	@Enumerated(EnumType.STRING)
	private EBoolean newContYn;
	
	private String arkRunsetId;
//		private String runsetId;
	private String cfKeyId;
	
	
	@Enumerated(EnumType.STRING)
	private ETiming cfTiming;
	
	@Enumerated(EnumType.STRING)
	private EBoolean outflowYn;
	
	private Integer setlAftPassMmcnt;				//Change for update 61st cf !!!! 
	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	@Override
	public String toString() {
		 StringBuilder 	builder = new StringBuilder();
	 	 return builder
	 		.append(baseYymm).append(",")
	 	 	.append(gocId).append(",")
	 	 	.append(stStatus).append(",")
	 	 	.append(endStatus).append(",")
	 	 	.append(newContYn).append(",")
	 	 	.append(arkRunsetId).append(",")
	 	 	.append(cfKeyId).append(",")
	 	 	.append(setlAftPassMmcnt).append(",")
	 	 	.toString();
	}
}
