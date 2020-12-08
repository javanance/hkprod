package com.gof.enums;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Predicate;

import com.gof.util.DateUtil;

import lombok.Getter;

@Getter
public enum ECohortYear {

	C2015Y	("Y2015", 	s-> s.contains("2015") )
,   C2016Y	("Y2016",	s-> s.contains("2016") )	
,   C2017Y	("Y2017",	s-> s.contains("2017") )
,   C2018Y	("Y2018",	s-> s.contains("2018") )
,   C2019Y	("Y2019",	s-> s.contains("2019") )
;
	
	private String code;
	private Predicate<String> predicate;
	
	private ECohortYear(String code, Predicate<String> predicate) {
		this.code =code;
		this.predicate = predicate;
	}
	
	
	public static String getECohortYear(String initYymm) {
		for(ECohortYear aa : ECohortYear.values()){
			if(aa.getPredicate().test(initYymm)) {
				return aa.getCode();
			}
		}
		return  DateUtil.convertFrom(initYymm).format(DateTimeFormatter.ofPattern("YYYY", Locale.US));
	}
}
