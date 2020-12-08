package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.interfaces.BaseValue;
import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(SegLgdId.class)
@Table( name ="EAS_SEG_LGD")
@Getter
@Setter
public class SegLgd implements Serializable, EntityIdentifier, BaseValue {

	private static final long serialVersionUID = 2360652480675748510L;

	@Id
	private String baseYymm;
	
	@Id
	private String lgdCalcTypCd;
	
	@Id
	private String segId;	
	
    private Double lgd;
    
	private Double vol;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public SegLgd() {}

	@Override
	public Double getBasicValue() {
		return lgd;
	}

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
			   .append(lgdCalcTypCd).append(delimeter)
			   .append(segId).append(delimeter)
			   .append(lgd).append(delimeter)
			   .append(vol)
			   ;
		
		return builder.toString();
	}
		
}


