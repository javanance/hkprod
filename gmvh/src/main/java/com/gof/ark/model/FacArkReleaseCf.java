package com.gof.ark.model;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.gof.ark.entity.ArkMstRunset;
import com.gof.ark.entity.ArkRawReleaseCf;
import com.gof.ark.entity.ArkReleaseCf;
import com.gof.dao.MstDao;
import com.gof.entity.MstCode;
import com.gof.enums.EBoolean;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class FacArkReleaseCf {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	
	
	public static ArkReleaseCf convert(String bssd, ArkRawReleaseCf cf, ArkMstRunset arkRunset) {
		
//		String setlYm  = arkRunset.getSetlYm(stBssd, bssd);
//		
//		int adj =0;
//		if(DateUtil.isGreaterOrEqual(bssd, setlYm)) {
//			adj = DateUtil.monthBetween(setlYm, bssd);
//		}	
//			
//		double cfMonthNum =cf.getSetlAftPassMmcnt() +  cf.getCfTiming().getAdj() - adj;
		
		int tenorAdjNum  = arkRunset.getTenorAdjNum(stBssd, bssd);
		double cfMonthNum =cf.getSetlAftPassMmcnt() - tenorAdjNum +  cf.getCfTiming().getAdj();
		
		return ArkReleaseCf.builder()
						.baseYymm(bssd)
						.gocId(cf.getCsmGrpCd())
						.liabType(ELiabType.LRC)
						.stStatus(PrvdMst.getContStatus(cf.getBemmStcd()))
						.endStatus(PrvdMst.getContStatus(cf.getEmmStcd()))
						.newContYn(cf.getCtrDvcd().equals("2")? EBoolean.Y : EBoolean.N )
//						.subKey(cf.getSubKey())				           
						.arkRunsetId(arkRunset.getArkRunsetId())
//						.runsetId(arkRunset.getRunsetId())
						.runsetId(arkRunset.getMstRunset().getRunsetId())
						.cfKeyId(cf.getCfKeyId())
						.cfType(cf.getCfType())
						.cfTiming(cf.getCfTiming())
						.outflowYn(cf.getOutflowYn())
						.cfMonthNum(cfMonthNum)
						.setlAftPassMmcnt(cf.getSetlAftPassMmcnt())
						.cfAmt(cf.getCfAmt())
						.absCfAmt(cf.getAbsCfAmt())
						.driveYm(bssd)
						.setlYm(cf.getSetlYm())
						.rsDivId(cf.getRsDivId())
						.csmGrpCd(cf.getCsmGrpCd())
						.bemmStcd(cf.getBemmStcd())
						.emmStcd(cf.getEmmStcd())
						.ctrDvcd(cf.getCtrDvcd())
						.remark("")
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}
	
}
