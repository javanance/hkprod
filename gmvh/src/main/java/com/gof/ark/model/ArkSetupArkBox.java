package com.gof.ark.model;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.ark.dao.ArkDao;
import com.gof.ark.entity.ArkBoxMap;
import com.gof.ark.entity.ArkBoxRst;
import com.gof.entity.RstBoxGoc;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ArkSetupArkBox {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	
	public static Stream<ArkBoxRst> create() {
	
//		Map<String, List<MstCalc>> boxMap = ArkDao.getArkBoxMap().collect(groupingBy(ArkBoxMap::getMapKey, mapping(ArkBoxMap::getMstCalc, toList())));
		Map<String, List<ArkBoxMap>> boxMap = ArkDao.getArkBoxMap().collect(groupingBy(ArkBoxMap::getMapKey, toList()));
		
		boxMap.entrySet().forEach(s-> log.info("aaa : {},{}", s.getKey(), s.getValue()));
		return ArkDao.getArkItemRstStream(bssd)
//						.peek(s-> log.info("bbb : {},{}", s.getMapKey()))
						.map(s-> FacArkBoxRst.createFromItemRst(bssd, s, boxMap.get(s.getMapKey())))
						.flatMap(s->s.stream())
						;
	}
	
	
	public static Stream<ArkBoxRst> createConversion() {
		bssd =stBssd;
		return create();
	}
	
	public static Stream<RstBoxGoc> createConversionRstBoxGoc() {
		bssd =stBssd;
		return ArkDao.getRstBoxGocFromArkBox(bssd);
	}
	
	public static Stream<RstBoxGoc> createRstBoxGoc() {
		return ArkDao.getRstBoxGocFromArkBox(bssd);
	}
	
}
