package com.gof.test;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.gof.dao.MstDao;
import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.RstBoxGoc;
import com.gof.entity.TstSeq;
import com.gof.enums.ECalcMethod;
import com.gof.infra.GmvConstant;
import com.gof.infra.HibernateUtil;
import com.gof.process.Main;
import com.gof.provider.PrvdMst;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestSeq {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static void main(String[] args) {
		String[] param = new String[4];
		param[0]= "-Dtime=201901";
		param[1]= "-Dvtime=201901";
		param[2]= "-Dbtime=201812";
//		param[3]= "-Dproperties=D:/Dev/Mov/gmv.properties";
		param[3]= "-Dproperties=C:/Dev/gmv_v3.properties";
		
		File aaa  = new File("./hibernate.cfg.gmv.xml");
		log.info("aaa a: {}", aaa.getAbsoluteFile());
		
		Main.initProperties(param);
		Main.initConstant();
		
//		
//		IntStream.rangeClosed(1,1+DateUtil.monthBetween("201912", "202003")).forEach(s->log.info("qqqq : {}", s));
//		
//		IntStream.rangeClosed(0,DateUtil.monthBetween("201912", "201912")).forEach(s->log.info("dddd : {}", s));
		
		
//		String query = "from TstSeq";
//		Query<TstSeq> q = session.createQuery(query, TstSeq.class);
//		q.stream().forEach(s->log.info("aaaa : {}", s.getCfId()));
//		
//		try {
//			Transaction tx = session.beginTransaction();
//			for(int i=0; i< 100;   i++) {
//				TstSeq test = new TstSeq();
//				session.save(test);
//			}
//			tx.commit();
//			
//		} catch (Exception e) {
//			log.info("error :  {}", e);
//		}
		
		
		
	}

}
