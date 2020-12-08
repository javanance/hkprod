package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.gof.annotation.ToCsv;
import com.gof.enums.EBoolean;
import com.gof.interfaces.EntityIdentifier;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name ="EAS_STD_ASST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
//@Setter(value = AccessLevel.PROTECTED)
//@ToString(callSuper = false)
//@EqualsAndHashCode(of= {"irModelId", "irModelNm", "irModelTyp"}, callSuper = false)

public class StdAssetMst extends BaseEntity implements Serializable{
	private static final long serialVersionUID = -8151467682976876533L;
	
	@Id
	private String stdAsstCd;
	private String stdAsstNm;
	private int seq;
	private String stdAsstTypCd;
	private String curCd;
	
	@Enumerated(EnumType.STRING)
	private EBoolean useYn;
	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	
	

}
