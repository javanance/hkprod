package com.gof.ncont;

import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao.MstDao;
import com.gof.dao.NcontDao;
import com.gof.dao.RawDao;
import com.gof.entity.MstRunset;
import com.gof.entity.NcontRstEpv;
import com.gof.entity.NcontRstFlat;
import com.gof.entity.NcontRstRa;
import com.gof.entity.NcontRstTvog;
import com.gof.enums.ECoa;
import com.gof.factory.FacNcont;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupNcontRst {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	
	private static	Map<ECoa, String> newRunsetMap = new HashMap<ECoa, String>();
	
	public static Stream<NcontRstFlat> createFlat() {
		
		Map<String, Double> raMap   =  NcontDao.getNcontRa(bssd)
											.filter(s-> s.getRaCalcType().equals("NATIVE"))
											.collect(toMap(NcontRstRa::getCtrPolno, NcontRstRa::getRaAmt, (s,u)->s+u));
		
		Map<String, Double> tvogMap =  NcontDao.getNcontTvog(bssd)
											.filter(s-> s.getTvogCalcType().equals("NATIVE"))
											.collect(toMap(NcontRstTvog::getCtrPolno, NcontRstTvog::getTvogAmt, (s,u)->s+u));
											;
		return NcontDao.getNcontEpv(bssd).map(s-> FacNcont.build(s, raMap, tvogMap));
	}
	
	public static Stream<NcontRstRa> createRa() {
		String rsDivId = getNewRunset(ECoa.RA);
		return  RawDao.getRawRa(bssd, rsDivId).map(s -> FacNcont.buildRa(s, "NATIVE"));
		
	}
	
	public static Stream<NcontRstTvog> createTvog() {
		String rsDivId = getNewRunset(ECoa.TVOG);
		return  RawDao.getRawTvog(bssd, rsDivId).map(s -> FacNcont.buildTvog(s, "NATIVE"));
	}
	
	public static Stream<NcontRstEpv> createEpv() {
		Map<String, NcontRstEpv> outEpvMap = NcontDao.getNcontOutEpvFromGroupBy(bssd).collect(toMap(NcontRstEpv::getCtrPolno, Function.identity()));
		
		return  NcontDao.getNcontRstEpvFromGroupBy(bssd).map(s -> FacNcont.buildEpv(s, outEpvMap.get(s.getCtrPolno())));
	}

//	public static Stream<NcontRstEpv> createConvertEpv() {
//		bssd="201809";
//		return  NcontDao.getNcontRstEpvFromGroupBy(bssd)
////						.filter(s->s.getTvogDivId().equals("FV"))
//						.map(s -> FacNcontFlat.buildEpv(s))
//										;
//	}
//	
//	public static Stream<NcontRstRa> createConvertRa() {
//		bssd="201809";
//		return  RawDao.getRawRaGroupBy(bssd, "CURR_CLOSING")
////					.filter(s->s.getRaDivId().equals("FV"))
//					.map(s -> FacNcontFlat.buildRa(s, raCalcType))
//					;
//	}
//	
//	public static Stream<NcontRstTvog> createConvertTvog() {
//		bssd="201809";
//		return  RawDao.getRawTvogGroupBy(bssd, "CURR_CLOSING")
////						.filter(s->s.getTvogDivId().equals("FV"))
//						.map(s -> FacNcontFlat.buildTvog(s, tvogCalcType))
//						;
//	}
//	
//	public static Stream<NcontRstFlat> createConversion() {
//		bssd="201809";
////		List<String> contList = MstDao.getMstContGocStream().map(mapKey).collect(toList());					
//		
//		Map<String, Double> raMap = NcontDao.getNcontRa(bssd).collect(toMap(mapKey, NcontRstRa::getRaAmt, (s,u)->s+u ));
//		log.info("RaMap : {}", raMap.size());
//		
//		Map<String, Double> tvogMap = NcontDao.getNcontTvog(bssd).collect(toMap(mapKey, NcontRstTvog::getTvogAmt, (s,u)->s+u ));
//		log.info("TvogMap : {}", tvogMap.size());
//		
//		Map<String, Double> fvMap = NcontDao.getRawFvGroupBy(bssd).collect(toMap(mapKey, RawFv::getEpvAmt, (s,u)->s+u ));
//		log.info("fvMap : {}", fvMap.size());
//
//		return NcontDao.getNcontEpv(bssd).map(s-> FacNcontFlat.buildConv(s, raMap, tvogMap, fvMap));
//	}
//
//	public static Stream<NcontRstRa> createConvertRa1() {
//		bssd="201809";
//		return  RawDao.getRawRa(bssd)
////					.filter(s->s.getRaDivId().equals("FV"))
//					.map(s -> FacNcontFlat.buildRa(s, raCalcType))
//					;
//	}
//
//	public static Stream<NcontRstTvog> createConvertTvog1() {
//		bssd="201809";
//		return  RawDao.getRawTvog(bssd)
////						.filter(s->s.getTvogDivId().equals("FV"))
//						.map(s -> FacNcontFlat.buildTvog(s, tvogCalcType))
//						;
//	}
	
	private static String getNewRunset(ECoa coaId) {
		if(newRunsetMap.isEmpty()) {
			newRunsetMap = MstDao.getMstRunset().stream()
//								  .filter(s->s.getNewContYn().isTrueFalse())
								  .filter(s->s.getRunsetType().isNewContYn())
//								  .filter(s->s.getCoaType().equals(ECoa.EPV))
								  .collect(toMap(MstRunset::getCoaId, MstRunset::getRsDivId, (s,u)->s));
		}
		return newRunsetMap.get(coaId);
	}
	
	
}