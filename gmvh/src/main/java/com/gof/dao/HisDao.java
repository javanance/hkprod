package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.HisRsDiv;
import com.gof.infra.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> BottomUp ������ ������{@link BottomupDcnt} �� DataBase ���� �����ϴ� �����? �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class HisDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<HisRsDiv> getHisRsDiv(String bssd){
		String query = "select a from HisRsDiv a where a.baseYymm =:bssd" ;
		return session.createQuery(query, HisRsDiv.class)
						.setParameter("bssd", bssd)
						.getResultList();
	}
	
	
	
	
}
