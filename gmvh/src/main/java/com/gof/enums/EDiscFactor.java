package com.gof.enums;

import java.util.EnumMap;

import com.gof.interfaces.Compoundable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum EDiscFactor {
	
	
	PSTL0 (1 , "Previous EPV with prevRate")  {
		public double getValue(Compoundable rates){
			return rates.getCompound().getDf(rates.getPrevRate(), rates.getPrevTimeFactor()); 
		}
		public double getRate(Compoundable rates) {
			return rates.getPrevRate();
		}
	 }
	
	,PSTL1 (2 , "Current EPV with PrevRate")  {
		public double getValue(Compoundable rates){
			return rates.getCompound().getDf(rates.getPrevRate(), rates.getCurrTimeFactor()); 
		}
		public double getRate(Compoundable rates) {
			return rates.getPrevRate();
		}
	 }
	
	, NFST0 (1 , "Init EPV at first new cont with first rate")  {
		public double getValue(Compoundable rates){
			return rates.getCompound().getDf(rates.getFirstNewContRate(), rates.getFirstNewContTimeFactor()); 
		}
		public double getRate(Compoundable rates) {
			return rates.getFirstNewContRate();
		}
	 }
	, NFST1 (2 , "Current EPV at first new cont  with first rate")  {
		public double getValue(Compoundable rates){
			return rates.getCompound().getDf(rates.getFirstNewContRate(), rates.getCurrTimeFactor()); 
		}
		public double getRate(Compoundable rates) {
			return rates.getFirstNewContRate();
		}
	 }
	, NSND0 (1 , "Init EPV at second new cont  with second rate")  {
		public double getValue(Compoundable rates){
			return rates.getCompound().getDf(rates.getSecondNewContRate(), rates.getSecondNewContTimeFactor()); 
		}
		public double getRate(Compoundable rates) {
			return rates.getSecondNewContRate();
		}
	 }
	, NSND1 (2 , "Current EPV at second new cont  with second rate")  {
		public double getValue(Compoundable rates){
			return rates.getCompound().getDf(rates.getSecondNewContRate(), rates.getCurrTimeFactor()); 
		}
		public double getRate(Compoundable rates) {
			return rates.getSecondNewContRate();
		}
	}
	, NTRD0 (1 , "Init EPV at third new cont  with third rate")  {
		public double getValue(Compoundable rates){
			return rates.getCompound().getDf(rates.getThirdNewContRate(), rates.getThirdNewContTimeFactor()); 
		}
		public double getRate(Compoundable rates) {
			return rates.getThirdNewContRate();
		}
	 }
	, NTRD1 (2 , "Current EPV at third new cont  with third rate")  {
		public double getValue(Compoundable rates){
			return rates.getCompound().getDf(rates.getThirdNewContRate(), rates.getCurrTimeFactor()); 
		}
		public double getRate(Compoundable rates) {
			return rates.getThirdNewContRate();
		}
	}
	,PSYS0	(3 , "Previous EPV with prevSysRate")  {
		public double getValue(Compoundable rates){
			return rates.getCompound().getDf(rates.getPrevSysRate(), rates.getPrevTimeFactor()); 
		}
		public double getRate(Compoundable rates) {
			return rates.getPrevSysRate();
		}
	 }
	,PSYS1	(4 , "Current EPV with prevSysRate ") {
		public double getValue(Compoundable rates){
			return rates.getCompound().getDf(rates.getPrevSysRate(), rates.getCurrTimeFactor()); 
		}
		public double getRate(Compoundable rates) {
			return rates.getPrevSysRate();
		}
	}
	,INIT1	(4 , "Current EPV with InitRate")	  {
		public double getValue(Compoundable rates){
			return rates.getCompound().getDf(rates.getInitRate(), rates.getCurrTimeFactor()); 
		}
		public double getRate(Compoundable rates) {
			return rates.getInitRate();
		}
	 }
	,CSYS1 (5 , "Current EPV with currSysRate")	  {

		 public double getValue(Compoundable rates){
			 return rates.getCompound().getDf(rates.getCurrSysRate(), rates.getCurrTimeFactor()); 
		 }
		 public double getRate(Compoundable rates) {
				return rates.getCurrSysRate();
		}
	 }
	,CSTL1 (7 , "Current EPV with currRate")	  {
		 public double getValue(Compoundable rates){
			 return rates.getCompound().getDf(rates.getCurrRate(),rates.getCurrTimeFactor()); 
		 }
		 public double getRate(Compoundable rates) {
				return rates.getCurrRate();
		}
	 }
	, ZERO (10, "Zero")
	
	, UNIT (20, "1") {
		 public double getValue(Compoundable rates){
			 return 1.0;
		 }
		 
	 }
;
	
	private int order;
	private String desc;
	
	private EDiscFactor(int order, String desc) {
		this.order =order;
		this.desc = desc;
	}
	
	public int getOrder() {
		return order;
	}
	
	public String getDesc() {
		return desc;
	}

	public double getValue(Compoundable rates) {
		return 0.0;
		
	}
	public double getRate(Compoundable rates) {
		return 0.0;
	}
	
//	public double getValue(DfFlatable rates) {
//		return 0.0;
//		
//	}
//	public double getValue(Compoundable rates, int monNum) {
//		return 0.0;
//	}
	
	
	public static EnumMap<EDiscFactor, Double> getDfMap(Compoundable rates){
		EnumMap<EDiscFactor, Double> rstMap = new EnumMap<>(EDiscFactor.class);
		for(EDiscFactor aa : EDiscFactor.values()){
//			log.info("Df in Dicsfactor : {},{},{},{},{}", aa.name(), aa.getValue(rates),rates.getThirdNewContRate(), rates.getCurrTimeFactor(), rates.getThirdNewContTimeFactor());
//			log.info("Df in Dicsfactor : {},{},{},{},{}", aa.name(), aa.getValue(rates),rates.getPrevTimeFactor(), rates.getPrevSysRate(), rates.getPrevSettleRate());
			rstMap.put(aa, aa.getValue(rates));
		}
		return rstMap;
	}
}
