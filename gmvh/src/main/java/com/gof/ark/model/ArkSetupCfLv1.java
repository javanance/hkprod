package com.gof.ark.model;

import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.ark.dao.ArkDao;
import com.gof.ark.entity.ArkMstRunset;
import com.gof.dao.DfDao;
import com.gof.entity.CfLv1Goc;
import com.gof.entity.DfLv1CurrRate;
import com.gof.enums.ESlidingType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ArkSetupCfLv1 {
	private static	String bssd				=GmvConstant.BSSD;
	private static	String stBssd			=GmvConstant.ST_BSSD;
	private static	String irCurveId		=GmvConstant.IR_CURVE_ID;
	private static	String newContRateDiv	=GmvConstant.NEW_CONT_RATE;
	
	private static Map<String, Map<Double, Double>> curveByYymmMap = new HashMap<String, Map<Double,Double>>();
	
	public static Stream<CfLv1Goc> createConversion() {
			bssd= stBssd;
			return createCurrent();
	}
	
	public static Stream<CfLv1Goc> createAll() {
		return createAll(null);
//		log.info("qqqqqqqqqqqqqqqqqqqqqq : {},{},{}" );
//		return createAll("2292_2019_1");
	}
	
	public static Stream<CfLv1Goc> createCurrent() {
		return createCurrent(null);
	}
	
	public static Stream<CfLv1Goc> createNewCont() {
		return createNewCont(null);
	}

//	public static Stream<CfLv1Goc> createPrev() {
//		return createPrev(null);
//	}

	
	public static Stream<CfLv1Goc> createAll(String gocId) {
		return  ArkDao.getArkMstRunset()
					.filter(s->s.getEirAplyYn().isTrueFalse())
//					.peek(s-> log.info("aaaa : {}", s.getArkRunsetId()))
					.flatMap(s-> create(gocId, s));
	}

	public static Stream<CfLv1Goc> createCurrent(String gocId) {
		return  ArkDao.getArkMstRunset()
				.filter(s-> s.getEirAplyYn().isTrueFalse())
				.filter(s->!s.getNewContYn().isTrueFalse())
				.filter(s-> s.getSetlYmSlidingType().equals(ESlidingType.EOP))
				.flatMap(s-> create(gocId, s));
	}

//	public static Stream<CfLv1Goc> createPrev(String gocId) {
//		return  ArkDao.getArkMstRunset()
//				.filter(s->s.getEirAplyYn().isTrueFalse())
//				.filter(s-> !s.getNewContYn().isTrueFalse())
//				.filter(s->s.getSlidingType().equals(ESlidingType.BOP))
//				.flatMap(s-> create(gocId, s));
//	}

	public static Stream<CfLv1Goc> createNewCont(String gocId) {
		return  ArkDao.getArkMstRunset()
						.filter(s-> s.getEirAplyYn().isTrueFalse())
						.filter(s-> s.getNewContYn().isTrueFalse())
						.flatMap(s-> create(gocId, s));
	}
	
	private static Stream<CfLv1Goc> create(String gocId, ArkMstRunset arkRunset) {
		String setlYm	= arkRunset.getSetlYm(stBssd,bssd);
		
		String cfYymm	= arkRunset.getIrCurveSlidingType().equals(ESlidingType.GOC_INIT)? PrvdMst.getMstGoc(gocId).getInitCurveYymm()
									:arkRunset.getCashFlowYymm(stBssd, bssd);
		
//		int adj = arkRunset.getSetlYmSlidingType().equals(ESlidingType.EOP)? 0 : DateUtil.monthBetween(stBssd, bssd) - arkRunset.getTenorAdjNum();
		
		int tenorAdjNum = arkRunset.getTenorAdjNum(stBssd, bssd);
		
		log.info("qqqq : {},{},{},{}", setlYm, cfYymm, tenorAdjNum, arkRunset.getArkRunsetId());
		
		return  ArkDao.getRawCfEirGroupByStream(bssd, setlYm, gocId)
					  .filter(cf->!cf.getCsmGrpCd().equals("NA"))
					  .filter(s-> s.getRsDivId().equals(arkRunset.getRsDivId())) 	
//					  .peek(s->log.info("aaaa : {},{},{}", s.toString()))
					  .map(cf-> FacArkCfLv1.convert(bssd, cf, arkRunset, tenorAdjNum, getCurveMap(cfYymm)))
				;
	}	
	
	private static Map<Double, Double> getCurveMap(String curveYymm){
		if(!curveByYymmMap.containsKey(curveYymm)) {
			curveByYymmMap.put(curveYymm,  DfDao.getDfLv1CurrRate(curveYymm, irCurveId).stream()
												.collect(toMap(DfLv1CurrRate::getCfMonthNum, DfLv1CurrRate::getAppliedRate))
							  );
		}
		return curveByYymmMap.get(curveYymm);
	}
}
