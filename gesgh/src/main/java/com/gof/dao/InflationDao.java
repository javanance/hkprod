package com.gof.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;

import com.gof.entity.Inflation;
import com.gof.entity.InflationUd;
import com.gof.util.FinUtils;
import com.gof.util.HibernateUtil;

/**
 *  <p> ���÷��̼� ������ �����ϴ� DAO
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
public class InflationDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
//	private static CriteriaBuilder crit = session.getCriteriaBuilder();
			
	private static String baseQuery = "select a from Inflation a where 1=1 ";

	public static List<Inflation> getEntities() {
		return session.createQuery(baseQuery, Inflation.class).getResultList();
	}
	

	/** 
	*  <p> ����ڰ� �Է��� ���÷��̼� ���� ���� ���� 
	*  <p> �ַ� ���⵿�� ��� ���÷��̼� ��·� ������ ���� ���� 1��ġ ������ ������
	*  @param bssd 	   ���س��
	*  @param monNum  ���س�� ���� ���� ���� 
	*  @return		  ����� �Է� ���÷��̼� ����                 
	*/ 
	public static List<InflationUd> getPrecedingInflationUd(String bssd, int monNum) {
		String sql = "select a "
				+ "			from InflationUd a "
    			+ "			where 1=1"
    			+ "			and a.baseYymm  >=  :stTime"
    			+ "			and a.baseYymm  <=  :endTime"
		;
		 
	    List<InflationUd> rst = session.createQuery(sql, InflationUd.class)
						    		.setParameter("stTime",FinUtils.addMonth(bssd, monNum))
	    							.setParameter("endTime",bssd)
	    							.getResultList();
		return rst;

	}

	public static List<InflationUd> getInflationUd(String bssd) {
		String sql = "select a "
				+ "			from InflationUd a "
    			+ "			where 1=1"
    			+ "			and a.baseYymm  =  :endTime"
		;
		 
	    List<InflationUd> rst = session.createQuery(sql, InflationUd.class)
//						    		.setParameter("stTime",FinUtils.addMonth(bssd, monNum))
	    							.setParameter("endTime",bssd)
	    							.getResultList();
		return rst;

	}
	/** 
	*  <p> 
	*   
	*  @param bssd 	   
	*  @param monNum   
	*  @return		                  
	*/ 
	public static List<Inflation> getPrecedingInflation(String bssd, int monNum) {
		 String sql = "select a from Inflation a "
	    			+ "			where 1=1"
	    			+ "			and a.baseYymm  >  :stTime"
	    			+ "			and a.baseYymm  <=   :endTime"
	    			;
		 
	    List<Inflation> rst = session.createQuery(sql, Inflation.class)
	    							.setParameter("stTime",FinUtils.addMonth(bssd.substring(0,6), monNum))
	    							.setParameter("endTime",bssd.substring(0,6))
	    							.getResultList();
		return rst;
	}
}
