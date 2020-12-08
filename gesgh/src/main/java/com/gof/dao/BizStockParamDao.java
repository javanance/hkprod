package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.BizStockParam;
import com.gof.entity.BizStockParamUd;
import com.gof.entity.BottomupDcnt;
import com.gof.util.HibernateUtil;

/**
 *  <p> BottomUp ������ ������{@link BottomupDcnt} �� DataBase ���� �����ϴ� ����� �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

public class BizStockParamDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<BizStockParam> getBizStockParam(String bssd, String bizDv) {
		 String sql = "select a from BizStockParam a where baseYymm =:bssd and a.applBizDv = :bizDv ";
		 
	    List<BizStockParam> rst = session.createQuery(sql, BizStockParam.class)
	    							.setParameter("bssd", bssd)
	    							.setParameter("bizDv", bizDv)
	    							.getResultList();
		return rst;
	}
	
	
	public static List<BizStockParam> getBizStockParam(String bssd, String bizDv, String paramTypCd) {
		 String sql = "select a from BizStockParam a where baseYymm =:bssd and a.applBizDv = :bizDv and a.paramTypCd =:paramTypCd ";
		 
	    List<BizStockParam> rst = session.createQuery(sql, BizStockParam.class)
	    							.setParameter("bssd", bssd)
	    							.setParameter("bizDv", bizDv)
	    							.setParameter("paramTypCd", paramTypCd)
	    							.getResultList();
		return rst;
	}
	
	public static List<BizStockParamUd> getBizStockParamUd(String bssd, String bizDv) {
		 String sql = "select a from BizStockParamUd a where baseYymm =:bssd and a.applBizDv = :bizDv ";
		 
	    List<BizStockParamUd> rst = session.createQuery(sql, BizStockParamUd.class)
	    							.setParameter("bssd", bssd)
	    							.setParameter("bizDv", bizDv)
	    							.getResultList();
		return rst;
	}
}	
