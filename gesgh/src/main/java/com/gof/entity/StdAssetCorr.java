package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.annotation.ToCsv;
import com.gof.enums.EBoolean;
import com.gof.interfaces.EntityIdentifier;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@IdClass(StdAssetCorrId.class)
@Table(name ="EAS_MV_CORR")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class StdAssetCorr extends BaseEntity implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;
	
	@Id	private String baseDate;
	@Id	private String volCalcId;
	@Id	private String mvId;
	@Id	private String refMvId;
	
//	private String curCd;
	
//	@Column(name = "MV_TYP_CD")
//	private String mvTypCd;
	
	private Double mvHisCov;
	private Double mvHisCorr;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public StdAssetCorr(String mvId, String refMvId, Double mvHisCov,Double mvHisCorr) {
		this.mvId = mvId;
		this.refMvId = refMvId;
		this.mvHisCov = mvHisCov;
		this.mvHisCorr = mvHisCorr;
	}
	
//	public StdAssetCorr(String mvId, String refMvId, Double mvHisCorr) {
//		this.mvId = mvId;
//		this.refMvId = refMvId;
//		this.mvHisCorr = mvHisCorr;
//	}
	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	
}
