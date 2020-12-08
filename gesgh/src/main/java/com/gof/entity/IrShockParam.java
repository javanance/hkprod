package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name ="EAS_IR_SHOCK_PARAM")
@Access(AccessType.FIELD)
@Getter
@Setter
@ToString
public class IrShockParam implements Serializable {
	
	private static final long serialVersionUID = -6565657307170343742L;
	
	@Id
	private String baseYymm; 
	
	@Id
	private String irShockTyp;
	
	@Id
	private String irCurveId;
	
	@Id
	private String paramTypCd;
	
	private Double paramVal;
	
	private String lastModifiedBy;

	private LocalDateTime lastUpdateDate;	
	
	public IrShockParam() {}

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
