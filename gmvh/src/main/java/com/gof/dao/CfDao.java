package com.gof.dao;

import java.util.List;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.entity.CfLv1Goc;
import com.gof.entity.CfLv2Delta;
import com.gof.entity.CfLv3Real;
import com.gof.entity.CfLv4Df;
import com.gof.infra.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *          
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class CfDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static Stream<CfLv1Goc> getCfLv1GocStream(String bssd){
		String query = "select a from CfLv1Goc a "
				+ "where a.baseYymm  = :bssd "
		;
		Query<CfLv1Goc> q = session.createQuery(query, CfLv1Goc.class);
		q.setParameter("bssd", bssd);
		return q.stream();
	}	
	
	public static Stream<CfLv1Goc> getCfLv1GocStream(String bssd, String gocId ){
		if(gocId==null) {
			return getCfLv1GocStream(bssd);
		}
		
		String query = "select a from CfLv1Goc a "
				+ "where a.baseYymm  = :bssd "
				+ " and a.gocId = :gocId"
		;
		
		Query<CfLv1Goc> q = session.createQuery(query, CfLv1Goc.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		
		return q.stream();
	}
	
//	public static Stream<CfLv2Goc> getCfLv2GocGroupByRunsetStream(String bssd, String gocId ){
//		String query = "select a from CfLv2Goc a "
//				+ "where a.baseYymm  = :bssd "
//				+ " and a.gocId = :gocId"
//				+ " group by a.baseYymm, a."
//		;
//		
//		Query<CfLv2Goc> q = session.createQuery(query, CfLv2Goc.class);
//		q.setParameter("bssd", bssd);
//		q.setParameter("gocId", gocId);
//		
//		return q.stream();
//	}
	

	public static List<CfLv1Goc> getCfLv1Goc(String bssd, String gocId){
		String query = "select a from CfLv1Goc a "
				+ "where  a.baseYymm  = :bssd "
				+ "and  a.gocId =:gocId"
			;
		
		Query<CfLv1Goc> q = session.createQuery(query, CfLv1Goc.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
	
		return q.getResultList();
	}

	public static Stream<CfLv1Goc> getCfLv1GocByTenor(String bssd, int maxTenor ){
		String query = "select a from CfLv1Goc a "
				+ "where  a.baseYymm  = :bssd "
				+ "and  a.setlAftPassMmcnt =:maxTenor"
			;
		
		Query<CfLv1Goc> q = session.createQuery(query, CfLv1Goc.class);
		q.setParameter("bssd", bssd);
		q.setParameter("maxTenor", maxTenor);

		return q.stream();
	}
	
	public static Stream<CfLv1Goc> getCfLv1GocByRunsetStream(String bssd, String runsetId){
		String query = "select a from CfLv1Goc a "
				+ "where a.baseYymm  = :bssd "
				+ " and a.runsetId = :runsetId"
				;
		
		Query<CfLv1Goc> q = session.createQuery(query, CfLv1Goc.class);
		q.setParameter("bssd", bssd);
		q.setParameter("runsetId", runsetId);
		
		return q.stream();
	}	
	
	
	public static Stream<CfLv1Goc> getCfLv1GocByRunsetStream(String bssd, String runsetId, String gocId ){
		if(gocId==null) {
			return getCfLv1GocByRunsetStream(bssd, runsetId);
		}
			String query = "select a from CfLv1Goc a "
					+ "where a.baseYymm  = :bssd "
					+ " and a.gocId = :gocId"
					+ " and a.runsetId = :runsetId"
	//				+ "and rownum < 10"
			;
			
			Query<CfLv1Goc> q = session.createQuery(query, CfLv1Goc.class);
			q.setParameter("bssd", bssd);
			q.setParameter("runsetId", runsetId);
			q.setParameter("gocId", gocId);
			
			return q.stream();
	}

	public static Stream<CfLv1Goc> getCfLv1GocByRsDivStream(String bssd, String rsDivId){
		String query = "select a from CfLv1Goc a "
				+ "where a.baseYymm  = :bssd "
				+ " and a.rsDivId = :rsDivId"
				;
		
		Query<CfLv1Goc> q = session.createQuery(query, CfLv1Goc.class);
		q.setParameter("bssd", bssd);
		q.setParameter("rsDivId", rsDivId);
		
		return q.stream();
	}	
	
	public static Stream<CfLv1Goc> getCfLv1GocByRsDivStream(String bssd, String rsDivId, String gocId){
		if(gocId==null) {
			return getCfLv1GocByRsDivStream(bssd, rsDivId);
		}
		String query = "select a from CfLv1Goc a "
				+ "where a.baseYymm  = :bssd "
				+ " and a.rsDivId = :rsDivId"
				+ " and a.gocId = :gocId"
				;
		
		Query<CfLv1Goc> q = session.createQuery(query, CfLv1Goc.class);
		q.setParameter("bssd", bssd);
		q.setParameter("rsDivId", rsDivId);
		q.setParameter("gocId", gocId);
		
		return q.stream();
	}
	
	public static Stream<CfLv1Goc> getCfLv1GocByDeltaGroupStream(String bssd, String deltaGroup){
		String query = "select a from CfLv1Goc a "
				+ "where a.baseYymm  = :bssd "
				+ " and a.deltaGroup = :deltaGroup"
				;
		
		Query<CfLv1Goc> q = session.createQuery(query, CfLv1Goc.class);
		q.setParameter("bssd", bssd);
		q.setParameter("deltaGroup", deltaGroup);
		
		return q.stream();
	}	
	

	public static Stream<CfLv1Goc> getCfLv1GocByDeltaGroupStream(String bssd, String deltaGroup, String gocId){
		if(gocId==null) {
			return getCfLv1GocByDeltaGroupStream(bssd, deltaGroup);
		}
		String query = "select a from CfLv1Goc a "
				+ "where a.baseYymm  = :bssd "
				+ " and a.deltaGroup = :deltaGroup"
				+ " and a.gocId = :gocId"
				;
		
		Query<CfLv1Goc> q = session.createQuery(query, CfLv1Goc.class);
		q.setParameter("bssd", bssd);
		q.setParameter("deltaGroup", deltaGroup);
		q.setParameter("gocId", gocId);
		
		return q.stream();
	}	
	
	public static Stream<CfLv1Goc> getCfLv1GocSettleStream(String bssd){
		return getCfLv1GocSettleStream(bssd, null);
	}
	
//	TODO :deprecated !!!!
	public static Stream<CfLv1Goc> getCfLv1GocSettleStream(String bssd, String gocId){
		if(gocId==null) {
			return getCfLv1GocSettleStream(bssd);
		}
		String query = "select a from CfLv1Goc a "
				+ "where a.baseYymm  = :bssd "
				+ " and a.deltaGroup like :settle"		//TODO :!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				+ " and a.gocId = :gocId"
				;
		
		Query<CfLv1Goc> q = session.createQuery(query, CfLv1Goc.class);
		q.setParameter("bssd", bssd);
		q.setParameter("settle","%SETTLE");
		q.setParameter("gocId", gocId);
		
		return q.stream();
	}	
	
	public static Stream<CfLv1Goc> getCfLv1GocNewContStream(String bssd){
		return getCfLv1GocSettleStream(bssd, null);
	}
	
	public static Stream<CfLv1Goc> getCfLv1GocNewContStream(String bssd, String gocId){
		if(gocId==null) {
			return getCfLv1GocSettleStream(bssd);
		}
		String query = "select a from CfLv1Goc a "
				+ "where a.baseYymm  = :bssd "
				+ " and a.deltaGroup like :newCont"						//TODO :!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				+ " and a.gocId = :gocId"
				;
		
		Query<CfLv1Goc> q = session.createQuery(query, CfLv1Goc.class);
		q.setParameter("bssd", bssd);
		q.setParameter("newCont","%NEW");
		q.setParameter("gocId", gocId);
		
		return q.stream();
	}	
	
	public static Stream<CfLv2Delta> getCfLv2DeltaStream(String bssd ){
		String query = "select a from CfLv2Delta a "
				+ "where a.baseYymm  = :bssd "
		;
		
		Query<CfLv2Delta> q = session.createQuery(query, CfLv2Delta.class);
		q.setParameter("bssd", bssd);
		
		return q.stream();
	}
	
	public static Stream<CfLv2Delta> getCfLv2DeltaStream(String bssd, String gocId ){
		if(gocId==null) {
			return getCfLv2DeltaStream(bssd);
		}
		String query = "select a from CfLv2Delta a "
				+ "where a.baseYymm  = :bssd "
				+ " and a.gocId =:gocId"
		;
		
		Query<CfLv2Delta> q = session.createQuery(query, CfLv2Delta.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		
		return q.stream();
	}
	
	public static Stream<CfLv2Delta> getCfLv2DeltaByRunsetStream(String bssd, String runsetId ){
		String query = "select a from CfLv2Delta a "
				+ " where a.baseYymm  = :bssd "
				+ " and a.runsetId = :runsetId"
		;
		
		Query<CfLv2Delta> q = session.createQuery(query, CfLv2Delta.class);
		q.setParameter("bssd", bssd);
		q.setParameter("runsetId", runsetId);
		
		return q.stream();
	}
	
	public static Stream<CfLv2Delta> getCfLv2DeltaByRunsetStream(String bssd, String runsetId, String gocId ){
		String query = "select a from CfLv2Delta a "
				+ " where a.baseYymm  = :bssd "
				+ " and a.runsetId = :runsetId "
				+ " and a.gocId= :gocId"
		;
		
		Query<CfLv2Delta> q = session.createQuery(query, CfLv2Delta.class);
		q.setParameter("bssd", bssd);
		q.setParameter("runsetId", runsetId);
		q.setParameter("gocId", gocId);
		
		return q.stream();
	}
	
	
	public static Stream<CfLv3Real> getCfLv3RealStream(String bssd){
		String query = "select a from CfLv3Real a "
				+ " where a.baseYymm  = :bssd "
		
		;
		
		Query<CfLv3Real> q = session.createQuery(query, CfLv3Real.class);
		q.setParameter("bssd", bssd);
		
		return q.stream();
	}

	public static Stream<CfLv3Real> getCfLv3RealStream(String bssd, String gocId){
		if(gocId==null) {
			return getCfLv3RealStream(bssd);
		}
		String query = "select a from CfLv3Real a "
				+ " where a.baseYymm  = :bssd "
				+ " and a.gocId = :gocId"
		;
		
		Query<CfLv3Real> q = session.createQuery(query, CfLv3Real.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		
		return q.stream();
	}

	public static Stream<CfLv3Real> getCfLv3RealGroupByStream(String bssd){
		String query = "select new com.gof.entity.CfLv3Real(a.baseYymm, a.gocId,  a.runsetId, a.liabType,  a.stStatus, a.endStatus, a.newContYn, a.cfKeyId, a.cfType, sum(a.cfAmt)) "
				+ " from CfLv3Real a "
				+ " where a.baseYymm  = :bssd "
				+ " group by a.baseYymm, a.gocId,  a.runsetId, a.liabType, a.stStatus, a.endStatus, a.newContYn, a.cfKeyId, a.cfType"
		;
		
		Query<CfLv3Real> q = session.createQuery(query, CfLv3Real.class);
		q.setParameter("bssd", bssd);
		
		return q.stream();
	}
	
	
	public static Stream<CfLv3Real> getCfLv3RealGroupByStream(String bssd, String gocId){
		if(gocId==null) {
			return getCfLv3RealGroupByStream(bssd);
		}
		String query = "select new com.gof.entity.CfLv3Real(a.baseYymm, a.gocId, a.runsetId, a.liabType,  a.stStatus, a.endStatus, a.newContYn, a.cfKeyId, a.cfType, sum(a.cfAmt)) "
				+ " from CfLv3Real a "
				+ " where a.baseYymm  = :bssd "
				+ " and a.gocId  = :gocId "
				+ " group by a.baseYymm, a.gocId, a.runsetId, a.liabType, a.stStatus, a.endStatus, a.newContYn, a.cfKeyId, a.cfType"
		;
		
		Query<CfLv3Real> q = session.createQuery(query, CfLv3Real.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		
		return q.stream();
	}
	
	
//	public static Stream<CfLv4DeltaDf> getCfLv4DeltaDfStream(String bssd){
//		String query = "select a from CfLv4DeltaDf a "
//				+ " where a.baseYymm  = :bssd "
//		;
//		
//		Query<CfLv4DeltaDf> q = session.createQuery(query, CfLv4DeltaDf.class);
//		q.setParameter("bssd", bssd);
//		
//		
//		return q.stream();
//	}
//	
//	public static Stream<CfLv4DeltaDf> getCfLv4DeltaDfStream(String bssd, String gocId){
//		String query = "select a from CfLv4DeltaDf a "
//				+ " where a.baseYymm  = :bssd "
//				+ " and a.gocId = :gocId"
//		;
//		
//		Query<CfLv4DeltaDf> q = session.createQuery(query, CfLv4DeltaDf.class);
//		q.setParameter("bssd", bssd);
//		q.setParameter("gocId", gocId);
//		
//		return q.stream();
//	}
//	public static Stream<CfLv4DeltaDf> getCfLv4DeltaDfByRunsetStream(String bssd, String runsetId ){
//		String query = "select a from CfLv4DeltaDf a "
//				+ " where a.baseYymm  = :bssd "
//				+ " and a.runsetId = :runsetId"
//		;
//		
//		Query<CfLv4DeltaDf> q = session.createQuery(query, CfLv4DeltaDf.class);
//		q.setParameter("bssd", bssd);
//		q.setParameter("runsetId", runsetId);
//		
//		return q.stream();
//	}
	
	public static Stream<CfLv4Df> getCfLv4DfStream(String bssd){
		String query = "select a from CfLv4Df a "
				+ " where a.baseYymm  = :bssd "
		;
		
		Query<CfLv4Df> q = session.createQuery(query, CfLv4Df.class);
		q.setParameter("bssd", bssd);
		
		return q.stream();
	}
	
	public static Stream<CfLv4Df> getCfLv4DfStream(String bssd, String gocId){
		if(gocId==null) {
			return getCfLv4DfStream(bssd);
		}
		String query = "select a from CfLv4Df a "
				+ " where a.baseYymm  = :bssd "
				+ " and a.gocId = :gocId"
		;
		
		Query<CfLv4Df> q = session.createQuery(query, CfLv4Df.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		
		return q.stream();
	}
	
	public static Stream<CfLv4Df> getCfLv4DfByRunsetStream(String bssd, String runsetId ){
		String query = "select a from CfLv4Df a "
				+ " where a.baseYymm  = :bssd "
				+ " and a.runsetId = :runsetId"
		;
		
		Query<CfLv4Df> q = session.createQuery(query, CfLv4Df.class);
		q.setParameter("bssd", bssd);
		q.setParameter("runsetId", runsetId);
		
		return q.stream();
	}
	
	public static Stream<CfLv4Df> getCfLv4DfByRunsetStream(String bssd, String runsetId, String gocId ){
		if(gocId==null) {
			return getCfLv4DfByRunsetStream(bssd, runsetId);
		}
		String query = "select a from CfLv4Df a "
				+ " where a.baseYymm  = :bssd "
				+ " and a.runsetId = :runsetId "
				+ " and a.gocId = :gocId"
		;
		
		Query<CfLv4Df> q = session.createQuery(query, CfLv4Df.class);
		q.setParameter("bssd", bssd);
		q.setParameter("runsetId", runsetId);
		q.setParameter("gocId", gocId);
		
		return q.stream();
	}
	
	public static Stream<CfLv4Df> getCfLv4DfByDeltaGroupStream(String bssd, String deltaGroup ){
		String query = "select a from CfLv4Df a "
				+ " where a.baseYymm  = :bssd "
				+ " and a.deltaGroup = :deltaGroup"
		;
		
		Query<CfLv4Df> q = session.createQuery(query, CfLv4Df.class);
		q.setParameter("bssd", bssd);
		q.setParameter("deltaGroup", deltaGroup);
		
		return q.stream();
	}
	
	public static Stream<CfLv4Df> getCfLv4DfByDeltaGroupStream(String bssd, String deltaGroup, String gocId ){
		if(gocId ==null) {
			return getCfLv4DfByDeltaGroupStream(bssd, deltaGroup);
		}
		String query = "select a from CfLv4Df a "
				+ " where a.baseYymm  = :bssd "
				+ " and a.deltaGroup = :deltaGroup"
				+ " and a.gocId = :gocId"
		;
		
		Query<CfLv4Df> q = session.createQuery(query, CfLv4Df.class);
		q.setParameter("bssd", bssd);
		q.setParameter("deltaGroup", deltaGroup);
		q.setParameter("gocId", gocId);
		
		return q.stream();
	}
}
