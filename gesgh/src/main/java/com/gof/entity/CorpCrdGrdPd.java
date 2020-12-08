package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.interfaces.BaseValue;
import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(CorpCrdGrdPdId.class)
@Table(name ="EAS_CORP_CRD_GRD_PD")
@Getter
@Setter
public class CorpCrdGrdPd implements Serializable, EntityIdentifier , BaseValue{

	private static final long serialVersionUID = -3833361109526416019L;

	@Id
	private String baseYymm;

	@Id
	private String crdEvalAgncyCd;

	@Id
	private String crdGrdCd;	
	
	private Double pd;
	
	private Double vol;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public CorpCrdGrdPd() {}

	

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
	public Double getBasicValue() {
		return pd;
	}

	@Override
	public String toString() {
		return toString(",");
	}
	
	public String toString(String delimeter) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(baseYymm).append(delimeter)
			   .append(crdEvalAgncyCd).append(delimeter)
			   .append(crdGrdCd).append(delimeter)
			   .append(pd).append(delimeter)
			   .append(vol)
			   ;
		
		return builder.toString();
	}
		
}


