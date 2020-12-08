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

import com.gof.dao.RaDao;
import com.gof.entity.MstRunset;
import com.gof.entity.RaLv1;
import com.gof.entity.RaLv2Delta;
import com.gof.enums.ECoa;
import com.gof.enums.ELiabType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupRaLv2Delta {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	
	
	public static Stream<RaLv2Delta> create(){
		return create(null);
	}
	
	public static Stream<RaLv2Delta> create(String gocId){
		return Stream.concat(createAlone(gocId), createDelta(gocId));
	}
	
	private static Stream<RaLv2Delta> createAlone(String gocId){
		List<String> aloneDeltaList = PrvdMst.getMstRunsetList(ECoa.RA).stream()
											.filter(s->s.getPriorDeltaGroup()==null)
											.map(s->s.getDeltaGroup()).collect(toList())
											;
		
		return RaDao.getRaLv1(bssd, gocId).stream().filter(s->aloneDeltaList.contains(s.getDeltaGroup())).map(s-> buildClose(bssd, s));
	}

	private static Stream<RaLv2Delta> createDelta(String gocId){
//		List<RaLv2Delta> rstList = new ArrayList<RaLv2Delta>();
//		
//		Map<String, MstRunset> priorDeltaMap = PrvdMst.getMstRunsetList(ECoa.RA).stream()
//												   .filter(s->s.getPriorDeltaGroup()!=null)
//												   .collect(toMap(MstRunset::getDeltaGroup, Function.identity(), (s,u)->s));
//		
//		Map<String, List<RaLv1>> raDeltaMap  = RaDao.getRaLv1(bssd, gocId).stream().collect(groupingBy(RaLv1::getGocId, toList()));
//		
//		
//		for(Map.Entry<String, List<RaLv1>> entry : raDeltaMap.entrySet()) {
//			rstList.addAll(build(bssd, entry.getKey(), raDeltaMap.get(entry.getKey()), priorDeltaMap));
//		}
//		
//		return rstList.stream();
		return Stream.concat(createDelta(gocId, ELiabType.LRC), createDelta(gocId, ELiabType.LIC));
	}
	
	private static Stream<RaLv2Delta> createDelta(String gocId, ELiabType liabType){
		List<RaLv2Delta> rstList = new ArrayList<RaLv2Delta>();
		
		Map<String, MstRunset> priorDeltaMap = PrvdMst.getMstRunsetList(ECoa.RA).stream()
												   .filter(s->s.getLiabType().equals(liabType))
												   .filter(s->s.getPriorDeltaGroup()!=null)
												   .collect(toMap(MstRunset::getDeltaGroup, Function.identity(), (s,u)->s));
		
		Map<String, List<RaLv1>> raDeltaMap  = RaDao.getRaLv1(bssd, gocId).stream().filter(s->s.getLiabType().equals(liabType)).collect(groupingBy(RaLv1::getGocId, toList()));
		
		
		for(Map.Entry<String, List<RaLv1>> entry : raDeltaMap.entrySet()) {
			rstList.addAll(build(bssd, entry.getKey(), raDeltaMap.get(entry.getKey()), priorDeltaMap));
		}
		
		return rstList.stream();
	}
	

	private static RaLv2Delta buildClose(String bssd, RaLv1 ra) {
		return RaLv2Delta.builder()
				.baseYymm(bssd)
				.liabType(ra.getLiabType())
				.gocId(ra.getGocId())
				.runsetId(ra.getRunsetId())
				.deltaGroup(ra.getDeltaGroup())
				.raAmt(ra.getRaAmt())
				.prevRaAmt(0.0)
				.deltaRaAmt(ra.getRaAmt())
				.priorDeltaGroup("")
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
		
	}
	
	private static List<RaLv2Delta> build(String bssd, String gocId, List<RaLv1> raList, Map<String, MstRunset> priorDeltaMap) {
		List<RaLv2Delta> rstList = new ArrayList<RaLv2Delta>();
		
		Map<String, Double> deltaMap = raList.stream().collect(toMap(RaLv1::getDeltaGroup, RaLv1::getRaAmt, (s,u)->s+u));
			
			
		for(Map.Entry<String, MstRunset> entry : priorDeltaMap.entrySet()) {
			String deltaGroup  		= entry.getKey();
			String priorDeltaGroup  = entry.getValue().getPriorDeltaGroup();
			
			double currAmt  = deltaMap.getOrDefault(deltaGroup, 0.0);
			double priorAmt = deltaMap.getOrDefault(priorDeltaGroup, 0.0);
				
			rstList.add( RaLv2Delta.builder()
									.baseYymm(bssd)
									.gocId(gocId)
									.liabType(entry.getValue().getLiabType())
									.runsetId(entry.getValue().getRunsetId())
									.deltaGroup(deltaGroup)
									.raAmt(currAmt)
									.prevRaAmt(priorAmt)
									.deltaRaAmt(currAmt - priorAmt)
									.priorDeltaGroup(priorDeltaGroup)
									.lastModifiedBy(GmvConstant.getLastModifier())
									.lastModifiedDate(LocalDateTime.now())
									.build()
							);
				
		}
		return rstList; 
	}
	
//	public static Stream<RaLv2Delta> create(String gocId){
//			
//		Map<String, String> priorDeltaMap = PrvdMst.getMstRunsetList(ECoa.RA).stream()
//														.filter(s->s.getPriorDeltaGroup()!=null)
//														.collect(toMap(MstRunset::getDeltaGroup, MstRunset::getPriorDeltaGroup));
//
////		key : gocId+ deltaGruop!!!!
//		Map<String, Double> raDeltaMap  = RaDao.getRaLv1(bssd, gocId).stream().collect(toMap(RaLv1::getDeltaGroupPk, RaLv1::getRaAmt, (s,u)->s+u));
//		
//		return RaDao.getRaLv1(bssd, gocId).stream().map(s-> build(bssd, s, priorDeltaMap.get(s.getDeltaGroup()), raDeltaMap));
//		
//	}
		

//	private static RaLv2Delta build(String bssd,  RaLv1 ra, String priorDeltaGroup, Map<String, Double> prevMap) {
//		double prevRaAmt = prevMap.getOrDefault(ra.getDeltaGroupPk(priorDeltaGroup), 0.0);		//key : gocId+ pridorDeltaGruop!!!!
//		
//		return RaLv2Delta.builder()
//				.baseYymm(bssd)
//				.gocId(ra.getGocId())
//				.runsetId(ra.getRunsetId())
//				.deltaGroup(ra.getDeltaGroup())
//				.raAmt(ra.getRaAmt())
//				.prevRaAmt(prevRaAmt)
//				.deltaRaAmt(ra.getRaAmt()- prevRaAmt)
//				.priorDeltaGroup(priorDeltaGroup)
//				.lastModifiedBy(GmvConstant.getLastModifier())
//				.lastModifiedDate(LocalDateTime.now())
//				.build();
//	}
	

//	private static List<MstRunset> getRunsetList() {
//		if(runsetList.isEmpty()) {
//			runsetList =MstDao.getMstRunset().stream().filter(s->s.getCoaType().equals(ECoa.RA)).collect(toList());
//		}
//		return runsetList;
//	}
	
//	private static List<MstGoc> getMstGocList() {
//		String gocId ="2292_Y2016_ETC";
//		if(mstGocList.isEmpty()) {
//			mstGocList = MstDao.getMstGoc().stream()
//								.filter(s->s.getUseYn().isTrueFalse())
//								.filter(s->s.getGocGroup().equals(gocGroup))
////								.filter(s->s.getGocId().equals(gocId))
//								.collect(toList());	
//		}
//		return mstGocList;
//	}
	
	
}