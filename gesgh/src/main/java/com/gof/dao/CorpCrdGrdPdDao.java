package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.CorpCrdGrdPd;
import com.gof.entity.CorpCumPd;
import com.gof.util.FinUtils;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> DataBase �� ���� ��� �ſ��� {@link CorpCrdGrdPd} ������ �����ϴ� ����� ������. 
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class CorpCrdGrdPdDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	private static String baseQuery = "select a from CorpCrdGrdPd a where 1=1 ";

	public static List<CorpCrdGrdPd> getEntities() {
		return session.createQuery(baseQuery, CorpCrdGrdPd.class).getResultList();
	}
	/** 
	*  
	*  @param bssd 	   ���س��
	*  @param monNum  ���س�� ���� ���� ����	
	*  @return        ��� �ſ��޺� �ε���    
	*/ 
	public static List<CorpCrdGrdPd> getPrecedingCorpPd(String bssd, int monNum) {
		 String sql = "select a from CorpCrdGrdPd a "
	    			+ "where a.crdEvalAgncyCd = :param "
	    			+ "and a.baseYymm <  :time "
	    			+ "and a.baseYymm >= :stTime "
	    			
	    			;
	    List<CorpCrdGrdPd> rst = session.createQuery(sql, CorpCrdGrdPd.class)
	    							.setParameter("param", "01")
	    							.setParameter("time",bssd.substring(0,6))
	    							.setParameter("stTime",FinUtils.addMonth(bssd,  monNum))
	    							.getResultList();

	    log.debug("test : {}", bssd);
		return rst;
	}

	public static List<CorpCrdGrdPd> getCorpPd(String bssd) {
		 String sql = "select a from CorpCrdGrdPd a "
	    			+ "where a.crdEvalAgncyCd = :param "
	    			+ "and a.baseYymm =  :bssd"
	    			;
	    List<CorpCrdGrdPd> rst = session.createQuery(sql, CorpCrdGrdPd.class)
	    							.setParameter("param", "01")
	    							.setParameter("bssd",bssd)
	    							.getResultList();
	    
		return rst;
	}
	
	
	public static List<CorpCumPd> getCorpCumPd(String bssd) {
		 String sql = "select a from CorpCumPd a "
	    			+ "where a.agencyCode = :param "
	    			+ "and a.baseYymm =  :bssd "
	    			;
		 
	    List<CorpCumPd> rst = session.createQuery(sql,CorpCumPd.class)
	    							.setParameter("param", "01")
	    							.setParameter("bssd",bssd)
	    							.getResultList();
	    
		return rst;
	}
}
