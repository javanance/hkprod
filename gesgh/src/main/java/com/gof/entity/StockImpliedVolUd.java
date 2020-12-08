package com.gof.entity;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.util.DateUtil;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Entity
@IdClass(StockImpliedVolUdId.class)
@Table( name ="EAS_USER_IMPLIED_VOL")
@Getter
@Slf4j
public class StockImpliedVolUd implements Serializable{

	private static final long serialVersionUID = 2360652480675748510L;

	@Id	private String baseYymm;
	@Id	private String stdAsstCd;
	@Id	private String optionMatDate;	
	
	private String atmOptionId;
	private Double impliedVol;

	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	
	public int getResidualDateNum(LocalDate baseDate) {
//		log.info("aakkk : {},{}",  optionMatDate, impliedVol);
		return (int) baseDate.until(DateUtil.convertFrom(optionMatDate), ChronoUnit.DAYS);
		
	}
		
}


