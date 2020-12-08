package com.gof.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.entity.TvogLv1;
import com.gof.entity.TvogLv2Delta;
import com.gof.infra.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> BottomUp ������ ������{@link BottomupDcnt} �� DataBase ���� �����ϴ� �����? �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class TvogDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
		
	public static List<TvogLv1> getTvogLv1(String bssd ){
		String query = "select a from TvogLv1 a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<TvogLv1> q = session.createQuery(query, TvogLv1.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<TvogLv1> getTvogLv1(String bssd, String gocId ){
		if(gocId==null) {
			return getTvogLv1(bssd);
		}
		
		String query = "select a from TvogLv1 a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId"
				;
		
		Query<TvogLv1> q = session.createQuery(query, TvogLv1.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		return q.getResultList();
	}
	
	public static List<TvogLv2Delta> getTvogLv2Delta(String bssd ){
		String query = "select a from TvogLv2Delta a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<TvogLv2Delta> q = session.createQuery(query, TvogLv2Delta.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<TvogLv2Delta> getTvogLv2Delta(String bssd , String gocId){
		if(gocId==null) {
			return getTvogLv2Delta(bssd);
		}
		String query = "select a from TvogLv2Delta a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId"
				;
		
		Query<TvogLv2Delta> q = session.createQuery(query, TvogLv2Delta.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		return q.getResultList();
	}
}
