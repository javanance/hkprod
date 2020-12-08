package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.interfaces.BaseValue;
import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(BizInflationId.class)
@Table(name ="EAS_BIZ_APLY_INFLATION")
@Getter
@Setter
public class BizInflation implements Serializable, EntityIdentifier, BaseValue {

	private static final long serialVersionUID = 2721034625992403875L;

	@Id
	private String baseYymm;
	@Id
	@Column(name="APPL_BIZ_DV", nullable=false)
	private String applyBizDv;
	
	@Id
	private String inflationId;	
	
	private Double inflationIndex;
	
	private Double inflation;
	
	private Double mgmtTargetLowerVal;
	
	private Double mgmtTargetUpperVal;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public BizInflation() {}

	@Override
	public Double getBasicValue() {
		return inflationIndex;
	}

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

	@Override
	public String toString() {
		return toString(",");
	}
	
	public String toString(String delimeter) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(baseYymm).append(delimeter)
			   .append(inflationId).append(delimeter)
			   .append(inflationIndex).append(delimeter)
			   .append(inflation).append(delimeter)
			   .append(mgmtTargetLowerVal).append(delimeter)
			   .append(mgmtTargetUpperVal).append(delimeter)
//			   .append(lastModifiedBy).append(delimeter)
//			   .append(lastUpdateDate)
			   ;
		
		return builder.toString();
	}
}


