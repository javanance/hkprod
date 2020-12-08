package com.gof.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gof.interfaces.Compoundable;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
@Getter
@Slf4j
public enum EBoxModel  {
//	 대문자 ,소문자 구분 : 시점 구분, 알파벳은 금리 구분
//	 AddOn 방식 & OCI/PL 개별 처리 방식을 선택할 수 있음.
	
	 p0		(1 ,  EDiscFactor.PSTL0, EDiscFactor.ZERO ,  EBoolean.Y,	EBoolean.N,EBoolean.N, "AddOn", "OCI", "PL")
	,Up		(2 ,  EDiscFactor.UNIT, EDiscFactor.PSTL0 ,  EBoolean.Y,	EBoolean.N,EBoolean.N, "AddOn", "OCI", "PL") 
	,Cp		(3 ,  EDiscFactor.CSTL1, EDiscFactor.PSTL0 , EBoolean.Y,	EBoolean.N,EBoolean.N, "AddOn", "PL")
	,Pp		(4 ,  EDiscFactor.PSTL1, EDiscFactor.PSTL0 , EBoolean.Y,	EBoolean.N,EBoolean.N, "AddOn", "PL")
	,CP		(5 ,  EDiscFactor.CSTL1, EDiscFactor.PSTL1 , EBoolean.Y,	EBoolean.N,EBoolean.N, "AddOn", "PL")
	
	,l0		(11 ,  EDiscFactor.NFST0, EDiscFactor.ZERO ,  EBoolean.N,	EBoolean.N,EBoolean.N, "AddOn", "OCI", "PL")
	,Ul		(12 ,  EDiscFactor.UNIT, EDiscFactor.NFST0 ,  EBoolean.N,	EBoolean.N,EBoolean.N, "AddOn", "OCI", "PL") 
	,Cl		(13 ,  EDiscFactor.CSTL1, EDiscFactor.NFST0 , EBoolean.N,	EBoolean.N,EBoolean.N, "AddOn", "PL")
	,Ll		(14 ,  EDiscFactor.NFST1, EDiscFactor.NFST0 , EBoolean.N,	EBoolean.N,EBoolean.N, "AddOn", "PL")
	,CL		(15 ,  EDiscFactor.CSTL1, EDiscFactor.NFST1 , EBoolean.N,	EBoolean.N,EBoolean.N, "AddOn", "PL")
	
	,m0		(21 ,  EDiscFactor.NSND0, EDiscFactor.ZERO ,  EBoolean.N,	EBoolean.N,EBoolean.N, "AddOn", "OCI", "PL")
	,Um		(22 ,  EDiscFactor.UNIT, EDiscFactor.NSND0 ,  EBoolean.N,	EBoolean.N,EBoolean.N, "AddOn", "OCI", "PL") 
	,Cm		(23 ,  EDiscFactor.CSTL1, EDiscFactor.NSND0 , EBoolean.N,	EBoolean.N,EBoolean.N, "AddOn", "PL")
	,Mm		(24 ,  EDiscFactor.NSND1, EDiscFactor.NSND0 , EBoolean.N,	EBoolean.N,EBoolean.N, "AddOn", "PL")
	,CM		(25 ,  EDiscFactor.CSTL1, EDiscFactor.NSND1 , EBoolean.N,	EBoolean.N,EBoolean.N, "AddOn", "PL")
	
	,n0		(31 ,  EDiscFactor.NTRD0, EDiscFactor.ZERO ,  EBoolean.N,	EBoolean.N,EBoolean.N, "AddOn", "OCI", "PL")
	,Un		(32 ,  EDiscFactor.UNIT, EDiscFactor.NTRD0 ,  EBoolean.N,	EBoolean.N,EBoolean.N, "AddOn", "OCI", "PL") 
	,Cn		(33 ,  EDiscFactor.CSTL1, EDiscFactor.NTRD0 , EBoolean.N,	EBoolean.N,EBoolean.N, "AddOn", "PL")
	,Nn		(34 ,  EDiscFactor.NTRD1, EDiscFactor.NTRD0 , EBoolean.N,	EBoolean.N,EBoolean.N, "AddOn", "PL")
	,CN		(35 ,  EDiscFactor.CSTL1, EDiscFactor.NTRD1 , EBoolean.N,	EBoolean.N,EBoolean.N, "AddOn", "PL")
	
	,sp 	(41 ,  EDiscFactor.PSYS0, EDiscFactor.PSTL0,  EBoolean.Y,	EBoolean.N,EBoolean.Y, "AddOn", "OCI")
	,ps 	(42 ,  EDiscFactor.PSTL0, EDiscFactor.PSYS0,  EBoolean.Y,	EBoolean.N,EBoolean.Y, "AddOn", "OCI")
	
	,Ss		(43 ,  EDiscFactor.PSYS1, EDiscFactor.PSYS0 , EBoolean.Y,	EBoolean.N,EBoolean.Y, "AddOn", "OCI")
//	,Sl		(43 ,  EDiscFactor.PSYS1, EDiscFactor.NFST0 , EBoolean.Y, "OCI")
//	,Sm		(43 ,  EDiscFactor.PSYS1, EDiscFactor.NSND0 , EBoolean.Y, "OCI")
//	,Sn		(43 ,  EDiscFactor.PSYS1, EDiscFactor.NTRD0 , EBoolean.Y, "OCI")
	
	,I0		(51 , EDiscFactor.INIT1, EDiscFactor.ZERO  , EBoolean.N,	EBoolean.Y,EBoolean.N, "AddOn", "OCI", "PL")
	
	,CI		(52 , EDiscFactor.CSTL1, EDiscFactor.INIT1 , EBoolean.N,	EBoolean.Y,EBoolean.N, "AddOn", "PL")
	,SI		(53 , EDiscFactor.PSYS1, EDiscFactor.INIT1 , EBoolean.N,	EBoolean.Y,EBoolean.Y, "AddOn", "OCI")

	,CS		(54 , EDiscFactor.CSTL1, EDiscFactor.PSYS1 , EBoolean.N,	EBoolean.Y,EBoolean.Y, "AddOn", "OCI")

	,CY		(61 , EDiscFactor.CSTL1, EDiscFactor.CSYS1 , EBoolean.N,	EBoolean.Y,EBoolean.Y, "AddOn", "OCI")
	,C0		(71 , EDiscFactor.CSTL1, EDiscFactor.ZERO  , EBoolean.N,	EBoolean.Y,EBoolean.N, "AddOn", "OCI", "PL")

	,UC		(99 , EDiscFactor.UNIT,  EDiscFactor.CSTL1 , EBoolean.Y,	EBoolean.Y,EBoolean.N, "AddOn", "OCI", "PL")
	,U0		(100, EDiscFactor.UNIT,  EDiscFactor.ZERO  , EBoolean.Y,	EBoolean.Y,EBoolean.N, "AddOn", "OCI", "PL")
	,ZU		(200, EDiscFactor.ZERO,  EDiscFactor.UNIT  , EBoolean.Y,	EBoolean.N,EBoolean.N, "ZZ")		//RELEASE CF !!!!
//	,UZ		(200, EDiscFactor.UNIT,  EDiscFactor.ZERO  , EBoolean.Y,	EBoolean.N,EBoolean.N, "ZZ")		//only RELEASE CF !!!!
	;
	
	private int order;
	private EDiscFactor toDf;
	private EDiscFactor fromDf;
	private EBoolean prevYn;
	private EBoolean deltaYn;
	private EBoolean onlyOciYn;								// Addon 방식의 경우에 적용되며, Y 인 경우는 OCI 를 위한 추가적인 처리임.
	private String[] postType;
	
	
	private EBoxModel(int order, EDiscFactor toDf, EDiscFactor fromDf, EBoolean prevYn, EBoolean deltaYn,EBoolean onlyOciYn, String... postType) {
		this.order = order;
		this.toDf = toDf;
		this.fromDf = fromDf;
		this.prevYn = prevYn;
		this.deltaYn = deltaYn;
		this.onlyOciYn = onlyOciYn;
		this.postType = postType;
	}
	
	public double getValue(Compoundable rates){
		return toDf.getValue(rates) - fromDf.getValue(rates);
	}
	
	public String getRateString(Compoundable rates){
		return toDf.getValue(rates) +"_"+ fromDf.getValue(rates);
	}

	public String getFullDesc() {
		return toDf.getDesc() + "-" + fromDf.getDesc();
	}
	
	public String getDesc() {
		return toDf + "-" + fromDf;
	}
	
	public List<String> getPostTypeList(){
		return Arrays.asList(postType);
	}
	
	public boolean  isContainedIn(String type){
		return getPostTypeList().contains(type);
	}
	
	public static List<EBoxModel> getPrevBoxList(){
		List<EBoxModel> rstList = new ArrayList<EBoxModel>();
		for(EBoxModel aa : EBoxModel.values()){
			if(aa.prevYn.isTrueFalse()){
				rstList.add(aa);
			}
		}
		return rstList;
	}
	
	public static List<EBoxModel> getDeltaBoxList(){
		List<EBoxModel> rstList = new ArrayList<EBoxModel>();

		for(EBoxModel aa : EBoxModel.values()){
			if(aa.deltaYn.isTrueFalse()){
				rstList.add(aa);
			}
		}
		return rstList;
	}
	
	public static Map<String, EBoxModel> getBoxMapByName(Compoundable rates, String type){
		Map<String, EBoxModel> rstMap = new HashMap<String, EBoxModel>();
		
		for(EBoxModel aa : EBoxModel.values()){
			if(aa.isContainedIn(type)){
				rstMap.put(aa.name(), aa);
			}
		}
		return rstMap;
	}
	
	public static Map<EBoxModel, Double> getBoxValueMap(Compoundable rates, String type){
		EnumMap<EBoxModel, Double> rstMap = new EnumMap<>(EBoxModel.class);
		for(EBoxModel aa : EBoxModel.values()){
			if(aa.isContainedIn(type)){
				rstMap.put(aa, aa.getValue(rates));
			}
		}
		return rstMap;
	}
	
	public static Map<EBoxModel, String> getBoxRateMap(Compoundable rates, String type){
		EnumMap<EBoxModel, String> rstMap = new EnumMap<>(EBoxModel.class);
		for(EBoxModel aa : EBoxModel.values()){
			if(aa.isContainedIn(type)){
				rstMap.put(aa, aa.getRateString(rates));
			}
		}
		return rstMap;
	}
	
//	*****************************************************************************************
	public static EnumMap<EBoxModel, Double> getBoxMapValidate(Compoundable rates){
		EnumMap<EBoxModel, Double> rstMap = new EnumMap<>(EBoxModel.class);
		
		rstMap.put(EBoxModel.p0, EBoxModel.p0.getValue(rates));
		rstMap.put(EBoxModel.Cp, EBoxModel.Cp.getValue(rates));
		rstMap.put(EBoxModel.UC, EBoxModel.UC.getValue(rates));
//		rstMap.put(EBoxNew.U0, EBoxNew.U0.getValue(rates));

		return rstMap;
	}
	
	public static EnumMap<EBoxModel, Double> getBoxMapValidate1(Compoundable rates){
		EnumMap<EBoxModel, Double> rstMap = new EnumMap<>(EBoxModel.class);
		
		rstMap.put(EBoxModel.p0, EBoxModel.p0.getValue(rates));
		rstMap.put(EBoxModel.sp, EBoxModel.sp.getValue(rates));
		rstMap.put(EBoxModel.Ss, EBoxModel.Ss.getValue(rates));
		rstMap.put(EBoxModel.CS, EBoxModel.CS.getValue(rates));
		rstMap.put(EBoxModel.UC, EBoxModel.UC.getValue(rates));
//		rstMap.put(EBoxNew.U0, EBoxNew.U0.getValue(rates));
		
		return rstMap;
	}
	
	public static EnumMap<EBoxModel, Double> getBoxMapValidate2(Compoundable rates){
		EnumMap<EBoxModel, Double> rstMap = new EnumMap<>(EBoxModel.class);
		
		rstMap.put(EBoxModel.I0, EBoxModel.I0.getValue(rates));
		rstMap.put(EBoxModel.CI, EBoxModel.CI.getValue(rates));
		rstMap.put(EBoxModel.UC, EBoxModel.UC.getValue(rates));
//		rstMap.put(EBoxNew.U0, EBoxNew.U0.getValue(rates));
		
		return rstMap;
	}
	
	public static EnumMap<EBoxModel, Double> getBoxMapValidate3(Compoundable rates){
		EnumMap<EBoxModel, Double> rstMap = new EnumMap<>(EBoxModel.class);
		
		rstMap.put(EBoxModel.I0, EBoxModel.I0.getValue(rates));
		rstMap.put(EBoxModel.SI, EBoxModel.SI.getValue(rates));
		rstMap.put(EBoxModel.CS, EBoxModel.CS.getValue(rates));
		rstMap.put(EBoxModel.UC, EBoxModel.UC.getValue(rates));
//		rstMap.put(EBoxNew.U0, EBoxNew.U0.getValue(rates));
		
		return rstMap;
	}
	
	
}
