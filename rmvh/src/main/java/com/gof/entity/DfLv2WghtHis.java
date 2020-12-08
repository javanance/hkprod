package com.gof.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DF_LV2_WGHT_HIS")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DfLv2WghtHis implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;
	
	@Id
	@SequenceGenerator(name = "DF_LV2_SEQ", sequenceName = "DF_LV2_SEQ", initialValue = 1, allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DF_LV2_SEQ" )
	private Long seqId;

	private String baseYymm;
	private String gocId;
	private Double cfMonthNum;
	
	private String initYymm;
	private Double wghtRate;
	private Double wghtFwdRate;
	
	private String lastModifiedBy;
	private LocalDateTime lastModifiedDate;

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
