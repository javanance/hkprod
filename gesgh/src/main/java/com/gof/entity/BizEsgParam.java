package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(BizEsgParamId.class)
@Table(name ="EAS_BIZ_APLY_PARAM")
@Getter
@Setter
public class BizEsgParam implements Serializable, EntityIdentifier {
	private static final long serialVersionUID = 1524655691890282755L;

	@Id
	private String baseYymm;
	
	@Id
	@Column(name="APPL_BIZ_DV", nullable=false)		
	private String applyBizDv;
	
	@Id
	private String irModelId;

	@Id
	private String paramTypCd;
	
	@Id
	private String matCd;
	
	private Double applParamVal;

	private Double vol;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	@ManyToOne()
	@JoinColumn(name ="IR_MODEL_ID", insertable=false, updatable=false)
	private EsgMst esgMst ;
	
	public BizEsgParam() {}
	

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public double getAppliedVal() {
		if(paramTypCd.equals("ALPHA") && matCd.equals("M1200")) {
//			return Math.max(applParamVal,  0.025);
//			return Math.max(applParamVal,  0.015);
		}
		return applParamVal;
	}
}


