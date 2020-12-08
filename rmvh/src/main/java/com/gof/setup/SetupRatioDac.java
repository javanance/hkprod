package com.gof.setup;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.gof.dao.RawDao;
import com.gof.entity.RatioDac;
import com.gof.entity.RawRatioDac;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupRatioDac {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	private static Map<String, List<RawRatioDac>> rawRatioDacMap = new HashMap<String, List<RawRatioDac>>();
	
	public static Stream<RatioDac> create(){
		List<RatioDac> rstList = new ArrayList<RatioDac>();
		return rstList.stream();
	}

	
	public static Stream<RatioDac> createFromRaw() {
		List<RatioDac> rstList = new ArrayList<RatioDac>();
		
		for(String gocId : PrvdMst.getGocIdList()) {
			if(getRawRatioDacMap().containsKey(gocId)) {
				rstList.addAll(createFromRaw(gocId).collect(toList()));
			}
			else {
				rstList.add(build(bssd, gocId, GmvConstant.CALC_TYPE_DAC_RATIO, 1.0, "No RawRatio"));
			}
		}
		return rstList.stream();
	}
	
	public static Stream<RatioDac> createFromRaw(String gocId) {
		return RawDao.getRawRatioDac(bssd).stream().filter(s->s.getGocId().equals(gocId)).map(s-> build(bssd, s));
	}
	
	
	private static Map<String, List<RawRatioDac>> getRawRatioDacMap(){
		if(rawRatioDacMap.isEmpty()) {
			rawRatioDacMap = RawDao.getRawRatioDac(bssd).stream().collect(groupingBy(RawRatioDac::getGocId, toList()));
		}
		return rawRatioDacMap;
	}
	
	
	private static RatioDac build(String bssd, RawRatioDac rawRatio) {
		String remark =rawRatio.getRemark();
		double ratio = rawRatio.getReleaseRatio();
		
		Set<String> cfGocIdSet = PrvdMst.getCfGocIdSet();
		if(!cfGocIdSet.contains(rawRatio.getGocId())) {
			remark = "Vanished: No CF";
			ratio = 1.0;
		}
		
		return RatioDac.builder()
				.baseYymm(bssd)
				.gocId(rawRatio.getGocId())
				.calcType(rawRatio.getCalcType())
				.prevCovUnit(rawRatio.getPrevCovUnit())
				.covUnitRelease(rawRatio.getReleasedCovUnit())
				.currCovUnit(rawRatio.getCurrCovUnit())
				.releaseRatio(ratio)
				.remark(remark)
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
	}
	
	
	private static RatioDac build(String bssd, String gocId, String calcType, double ratio, String remark) {
		return RatioDac.builder()
				.baseYymm(bssd)
				.gocId(gocId)
				.calcType(calcType)
				.prevCovUnit(0.0)
				.covUnitRelease(0.0)
				.currCovUnit(0.0)
				.releaseRatio(ratio)
				.remark(remark)
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
	}
	
	
}