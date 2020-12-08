package com.gof.setup;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import com.gof.dao.RawDao;
import com.gof.entity.CfLv3Real;
import com.gof.entity.MstRunset;
import com.gof.entity.RawCash;
import com.gof.enums.ECoa;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupCfLv3Cash {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	
	
	public static Stream<CfLv3Real> create(){
		return create(null);
	}
	
	public static Stream<CfLv3Real> create(String gocId){
		return  PrvdMst.getMstRunsetList().stream()
						.filter(s->s.getCoaId().equals(ECoa.CASH))
						.flatMap(s-> create(gocId, s));
	}
	
	private static Stream<CfLv3Real> create(String gocId, MstRunset runset){
		return RawDao.getRawCashStream(bssd, gocId)
						.filter(s->s.getRsDivId().equals(runset.getRsDivId()))
						.map(s-> convert(s, runset))
						;
	}
	
	private static CfLv3Real convert(RawCash cash, MstRunset runset) {
		return CfLv3Real.builder()
					.baseYymm(cash.getBaseYymm())
					.gocId(cash.getGocId())
					.runsetId(runset.getRunsetId())
					.liabType(cash.getLiabType())
					.stStatus(PrvdMst.getContStatus(cash.getStStatus()))
					.endStatus(PrvdMst.getContStatus(cash.getEndStatus()))
					.newContYn(cash.getNewContYn())
					.cfKeyId(cash.getCfId())
					.cfType(cash.getCfType())
					.outflowYn(cash.getOutflowYn())
					.cfStartYymm(cash.getCfStartYymm())
					.cfEndYymm(cash.getCfEndYymm())
					.cfAmt(cash.getCfAmt())
					.lastModifiedBy(GmvConstant.getLastModifier())
					.lastModifiedDate(LocalDateTime.now())
					.build();
	}
}