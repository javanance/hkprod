package com.gof.dao;

import java.util.List;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.entity.DfLv1CurrRate;
import com.gof.entity.DfLv2Eir;
import com.gof.entity.DfLv2EirNewgoc;
import com.gof.entity.DfLv2InitRate;
import com.gof.entity.DfLv2WghtHis;
import com.gof.entity.DfLv2WghtRate;
import com.gof.entity.DfLv3Flat;
import com.gof.entity.DfLv4Eir;
import com.gof.enums.ELiabType;
import com.gof.infra.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> BottomUp ������ ������{@link BottomupDcnt} �� DataBase ���� �����ϴ� �����? �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class DfDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<DfLv1CurrRate> getDfLv1CurrRate(String irCurveId ){
		String query = "from DfLv1CurrRate a where a.irCurveId =:irCurveId" ;
		
		Query<DfLv1CurrRate> q = session.createQuery(query, DfLv1CurrRate.class);
		q.setParameter("irCurveId", irCurveId);

		return q.getResultList();
	}
	
	public static Stream<DfLv1CurrRate> getDfLv1CurrRateStream(String irCurveId ){
		String query = "from DfLv1CurrRate a where a.irCurveId =:irCurveId";
		
		Query<DfLv1CurrRate> q = session.createQuery(query, DfLv1CurrRate.class);
		q.setParameter("irCurveId", irCurveId);

		return q.stream();
	}
	
	public static Stream<DfLv1CurrRate> getDfLv1CurrRateByDateStream(String bssd ){
		String query = "select a from DfLv1CurrRate a "
				+ "where a.baseYymm =:bssd"
			;
		
		Query<DfLv1CurrRate> q = session.createQuery(query, DfLv1CurrRate.class);
		q.setParameter("bssd", bssd);

		return q.stream();
	}
	
	public static List<DfLv1CurrRate> getDfLv1CurrRate(String bssd, String irCurveId ){
		String query = "select a from DfLv1CurrRate a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.irCurveId =:irCurveId "
				+ "order by a.cfMonthNum "
				;
		
		Query<DfLv1CurrRate> q = session.createQuery(query, DfLv1CurrRate.class);
		q.setParameter("bssd", bssd);
		q.setParameter("irCurveId", irCurveId);
		
		log.debug("Term Sructure param : {},{},{}", bssd) ;
		return q.getResultList();
	}
	
	public static List<DfLv1CurrRate> getDfLv1CurrRateBtw(String stBssd ,String bssd, String irCurveId ){
		String query = "select a from DfLv1CurrRate a "
				+ "where a.baseYymm  <= :bssd "
				+ "and a.baseYymm >= :stBssd "
				+ "and a.irCurveId =:irCurveId "
				+ "order by a.cfMonthNum "
				;
		
		Query<DfLv1CurrRate> q = session.createQuery(query, DfLv1CurrRate.class);
		q.setParameter("bssd", bssd);
		q.setParameter("stBssd", stBssd);
		q.setParameter("irCurveId", irCurveId);
		
		return q.getResultList();
	}
	
	public static List<DfLv1CurrRate> getDfLv1CurrRate(String bssd, String irCurveId, double maxTenor ){
		String query = "select a from CurrentCurve a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.irCurveId =:irCurveId "
				+ "and a.matNum <= :maxTenor"
				;
		
		Query<DfLv1CurrRate> q = session.createQuery(query, DfLv1CurrRate.class);
		q.setParameter("bssd", bssd);
		q.setParameter("irCurveId", irCurveId);
		q.setParameter("maxTenor", maxTenor);
		
		log.debug("Term Sructure param : {},{},{}", bssd) ;
		return q.getResultList();
	}
	
	public static List<DfLv2InitRate> getDfLv2InitRate(){
		String query = "select a from DfLv2InitRate a ";
		
		Query<DfLv2InitRate> q = session.createQuery(query, DfLv2InitRate.class);
		
		return q.getResultList();
	}
	
	public static List<DfLv2InitRate> getDfLv2InitRate(String gocId ){
		if(gocId==null) {
			return getDfLv2InitRate();
		}
		
		String query = "select a from DfLv2InitRate a where a.gocId  = :gocId "		;
		
		Query<DfLv2InitRate> q = session.createQuery(query, DfLv2InitRate.class);
		q.setParameter("gocId", gocId);
		
		return q.getResultList();
	}

	public static List<String> getUsedGocInDfLv2InitRate(){
		String query = "select a.gocId from DfLv2InitRate a group by a.gocId"	;
		
		return session.createQuery(query, String.class).getResultList();
		
	}

	public static List<DfLv2WghtHis> getDfLv2WghtHis(String bssd ){
		String query = "select a from DfLv2WghtHis a where a.baseYymm  = :bssd "	;
		
		Query<DfLv2WghtHis> q = session.createQuery(query, DfLv2WghtHis.class);
		q.setParameter("bssd", bssd);
		
		return q.getResultList();
	}
	
	public static List<DfLv2WghtHis> getDfLv2WghtHis(String bssd, String gocId ){
		if(gocId==null) {
			return getDfLv2WghtHis(bssd);
		}
		String query = "select a from DfLv2WghtHis a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId"
				;
		
		Query<DfLv2WghtHis> q = session.createQuery(query, DfLv2WghtHis.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		
		return q.getResultList();
	}
	
	public static List<DfLv2WghtRate> getDfLv2WghtRate(){
		String query = "select a from DfLv2WghtRate a ";
		
		Query<DfLv2WghtRate> q = session.createQuery(query, DfLv2WghtRate.class);
		
		return q.getResultList();
	}
	
	public static List<DfLv2WghtRate> getDfLv2WghtRate(String gocId ){
		if(gocId==null) {
			return getDfLv2WghtRate();
		}
		String query = "select a from DfLv2WghtRate a where a.gocId  = :gocId "
				;
		
		Query<DfLv2WghtRate> q = session.createQuery(query, DfLv2WghtRate.class);
		q.setParameter("gocId", gocId);
		
		return q.getResultList();
	}

	public static List<DfLv2WghtHis> getDfLv2WghtRate(String bssd, String gocId ){
		String query = "select a from DfLv2WghtHis a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId"
				;
		
		Query<DfLv2WghtHis> q = session.createQuery(query, DfLv2WghtHis.class);
		q.setParameter("bssd", getDfLv2WghtHisLastBssd(bssd, gocId));
		q.setParameter("gocId", gocId);
		
		return q.getResultList();
	}

	public static List<DfLv2WghtHis> getPrevDfLv2Wght(String bssd, String gocId ){
		String query = "select a from DfLv2WghtHis a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId"
				;
		
		Query<DfLv2WghtHis> q = session.createQuery(query, DfLv2WghtHis.class);
		q.setParameter("bssd", getDfLv2WghtHisPreviousBssd(bssd, gocId));
		q.setParameter("gocId", gocId);
		
		return q.getResultList();
	}
	
	public static List<DfLv2WghtRate> getDfLv2WghtRateByTenor(double cfMonthNum){
		String query = "select a from DfLv2WghtRate a where a.cfMonthNum  = :cfMonthNum "
				;
		
		Query<DfLv2WghtRate> q = session.createQuery(query, DfLv2WghtRate.class);
		q.setParameter("cfMonthNum", cfMonthNum);
		return q.getResultList();
	}
	
	public static List<DfLv2Eir> getLv2Eir(String bssd ){
		String query = "select a from DfLv2Eir a where a.baseYymm  = :bssd "	;
		
		Query<DfLv2Eir> q = session.createQuery(query, DfLv2Eir.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<DfLv4Eir> getLv4Eir(String bssd ){
		String query = "select a from DfLv4Eir a where a.baseYymm  = :bssd " ;
		
		Query<DfLv4Eir> q = session.createQuery(query, DfLv4Eir.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<DfLv4Eir> getEir(String bssd ){
		String query = "select a from DfLv4Eir a where a.baseYymm  = :bssd ";
		
		Query<DfLv4Eir> q = session.createQuery(query, DfLv4Eir.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<DfLv4Eir> getEir(String bssd, ELiabType liabType ){
		String query = "select a from DfLv4Eir a "
				+ "where a.baseYymm  = :bssd "
				+ " and a.liabType = :liabType"
				;
		
		Query<DfLv4Eir> q = session.createQuery(query, DfLv4Eir.class);
		q.setParameter("bssd", bssd);
		q.setParameter("liabType", liabType);
		return q.getResultList();
	}
	
	
	public static List<DfLv2EirNewgoc> getEirNewgoc(String bssd){
		String query = "select a from DfLv2EirNewgoc a where a.baseYymm  = :bssd "				;
		
		Query<DfLv2EirNewgoc> q = session.createQuery(query, DfLv2EirNewgoc.class);
		q.setParameter("bssd", bssd);
		
		return q.getResultList();
	}
	
	
	public static List<DfLv2EirNewgoc> getEirNewgoc(){
		String query = "select a from DfLv2EirNewgoc a "
				;
		
		Query<DfLv2EirNewgoc> q = session.createQuery(query, DfLv2EirNewgoc.class);
		return q.getResultList();
	}
	
	public static List<DfLv3Flat> getDfLv3Flat(String bssd ){
		String query = "select a from DfLv3Flat a "
				+ "where a.baseYymm = :bssd "
//				+ " and a.evalYymm = :vBssd"
				;
		
		Query<DfLv3Flat> q = session.createQuery(query, DfLv3Flat.class);
		q.setParameter("bssd", bssd);
		
		return q.getResultList();
	}
	
	public static List<DfLv3Flat> getDfLv3Flat(String bssd, String gocId ){
			String query = "select a from DfLv3Flat a "
					+ "where a.gocId  = :gocId "
					+ " and a.baseYymm = :bssd "
	//				+ " and a.evalYymm = :vBssd"
					;
			
			Query<DfLv3Flat> q = session.createQuery(query, DfLv3Flat.class);
			q.setParameter("gocId", gocId);
			q.setParameter("bssd", bssd);
			
			return q.getResultList();
		}

	public static Stream<DfLv3Flat> getDfLv3FlatStream(String bssd ){
		String query = "select a from DfLv3Flat a "
				+ "where a.baseYymm = :bssd "
//				+ " and a.evalYymm = :vBssd"
				;
		
		Query<DfLv3Flat> q = session.createQuery(query, DfLv3Flat.class);
		q.setParameter("bssd", bssd);
		
		return q.stream();
	}
	public static Stream<DfLv3Flat> getDfLv3FlatStream(String bssd, String gocId){
		if(gocId==null) {
			return getDfLv3FlatStream(bssd);
		}
		String query = "select a from DfLv3Flat a "
				+ "where a.baseYymm = :bssd "
				+ "and a.evalYymm = :bssd "
				+ "and a.gocId  = :gocId "
				;
		
		Query<DfLv3Flat> q = session.createQuery(query, DfLv3Flat.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		
		return q.stream();
	}
	
	private static String getDfLv2WghtHisPreviousBssd(String bssd, String gocId ){
		String query = "select max(a.baseYymm) from DfLv2WghtHis a "
				+ "where a.baseYymm  < :bssd "
				+ " and a.gocId = :gocId"
				;
		
		Query<String> q = session.createQuery(query, String.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		
		return q.getSingleResult();
	}
	
	private static String getDfLv2WghtHisLastBssd(String bssd, String gocId ){
		String query = "select max(a.baseYymm) from DfLv2WghtHis a "
				+ "where a.baseYymm  <= :bssd "
				+ " and a.gocId = :gocId"
				;
		
		Query<String> q = session.createQuery(query, String.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		
		return q.getSingleResult();
	}
}
