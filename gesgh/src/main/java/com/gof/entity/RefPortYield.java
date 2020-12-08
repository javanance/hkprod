package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(RefPortYieldId.class)
@Table(name ="EAS_REF_PORT_YIELD")
@Getter
@Setter
public class RefPortYield implements Serializable, EntityIdentifier {

	private static final long serialVersionUID = 8064247948865500380L;

	@Id
	private String baseYymm;	
	@Id
	private String asstClassTypCd;
	@Id
	private String matCd;

	@Column(name ="ASST_YIELD")
	private Double assetYield;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public RefPortYield() {}


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
		return "RefPortYield [baseYymm=" + baseYymm + ", asstClassTypCd=" + asstClassTypCd + ", matCd=" + matCd
				+ ", assetYield=" + assetYield +"]";
	}

}


