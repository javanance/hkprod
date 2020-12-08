package com.gof.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.entity.RaLv1;
import com.gof.entity.RaLv2Delta;
import com.gof.infra.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> BottomUp ������ ������{@link BottomupDcnt} �� DataBase ���� �����ϴ� �����? �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class RaDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	
	public static List<RaLv1> getRaLv1(String baseYymm ){
		String query = "select a from RaLv1 a "
				+ "where a.baseYymm  = :baseYymm "
				;
		
		Query<RaLv1> q = session.createQuery(query, RaLv1.class);
		q.setParameter("baseYymm", baseYymm);
		return q.getResultList();
	}
	
	public static List<RaLv1> getRaLv1(String baseYymm , String gocId){
		if(gocId ==null) {
			log.info("Ra Lv1 goc Id is null");
			return getRaLv1(baseYymm);
		}
		String query = "select a from RaLv1 a "
				+ "where a.baseYymm  = :baseYymm "
				+ "and a.gocId  = :gocId "
				;
		
		Query<RaLv1> q = session.createQuery(query, RaLv1.class);
		q.setParameter("baseYymm", baseYymm);
		q.setParameter("gocId", gocId);
		return q.getResultList();
	}
	
	public static List<RaLv2Delta> getRaLv2Delta(String baseYymm ){
		String query = "select a from RaLv2Delta a "
				+ "where a.baseYymm  = :baseYymm "
				;
		
		Query<RaLv2Delta> q = session.createQuery(query, RaLv2Delta.class);
		q.setParameter("baseYymm", baseYymm);
		return q.getResultList();
	}
	
	public static List<RaLv2Delta> getRaLv2Delta(String baseYymm, String gocId){
		if(gocId ==null) {
			return getRaLv2Delta(baseYymm);
		}
		String query = "select a from RaLv2Delta a "
				+ "where a.baseYymm  = :baseYymm "
				+ "and a.gocId =:gocId"
				;
		
		Query<RaLv2Delta> q = session.createQuery(query, RaLv2Delta.class);
		q.setParameter("baseYymm", baseYymm);
		q.setParameter("gocId", gocId);
		return q.getResultList();
	}
	
}
