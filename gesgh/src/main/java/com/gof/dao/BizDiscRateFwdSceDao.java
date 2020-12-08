package com.gof.dao;

import java.util.List;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.entity.BizDiscRateFwdSce;
import com.gof.entity.BottomupDcnt;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> BottomUp ������ ������{@link BottomupDcnt} �� DataBase ���� �����ϴ� ����� �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class BizDiscRateFwdSceDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	
	public static List<BizDiscRateFwdSce> getForwardRatesByMat(String bssd, String bizDv, String irCurveId, String matCd, String sceNo){
		StringBuilder builder = new StringBuilder();
		builder.append("select a from BizDiscRateFwdSce a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.applyBizDv = :bizDv "
				+ "and a.irCurveId = :irCurveId "
				+ "and a.matCd  = :matCd "
				+ "and a.sceNo = :sceNo "
				
				)
		;
		
		Query<BizDiscRateFwdSce> q = session.createQuery(builder.toString(), BizDiscRateFwdSce.class);
		q.setParameter("bssd", bssd);
		q.setParameter("bizDv", bizDv);
		q.setParameter("irCurveId", irCurveId);
		q.setParameter("matCd", matCd);
		q.setParameter("sceNo", sceNo);
		;
		
		return q.getResultList();
	}
	
	public static List<BizDiscRateFwdSce> getForwardRates(String bssd, String bizDv, String irCurveId, String sceNo){
		StringBuilder builder = new StringBuilder();
		builder.append("select a from BizDiscRateFwdSce a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.applyBizDv = :bizDv "
				+ "and a.irCurveId = :irCurveId "
				+ "and a.sceNo = :sceNo "
				
				)
		;
		
		Query<BizDiscRateFwdSce> q = session.createQuery(builder.toString(), BizDiscRateFwdSce.class);
		q.setParameter("bssd", bssd);
		q.setParameter("bizDv", bizDv);
		q.setParameter("irCurveId", irCurveId);
		q.setParameter("sceNo", sceNo);
		;
		
		return q.getResultList();
	}

	public static List<BizDiscRateFwdSce> getForwardRatesPart(String bssd, String bizDv, String irCurveId, String sceNo ){
		StringBuilder builder = new StringBuilder();
		builder.append("select a from BizDiscRateFwdSce a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.applyBizDv = :bizDv "
				+ "and a.irCurveId = :irCurveId "
				+ "and a.sceNo <= :sceNo "
				
				)
		;
		
		Query<BizDiscRateFwdSce> q = session.createQuery(builder.toString(), BizDiscRateFwdSce.class);
		q.setParameter("bssd", bssd);
		q.setParameter("bizDv", bizDv);
		q.setParameter("irCurveId", irCurveId);
		q.setParameter("sceNo", sceNo);
		;
		
		log.debug("test : {}", bssd);
		
		return q.getResultList();
	}
	
	
	public static Stream<BizDiscRateFwdSce> getForwardRatesSce(String bssd, String bizDv, String irCurveId){
		
		String query = "select a from BizDiscRateFwdSce a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.applyBizDv = :bizDv "
				+ "and a.irCurveId = :irCurveId "
//				+ "and a.sceNo <= :sceNo "
				;

		Query<BizDiscRateFwdSce> q = session.createQuery(query, BizDiscRateFwdSce.class);
		q.setParameter("bssd", bssd);
		q.setParameter("bizDv", bizDv);
		q.setParameter("irCurveId", irCurveId);
//		q.setParameter("sceNo", "10");
		;
		
		return q.stream();
	}
}
