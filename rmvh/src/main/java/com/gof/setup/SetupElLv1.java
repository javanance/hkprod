package com.gof.setup;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.gof.dao.RawDao;
import com.gof.entity.ElLv1;
import com.gof.entity.MstRunset;
import com.gof.entity.RaLv1;
import com.gof.entity.RawElGoc;
import com.gof.entity.RawRaGoc;
import com.gof.enums.ECoa;
import com.gof.enums.ESlidingType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupElLv1 {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
//	private static List<MstRunset> runsetList =new ArrayList<MstRunset>();

	public static Stream<ElLv1> createConversion(){
		bssd= stBssd;
//		return PrvdMst.getCfGocIdSet().stream().flatMap(gocId -> create(gocId));
		return create();
	}
	
	public static Stream<ElLv1> create(){
		return PrvdMst.getCfGocIdSet().stream().flatMap(gocId -> create(gocId));
//		return create(null);
	}
	
	public static Stream<ElLv1> create(String gocId){
		return  PrvdMst.getMstRunsetList().stream()
						.filter(s->s.getCoaId().equals(ECoa.EL))
						.flatMap(s-> create(gocId, s));
	}
	
	private static Stream<ElLv1> create(String gocId,  MstRunset runset) {
		String baseYymm = runset.getSetlYmSlidingType().equals(ESlidingType.EOP)?  bssd : runset.getGenYymm(stBssd, bssd);
		return RawDao.getRawElGoc(baseYymm, gocId)
						.filter(s->s.getRsDivId().equals(runset.getRsDivId()))
						.map(s-> build(bssd, runset, s));
	}
	
//	public static Stream<ElLv1> create(){
//		List<String> gocIdList = PrvdMst.getGocIdList();
//		return create(null).filter(s-> gocIdList.add(s.getGocId()));
//	}
	
//	public static Stream<ElLv1> create(String gocId){
////		return  PrvdMst.getMstRunsetList().stream()
////						.filter(s->s.getCoaId().equals(ECoa.EL))
////						.flatMap(s-> create(gocId, s));
//		
//		List<ElLv1> rstList = new ArrayList<ElLv1>();
//		
//		String tempYymm ="";
//		
//		for(MstRunset runset : PrvdMst.getMstRunsetList(ECoa.EL)) {
//			tempYymm = runset.getRunsetType().isPrev()?  DateUtil.addMonthToString(stBssd, runset.getSlidingType().getSlidingNum()): bssd;
//
//			for(RawElGoc el : RawDao.getRawElGoc(tempYymm, gocId)) {
////				log.info("aaa :  {},{},{}", ra.getRsDivId(), runset.getRunsetId(), runset.getRsDivId());
//				if(el.getRsDivId().equals(runset.getRsDivId()) ) {
//					rstList.add(build(bssd, runset, el));
//				}
//			}
//		}
//		return rstList.stream();
//		
//	}

	
	private static ElLv1 build(String bssd,  MstRunset runset, RawElGoc el) {
		return ElLv1.builder()
				.baseYymm(bssd)
				.gocId(el.getGocId())
				.runsetId(runset.getRunsetId())
				.deltaGroup(runset.getDeltaGroup())
				.elAmt(el.getElAmt())
				.srcBaseYymm(el.getBaseYymm())
				.rsDivId(el.getRsDivId())
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
	}
	
}