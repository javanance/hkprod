package com.gof.setup;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao.TvogDao;
import com.gof.entity.MstRunset;
import com.gof.entity.TvogLv1;
import com.gof.entity.TvogLv2Delta;
import com.gof.enums.ECoa;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupTvogLv2Delta {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	private static List<MstRunset> runsetList =new ArrayList<MstRunset>();
	
	public static Stream<TvogLv2Delta> create(){
		return create(null);
	}
	
	public static Stream<TvogLv2Delta> create(String gocId){
		return Stream.concat(createAlone(gocId), createDelta(gocId));
	}
	
	private static Stream<TvogLv2Delta> createAlone(String gocId){
		List<String> aloneDeltaList = PrvdMst.getMstRunsetList(ECoa.TVOG).stream()
											.filter(s->s.getPriorDeltaGroup()==null)
											.map(s->s.getDeltaGroup()).collect(toList());
		
		return TvogDao.getTvogLv1(bssd, gocId).stream().filter(s->aloneDeltaList.contains(s.getDeltaGroup())).map(s-> buildClose(bssd, s));
	}

	private static Stream<TvogLv2Delta> createDelta(String gocId){
		List<TvogLv2Delta> rstList = new ArrayList<TvogLv2Delta>();
		
		Map<String, MstRunset> priorDeltaMap = PrvdMst.getMstRunsetList(ECoa.TVOG).stream()
												   .filter(s->s.getPriorDeltaGroup()!=null)
												   .collect(toMap(MstRunset::getDeltaGroup, Function.identity(), (s,u)->s));
		
		Map<String, List<TvogLv1>> tvogDeltaMap  = TvogDao.getTvogLv1(bssd, gocId).stream()
														  .collect(groupingBy(TvogLv1::getGocId, toList()));
		
		
		for(Map.Entry<String, List<TvogLv1>> entry : tvogDeltaMap.entrySet()) {
			rstList.addAll(build(bssd, entry.getKey(), tvogDeltaMap.get(entry.getKey()), priorDeltaMap));
		}
		
		return rstList.stream();
	}
	

	private static TvogLv2Delta buildClose(String bssd, TvogLv1 tvog) {
		return TvogLv2Delta.builder()
				.baseYymm(bssd)
				.gocId(tvog.getGocId())
				.runsetId(tvog.getRunsetId())
				.deltaGroup(tvog.getDeltaGroup())
				.tvogAmt(tvog.getTvogAmt())
				.prevTvogAmt(0.0)
				.deltaTvogAmt(tvog.getTvogAmt())
				.priorDeltaGroup("")
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
	}
	
	private static List<TvogLv2Delta> build(String bssd, String gocId, List<TvogLv1> tvogList, Map<String, MstRunset> priorDeltaMap) {
		List<TvogLv2Delta> rstList = new ArrayList<TvogLv2Delta>();
		
		Map<String, Double> deltaMap = tvogList.stream().collect(toMap(TvogLv1::getDeltaGroup, TvogLv1::getTvogAmt, (s,u)->s+u));
			
		for(Map.Entry<String, MstRunset> entry : priorDeltaMap.entrySet()) {
			String deltaGroup  		= entry.getKey();
			String priorDeltaGroup  = entry.getValue().getPriorDeltaGroup();
			
			double currAmt  = deltaMap.getOrDefault(deltaGroup, 0.0);
			double priorAmt = deltaMap.getOrDefault(priorDeltaGroup, 0.0);
				
			rstList.add( TvogLv2Delta.builder()
									.baseYymm(bssd)
									.gocId(gocId)
									.runsetId(entry.getValue().getRunsetId())
									.deltaGroup(deltaGroup)
									.tvogAmt(currAmt)
									.prevTvogAmt(priorAmt)
									.deltaTvogAmt(currAmt - priorAmt)
									.priorDeltaGroup(priorDeltaGroup)
									.lastModifiedBy(GmvConstant.getLastModifier())
									.lastModifiedDate(LocalDateTime.now())
									.build()
							);
				
		}
		return rstList; 
	}
	
	
}