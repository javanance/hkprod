package com.gof.setup;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.dao.MstDao;
import com.gof.dao.RstDao;
import com.gof.entity.MstRunset;
import com.gof.entity.RstEpvNgoc;
import com.gof.enums.ERunsetType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdAcct;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupRstEpvNgoc {
	private static	String bssd			= GmvConstant.BSSD;
	private static	String stBssd		= GmvConstant.ST_BSSD;

//	private static	Function<CfLv1Goc, Double> tenorAdjFn = s-> s.getCfMonthNum();
	
	public static Stream<RstEpvNgoc> createConversion() {
		
		List<String> runsetList = MstDao.getMstRunset().stream()
										.filter(s->s.getRunsetType().equals(ERunsetType.CURR_CLOSING))		//TODO : Check
										.map(MstRunset::getRunsetId)
										.collect(toList())
										;
		
		Map<String, Double> outRst = RstDao.getRstEpvNgocFromOutCf(stBssd).stream()
											.collect(toMap(RstEpvNgoc::getPk, RstEpvNgoc::getEpvAmt, (s,u)->s+u));
											
		return RstDao.getRstEpvNgocFromCf(stBssd).stream()
										 .filter(s-> runsetList.contains(s.getRunsetId()))
//										 .filter(s->s.getStStatus().equals(EContStatus.NEW))				//TODO ::: new cont ????
//										 .filter(s->s.getEndStatus().equals(EContStatus.NORMAL))
										 .map(s-> build(s, stBssd, outRst.getOrDefault(s.getPk(),0.0)))
										 ;
	}
	
	public static Stream<RstEpvNgoc> create() {
		return create(null);
	}
	
	public static Stream<RstEpvNgoc> create(String gocId) {
		List<String> runsetList = MstDao.getMstRunset().stream()
										.filter(s-> s.getRunsetType().isNewContYn())
										.map(MstRunset::getRunsetId).collect(toList());
		
		Map<String, Double> outRst = RstDao.getRstEpvNgocFromOutCf(bssd, gocId).stream()
										   .collect(toMap(RstEpvNgoc::getPk, RstEpvNgoc::getEpvAmt, (s,u)->s+u));
		
		return RstDao.getRstEpvNgocFromCf(bssd, gocId).stream()
							 .filter(s-> runsetList.contains(s.getRunsetId()))
//							 .filter(s->s.getStStatus().equals(EContStatus.NEW))				//TODO ::: new cont ????
//							 .filter(s->s.getEndStatus().equals(EContStatus.NORMAL))							 
//							 .map(s-> appendOutEpv(s, outRst.get(s.getPk())))
							 .map(s-> appendOutEpv(s, outRst.getOrDefault(s.getPk(), 0.0)))
							 ;
	}
	
	
	private static RstEpvNgoc appendOutEpv(RstEpvNgoc rstEpv, double outEpvAmt) {
//		String newContCurveYymm = PrvdAcct.getCurveYymm(rstEpv.getGocId(), GmvConstant.NEW_CONT_RATE, PrvdMst.getMstRunset(rstEpv.getRunsetId()));
		String newContCurveYymm = PrvdAcct.getCurveYymm(rstEpv.getGocId(), PrvdMst.getMstRunset(rstEpv.getRunsetId()));
		
		return build(rstEpv, newContCurveYymm, outEpvAmt);
	}

	private static RstEpvNgoc build(RstEpvNgoc rstEpv, String newContCurveYymm, double outEpvAmt) {
		return RstEpvNgoc.builder()
				.baseYymm(rstEpv.getBaseYymm())
				.gocId(rstEpv.getGocId())
				.liabType(rstEpv.getLiabType())
				.stStatus(rstEpv.getStStatus())
				.endStatus(rstEpv.getEndStatus())
				.newContYn(rstEpv.getNewContYn())
				.ncontCurveYymm(newContCurveYymm)
				.runsetId(rstEpv.getRunsetId())
				.outCfAmt(rstEpv.getOutCfAmt())
				.outEpvAmt(outEpvAmt)
				.inCfAmt(rstEpv.getInCfAmt())
				.inEpvAmt(rstEpv.getInEpvAmt())
				.cfAmt(rstEpv.getCfAmt())
				.epvAmt(rstEpv.getEpvAmt())
				.remark(rstEpv.getRemark())
				.lastModifiedBy(rstEpv.getLastModifiedBy())
				.lastModifiedDate(rstEpv.getLastModifiedDate())
				.build();
	}
}