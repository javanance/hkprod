package com.gof.entity;

import java.io.Serializable;

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
import com.gof.interfaces.Pricable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@IdClass(StdAssetHisId.class)
@Table(name ="EAS_STD_ASST_PRICE_HIS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
//@Setter(value = AccessLevel.PROTECTED)
//@ToString(callSuper = false)
//@EqualsAndHashCode(of= {"irModelId", "irModelNm", "irModelTyp"}, callSuper = false)

public class StdStockAssetHis extends BaseEntity implements Serializable, Pricable{
	private static final long serialVersionUID = -8151467682976876533L;
	
	@Id	private String baseDate;
	@Id	private String stdAsstCd;
	

	private Double stdAsstPrice;
	
	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	@Override
	public double getPrice() {
		return stdAsstPrice;
	}
	
	
}
