package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.BottomupDcnt;
import com.gof.entity.StdAssetMst;
import com.gof.entity.StdBondAssetHis;
import com.gof.entity.StdStockAssetHis;
import com.gof.entity.StockImpliedVolUd;
import com.gof.enums.EBoolean;
import com.gof.interfaces.Pricable;
import com.gof.util.HibernateUtil;

/**
 *  <p> BottomUp ������ ������{@link BottomupDcnt} �� DataBase ���� �����ϴ� ����� �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

public class StockImpliedVolDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<StockImpliedVolUd> getStockImpliedVolUd(String bssd) {
		 String sql = "select a from StockImpliedVolUd a where baseYymm =:bssd";
		 
	    List<StockImpliedVolUd> rst = session.createQuery(sql, StockImpliedVolUd.class)
	    							.setParameter("bssd", bssd)
	    							.getResultList();
		return rst;
	}
	
	public static List<StockImpliedVolUd> getStockImpliedVolUd(String bssd, String stdAsstCd) {
		 String sql = "select a from StockImpliedVolUd a where baseYymm =:bssd and a.stdAsstCd = :stdAsstCd";
		 
	    List<StockImpliedVolUd> rst = session.createQuery(sql, StockImpliedVolUd.class)
	    							.setParameter("bssd", bssd)
	    							.setParameter("stdAsstCd", stdAsstCd)
	    							.getResultList();
		return rst;
	}
}	
