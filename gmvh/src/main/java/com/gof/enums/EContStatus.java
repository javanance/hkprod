package com.gof.enums;

import lombok.Getter;

@Getter
public enum EContStatus {
	
	 NA				("99")
	,NEW			("00")
	,NORMAL 		("01")
	,VANISH			("02")
	,MATURITY 		("03")
	,LAPSE			("04")
	,REVIVE			("05")
	,RESTORATION	("05")
	,CHANGE			("06")
	,WITHDRAW		("08")
	,ALL			("100")
	
;
	
	private String orinCode;
	
	private EContStatus(String orinCode) {
		this.orinCode =orinCode;
	}
}
