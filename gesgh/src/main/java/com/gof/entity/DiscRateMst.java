package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name ="EAS_DISC_RATE_MST")
@Getter
@Setter
public class DiscRateMst implements Serializable {
	private static final long serialVersionUID = -1727949379975877048L;

	@Id
	private String intRateCd;		
	private String discRateCalcTyp;		
	private String discRateCalcDtl;		
	private String acctDvCd;	

	private Double discLowerVal;		
	private Double discUpperVal;		
	private Double discGuideVal;		
	private Double discRateSpread;

	private Double discMinGuaran;
	
	private String mgtAsstTyp;	
	private String exIdxTyp;	
	private String wghtTyp;	
	private String discRateCalcDesc;	
	private String calcYn;	
	private String calcMeth;	
	
	private String remark;


//	***************************Biz *************************
	
//	@Transient
//	public String getCalcType() {
//		return remark.split("_")[0];
//	}
//	
	@Transient
	public boolean isCalculable() {
		if( calcYn==null) {
			return false;				//TODO : �쓽�궗寃곗젙 �븘�슂
		}
		else if(calcYn.equals("Y")) {
			return true;
		}
		else {
			return false;
		}
		
//		if(calcMeth==null) {
//			return false;				//TODO : �쓽�궗寃곗젙 �븘�슂
//		}
		//else if(calcMeth.contains("산출")) {
//		else if(calcMeth.contains("Y")) {
//			return true;
//		}
//		else {
//			return false;
//		}
	}
	
}


