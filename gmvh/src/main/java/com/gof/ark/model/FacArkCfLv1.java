package com.gof.ark.model;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.gof.ark.entity.ArkMstRunset;
import com.gof.ark.entity.ArkRawCfEir;
import com.gof.dao.MstDao;
import com.gof.entity.CfLv1Goc;
import com.gof.entity.MstCode;
import com.gof.enums.EBoolean;
import com.gof.enums.ECfType;
import com.gof.enums.ECompound;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class FacArkCfLv1 {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	
	public static CfLv1Goc convert(String bssd, ArkRawCfEir cf, ArkMstRunset arkRunset, int tenorAdjNum, Map<Double, Double> currCurveMap) {
		double cfAmt = cf.getCfAmt();
		
//		int setlAtfPassCnt = cf.getSetlAftPassMmcnt()  ;
//		ETiming timing     = cf.getCfTiming();
		
		double cfMonthNum = cf.getSetlAftPassMmcnt()  + cf.getCfTiming().getAdj()  ;
		
		double disRate = currCurveMap.getOrDefault(cfMonthNum, 0.0);				
		double df = ECompound.Annualy.getDf(disRate, cfMonthNum/12.0);
		double epvAmt = cfAmt * df;			

		cfMonthNum = cfMonthNum - tenorAdjNum;
		
		return CfLv1Goc.builder()
						.baseYymm(bssd)
						.gocId(cf.getCsmGrpCd())
						.liabType(ELiabType.LRC)
						.stStatus(PrvdMst.getContStatus(cf.getBemmStcd()))
						.endStatus(PrvdMst.getContStatus(cf.getEmmStcd()))
						.newContYn(cf.getCtrDvcd().equals("2")? EBoolean.Y : EBoolean.N )
						.subKey("ALL")				           
						.runsetId(arkRunset.getMstRunset().getRunsetId())
						.deltaGroup(arkRunset.getMstRunset().getDeltaGroup())
						.cfKeyId("ALL")
						.cfType(ECfType.ALL)
						.cfTiming(cf.getCfTiming())
						.outflowYn(EBoolean.Y)
						.cfMonthNum(cfMonthNum)
						.setlAftPassMmcnt(cf.getSetlAftPassMmcnt())
						.cfAmt(cfAmt)
						.pvAmt(0.0)
						.epvAmt(epvAmt)
						.absCfAmt(cfAmt)
						.absPvAmt(0.0)
						.slidingNum(tenorAdjNum)						// TODO : column name change !!!==> tenor Adj Num
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
