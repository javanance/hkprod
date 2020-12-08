package com.gof.conv;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.dao.RawDao;
import com.gof.entity.FvFlat;
import com.gof.entity.RawElGoc;
import com.gof.entity.RawFvGoc;
import com.gof.entity.RawRaGoc;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MldFvFlat {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	private static	String fvRsDivId	=GmvConstant.FV_RS_DIV_ID;			//Default : FV
	
	public static Stream<FvFlat> createConversion(){
		bssd= stBssd;
		return create();
	}
	
	public static Stream<FvFlat> create(){
		List<FvFlat> rstList = new ArrayList<FvFlat>();
		
//		Map<String, Double> tovgMap = RawDao.getRawTvogGoc(bssd).stream().filter(s->s.getRsDivId().equals(fvRsDivId)).collect(toMap(RawTvogGoc::getGocId, RawTvogGoc::getTvogAmt));
		Map<String, Double> raMap 	= RawDao.getRawRaGoc(bssd).stream().filter(s->s.getRsDivId().equals(fvRsDivId)).collect(toMap(RawRaGoc::getGocId, RawRaGoc::getRaAmt));
		Map<String, Double> elMap 	= RawDao.getRawElGoc(bssd).filter(s->s.getRsDivId().equals(fvRsDivId)).collect(toMap(RawElGoc::getGocId, RawElGoc::getElAmt));
		
		Map<String, Double> epvMap 	= RawDao.getRawFvGoc(bssd).stream().collect(toMap(RawFvGoc::getGocId, RawFvGoc::getEpvAmt));
		
		for(Map.Entry<String, Double> entry : epvMap.entrySet()) {
			String gocId = entry.getKey();
//			rstList.add(build(bssd, gocId, entry.getValue(), raMap.getOrDefault(gocId,0.0), tovgMap.getOrDefault(gocId, 0.0)));
			rstList.add(build(bssd, gocId, entry.getValue(), raMap.getOrDefault(gocId,0.0), elMap.getOrDefault(gocId, 0.0)));
		}
		
		return rstList.stream();
	}

	public static FvFlat build(String bssd,  String gocId,  double epvAmt, double raAmt, double elAmt) {
		return FvFlat.builder()
				.baseYymm(bssd)
				.gocId(gocId)
//				.cfAmt(cfAmt)
				.epvAmt(epvAmt)
				.raAmt(raAmt)
				.elAmt(elAmt)
				.fvAmt(epvAmt + raAmt + elAmt)
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
	}
}