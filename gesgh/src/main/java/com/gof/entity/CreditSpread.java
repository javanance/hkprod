package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(CreditSpreadId.class)
@Table(name ="EAS_CRD_SPREAD")
@Getter
@Setter
public class CreditSpread implements Serializable, EntityIdentifier {

	private static final long serialVersionUID = -8151467682976876533L;
	
	@Id
	private String baseYymm;

	@Id
	private String crdGrdCd;

	@Id
	private String matCd;
	
	@Column(name ="CRD_GRD_NM")
	private String crdGrdName;
	
	
	private Double crdSpread;	
	
	public CreditSpread() {}

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


