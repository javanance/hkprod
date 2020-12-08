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
@IdClass(BizIndiPdId.class)
@Table(name ="EAS_BIZ_APLY_INDI_PD")
@Getter
@Setter
public class BizIndiPd implements Serializable, EntityIdentifier {

	private static final long serialVersionUID = 593109163503140012L;
	
	@Id
	private String baseYymm;
	
	@Id
	@Column(name = "APPL_BIZ_DV")
	private String applyBizDv;
	@Id
	private String cbGrdCd;
	
	@Id
	private String matCd;	

	@Id
	private String crdEvalAgncyCd;
	private Double cumPd;	
	
	private Double fwdPd;
	private Double cumPdChgRate;
	
	private Double vol;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public BizIndiPd() {}

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
		return "IndiCrdGrdCumPd [baseYymm=" + baseYymm + ", cbGrdCd=" + cbGrdCd + ", matCd=" + matCd
				+ ", crdEvalAgncyCd=" + crdEvalAgncyCd + ", cumPd=" + cumPd + ", fwdPd=" + fwdPd + ", cumPdChgRate="
				+ cumPdChgRate + "]";
	}
		
}


