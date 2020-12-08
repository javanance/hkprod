package com.gof.dao;

import java.util.List;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.jpa.QueryHints;

import com.gof.entity.BizIrCurveSce;
import com.gof.entity.IrSce;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;


/**
 *  <p> �ݸ� �Ⱓ������ �����ϴ� �ݸ��̷�  ������ ������.
 *  <p> 
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class IrCurveSceDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	public static List<IrSce> getIrCurveSce(String bssd, String irCurveId, String sceNo){
		String query ="select a from IrSce a " 
					+ "where a.irCurveId =:irCurveId "			
					+ "and a.baseDate = :bssd "
					+ "and a.sceNo = :sceNo "
				;
		
		return session.createQuery(query, IrSce.class)
				.setParameter("irCurveId", irCurveId)
				.setParameter("bssd", bssd)
				.setParameter("sceNo", sceNo)
				.getResultList()
				;
	}

	public static List<IrSce> getIrCurveSce(String bssd, String irCurveId){
		String query ="select a from IrSce a " 
					+ "where a.irCurveId =:irCurveId "			
					+ "and a.baseDate = :bssd "
				;
		
		return session.createQuery(query, IrSce.class)
				.setParameter("irCurveId", irCurveId)
				.setParameter("bssd", bssd)
				.setHint(QueryHints.HINT_READONLY, true)        //TODO : Check performacen!!!!
				.getResultList()
				;
	}

	public static List<BizIrCurveSce> getBizIrCurveSce(String bssd, String bizDv, String irCurveId, String sceNo){
		String query ="select a from BizIrCurveSce a " 
					+ "where a.irCurveId =:irCurveId "		
					+ "and a.baseYymm = :bssd "
					+ "and a.applBizDv = :bizDv "
					+ "and a.sceNo = :sceNo "
				;
		
		return session.createQuery(query, BizIrCurveSce.class)
				.setParameter("irCurveId", irCurveId)
				.setParameter("bssd", bssd)
				.setParameter("bizDv", bizDv)
				.setParameter("sceNo", sceNo)
				.getResultList()
				;
	}
	
	

	public static List<BizIrCurveSce> getBizIrCurveSce(String bssd, String bizDv, String irCurveId){
		String query ="select a from BizIrCurveSce a " 
					+ "where 1=1 "
					+ "and a.baseYymm = :bssd "
					+ "and a.applBizDv = :bizDv "
					+ "and a.irCurveId =:irCurveId"
				;
		
		return session.createQuery(query, BizIrCurveSce.class)
				.setParameter("bssd", bssd)
				.setParameter("bizDv", bizDv)
				.setParameter("irCurveId", irCurveId)
				.getResultList()
				;
	}
	
	public static Stream<BizIrCurveSce> getBizIrCurveSceStream(String bssd, String bizDv, String irCurveId){
		String query ="select a from BizIrCurveSce a " 
					+ "where 1=1 "
					+ "and a.baseYymm = :bssd "
					+ "and a.applBizDv = :bizDv "
					+ "and a.irCurveId =:irCurveId"
				;
		
		return session.createQuery(query, BizIrCurveSce.class)
				.setParameter("bssd", bssd)
				.setParameter("bizDv", bizDv)
				.setParameter("irCurveId", irCurveId)
				.stream()
				;
	}

	
}
