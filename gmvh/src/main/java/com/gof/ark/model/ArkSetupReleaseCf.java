package com.gof.ark.model;

import java.util.stream.Stream;

import com.gof.ark.dao.ArkDao;
import com.gof.ark.entity.ArkMstRunset;
import com.gof.ark.entity.ArkReleaseCf;
import com.gof.enums.ESlidingType;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ArkSetupReleaseCf {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	
		
	public static Stream<ArkReleaseCf> createConversion() {
		bssd =stBssd;
		return create(null);
	}
	public static Stream<ArkReleaseCf> create() {
		return create(null);
	}
	
	public static Stream<ArkReleaseCf> create(String gocId) {
		return ArkDao.getArkMstRunset()
					.filter(s->s.getCfReleaseYn().isTrueFalse())
					.flatMap(s->build(s));
	}
	
	public static Stream<ArkReleaseCf> createPrev() {
		return ArkDao.getArkMstRunset()
				.filter(s-> s.getCfReleaseYn().isTrueFalse())
				.filter(s->!s.getNewContYn().isTrueFalse())
				.filter(s-> s.getSetlYmSlidingType().equals(ESlidingType.BOP))
				.flatMap(s->build(s));
	}
	
	public static Stream<ArkReleaseCf> createNewCont() {
		return ArkDao.getArkMstRunset()
				.filter(s-> s.getCfReleaseYn().isTrueFalse())
				.filter(s-> s.getNewContYn().isTrueFalse())
				.flatMap(s->build(s));
	}
	
	
	private static Stream<ArkReleaseCf> build(ArkMstRunset arkRunset) {

//		String driveYm = arkRunset.getDriveYmSldingType().equals(ESlidingType.EOP)? bssd : arkRunset.getGenYymm(stBssd, bssd)  ;
//		String setlYm  = arkRunset.getGenYymm(stBssd, bssd);
		
		String driveYm = arkRunset.getDriveYm(stBssd, bssd);
		String setlYm  = arkRunset.getSetlYm(stBssd, bssd);
		
		
		log.info("Relese Cf : {},{},{}", setlYm, arkRunset.getRsDivId(), arkRunset.toString());
		
		return ArkDao.getRawReleaseCfGroupByStream(driveYm, setlYm)
					 .filter(s->s.getRsDivId().equals(arkRunset.getRsDivId()))
					 .map(s-> FacArkReleaseCf.convert(bssd, s, arkRunset))
//				     .peek(s->log.info("aaaa :  {},{},{}", setlYm, s.toString(), s.getCfAmt()))
				     ;	
	}
	
}
