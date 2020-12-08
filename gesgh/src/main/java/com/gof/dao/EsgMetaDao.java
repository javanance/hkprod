package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.EsgMeta;
import com.gof.enums.EBoolean;
import com.gof.util.HibernateUtil;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

public class EsgMetaDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<EsgMeta> getEsgMeta() {
		String q = "select a from EsgMeta a "	;
		return session.createQuery(q, EsgMeta.class).getResultList();
	}

	public static List<EsgMeta> getEsgMeta(String groupId) {
		String q = " select a from EsgMeta a "
				+ "   where 1=1 "
				+ "	  and a.groupId = :groupId	" 
				+ "   and a.useYn = :param"
				;
		
		return session.createQuery(q, EsgMeta.class)
				.setParameter("groupId", groupId)
				.setParameter("param", EBoolean.Y)
				.getResultList();
	}
}
