package com.gof.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import com.gof.entity.SegLgd;
import com.gof.entity.SegLgdUd;
import com.gof.util.FinUtils;
import com.gof.util.HibernateUtil;

/**
 *  <p> LGD ���׸�Ʈ ���� ����
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
public class SegLgdDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	private static String baseQuery = "select a from SegLgd a where 1=1 ";

	public static List<SegLgd> getEntities() {
		return session.createQuery(baseQuery, SegLgd.class).getResultList();
	}
	public static List<SegLgdUd> getSegLgdUd(String bssd) {
		String maxBaseYymm =  "select max(a.applStYymm) "
							+ "from SegLgdUd a "
							+ "where 1=1 "
							+ "and a.applStYymm <= :time "
							+ "and a.applEdYymm >= :time "
							;
		
		String maxYymm= session.createQuery(maxBaseYymm, String.class)
								.setParameter("time",bssd.substring(0,6))
								.uniqueResult();

		
		String query = "select a from SegLgdUd a "
	    			+  "where 1=1 "
	    			+  "and a.applStYymm = :maxTime "
	    			;
		
		if(maxYymm == null) {
			return new ArrayList<SegLgdUd>() ;
		}
		else {
			List<SegLgdUd> rst = session.createQuery(query, SegLgdUd.class)
					.setParameter("maxTime",maxYymm)
					.getResultList();
			return rst;
		}

	}

	public static List<SegLgd> getSegLgd(String bssd) {
		 return getSegLgd(bssd, "07");
	}

	/** 
	*  <p> ������ ������ ���� 36����ġ LGD ���׸�Ʈ ���� ����
	*  @param bssd 	   ���س��
	*  @return		   LGD ���׸�Ʈ ���� �̷�                 
	*/
	public static List<SegLgd> getSegLgdLessThan(String bssd) {
		 return getSegLgdLessThan(bssd, "07");
	}

	public static List<SegLgd> getSegLgd(String bssd, String lgdCaclType) {
		 String sql = "select a from SegLgd a "
	    			+ "where a.baseYymm  =  :time "
	    			+ "and a.lgdCalcTypCd = :param "
	    			;
		 
	    List<SegLgd> rst = session.createQuery(sql, SegLgd.class)
	    							.setParameter("param", lgdCaclType)
	    							.setParameter("time",bssd.substring(0,6))
	    							.getResultList();
		return rst;
	}
	
	/** 
	*  <p> ���� 36����ġ LGD ���׸�Ʈ ���� ����
	*  @param bssd 	   ���س��
	*  @param lgdCaclType	LGD ���� ����
	*  @return		   LGD ���׸�Ʈ ���� �̷�                 
	*/ 
	public static List<SegLgd> getSegLgdLessThan(String bssd, String lgdCaclType) {
		 String sql = "select a from SegLgd a "
	    			+ "where a.lgdCalcTypCd = :param "
	    			+ "and a.baseYymm  >=  :stTime "
	    			+ "and a.baseYymm  <   :endTime "
	    			;
		 
	    List<SegLgd> rst = session.createQuery(sql, SegLgd.class)
	    							.setParameter("param", lgdCaclType)
	    							.setParameter("stTime",FinUtils.addMonth(bssd.substring(0,6), -36))
	    							.setParameter("endTime",bssd.substring(0,6))
	    							.getResultList();
		return rst;
	}
}
