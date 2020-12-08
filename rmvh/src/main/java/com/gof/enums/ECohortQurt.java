package com.gof.enums;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Predicate;

import com.gof.util.DateUtil;

import lombok.Getter;

@Getter
public enum ECohortQurt {

	C2015Q1	("2015Q1", 	s-> s.compareTo("201601") <  0 )
,   C2016Q1	("2016Q1",	s-> s.compareTo("201601") >= 0  && s.compareTo("201701") < 0 )	
,   C2017Q1	("2017Q1",	s-> s.compareTo("201701") >= 0  && s.compareTo("201801") < 0)
,   C2018Q1	("2018Q1",	s-> s.compareTo("201801") >= 0 	&& s.compareTo("201804") < 0)
,   C2018Q2	("2018Q2",	s-> s.compareTo("201804") >= 0  && s.compareTo("201807") < 0)
,   C2018Q3	("2018Q3",	s-> s.compareTo("201807") >= 0  && s.compareTo("201810") < 0)
,   C2018Q4	("2018Q4",	s-> s.compareTo("201810") >= 0  && s.compareTo("201901") < 0)
,   C2019Q1	("2019Q1",	s-> s.compareTo("201901") >= 0  && s.compareTo("201904") < 0)
;
	
	private String code;
	private Predicate<String> predicate;
	
	private ECohortQurt(String code, Predicate<String> predicate) {
		this.code =code;
		this.predicate = predicate;
	}
	
	
	public static String getECohrtQurt(String initYymm) {
		for(ECohortQurt aa : ECohortQurt.values()){
			if(aa.getPredicate().test(initYymm)) {
				return aa.getCode();
			}
		}
		return  DateUtil.convertFrom(initYymm).format(DateTimeFormatter.ofPattern("YYYYQQQ", Locale.US));
	}
}
