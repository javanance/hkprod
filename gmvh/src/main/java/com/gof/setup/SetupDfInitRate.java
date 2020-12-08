package com.gof.setup;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.dao.DfDao;
import com.gof.entity.DfLv1CurrRate;
import com.gof.entity.DfLv2EirNewgoc;
import com.gof.entity.DfLv2InitRate;
import com.gof.entity.DfLv4Eir;
import com.gof.entity.MstGoc;
import com.gof.enums.ELiabType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupDfInitRate {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String irCurveId	=GmvConstant.IR_CURVE_ID;
	private static 	String currEirType	= GmvConstant.CURR_EIR_TYPE;

	private static Map<String, Double> newEirMap 				= new HashMap<String, Double>();
	private static Map<String, List<DfLv1CurrRate>> irCurveMap 	= new HashMap<String, List<DfLv1CurrRate>>();
	
	public static Stream<DfLv2InitRate> createConversion(){
			List<DfLv2InitRate> rstList = new ArrayList<DfLv2InitRate>();
	
			log.info("create SetupDfInitRate: {}", stBssd);
	//		getGocMstList().forEach(s->log.info("gocMst : {},{}", s.getGocId()));
	
			List<MstGoc> gocMstList = PrvdMst.getGocList().stream().filter(s->DateUtil.isGreaterOrEqual(bssd, s.getInitYymm())).collect(toList());
			
			Map<String, Map<ELiabType, Double>> eirMap = DfDao.getEir(stBssd).stream()
															  .collect(groupingBy(DfLv4Eir::getGocId, toMap(DfLv4Eir::getLiabType,DfLv4Eir::getEir)));
			
			double eir=0.0;
			double lrcEir=0.0;
			double licEir =0.0;
			
			for(MstGoc aa : gocMstList) {
				String initCurveYymm = aa.getInitCurveYymm();
	
				if(eirMap.containsKey(aa.getGocId())) {
					eir 	= eirMap.get(aa.getGocId()).getOrDefault(ELiabType.ALL, 0.0);
					lrcEir 	= eirMap.get(aa.getGocId()).getOrDefault(ELiabType.LRC, 0.0);
					licEir 	= eirMap.get(aa.getGocId()).getOrDefault(ELiabType.LIC, 0.0);
				}
				
				rstList.addAll(build(aa.getGocId(), initCurveYymm, getIrCurve(initCurveYymm), eir, lrcEir, licEir));	
			}
			return rstList.stream();
		}

//	public static Stream<DfLv2InitRate> createNew(){
//		List<DfLv2InitRate> rstList = new ArrayList<DfLv2InitRate>();
//		List<String> usedGocList = DfDao.getUsedGocInDfLv2InitRate();				
//		
//		List<MstGoc> gocMstList = PrvdMst.getGocList().stream().filter(s->DateUtil.isGreaterOrEqual(bssd, s.getInitYymm())).collect(toList());
//		
//		for(MstGoc aa : gocMstList) {
//			String initCurveYymm = aa.getInitCurveYymm();
//			if(!usedGocList.contains(aa.getGocId())) {													//to exclude existed goc in InitRate!!
//				rstList.addAll(build(aa.getGocId(), initCurveYymm, getIrCurve(initCurveYymm)));	
//			}
//		}
//		return rstList.stream();
//	}
	
	public static Stream<DfLv2InitRate> createNew(){
		List<String> usedGocList = DfDao.getUsedGocInDfLv2InitRate();				

		return PrvdMst.getGocList().stream()
						.filter(s->DateUtil.isGreaterOrEqual(bssd, s.getInitYymm()))
						.filter(s->!usedGocList.contains(s.getGocId()))
						.flatMap(s-> createNew(s.getGocId()));
	}
	
	public static Stream<DfLv2InitRate> createNew(String gocId){
		MstGoc goc = PrvdMst.getMstGoc(gocId);
		String initCurveYymm = goc.getInitCurveYymm();
		
		if(initCurveYymm==null) {
			log.error("There is no MstGoc or InitYymm for Id : {}. Check MST_GOC Table ",  gocId);
			System.exit(1);
		}
		
		double eir = getNewEirMap().getOrDefault(gocId, 0.0);
		
		return build(gocId, initCurveYymm, getIrCurve(initCurveYymm), eir, eir, eir).stream();
	}

//	public static Stream<DfLv2InitRate> createAll(){
//		List<DfLv2InitRate> rstList = new ArrayList<DfLv2InitRate>();
//
//		log.info("create SetupDfInitRate: {}", stBssd);
////		getGocMstList().forEach(s->log.info("gocMst : {},{}", s.getGocId()));
//
//		List<MstGoc> gocMstList = PrvdMst.getGocList().stream().filter(s->DateUtil.isGreaterOrEqual(bssd, s.getInitYymm())).collect(toList());
//		
//		return PrvdMst.getGocList().stream()
//				.filter(s->DateUtil.isGreaterOrEqual(bssd, s.getInitYymm()))
//				.flatMap(s-> createNew(s.getGocId()));
//		
//		
//		Map<String, Double> newEirMap = DfDao.getEirNewgoc().stream().collect(toMap(DfLv2EirNewgoc::getGocId,DfLv2EirNewgoc::getEir));
//		
//		double eir=0.0;
//		double licEir =0.0;
//		
//		for(MstGoc aa : gocMstList) {
//			if(newEirMap.containsKey(aa.getGocId())) {
//				eir = newEirMap.get(aa.getGocId());
//				licEir = newEirMap.get(aa.getGocId());
//			}
//			String initCurveYymm = aa.getInitCurveYymm();
//			rstList.addAll(build(aa.getGocId(), initCurveYymm, getIrCurve(initCurveYymm), eir, licEir));	
//		}
//		return rstList.stream();
//	}
	
	private static List<DfLv1CurrRate> getIrCurve(String initCurveYymm){
		if(!irCurveMap.containsKey(initCurveYymm)) {
			irCurveMap.put(initCurveYymm,  DfDao.getDfLv1CurrRate(initCurveYymm, irCurveId));
		}	
		return irCurveMap.get(initCurveYymm);
	}

	private static Map<String, Double> getNewEirMap() {
		if(newEirMap.isEmpty()) {
			newEirMap = DfDao.getEirNewgoc(bssd).stream().collect(toMap(DfLv2EirNewgoc::getGocId, DfLv2EirNewgoc::getEir, (s,u)->s));
		}
		return newEirMap;
	}



	private static List<DfLv2InitRate> build(String gocId, String initCurveYymm, List<DfLv1CurrRate> irCurve, double eir, double lrcEir, double licEir) {
		List<DfLv2InitRate> rstList = new ArrayList<DfLv2InitRate>();
		for(DfLv1CurrRate bb : irCurve) {
			rstList.add(DfLv2InitRate.builder()
							.gocId(gocId)
							.cfMonthNum(bb.getCfMonthNum())
							.initCurveYymm(initCurveYymm)					
							.initRate(bb.getAdjRfRate())
							.initFwdRate(bb.getAdjRfFwdRate())
							.initEir(eir)
							.initLrcEir(lrcEir)
							.initLicEir(licEir)
							.lastModifiedBy(GmvConstant.getLastModifier())
							.lastModifiedDate(LocalDateTime.now())
							.build()
						);	
		}
		return rstList;
	}
}