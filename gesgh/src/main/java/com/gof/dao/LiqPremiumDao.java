package com.gof.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.entity.BizLiqPremium;
import com.gof.entity.BizLiqPremiumUd;
import com.gof.entity.BottomupDcnt;
import com.gof.entity.LiqPremium;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> BottomUp ������ ������{@link BottomupDcnt} �� DataBase ���� �����ϴ� ����� �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class LiqPremiumDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	private static String  getApplyDateLiqPremiumUd(String bssd, String bizDv){
		String maxQuery = "select max(a.applyStartYymm) from BizLiqPremiumUd a "
				+ "		where 1=1"
				+ "		and a.applyBizDv = :bizDv"
				+ "		and a.applyStartYymm <= :bssd"
				+ "		and a.applyEndYymm >=  :bssd"
		;
		
		Query<String> q = session.createQuery(maxQuery, String.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("bizDv", bizDv);
		;
		
		log.info("Applied Date for User Input Liq premium : {}", q.getSingleResult());
		return q.getSingleResult();
	}
	
	public static List<BizLiqPremiumUd> getLiqPremiumUd(String bssd){
		
		String query = "select a from BizLiqPremiumUd a "
				+ "		where 1=1"
				+ "		and a.applyBizDv = :bizDv"
				+ "		and a.applyStartYymm = :stDate"
		;
		
		Query<BizLiqPremiumUd> q = session.createQuery(query, BizLiqPremiumUd.class);
		
		q.setParameter("stDate", getApplyDateLiqPremiumUd(bssd, "I"));
		q.setParameter("bizDv", "I");
		
		
		return q.getResultList();
	}
	
	public static List<BizLiqPremiumUd> getLiqPremiumUd(String bssd,String bizDv){
		
		String query = "select a from BizLiqPremiumUd a "
				+ "		where 1=1"
				+ "		and a.applyBizDv = :bizDv"
				+ "		and a.applyStartYymm = :stDate"
		;
		
		Query<BizLiqPremiumUd> q = session.createQuery(query, BizLiqPremiumUd.class);
		
		q.setParameter("stDate", getApplyDateLiqPremiumUd(bssd, bizDv));
		q.setParameter("bizDv", bizDv);
		
		
		return q.getResultList();
	}

	public static List<LiqPremium> getLiqPremium(String bssd, String modelId, String order){
		
		String query = "select a from LiqPremium a "
				+ "where a.modelId = :modelId "
				+ "and a.baseYymm = :bssd "
				+ "order by a.matCd :desc"
		;
		
		Query<LiqPremium> q = session.createQuery(query, LiqPremium.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("modelId", modelId);
		q.setParameter("desc", order);
		
		return q.getResultList();
	}
	
	public static List<LiqPremium> getLiqPremium(String bssd, String modelId){
		
		String query = "select a from LiqPremium a "
				+ "where a.modelId = :modelId "
				+ "and a.baseYymm = :bssd "
				
		;
		
		Query<LiqPremium> q = session.createQuery(query, LiqPremium.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("modelId", modelId);
		
		return q.getResultList();
	}

	public static List<BizLiqPremium> getBizLiqPremium(String bssd){
		
		String query = "select a from BizLiqPremium a "
					+  "where a.applyBizDv = :bizDv "
					+  "and a.baseYymm = :bssd"
					;
		
		Query<BizLiqPremium> q = session.createQuery(query, BizLiqPremium.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("bizDv", "I");
		
		return q.getResultList();
	}
	
	public static List<BizLiqPremium> getBizLiqPremium(String bssd, String bizDv){
		
		String query = "select a from BizLiqPremium a "
					+  "where a.applyBizDv = :bizDv "
					+  "and a.baseYymm = :bssd"
					;
		
		Query<BizLiqPremium> q = session.createQuery(query, BizLiqPremium.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("bizDv", bizDv);
		
		return q.getResultList();
	}
}
