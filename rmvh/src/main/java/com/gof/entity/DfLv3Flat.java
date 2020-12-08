package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.gof.enums.EBoolean;
import com.gof.enums.EBoxModel;
import com.gof.enums.ECompound;
import com.gof.enums.EDiscFactor;
import com.gof.interfaces.Compoundable;
import com.gof.interfaces.IGocKey;
import com.gof.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DF_LV3_FLAT")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@Slf4j
public class DfLv3Flat implements Compoundable, Serializable, IGocKey{
	private static final long serialVersionUID = -8151467682976876533L;
	@Id
	@SequenceGenerator(name = "DF_LV3_SEQ", sequenceName = "DF_LV3_SEQ", initialValue = 1, allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DF_LV3_SEQ" )
	private Long seqId;
	
	private String baseYymm;
	private String evalYymm;
	private String gocId;
	private Double cfMonthNum;
	
	@Enumerated(EnumType.STRING)
	private EBoolean eirYn;
	
	@Enumerated(EnumType.STRING)
	private EBoolean ociYn;
	
	private String initCurveYymm;
	private double initRate;
	private String prevCurveYymm;
	private double prevRate;
	
	@Column(name = "FST_NEW_CONT_RATE")
	private double firstNewContRate;
	
	@Column(name = "SND_NEW_CONT_RATE")
	private double secondNewContRate;
	
	@Column(name = "TRD_NEW_CONT_RATE")
	private double thirdNewContRate;

	private double prevSysRate;
	private double prevLrcSysRate;
	private double prevLicSysRate;
	private double currRate;
	private double currSysRate;

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
	
	@Transient
	private EnumMap<EDiscFactor, Double> dfMap = new EnumMap<>(EDiscFactor.class);
	@Transient
	private EnumMap<EBoxModel, Double> boxMap =  new EnumMap<>(EBoxModel.class);
	@Transient
	private Map<String, EBoxModel> boxMap1 =  new HashMap<String, EBoxModel>();
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		return builder
					.append("_").append(gocId)
					.append("_").append(cfMonthNum)
					.append("_").append(prevRate)
					.append("_").append(initRate)
					.append("_").append(prevSysRate)
					.append("_").append(currRate)
					.toString();
	}
	
	public String getDfLv3Pk() {
		return gocId+cfMonthNum;
	}
	
	public String toCsv() {
		StringBuilder builder = new StringBuilder();
		return builder.append("_").append(cfMonthNum)
					.append("_").append(prevRate)
					.append("_").append(initRate)
					.append("_").append(prevSysRate)
					.append("_").append(currRate)
					.toString();
	}
	
	public Map<EBoxModel, Double> getBoxValueMap(){
		if(boxMap.size()==0) {
//			return EBoxModel.getBoxMap(this, boxModelType);
			return EBoxModel.getBoxValueMap(this, "AddOn");
		}
		return boxMap;
	}
	
//	public Map<String, EBoxModel> getBoxMap1(){
//		if(boxMap.size()==0) {
//			return EBoxModel.getBoxMap1(this, boxModelType);
//		}
//		return boxMap1;
//	}
	
	public EnumMap<EDiscFactor, Double> getDfMap(){
		if(dfMap.size()==0) {
			return EDiscFactor.getDfMap(this);
		}
		return dfMap;
	}
	@Override
	public ECompound getCompound() {
		return ECompound.Annualy;
	}
	@Override
	public LocalDate getBaseDate() {
		return DateUtil.convertFrom(baseYymm); 
	}
	@Override
	public double getCurrTimeFactor() {
		if(cfMonthNum < 0.0 ) {
			return 0.0;
		}
		return cfMonthNum/12.0;
	}
	@Override
	public double getPrevTimeFactor() {
		int addedMonNum = DateUtil.monthBetween(prevCurveYymm, baseYymm) ;
		if(cfMonthNum + addedMonNum <0.0) {
			return 0.0;
		}
		return ( cfMonthNum + addedMonNum) /12.0;  
	}
	
	@Override
	public double getFirstNewContTimeFactor() {
		int addedMonNum = DateUtil.monthBetween(prevCurveYymm, baseYymm) ;
		if(cfMonthNum + addedMonNum < 0.0) {
			return 0.0;
		}
		return ( cfMonthNum + addedMonNum) /12.0;  
	}
	@Override     
	public double getSecondNewContTimeFactor() {
		int addedMonNum = DateUtil.monthBetween(prevCurveYymm, baseYymm) -1;
		if(cfMonthNum + addedMonNum <0.0) {
			return 0.0;
		}
		return ( cfMonthNum + addedMonNum) /12.0;  
	}
	@Override
	public double getThirdNewContTimeFactor() {
		          
		int addedMonNum = DateUtil.monthBetween(prevCurveYymm, baseYymm) -2;
//		log.info("third New timeFactor : {},{},{},{}", addedMonNum, cfMonthNum, cfMonthNum+ addedMonNum, thirdNewContRate);
		
		if(cfMonthNum + addedMonNum <0.0) {
			return 0.0;
		}
		return ( cfMonthNum + addedMonNum) /12.0;  
	}
	
	
}
