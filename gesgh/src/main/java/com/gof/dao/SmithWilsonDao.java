package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.SmithWilsonParamHis;
import com.gof.util.HibernateUtil;

/**
 *  <p> �ݸ������� �Ű����� ������ ������.
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

public class SmithWilsonDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	
	public static List<SmithWilsonParamHis> getParamHisList(String bssd) {
		String q = "select a from SmithWilsonParamHis a "
				+ "where 1=1 "
				+ "and a.applStartYymm < :bssd "
				+ "and a.applEndYymm   >= :bssd "
				;
		
		return session.createQuery(q, SmithWilsonParamHis.class)
								.setParameter("bssd", bssd)
								.list()
		;

	}
	
}
