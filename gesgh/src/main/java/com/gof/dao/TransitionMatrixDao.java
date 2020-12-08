package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.TransitionMatrix;
import com.gof.entity.TransitionMatrixUd;
import com.gof.util.EsgConstant;
import com.gof.util.HibernateUtil;

/**
 *  <p> ������� ������ �����ϴ� DAO
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
public class TransitionMatrixDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<TransitionMatrixUd> getTMUd(String bssd) {
//		 String tmType = ParamUtil.getParamMap().getOrDefault("tmType", "STM1");
		 String tmType =EsgConstant.getStrConstant().getOrDefault("tmType", "STM1");
		 String sql = "select a from TransitionMatrixUd a "
	    			+ "where a.tmType = :param "
	    			+ "and a.baseYyyy = :time" 
	    			;
	    
	    List<TransitionMatrixUd> rst = session.createQuery(sql, TransitionMatrixUd.class)
	    							.setParameter("param", tmType)
	    							.setParameter("time",bssd.substring(0,4))
	    							.getResultList();

	    
		return rst;
	}
	
	
	public static List<TransitionMatrixUd> getDefaultRateUd(String bssd) {
//		String tmType = ParamUtil.getParamMap().getOrDefault("tmType", "STM1");
		String tmType =EsgConstant.getStrConstant().getOrDefault("tmType", "STM1");
		
		
		String sql = "select a from TransitionMatrixUd a "
	    			+ "where a.tmType = :param "
	    			+ "and a.toGrade  = :param2 "
	    			+ "and a.baseYyyy = :time" 
	    			;
	    
	    List<TransitionMatrixUd> rst = session.createQuery(sql, TransitionMatrixUd.class)
	    							.setParameter("param", tmType)
	    							.setParameter("param2", "D")
	    							.setParameter("time",bssd.substring(0,4))
	    							.getResultList();
		return rst;
	}
	
	
	
	public static List<TransitionMatrix> getTM(String bssd) {
//		 String tmType = ParamUtil.getParamMap().getOrDefault("tmType", "STM1");
		 String tmType =EsgConstant.getStrConstant().getOrDefault("tmType", "STM1");
		 String sql = "select a from TransitionMatrix a "
	    			+ "where a.tmType = :param "
	    			+ "and a.baseYyyy = :time" 
	    			;
	    
	    List<TransitionMatrix> rst = session.createQuery(sql, TransitionMatrix.class)
	    							.setParameter("param", tmType)
	    							.setParameter("time",bssd.substring(0,4))
	    							.getResultList();

	    
		return rst;
	}
	
	public static List<TransitionMatrix> getDefaultRate(String bssd) {
//		String tmType = ParamUtil.getParamMap().getOrDefault("tmType", "STM1");
		String tmType =EsgConstant.getStrConstant().getOrDefault("tmType", "STM1");
		
		
		String sql = "select a from TransitionMatrix a "
	    			+ "where a.tmType = :param "
	    			+ "and a.toGrade  = :param2 "
	    			+ "and a.baseYyyy = :time" 
	    			;
	    
	    List<TransitionMatrix> rst = session.createQuery(sql, TransitionMatrix.class)
	    							.setParameter("param", tmType)
	    							.setParameter("param2", "D")
	    							.setParameter("time",bssd.substring(0,4))
	    							.getResultList();
		return rst;
	}
}
