package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.BottomupDcnt;
import com.gof.entity.StockParamHis;
import com.gof.util.HibernateUtil;

/**
 *  <p> BottomUp ������ ������{@link BottomupDcnt} �� DataBase ���� �����ϴ� ����� �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

public class StockParamHisDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<StockParamHis> getStockParamHis(String bssd) {
		 String sql = "select a from StockParamHis a where baseYymm =:bssd";
		 
	    List<StockParamHis> rst = session.createQuery(sql, StockParamHis.class)
	    							.setParameter("bssd", bssd)
	    							.getResultList();
		return rst;
	}
	
	public static List<StockParamHis> getStockParamHis(String bssd, String stdAsstCd) {
		 String sql = "select a from StockParamHis a where baseYymm =:bssd and a.stdAsstCd = :stdAsstCd";
		 
	    List<StockParamHis> rst = session.createQuery(sql, StockParamHis.class)
	    							.setParameter("bssd", bssd)
	    							.setParameter("stdAsstCd", stdAsstCd)
	    							.getResultList();
		return rst;
	}
	

}	
