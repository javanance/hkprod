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

import com.gof.dao.WaterfallDao;
import com.gof.entity.ElLv1;
import com.gof.entity.ElLv2Delta;
import com.gof.entity.MstRunset;
import com.gof.enums.ECoa;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupElLv2Delta {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	
	
	public static Stream<ElLv2Delta> create(){
		return create(null);
	}
	
	public static Stream<ElLv2Delta> create(String gocId){
		return Stream.concat(createAlone(gocId), createDelta(gocId));
	}
	
	private static Stream<ElLv2Delta> createAlone(String gocId){
		List<String> aloneDeltaList = PrvdMst.getMstRunsetList(ECoa.EL).stream().filter(s->s.getPriorDeltaGroup()==null).map(s->s.getDeltaGroup()).collect(toList());
		
		return WaterfallDao.getElLv1(bssd, gocId).stream().filter(s->aloneDeltaList.contains(s.getDeltaGroup())).map(s-> buildClose(bssd, s));
	}

	private static Stream<ElLv2Delta> createDelta(String gocId){
		List<ElLv2Delta> rstList = new ArrayList<ElLv2Delta>();
		
		Map<String, MstRunset> priorDeltaMap = PrvdMst.getMstRunsetList(ECoa.EL).stream()
												   .filter(s->s.getPriorDeltaGroup()!=null)
												   .collect(toMap(MstRunset::getDeltaGroup, Function.identity(), (s,u)->s));
		
		Map<String, List<ElLv1>> elDeltaMap  = WaterfallDao.getElLv1(bssd, gocId).stream()
													.collect(groupingBy(ElLv1::getGocId, toList()));
		
		
		for(Map.Entry<String, List<ElLv1>> entry : elDeltaMap.entrySet()) {
			rstList.addAll(build(bssd, entry.getKey(), elDeltaMap.get(entry.getKey()), priorDeltaMap));
		}
		
		return rstList.stream();
	}
	

	private static ElLv2Delta buildClose(String bssd, ElLv1 el) {
		return ElLv2Delta.builder()
				.baseYymm(bssd)
				.gocId(el.getGocId())
				.runsetId(el.getRunsetId())
				.deltaGroup(el.getDeltaGroup())
				.elAmt(el.getElAmt())
				.prevElAmt(0.0)
				.deltaElAmt(el.getElAmt())
				.priorDeltaGroup("")
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
		
	}
	
	private static List<ElLv2Delta> build(String bssd, String gocId, List<ElLv1> elList, Map<String, MstRunset> priorDeltaMap) {
		List<ElLv2Delta> rstList = new ArrayList<ElLv2Delta>();
		
		Map<String, Double> deltaMap = elList.stream().collect(toMap(ElLv1::getDeltaGroup, ElLv1::getElAmt, (s,u)->s+u));
			
			
		for(Map.Entry<String, MstRunset> entry : priorDeltaMap.entrySet()) {
			String deltaGroup  		= entry.getKey();
			String priorDeltaGroup  = entry.getValue().getPriorDeltaGroup();
			
			double currAmt  = deltaMap.getOrDefault(deltaGroup, 0.0);
			double priorAmt = deltaMap.getOrDefault(priorDeltaGroup, 0.0);
				
			rstList.add( ElLv2Delta.builder()
									.baseYymm(bssd)
									.gocId(gocId)
									.runsetId(entry.getValue().getRunsetId())
									.deltaGroup(deltaGroup)
									.elAmt(currAmt)
									.prevElAmt(priorAmt)
									.deltaElAmt(currAmt - priorAmt)
									.priorDeltaGroup(priorDeltaGroup)
									.lastModifiedBy(GmvConstant.getLastModifier())
									.lastModifiedDate(LocalDateTime.now())
									.build()
							);
				
		}
		return rstList; 
	}
	
}