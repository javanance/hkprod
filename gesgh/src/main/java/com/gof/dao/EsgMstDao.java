package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.EsgMst;
import com.gof.enums.EBoolean;
import com.gof.util.HibernateUtil;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

public class EsgMstDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	private static String baseQuery = "select a from EsgMst a where 1=1 ";

	public static List<EsgMst> getEntities() {
		return session.createQuery(baseQuery, EsgMst.class).getResultList();
	}

	
	public static List<EsgMst> getEsgMst(EBoolean useYn) {
		String q = "select a from EsgMst a where a.useYn = :param";
		
		return session.createQuery(q, EsgMst.class)
				.setParameter("param", useYn)
				.getResultList();
	}
}
