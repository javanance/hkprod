package com.gof.setup;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.gof.dao.RawDao;
import com.gof.entity.MstRunset;
import com.gof.entity.RaLv1;
import com.gof.entity.RawRaGoc;
import com.gof.enums.ECoa;
import com.gof.enums.ESlidingType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupRaLv1 {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	private static List<MstRunset> runsetList =new ArrayList<MstRunset>();
	
	public static Stream<RaLv1> createConversion(){
		bssd= stBssd;
		return create();
	}
	
	public static Stream<RaLv1> create(){
		return create(null);
	}
	
	
	public static Stream<RaLv1> create(String gocId){
		return  PrvdMst.getMstRunsetList().stream()
						.filter(s->s.getCoaId().equals(ECoa.RA))
						.flatMap(s-> create(gocId, s));
	}
	
	private static Stream<RaLv1> create(String gocId,  MstRunset runset) {
		String baseYymm = runset.getSetlYmSlidingType().equals(ESlidingType.EOP)?  bssd : runset.getGenYymm(stBssd, bssd);
		return RawDao.getRawRaGoc(baseYymm, gocId).stream()
				.filter(s->s.getRsDivId().equals(runset.getRsDivId()))
				.map(s-> build(bssd, runset, s));
	}
	
	private static RaLv1 build(String bssd,  MstRunset runset, RawRaGoc ra) {
		return RaLv1.builder()
				.baseYymm(bssd)
				.liabType(ra.getLiabType())
				.gocId(ra.getGocId())
				.runsetId(runset.getRunsetId())
				.deltaGroup(runset.getDeltaGroup())
				.raAmt(ra.getRaAmt())
				.srcBaseYymm(ra.getBaseYymm())
				.rsDivId(ra.getRsDivId())
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
	}

	
}