package com.gof.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class DateUtil {
	public static boolean isGreaterThan(String yyyymm, String otherYymm) {
		if(yyyymm.compareTo(otherYymm) >= 1) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static String getPrevQuater(String yyyymm) {
		LocalDate date  = DateUtil.convertFrom(yyyymm);
		LocalDate bb 	= date.minus(date.getMonthValue()-1, ChronoUnit.MONTHS);
		int prevQuater 		= (date.getMonthValue()-1) /3 ;
		
		return bb.plusMonths( prevQuater * 3 -1).format(DateTimeFormatter.ofPattern("yyyyMM"));	 
		
	}
	
	public static boolean isGreaterOrEqual(String yyyymm, String otherYymm) {
//		log.info("aaa compare: {},{},{}", yyyymm, otherYymm, yyyymm.compareTo(otherYymm));
		if(yyyymm.compareTo(otherYymm) >= 0	) {
			return true;
		}
		else {
			return false;
		}
		
	}
	public static LocalDate convertFrom(String yyyymmdd) {
		String bssd =  yyyymmdd.replaceAll("-", "").replaceAll("/", "");
		
		if(bssd.length()==4) {
			return  LocalDate.parse(bssd+"0101", DateTimeFormatter.BASIC_ISO_DATE);
		}
		else if(bssd.length()==6) {
			return  LocalDate.parse(bssd+"01", DateTimeFormatter.BASIC_ISO_DATE).with(TemporalAdjusters.lastDayOfMonth());
		}
		else if(bssd.length()==8) {
			return  LocalDate.parse(bssd, DateTimeFormatter.BASIC_ISO_DATE);
		}
		else {
			log.error("Date Convert Error : {} at DateUtil", yyyymmdd);
			System.exit(1);
		}
		return null;
	}
	
	public static int monthBetween(String from, String to) {
		return  Period.between(convertFrom(from).with(TemporalAdjusters.firstDayOfMonth()) 
								, convertFrom(to).with(TemporalAdjusters.firstDayOfMonth()))
						.getMonths();
	}
	
	 public static LocalDate addMonth(String yyyymmdd, int addNum) {
		 return convertFrom(yyyymmdd).plusMonths(addNum); 
	 }
	 
	 public static String addMonthToString(String yyyymmdd, int addNum) {
		 if(yyyymmdd.length()==6) {
			 return addMonth(yyyymmdd, addNum).format(DateTimeFormatter.ofPattern("yyyyMM"));
		 }
		 if(yyyymmdd.length()==8) {
			 return addMonth(yyyymmdd, addNum).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		 }
		 else {
			 return addMonth(yyyymmdd, addNum).format(DateTimeFormatter.ofPattern("YYYYMMDD"));
			 
		 }
	 }
}
