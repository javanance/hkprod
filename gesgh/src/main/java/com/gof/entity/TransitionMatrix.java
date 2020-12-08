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
@Table(name ="EAS_CORP_CRD_GRD_TM")
@Getter
@Setter
public class TransitionMatrix implements Serializable, Comparable<TransitionMatrix>{
	private static final long serialVersionUID = -8151467682976876533L;	
	@Id
	private String baseYyyy;
	@Id
	private String tmType;
	
	@Id
	private String fromGrade;
	
	@Id
	private String toGrade;
	

	@Column(name = "PROB_RATE")
	private double tmRate;

	public TransitionMatrix() {
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
	public int compareTo(TransitionMatrix other) {
		return 100* ( this.getFromGradeEnum().getOrder() - other.getFromGradeEnum().getOrder()) 
				 + ( this.getToGradeEnum().getOrder()  - other.getToGradeEnum().getOrder())
				 ;  
		
	}

	
}
