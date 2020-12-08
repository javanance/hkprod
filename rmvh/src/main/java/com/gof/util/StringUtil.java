package com.gof.util;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
@Slf4j

public class StringUtil {
		
	public static String ColNametoCarmel(String s) {
		char[] c = s.toLowerCase().trim().replaceAll(" ", "").toCharArray();
		String sChar = "";
		String sGen = "";
		boolean bUpper = false;
		for(int i = 0 ; i < c.length ; i++) {
			if(bUpper) {
				sGen += (char)((int)c[i]-32);
				bUpper = false;
			}else if(i==0 && ((int)c[i] != 95)) {
				sGen += (char)((int)c[i]);
			}else if((int)c[i] == 95) {			//ascii code 95 =  "_"
				sGen += " ";
				bUpper = true;
			}else {
				sGen += c[i];
			}
			log.info("Char : {},{}", c[i], (int)c[i]);
			
		}
		return sGen.replaceAll(" ", "");
	}

	public static String ColNametoPascal(String s) {
		char[] c = s.toLowerCase().trim().replaceAll(" ", "").toCharArray();
		String sGen = "";
		boolean bUpper = false;
		for(int i = 0 ; i < c.length ; i++) {
			if(bUpper) {
				sGen += (char)((int)c[i]-32);
				bUpper = false;
			}else if(i==0 && ((int)c[i] != 95)) {
				sGen += (char)((int)c[i]-32);
			}else if((int)c[i] == 95) {
				sGen += " ";
				bUpper = true;
			}else {
				sGen += c[i];
			}
//			System.out.println(c[i]+" : "+(int)c[i]);
			
		}
//		System.out.println(sGen.replaceAll(" ", ""));
	
		return sGen.replaceAll(" ", "");
	}
	
	
	public static String toStringWith(String dilemeter, String...columns){
		StringBuilder builder = new StringBuilder();
		for(String col : columns){
			builder.append(dilemeter).append(col);
		}
		
		return builder.toString().replaceFirst(dilemeter, "");
	}
	
	public static String toStringWith(String dilemeter, List<? extends Object> columns){
		StringBuilder builder = new StringBuilder();
		for(Object col : columns){
			builder.append(dilemeter).append(col.toString());
		}
		
		return builder.toString().replaceFirst(dilemeter, "");
	}
	
	public static String toCsv(String...columns){
		return toStringWith(",", columns);
	}
	
	public static String toCsv(List<? extends Object> columns){
		return toStringWith(",", columns);
	}
	
	
	
}
