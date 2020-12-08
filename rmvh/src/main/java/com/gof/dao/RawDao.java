package com.gof.dao;

import java.util.List;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.entity.RawCash;
import com.gof.entity.RawCf;
import com.gof.entity.RawCfDetail;
import com.gof.entity.RawCfNcont;
import com.gof.entity.RawElGoc;
import com.gof.entity.RawFvGoc;
import com.gof.entity.RawIntRate;
import com.gof.entity.RawRa;
import com.gof.entity.RawRaGoc;
import com.gof.entity.RawRaIbnr;
import com.gof.entity.RawRatioCsm;
import com.gof.entity.RawRatioDac;
import com.gof.entity.RawRatioLossRcv;
import com.gof.entity.RawTvog;
import com.gof.entity.RawTvogGoc;
import com.gof.entity.RawUlGocLossRcv;
import com.gof.infra.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

@Slf4j
public class RawDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<RawIntRate> getRawIntRate(String bssd ){
		String query = "select a from RawIntRate a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RawIntRate> q = session.createQuery(query, RawIntRate.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<RawIntRate> getRawIntRate(String stBssd, String bssd ){
		String query = "select a from RawIntRate a "
				+ "where a.baseYymm  <= :bssd "
				+ "and a.baseYymm  > :stBssd "
				;
		
		Query<RawIntRate> q = session.createQuery(query, RawIntRate.class);
		q.setParameter("bssd", bssd);
		q.setParameter("stBssd", stBssd);
		return q.getResultList();
	}
	
	public static List<RawIntRate> getRawIntRate(String stBssd, String bssd , String irCurveId){
		String query = "select a from RawIntRate a "
				+ "where a.baseYymm  <= :bssd "
				+ "and a.baseYymm  > :stBssd "
				+ "and a.irCurveId = :irCurveId"
				;
		
		Query<RawIntRate> q = session.createQuery(query, RawIntRate.class);
		q.setParameter("bssd", bssd);
		q.setParameter("stBssd", stBssd);
		q.setParameter("irCurveId", irCurveId);
		return q.getResultList();
	}
	
	
	public static List<RawFvGoc> getRawFvGoc(String bssd ){
		String query = "select a from RawFvGoc a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RawFvGoc> q = session.createQuery(query, RawFvGoc.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<RawRatioDac> getRawRatioDac(String bssd ){
		String query = "select a from RawRatioDac a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RawRatioDac> q = session.createQuery(query, RawRatioDac.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<RawRatioCsm> getRawRatioCsm(String bssd ){
		String query = "select a from RawRatioCsm a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RawRatioCsm> q = session.createQuery(query, RawRatioCsm.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<RawRaGoc> getRawRaGoc(String bssd ){
		String query = "select a from RawRaGoc a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RawRaGoc> q = session.createQuery(query, RawRaGoc.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<RawRaGoc> getRawRaGoc(String bssd, String gocId ){
		if(gocId==null) {
			return getRawRaGoc(bssd);
		}
		String query = "select a from RawRaGoc a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId"
				;
		
		Query<RawRaGoc> q = session.createQuery(query, RawRaGoc.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		return q.getResultList();
	}
	
	public static Stream<RawRa> getRawRa(String bssd ){
		String query = "select a from RawRa a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RawRa> q = session.createQuery(query, RawRa.class);
		q.setParameter("bssd", bssd);
		return q.stream();
	}
	
	public static Stream<RawRa> getRawRa(String bssd, String rsDivId){
		String query = "select a from RawRa a "
				+ " where a.baseYymm  = :bssd "
				+ " and a.rsDivId= :rsDivId"
				;
		
		Query<RawRa> q = session.createQuery(query, RawRa.class);
		q.setParameter("bssd", bssd);
		q.setParameter("rsDivId", rsDivId);
		return q.stream();
	}
	
//	public static Stream<NcontRstRa> getRawRaGroupBy(String bssd, String raDivId ){
//		String query = "select new com.hkl.ifrs.bat.entity.NcontRstRa(a.baseYymm, a.ctrPolno, sum(raAmt)) "
//				+ " from RawRa a "
//				+ " where a.baseYymm  = :bssd "
//				+ " and a.raDivId =:raDivId "
//				+ " group by a.baseYymm, a.ctrPolno "
//				;
//		
//		Query<NcontRstRa> q = session.createQuery(query, NcontRstRa.class);
//		q.setParameter("bssd", bssd);
//		q.setParameter("raDivId", raDivId);
//		return q.stream();
//	}
	
	public static Stream<RawElGoc> getRawElGoc(String bssd ){
		String query = "select a from RawElGoc a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RawElGoc> q = session.createQuery(query, RawElGoc.class);
		q.setParameter("bssd", bssd);
		return q.stream();
	}
	
	public static Stream<RawElGoc> getRawElGoc(String bssd , String gocId){
		if(gocId==null) {
			return getRawElGoc(bssd);
		}
		String query = "select a from RawElGoc a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId"
				;
		
		Query<RawElGoc> q = session.createQuery(query, RawElGoc.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		return q.stream();
	}
	
	
	public static Stream<RawRatioLossRcv> getRawRatioLossRcv(String setlYm){
		String query = "select a from RawRatioLossRcv a "
				+ "where a.setlYm  = :setlYm "
				;
		Query<RawRatioLossRcv> q = session.createQuery(query, RawRatioLossRcv.class);
		q.setParameter("setlYm", setlYm);
		
		return q.stream();
	}
	
	public static Stream<RawRatioLossRcv> getRawRatioLossRcv(String setlYm , String gocId ){
		if(gocId==null) {
			return getRawRatioLossRcv(setlYm);
		}
		String query = "select a from RawRatioLossRcv a "
				+ "where a.setlYm  = :setlYm "
				+ "and a.gocId = :gocId "
				;
		
		Query<RawRatioLossRcv> q = session.createQuery(query, RawRatioLossRcv.class);
		q.setParameter("setlYm", setlYm);
		q.setParameter("gocId", gocId);
		return q.stream();
	}
	
	public static Stream<RawUlGocLossRcv> getRawUlGocLossRcv(String bssd){
		String query = "select a from RawUlGocLossRcv a "
				+ "where a.baseYymm  = :bssd "
			;
		
		Query<RawUlGocLossRcv> q = session.createQuery(query, RawUlGocLossRcv.class);
		q.setParameter("bssd", bssd);
		return q.stream();
	}
	
	public static Stream<RawUlGocLossRcv> getRawUlGocLossRcv(String bssd , String ulGocId){
		if(ulGocId==null) {
			return getRawUlGocLossRcv(bssd);
		}
		String query = "select a from RawUlGocLossRcv a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.ulGocId = :ulGocId "
				;
		
		Query<RawUlGocLossRcv> q = session.createQuery(query, RawUlGocLossRcv.class);
		q.setParameter("bssd", bssd);
		q.setParameter("ulGocId", ulGocId);
		return q.stream();
	}
	
	public static Stream<RawTvog> getRawTvog(String bssd ){
		String query = "select a from RawTvog a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RawTvog> q = session.createQuery(query, RawTvog.class);
		q.setParameter("bssd", bssd);
		return q.stream();
	}
	
	public static Stream<RawTvog> getRawTvog(String bssd , String rsDivId){
		String query = "select a from RawTvog a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.rsDivId = :rsDivId"
				;
		
		Query<RawTvog> q = session.createQuery(query, RawTvog.class);
		q.setParameter("bssd", bssd);
		q.setParameter("rsDivId", rsDivId);
		return q.stream();
	}
	
//	public static Stream<NcontRstTvog> getRawTvogGroupBy(String bssd, String tvogDivId ){
//		String query = "select new com.hkl.ifrs.bat.entity.NcontRstTvog(a.baseYymm, a.ctrPolno, sum(tvogAmt)) "
//				+ " from RawTvog a "
//				+ "where a.baseYymm  = :bssd "
//				+ " and a.tvogDivId = :tvogDivId"
//				+ " group by a.baseYymm, a.ctrPolno"
//				;
//		
//		Query<NcontRstTvog> q = session.createQuery(query, NcontRstTvog.class);
//		q.setParameter("bssd", bssd);
//		q.setParameter("tvogDivId", tvogDivId);
//		return q.stream();
//	}
	
	public static List<RawTvogGoc> getRawTvogGoc(String bssd ){
		String query = "select a from RawTvogGoc a "
				+ "where a.baseYymm  = :bssd "
				;
		
		Query<RawTvogGoc> q = session.createQuery(query, RawTvogGoc.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static List<RawTvogGoc> getRawTvogGoc(String bssd, String gocId ){
		if(gocId==null) {
			return getRawTvogGoc(bssd);
		}
		String query = "select a from RawTvogGoc a "
				+ "where a.baseYymm  = :bssd "
				+ "and a.gocId = :gocId"
				;
		
		Query<RawTvogGoc> q = session.createQuery(query, RawTvogGoc.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		return q.getResultList();
	}
	public static Stream<RawCfNcont> getRawCfNcontStream(String bssd){
		String query = "select a from RawCfNcont a "
				+ "where a.setlYm  = :bssd "
				;
		
		Query<RawCfNcont> q = session.createQuery(query, RawCfNcont.class);
		q.setParameter("bssd", bssd);

		return q.stream();
	}
	public static Stream<RawCfNcont> getRawCfNcontStream(String bssd, String rsDivId){
		String query = "select a from RawCfNcont a "
				+ "where a.setlYm  = :bssd "
				+ " and a.rsDivId =:rsDivId"
		;
		Query<RawCfNcont> q = session.createQuery(query, RawCfNcont.class);
		q.setParameter("bssd", bssd);
		q.setParameter("rsDivId", rsDivId);

		return q.stream();
	}
	
	public static Stream<RawCfNcont> getRawCfNcontStream(String bssd, String rsDivId, String ctrPolno){
		String query = "select a from RawCfNcont a "
				+ " where a.setlYm  = :bssd "
				+ " and a.rsDivId =:rsDivId"
				+ " and a.ctrPolno =:ctrPolno"
		;
		Query<RawCfNcont> q = session.createQuery(query, RawCfNcont.class);
		q.setParameter("bssd", bssd);
		q.setParameter("rsDivId", rsDivId);
		q.setParameter("ctrPolno", ctrPolno);

		return q.stream();
	}
	
	public static Stream<RawCfDetail> getRawCfDetailStream(String bssd){
		String query = "select a from RawCfDetail a "
				+ "where a.setlYm  = :bssd "
		;
		Query<RawCfDetail> q = session.createQuery(query, RawCfDetail.class);
		q.setParameter("bssd", bssd);
		
		
		return q.stream();
	}
	
	public static Stream<RawCfDetail> getRawCfDetailByRsDivStream(String bssd, String rsDivId){
		String query = "select a from RawCfDetail a "
				+ "where a.setlYm  = :bssd "
				+ "and a.rsDivId  = :rsDivId "
		;
		Query<RawCfDetail> q = session.createQuery(query, RawCfDetail.class);
		q.setParameter("bssd", bssd);
		q.setParameter("rsDivId", rsDivId);
		
		return q.stream();
	}
	
	
//	public static Stream<RawCf> getRawCfStream(String driveYm, String setlYm){
//		String query = "select a from RawCf a "
//				+ "where  a.driveYm= :driveYm "
//				+ "and a.setlYm  = :setlYm "
//		;
//		
//		Query<RawCf> q = session.createQuery(query, RawCf.class);
//		q.setParameter("driveYm", driveYm);
//		q.setParameter("setlYm", setlYm);
//		
//		return q.stream();
//	}
//	
//	public static Stream<RawCf> getRawCfStream(String driveYm, String setlYm, String gocId){
//		if(gocId==null) {
//			return getRawCfStream(driveYm, setlYm);
//		}
//		String query = "select a from RawCf a "
//				+ "where a.driveYm= :driveYm "
//				+ "and a.setlYm  = :setlYm "
//				+ "and a.csmGrpCd  = :gocId "
//		;
//		
//		Query<RawCf> q = session.createQuery(query, RawCf.class);
//		q.setParameter("driveYm", driveYm);
//		q.setParameter("setlYm", setlYm);
//		q.setParameter("gocId", gocId);
//		
//		return q.stream();
//	}
	
	public static Stream<RawCf> getRawCfByRsDivStream(String driveYm, String setlYm, String rsDivId){
		String query = "select a from RawCf a "
				+ "where a.driveYm= :driveYm "
				+ "and a.setlYm  = :setlYm "
				+ "and a.rsDivId  = :rsDivId "
		;
		Query<RawCf> q = session.createQuery(query, RawCf.class);
		q.setParameter("driveYm", driveYm);
		q.setParameter("setlYm", setlYm);
		q.setParameter("rsDivId", rsDivId);
		
		return q.stream();
	}
	
	public static Stream<RawCf> getRawCfByRsDivStream(String driveYm, String setlYm, String rsDivId, String gocId){
		if(gocId==null) {
			return getRawCfByRsDivStream(driveYm, setlYm, rsDivId);
		}
		String query = "select a from RawCf a "
				+ "where a.driveYm =:driveYm "
				+ "and a.setlYm  = :setlYm "
				+ "and a.rsDivId  = :rsDivId "
				+ "and a.csmGrpCd  = :gocId "
		;
		Query<RawCf> q = session.createQuery(query, RawCf.class);
		q.setParameter("driveYm", driveYm);
		q.setParameter("setlYm", setlYm);
		q.setParameter("rsDivId", rsDivId);
		q.setParameter("gocId", gocId);
		
		return q.stream();
	}
	
	
	
//	public static Stream<RawCf> getRawCfStream1(String driveYm, String setlYm, String rsDivId, String exeId){
//		String query = "select new com.goc.entity.RawCf(setlYm, rsDivId, csmGrpCd, bemmStcd, emmStcd, ctrDvcd, setlAftPassMmcnt, cfGroup, cfTiming, outflowYn"
//				+ " , avg(absCfAmt), avg(absPvAmt) )"
//				+ " from CfLv1RawGroup a "
//				+ "where a.setlYm  = :bssd "
//				+ "and a.rsDivId  = :rsDivId "
//				+ " and a.exeIdno  like %:exeId%"
//				+ " group by setlYm, rsDivId, csmGrpCd, bemmStcd, emmStcd, ctrDvcd, setlAftPassMmcnt, cfGroup, cfTiming, outflowYn";
//		
//		;
//		Query<RawCf> q = session.createQuery(query, RawCf.class);
//		q.setParameter("bssd", bssd);
//		q.setParameter("rsDivId", rsDivId);
//		q.setParameter("exeId", exeId);
//		
//		
//		return q.stream();
//	}
	
	
	public static Stream<RawCash> getRawCashStream(String bssd){
		String query = "select a from RawCash a "
				+ " where a.baseYymm  = :bssd "
				;
		
		Query<RawCash> q = session.createQuery(query, RawCash.class);
		q.setParameter("bssd", bssd);
		
		return q.stream();
	}

	public static Stream<RawCash> getRawCashStream(String bssd, String gocId){
		if(gocId==null) {
			return getRawCashStream(bssd);
		}
		String query = "select a from RawCash a "
				+ " where a.baseYymm  = :bssd "
				+ " and a.gocId = :gocId"
		;
		
		Query<RawCash> q = session.createQuery(query, RawCash.class);
		q.setParameter("bssd", bssd);
		q.setParameter("gocId", gocId);
		
		return q.stream();
	}
	public static Stream<RawRaIbnr> getRawRaIbnrByRsDivStream(String driveYm, String setlYm, String rsDivId){
		String query = "select a from RawRaIbnr a "
				+ "where a.driveYm= :driveYm "
				+ "and a.setlYm  = :setlYm "
				+ "and a.rsDivId  = :rsDivId "
		;
		Query<RawRaIbnr> q = session.createQuery(query, RawRaIbnr.class);
		q.setParameter("driveYm", driveYm);
		q.setParameter("setlYm", setlYm);
		q.setParameter("rsDivId", rsDivId);
		
		return q.stream();
	}
	
	
	public static Stream<RawRaIbnr> getRawRaIbnrByRsDivStream(String driveYm, String setlYm, String rsDivId, String gocId){
		if(gocId==null) {
			return getRawRaIbnrByRsDivStream(driveYm, setlYm, rsDivId);
		}
		String query = "select a from RawRaIbnr a "
				+ "where a.driveYm =:driveYm "
				+ "and a.setlYm  = :setlYm "
				+ "and a.rsDivId  = :rsDivId "
				+ "and a.csmGrpCd  = :gocId "
		;
		Query<RawRaIbnr> q = session.createQuery(query, RawRaIbnr.class);
		q.setParameter("driveYm", driveYm);
		q.setParameter("setlYm", setlYm);
		q.setParameter("rsDivId", rsDivId);
		q.setParameter("gocId", gocId);
		
		return q.stream();
	}
}
