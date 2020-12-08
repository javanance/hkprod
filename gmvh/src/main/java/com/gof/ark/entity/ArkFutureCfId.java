package com.gof.ark.entity;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.gof.enums.EBoolean;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;

import lombok.Getter;


@Embeddable
@Getter
public class ArkFutureCfId implements java.io.Serializable  {

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
	private String cfKeyId;
	
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
	 	 	.toString();
	}
}
