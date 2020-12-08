package com.gof.dao;

import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.entity.NcontRstEpv;
import com.gof.entity.NcontRstFlat;
import com.gof.entity.NcontRstRa;
import com.gof.entity.NcontRstTvog;
import com.gof.entity.RawFv;
import com.gof.enums.EBoolean;
import com.gof.infra.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class NcontDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static Stream<NcontRstEpv> getNcontRstEpvFromGroupBy(String bssd){
		String query = "select new com.gof.entity.NcontRstEpv(a.baseYymm, a.ctrPolno, max(a.prodCd),sum(a.cfAmt), sum(a.epvAmt)) "
				+ "	from NcontCf a "
				+ " where a.baseYymm  = :bssd "
				+ " group by a.baseYymm, a.ctrPolno"
				;
		
		Query<NcontRstEpv> q = session.createQuery(query, NcontRstEpv.class)
									.setParameter("bssd", bssd)
									;
		return q.stream();
	}
	
	public static Stream<NcontRstEpv> getNcontOutEpvFromGroupBy(String bssd){
		String query = "select com.gof.entity.NcontRstEpv(a.baseYymm, a.ctrPolno, max(a.prodCd),sum(a.cfAmt), sum(a.epvAmt)) "
				+ "	from NcontCf a "
				+ " where a.baseYymm  = :bssd "
				+ " and a.outflowYn =:outflowYn"
				+ " group by a.baseYymm, a.ctrPolno"
				;
		
		Query<NcontRstEpv> q = session.createQuery(query, NcontRstEpv.class)
									.setParameter("bssd", bssd)
									.setParameter("outflowYn", EBoolean.Y)
									;
		return q.stream();
	}
	
	public static Stream<NcontRstEpv> getNcontEpv(String bssd){
		String query = "select a from NcontRstEpv a "
				+ "where a.baseYymm  = :bssd "
		;
		
		Query<NcontRstEpv> q = session.createQuery(query, NcontRstEpv.class)
									.setParameter("bssd", bssd);
				
		return q.stream();
	}
	
	public static Stream<NcontRstRa> getNcontRa(String bssd){
		String query = "select a from NcontRstRa a "
				+ "where a.baseYymm  = :bssd "
		;
		
		Query<NcontRstRa> q = session.createQuery(query, NcontRstRa.class)
									.setParameter("bssd", bssd);
				
		return q.stream();
	}
	
	public static Stream<NcontRstTvog> getNcontTvog(String bssd){
		String query = "select a from NcontRstTvog a "
				+ "where a.baseYymm  = :bssd "
		;
		
		Query<NcontRstTvog> q = session.createQuery(query, NcontRstTvog.class)
									.setParameter("bssd", bssd);
				
		return q.stream();
	}
	
	public static Stream<NcontRstFlat> getNcontRstFlat(String bssd){
		String query = "select a from NcontRstFlat a "
				+ "where a.baseYymm  = :bssd "
		;
		
		Query<NcontRstFlat> q = session.createQuery(query, NcontRstFlat.class)
									.setParameter("bssd", bssd);
				
		return q.stream();
	}
	
	public static Stream<RawFv> getRawFv(String bssd  ){
		String query = "select a from RawFv a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RawFv> q = session.createQuery(query, RawFv.class);
		q.setParameter("bssd", bssd);
		return q.stream();
	}
	
	public static Stream<RawFv> getRawFvGroupBy(String bssd  ){
		String query = "select new com.gof.entity.RawFv(a.baseYymm, a.ctrPolno, sum(epvAmt)) "
				+ " from RawFv a "
				+ " where a.baseYymm  = :bssd "
				+ " group by a.baseYymm, a.ctrPolno"
				;
		
		Query<RawFv> q = session.createQuery(query, RawFv.class);
		q.setParameter("bssd", bssd);
		return q.stream();
	}
}
