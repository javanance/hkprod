package com.gof.setup;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.gof.dao.DfDao;
import com.gof.dao.RatioDao;
import com.gof.dao.RawDao;
import com.gof.entity.DfLv2InitRate;
import com.gof.entity.DfLv2WghtRate;
import com.gof.entity.RatioCovUnit;
import com.gof.entity.RatioLv2;
import com.gof.entity.RawRatioCsm;
import com.gof.enums.ECompound;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupRatioLv2 {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static String csmCalcType   =GmvConstant.CALC_TYPE_COV_UNIT;
	private static String csmIntType    =GmvConstant.CALC_TYPE_CSM_INT;
	
	private static Map<String, Map<Double,Double>> wghtRateMap = new HashMap<String, Map<Double,Double>>();
	
	public static Stream<RatioLv2> create(){
		return PrvdMst.getGocIdList().stream().flatMap(s->create(s));
	}
	
	
	public static Stream<RatioLv2> create(String gocId){
		return Stream.concat(createFromRatioCsm(gocId), createCsmIntFactor(gocId));
	}
	
	public static Stream<RatioLv2> createFromRatioCsm(String gocId) {
		return RatioDao.getCovUnit(bssd).stream()
						.filter(s->s.getGocId().equals(gocId))
						.filter(s->s.getCalcType().equals(csmCalcType))
						.map(s-> build(bssd, s.getGocId(),  s.getCalcType(), s.getReleaseRatio(), ""));
	}
	
	public static Stream<RatioLv2> createCsmIntFactor(String gocId) {
		double tenor = (double)DateUtil.monthBetween(stBssd, bssd);
		Map<Double, Double> wghtRateMap = getWghtRateMap(gocId, tenor);
		
		double intRate      = wghtRateMap.getOrDefault(tenor, 0.0);
		double releaseRatio = 1.0/ ECompound.Annualy.getDf(intRate, tenor/12.0) -1.0;
		
		return Stream.of(build(bssd, gocId, csmIntType, releaseRatio, String.valueOf(intRate)));
	}

	private static Map<Double, Double> getWghtRateMap(String gocId, double tenor) {
		if(wghtRateMap.isEmpty()) {
			wghtRateMap = DfDao.getDfLv2WghtRate().stream()
							   .filter(s->s.getCfMonthNum()==tenor)
							   .collect(groupingBy(DfLv2WghtRate::getGocId, toMap(DfLv2WghtRate::getCfMonthNum, DfLv2WghtRate::getWghtRate)));
		}
		return wghtRateMap.getOrDefault(gocId, new HashMap<Double, Double>());
	}
	
	private static RatioLv2 build(String bssd, String gocId, String ratioId, double releaseRatio, String remark) {
		return RatioLv2.builder()
						.baseYymm(bssd)
						.gocId(gocId)
						.ratioId(ratioId)
						.releaseRatio(releaseRatio)
						.remark(remark)
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}
	
}