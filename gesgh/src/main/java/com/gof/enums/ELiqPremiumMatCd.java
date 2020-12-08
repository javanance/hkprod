package com.gof.enums;

public enum ELiqPremiumMatCd {
	  M0001	 (0.083){
		   public String getKTBCode() {   return "KTB1M";   }
	   }
	 , M0003 (0.25){
		   public String getKTBCode() {   return "KTB3M";   }
	   }
	 , M0006 (0.5){
		   public String getKTBCode() {   return "KTB6M";  }
	   }
	 , M0009 (0.75){
		   public String getKTBCode() {   return "KTB9M";   }
	   }
	 , M0012 (1.0){
		   public String getKTBCode() {   return "KTB1Y";   }
	   }
	 , M0018 (1.5){
		   public String getKTBCode() {	   return "KTB1.5Y";   }
	   }
	 , M0024 (2.0){
		   public String getKTBCode() {	   return "KTB2Y";   }
	   }
	 , M0030 (2.5){
		   public String getKTBCode() {	   return "KTB2.5Y";   }
	   }
	 , M0036 (3.0){
		   public String getKTBCode() {	   return "KTB3Y";   }
	   }
	 , M0048 (4.0){
		   public String getKTBCode() {   return "KTB4Y";   }
	   }
	 , M0060 (5.0){
		   public String getKTBCode() {	   return "KTB5Y";   }
	   }
	 , M0084 (7.0){
		   public String getKTBCode() {	   return "KTB7Y";   }
	   }
	 , M0120 (10.0){
		   public String getKTBCode() {	   return "KTB10Y";   }
	   }
	 , M0240 (20.0){
		   public String getKTBCode() {	   return "KTB10Y";   }
	   }
	 ;
	
	
	private double yearFrac;

	private ELiqPremiumMatCd(double yearFrac) {
		this.yearFrac = yearFrac;
	}

	public String getKTBCode() {
		return null;
	}
	
	
	public double getYearFrac() {
		return yearFrac;
	}

	public static boolean contains(String matCd) {
		for(ELiqPremiumMatCd aa : ELiqPremiumMatCd.values()) {
			if(aa.name().equals(matCd)) {
				return true;
			}
		}
		return false;
	}
	
}
