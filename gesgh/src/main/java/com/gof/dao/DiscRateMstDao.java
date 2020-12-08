package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.DiscRateMst;
import com.gof.util.HibernateUtil;


public class DiscRateMstDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<DiscRateMst> getDiscRateMstList(){
		String query = "select a from DiscRateMst a ";
		return session.createQuery(query, DiscRateMst.class).getResultList();
		 		
//	    List<DiscRateMst> rst = session.createQuery(query, DiscRateMst.class).getResultList();
//	    return rst;
	    
	}
	
	public static DiscRateMst getDiscRateMst(String intRateCd){
		String sql = "select a from DiscRateMst a where a.intRateCd = :param" ;
	 		
	    DiscRateMst rst = session.createQuery(sql, DiscRateMst.class)
	    		.setParameter("param", intRateCd)
	    		.getSingleResult();
	    
		return rst;
	}
}
