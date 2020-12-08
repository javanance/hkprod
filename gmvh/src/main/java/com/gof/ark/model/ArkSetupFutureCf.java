package com.gof.ark.model;

import java.util.stream.Stream;

import com.gof.ark.dao.ArkDao;
import com.gof.ark.entity.ArkFutureCf;
import com.gof.ark.entity.ArkMstRunset;
import com.gof.enums.ERunsetType;
import com.gof.enums.ESlidingType;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ArkSetupFutureCf {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	
	
	public static Stream<ArkFutureCf> createConversion() {
		bssd= stBssd;
		return createCurrent();
	}
	
	public static Stream<ArkFutureCf> createAll() {
		return  ArkDao.getArkMstRunset()
				.filter(s-> s.getFutCfAplyYn().isTrueFalse())
//				.filter(s->s.getRunsetType().equals(ERunsetType.PREV_CLOSING))
				.flatMap(s-> create(s));
	}
	
	public static Stream<ArkFutureCf> createCurrent() {
		return  ArkDao.getArkMstRunset()
				.filter(s-> s.getFutCfAplyYn().isTrueFalse())
				.filter(s->!s.getNewContYn().isTrueFalse())
				.filter(s-> s.getSetlYmSlidingType().equals(ESlidingType.EOP))
				.flatMap(s-> create(s));
	}
	
	public static Stream<ArkFutureCf> createPrev() {
		return  ArkDao.getArkMstRunset()
				.filter(s-> s.getFutCfAplyYn().isTrueFalse())
				.filter(s->!s.getNewContYn().isTrueFalse())
				.filter(s-> s.getSetlYmSlidingType().equals(ESlidingType.BOP))
				.flatMap(s-> create(s));
	}
	
	public static Stream<ArkFutureCf> createNewCont() {
		return  ArkDao.getArkMstRunset()
				.filter(s-> s.getFutCfAplyYn().isTrueFalse())
				.filter(s-> s.getNewContYn().isTrueFalse())
				.flatMap(s-> create(s));
	}
	
	private static Stream<ArkFutureCf> create(ArkMstRunset arkRunset) {
//		String driveYm = arkRunset.getSlidingType().equals(ESlidingType.BOP)? stBssd : bssd; 
//		String driveYm = arkRunset.getDriveYmSldingType().equals(ESlidingType.BOP)? stBssd : bssd;
		String driveYm = arkRunset.getDriveYm(stBssd, bssd);
		String setlYm  = arkRunset.getSetlYm(stBssd, bssd);
		
		log.info("aaaa : {},{},{},{}", arkRunset.getRsDivId(), arkRunset.getArkRunsetId(), driveYm, setlYm);
		
//		return ArkDao.getRawEpvDetailGroupByStream(driveYm, setlYm)
		return ArkDao.getRawFutureCfGroupByStream(driveYm, setlYm)
					 .filter(s->arkRunset.getRsDivId().equals(s.getRsDivId()))
					 .map(s-> FacArkFutureCf.convertFrom(bssd, s, arkRunset))
//					 .peek(s->log.info("aaaa :  {},{}", s.toString()))
					 ;
	}

}
