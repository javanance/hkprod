package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;

import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(ParamCalcHisId.class)
@Table( name ="EAS_PARAM_CALC_HIS")
@FilterDef(name="FILTER", parameters= { @ParamDef(name="baseYymm", type="string") })
@Filters( { @Filter(name ="FILTER", condition="BASE_YYMM = :baseYymm") } )
@Getter
@Setter
public class ParamCalcHis implements Serializable, EntityIdentifier {

	private static final long serialVersionUID = -3199922647182076353L;

	@Id
	private String baseYymm;
	
	@Id	
	private String irModelTyp;
	
	@Id	
	private String paramCalcCd; 

	@Id
	private String paramTypCd;

	@Id
	private String matCd;	
	
	private Double paramVal;
	
	private Double vol;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	
	public ParamCalcHis() {}

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


