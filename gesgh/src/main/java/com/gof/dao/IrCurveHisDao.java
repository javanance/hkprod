package com.gof.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.jpa.QueryHints;

import com.gof.entity.BizIrCurveHis;
import com.gof.entity.BizIrCurveSce;
import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveHis;
import com.gof.entity.IrSce;
import com.gof.enums.EBaseMatCd;
import com.gof.enums.EBoolean;
import com.gof.util.FinUtils;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;


/**
 *  <p> 占쌥몌옙 占썩간占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占싹댐옙 占쌥몌옙占싱뤄옙  占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙.
 *  <p> 
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class IrCurveHisDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();

	public static IrCurve getIrCurve(String curveId) {
		String query = "select a from IrCurve a where a.irCurveId =:id ";
		return   session.createQuery(query, IrCurve.class)
						.setParameter("id",curveId )			
						.getSingleResult();
	}
	
	public static List<IrCurve> getRiskFreeIrCurve(){
		String query = "select a from IrCurve a "
					 + "where 1=1 "
					 + "and a.creditGrate = :crdGrdCd "
					 + "and a.useYn = :useYn "
					 ;
		
		return   session.createQuery(query, IrCurve.class)
						.setParameter("crdGrdCd","RF" )				
						.setParameter("useYn", EBoolean.Y )			
						.getResultList();
	}

	public static List<IrCurve> getIrCurveByCrdGrdCd(String crdCrdCd){
		String query = "select a from IrCurve a "
					 + "where 1=1 "
					 + "and a.creditGrate = :crdGrdCd "
					 + "and a.useYn		  = :useYn "
					 + "and a.applMethDv  <> '6' "					
					 + "and a.refCurveId  is null "
					 ;
		
		return   session.createQuery(query, IrCurve.class)
								 .setParameter("crdGrdCd",crdCrdCd )				
								 .setParameter("useYn", EBoolean.Y )				
								 .getResultList();
								 
	}
	
	public static List<IrCurve> getBottomUpIrCurve(){
		return getIrCurveByGenMethod("4");
	}
	
	public static List<IrCurve> getIrCurveByGenMethod(String applMethDv){
		String query = "select a from IrCurve a "
					 + "where 1=1 "
					 + "and a.applMethDv = :applMethDv "
					 + "and a.useYn = :useYn"
					 ;
		
		return   session.createQuery(query, IrCurve.class)
								 .setParameter("applMethDv",applMethDv)			// Bond Gen : 3, BottomUp : 4 , TopDown : 6, KICS : 5 SwapRate : 7
								 .setParameter("useYn", EBoolean.Y)				
								 .getResultList();
	}
	
	public static Map<String, String> getEomMap(String bssd, String irCurveId){
		String query = "select substring(a.baseDate, 0,6), max(a.baseDate) "
					 + "from IrCurveHis a "
					 + "where 1=1 "
					 + "and a.irCurveId = :irCurveId "
					 + "and a.baseDate <= :bssd	"
					 + "group by substring(a.baseDate, 0,6)"
					 ;
		
		List<Object[]> maxDate =  session.createQuery(query)
				 				 .setParameter("irCurveId", irCurveId)			
								 .setParameter("bssd", FinUtils.toEndOfMonth(bssd))
								 .getResultList();
//		if(maxDate == null) {
//			log.warn("IR Curve History Data is not found {} at {}!!!" , irCurveId, FinUtils.toEndOfMonth(bssd));
//			return new hashMap<String, String>;
//		}
		
		Map<String, String> rstMap = new HashMap<String, String>();
		for(Object[] aa : maxDate) {
			rstMap.put(aa[0].toString(), aa[1].toString());
		}
		return rstMap;
	}
	public static String getEomDate (String bssd, String irCurveId) {
		String query = "select max(a.baseDate) "
				 + "from IrCurveHis a "
				 + "where 1=1 "
				 + "and a.irCurveId = :irCurveId "
				 + "and a.baseDate >= :bom	"
				 + "and a.baseDate <= :eom	"
				 ;
		Object maxDate =  session.createQuery(query)
				 				 .setParameter("irCurveId", irCurveId)			
				 				 .setParameter("bom", bssd)
								 .setParameter("eom", FinUtils.toEndOfMonth(bssd))
								 .uniqueResult();
		if(maxDate==null) {
			log.warn("IR Curve History Data is not found {} at {}!!!" , irCurveId, bssd);
			return bssd;
		}
		return maxDate.toString();
	}
	
	public static String getMaxBaseDate (String bssd, String irCurveId) {
		String query = "select max(a.baseDate) "
					 + "from IrCurveHis a "
					 + "where 1=1 "
					 + "and a.irCurveId = :irCurveId "
					 + "and a.baseDate <= :bssd	"
					 ;
		Object maxDate =  session.createQuery(query)
				 				 .setParameter("irCurveId", irCurveId)			
								 .setParameter("bssd", FinUtils.toEndOfMonth(bssd))
								 .uniqueResult();
		if(maxDate==null) {
			log.warn("IR Curve History Data is not found {} at {}!!!" , irCurveId, FinUtils.toEndOfMonth(bssd));
			return bssd;
		}
		return maxDate.toString();
	}
	
	public static List<IrCurveHis> getIrCurveHis(String bssd, String irCurveId){
		String query = "select a from IrCurveHis a "
					 + "where 1=1 "
					 + "and a.irCurveId =:irCurveId "
					 + "and a.baseDate  = :bssd	"
					 + "order by a.matCd"
					 ;
		
		List<IrCurveHis> curveRst =  session.createQuery(query, IrCurveHis.class)
				.setParameter("irCurveId", irCurveId)
				.setParameter("bssd", getMaxBaseDate(bssd, irCurveId))
				.getResultList();
		
//		log.info("maxDate : {}, curveSize : {}", getMaxBaseDate(bssd, irCurveId),curveRst.size());
		return curveRst;
	}
	
	/** 
	*  <p> 
	*  @param bssd 	 
	*  @param stBssd  
	*  @param curveId  
	*  @return		                     
	*/
	public static List<IrCurveHis> getCurveHisBetween(String bssd, String stBssd, String curveId){
		String query = "select a from IrCurveHis a "
				+ "where 1=1 "
				+ "and a.baseDate <= :bssd	"
				+ "and a.baseDate >= :stBssd "
				+ "and a.irCurveId =:param1 "
//				+ "and a.matCd not in (:matCd1, :matCd2, :matCd3) "
				+ "order by a.baseDate"
				;
		
		List<IrCurveHis> curveRst =  session.createQuery(query, IrCurveHis.class)
				.setParameter("param1", curveId)
				.setParameter("bssd", FinUtils.addMonth(bssd, 1))
				.setParameter("stBssd", stBssd)
//				.setParameter("matCd1", "M0018")
//				.setParameter("matCd2", "M0030")
//				.setParameter("matCd3", "M0048")
				.getResultList();		
		
//		Map<String, Map<String, IrCurveHis>> curveMap = curveRst.stream().collect(Collectors.groupingBy(s -> s.getMatCd()
//				, Collectors.toMap(s-> s.getBaseYymm(), Function.identity(), (s,u)->u)));
//		curveMap.entrySet().forEach(s -> log.info("aaa : {},{},{}", s.getKey(), s.getValue()));
		return curveRst;
	}
	
	
	public static List<IrCurveHis> getShortRateBtw(String stBssd, String bssd, String curveId){
		String query = "select a from IrCurveHis a "
				+ "where 1=1 "
				+ "and a.baseDate <= :bssd	"
				+ "and a.baseDate >= :stBssd "
				+ "and a.irCurveId =:param1 "
				+ "and a.matCd = :matCd "
				+ "order by a.baseDate desc"
				;
		
		List<IrCurveHis> curveRst =  session.createQuery(query, IrCurveHis.class)
				.setParameter("param1", curveId)
				.setParameter("stBssd", stBssd+"01")
				.setParameter("bssd", bssd+"31")
				.setParameter("matCd", "M0003")
				.getResultList();		
		
//		Map<String, Map<String, IrCurveHis>> curveMap = curveRst.stream().collect(Collectors.groupingBy(s -> s.getMatCd()
//				, Collectors.toMap(s-> s.getBaseYymm(), Function.identity(), (s,u)->u)));
//		curveMap.entrySet().forEach(s -> log.info("aaa : {},{},{}", s.getKey(), s.getValue()));
		return curveRst;
	}
	
	
	public static IrCurveHis getShortRateHis(String bssd, String curveId){
		String query = "select a from IrCurveHis a "
				+ "where 1=1 "
				+ "and a.baseDate = :bssd	"
				+ "and a.irCurveId =:param1 "
				+ "and a.matCd = :matCd "
				+ "order by a.baseDate"
				;
		
		IrCurveHis curveRst =  session.createQuery(query, IrCurveHis.class)
				.setParameter("param1", curveId)
				.setParameter("bssd", getMaxBaseDate(bssd, curveId))
				.setParameter("matCd", "M0003")
				.getSingleResult()
				;		
		

		return curveRst;
	}
	
	public static List<IrCurveHis> getIrCurveHisByMaturityHis(String bssd, int monthNum, String irCurveId,String matCd){
		String query = "select a from IrCurveHis a "
					 + "where 1=1 "
					 + "and a.irCurveId =:param1 "
					 + "and a.baseDate >=:stBssd "
					 + "and a.baseDate <=:bssd "
					 + "and a.matCd =:param2 ";
					 ;
		
		return   session.createQuery(query, IrCurveHis.class)
				.setParameter("param1", irCurveId)
				.setParameter("stBssd", FinUtils.addMonth(bssd, monthNum)+"01")
				.setParameter("bssd", bssd+"31")
				.setParameter("param2", matCd)				
				.getResultList();
	}
	
	/** 
	*  <p> KRW 占쏙옙占쏙옙채占쏙옙 특占쏙옙 占쏙옙占쏙옙占쌘듸옙占쏙옙 占쏙옙占쏙옙 占쌥몌옙占싱뤄옙 占쏙옙占쏙옙 占쏙옙占쏙옙 (占쏙옙체) 
	*  @param bssd 	   占쏙옙占쌔놂옙占�
	*  @param matCd1   占쏙옙占쏙옙占쌘듸옙 1
	*  @param matCd2   占쏙옙占쏙옙占쌘듸옙 2
	*  @return		   특占쏙옙 占쏙옙占쏙옙占쌘듸옙占쏙옙 占쏙옙占쏙옙 占싱뤄옙                  
	*/
	public static List<IrCurveHis> getKTBMaturityHis(String bssd, String matCds){
//	public static List<IrCurveHis> getKTBMaturityHis(String bssd, String matCd1, String matCd2){
		String matCd1 = matCds.split(",")[0].trim();
		String matCd2 ="";
		if(matCds.split(",").length==2) {
			matCd2 =matCds.split(",")[1].trim();
		}
		
		String query = 	"select new com.gof.entity.IrCurveHis (substr(a.baseDate,1,6), a.matCd, avg(a.intRate)) "
					+ "from IrCurveHis a "
					+ "where 1=1 "
					+ "and a.baseDate <= :bssd	"
					+ "and a.irCurveId =:param1 "
					+ "and a.matCd in (:param2, :param3) "
					+ "group by substr(a.baseDate,1,6), a.matCd "
					;
		
		List<IrCurveHis> curveRst =  session.createQuery(query, IrCurveHis.class)
				.setParameter("param1", "A100")
				.setParameter("param2", matCd1)
				.setParameter("param3", matCd2)
				.setParameter("bssd", FinUtils.addMonth(bssd, 1))
				.getResultList();		
		return curveRst;
	}
	
	public static List<IrCurveHis> getKTBMaturityHis(String bssd, String matCd1, String matCd2){
			
			String query = "select new com.gof.entity.IrCurveHis (substr(a.baseDate,1,6), a.matCd, avg(a.intRate)) "
						+ "from IrCurveHis a "
						+ "where 1=1 "
						+ "and a.baseDate <= :bssd	"
						+ "and a.irCurveId =:param1 "
						+ "and a.matCd in (:param2, :param3) "
						+ "group by substr(a.baseDate,1,6), a.matCd "
						;
			
			List<IrCurveHis> curveRst =  session.createQuery(query, IrCurveHis.class)
					.setParameter("param1", "A100")
					.setParameter("param2", matCd1)
					.setParameter("param3", matCd2)
					.setParameter("bssd", FinUtils.addMonth(bssd, 1))
					.getResultList();		
			return curveRst;
	}
	
	
	/** 
	*  <p> 占쏙옙占쏙옙占쏙옙占쌘븝옙 占쌥몌옙占썩간占쏙옙占쏙옙占쏙옙 List 占쏙옙 占쏙옙占쏙옙占쏙옙. DNS 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙 Term Structure 占쏙옙 占썰구占실댐옙 占쏙옙占쏙옙占쏙옙 활占쏙옙占�  
	*  @param bssd 	   占쏙옙占쌔놂옙占�
	*  @param stBssd  占쏙옙占쌜놂옙占�
	*  @param irCurveId  占쌥몌옙占쏘선 ID
	*  @return		  占쏙옙占쏙옙占쏙옙占쌘븝옙 占쌥몌옙占썩간占쏙옙占쏙옙                   
	*/
	public static Map<String, List<IrCurveHis>> getIrCurveListTermStructure(String bssd, String stBssd, String irCurveId){
		String query =" select a from IrCurveHis a " 
					+ "where a.irCurveId =:irCurveId "			
					+ "and a.baseDate >= :stBssd "
					+ "and a.baseDate <= :bssd "
					+ "and a.matCd in (:matCdList)"
					+ "order by a.baseDate, a.matCd "
					;
		
		return session.createQuery(query, IrCurveHis.class)
				.setParameter("irCurveId", irCurveId)
				.setParameter("stBssd", stBssd)
				.setParameter("bssd", FinUtils.toEndOfMonth(bssd))
				.setParameterList("matCdList", EBaseMatCd.names())
				.stream()
//				.collect(Collectors.groupingBy(s ->s.getBaseDate(), TreeMap::new, Collectors.toList()))
				.collect(Collectors.groupingBy(s ->s.getMatCd(), TreeMap::new, Collectors.toList()))
				;
	}
	
	public static List<BizIrCurveHis> getBizIrCurveHis(String bssd, String bizDv, String irCurveId){
		String query ="select a from BizIrCurveHis a " 
					+ "where a.irCurveId =:irCurveId "			
					+ "and a.baseYymm = :bssd "
					+ "and a.applBizDv = :bizDv "
					+ "order by a.matCd"
				;
		
		return session.createQuery(query, BizIrCurveHis.class)
				.setParameter("irCurveId", irCurveId)
				.setParameter("bssd", bssd)
				.setParameter("bizDv", bizDv)
				.getResultList()
				;
	}

	public static List<IrSce> getIrCurveSce(String bssd, String irCurveId, String sceNo){
		String query ="select a from IrSce a " 
					+ "where a.irCurveId =:irCurveId "			
					+ "and a.baseDate = :bssd "
					+ "and a.sceNo = :sceNo "
				;
		
		return session.createQuery(query, IrSce.class)
				.setParameter("irCurveId", irCurveId)
				.setParameter("bssd", bssd)
				.setParameter("sceNo", sceNo)
				.getResultList()
				;
	}

	public static List<IrSce> getIrCurveSce(String bssd, String irCurveId){
		String query ="select a from IrSce a " 
					+ "where a.irCurveId =:irCurveId "			
					+ "and a.baseDate = :bssd "
				;
		
		return session.createQuery(query, IrSce.class)
				.setParameter("irCurveId", irCurveId)
				.setParameter("bssd", bssd)
				.setHint(QueryHints.HINT_READONLY, true)        //TODO : Check performance!!!!
				.getResultList()
				;
	}

	public static List<BizIrCurveSce> getBizIrCurveSce(String bssd, String bizDv, String irCurveId, String sceNo){
		String query ="select a from BizIrCurveSce a " 
					+ "where a.irCurveId =:irCurveId "		
					+ "and a.baseYymm = :bssd "
					+ "and a.applBizDv = :bizDv "
					+ "and a.sceNo = :sceNo "
				;
		
		return session.createQuery(query, BizIrCurveSce.class)
				.setParameter("irCurveId", irCurveId)
				.setParameter("bssd", bssd)
				.setParameter("bizDv", bizDv)
				.setParameter("sceNo", sceNo)
				.getResultList()
				;
	}
	
	

	public static List<BizIrCurveSce> getBizIrCurveSce(String bssd, String bizDv, String irCurveId){
		String query ="select a from BizIrCurveSce a " 
					+ "where 1=1 "
					+ "and a.baseYymm = :bssd "
					+ "and a.applBizDv = :bizDv "
					+ "and a.irCurveId =:irCurveId"
				;
		
		return session.createQuery(query, BizIrCurveSce.class)
				.setParameter("bssd", bssd)
				.setParameter("bizDv", bizDv)
				.setParameter("irCurveId", irCurveId)
				.getResultList()
				;
	}

	public static List<IrCurveHis> getEomTimeSeries(String bssd, String irCurveId, String matCd, int monNum){
		Collection<String> eomList = getEomMap(bssd, irCurveId).values(); 

		String query = "select a from IrCurveHis a "
				 	 + "where a.irCurveId = :irCurveId "
				 	 + "and a.baseDate > :stBssd "
				 	 + "and a.baseDate < :bssd "
				 	 + "and a.baseDate in :eomList "
				 	 + "and a.matCd = :matCd "
				 	 + "order by a.baseDate desc "
				 	 ;
		
		return   session.createQuery(query, IrCurveHis.class)
				 .setParameter("bssd", FinUtils.toEndOfMonth(bssd))			
				 .setParameter("stBssd", FinUtils.toEndOfMonth( FinUtils.addMonth(bssd, monNum)))				
				 .setParameter("irCurveId", irCurveId)				
				 .setParameter("matCd", matCd)				
				 .setParameter("eomList", eomList)	
				 .getResultList();
	}
	
	
    //TODO:
	public static List<IrCurveHis> getIrCurveListTermStructureForShock(String bssd, String stBssd, String irCurveId){
		
		String query =" select a from IrCurveHis a " 
					+ "where a.irCurveId =:irCurveId "			
					+ "and a.baseDate >= :stBssd "
					+ "and a.baseDate <= :bssd "
					//+ "and a.matCd in (:matCdList)"
					+ "order by a.baseDate, a.matCd "
					;
		
		return session.createQuery(query, IrCurveHis.class)
				.setParameter("irCurveId", irCurveId)
				.setParameter("stBssd", stBssd)
				.setParameter("bssd", FinUtils.toEndOfMonth(bssd))
//				.setParameterList("matCdList", EBaseMatCdShock.names())
				.getResultList()
//				.collect(Collectors.groupingBy(s ->s.getBaseDate(), TreeMap::new, Collectors.toList()))
//				.collect(Collectors.groupingBy(s ->s.getMatCd(), TreeMap::new, Collectors.toList()))
				;
	}	
	
    //TODO:
	public static List<IrCurveHis> getIrCurveListTermStructureForShock(String bssd, String stBssd, String irCurveId, List<String> tenorList){
		
		String query =" select a from IrCurveHis a " 
					+ "where a.irCurveId =:irCurveId "			
					+ "and a.baseDate >= :stBssd "
					+ "and a.baseDate <= :bssd "
					+ "and a.matCd in (:matCdList)"
					+ "order by a.baseDate, a.matCd "
					;
		
		return session.createQuery(query, IrCurveHis.class)
				.setParameter("irCurveId", irCurveId)
				.setParameter("stBssd", stBssd)
				.setParameter("bssd", FinUtils.toEndOfMonth(bssd))
				.setParameterList("matCdList", tenorList)
				.getResultList()
				;
	}
	
	
	public static List<IrCurveHis> getIrCurveHis(String bssd, String irCurveId, List<String> tenorList){
		String query = "select a from IrCurveHis a "
					 + "where 1=1 "
					 + "and a.irCurveId =:irCurveId "
					 + "and a.baseDate  = :bssd	"
					 + "and a.matCd in (:matCdList)"
					 + "order by a.matCd"
					 ;
		
		List<IrCurveHis> curveRst =  session.createQuery(query, IrCurveHis.class)
				.setParameter("irCurveId", irCurveId)
				.setParameter("bssd", getMaxBaseDate(bssd, irCurveId))
				.setParameterList("matCdList", tenorList)
				.getResultList();		

		return curveRst;
	}	
	
}
