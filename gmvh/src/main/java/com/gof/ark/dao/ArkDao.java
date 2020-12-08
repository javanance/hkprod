package com.gof.ark.dao;

import java.util.List;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.ark.entity.ArkBoxMap;
import com.gof.ark.entity.ArkFutureCf;
import com.gof.ark.entity.ArkFwdEpv;
import com.gof.ark.entity.ArkItemRst;
import com.gof.ark.entity.ArkMstRunset;
import com.gof.ark.entity.ArkRawCfEir;
import com.gof.ark.entity.ArkRawEpv;
import com.gof.ark.entity.ArkRawEpvDetail;
import com.gof.ark.entity.ArkRawFutureCf;
import com.gof.ark.entity.ArkRawReleaseCf;
import com.gof.ark.entity.ArkReleaseCf;
import com.gof.entity.RstBoxGoc;
import com.gof.enums.EBoolean;
import com.gof.enums.ECfType;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;
import com.gof.enums.ETiming;
import com.gof.infra.HibernateUtil;

public class ArkDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static Stream<ArkMstRunset> getArkMstRunset(){
		String query = "select a from ArkMstRunset a "
				;
		Query<ArkMstRunset> q = session.createQuery(query, ArkMstRunset.class);
		return q.stream();
	}
	
	public static Stream<ArkBoxMap> getArkBoxMap(){
		String query = "select a from ArkBoxMap a "
				;
		Query<ArkBoxMap> q = session.createQuery(query, ArkBoxMap.class);
		return q.stream();
	}
	
	public static Stream<ArkRawReleaseCf> getRawReleaseCfStream(String driveYm){
		String query = "select a from ArkRawReleaseCf a "
					 + "where a.driveYm  = :bssd "
		;
		Query<ArkRawReleaseCf> q = session.createQuery(query, ArkRawReleaseCf.class);
		q.setParameter("bssd", driveYm);
		return q.stream();
	}
	
	
	public static Stream<ArkRawReleaseCf> getRawReleaseCfGroupByStream(String driveYm, String setlYm){
		String query = "select new com.gof.ark.entity.ArkRawReleaseCf(a.setlYm, a.rsDivId, a.csmGrpCd, a.bemmStcd, a.emmStcd, a.ctrDvcd "
					 + " , a.setlAftPassMmcnt, a.cfKeyId, a.cfType, a.cfTiming,a.outflowYn, sum(a.absCfAmt), sum(a.cfAmt)) "
					 + " from ArkRawReleaseCf a "
					 + " where a.driveYm  = :driveYm "
					 + " and a.setlYm = :setlYm "
					 + " group by a.setlYm, a.rsDivId, a.csmGrpCd, a.bemmStcd, a.emmStcd, a.ctrDvcd, a.setlAftPassMmcnt, a.cfKeyId, a.cfType, a.cfTiming, a.outflowYn"
		;
		Query<ArkRawReleaseCf> q = session.createQuery(query, ArkRawReleaseCf.class);
		q.setParameter("driveYm", driveYm);
		q.setParameter("setlYm", setlYm);
		return q.stream();
	}

	public static Stream<ArkRawEpv> getRawEpvGroupByStream(String bssd, String setlYm){
		String query = "select new com.gof.ark.entity.ArkRawEpv(a.setlYm, a.rsDivId, a.csmGrpCd, a.bemmStcd, a.emmStcd, a.ctrDvcd, a.subKey"
					 + ", sum(a.ttrmBelAmt), sum(a.mm1AfBelAmt), sum(a.mm2AfBelAmt), sum(a.mm3AfBelAmt))"
					 + "from ArkRawEpv a "
					 + "where a.driveYm  = :bssd "
					 + "and a.setlYm = :setlYm "
					 + "group by a.setlYm, a.rsDivId, a.csmGrpCd, a.bemmStcd, a.emmStcd, a.ctrDvcd, a.subKey "
		;
		Query<ArkRawEpv> q = session.createQuery(query, ArkRawEpv.class);
		q.setParameter("bssd", bssd);
		q.setParameter("setlYm", setlYm);
		return q.stream();
	}

	
	
	public static Stream<ArkRawFutureCf> getRawFutureCfGroupByStream(String bssd, String setlYm){
		String query = "select new com.gof.ark.entity.ArkRawFutureCf(a.setlYm, a.rsDivId, a.csmGrpCd, a.bemmStcd, a.emmStcd, a.ctrDvcd"
				+ ", cfKeyId, a.cfType, sum(a.cfAmt), sum(a.absCfAmt), sum(a.pvAmt)) "
				 + "from ArkRawFutureCf a "
				 + "where a.driveYm  = :bssd "
				 + "and a.setlYm = :setlYm "
				 + "group by a.setlYm, a.rsDivId, a.csmGrpCd, a.bemmStcd, a.emmStcd, a.ctrDvcd, a.cfKeyId, a.cfType"
		;
		
		Query<ArkRawFutureCf> q = session.createQuery(query, ArkRawFutureCf.class);
		q.setParameter("bssd", bssd);
		q.setParameter("setlYm", setlYm);
		
		return q.stream();
	
	}
	
	public static Stream<ArkRawEpvDetail> getRawEpvDetailGroupByStream(String bssd, String setlYm){
		String query = "select new com.gof.ark.entity.ArkRawEpvDetail(a.setlYm, a.rsDivId, a.csmGrpCd, a.bemmStcd, a.emmStcd, a.ctrDvcd, a.cfType"
					 + ", sum(a.cfAmt), sum(a.epvAmt)) "
					 + "from ArkRawEpvDetail a "
					 + "where a.driveYm  = :bssd "
					 + "and a.setlYm = :setlYm "
					 + "group by a.setlYm, a.rsDivId, a.csmGrpCd, a.bemmStcd, a.emmStcd, a.ctrDvcd, a.cfType"
		;
		
		Query<ArkRawEpvDetail> q = session.createQuery(query, ArkRawEpvDetail.class);
		q.setParameter("bssd", bssd);
		q.setParameter("setlYm", setlYm);
		
		return q.stream();
	}

	public static Stream<ArkRawEpvDetail> getRawEpvDetailStream(String bssd){
		String query = "select a from ArkRawEpvDetail a "
					 + "where a.driveYm  = :bssd "
		;
		
		Query<ArkRawEpvDetail> q = session.createQuery(query, ArkRawEpvDetail.class);
		q.setParameter("bssd", bssd);
		
		return q.stream();
	}

	public static Stream<ArkRawEpv> getRawEpvStream(String bssd){
		String query = "select a from ArkRawEpv a "
					 + "where a.driveYm  = :bssd "
		;
		
		Query<ArkRawEpv> q = session.createQuery(query, ArkRawEpv.class);
		q.setParameter("bssd", bssd);
		
		return q.stream();
	}

	public static Stream<ArkRawCfEir> getRawCfEirStream(String bssd){
		String query = "select a from ArkRawCfEir a "
					 + "where a.driveYm  = :bssd "
		;
		
		Query<ArkRawCfEir> q = session.createQuery(query, ArkRawCfEir.class);
		q.setParameter("bssd", bssd);
		
		return q.stream();
	}
	
	public static Stream<ArkRawCfEir> getRawCfEirGroupByStream(String bssd, String setlYm){
		String query = "select new com.gof.ark.entity.ArkRawCfEir(a.setlYm, a.rsDivId, a.csmGrpCd, a.bemmStcd, a.emmStcd"
					 + "                                            , a.ctrDvcd, a.setlAftPassMmcnt, a.cfTiming, sum(a.cfAmt)) "
					 + "from ArkRawCfEir a "
					 + "where a.driveYm  = :bssd "
					 + "and a.setlYm = :setlYm "
					 + "group by a.setlYm, a.rsDivId, a.csmGrpCd, a.bemmStcd, a.emmStcd, a.ctrDvcd, a.setlAftPassMmcnt, a.cfTiming"
		;

		Query<ArkRawCfEir> q = session.createQuery(query, ArkRawCfEir.class);
		q.setParameter("bssd", bssd);
		q.setParameter("setlYm", setlYm);
		
		return q.stream();
	}
	
	public static Stream<ArkRawCfEir> getRawCfEirGroupByStream(String bssd, String setlYm, String gocId){
		if(gocId ==null) {
			return getRawCfEirGroupByStream(bssd, setlYm);
		}
		String query = "select new com.gof.ark.entity.ArkRawCfEir(a.setlYm, a.rsDivId, a.csmGrpCd, a.bemmStcd, a.emmStcd"
					 + "                                            , a.ctrDvcd, a.setlAftPassMmcnt, a.cfTiming, sum(a.cfAmt)) "
					 + "from ArkRawCfEir a "
					 + "where a.driveYm  = :bssd "
					 + "and a.setlYm = :setlYm "
					 + "and a.csmGrpCd = :gocId "
					 + "group by a.setlYm, a.rsDivId, a.csmGrpCd, a.bemmStcd, a.emmStcd, a.ctrDvcd, a.setlAftPassMmcnt, a.cfTiming"
		;

		Query<ArkRawCfEir> q = session.createQuery(query, ArkRawCfEir.class);
		q.setParameter("bssd", bssd);
		q.setParameter("setlYm", setlYm);
		q.setParameter("gocId", gocId);
		
		return q.stream();
	}
	
	public static Stream<ArkReleaseCf> getArkReleaseCfStream(String bssd){
		String query = "select a from ArkReleaseCf a "
					 + "where a.baseYymm  = :bssd "
		;
		Query<ArkReleaseCf> q = session.createQuery(query, ArkReleaseCf.class);
		q.setParameter("bssd", bssd);
		return q.stream();
	}	
	
	public static Stream<ArkFwdEpv> getArkFwdEpvStream(String bssd){
		String query = "select a from ArkFwdEpv a "
					 + "where a.baseYymm  = :bssd "
		;
		Query<ArkFwdEpv> q = session.createQuery(query, ArkFwdEpv.class);
		q.setParameter("bssd", bssd);
		return q.stream();
	}	
	
	public static Stream<ArkReleaseCf> getArkReleaseCfGroupByCfType(String bssd){
		String query = "select new com.gof.ark.entity.ArkReleaseCf(a.baseYymm, a.gocId,  a.liabType, a.stStatus, a.endStatus, a.newContYn, a.arkRunsetId"
					 + " , a.runsetId, a.cfType, sum(cfAmt)) "
					 + "from ArkReleaseCf a "
					 + "where a.baseYymm  = :bssd "
					 + "group by a.baseYymm, a.gocId,  a.liabType, a.stStatus, a.endStatus, a.newContYn, a.arkRunsetId, a.runsetId, a.cfType"
		;
		Query<ArkReleaseCf> q = session.createQuery(query, ArkReleaseCf.class);
		q.setParameter("bssd", bssd);
		return q.stream();
	}
	
	public static Stream<ArkReleaseCf> getArkReleaseCfGroupBy(String bssd){
		String query = "select new com.gof.ark.entity.ArkReleaseCf(a.baseYymm, a.gocId,  a.liabType, a.stStatus, a.endStatus, a.newContYn"
					 + ", a.arkRunsetId, a.runsetId, sum(cfAmt)) "
					 + "from ArkReleaseCf a "
					 + "where a.baseYymm  = :bssd "
					 + "group by a.baseYymm, a.gocId,  a.liabType, a.stStatus, a.endStatus, a.newContYn,  a.arkRunsetId, a.runsetId"
		;
		Query<ArkReleaseCf> q = session.createQuery(query, ArkReleaseCf.class);
		q.setParameter("bssd", bssd);
		return q.stream();
	}
	
	public static Stream<ArkFutureCf> getArkFutureCfStream(String baseYymm){
		String query = "select a from ArkFutureCf a "
					 + "where a.baseYymm  = :baseYymm "
		;
		Query<ArkFutureCf> q = session.createQuery(query, ArkFutureCf.class);
		q.setParameter("baseYymm", baseYymm);
		return q.stream();
	}
	
	
	public static Stream<ArkFutureCf> getArkFutureCfStream(String baseYymm, String gocId){
		if(gocId==null) {
			return getArkFutureCfStream(baseYymm);
		}
		String query = "select a from ArkFutureCf a "
					 + "where a.baseYymm  = :baseYymm "
					 + "and a.gocId  = :gocId "
		;
		Query<ArkFutureCf> q = session.createQuery(query, ArkFutureCf.class);
		q.setParameter("baseYymm", baseYymm);
		q.setParameter("gocId", gocId);
		return q.stream();
	}
	
	
	public static Stream<ArkFutureCf> getFutureCfGroupByStream(String bssd){
		String query = "select new com.gof.ark.entity.ArkFutureCf(a.baseYymm, a.gocId,  a.liabType, a.stStatus, a.endStatus, a.newContYn"
					 + ",  a.arkRunsetId, a.runsetId, sum(a.cfAmt), sum(a.epvAmt)) "
					 + "from ArkFutureCf a "
					 + "where a.baseYymm  = :bssd "
					 + "group by a.baseYymm, a.gocId,  a.liabType, a.stStatus, a.endStatus, a.newContYn,  a.arkRunsetId, a.runsetId"
		;
		Query<ArkFutureCf> q = session.createQuery(query, ArkFutureCf.class);
		q.setParameter("bssd", bssd);
		return q.stream();
	}
	
	
	public static Stream<ArkFutureCf> getFutureCfByCfTypeStream(String bssd){
		String query = "select new com.gof.ark.entity.ArkFutureCf(a.baseYymm, a.gocId,  a.liabType, a.stStatus, a.endStatus, a.newContYn"
					 + ",  a.arkRunsetId, a.runsetId, a.cfType, sum(a.cfAmt), sum(a.epvAmt), max(a.setlYm)) "
					 + "from ArkFutureCf a "
					 + "where a.baseYymm  = :bssd "
					 + "group by a.baseYymm, a.gocId,  a.liabType, a.stStatus, a.endStatus, a.newContYn,  a.arkRunsetId, a.runsetId, a.cfType"
		;
		Query<ArkFutureCf> q = session.createQuery(query, ArkFutureCf.class);
		q.setParameter("bssd", bssd);
		return q.stream();
	}
	
	public static Stream<ArkItemRst> getArkItemRstStream(String bssd){
		String query = "select a from ArkItemRst a "
					 + "where a.baseYymm  = :bssd "
		;
		Query<ArkItemRst> q = session.createQuery(query, ArkItemRst.class);
		q.setParameter("bssd", bssd);
		return q.stream();
	}	
	
	public static List<ArkItemRst> getArkItemRst(String bssd){
		String query = "select a from ArkItemRst a "
					 + "where a.baseYymm  = :bssd "
		;
		Query<ArkItemRst> q = session.createQuery(query, ArkItemRst.class);
		q.setParameter("bssd", bssd);
		return q.getResultList();
	}
	
	public static Stream<RstBoxGoc> getRstBoxGocFromArkBox(String bssd){
		String query = "select new com.gof.entity.RstBoxGoc(a.baseYymm, a.gocId,  a.liabType, a.mstRunset, a.calcId, a.stStatus, a.endStatus, a.newContYn, "
					 + "  sum(a.cfAmt), sum(a.prevCfAmt), sum(a.deltaCfAmt), sum(a.boxValue)) "
					 + " from ArkBoxRst a "
					 + "where a.baseYymm  = :bssd "
					 + "group by  a.baseYymm, a.gocId,  a.liabType, a.stStatus, a.endStatus, a.newContYn, a.mstRunset, a.calcId"
		;
		
		Query<RstBoxGoc> q = session.createQuery(query, RstBoxGoc.class);
		q.setParameter("bssd", bssd);
		return q.stream();
	}
}
