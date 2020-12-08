package com.gof.ark.model;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.ark.dao.ArkDao;
import com.gof.ark.entity.ArkFwdEpv;
import com.gof.ark.entity.ArkItemRst;
import com.gof.ark.entity.ArkMstRunset;
import com.gof.dao.DfDao;
import com.gof.entity.DfLv2EirNewgoc;
import com.gof.entity.DfLv4Eir;
import com.gof.enums.ELiabType;
import com.gof.infra.GmvConstant;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ArkSetupItemRst {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	
	public static Stream<ArkItemRst> createConversion() {
		bssd= stBssd;
		return create();
	}
	
	public static Stream<ArkItemRst> create() {
		List<ArkItemRst> rstList = new ArrayList<ArkItemRst>();
	
		rstList.addAll(createFromEpv().collect(toList()));
		
		rstList.addAll(createFromFutureCf().collect(toList()));
		rstList.addAll(createFromFutureCfAll().collect(toList()));
		rstList.addAll(createEpvFromFutureCf().collect(toList()));
		rstList.addAll(createAccreteFromFutureCf().collect(toList()));
		rstList.addAll(createFromReleaseCf().collect(toList()));
		rstList.addAll(createFromReleaseCfAll().collect(toList()));
		
//		rstList.forEach(s->log.info("aaa : {}", s.toString()));
		return rstList.stream();
	}
	
	
	private static Stream<ArkItemRst> createFromEpv() {
		int slidingNum = DateUtil.monthBetween(stBssd, bssd);
		
//		Map<String, ArkMstRunset> runsetMap = ArkDao.getArkMstRunset().collect(toMap(ArkMstRunset::getRunsetId, Function.identity()));
		Map<String, ArkMstRunset> runsetMap = ArkDao.getArkMstRunset().collect(toMap(ArkMstRunset::getArkRunsetId, Function.identity()));
		
		runsetMap.entrySet().forEach(s->log.info("qqq : {},{}", s.getKey(), s.getValue()));
		
		log.info("zzz : {},{}", slidingNum);
		
		Stream<ArkFwdEpv> arkFwdEpv = ArkDao.getArkFwdEpvStream(bssd)
											.filter(s-> s.getFwdNum()==0 || s.getFwdNum() == slidingNum );
//											.filter(s-> s.getFwdNum()==0 || s.getFwdNum() == slidingNum - runsetMap.get(s.getArkRunsetId()).getTenorAdjNum());
		
		return arkFwdEpv.map(s-> FacArkItemRst.createFromFwdEpv(bssd, s));
	}
	
	
	private static Stream<ArkItemRst> createFromReleaseCf() {
		return  ArkDao.getArkReleaseCfGroupByCfType(bssd).map(s->FacArkItemRst.createFromReleaseCf(bssd, s));
	}
	
	private static Stream<ArkItemRst> createFromReleaseCfAll() {
		return  ArkDao.getArkReleaseCfGroupBy(bssd).map(s->FacArkItemRst.createFromReleaseCfAll(bssd, s));
	}

	private static Stream<ArkItemRst> createFromFutureCf() {
		return ArkDao.getFutureCfByCfTypeStream(bssd).map(s->FacArkItemRst.createFromFutureCf(bssd, s));
	}
	
	private static Stream<ArkItemRst> createEpvFromFutureCf() {
		return ArkDao.getFutureCfByCfTypeStream(bssd).map(s->FacArkItemRst.createEpvFromFutureCf(bssd, s));
	}
	
	
	private static Stream<ArkItemRst> createAccreteFromFutureCf() {
		Map<String, Double> eirMap = DfDao.getLv4Eir(stBssd).stream().filter(s->s.getLiabType().equals(ELiabType.LRC)).collect(toMap(DfLv4Eir::getGocId, DfLv4Eir::getEir));
		Map<String, Double> newEirMap = DfDao.getEirNewgoc(bssd).stream().collect(toMap(DfLv2EirNewgoc::getGocId, DfLv2EirNewgoc::getEir));
		
		for(Map.Entry<String, Double> entry : newEirMap.entrySet()) {
			eirMap.putIfAbsent(entry.getKey(), entry.getValue());
		}
		
		return ArkDao.getFutureCfByCfTypeStream(bssd).map(s->FacArkItemRst.createAccreteFromFutureCf(bssd, s, eirMap));
	}
	
	
	private static Stream<ArkItemRst> createFromFutureCfAll() {
		return ArkDao.getFutureCfGroupByStream(bssd).map(s->FacArkItemRst.createFromFutureCfAll(bssd, s));
	}
}
