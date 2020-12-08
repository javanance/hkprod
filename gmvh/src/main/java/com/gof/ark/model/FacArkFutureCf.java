package com.gof.ark.model;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.gof.ark.entity.ArkFutureCf;
import com.gof.ark.entity.ArkMstRunset;
import com.gof.ark.entity.ArkRawEpvDetail;
import com.gof.ark.entity.ArkRawFutureCf;
import com.gof.dao.MstDao;
import com.gof.entity.MstCode;
import com.gof.enums.EBoolean;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class FacArkFutureCf {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	
	public static ArkFutureCf convert(String bssd, ArkRawEpvDetail cf, ArkMstRunset arkRunset) {
		return ArkFutureCf.builder()
						.baseYymm(bssd)
						.gocId(cf.getCsmGrpCd())
						.liabType(ELiabType.LRC)
						.stStatus(PrvdMst.getContStatus(cf.getBemmStcd()))
						.endStatus(PrvdMst.getContStatus(cf.getEmmStcd()))
						.newContYn(cf.getCtrDvcd().equals("2")? EBoolean.Y : EBoolean.N )
						.arkRunsetId(arkRunset.getArkRunsetId())
						.runsetId(arkRunset.getMstRunset().getRunsetId())
						.cfKeyId(cf.getCfType().name())
						.cfType(cf.getCfType())
						.cfAmt(cf.getCfAmt())
						.epvAmt(cf.getEpvAmt())
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
	
	
	public static ArkFutureCf convertFrom(String bssd, ArkRawFutureCf cf, ArkMstRunset arkRunset) {
		return ArkFutureCf.builder()
						.baseYymm(bssd)
						.gocId(cf.getCsmGrpCd())
						.liabType(ELiabType.LRC)
						.stStatus(PrvdMst.getContStatus(cf.getBemmStcd()))
						.endStatus(PrvdMst.getContStatus(cf.getEmmStcd()))
						.newContYn(cf.getCtrDvcd().equals("2")? EBoolean.Y : EBoolean.N )
						.arkRunsetId(arkRunset.getArkRunsetId())
						.runsetId(arkRunset.getMstRunset().getRunsetId())
						.cfKeyId(cf.getCfKeyId())
						.cfType(cf.getCfType())
						.cfAmt(cf.getCfAmt())
						.epvAmt(cf.getPvAmt())
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
