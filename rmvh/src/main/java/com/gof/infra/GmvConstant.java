package com.gof.infra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

/**
 * 
 * @author takion77@gofconsulting.co.kr
 * 
 */
@Getter
public class GmvConstant {
	public static String URL;
	public static String DRIVER;
	public static String DIALECT;
	public static String USERNAME;
	public static String PASSWORD;
	public static String TABLE_SCHEMA;
	public static String TABLE_PREFIX;
	
	public static String ARK_RUNSET_MODE;
	
	public static String DELETE_THEN_INSERT;
	public static String ROLLFWD_CLOSE_STEP;
	public static String GOC_GROUP;
	
	
	public static String COHORT_TYPE;
	
	public static String RUNSET_FIRST_NEW;
	public static String RUNSET_SECOND_NEW;
	public static String RUNSET_THIRD_NEW;
	public static String RUNSET_PREV;
	public static String RUNSET_CURR;
	
	public static String DELTA_GROUP_PREV;
	public static String DELTA_GROUP_LOSS;
	public static String GOC_ID;
	
	
	public static String BSSD;
	public static String ST_BSSD;
	public static String V_BSSD;
	public static String JOB_NO ;
	
	public static String NEW_RS_DIV_ID ;
	public static String CLOSING_RS_DIV_ID;
	public static String EIR_TARGET_RS_DIV_ID;
	public static String EIR_CF_RS_DIV_ID;
	
	public static String EIR_TARGET_RUNSET;
	public static String EIR_CF_RUNSET;
	public static String EIR_SL_ADJ;
	
	public static String CURR_EIR_TYPE;
	public static String INIT_CURVE_YN;
	
	public static String NEW_CONT_RATE;
	
	public static String IR_CURVE_ID ;
	
	public static String CALC_TYPE_COV_UNIT ;
	public static String CALC_TYPE_CSM_INT ;
	
	public static String CALC_TYPE_DAC_RATIO ;
	
	public static double DEFAULT_DAC_RATIO ;
	public static double DEFAULT_CSM_RATIO ;
//	public static String CALC_TYPE_LOSS_RATIO ;
	
	public static String LOSS_CF_GROUP ;
	public static String FV_RS_DIV_ID ;
	public static String DAC_CF_GROUP;
	
	public static int MAX_RATE_TENOR;
	public static int MAX_TENOR ;
	public static int EIR_ITER_NUM ;
	public static double EIR_START_RATE ;
	public static double EIR_ERROR_TOLERANCE ;
	
	public static List<Integer>  GROUP_TENOR_LIST ;
	
	
	
	private static Map<String, String> strConstant = new HashMap<String, String>();
	private static Map<String, Double> doubleConstant = new HashMap<String, Double>();
	private static Map<String, Long> longConstant = new HashMap<String, Long>();
	private static Map<String, Integer> intConstant = new HashMap<String, Integer>();
	private static List<String> tenorList = new ArrayList<String>();
	private static List<Integer> groupCfTenorList = new ArrayList<Integer>();
	
	
	public static Map<String, String> getStrConstant() {
		return strConstant;
	}
	
	
	public static Map<String, Double> getDoubleConstant() {
		return doubleConstant;
	}


	public static Map<String, Long> getLongConstant() {
		return longConstant;
	}

	public static Map<String, Integer> getIntConstant() {
		return intConstant;
	}


	public static List<String> getTenorList() {
		return tenorList;
	}
	
	public static List<Integer> getGroupCfTenorList() {
		return groupCfTenorList;
	}
	
	public static String getLastModifier() {
		return GmvConstant.TABLE_PREFIX +"_" + GmvConstant.JOB_NO;
	}
}
