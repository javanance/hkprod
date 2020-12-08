package com.gof.factory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.gof.entity.CfLv1Goc;
import com.gof.entity.MstRunset;
import com.gof.entity.RawCf;
import com.gof.entity.RawCfLic;
import com.gof.enums.EBoolean;
import com.gof.enums.ECompound;
import com.gof.enums.ELiabType;
import com.gof.enums.ETiming;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class FacCf {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;

	public static CfLv1Goc convertAndUpdateNew(String bssd, RawCf cf, MstRunset runset, int adjMonth, Map<Double, Double> initCurveMap, Map<Double, Double> currCurveMap, List<Integer> groupTenorList) {
		double cfAmt = cf.getOutflowYn().isTrueFalse()? cf.getAbsCfAmt(): cf.getAbsCfAmt()*-1.0;
		double pvAmt = cf.getOutflowYn().isTrueFalse()? cf.getAbsPvAmt(): cf.getAbsPvAmt()*-1.0 ;
		
		double targetDf = pvAmt / cfAmt;
		double prevDf =1.0;
		double currDf =1.0;
		
		int setlAtfPassCnt = cf.getSetlAftPassMmcnt()  ;
		ETiming timing     = cf.getCfTiming();
		
		if(cf.getCfKeyId().endsWith("_RFND")){
			setlAtfPassCnt = setlAtfPassCnt +1;
			timing = ETiming.START;
		}
		
		double cfMonthNum = cf.getSetlAftPassMmcnt()  + cf.getCfTiming().getAdj()  ;
		double tempNum=0.0;
		
		if(cf.getLiabType().equals(ELiabType.LRC) && groupTenorList.contains(cf.getSetlAftPassMmcnt())) {
//		if(groupTenorList.contains(cf.getSetlAftPassMmcnt())) {
			for(int i=61; i<= 1200; i++) {
				if(cf.getCfTiming().equals(ETiming.MID)) {
					tempNum = i + 0.5;
				}else {
					tempNum = (double)i ;
				}
				currDf = ECompound.Annualy.getDf(initCurveMap.getOrDefault(tempNum, 0.0), tempNum/12.0);
				
				if(currDf <= targetDf && targetDf < prevDf) {
					cfMonthNum = tempNum;
					break;
				}
				prevDf = currDf;
			}
		}
		
		double disRate = currCurveMap.getOrDefault(cfMonthNum, 0.0);	
		double df = ECompound.Annualy.getDf(disRate, cfMonthNum/12.0);
		double epvAmt = cfAmt * df;			

		cfMonthNum = cfMonthNum - adjMonth;
//			log.info("zzz :  {},{},{},{},{},{}", cf.getSetlYm(), cf.getCsmGrpCd(), runset.getRunsetId(), cf.getCfKeyId(), setlAtfPassCnt, cfMonthNum);
		
		
		return CfLv1Goc.builder()
						.baseYymm(bssd)
						.gocId(cf.getCsmGrpCd())
						.liabType(cf.getLiabType())
						.stStatus(PrvdMst.getContStatus(cf.getBemmStcd()))
						.endStatus(PrvdMst.getContStatus(cf.getEmmStcd()))
						.newContYn(cf.getCtrDvcd().equals("2")? EBoolean.Y : EBoolean.N )
						.subKey(cf.getSubKey())				           
						.runsetId(runset.getRunsetId())
						.deltaGroup(runset.getDeltaGroup())
						.cfKeyId(cf.getCfKeyId())
						.cfType(cf.getCfType())
						.cfTiming(timing)
						.outflowYn(cf.getOutflowYn())
						.cfMonthNum(cfMonthNum)
						.setlAftPassMmcnt(setlAtfPassCnt)
						.cfAmt(cfAmt)
						.pvAmt(pvAmt)
						.epvAmt(epvAmt)
						.absCfAmt(cf.getAbsCfAmt())
						.absPvAmt(cf.getAbsPvAmt())
						.slidingNum(adjMonth)						// TODO : column name change !!!==> tenor Adj Num
						.setlYm(cf.getSetlYm())
						.rsDivId(cf.getRsDivId())
						.csmGrpCd(cf.getCsmGrpCd())
						.bemmStcd(cf.getBemmStcd())
						.emmStcd(cf.getEmmStcd())
						.ctrDvcd(cf.getCtrDvcd())
						.remark(disRate+"_"+df)
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}
	
	
	public static CfLv1Goc convertFromLic(String bssd, RawCfLic cf, MstRunset runset, int adjMonth, Map<Double, Double> currCurveMap) {
		double cfAmt = cf.getOutflowYn().isTrueFalse()? cf.getAbsCfAmt(): cf.getAbsCfAmt()*-1.0;
		double pvAmt = cf.getOutflowYn().isTrueFalse()? cf.getAbsPvAmt(): cf.getAbsPvAmt()*-1.0 ;
		
		
		int setlAtfPassCnt = cf.getSetlAftPassMmcnt()  ;
		ETiming timing     = cf.getCfTiming();
		
		if(cf.getCfKeyId().endsWith("_RFND")){
			setlAtfPassCnt = setlAtfPassCnt +1;
			timing = ETiming.START;
		}
		
		double cfMonthNum = cf.getSetlAftPassMmcnt()  + cf.getCfTiming().getAdj()  ;
		
		double disRate = currCurveMap.getOrDefault(cfMonthNum, 0.0);	
		double df = ECompound.Annualy.getDf(disRate, cfMonthNum/12.0);
		double epvAmt = cfAmt * df;			

		cfMonthNum = cfMonthNum - adjMonth;
		
		
//		log.info("zzz :  {},{},{},{},{},{},{}", cf.getSetlYm(), cf.getCsmGrpCd(), runset.getRunsetId(), cf.getCfKeyId(), setlAtfPassCnt, cfMonthNum, disRate);
		
		
		return CfLv1Goc.builder()
						.baseYymm(bssd)
						.gocId(cf.getCsmGrpCd())
						.liabType(ELiabType.LIC)
						.stStatus(PrvdMst.getContStatus(cf.getBemmStcd()))
						.endStatus(PrvdMst.getContStatus(cf.getEmmStcd()))
						.newContYn(cf.getCtrDvcd().equals("2")? EBoolean.Y : EBoolean.N )
						.subKey(cf.getSubKey())				           
						.runsetId(runset.getRunsetId())
						.deltaGroup(runset.getDeltaGroup())
						.cfKeyId(cf.getCfKeyId())
						.cfType(cf.getCfType())
						.cfTiming(timing)
						.outflowYn(cf.getOutflowYn())
						.cfMonthNum(cfMonthNum)
						.setlAftPassMmcnt(setlAtfPassCnt)
						.cfAmt(cfAmt)
						.pvAmt(pvAmt)
						.epvAmt(epvAmt)
						.absCfAmt(cf.getAbsCfAmt())
						.absPvAmt(cf.getAbsPvAmt())
						.slidingNum(adjMonth)			// TODO:column name change !!!==> tenor Adj Num
						.setlYm(cf.getSetlYm())
						.rsDivId(cf.getRsDivId())
						.csmGrpCd(cf.getCsmGrpCd())
						.bemmStcd(cf.getBemmStcd())
						.emmStcd(cf.getEmmStcd())
						.ctrDvcd(cf.getCtrDvcd())
						.remark(disRate+"_"+df)
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}
	
	public static CfLv1Goc convert(String bssd, RawCf cf, MstRunset runset, int adjMonth, Map<Double, Double> currCurveMap) {
		double cfAmt = cf.getOutflowYn().isTrueFalse()? cf.getAbsCfAmt(): cf.getAbsCfAmt()*-1.0;
		double pvAmt = cf.getOutflowYn().isTrueFalse()? cf.getAbsPvAmt(): cf.getAbsPvAmt()*-1.0 ;
		
		int setlAtfPassCnt = cf.getSetlAftPassMmcnt()  ;
		ETiming timing     = cf.getCfTiming();
		
		if(cf.getCfKeyId().endsWith("_RFND")){
			setlAtfPassCnt = setlAtfPassCnt +1;
			timing = ETiming.START;
		}
		
		double cfMonthNum = cf.getSetlAftPassMmcnt()  + cf.getCfTiming().getAdj()  ;
		
		double disRate = currCurveMap.getOrDefault(cfMonthNum, 0.0);	
		double df = ECompound.Annualy.getDf(disRate, cfMonthNum/12.0);
		double epvAmt = cfAmt * df;			

		cfMonthNum = cfMonthNum - adjMonth;
//			log.info("zzz :  {},{},{},{},{},{}", cf.getSetlYm(), cf.getCsmGrpCd(), runset.getRunsetId(), cf.getCfKeyId(), setlAtfPassCnt, cfMonthNum);
		
		
		return CfLv1Goc.builder()
						.baseYymm(bssd)
						.gocId(cf.getCsmGrpCd())
						.liabType(cf.getLiabType())
						.stStatus(PrvdMst.getContStatus(cf.getBemmStcd()))
						.endStatus(PrvdMst.getContStatus(cf.getEmmStcd()))
						.newContYn(cf.getCtrDvcd().equals("2")? EBoolean.Y : EBoolean.N )
						.subKey(cf.getSubKey())				           
						.runsetId(runset.getRunsetId())
						.deltaGroup(runset.getDeltaGroup())
						.cfKeyId(cf.getCfKeyId())
						.cfType(cf.getCfType())
						.cfTiming(timing)
						.outflowYn(cf.getOutflowYn())
						.cfMonthNum(cfMonthNum)
						.setlAftPassMmcnt(setlAtfPassCnt)
						.cfAmt(cfAmt)
						.pvAmt(pvAmt)
						.epvAmt(epvAmt)
						.absCfAmt(cf.getAbsCfAmt())
						.absPvAmt(cf.getAbsPvAmt())
						.slidingNum(adjMonth)						// TODO : column name change !!!==> tenor Adj Num
						.setlYm(cf.getSetlYm())
						.rsDivId(cf.getRsDivId())
						.csmGrpCd(cf.getCsmGrpCd())
						.bemmStcd(cf.getBemmStcd())
						.emmStcd(cf.getEmmStcd())
						.ctrDvcd(cf.getCtrDvcd())
						.remark(disRate+"_"+df)
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}
}
