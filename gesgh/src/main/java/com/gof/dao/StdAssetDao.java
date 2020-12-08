package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.BizStockYield;
import com.gof.entity.BottomupDcnt;
import com.gof.entity.StdAssetMst;
import com.gof.entity.StdBondAssetHis;
import com.gof.entity.StdStockAssetHis;
import com.gof.enums.EBoolean;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> BottomUp ������ ������{@link BottomupDcnt} �� DataBase ���� �����ϴ� ����� �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class StdAssetDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<StdAssetMst> getStdAssetMst() {
		 String sql = "select a from StdAssetMst a where useYn =:useYn order by a.seq";
		 
	    List<StdAssetMst> rst = session.createQuery(sql, StdAssetMst.class)
	    							.setParameter("useYn", EBoolean.Y)
	    							.getResultList();
		return rst;
	}
	
	
//	public static List<StdBondAssetHis> getStdBondAssetHis(String bssd) {
//		 String sql = "select a from StdBondAssetHis a "
//		 		+ "	where baseDate <= :bssd"
//		 		+ " order by baseDate desc"
//		 		;
//		 
//	    List<StdBondAssetHis> rst = session.createQuery(sql, StdBondAssetHis.class)
//	    							.setParameter("bssd", bssd+"31")
//	    							.getResultList();
//		return rst;
//	}
//	
	
	public static StdBondAssetHis getStdBondAssetHis(String bssd, String stdAsstCd) {
		 String sql = "select a from StdBondAssetHis a "
		 		+ "	where baseDate = :bssd"
		 		+ " and a.stdAsstCd = :stdAsstCd"
		 		+ " order by baseDate desc"
		 		;
		 
	    return session.createQuery(sql, StdBondAssetHis.class)
	    							.setParameter("bssd",getBondMaxDate(bssd, stdAsstCd))
	    							.setParameter("stdAsstCd", stdAsstCd)
	    							.getSingleResult()
	    							;
	}
	
	public static List<StdBondAssetHis> getStdBondAssetBtw(String stBssd, String bssd, String stdAsstCd) {
		 String sql = "select a from StdBondAssetHis a "
		 		+ "	where baseDate >= :stBssd"  
		 		+ " and baseDate <= :bssd"
		 		+ " and a.stdAsstCd = :stdAsstCd"
		 		+ " order by baseDate desc"
		 		;
		 
	    List<StdBondAssetHis> rst = session.createQuery(sql, StdBondAssetHis.class)
	    							.setParameter("stBssd", stBssd+"01")
	    							.setParameter("bssd", bssd+"31")
	    							.setParameter("stdAsstCd", stdAsstCd)
	    							.getResultList();
		return rst;
	}
	
	public static StdStockAssetHis getStdStockAssetHis(String bssd, String stdAsstCd) {
		 String sql = "select a from StdStockAssetHis a "
		 		+ "	where baseDate = :bssd"
		 		+ " and a.stdAsstCd = :stdAsstCd"
		 		+ " order by baseDate desc"
		 		;
		 
	   return session.createQuery(sql, StdStockAssetHis.class)
	    							.setParameter("bssd",getStockMaxDate(bssd, stdAsstCd))
	    							.setParameter("stdAsstCd", stdAsstCd)
	    							.getSingleResult();
	   
		
	}
	
	
	public static List<StdStockAssetHis> getStdStockAssetBtw(String stBssd, String bssd, String stdAsstCd) {
		 String sql = "select a from StdStockAssetHis a "
		 		+ "	where baseDate >= :stBssd"
		 		+ " and baseDate <= :bssd"
		 		+ " and a.stdAsstCd = :stdAsstCd"
		 		+ " order by baseDate desc"
		 		;
		 
	    List<StdStockAssetHis> rst = session.createQuery(sql, StdStockAssetHis.class)
	    							.setParameter("stBssd", stBssd+"01")
	    							.setParameter("bssd", bssd+"31")
	    							.setParameter("stdAsstCd", stdAsstCd)
	    							.getResultList();
		return rst;
	}

	public static String getStockMaxDate(String bssd, String stdAsstCd) {
		 String sql = "select max(a.baseDate) from StdStockAssetHis a "
		 		+ "	where a.baseDate <= :bssd"
		 		+ " and a.stdAsstCd = :stdAsstCd"
		 		;
		 
		String rst = session.createQuery(sql, String.class)
	    							.setParameter("bssd", bssd+"31")
	    							.setParameter("stdAsstCd", stdAsstCd)
	    							.getSingleResult();
//		log.info("zzzz : {},{}", stdAsstCd, rst);
		return rst;
	}
	
	public static String getBondMaxDate(String bssd, String stdAsstCd) {
		 String sql = "select max(a.baseDate) from StdBondAssetHis a "
		 		+ "	where a.baseDate <= :bssd"
		 		+ " and a.stdAsstCd = :stdAsstCd"
		 		;
		 
		String rst = session.createQuery(sql, String.class)
	    							.setParameter("bssd", bssd+"31")
	    							.setParameter("stdAsstCd", stdAsstCd)
	    							.getSingleResult();
//		log.info("zzzz : {},{}", stdAsstCd, rst);
		return rst;
	}
	
	
	public static List<BizStockYield> getStdStockYield(String bssd, String bizDv) {
		 String sql = "select new com.gof.entity.BizStockYield( a.baseYymm, a.applBizDv, a.stdAsstCd, a.matCd, avg(a.asstYield)) "
		 		+ " from BizStockSce a "
		 		+ "	where baseYymm = :bssd"
		 		+ "	and a.applBizDv = :bizDv"
		 		+ " group by a.baseYymm, a.applBizDv, a.stdAsstCd, a.matCd"
		 		;
		 
	    List<BizStockYield> rst = session.createQuery(sql, BizStockYield.class)
	    							.setParameter("bssd", bssd)
	    							.setParameter("bizDv", bizDv)
	    							.getResultList();
		return rst;
	}

}	
