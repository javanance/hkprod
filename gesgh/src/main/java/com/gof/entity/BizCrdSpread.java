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
@IdClass(BizCrdSpreadId.class)
@Table(name ="EAS_BIZ_APLY_CRD_SPREAD")
@Getter
@Setter
public class BizCrdSpread implements Serializable, EntityIdentifier {

	private static final long serialVersionUID = -8151467682976876533L;
	
	@Id
	private String baseYymm;
	
	@Id
	@Column(name="APPL_BIZ_DV", nullable=false)
	private String applyBizDv;
	
	@Id
	private String crdGrdCd;	
	
	@Id
	private String matCd;
	
	@Column(name="APPL_CRD_SPREAD")
	private Double applyCrdSpread;
	
	private Double vol;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public BizCrdSpread() {}
	
	
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
//			   .append(applyEndYymm).append(delimeter)
			   .append(applyBizDv).append(delimeter)
			   .append(crdGrdCd).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(applyCrdSpread).append(delimeter)
			   .append(vol)
			   ;
		return builder.toString();
	}
}


