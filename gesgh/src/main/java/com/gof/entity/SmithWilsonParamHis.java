package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@IdClass(SmithWilsonParamHisId.class)
@Table(name="EAS_PARAM_SMITH_WILSON_HIS")
@Getter
@Setter
public class SmithWilsonParamHis implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;
	@Id
	private String applStartYymm;
	
	@Id
	private String applEndYymm;
	
	@Id
	private String curCd;	

//	@Column(name ="IR_CURVE_DV")
//	private String irCurveDv;
	
	@Column(name ="LLP")
	private Double llp;
	
	@Column(name ="UFR")
	private Double ufr;

	@Column(name ="UFR_T")
	private Double ufrT;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;

	
	public SmithWilsonParamHis() {
		super();
	}
	
	public SmithWilsonParamHis(String curCd, double ufr, double ufrT) {
		super();
		this.curCd = curCd;
		this.ufr = ufr;
		this.ufrT = ufrT;
	}

	
	
	
	
}


