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

//TODO : Drop Table !!!!
@Entity
@IdClass(BizCrdSpreadUdId.class)
@Table(name ="EAS_USER_CRD_SPREAD")
@Getter
@Setter
public class BizCrdSpreadUd implements Serializable, EntityIdentifier {

	private static final long serialVersionUID = -8151467682976876533L;
	
	@Id
	@Column(name="APPL_ST_YYMM", nullable=false)
	private String applStYymm;
	
	@Id
	@Column(name="APPL_BIZ_DV", nullable=false)
	private String applyBizDv;
	@Id
	private String crdGrdCd;	
	@Id
	private String matCd;
	
	@Column(name="APPL_ED_YYMM")
	private Double applEdYymm;
	
	@Column(name="APPL_CRD_SPREAD")
	private Double applyCrdSpread;
	
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public BizCrdSpreadUd() {}
	

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
		
		builder.append(applStYymm).append(delimeter)
//			   .append(applyEndYymm).append(delimeter)
			   .append(applyBizDv).append(delimeter)
			   .append(crdGrdCd).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(applyCrdSpread)
			   ;
		return builder.toString();
	}
}


