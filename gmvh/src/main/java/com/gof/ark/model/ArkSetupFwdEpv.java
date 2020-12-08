package com.gof.ark.model;

import java.util.stream.Stream;

import com.gof.ark.dao.ArkDao;
import com.gof.ark.entity.ArkFwdEpv;
import com.gof.ark.entity.ArkMstRunset;
import com.gof.enums.ESlidingType;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ArkSetupFwdEpv {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	
	
	public static Stream<ArkFwdEpv> createConversion() {
		bssd= stBssd;
		return createCurrent();
	}
	
	public static Stream<ArkFwdEpv> createAll() {
		return  ArkDao.getArkMstRunset().flatMap(s-> create(s));
	}
	
	
	public static Stream<ArkFwdEpv> createCurrent() {
		return  ArkDao.getArkMstRunset()
				.filter(s->!s.getNewContYn().isTrueFalse())
				.filter(s->s.getSetlYmSlidingType().equals(ESlidingType.EOP))
				.flatMap(s-> create(s));
	}
	
	public static Stream<ArkFwdEpv> createPrev() {
		return  ArkDao.getArkMstRunset()
				.filter(s->!s.getNewContYn().isTrueFalse())
				.filter(s->s.getSetlYmSlidingType().equals(ESlidingType.BOP))
				.flatMap(s-> create(s));
	}
	
	public static Stream<ArkFwdEpv> createNewCont() {
		return  ArkDao.getArkMstRunset()
				.filter(s->s.getNewContYn().isTrueFalse())
				.flatMap(s-> create(s));
	}
	
	private static Stream<ArkFwdEpv> create(ArkMstRunset arkRunset) {
//		String driveYm = arkRunset.getSlidingType().equals(ESlidingType.BOP)? stBssd : bssd;
//		String driveYm = arkRunset.getDriveYmSldingType().equals(ESlidingType.BOP)? stBssd : bssd;
//		String setlYm  = arkRunset.getGenYymm(stBssd, bssd);
		
		String driveYm = arkRunset.getDriveYm(stBssd, bssd);
		String setlYm  = arkRunset.getSetlYm(stBssd, bssd);
		
		log.info("aaaa : {},{},{}", arkRunset.getRsDivId(), arkRunset.getArkRunsetId(), setlYm);

		return ArkDao.getRawEpvGroupByStream(driveYm, setlYm)
					 .filter(s->arkRunset.getRsDivId().equals(s.getRsDivId()))
//					 .filter(s->s.getCsmGrpCd().equals("0401_0000_3"))
					 .map(s->FacArkFwdEpv.convert(bssd, s, arkRunset))
					 .flatMap(s->s.stream())
//					 .peek(s-> log.info("zzz :  {},{},{}", s.getArkRunsetId(), s.getEpvAmt()))
					 ;
	}
}
