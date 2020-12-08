package com.gof.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.entity.FvFlat;
import com.gof.entity.RstCsm;
import com.gof.entity.RstDac;
import com.gof.entity.RstEpv;
import com.gof.entity.RstEpvNgoc;
import com.gof.entity.RstLoss;
import com.gof.entity.RstLossStep;
import com.gof.entity.RstRollFwd;
import com.gof.entity.RstRollFwdLoss;
import com.gof.enums.EBoolean;
import com.gof.enums.ECoa;
import com.gof.infra.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> BottomUp ������ ������{@link BottomupDcnt} �� DataBase ���� �����ϴ� �����? �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class RstDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<FvFlat> getFvFlat(String bssd ){
		String query = "select a from FvFlat a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<FvFlat> q = session.createQuery(query, FvFlat.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
//	public static List<RstRa> getRstRa(String bssd ){
//		String query = "select a from RstRa a "
//				+ "where a.baseYymm  = :bssd "
//				;
//		
//		Query<RstRa> q = session.createQuery(query, RstRa.class);
//		q.setParameter("bssd", bssd);
//		return q.getResultList();
//	}
	
	public static List<RstLossStep> getRstEpvLossStep(String bssd ){
		String query = "select a from RstLossStep a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RstLossStep> q = session.createQuery(query, RstLossStep.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<RstLossStep> getRstEpvLossStep(String bssd, String gocId ){
		if(gocId ==null) {
			return getRstEpvLossStep(bssd);
		}
		String query = "select a from RstLossStep a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId"
				;
		
		Query<RstLossStep> q = session.createQuery(query, RstLossStep.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		return q.getResultList();
	}
	
//	public static List<RstBoxDetail> getRstCalc(String bssd ){
//		String query = "select a from RstCalc a "
//				+ "where a.baseYymm  = :bssd "
//				;
//		
//		Query<RstBoxDetail> q = session.createQuery(query, RstBoxDetail.class);
//		q.setParameter("bssd", bssd);
//		return q.getResultList();
//	}
	
//	public static List<RstBoxDetail> getRstCalc(String bssd, String gocId ){
//		String query = "select a from RstCalc a "
//				+ "where a.baseYymm  = :bssd "
//				+ " and a.gocId = :gocId"
//				;
//		
//		Query<RstBoxDetail> q = session.createQuery(query, RstBoxDetail.class);
//		q.setParameter("bssd", bssd);
//		q.setParameter("gocId", gocId);
//		return q.getResultList();
//	}
	
//	public static List<RstBoxGoc> getRstCalcGroupBy(String bssd ){
//		String query = "select new com.gof.entity.RstBoxGoc(a.baseYymm, a.gocId, a.liabType, a.runsetId, a.calcId, a.stStatus, a.endStatus, a.newContYn"
//				+ ", sum(a.cfAmt), sum(a.prevCfAmt), sum(a.deltaCfAmt), sum(a.boxValue)) "
//				+ "from RstCalc a "
//				+ "where a.baseYymm  = :bssd "
//				+ "group by a.baseYymm, a.gocId, a.liabType, a.runsetId, a.calcId, a.stStatus, a.endStatus, a.newContYn"
//				;
//		
//		
//		Query<RstBoxGoc> q = session.createQuery(query, RstBoxGoc.class);
//		q.setParameter("bssd", bssd);
//		return q.getResultList();
//	}
	
	public static List<RstRollFwd> getRollFwdRst(String bssd){
		String query = "select a from RstRollFwd a "
				+ "where a.baseYymm  = :bssd "
				
				;
		
		Query<RstRollFwd> q = session.createQuery(query, RstRollFwd.class);
		q.setParameter("bssd", bssd);
		
		return q.getResultList();
	}
	
	public static List<RstRollFwdLoss> getRstRollFwdLoss(String bssd){
		String query = "select a from RstRollFwdLoss a "
				+ "where a.baseYymm  = :bssd "
				
				;
		
		Query<RstRollFwdLoss> q = session.createQuery(query, RstRollFwdLoss.class);
		q.setParameter("bssd", bssd);
		
		return q.getResultList();
	}
	
	public static List<RstRollFwd> getRollFwdRst(String bssd , ECoa coa){
		String query = "select a from RstRollFwd a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.coaId = :coa"
				;
		
		Query<RstRollFwd> q = session.createQuery(query, RstRollFwd.class);
		q.setParameter("bssd", bssd);
		q.setParameter("coa", coa);
		return q.getResultList();
	}
	
	public static List<RstRollFwd> getRollFwdRst(String bssd , String gocId){
		if(gocId==null) {
			return getRollFwdRst(bssd);
		}
		String query = "select a from RstRollFwd a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId"
				;
		
		Query<RstRollFwd> q = session.createQuery(query, RstRollFwd.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		return q.getResultList();
	}
//	public static List<RstRaNewgoc> getRstRaNewgoc(String bssd ){
//		String query = "select a from RstRaNewgoc a "
//				+ "where a.baseYymm  = :bssd "
//				;
//		
//		Query<RstRaNewgoc> q = session.createQuery(query, RstRaNewgoc.class);
//		q.setParameter("bssd", bssd);
//		return q.getResultList();
//	}
	
	
//	public static List<RatioLoss> getRatioLoss(String bssd  ){
//		String query = "select a from RatioLoss a "
//				+ "where a.baseYymm  = :bssd "
//				;
//		
//		Query<RatioLoss> q = session.createQuery(query, RatioLoss.class);
//		q.setParameter("bssd", bssd);
//		return q.getResultList();
//	}
//	
//	public static List<RatioLoss> getRatioLoss(String bssd, String gocId  ){
//		if(gocId ==null) {
//			return getRatioLoss(bssd);
//		}
//		String query = "select a from RatioLoss a "
//				+ "where a.baseYymm  = :bssd "
//				+ "and a.gocId = :gocId"
//				;
////		log.info("GET CSM : {},{},{}", session.get);
//		
//		Query<RatioLoss> q = session.createQuery(query, RatioLoss.class);
//		q.setParameter("bssd", bssd);
//		q.setParameter("gocId", gocId);
//		return q.getResultList();
//	}
	
	public static List<RstLoss> getRstLoss(String bssd  ){
		String query = "select a from RstLoss a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RstLoss> q = session.createQuery(query, RstLoss.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<RstLoss> getRstLoss(String bssd, String gocId  ){
		if(gocId ==null) {
			return getRstLoss(bssd);
		}
		String query = "select a from RstLoss a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId"
				;
//		log.info("GET CSM : {},{},{}", session.get);
		
		Query<RstLoss> q = session.createQuery(query, RstLoss.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		return q.getResultList();
	}
	public static List<RstCsm> getRstCsm(String bssd  ){
		String query = "select a from RstCsm a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RstCsm> q = session.createQuery(query, RstCsm.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<RstCsm> getRstCsm(String bssd, String gocId  ){
		if(gocId ==null) {
			return getRstCsm(bssd);
		}
		String query = "select a from RstCsm a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId "
				+ "order by a.seq"
				;
//		log.info("GET CSM : {},{},{}", session.get);
		
		Query<RstCsm> q = session.createQuery(query, RstCsm.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		return q.getResultList();
	}
	
	public static List<RstDac> getRstDac(String bssd ){
		String query = "select a from RstDac a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RstDac> q = session.createQuery(query, RstDac.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	
	public static List<RstEpv> getRstEpv(String bssd  ){
		String query = "select a from RstEpv a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RstEpv> q = session.createQuery(query, RstEpv.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<RstEpvNgoc> getRstEpvNgocFromCf(String bssd ){
		String query = "select new com.gof.entity.RstEpvNgoc(a.baseYymm, a.gocId, a.liabType, a.stStatus, a.endStatus, a.newContYn, a.runsetId"
				+ ", sum(a.cfAmt),  sum(a.epvAmt)) "
				+ "from CfLv1Goc a "
				+ "where a.baseYymm  = :bssd "
				+ "group by a.baseYymm, a.gocId, a.liabType, a.stStatus, a.endStatus, a.newContYn, a.runsetId"
				;
		
		Query<RstEpvNgoc> q = session.createQuery(query, RstEpvNgoc.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<RstEpvNgoc> getRstEpvNgocFromOutCf(String bssd ){
		String query = "select new com.gof.entity.RstEpvNgoc(a.baseYymm, a.gocId, a.liabType, a.stStatus, a.endStatus, a.newContYn, a.runsetId"
				+ ", sum(a.cfAmt),  sum(a.epvAmt)) "
				+ "from CfLv1Goc a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.outflowYn =:outflowYn "
				+ "group by a.baseYymm, a.gocId, a.liabType, a.stStatus, a.endStatus, a.newContYn, a.runsetId"
				;
		
		Query<RstEpvNgoc> q = session.createQuery(query, RstEpvNgoc.class);
		q.setParameter("bssd", bssd);
		q.setParameter("outflowYn", EBoolean.Y);
		return q.getResultList();
	}
	
	public static List<RstEpvNgoc> getRstEpvNgocFromCf(String bssd, String gocId ){
		if(gocId==null) {
			log.info("In the Dao null Goc");
			return getRstEpvNgocFromCf(bssd);
		}
		String query = "select new com.gof.entity.RstEpvNgoc(a.baseYymm, a.gocId, a.liabType, a.stStatus, a.endStatus, a.newContYn, a.runsetId"
				+ ", sum(a.cfAmt),  sum(a.epvAmt)) "
				+ "from CfLv1Goc a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId "
				+ "group by a.baseYymm, a.gocId, a.liabType, a.stStatus, a.endStatus, a.newContYn, a.runsetId"
				;
		
		Query<RstEpvNgoc> q = session.createQuery(query, RstEpvNgoc.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		return q.getResultList();
	}
	
	public static List<RstEpvNgoc> getRstEpvNgocFromOutCf(String bssd, String gocId ){
		if(gocId==null) {
			log.info("In the Dao null Goc");
			return getRstEpvNgocFromOutCf(bssd);
		}
		
		String query = "select new com.gof.entity.RstEpvNgoc(a.baseYymm, a.gocId, a.liabType, a.stStatus, a.endStatus, a.newContYn, a.runsetId"
				+ ", sum(a.cfAmt),  sum(a.epvAmt)) "
				+ "from CfLv1Goc a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId "
				+ "and a.outflowYn =:outflowYn "
				+ "group by a.baseYymm, a.gocId, a.liabType, a.stStatus, a.endStatus, a.newContYn, a.runsetId"
				;
		
		Query<RstEpvNgoc> q = session.createQuery(query, RstEpvNgoc.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		q.setParameter("outflowYn", EBoolean.Y);
		return q.getResultList();
	}
	
	public static List<RstEpvNgoc> getRstEpvNgoc(String bssd  ){
		String query = "select a from RstEpvNgoc a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RstEpvNgoc> q = session.createQuery(query, RstEpvNgoc.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<RstEpvNgoc> getRstEpvNgocByGoc(String gocId  ){
		String query = "select a from RstEpvNgoc a "
				+ "where a.gocId  = :gocId "
				;
		
		Query<RstEpvNgoc> q = session.createQuery(query, RstEpvNgoc.class);
		q.setParameter("gocId", gocId);
		return q.getResultList();
	}
}
