package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.SwaptionVol;
import com.gof.util.FinUtils;
import com.gof.util.HibernateUtil;


/**
 *  <p> Swaption Vol �� �̷�  ������ ������.
 *  <p> 
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
public class SwaptionVolDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	
	/** 
	*  <p> ���س�� ������ ���� Swpation ������ ����  
	*  @param bssd 	   ���س��
	*  @param	monthNum ���س�� ���� ����
	*  @return		   ������ �̷�                 
	*/ 
	public static List<SwaptionVol> getPrecedingSwaptionVol(String bssd, int monthNum){
		String q = " select a from SwaptionVol a where a.baseYymm between :stDate and :endDate ";
		
		return session.createQuery(q, SwaptionVol.class)
						.setParameter("stDate", FinUtils.addMonth(bssd, monthNum))
						.setParameter("endDate", bssd)
						.list()
						;
	}
	
	public static List<SwaptionVol> getSwaptionVol(String bssd){
		String q = " select a from SwaptionVol a " 
				+ "where a.baseYymm = :endDate "
				+ "order by a.swapTenor, a.swaptionMaturity "
				;
		return session.createQuery(q, SwaptionVol.class)
						.setParameter("endDate", bssd)
						.list()
						;
	}
	
}
