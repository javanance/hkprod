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
import com.gof.entity.RatioCovUnit;
import com.gof.entity.RawRatioCsm;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupRatioCsm {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static Map<String, List<RawRatioCsm>> rawRatioCsmMap = new HashMap<String, List<RawRatioCsm>>();
	
	
	public static Stream<RatioCovUnit> create(){
		return Stream.empty();
	}
	
	public static Stream<RatioCovUnit> createFromRaw() {
		List<RatioCovUnit> rstList = new ArrayList<RatioCovUnit>();
		for(String gocId : PrvdMst.getGocIdList()) {
			if(getRawRatioCsmMap().containsKey(gocId)) {
				rstList.addAll(createFromRaw(gocId).collect(toList()));
			}
			else {
				rstList.add(build(bssd, gocId, GmvConstant.CALC_TYPE_COV_UNIT, 1.0, "No RawRatio"));
			}
		}
		return rstList.stream();
	}
	
	public static Stream<RatioCovUnit> createFromRaw(String gocId) {
		return getRawRatioCsm(gocId).stream().map(s-> build(bssd, s));
	}
	
	
	private static List<RawRatioCsm> getRawRatioCsm(String gocId){
		if(rawRatioCsmMap.isEmpty()) {
			rawRatioCsmMap = RawDao.getRawRatioCsm(bssd).stream().collect(groupingBy(RawRatioCsm::getGocId, toList()));
		}
		return rawRatioCsmMap.getOrDefault(gocId, new ArrayList<RawRatioCsm>());
	}
	
	private static Map<String, List<RawRatioCsm>> getRawRatioCsmMap(){
		if(rawRatioCsmMap.isEmpty()) {
			rawRatioCsmMap = RawDao.getRawRatioCsm(bssd).stream().collect(groupingBy(RawRatioCsm::getGocId, toList()));
		}
		return rawRatioCsmMap;
	}
	
	private static RatioCovUnit build(String bssd, RawRatioCsm rawRatio) {
		String remark =rawRatio.getRemark();
		double ratio = rawRatio.getReleaseRatio();
		
		Set<String> cfGocIdSet = PrvdMst.getCfGocIdSet();
		if(!cfGocIdSet.contains(rawRatio.getGocId())) {
			remark = "Vanished: No CF Goc";
			ratio  = 1.0;
		}
		
		return RatioCovUnit.builder()
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
	
	private static RatioCovUnit build(String bssd, String gocId, String calcType, double ratio, String remark) {
		return RatioCovUnit.builder()
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