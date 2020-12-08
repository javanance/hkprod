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
@IdClass(StdAssetVolId.class)
@Table(name ="EAS_MV_VOL")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class StdAssetVol extends BaseEntity implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;
	
	@Id	private String baseDate;
	@Id	private String volCalcId;
	@Id	private String mvId;
	
	private String curCd;
	
//	@Column(name = "MV_TYP_CD")
	private String mvTypCd;
	private Double mvHisVol;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public StdAssetVol(String mvId, Double mvHisVol) {
		super();
		this.mvId = mvId;
		this.mvHisVol = mvHisVol;
	}
	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	public StockParamHis convert(String bssd, String volCalcId) {
		return StockParamHis.builder()
				.baseYymm(bssd)
				.stdAsstCd(this.getMvId())
				.paramCalcCd(volCalcId)
				.paramTypCd("SIGMA")
				.matDayNum(365)
				.paramVal(this.getMvHisVol())
				.lastModifiedBy("ESG")
				.lastUpdateDate(LocalDateTime.now())
				.build();
	}
}
