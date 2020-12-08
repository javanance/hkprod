package com.gof.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.entity.RatioCovUnit;
import com.gof.entity.RatioDac;
import com.gof.infra.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> BottomUp ������ ������{@link BottomupDcnt} �� DataBase ���� �����ϴ� �����? �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class RatioDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<RatioCovUnit> getCovUnit(String bssd ){
		String query = "select a from RatioCovUnit a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RatioCovUnit> q = session.createQuery(query, RatioCovUnit.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	
	public static List<RatioDac> getRatioDac(String bssd ){
		String query = "select a from RatioDac a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RatioDac> q = session.createQuery(query, RatioDac.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	
	
}
