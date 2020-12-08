package com.gof.setup;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao.MstDao;
import com.gof.dao.RawDao;
import com.gof.entity.MstCalcRfwdDetail;
import com.gof.entity.MstCalcUlRefDetail;
import com.gof.entity.MstGoc;
import com.gof.entity.MstRunset;
import com.gof.entity.RawRatioLossRcv;
import com.gof.entity.RawUlGocLossRcv;
import com.gof.entity.RstLossRcv;
import com.gof.enums.ECalcType;
import com.gof.enums.ECoa;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupRstLossRcv {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	
	private static Map<String, Map<String, RawRatioLossRcv>> ratioMap  = new HashMap<String, Map<String,RawRatioLossRcv>>();
	private static Map<String, Map<String, Double>> ulLossMap = new HashMap<String, Map<String, Double>>();
	private static Map<String, Double> conversionLossMap  = new HashMap<String, Double>(); 
	private static Map<String, MstGoc> gocMap  = new HashMap<String, MstGoc>();
	

	

	public static Stream<RstLossRcv> createConversion(){
		bssd= stBssd;
		
		MstRunset mstRunset = PrvdMst.getMstRunsetList().stream().filter(s->s.getCoaId().equals(ECoa.LOSS_RCV)).findFirst().orElseGet(MstRunset::new);
		log.info("zzz : {}", mstRunset.getRunsetId());

		List<MstCalcRfwdDetail> calcList = MstDao.getMstCalcRfwdDetail().stream()
												.filter(s->s.getCoaId().equals(ECoa.LOSS_RCV))
												.filter(s->s.getCalcSubType().equals(ECalcType.PREV_RST))
//												.peek(s->log.info("zzzz : {},{}", s.getCalcId(),s.getCoaId()))
												.collect(toList())
												;
		
		return RawDao.getRawRatioLossRcv(bssd)
//						.filter(s->s.getGocId().equals("9901_0000_3"))
						.filter(s->s.getLossRcvStep().equals("LR0"))				//TODO :::::::
						.flatMap(s-> createConversion(s, calcList, mstRunset));
	}
	
	public static Stream<RstLossRcv> create(){
		return create(null);
	}
	
	public static Stream<RstLossRcv> create(String gocId){
		MstRunset mstRunset = PrvdMst.getMstRunsetList().stream().filter(s->s.getCoaId().equals(ECoa.LOSS_RCV)).findFirst().orElseGet(MstRunset::new);
		
		List<MstCalcUlRefDetail> calcList = MstDao.getMstCalcUlRefDetail();
		
		return RawDao.getRawRatioLossRcv(bssd, gocId).flatMap(s-> create(s, calcList, mstRunset));
		
		
	}
	
	private static Stream<RstLossRcv> create(RawRatioLossRcv ratio, List<MstCalcUlRefDetail> calcList, MstRunset mstRunset) {
		
		List<RstLossRcv> rstList = new ArrayList<RstLossRcv>();
		MstGoc mstGoc = getMstGoc(ratio.getGocId());
		
		for(MstCalcUlRefDetail calcDetail : calcList) {
			if(calcDetail.getApplyGocDiv().contains(mstGoc.getGocDiv(stBssd, bssd))) {
				Map<String, Double> lossAmtMap = getUlLossList(calcDetail.getUlRefCalcId());
				
				if(lossAmtMap.containsKey(ratio.getPk())) {
					double lossAmt = lossAmtMap.getOrDefault(ratio.getPk(), 0.0);
					rstList.add(build(bssd, ratio, calcDetail, mstRunset, lossAmt));
				}
			}
		}

		return rstList.stream();
	}
	
	
	private static Stream<RstLossRcv> createConversion(RawRatioLossRcv ratio, List<MstCalcRfwdDetail> calcList, MstRunset mstRunset) {
		
		List<RstLossRcv> rstList = new ArrayList<RstLossRcv>();
		
		Map<String, Double> lossAmtMap = getConversionUnderlyingLossAmtMap();
		
		log.info("calclist size : {}", calcList.size());
		
		for(MstCalcRfwdDetail calcDetail : calcList) {
			if(lossAmtMap.containsKey(ratio.getUlGocId())) {
				double lossAmt = lossAmtMap.getOrDefault(ratio.getUlGocId(), 0.0);
				rstList.add(build(bssd, ratio, calcDetail, mstRunset, lossAmt));
			}
		}
	
		return rstList.stream();
	}

	private static RstLossRcv build(String bssd, RawRatioLossRcv ratio, MstCalcUlRefDetail calcDetail, MstRunset mstRunset, double lossAmt) {
		log.info("build : {},{},{}", bssd,mstRunset.getRunsetId(), calcDetail.getCalcId());
		
		double boxValue = lossAmt * ratio.getRatio();
		
		return RstLossRcv.builder()
				.baseYymm(bssd)
				.gocId(ratio.getGocId())
				.ulGocId(ratio.getUlGocId())
				.liabType(ELiabType.LRC)
//				.runsetId(mstRunset.getRunsetId())
				.mstRunset(mstRunset)
				.calcId(calcDetail.getCalcId())
				.ulCalcId(calcDetail.getUlRefCalcId())
				.stStatus(EContStatus.NORMAL.name())
				.endStatus(EContStatus.NORMAL.name())
				.newContYn("N")
				.lossAdjAmt(lossAmt)
				.boxValue(boxValue)
				.lossRcvStep(ratio.getLossRcvStep())
				.ratio(ratio.getRatio())
				.numerator(ratio.getNumerator())
				.denominator(ratio.getDenominator())
				.remark("")
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
	}

	private static RstLossRcv build(String bssd, RawRatioLossRcv ratio, MstCalcRfwdDetail calcDetail, MstRunset mstRunset, double lossAmt) {
		log.info("build : {},{},{}", bssd,ratio.getGocId(), mstRunset.getRunsetId(), calcDetail.getCalcId());
		
		double boxValue = lossAmt * ratio.getRatio();
		
		return RstLossRcv.builder()
				.baseYymm(bssd)
				.gocId(ratio.getGocId())
				.ulGocId(ratio.getUlGocId())
				.liabType(ELiabType.LRC)
//				.runsetId(mstRunset.getRunsetId())
				.mstRunset(mstRunset)
				.calcId(calcDetail.getCalcId())
				.ulCalcId("CLOSE_STEP")
				.stStatus(EContStatus.NORMAL.name())
				.endStatus(EContStatus.NORMAL.name())
				.newContYn("N")
				.lossAdjAmt(lossAmt)
				.boxValue(boxValue)
				.lossRcvStep(ratio.getLossRcvStep())
				.ratio(ratio.getRatio())
				.numerator(ratio.getNumerator())
				.denominator(ratio.getDenominator())
				.remark("")
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
	}
//	private static Map<String, Map<String, RawRatioLossRcv>> getRatioMap() {
//		if(ratioMap.isEmpty()) {
//			ratioMap = RawDao.getRawRatioLossRcv(bssd).collect(groupingBy(RawRatioLossRcv::getGocId, toMap(RawRatioLossRcv::getPk, Function.identity())));
//		}
//		return ratioMap; 
//	}
	
	private static MstGoc getMstGoc(String gocId) {
		if(gocMap.isEmpty()) {
			gocMap = PrvdMst.getGocList().stream().collect(toMap(MstGoc::getGocId, Function.identity()));
		}
		if(!gocMap.containsKey(gocId)) {
			log.error("Error : No MstGoc for {} for Loss RCV ", gocId);
			System.exit(1);
		}
		return gocMap.get(gocId);
	}
	
	private static Map<String, Double> getUlLossList(String ulCalcId) {
		if(ulLossMap.isEmpty()) {
			ulLossMap = RawDao.getRawUlGocLossRcv(bssd).collect(groupingBy(RawUlGocLossRcv::getUlCalcId, toMap(RawUlGocLossRcv::getPk, RawUlGocLossRcv::getLossAdjAmt)));
		}
		return ulLossMap.get(ulCalcId); 
	}

	private static Map<String, Double> getConversionUnderlyingLossAmtMap() {
		if(conversionLossMap.isEmpty()) {
			
			conversionLossMap = RawDao.getRawUlGocLossRcv(bssd)
										.filter(s->s.getLossRcvStep().equals("LR0"))
										.filter(s->s.getUlCalcId().equals("CLOSE_STEP"))
										.peek(s->log.info("qqqqq : {},{}", s.getUlGocId(), s.getUlCalcId()))
										.collect(toMap(RawUlGocLossRcv::getUlGocId, RawUlGocLossRcv::getLossAdjAmt));
		}
		return conversionLossMap;
	}
}