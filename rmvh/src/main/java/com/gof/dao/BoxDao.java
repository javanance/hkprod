package com.gof.dao;

import java.util.List;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.entity.RstBoxDetail;
import com.gof.entity.RstBoxGoc;
import com.gof.infra.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p>
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class BoxDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	//	public static List<RstBoxDetail> getRstBoxDetail(String bssd  ){
//		String query = "select a from RstBoxDetail a "
//				+ "where a.baseYymm  = :bssd "
//				;
//		
//		Query<RstBoxDetail> q = session.createQuery(query, RstBoxDetail.class);
//		q.setParameter("bssd", bssd);
//		return q.getResultList();
//	}
	
	public static Stream<RstBoxGoc> getRstBoxGoc(String bssd){
		String query = "select a from RstBoxGoc a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RstBoxGoc> q = session.createQuery(query, RstBoxGoc.class);
		q.setParameter("bssd", bssd);
		return q.stream();
	}
	
	public static Stream<RstBoxGoc> getRstBoxGoc(String bssd, String gocId  ){
		if(gocId==null) {
			return getRstBoxGoc(bssd);
		}
		String query = "select a from RstBoxGoc a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId"
				;
		
		Query<RstBoxGoc> q = session.createQuery(query, RstBoxGoc.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		return q.stream();
	}

	public static List<RstBoxGoc> getRstBoxGroupBy(String bssd ){
		String query = "select new com.gof.entity.RstBoxGoc(a.baseYymm, a.gocId, a.liabType, a.mstRunset, a.calcId, a.stStatus, a.endStatus, a.newContYn"
				+ ", sum(a.cfAmt), sum(a.prevCfAmt), sum(a.deltaCfAmt), sum(a.boxValue)) "
				+ "from RstBoxDetail a "
				+ "where a.baseYymm  = :bssd "
				+ "group by a.baseYymm, a.gocId, a.liabType, a.mstRunset, a.calcId, a.stStatus, a.endStatus, a.newContYn"
				;
		
		Query<RstBoxGoc> q = session.createQuery(query, RstBoxGoc.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}

	public static List<RstBoxGoc> getRstBoxGroupBy(String bssd, String gocId ){
		if(gocId==null) {
			return getRstBoxGroupBy(bssd);
		}
		String query = "select new com.gof.entity.RstBoxGoc(a.baseYymm, a.gocId, a.liabType, a.mstRunset, a.calcId, a.stStatus, a.endStatus, a.newContYn"
				+ ", sum(a.cfAmt), sum(a.prevCfAmt), sum(a.deltaCfAmt), sum(a.boxValue)) "
				+ "from RstBoxDetail a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId "
				+ "group by a.baseYymm, a.gocId, a.liabType, a.mstRunset, a.calcId, a.stStatus, a.endStatus, a.newContYn"
				;
		
		Query<RstBoxGoc> q = session.createQuery(query, RstBoxGoc.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		return q.getResultList();
	}
	
//	TODO : Check LRC, LIC
	public static Stream<RstBoxGoc> getRstBoxGocGroup(String bssd  ){
//		String query = "select new com.gof.entity.RstBoxGoc(a.baseYymm, a.gocId, a.liabType, a.mstRunset, a.calcId, sum(a.boxValue)) "
//				+ "from RstBoxGoc a "
//				+ "where a.baseYymm  = :bssd "
//				+ "group by a.baseYymm, a.gocId, a.liabType, a.mstRunset, a.calcId"
//				;
		
		String query = "select new com.gof.entity.RstBoxGoc(a.baseYymm, a.gocId, a.mstRunset, a.calcId, sum(a.boxValue)) "
				+ "from RstBoxGoc a "
				+ "where a.baseYymm  = :bssd "
				+ "group by a.baseYymm, a.gocId,  a.mstRunset, a.calcId"
				;
		
		Query<RstBoxGoc> q = session.createQuery(query, RstBoxGoc.class);
		q.setParameter("bssd", bssd);
		return q.stream();
	}
	
	public static Stream<RstBoxGoc> getRstBoxGocGroup(String bssd, String gocId  ){
		if(gocId ==null) {
			return getRstBoxGocGroup(bssd);
		}
//		String query = "select new com.gof.entity.RstBoxGoc(a.baseYymm, a.gocId, a.mstRunset, a.calcId, sum(a.boxValue)) "
//				+ "from RstBoxGoc a "
//				+ "where a.baseYymm  = :bssd "
//				+ "and a.gocId = :gocId "
//				+ "group by a.baseYymm, a.gocId, a.mstRunset, a.calcId"
//				;
		
		String query = "select new com.gof.entity.RstBoxGoc(a.baseYymm, a.gocId, a.liabType, a.mstRunset, a.calcId, sum(a.boxValue)) "
				+ "from RstBoxGoc a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId "
				+ "group by a.baseYymm, a.gocId, a.liabType, a.mstRunset, a.calcId"
				;
		
		
		Query<RstBoxGoc> q = session.createQuery(query, RstBoxGoc.class);
		
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		return q.stream();
	}
	
	public static Stream<RstBoxDetail> getRstBoxDetailStream(String bssd  ){
		String query = "select a from RstBoxDetail a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RstBoxDetail> q = session.createQuery(query, RstBoxDetail.class);
		q.setParameter("bssd", bssd);
		
		return q.stream();
	}
	
	public static Stream<RstBoxDetail> getRstBoxDetailStream(String bssd , String gocId ){
		if(gocId==null) {
			return getRstBoxDetailStream(bssd);
		}
		String query = "select a from RstBoxDetail a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId"
				;
		
		Query<RstBoxDetail> q = session.createQuery(query, RstBoxDetail.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		return q.stream();
	}
}
