package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.enums.ECreditGrade;

import lombok.Getter;
import lombok.Setter;

@Entity
@IdClass(TransitionMatrixId.class)
@Table(name ="EAS_USER_CORP_CRD_GRD_TM")
@Getter
@Setter
public class TransitionMatrixUd implements Serializable, Comparable<TransitionMatrixUd>{
	private static final long serialVersionUID = -8151467682976876533L;	
	@Id
	private String baseYyyy;
	@Id
	@Column(name = "FROM_CRD_GRD_CD") 
	private String fromGrade;
	
	@Id
	private String toGrade;
	
	@Id
	private String tmType;

	@Column(name = "PROB_RATE")
	private double tmRate;

//	@Transient
//	private ECreditGrade fromGradeEnum;
	
//	@Transient
//	private ECreditGrade toGradeEnum;
	
	public TransitionMatrixUd() {
	}
	
	public ECreditGrade getFromGradeEnum() {
		return ECreditGrade.getECreditGrade(fromGrade) ;
	}
	
	public ECreditGrade getToGradeEnum() {
		return ECreditGrade.getECreditGrade(toGrade) ;
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
	public int compareTo(TransitionMatrixUd other) {
		return 10* ( this.getFromGradeEnum().getOrder() - other.getFromGradeEnum().getOrder()) 
				 + ( this.getToGradeEnum().getOrder()  - other.getToGradeEnum().getOrder())
				 ;  
		
	}

	
}
