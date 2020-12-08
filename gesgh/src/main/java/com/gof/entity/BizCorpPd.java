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
@IdClass(BizCorpPdId.class)
@Table(name ="EAS_BIZ_APLY_CORP_PD")
@Getter
@Setter
public class BizCorpPd implements Serializable, EntityIdentifier{
	private static final long serialVersionUID = -8151467682976876533L;
	@Id
	private String baseYymm;
	
	@Id
	@Column(name = "APPL_BIZ_DV")
	private String applyBizDv;
	
	@Id
	@Column(name = "CRD_GRD_CD")
	private String crdGrdCd;
	
	@Id
	private String matCd;

	private Double pd;
	private Double cumPd;
	private Double fwdPd;
	private Double vol;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	
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
