package com.gof.setup;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.ark.dao.ArkDao;
import com.gof.ark.entity.ArkRawEpv;
import com.gof.dao.RawDao;
import com.gof.entity.CfLv3Real;
import com.gof.entity.MstRunset;
import com.gof.entity.RawCash;
import com.gof.entity.RawCf;
import com.gof.enums.ECoa;
import com.gof.enums.ERunsetType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupCfLv3Cash {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	
	private static Set<String> aliveGocSet = new HashSet<String>();
	private static Map<ERunsetType, String> runsetIdMap = new HashMap<ERunsetType, String>();
	
	public static Stream<CfLv3Real> create(){
		return create(null);
	}
	
	public static Stream<CfLv3Real> create(String gocId){
		
		return  PrvdMst.getMstRunsetList().stream()
						.filter(s->s.getCoaId().equals(ECoa.CASH))
						.flatMap(s-> create(gocId, s))
						;
	}
	
	private static Stream<CfLv3Real> create(String gocId, MstRunset runset){
//		return RawDao.getRawCashStream(bssd, gocId).filter(s->s.getRsDivId().equals(runset.getRsDivId())).map(s-> convert(s, runset));
		return RawDao.getRawCashGroupByStream(bssd, gocId).filter(s->s.getRsDivId().equals(runset.getRsDivId())).map(s-> convert(s, runset));
	}
	
	private static CfLv3Real convert(RawCash cash, MstRunset runset) {
		
		String runsetId = getAliveGocList().contains(cash.getGocId())? runset.getRunsetId() : getReaExRunsetId(); 		
		
		return CfLv3Real.builder()
					.baseYymm(cash.getBaseYymm())
					.gocId(cash.getGocId())
//					.runsetId(runset.getRunsetId())
					.runsetId(runsetId)
					.liabType(cash.getLiabType())
					.stStatus(PrvdMst.getContStatus(cash.getStStatus()))
					.endStatus(PrvdMst.getContStatus(cash.getEndStatus()))
					.newContYn(cash.getNewContYn())
//					.cfId(cash.getCfId())
					.cfKeyId(cash.getCfKeyId())
					.cfType(cash.getCfType())
					.outflowYn(cash.getOutflowYn())
//					.cfStartYymm(cash.getCfStartYymm())
//					.cfEndYymm(cash.getCfEndYymm())
					.cfAmt(cash.getCfAmt())
					.lastModifiedBy(GmvConstant.getLastModifier())
					.lastModifiedDate(LocalDateTime.now())
					.build();
	}
	
	private static Set<String> getAliveGocList(){
		if(aliveGocSet.isEmpty()) {
			aliveGocSet = RawDao.getRawCfStream(bssd).map(RawCf::getCsmGrpCd).collect(toSet());
			aliveGocSet.addAll(ArkDao.getRawEpvStream(bssd).map(ArkRawEpv::getCsmGrpCd).collect(toSet()));
		}
		return aliveGocSet;
	}
	

	private static String getReaExRunsetId() {
		if(runsetIdMap.isEmpty()) {
			runsetIdMap = PrvdMst.getMstRunsetList().stream().collect(toMap(MstRunset::getRunsetType, MstRunset::getRunsetId, (s,u)->s));
		}
		return runsetIdMap.getOrDefault(ERunsetType.SERVICE_RELEASE_EX, "REAL_EX");
	}
	
}