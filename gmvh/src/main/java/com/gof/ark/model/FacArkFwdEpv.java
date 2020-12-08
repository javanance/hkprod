package com.gof.ark.model;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gof.ark.entity.ArkFwdEpv;
import com.gof.ark.entity.ArkMstRunset;
import com.gof.ark.entity.ArkRawEpv;
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
public class FacArkFwdEpv {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	
	public static List<ArkFwdEpv> convert(String bssd, ArkRawEpv epv, ArkMstRunset arkRunset) {
		List<ArkFwdEpv> rstList = new ArrayList<ArkFwdEpv>();
		int slidingNum = DateUtil.monthBetween(stBssd, bssd);
		int loopNum = Math.min(epv.getFwdEpvMap().size(), slidingNum+1);
		
		for(int i=0; i < loopNum; i++) {
			rstList.add(buildFwdEpv(bssd, epv, arkRunset, i));
		}
		
		return rstList;
	}
	
	
	private static ArkFwdEpv buildFwdEpv(String bssd, ArkRawEpv epv, ArkMstRunset arkRunset, int fwdNum) {
//		log.info("eeeee : {},{},{}", arkRunset.getArkRunsetId(), epv.getTtrmBelAmt(), epv.getMm1AfBelAmt());
		return ArkFwdEpv.builder()
					.baseYymm(bssd)
					.gocId(epv.getCsmGrpCd())
					.liabType(ELiabType.LRC)
					.stStatus(PrvdMst.getContStatus(epv.getBemmStcd()))
					.endStatus(PrvdMst.getContStatus(epv.getEmmStcd()))
					.newContYn(epv.getCtrDvcd().equals("2")? EBoolean.Y : EBoolean.N )
					.arkRunsetId(arkRunset.getArkRunsetId())
//					.runsetId(arkRunset.getRunsetId())
					.runsetId(arkRunset.getMstRunset().getRunsetId())
					.fwdNum(fwdNum)
					.epvAmt(epv.getFwdEpvMap().get(fwdNum))
					.remark(arkRunset.getRsDivId())
					.lastModifiedBy(GmvConstant.getLastModifier())
					.lastModifiedDate(LocalDateTime.now())
					.build()
					;
	}

	
	
}
