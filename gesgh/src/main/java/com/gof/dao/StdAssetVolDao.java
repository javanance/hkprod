package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.BottomupDcnt;
import com.gof.entity.StdAssetCorr;
import com.gof.entity.StdAssetVol;
import com.gof.model.StringTuple3;
import com.gof.model.Tuple3;
import com.gof.util.HibernateUtil;

/**
 *  <p> BottomUp ������ ������{@link BottomupDcnt} �� DataBase ���� �����ϴ� ����� �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

public class StdAssetVolDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<StdAssetVol> getStdAssetVol(String bssd, String volCalcId) {
		 String sql = "select a from StdAssetVol a "
		 		+ "	where a.baseDate = :bssd "
		 		+ " and a.volCalcId = :volCalcId";
		 
	    List<StdAssetVol> rst = session.createQuery(sql, StdAssetVol.class)
	    							.setParameter("bssd", bssd)
	    							.setParameter("volCalcId", volCalcId)
	    							.getResultList();
		return rst;
	}
	
	public static List<StdAssetVol> getStdAssetVolByMonth(String bssd, String volCalcId) {
		 String sql = "select new com.gof.entity.StdAssetVol(a.mvId, avg(mvHisVol)) from StdAssetVol a "
		 		+ "	where substr(baseDate,1,6) = :bssd"
		 		+ " and a.volCalcId = :volCalcId"
		 		+ " group by :bssd, a.mvId"
		 		+ " order by a.mvId"	 
		 		;
		 
	    List<StdAssetVol> rst = session.createQuery(sql, StdAssetVol.class)
	    									.setParameter("bssd", bssd)
	    									.setParameter("volCalcId", volCalcId)
	    									.getResultList();
		return rst;
	}
	
	
	public static List<StdAssetCorr> getStdAssetCorr(String bssd, String volCalcId) {
		 String sql = "select a from StdAssetCorr a "
		 		+ "	where baseDate = :bssd"
		 		+ " and a.volCalcId = :volCalcId"	 
		 		;
		 
	    List<StdAssetCorr> rst = session.createQuery(sql, StdAssetCorr.class)
	    									.setParameter("bssd", bssd)
	    									.setParameter("volCalcId", volCalcId)
	    									.getResultList();
		return rst;
	}
	
	
	public static List<StdAssetCorr> getStdAssetCorrByMonth(String bssd, String volCalcId) {
		 String sql = "select new com.gof.entity.StdAssetCorr(a.mvId, a.refMvId, avg(mvHisCov), avg(mvHisCorr)) from StdAssetCorr a "
		 		+ "	where substr(baseDate,1,6) = :bssd"
		 		+ " and a.volCalcId = :volCalcId"
		 		+ " group by a.mvId, a.refMvId"
		 		+ " order by a.mvId, a.refMvId"	 
		 		;
		 
	    List<StdAssetCorr> rst = session.createQuery(sql, StdAssetCorr.class)
	    									.setParameter("bssd", bssd)
	    									.setParameter("volCalcId", volCalcId)
	    									.getResultList();
		return rst;
	}

//	public static List<StringTuple3> getStdAssetCorrByMonth1(String bssd, String volCalcId) {
//		 String sql = "select new com.gof.model.Tuple3(a.mvId, a.refMvId, avg(mvHisCorr)) from StdAssetCorr a "
//		 		+ "	where substr(baseDate,1,6) = :bssd"
//		 		+ " and a.volCalcId = :volCalcId"
//		 		+ " group by a.mvId, a.refMvId"
//		 		+ " order by a.mvId, a.refMvId"	 
//		 		;
//		 
//	    List<StringTuple3> rst = session.createQuery(sql, StringTuple3.class)
//	    									.setParameter("bssd", bssd)
//	    									.setParameter("volCalcId", volCalcId)
//	    									.getResultList();
//		return rst;
//	}
}	
