package com.gof.setup;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.dao.DfDao;
import com.gof.dao.RawDao;
import com.gof.entity.CfLv1Goc;
import com.gof.entity.DfLv1CurrRate;
import com.gof.entity.MstRunset;
import com.gof.entity.RaLv1;
import com.gof.entity.RawCfLic;
import com.gof.entity.RawRaIbnr;
import com.gof.enums.EBoolean;
import com.gof.enums.ECoa;
import com.gof.enums.ECompound;
import com.gof.enums.ELiabType;
import com.gof.enums.ESlidingType;
import com.gof.enums.ETiming;
import com.gof.factory.FacCf;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupRaLv1Ibnr {
	private static	String bssd				=GmvConstant.BSSD;
	private static	String stBssd			=GmvConstant.ST_BSSD;
	private static	String irCurveId		=GmvConstant.IR_CURVE_ID;
	
	private static  Map<String, Map<Double, Double>> curveByYymmMap = new HashMap<String, Map<Double,Double>>();
	
	public static Stream<RaLv1> createConversion() {
		bssd= stBssd;
		return create()
//				.filter(s->s.getLiabType().equals(ELiabType.LIC)).limit(1).peek(s-> log.info("aaaa : {},{}", s.toString()))
				;
	}

	public static Stream<RaLv1> create() {
		return create(null);
	}
	
	public static Stream<RaLv1> create(String gocId) {
		return  PrvdMst.getMstRunsetList().stream()
						.filter(s->s.getCoaId().equals(ECoa.RA))
						.filter(s->s.getLiabType().equals(ELiabType.LIC))
						.flatMap(s-> create(gocId, s))
						;
	}
	
	private static Stream<RaLv1> create(String gocId,  MstRunset mstRunset) {
		String rsDivId = mstRunset.getRsDivId();
		String driveYm = mstRunset.getDriveYmSlidingType().equals(ESlidingType.EOP)?  bssd : stBssd	;
		
		String setlYm  = mstRunset.getSetlYmSlidingType().equals(ESlidingType.EOP) ?  bssd : mstRunset.getGenYymm(stBssd, bssd);
		String cfYymm  = mstRunset.getIrCurveSlidingType().equals(ESlidingType.EOP)?  bssd : mstRunset.getCashFlowYymm(stBssd, bssd) ;
		
		if(DateUtil.isGreaterOrEqual(bssd, setlYm)) {
			int adj = DateUtil.monthBetween(cfYymm, bssd);
			
			log.info("RA IBNR LV1 : {},{},{},{},{}, {}", setlYm, cfYymm, adj, rsDivId, mstRunset.getRunsetId(), gocId ); 
			
			Map<String, Double> raAmtMap =  RawDao.getRawRaIbnrByRsDivStream(driveYm, setlYm, rsDivId, gocId)
//						.filter(s->s.getSetlAftPassMmcnt()==6)
						  .collect(toMap(RawRaIbnr::getCsmGrpCd, cf-> getDiscount(cf, getCurveMap(getCurveYymm(cf.getCsmGrpCd(),  mstRunset))), (s,u)-> s+u));
												  
			return raAmtMap.entrySet().stream().map(s-> build(bssd, s.getKey(), mstRunset, s.getValue(), setlYm));			  		  
		}
		return Stream.empty();
	}
	
	
	private static String getCurveYymm(String gocId, MstRunset mstRunset) {
		if(mstRunset.getIrCurveSlidingType().equals(ESlidingType.GOC_INIT)) {
			return getInitCurveYymm(gocId);
		}
		return mstRunset.getCashFlowYymm(stBssd, bssd);			//DEFAULT!!!
	}
	
	private static String getInitCurveYymm(String gocId) {
		return PrvdMst.getMstGoc(gocId).getInitCurveYymm();
	}
	
	private static Map<Double, Double> getCurveMap(String curveYymm){
		if(!curveByYymmMap.containsKey(curveYymm)) {
			curveByYymmMap.put(curveYymm,  DfDao.getDfLv1CurrRate(curveYymm, irCurveId).stream()
												.collect(toMap(DfLv1CurrRate::getCfMonthNum, DfLv1CurrRate::getAppliedRate))
							  );
		}
		return curveByYymmMap.get(curveYymm);
	}
	
	
	private static double getDiscount(RawRaIbnr cf, Map<Double, Double> currCurveMap) {

		double cfMonthNum = cf.getSetlAftPassMmcnt()  + cf.getCfTiming().getAdj()  ;
		double disRate = currCurveMap.getOrDefault(cfMonthNum, 0.0);
		
		double df = ECompound.Annualy.getDf(disRate, cfMonthNum/12.0);
		double epvAmt = cf.getRaAmt() * df;
		
//		log.info("aaaaaaaa : {},{},{},{},{},{},{}", cf.getCsmGrpCd(), cf.getSetlAftPassMmcnt(),cf.getCfTiming(), cf.getRaAmt(), disRate, df, epvAmt);
		return epvAmt;
		
	}
	
	private static RaLv1 build(String bssd, String gocId, MstRunset mstRunset, double raAmt, String setlYm) {
		return RaLv1.builder()
						.baseYymm(bssd)
						.liabType(ELiabType.LIC)
						.gocId(gocId)
						.runsetId(mstRunset.getRunsetId())
						.deltaGroup(mstRunset.getDeltaGroup())
						.raAmt(raAmt)
						.srcBaseYymm(setlYm)
						.rsDivId(mstRunset.getRsDivId())
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}
}