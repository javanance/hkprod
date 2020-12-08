package com.gof.model;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao.BoxDao;
import com.gof.dao.CfDao;
import com.gof.dao.MapDao;
import com.gof.dao.MstDao;
import com.gof.dao.RaDao;
import com.gof.dao.RstDao;
import com.gof.dao.TvogDao;
import com.gof.dao.WaterfallDao;
import com.gof.entity.CfLv3Real;
import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MapRunsetCalc;
import com.gof.entity.MstCalcRfwdDetail;
import com.gof.entity.MstRunset;
import com.gof.entity.RstBoxDetail;
import com.gof.entity.RstBoxGoc;
import com.gof.entity.RstCsm;
import com.gof.entity.RstRollFwd;
import com.gof.enums.ECalcType;
import com.gof.enums.ECoa;
import com.gof.factory.FacBoxDetail;
import com.gof.factory.FacBoxGoc;
import com.gof.factory.FacRstCsmReins;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MdlRstBox {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;

	private static List<MapRunsetCalc> runsetCalcList = new ArrayList<MapRunsetCalc>();
	
	public static Stream<RstBoxDetail> createConversionDetail() {
		bssd =stBssd;
		return createBeforeEirUpdatDetail(null);
	}
	
	public static Stream<RstBoxGoc> createConversionRa() {
		bssd =stBssd;
		return createRa(null);
	}
	
	public static Stream<RstBoxGoc> createConversionTvog() {
		bssd =stBssd;
		return createTvog(null);
	}
	
	public static Stream<RstBoxGoc> createConversionRealCash() {
		bssd =stBssd;
		return createRealCash(null);
	}
	
	public static Stream<RstBoxGoc> createConversionFromDetail() {
		bssd =stBssd;
		List<String> gocIdList = PrvdMst.getGocIdList();
		return BoxDao.getRstBoxGroupBy(bssd).stream().filter(s->gocIdList.contains(s.getGocId()));
	}
	
	public static Stream<RstBoxGoc> createLossRcvConversion(){
		bssd=stBssd;
		return createLossRcv();
	}
	
	
	
	public static Stream<RstBoxGoc> createRa() {
		return createRa(null);
	}
	
	public static Stream<RstBoxGoc> createTvog() {
		return createTvog(null);
	}
	
	public static Stream<RstBoxGoc> createEl() {
		return createEl(null);
	}
	
	public static Stream<RstBoxGoc> createLossRcv() {
		return createLossRcv(null);
	}
	
	public static Stream<RstBoxGoc> createGoc() {
		List<String> gocIdList = PrvdMst.getGocIdList();
		return BoxDao.getRstBoxGroupBy(bssd).stream().filter(s->gocIdList.contains(s.getGocId()));
	}
	
	public static Stream<RstBoxGoc> createRealCash() {
		return createRealCash(null);
	}


	public static Stream<RstBoxGoc> createRa(String gocId) {
		Map<String, List<MapRunsetCalc>> runsetMap = getRunsetCalcList(ECalcType.WATERFALL).stream().collect(groupingBy(MapRunsetCalc::getRunsetId, toList()));
		
		return RaDao.getRaLv2Delta(bssd,gocId).stream()
										.filter(s->s.getDeltaRaAmt()!= 0.0)
										.map(s->FacBoxGoc.build(bssd, s, runsetMap.getOrDefault(s.getRunsetId(), new ArrayList<MapRunsetCalc>())))
										.flatMap(s->s.stream());
	}
	
	public static Stream<RstBoxGoc> createTvog(String gocId) {
		Map<String, List<MapRunsetCalc>> runsetMap = getRunsetCalcList(ECalcType.WATERFALL).stream().collect(groupingBy(MapRunsetCalc::getRunsetId, toList()));
	
		return TvogDao.getTvogLv2Delta(bssd, gocId).stream()
										.filter(s->s.getDeltaTvogAmt()!= 0.0)
										.map(s->FacBoxGoc.build(bssd, s, runsetMap.getOrDefault(s.getRunsetId(), new ArrayList<MapRunsetCalc>())))
										.flatMap(s->s.stream());
	}
	
	
	public static Stream<RstBoxGoc> createEl(String gocId) {
		Map<String, List<MapRunsetCalc>> runsetMap = getRunsetCalcList(ECalcType.WATERFALL).stream().collect(groupingBy(MapRunsetCalc::getRunsetId, toList()));
	
		return WaterfallDao.getElLv2Delta(bssd, gocId).stream()
										.filter(s->s.getDeltaElAmt()!= 0.0)
										.map(s->FacBoxGoc.build(bssd, s, runsetMap.getOrDefault(s.getRunsetId(), new ArrayList<MapRunsetCalc>())))
										.flatMap(s->s.stream());
	}
	
	public static Stream<RstBoxGoc> createLossRcv(String gocId) {
//		return RstDao.getRstLossRcvGroupBy(bssd).stream().filter(s->s.getGocId().equals(gocId)).map(s->FacBoxGoc.build(bssd, s));
		return RstDao.getRstLossRcvGroupBy(bssd, gocId).stream().map(s->FacBoxGoc.build(bssd, s));
	}
	
	public static Stream<RstBoxGoc> createRealCash(String gocId) {
		List<String> runsetIdList = PrvdMst.getMstRunsetList(ECoa.CASH).stream().map(MstRunset::getRunsetId).collect(toList());
		List<MapRunsetCalc> mapRunsetCalcList = MapDao.getMapRunsetCalc().stream()
									.filter(s->"Y".equals(s.getMstCalc().getUseYn()))
									.filter(s-> runsetIdList.contains(s.getRunsetId()))			
									.collect(toList());
		
		return CfDao.getCfLv3RealGroupByStream(bssd, gocId)
					.map(cf-> buildRealCash(cf, mapRunsetCalcList))
					.flatMap(s->s.stream())
					.collect(toMap(RstBoxGoc::getPk, Function.identity(), (s,u)-> s.merge(u)))
					.entrySet().stream()
	//			 	.peek(s-> log.info("bbb :  {},{}", s.getKey(), s.getValue()))
					.map(s-> s.getValue())
					;
	}

	public static Stream<RstBoxGoc> createGoc(String gocId) {
		return BoxDao.getRstBoxGroupBy(bssd, gocId).stream();
	}


	public static Stream<RstBoxDetail> createDetail() {
		return createDetail(null);
	}

	public static Stream<RstBoxDetail> createDetail(String gocId) {
		List<MapRunsetCalc> runsetList = getRunsetCalcList(ECalcType.BOX);
		
		return CfDao.getCfLv4DfStream(bssd, gocId)
							.filter(s->s.getDeltaCfAmt()!= 0.0)
							.map(s->FacBoxDetail.build(bssd, s, runsetList))
							.flatMap(s->s.stream())
//							.peek(s-> log.info("aaa : {}", s.get))
							;
	}
	
	public static Stream<RstBoxDetail> createBeforeEirUpdatDetail() {
		return createBeforeEirUpdatDetail(null);
	}

	public static Stream<RstBoxDetail> createBeforeEirUpdatDetail(String gocId) {

		List<MapRunsetCalc> runsetList = getRunsetCalcList(ECalcType.BOX);
		
//		Map<ELiabType, Integer> eirUpdateRunsetSeq =PrvdMst.getMstRunsetList(ECoa.EPV).stream()
//													  .filter(s->s.getEirAplyYn().isTrueFalse())
//													  .collect(toMap(MstRunset::getLiabType, MstRunset::getSeq, (s,u)-> Math.min(s, u)));
		
		return PrvdMst.getMstRunsetList(ECoa.EPV).stream()
					  .filter(s->s.getBeforeEirUpdateYn().isTrueFalse())
					  .flatMap(s-> create(gocId, s, runsetList))	
					  ;
	}
	
	public static Stream<RstBoxDetail> createAfterEirUpdatDetail() {
		return createAfterEirUpdatDetail(null);
	}

	public static Stream<RstBoxDetail> createAfterEirUpdatDetail(String gocId) {

		List<MapRunsetCalc> runsetList = getRunsetCalcList(ECalcType.BOX);
		
		return PrvdMst.getMstRunsetList(ECoa.EPV).stream()
					  .filter(s-> !s.getBeforeEirUpdateYn().isTrueFalse())
					  .flatMap(s-> create(gocId, s, runsetList))	
					  ;
	}
	
	private  static Stream<RstBoxDetail> create(String gocId, MstRunset runset, List<MapRunsetCalc> runsetList) {
		return CfDao.getCfLv4DfByRunsetStream(bssd, runset.getRunsetId(), gocId)
					.filter(s->s.getDeltaCfAmt()!= 0.0)
					.map(s->FacBoxDetail.build(bssd, s, runsetList))
					.flatMap(s->s.stream())
					;
	}
	
	private static List<RstBoxGoc> buildRealCash(CfLv3Real cf, List<MapRunsetCalc> mapRunsetCalcList) {
		List<RstBoxGoc> rstList = new ArrayList<RstBoxGoc>();
		for(MapRunsetCalc aa :mapRunsetCalcList) {
			if(aa.getRunsetId().equals(cf.getRunsetId()) && aa.getMstCalc().contains(cf.getCfType())) {
//				log.info("zzzz : {},{},{}", cf.getCfType(), aa.getCalcId(), cf.toString());
				double boxValue   = aa.getMstCalc().getSignAdjust().getAdj() * cf.getCfAmt();
				
				rstList.add(RstBoxGoc.builder()
								.baseYymm(cf.getBaseYymm())
								.gocId(cf.getGocId())
								.liabType(cf.getLiabType())
								.mstRunset(aa.getMstRunset())
								.calcId(aa.getCalcId())
								.stStatus(cf.getStStatus())
								.endStatus(cf.getEndStatus())
								.newContYn(cf.getNewContYn())
								.slidingNum(0)
								.cfAmt(cf.getCfAmt())
								.prevCfAmt(0.0)
								.deltaCfAmt(cf.getCfAmt())
								.boxValue(boxValue)
								.appliedRate(0.0)
								.remark("")
								.lastModifiedBy(GmvConstant.getLastModifier())
								.lastModifiedDate(LocalDateTime.now())
								.build()
						);
				
			}
		}
		return rstList;
	}

	private static List<MapRunsetCalc> getRunsetCalcList(ECalcType calcType){
		if(runsetCalcList.isEmpty()) {
			runsetCalcList = MapDao.getMapRunsetCalc().stream()
					.filter(s->"Y".equals(s.getMstCalc().getUseYn()))	
					.collect(toList());
		}
		return runsetCalcList.stream().filter(s-> s.getMstCalc().getCalcType().equals(calcType)).collect(toList());
	}

}