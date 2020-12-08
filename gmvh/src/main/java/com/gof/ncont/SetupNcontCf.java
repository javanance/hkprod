package com.gof.ncont;

import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.dao.DfDao;
import com.gof.dao.MstDao;
import com.gof.dao.RawDao;
import com.gof.entity.DfLv1CurrRate;
import com.gof.entity.MstRunset;
import com.gof.entity.NcontCf;
import com.gof.enums.ECoa;
import com.gof.infra.GmvConstant;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupNcontCf {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
//	private static	String vBssd		=EsgConstant.V_BSSD;
	private static	String irCurveId	=GmvConstant.IR_CURVE_ID;
	
	private static	List<Integer> groupTenorList = GmvConstant.GROUP_TENOR_LIST;
	private static	Map<ECoa, String> newRunsetMap = new HashMap<ECoa, String>();
	
	
	public static Stream<NcontCf> createAll() {
		return Stream.concat(createFirst(), Stream.concat(createSecond(), createThird()));
	}
	
	public static Stream<NcontCf> createCurrent() {
		return createNewCont(bssd);
	}
	
	public static Stream<NcontCf> createFirst() {
		String baseYymm =  DateUtil.addMonthToString(stBssd, 1);
		
		return createNewCont(baseYymm);
	}
	
	public static Stream<NcontCf> createSecond() {
		String baseYymm =  DateUtil.addMonthToString(stBssd, 2);
		return createNewCont(baseYymm);
	}

	public static Stream<NcontCf> createThird() {
		String baseYymm =  DateUtil.addMonthToString(stBssd, 3);
		return createNewCont(baseYymm);
	}
	
	public static Stream<NcontCf> createConversion() {
		bssd=stBssd;
		return createAll();
	}

	private static Stream<NcontCf> createNewCont(String baseYymm, String rsDivId) {
		String curveYymm = DateUtil.addMonthToString(baseYymm, -1);
		
		Map<Double, Double> curveMap =DfDao.getDfLv1CurrRate(curveYymm, irCurveId).stream()
											.collect(toMap(DfLv1CurrRate::getCfMonthNum, DfLv1CurrRate::getAppliedRate));
		
		return  RawDao.getRawCfNcontStream(baseYymm, getNewRunset())
					  .map(s-> s.convertAndUpdate(bssd, curveMap, groupTenorList))
					  ;
	}
	
	private static Stream<NcontCf> createNewCont(String baseYymm) {
		String curveYymm = DateUtil.addMonthToString(baseYymm, -1);
		
		Map<Double, Double> curveMap =DfDao.getDfLv1CurrRate(curveYymm, irCurveId).stream()
											.collect(toMap(DfLv1CurrRate::getCfMonthNum, DfLv1CurrRate::getAppliedRate));
		
		
				
		return  RawDao.getRawCfNcontStream(baseYymm, getNewRunset())
//		return  RawDao.getRawCfNcontStream(baseYymm)
//					  .filter(s->getNewRunsetList().contains(s.getRsDivId()))
//					  .filter(s->s.getCtrPolno().equals(ctr))
					  .map(s-> s.convertAndUpdate(bssd, curveMap, groupTenorList))
					  ;
					  
	}
	
	private static String getNewRunset() {
		if(newRunsetMap.isEmpty()) {
			newRunsetMap = MstDao.getMstRunset().stream()
//								  .filter(s->s.getNewContYn().isTrueFalse())
								  .filter(s->s.getRunsetType().isNewContYn())
								  
								  .filter(s->s.getCoaId().equals(ECoa.EPV))
								  .collect(toMap(MstRunset::getCoaId, MstRunset::getRsDivId, (s,u)->s));
		}
		return newRunsetMap.get(ECoa.EPV);
	}
	
}