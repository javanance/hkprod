package com.gof.factory;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gof.dao.MapDao;
import com.gof.entity.CfLv4Df;
import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MapRunsetCalc;
import com.gof.entity.MstRunset;
import com.gof.entity.RstBoxDetail;
import com.gof.enums.EBoxModel;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FacBoxDetail {
	
	public static List<RstBoxDetail> buildAoci(String bssd, CfLv4Df cf, MstRunset mstRunset, double eir, List<MapJournalRollFwd> journalList){
		
		List<RstBoxDetail> rstList = new ArrayList<RstBoxDetail>();

		for(MapJournalRollFwd journal :journalList) {
			EBoxModel boxId = journal.getMstCalc().getBoxId()==null? EBoxModel.CY: journal.getMstCalc().getBoxId();
			double aociAmt = cf.getAoci(eir);
			
			rstList.add(RstBoxDetail.builder()
							.baseYymm(bssd)
							.gocId(cf.getGocId())
							.liabType(cf.getLiabType())
							.mstRunset(mstRunset)
							.calcId(journal.getCalcId())
							.deltaGroup(cf.getDeltaGroup())
							.stStatus(cf.getStStatus())
							.endStatus(cf.getEndStatus())
							.newContYn(cf.getNewContYn())
							.subKey(cf.getSubKey())
							.cfKeyId(cf.getCfKeyId())
							.cfType(cf.getCfType())
							.cfTiming(cf.getCfTiming())
							.outflowYn(cf.getOutflowYn())
							.cfMonthNum(cf.getCfMonthNum())
							.setlAftPassMmcnt(cf.getSetlAftPassMmcnt())
							.boxId(boxId)
							.slidingNum(cf.getSlidingNum())
							.cfAmt(cf.getCfAmt())
							.prevCfAmt(cf.getPrevCfAmt())
							.deltaCfAmt(cf.getDeltaCfAmt())
							.boxValue(aociAmt)
							.appliedRate(eir)					//TODO!!!!!
							.remark(cf.getPrevRate()+"_"+cf.getPrevSysRate()+"_"+ cf.getCurrSysRate())
							.lastModifiedBy(GmvConstant.getLastModifier())
							.lastModifiedDate(LocalDateTime.now())
							.build()
					);
			
		}
		return rstList;
	}
	
	public static List<RstBoxDetail> buildAoci(String bssd, CfLv4Df cf, double eir, List<MapJournalRollFwd> journalList){
		List<RstBoxDetail> rstList = new ArrayList<RstBoxDetail>();

		Map<String, MstRunset> aa = MapDao.getMapRunsetCalc().stream().collect(toMap(MapRunsetCalc::getCalcId, MapRunsetCalc::getMstRunset, (s,u)->s));
		
		for(MapJournalRollFwd journal :journalList) {
			
			MstRunset mstRunset = aa.getOrDefault(journal.getCalcId(), PrvdMst.getMstRunset(cf.getRunsetId()));
			EBoxModel boxId = journal.getMstCalc().getBoxId()==null? EBoxModel.CY: journal.getMstCalc().getBoxId();
			double aociAmt = cf.getAoci(eir);
			
			rstList.add(RstBoxDetail.builder()
					.baseYymm(bssd)
					.gocId(cf.getGocId())
					.liabType(cf.getLiabType())
					.mstRunset(mstRunset)
					.calcId(journal.getCalcId())
					.deltaGroup(cf.getDeltaGroup())
					.stStatus(cf.getStStatus())
					.endStatus(cf.getEndStatus())
					.newContYn(cf.getNewContYn())
					.subKey(cf.getSubKey())
					.cfKeyId(cf.getCfKeyId())
					.cfType(cf.getCfType())
					.cfTiming(cf.getCfTiming())
					.outflowYn(cf.getOutflowYn())
					.cfMonthNum(cf.getCfMonthNum())
					.setlAftPassMmcnt(cf.getSetlAftPassMmcnt())
					.boxId(boxId)
					.slidingNum(cf.getSlidingNum())
					.cfAmt(cf.getCfAmt())
					.prevCfAmt(cf.getPrevCfAmt())
					.deltaCfAmt(cf.getDeltaCfAmt())
					.boxValue(aociAmt)
					.appliedRate(eir)					//TODO!!!!!
					.remark(cf.getPrevRate()+"_"+cf.getPrevSysRate()+"_"+ cf.getCurrSysRate())
					.lastModifiedBy(GmvConstant.getLastModifier())
					.lastModifiedDate(LocalDateTime.now())
					.build()
					);
			
		}
		return rstList;
		
	}
	
	public static List<RstBoxDetail> build(String bssd, CfLv4Df cf, List<MapRunsetCalc> calcList){
		List<RstBoxDetail> rstList = new ArrayList<RstBoxDetail>();
//		log.info("In the FacRstCal : {}, {}", cf.getRunsetId(), cf.getDeltaCfAmt());
		
		for(MapRunsetCalc mapCalc :calcList) {
			if(cf.check(mapCalc)) {
//				log.info("In the FacRstCal Check: {}, {},{}", cf.getRunsetId(), mapCalc.getMstCalc().getCalcId(), cf.getBoxValue(mapCalc.getMstCalc().getBoxId()));
				rstList.add(build(bssd,	cf, mapCalc));
			}
		}
		return rstList;
		
//		return calcList.stream().filter(mapCalc-> cf.check(mapCalc)).map(mapCalc->build(bssd, cf, mapCalc)).collect(toList());
	}	
	
	private static RstBoxDetail build(String bssd, CfLv4Df cf, MapRunsetCalc calc){
		double cfAmt      = calc.getMstCalc().getBoxId().equals(EBoxModel.ZU) && cf.isFutureCf(0.0) ? 0.0:cf.getCfAmt();
		double prevCfAmt  = calc.getMstCalc().getBoxId().equals(EBoxModel.ZU) && cf.isFutureCf(0.0) ? 0.0:cf.getPrevCfAmt();
		double deltaCfAmt = cfAmt - prevCfAmt;
		
		double boxValue   = calc.getMstCalc().getSignAdjust().getAdj() * cf.getBoxValue(calc.getMstCalc().getBoxId());
		
//		double cfAmt      = cf.getCfAmt();
//		double prevCfAmt  = cf.getPrevCfAmt();
//		double deltaCfAmt = cf.getDeltaCfAmt();
		
//		log.info("in the builder : {},{},{},{},{}", cf.getGocId(), cf.getRunsetId(), cf.getCfType(),calc.getRunsetId(), calc.getCalcId());
//		if(cf.getPrevCfAmt()>0) {
//			log.info("in the builder : {},{},{},{},{}", cf.getGocId(), cf.getRunsetId(), cf.getCfType(), cf.getPrevCfAmt());
//		}
		
		return RstBoxDetail.builder()
						.baseYymm(bssd)
						.gocId(cf.getGocId())
						.liabType(cf.getLiabType())
						.mstRunset(calc.getMstRunset())
						.calcId(calc.getMstCalc().getCalcId())
						.deltaGroup(cf.getDeltaGroup())
						.stStatus(cf.getStStatus())
						.endStatus(cf.getEndStatus())
						.newContYn(cf.getNewContYn())
						.subKey(cf.getSubKey())
						.cfKeyId(cf.getCfKeyId())
						.cfType(cf.getCfType())
						.cfTiming(cf.getCfTiming())
						.outflowYn(cf.getOutflowYn())
						.cfMonthNum(cf.getCfMonthNum())
						.setlAftPassMmcnt(cf.getSetlAftPassMmcnt())
						.boxId(calc.getMstCalc().getBoxId())
						.slidingNum(cf.getSlidingNum())
						.cfAmt(cfAmt)
						.prevCfAmt(prevCfAmt)
						.deltaCfAmt(deltaCfAmt)
						.boxValue(boxValue)
						.appliedRate(cf.getCurrRate())													//TODO!!!!!
						.remark(cf.getPrevRate()+"_"+cf.getPrevSysRate()+"_"+ cf.getCurrSysRate())
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build()
			;	
	}
}
