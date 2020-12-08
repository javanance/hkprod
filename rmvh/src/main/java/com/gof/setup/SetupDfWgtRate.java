package com.gof.setup;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.gof.dao.DfDao;
import com.gof.dao.MstDao;
import com.gof.dao.RstDao;
import com.gof.entity.DfLv1CurrRate;
import com.gof.entity.DfLv2WghtHis;
import com.gof.entity.DfLv2WghtRate;
import com.gof.entity.RstEpvNgoc;
import com.gof.factory.FacDfWghtRate;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupDfWgtRate {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
//	private static	String vBssd		=EsgConstant.V_BSSD;
	private static	String irCurveId	=GmvConstant.IR_CURVE_ID;
	private static	String gocGroup		=GmvConstant.GOC_GROUP;

	private static Map<Double, Map<String, Double>> curveHisMap = new HashMap<Double, Map<String,Double>>();		//Tenor 蹂� �씪�옄蹂� 湲덈━ 
	private static Set<String> newGocSet = new HashSet<String>();
	

	public static Stream<DfLv2WghtHis> createNew(){
		List<DfLv2WghtHis> rstList = new ArrayList<DfLv2WghtHis>();
		
//		Set<String> newGocSet = RstDao.getRstEpvNewgoc(bssd).stream().map(s->s.getGocId()).collect(toSet());
//		newGocSet.forEach(s->log.info("Set : {}", s));
		
		for(String gocId : getNewGocSet()) {
			rstList.addAll(create(gocId).collect(toList()));
		}
		
		return rstList.stream();
	}
	
	public static Stream<DfLv2WghtHis> createNew(String gocId){
		List<DfLv2WghtHis> rstList = new ArrayList<DfLv2WghtHis>();
		
		if(getNewGocSet().contains(gocId)) {
			rstList.addAll(create(gocId).collect(toList()));
		}
		return rstList.stream();
	}
	public static Stream<DfLv2WghtHis> createConversion(){
		bssd= stBssd;
		return createAll();
	}
	
	public static Stream<DfLv2WghtHis> createAll(){
		List<DfLv2WghtHis> rstList = new ArrayList<DfLv2WghtHis>();

		for(String gocId : PrvdMst.getGocIdList()) {
			rstList.addAll(create(gocId).collect(toList()));
		}
		return rstList.stream();
	}
		
	public static Stream<DfLv2WghtRate> determineNew(){
		return DfDao.getDfLv2WghtHis(bssd).stream().map(s-> FacDfWghtRate.convert(s));
	}

	public static Stream<DfLv2WghtRate> determineNew(String gocId){
		return DfDao.getDfLv2WghtHis(bssd, gocId).stream().map(s->FacDfWghtRate.convert(s));
	}
	
	public static Stream<DfLv2WghtRate> determineAll(){
		return MstDao.getMstGoc().stream().map(s -> DfDao.getDfLv2WghtRate(bssd, s.getGocId()))
										.flatMap(s->s.stream())
										.map(s-> FacDfWghtRate.convert(s) );
	}

	public static Stream<DfLv2WghtRate> determineWithPrevious(){
		return MstDao.getMstGoc().stream().map(s -> DfDao.getPrevDfLv2Wght(bssd, s.getGocId()))
										.flatMap(s->s.stream())
										.map(s-> FacDfWghtRate.convert(s) );
	}

	private static Stream<DfLv2WghtHis> create(String gocId){
			Map<Double, Double> wghtRate = new HashMap<Double, Double>();
	
			Map<String, Double> outEpvMap = RstDao.getRstEpvNgocByGoc(gocId).stream()
												.filter(s-> DateUtil.isGreaterOrEqual(bssd, s.getBaseYymm()))
//												.filter(s->s.getOutflowYn().isTrueFalse())
												.collect(toMap(RstEpvNgoc::getInitCurveYymm, RstEpvNgoc::getOutEpvAmt, (s,u)->s+u));
			
			log.info("outEpvMap : {},{},{}" , gocId,outEpvMap.size(), RstDao.getRstEpvNgocByGoc(gocId).size());
			
				
			if(!outEpvMap.isEmpty()) {	
				for(Map.Entry<Double, Map<String, Double>> curve : getCurevHisMap().entrySet()){
					double tenor = curve.getKey();
					double sum=0.0;
					double wghtSum =0.0;
					for(Map.Entry<String, Double> epvEntry : outEpvMap.entrySet()) {
						sum = sum + epvEntry.getValue() ;																	
						wghtSum = wghtSum + epvEntry.getValue() * curve.getValue().getOrDefault(epvEntry.getKey(), 0.0) ;	
//						log.info("wgth Sum : {},{},{},{}", sum, wghtSum, epvEntry.getKey(), curve.getValue());
					}
					wghtRate.put(tenor, sum==0 ? 0.0: wghtSum / sum);
				}
				return FacDfWghtRate.build(bssd, gocId, wghtRate);
			}
			else {
				return Stream.empty();
			}
		}

	private static Map<Double, Map<String, Double>> getCurevHisMap() {
		String befBssd = DateUtil.addMonthToString(bssd, -12);
		
		if(curveHisMap.isEmpty()) {
	
			curveHisMap= DfDao.getDfLv1CurrRateStream(irCurveId)
//						.filter(s->DateUtil.isGreaterOrEqual(s.getBaseYymm(), stBssd))		
						.filter(s->DateUtil.isGreaterOrEqual(s.getBaseYymm(), befBssd))		
						.collect(groupingBy(DfLv1CurrRate::getCfMonthNum, toMap(DfLv1CurrRate::getBaseYymm, DfLv1CurrRate::getAppliedRate)));
		}
		return curveHisMap;
	}

	private static Set<String> getNewGocSet() {
		if(newGocSet.isEmpty()) {
			newGocSet = RstDao.getRstEpvNgoc(bssd).stream().map(s->s.getGocId()).collect(toSet());
		}
		return newGocSet;
	}


//	private static List<DfLv2WghtHis> build(String bssd, String gocId, Map<Double, Double> wghtInt) {
//		List<DfLv2WghtHis> rstList = new ArrayList<DfLv2WghtHis>();
//		
//		for(Map.Entry<Double, Double> entry : wghtInt.entrySet()) {
//			rstList.add(DfLv2WghtHis.builder()
//					.baseYymm(bssd)
//					.gocId(gocId)
//					.cfMonthNum(entry.getKey())
////					.initYymm(bssd)
//					.wghtRate(entry.getValue())
////					.wghtFwdRate(.entry)
//					.lastModifiedBy("GMV")
//					.lastModifiedDate(LocalDateTime.now())
//					.build()
//					);
//		}
//		return rstList;
//	}
//
//	private static DfLv2WghtRate convert(DfLv2WghtHis wghtHis) {
//		return DfLv2WghtRate.builder()
//				.gocId(wghtHis.getGocId())
//				.cfMonthNum(wghtHis.getCfMonthNum())
////				.initYymm(bssd)
//				.wghtRate(wghtHis.getWghtRate())
////				.wghtFwdRate(.entry)
//				.lastModifiedBy("GMV")
//				.lastModifiedDate(LocalDateTime.now())
//				.build();
//	}

}