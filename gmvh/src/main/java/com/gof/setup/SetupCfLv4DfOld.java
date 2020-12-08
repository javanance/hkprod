package com.gof.setup;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao.CfDao;
import com.gof.dao.DfDao;
import com.gof.entity.CfLv1Goc;
import com.gof.entity.CfLv4Df;
import com.gof.entity.DfLv3Flat;
import com.gof.factory.FacCfDf;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupCfLv4DfOld {
	private static	String bssd				=GmvConstant.BSSD;
	private static	String stBssd			=GmvConstant.ST_BSSD;
	private static	String vBssd			=GmvConstant.V_BSSD;
	
	private static Map<String, Map<Double, DfLv3Flat>> dfMap = new HashMap<String, Map<Double,DfLv3Flat>>();
	
	public static Stream<CfLv4Df> createConversion() {
		bssd =stBssd;
		return createClose();
	}
	
	public static Stream<CfLv4Df> createClose() {
		List<CfLv4Df> rstList = new ArrayList<CfLv4Df>();
		
		for(String gocId: PrvdMst.getGocIdList()) {
			rstList.addAll(createClose(gocId).collect(toList()));
		}
		return rstList.stream();
	}

	public static Stream<CfLv4Df> createNew() {
		List<CfLv4Df> rstList = new ArrayList<CfLv4Df>();
		
		for(String gocId: PrvdMst.getGocIdList()) {
			rstList.addAll(createNew(gocId).collect(toList()));
		}
		return rstList.stream();
	}
	
	public static Stream<CfLv4Df> createDelta() {
		List<CfLv4Df> rstList = new ArrayList<CfLv4Df>();
		
		for(String gocId: PrvdMst.getGocIdList()) {
			rstList.addAll(buildDelta(gocId));
		}
		return rstList.stream();
	}
	
	public static Stream<CfLv4Df> createClose(String gocId) {
//		Map<Double, DfLv3Flat> df = DfDao.getDfLv3FlatStream(bssd, gocId).collect(toMap(DfLv3Flat::getCfMonthNum, Function.identity()));
		Map<String, DfLv3Flat> df = DfDao.getDfLv3FlatStream(bssd, gocId).collect(toMap(DfLv3Flat::getDfLv3Pk, Function.identity()));
					
		return  CfDao.getCfLv1GocSettleStream(bssd,  gocId)
						 .map(s-> FacCfDf.build(s, df.getOrDefault(s.getDfLv3Pk(), new DfLv3Flat())));
	}

	public static Stream<CfLv4Df> createNew(String gocId) {
//		Map<Double, DfLv3Flat> df = DfDao.getDfLv3FlatStream(bssd, gocId).collect(toMap(DfLv3Flat::getCfMonthNum, Function.identity()));
		Map<String, DfLv3Flat> df = DfDao.getDfLv3FlatStream(bssd, gocId).collect(toMap(DfLv3Flat::getDfLv3Pk, Function.identity()));
					
		return  CfDao.getCfLv1GocNewContStream(bssd,  gocId)
						 .map(s-> FacCfDf.build(s, df.getOrDefault(s.getDfLv3Pk(), new DfLv3Flat())));
	}
	
	public static Stream<CfLv4Df> createDelta(String gocId) {
		return buildDelta(gocId).stream();
	}
	
	private static List<CfLv4Df> buildDelta(String gocId){
		List<CfLv4Df> rstList = new ArrayList<CfLv4Df>();
		
		Map<Double, DfLv3Flat> df = DfDao.getDfLv3FlatStream(bssd, gocId).collect(toMap(DfLv3Flat::getCfMonthNum, Function.identity()));
			
		Map<String, List<CfLv1Goc>> cfMap = CfDao.getCfLv1GocStream(bssd, gocId)
													.collect(groupingBy(s->s.getDeltaCashFlowPk(), TreeMap::new, toList())); 
			
			
		for(Map.Entry<String, List<CfLv1Goc>> entry : cfMap.entrySet()) {
			if(!entry.getValue().isEmpty()) {
				Double cfMonthNum  = entry.getValue().get(0).getCfMonthNum();
				rstList.addAll(FacCfDf.buildFromGeneration(entry.getValue(), df.get(cfMonthNum) ));		
			}
		}
		return rstList;
	}

}