package com.gof.entity;


import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@IdClass(EsgRandomId.class)
@Table(name ="EAS_ESG_RANDOM")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class EsgRandom implements Serializable {

	private static final long serialVersionUID = -4252300668894647002L;
	
	@Id	private String baseYymm;
	@Id	private String stdAsstCd;
	@Id	private String volCalcId;
	@Id	private Integer sceNo;
	@Id	private Integer matNum;
	
	private Double rndNum;
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}



