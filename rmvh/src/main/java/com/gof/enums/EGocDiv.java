package com.gof.enums;

import java.util.function.Predicate;

import lombok.Getter;

@Getter
public enum EGocDiv {
	
	 NEW			("",   	s->"Y".equals(s))
	,HOLDING		("08",	s->!"Y".equals(s))
	,ALL			("99",	s->true) {
		public boolean contains(EGocDiv gocDiv) {
			return true;
		}
	 }
	
;
	
	private String orinCode;
	private Predicate<String> predicateFn;
	
	private EGocDiv(String orinCode, Predicate<String> predicateFn) {
		this.orinCode =orinCode;
		this.predicateFn =predicateFn;
	}
	
	public boolean contains(EGocDiv gocDiv) {
		return this.equals(gocDiv);
	}
}
