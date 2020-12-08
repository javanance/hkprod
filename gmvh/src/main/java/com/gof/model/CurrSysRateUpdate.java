package com.gof.model;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.dao.CfDao;
import com.gof.dao.DfDao;
import com.gof.entity.CfLv4Df;
import com.gof.entity.DfLv4Eir;
import com.gof.enums.ELiabType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CurrSysRateUpdate {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	private static	String currEirType	=GmvConstant.CURR_EIR_TYPE;
	
	
	private static Map<String, Map<ELiabType, Double>> eirMap = new HashMap<String, Map<ELiabType,Double>>();

	public static Stream<CfLv4Df> updateCurrEir(){
		return  PrvdMst.getGocIdList().stream().flatMap(s->updateCurrEir(s));
	}
	
	public static Stream<CfLv4Df> updateCurrEir(String gocId){
		if(currEirType.equals("EACH")) {
			return updateCurrEirByLiabType(gocId);
		}
		else if(currEirType.equals("LRC")) {
			return updateCurrLrcEir(gocId);
		}
		else {
			return updateCurrEirAll(gocId);
		}
	}
	
	
	private static Stream<CfLv4Df> updateCurrEirAll(String gocId){
		return CfDao.getCfLv4DfStream(bssd, gocId).map(s->s.updateEir(getEirMap(gocId)));
	}
	
	private static Stream<CfLv4Df> updateCurrEirByLiabType(String gocId){
		Map<ELiabType, Double> eirMap = getEirMap(gocId);
		eirMap.entrySet().forEach(s-> log.info("aaa : {},{}", s.getKey(), s.getValue()));
		
		return CfDao.getCfLv4DfStream(bssd, gocId).map(s->s.updateEirByLiabType(eirMap));
	}

	private static Stream<CfLv4Df> updateCurrLrcEir(String gocId){
		Map<ELiabType, Double> eirMap = getEirMap(gocId);
		eirMap.entrySet().forEach(s-> log.info("aaa : {},{}", s.getKey(), s.getValue()));
		
		return CfDao.getCfLv4DfStream(bssd, gocId).map(s->s.updateLrcEir(eirMap));
	}
	
	private static Map<ELiabType, Double> getEirMap(String gocId){
		if(eirMap.isEmpty()) {
			eirMap = DfDao.getEir(bssd).stream().collect(groupingBy(DfLv4Eir::getGocId, toMap(DfLv4Eir::getLiabType, DfLv4Eir::getEir)));
		}
		return eirMap.getOrDefault(gocId, new HashMap<ELiabType, Double>());
	}
}