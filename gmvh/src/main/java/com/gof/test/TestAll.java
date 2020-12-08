package com.gof.test;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import com.gof.dao.MstDao;
import com.gof.entity.MapJournalRollFwd;
import com.gof.enums.ECalcMethod;
import com.gof.infra.GmvConstant;
import com.gof.process.Main;
import com.gof.provider.PrvdMst;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestAll {

	public static void main(String[] args) {
		String[] param = new String[4];
		param[0]= "-Dtime=201901";
		param[1]= "-Dvtime=201901";
		param[2]= "-Dbtime=201812";
//		param[3]= "-Dproperties=D:/Dev/Mov/gmv.properties";
		param[3]= "-Dproperties=C:/Dev/gmv_v3.properties";
		
		Main.initProperties(param);
		Main.initConstant();
		
		MstDao.getMstRollFwd().forEach(s->log.info("zz : {},{}", GmvConstant.GOC_GROUP, s.getRollFwdType()));
		
		List<MapJournalRollFwd> list = PrvdMst.getJournalRollFwdList().stream().filter(s->s.getSubSeq()>10).collect(toList());
		
		log.info("qqqqq111 :  {}", list.size(), list.size()==0);
		
		for(MapJournalRollFwd qq : list) {
			log.info("qqqqq :  {}", qq.getRollFwdType());
		}
		
		
		log.info("mod : {}", (int)(1.5*2) % 2);
		log.info("mod : {}", Math.floorMod(5, 2));
		
		
//		aaa();
		bbb();
		
		log.info("aaa1 : {}", DateUtil.monthBetween("201809", "201812"));
		log.info("aaa2 : {}", ECalcMethod.CSM_PREV.equals(null));
		
		log.info("xxx : {}",DateUtil.monthBetween("202001", "202001"));
		
		
		log.info("aaa3 : {}",DateUtil.monthBetween("202001", "202002"));
		log.info("aaa3 : {}",DateUtil.monthBetween("202002", "202003"));
		log.info("aaa3 : {}",DateUtil.monthBetween("202003", "202004"));
		log.info("aaa3 : {}",DateUtil.monthBetween("202004", "202005"));
		log.info("aaa3 : {}",DateUtil.monthBetween("202005", "202006"));
		log.info("aaa3 : {}",DateUtil.monthBetween("202006", "202007"));
		log.info("aaa3 : {}",DateUtil.monthBetween("202007", "202008"));
		log.info("aaa3 : {}",DateUtil.monthBetween("202008", "202009"));
		log.info("aaa3 : {}",DateUtil.monthBetween("202009", "202010"));
		log.info("aaa3 : {}",DateUtil.monthBetween("202010", "202011"));
		log.info("aaa3 : {}",DateUtil.monthBetween("202011", "202012"));
//		log.info("aaa3 : {}",DateUtil.monthBetween("202012", "202002"));
		
		
		for(int i=0 ; i< 200; i++) {
//			log.info("aaa3 : {}, {}",i, DateUtil.addMonthToString("202001", i));
			
		}
		
		
		log.info("qqq : {}", PrvdMst.getMstGoc("0201_2019_1"));

		
	}

	private static void aaa() {
		String bssd  ="201811";
		log.info("aaa : {}",  DateUtil.convertFrom(bssd).format(DateTimeFormatter.ofPattern("YYYYQQQ", Locale.US)));
		LocalDate aa  = DateUtil.convertFrom(bssd);
		log.info("aaa : {},{}" ,aa.getMonthValue(), ( aa.getMonthValue()-1) /3 +1);

		LocalDate bb = aa.minus(aa.getMonthValue()-1, ChronoUnit.MONTHS);
		log.info("bbb : {},{}" ,bb.getMonthValue(), bb.getMonthValue()/3);
		
		
	}
	private static void bbb() {
		String bssd  ="201811";
		LocalDate aa  = DateUtil.convertFrom(bssd);
		for( int i=0 ; i< 12 ; i++) {
			String zz = DateUtil.addMonthToString(bssd, -i);
//			log.info("bbb : {},{}" ,zz, DateUtil.getPrevQuater(zz));
			
		}
		
		log.info("aaa : {}", DateUtil.isGreaterThan(bssd, "201809"));
		log.info("aaa : {}", DateUtil.isGreaterThan(bssd, "201811"));
		log.info("aaa : {}", DateUtil.isGreaterThan(bssd, "201812"));
	}
}
