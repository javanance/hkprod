package com.gof.dao;

import java.util.List;

import org.hibernate.Session;

import com.gof.entity.BizDiscRateFwdSce;
import com.gof.entity.DiscRate;
import com.gof.entity.DiscRateHis;
import com.gof.util.FinUtils;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * <p> �������� {@link DiscRate } �� DataBase ���� �����ϴ� ����� �����ϴ� Class ��         
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class DiscRateDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	public static List<DiscRateHis> getDiscRateHis(String bssd){
		String query = " select a from DiscRateHis a "
				+ "where a.baseYymm = :bssd "
				+ "order by a.baseYymm "
				;
		log.debug("test : {}", bssd);
		return  session.createQuery(query,  DiscRateHis.class)
					   .setParameter("bssd",bssd)
					   .getResultList()
					   ;
		
	}
	
	public static List<DiscRateHis> getDiscRateHis(String bssd, int monthNum){
		String query = " select a from DiscRateHis   a "
				+ "where a.baseYymm between :stBssd and :bssd "
				+ "order by a.baseYymm"
				;
		
		return  session.createQuery(query,  DiscRateHis.class)
					   .setParameter("bssd",bssd)
					   .setParameter("stBssd",FinUtils.addMonth(bssd, monthNum))
					   .getResultList()
					   ;
		
	}
	
	public static List<DiscRate> getDiscRateByCalcType(String bssd, String calcType){
		String query = " select a from DiscRate a "
				+ "where a.baseYymm = :bssd "
				+ "and a.discRateCalcTyp =:calcType "
				;
		
		
		return session.createQuery(query, DiscRate.class)
					.setParameter("bssd", bssd)
					.setParameter("calcType", calcType)
					.getResultList();
	}
	
	public static List<DiscRate> getDiscRate(String bssd){
		String query = " select a from DiscRate a "
				+ "where a.baseYymm = :bssd "
				;
		
		
		return session.createQuery(query, DiscRate.class)
					.setParameter("bssd", bssd)
					.getResultList();
	}
	
	
	
	public static BizDiscRateFwdSce getBizDiscRateFwdSce(String bssd, String bizDv, String curveId, String matCd, String sceNo, String fwdNo){
		String query = " select a from BizDiscRateFwdSce a "
				+ "where a.baseYymm = :bssd "
				+ "and a.applyBizDv = :bizDv "
				+ "and a.irCurveId = :curveId "
				+ "and a.matCd = :matCd "
				+ "and a.sceNo = :sceNo "
				+ "and a.fwdNo = :fwdNo "
				;
		
		
		return session.createQuery(query, BizDiscRateFwdSce.class)
					.setParameter("bssd", bssd)
					.setParameter("bizDv", bizDv)
					.setParameter("curveId", curveId)
					.setParameter("matCd", matCd)
					.setParameter("sceNo", sceNo)
					.setParameter("fwdNo", fwdNo)
					.getSingleResult();
	}
	
	/** 
	*  <p> KICS ���� ������ �������� �������� ���������� �ڻ�����ͷ� ������ ���� 36���� ���ġ�� ������ ���� �䱸��.
	*  <p> �Է��� ���س�� ��������  ��� �����ڵ忡 ���ؼ� �������� ���� = ��������/�ڻ�����ͷ� �� 36���� ��հ��� �����Ͽ�   
	*  @param bssd 	   ���س��
	*  @return		 ( �����ڵ�, ��հ������� ����) �� (key, value) �� Map ���� ������.                 
	*/ 
	
//	public static Map<String, Double> getKicsAvgAdjust(String bssd){
//		Map<String, Double> rstMap = new HashMap<String, Double>();
//
//		String query = " SELECT a.INT_RATE_CD, AVG(a.APPL_DISC_RATE / DECODE(a.MGT_ASST_YIELD,  0, a.EX_BASE_IR+a.DISC_RATE_SPREAD, a.MGT_ASST_YIELD)) " 
//				+ "				from ESG.EAS_DISC_RATE_HIS   a "
//				+ "				where 1=1 "
//				+ "				and a.BASE_YYMM <= :bssd	"
//				+ "				and a.BASE_YYMM >= :stBssd "
////				+ "				and a.MGT_ASST_YIELD <> 0"
//				+ "				group by a.INT_RATE_CD"
//				;
//		
//		
//		List<Object[]> rstTemp =  session.createNativeQuery(query)
//				.setParameter("bssd", bssd)
//				.setParameter("stBssd", FinUtils.addMonth(bssd, -36))
//				.getResultList();
//		
//		for(Object[] aa :rstTemp) {
//			rstMap.put(aa[0].toString(), Double.parseDouble(aa[1].toString()));
//		}
////		logger.info("KicsAvgAdj : {}" ,rstMap);
//		
//		return rstMap;
//	}

	/** 
	*  <p> KICS ���� ������ �������� �������� ���������� �ڻ�����ͷ� ������ ���� 36���� ���ġ�� ������ ���� �䱸��.
	*  <p> �Է��� �����ڵ�, ���س�� �������� 36�������� ��������, �����ͷ� �����͸� ������.   
	*  @param bssd 	   ���س��
	*  @param intRateCd		�����ڵ� 
	*  @return			�������� ���� = ��������/�ڻ�����ͷ� �� 36���� ��հ�.                
	*/ 
	
//	public static Double getKicsAvgAdjust(String bssd, String intRateCd){
//
//		String query = " SELECT AVG(a.APPL_DISC_RATE / DECODE(a.MGT_ASST_YIELD,  0, a.EX_BASE_IR+a.DISC_RATE_SPREAD, a.MGT_ASST_YIELD)) " 
////		String query = " SELECT AVG(a.APPL_DISC_RATE / a.MGT_ASST_YIELD) "		
//				+ "				from ESG.EAS_DISC_RATE_HIS   a "
//				+ "				where 1=1 "
//				+ "				and a.INT_RATE_CD =:param	"
//				+ "				and a.BASE_YYMM <= :bssd	"
//				+ "				and a.BASE_YYMM >= :stBssd "
////				+ "				and a.MGT_ASST_YIELD <> 0"
//				+ "				group by a.INT_RATE_CD"
//				;
//		
//		
//		Object rstTemp =  session.createNativeQuery(query)
//				.setParameter("bssd", bssd)
//				.setParameter("stBssd", FinUtils.addMonth(bssd, -36))
//				.setParameter("param", intRateCd)
//				.uniqueResult()
//				;
//		return Double.parseDouble(rstTemp.toString());
//	}
}
