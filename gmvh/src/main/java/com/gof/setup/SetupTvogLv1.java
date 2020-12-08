package com.gof.setup;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.gof.dao.RawDao;
import com.gof.entity.MstRunset;
import com.gof.entity.RawTvogGoc;
import com.gof.entity.TvogLv1;
import com.gof.enums.ECoa;
import com.gof.enums.ESlidingType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupTvogLv1 {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	private static List<MstRunset> runsetList =new ArrayList<MstRunset>();
	
	public static Stream<TvogLv1> createConversion(){
		bssd= stBssd;
		return create();
	}
	
	public static Stream<TvogLv1> create(){
		return create(null);
	}
	
	public static Stream<TvogLv1> create(String gocId){
		return  PrvdMst.getMstRunsetList().stream()
						.filter(s->s.getCoaId().equals(ECoa.TVOG))
						.flatMap(s-> create(gocId, s));
	}

	private static Stream<TvogLv1> create(String gocId,  MstRunset runset) {
		String baseYymm = runset.getSetlYmSlidingType().equals(ESlidingType.EOP)?  bssd : runset.getGenYymm(stBssd, bssd);
		
		return RawDao.getRawTvogGoc(baseYymm, gocId).stream()
				.filter(s->s.getRsDivId().equals(runset.getRsDivId()))
				.map(s-> build(bssd, runset, s));
	}
	
	private static TvogLv1 build(String bssd,  MstRunset runset, RawTvogGoc tvog) {
		return TvogLv1.builder()
				.baseYymm(bssd)
				.gocId(tvog.getGocId())
				.runsetId(runset.getRunsetId())
				.deltaGroup(runset.getDeltaGroup())
				.tvogAmt(tvog.getTvogAmt())
				.srcBaseYymm(tvog.getBaseYymm())
				.rsDivId(tvog.getRsDivId())
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
	}
}