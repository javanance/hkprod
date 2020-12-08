package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.MapCfGroup;
import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MapRunsetCalc;
import com.gof.infra.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class MapDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<MapCfGroup> getMapCfGroup(){
		String query = "select a from MapCfGroup a "	;
		return session.createQuery(query, MapCfGroup.class).getResultList();
	}
	
	
//	public static List<MapRollFwdRunset> getMapRollFwdRunset(){
//		String query = "select a from MapRollFwdRunset a "	;
//		return session.createQuery(query, MapRollFwdRunset.class).getResultList();
//	}
	
	public static List<MapRunsetCalc> getMapRunsetCalc(){
		String query = "select a from MapRunsetCalc a "	;
		return session.createQuery(query, MapRunsetCalc.class).getResultList();
	}
	
	public static List<MapJournalRollFwd> getMapJournalRollFwd(){
		String query = "select a from MapJournalRollFwd a "	;
		return session.createQuery(query, MapJournalRollFwd.class).getResultList();
	}
}
