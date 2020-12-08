package com.gof.setup;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.gof.dao.DfDao;
import com.gof.dao.RawDao;
import com.gof.entity.DfLv1CurrRate;
import com.gof.entity.RawIntRate;
import com.gof.infra.GmvConstant;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupDfCurrRate {
	private static	String bssd			= GmvConstant.BSSD;
	private static	String stBssd		= GmvConstant.ST_BSSD;
	private static	String vBssd		= GmvConstant.V_BSSD;
	private static	String irCurveId	= GmvConstant.IR_CURVE_ID;
	private static	int maxRateTenor	= GmvConstant.MAX_RATE_TENOR;
	
	private static Map<String, Map<Double,Double>> rawRateMap = new HashMap<String, Map<Double,Double>>();
	private static Map<String, Map<Double,Double>> dfMap = new HashMap<String, Map<Double,Double>>();
	
	public static Stream<DfLv1CurrRate> createConversion(){
		Function<RawIntRate, Double> adjFn = s-> s.getAdjRfRate();
		return RawDao.getRawIntRate(DateUtil.addMonthToString(stBssd, -1), stBssd).stream().map(s->build(s, adjFn));
	}
	
	public static Stream<DfLv1CurrRate> createArkSyncConversion(){
		Function<RawIntRate, Double> adjFn = s-> getArkSyncRate(s);
		return RawDao.getRawIntRate(DateUtil.addMonthToString(stBssd, -1), stBssd).stream().map(s-> build(s, adjFn));
	}
	
	public static Stream<DfLv1CurrRate> appendConversion(){
		return IntStream.rangeClosed(0, DateUtil.monthBetween(stBssd, bssd))
						.mapToObj(s-> DateUtil.addMonthToString(stBssd, s))
						.peek(s-> log.info("append int Rate for : {}", s))
						.flatMap(s-> append(s))
						;
	}

	public static Stream<DfLv1CurrRate> create(){
		Function<RawIntRate, Double> adjFn = s-> s.getAdjRfRate();
		return RawDao.getRawIntRate(stBssd, bssd).stream().map(s->build(s, adjFn));
	}
	
	public static Stream<DfLv1CurrRate> createArkSync(){
		Function<RawIntRate, Double> adjFn = s-> getArkSyncRate(s);
		return RawDao.getRawIntRate(stBssd, bssd).stream().map(s-> build(s, adjFn));
	}
	
	public static Stream<DfLv1CurrRate> append(){
		return IntStream.rangeClosed(1, DateUtil.monthBetween(stBssd, bssd))
						.mapToObj(s-> DateUtil.addMonthToString(stBssd, s))
						.peek(s-> log.info("append int Rate for : {}", s))
						.flatMap(s-> append(s))
						;
	}
	
	private static DfLv1CurrRate build(RawIntRate rawRate, Function<RawIntRate, Double> adjFn) {
			double spotRate = adjFn.apply(rawRate);
			
			return DfLv1CurrRate.builder()
					.baseYymm(rawRate.getBaseYymm())
					.irCurveId(rawRate.getIrCurveId())
					.cfMonthNum(rawRate.getCfMonthNum())
					.rfRate(rawRate.getRfRate())
					.spread(rawRate.getSpread())
					.adjRfRate(spotRate)
					.adjRfFwdRate(rawRate.getAdjRfFwdRate())
					.lastModifiedBy(GmvConstant.getLastModifier())
					.lastUpdateDate(LocalDateTime.now())
					.build();
	}

	private static double getArkSyncRate(RawIntRate rawRate) {
		Map<Double, Double>  dfMap = getDfMap(rawRate.getBaseYymm());
//		log.info("aaaa :  {},{}", rawRate.getCfMonthNum());
//		log.info("bbb :  {},{}", dfMap.size());
		double currDf = dfMap.get(rawRate.getCfMonthNum());
		double spotRate	 = Math.pow(currDf, -12.0 / rawRate.getCfMonthNum()) - 1.0;
		return spotRate;
	}
	
	
	private static Stream<DfLv1CurrRate> append(String bssd){
		List<DfLv1CurrRate> rstList = new ArrayList<DfLv1CurrRate>();
		
		Map<String, Double> maxTenorMap = RawDao.getRawIntRate(bssd).stream()					
												.collect(toMap(RawIntRate::getIrCurveId, RawIntRate::getCfMonthNum, (s,u)-> Math.max(s, u)));
		
		Map<String, DfLv1CurrRate> lastRateMap = DfDao.getDfLv1CurrRateByDateStream(bssd)
													  .filter(s-> s.getCfMonthNum().equals(maxTenorMap.getOrDefault(s.getIrCurveId(), 0.1)))
													  .collect(toMap(DfLv1CurrRate::getIrCurveId, Function.identity(), (s,u)-> s));
		
		for(Map.Entry<String, DfLv1CurrRate> entry : lastRateMap.entrySet()) {
			int k = entry.getValue().getCfMonthNum().intValue();
			if(k < maxRateTenor ) {
				for(int i = 1  ; i <= 2* (maxRateTenor - k) ; i++) {
					rstList.add(DfLv1CurrRate.builder()
							.baseYymm(bssd)
							.irCurveId(entry.getKey())
							.cfMonthNum(k + i/2.0)
							.rfRate(entry.getValue().getRfRate())
							.spread(entry.getValue().getSpread())
							.adjRfRate(entry.getValue().getAdjRfRate())
							.adjRfFwdRate(entry.getValue().getAdjRfFwdRate())
							.lastModifiedBy(GmvConstant.getLastModifier())
							.lastUpdateDate(LocalDateTime.now())
							.build()
							);
				}
			}
		}
		return rstList.stream();
	}

	private static Map<String, Map<Double, Double>> getFwdRateMap(){
		if(rawRateMap.isEmpty()) {
//			rawRateMap = RawDao.getRawIntRate(stBssd, bssd).stream().collect(groupingBy(RawIntRate::getBaseYymm, toMap(RawIntRate::getCfMonthNum, RawIntRate::getAdjRfFwdRate)));
			String startBssd =DateUtil.addMonthToString(stBssd, -1);
			rawRateMap = RawDao.getRawIntRate(startBssd, bssd).stream().collect(groupingBy(RawIntRate::getBaseYymm, toMap(RawIntRate::getCfMonthNum, RawIntRate::getAdjRfFwdRate)));
		}
		return rawRateMap;
	}
	private static Map<Double, Double> getDfMap(String baseYymm){
		if(dfMap.isEmpty()) {
			
			for(Map.Entry<String, Map<Double,Double>> entry : getFwdRateMap().entrySet()) {
				Map<Double, Double> tempMap = new HashMap<Double, Double>();
				Map<Double, Double> fwdRateMap = entry.getValue();
				
				double fwdDf =1.0;
				double fwdRate =0.0;
				
				for(int i=1; i<=  fwdRateMap.size(); i++)		{
					double tenor = i/2.0;
					fwdRate = fwdRateMap.getOrDefault(tenor, fwdRate);
					
					if(i % 2 ==0) {
						fwdDf = fwdDf * Math.pow(1+ fwdRate, -1.0 / 12.0);
						tempMap.put(tenor, fwdDf);
					}
					else {
						tempMap.put(tenor, fwdDf * Math.pow(1+ fwdRate, -1.0 / 24.0));
					}
				}
				dfMap.put(entry.getKey(), tempMap);
			}
		}
		return dfMap.get(baseYymm);
	}
}