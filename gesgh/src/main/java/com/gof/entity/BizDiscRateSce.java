package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(BizDiscRateSceId.class)
@Table(name ="EAS_BIZ_APLY_DISC_RATE_SCE")
@Getter
@Setter
public class BizDiscRateSce implements Serializable {

	private static final long serialVersionUID = 9014011291962830436L;

	@Id
	private String baseYymm;	
	
	@Id
	private String intRateCd;
	
	@Id
	private String applBizDv;
	
	@Id
	private String sceNo;
	
	@Id
	private String matCd;	
	
	
	private Double baseDiscRate;
	
	private Double adjRate;
	
	private Double discRate;	
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public BizDiscRateSce() {}


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
		return toString(",");
	}
	
	public String toString(String delimeter) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(baseYymm).append(delimeter)
			   .append(applBizDv).append(delimeter)
			   .append(intRateCd).append(delimeter)
			   .append(sceNo).append(delimeter)
			   .append(matCd).append(delimeter)
			   
			   
			   .append(baseDiscRate).append(delimeter)
			   .append(adjRate).append(delimeter)
			   .append(discRate).append(delimeter)
			   
			   .append(lastModifiedBy).append(delimeter)
			   .append(lastUpdateDate)
			   ;
		
		return builder.append("\n").toString();
	}
	
	public String toBizString(String delimeter) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(baseYymm).append(delimeter)
			   .append(applBizDv).append(delimeter)
			   .append(intRateCd).append(delimeter)
			   .append(sceNo).append(delimeter)
			   .append(matCd).append(delimeter)
			   
			   
			   .append(baseDiscRate).append(delimeter)
			   .append(adjRate).append(delimeter)
			   .append(discRate).append(delimeter)
			   
			   .append(lastModifiedBy).append(delimeter)
			   .append(lastUpdateDate)
			   ;
		
		return builder.append("\n").toString();
	}
	
}


