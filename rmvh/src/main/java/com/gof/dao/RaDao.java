package com.gof.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.entity.RaLv1;
import com.gof.entity.RaLv2Delta;
import com.gof.infra.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> BottomUp ������ ������{@link BottomupDcnt} �� DataBase ���� �����ϴ� ����� �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class RaDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	
	public static List<RaLv1> getRaLv1(String bssd ){
		String query = "select a from RaLv1 a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RaLv1> q = session.createQuery(query, RaLv1.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<RaLv1> getRaLv1(String bssd , String gocId){
		if(gocId ==null) {
			return getRaLv1(bssd);
		}
		String query = "select a from RaLv1 a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RaLv1> q = session.createQuery(query, RaLv1.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<RaLv2Delta> getRaLv2Delta(String bssd ){
		String query = "select a from RaLv2Delta a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RaLv2Delta> q = session.createQuery(query, RaLv2Delta.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<RaLv2Delta> getRaLv2Delta(String bssd, String gocId){
		if(gocId ==null) {
			return getRaLv2Delta(bssd);
		}
		String query = "select a from RaLv2Delta a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId =:gocId"
				;
		
		Query<RaLv2Delta> q = session.createQuery(query, RaLv2Delta.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		return q.getResultList();
	}
	
}
