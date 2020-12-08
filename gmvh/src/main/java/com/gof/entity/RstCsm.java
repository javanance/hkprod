package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.gof.enums.EGocStatus;
import com.gof.enums.ELossStep;
import com.gof.enums.EOperator;
import com.gof.enums.ERollFwdType;
import com.gof.interfaces.IBoxRst;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@IdClass(RstCsmId.class)
@Table(name ="RST_CSM")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
//@Setter 
//@ToString()
//@Slf4j
public class RstCsm implements Serializable, IBoxRst{
	private static final long serialVersionUID = -8151467682976876533L;
	
	@Id	private String baseYymm;
	@Id	private String gocId;
	
//	@Enumerated(EnumType.STRING)
//	@Id	private ERollFwdType rollFwdType;
//	@Id	private String calcId;
	
	@ManyToOne
	@JoinColumn(name = "ROLL_FWD_TYPE")
	@Fetch(FetchMode.JOIN)
	@Id	private MstRollFwd mstRollFwd;
	
	@Id private String runsetId;
	
	@ManyToOne
	@JoinColumn(name = "CALC_ID")
	@Fetch(FetchMode.JOIN)
	@Id	private MstCalc mstCalc;
	
	private Integer seq;
	
	@Enumerated(EnumType.STRING)
	private EOperator operatorType;
	
	private Double boxAmt;
	private Double deltaCalcCsmAmt;
	private Double calcCsmAmt;
	
	private Double deltaCsmAmt;
	private Double csmAmt;

	
	@Enumerated(EnumType.STRING)
	private ELossStep lossStep;
	private double lossFulfillAmt;
//	private double epvAmt;
//	private double raAmt;
	private double deltaLossRatio;
	private double lossRatio;
	
	private String remark;
	private String lastModifiedBy;
	private LocalDateTime lastModifiedDate;
	
	@Transient
	private EGocStatus gocStatus;
	
	
	public RstCsm(String bssd,String gocId) {
		this.gocId = gocId;
		this.baseYymm = bssd;
		this.calcCsmAmt =0.0;
	}
	
	public ERollFwdType getRollFwdType() {
		return mstRollFwd.getRollFwdType();
	}
	public String getCalcId() {
		return mstCalc.getCalcId();
	}
	
	public double getLossAmt() {
		return calcCsmAmt>=0.0 ? 0.0: -1.0* calcCsmAmt;
	}
	
	public String getAppliedCalcId() {
		return mstCalc.getAppliedCalcId();
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
	 	 return builder
	 		.append(baseYymm).append(",")
	 	 	.append(gocId).append(",")
	 	 	.append(mstRollFwd.getRollFwdType()).append(",")
	 	 	.append(runsetId).append(",")
	 	 	.append(mstCalc.getCalcId()).append(",")
	 	 	.append(seq).append(",")
	 	 	.append(operatorType).append(",")
	 	 	.append(deltaCalcCsmAmt).append(",")
	 	 	.append(calcCsmAmt).append(",")
	 	 	.toString(); 
	}


	@Override
	public Double getBoxValue() {
//		double boxValue = 0.0;
//		if(mstRollFwd.getRollFwdType().isClose()) {
//			boxValue =Math.max(calcCsmAmt, 0.0);
//		}
//		else {
//			boxValue = deltaCalcCsmAmt==0.0? deltaCsmAmt : deltaCalcCsmAmt	;
//		}
//		return boxValue;
		return mstRollFwd.getRollFwdType().isClose()? Math.max(calcCsmAmt, 0.0): boxAmt;
	}
	
	public Double getAppliedLossRatio() {
		if(lossStep.deltaYn()) {
			return deltaLossRatio;
		}
		else {
			return lossRatio;
		}
//		if(lossStep.equals("L3")) {			//TODO !!!!
//			return deltaLossRatio;
//		}
//		return lossRatio;
	}
}

