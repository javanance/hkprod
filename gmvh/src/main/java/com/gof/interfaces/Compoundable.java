package com.gof.interfaces;

import java.time.LocalDate;
import java.util.Map;

import com.gof.enums.EBoxModel;
import com.gof.enums.ECompound;
import com.gof.util.DateUtil;

public interface Compoundable {
	public default ECompound getCompound() {
		return ECompound.Annualy;
	};

	public String getBaseYymm();

	public default LocalDate getBaseDate() {
		return DateUtil.convertFrom(getBaseYymm()); 
	}
	;
	
	public double getCurrTimeFactor();
	public double getPrevTimeFactor();
	public double getFirstNewContTimeFactor();
	public double getSecondNewContTimeFactor();
	public double getThirdNewContTimeFactor();
	
	public double getPrevRate();
	public double getPrevSysRate();
	public double getInitRate();
	public double getCurrSysRate();
	public double getCurrRate();
	
	public double getFirstNewContRate();
	public double getSecondNewContRate();
	public double getThirdNewContRate();
	
	
//	public EnumMap<EBoxModel, Double> getBoxMap();
	public Map<EBoxModel, Double> getBoxValueMap();
//	public default EnumMap<EBoxModel, Double> getBoxMap(){
//		return EBoxModel.getBoxMap(this, "AddOn");
//	}
	

//	public double getPrevDf();
//	public double getCurrDf();
}
