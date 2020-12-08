package com.gof.ark.model;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.ark.dao.ArkDao;
import com.gof.ark.entity.ArkItemRst;
import com.gof.dao.BoxDao;
import com.gof.dao.CfDao;
import com.gof.dao.MapDao;
import com.gof.dao.RatioDao;
import com.gof.dao.RstDao;
import com.gof.entity.CfLv1Goc;
import com.gof.entity.CfLv3Real;
import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MapRunsetCalc;
import com.gof.entity.MstCalc;
import com.gof.entity.MstRollFwd;
import com.gof.entity.MstRunset;
import com.gof.entity.RatioDac;
import com.gof.entity.RstBoxGoc;
import com.gof.entity.RstDac;
import com.gof.enums.ECfType;
import com.gof.enums.ECoa;
import com.gof.enums.ERollFwdType;
import com.gof.enums.ERunsetType;
import com.gof.factory.FacRstDac;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdAcct;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ArkMdlDac {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	
	private static Map<ERollFwdType, List<MapJournalRollFwd>> dac1JournalMap = new HashMap<ERollFwdType, List<MapJournalRollFwd>>();
	private static Map<ERollFwdType, List<MapJournalRollFwd>> dac2JournalMap = new HashMap<ERollFwdType, List<MapJournalRollFwd>>();
	
	public static Stream<RstDac> createConversion() {
		List<RstDac> rstList = new ArrayList<RstDac>();
		
		rstList.addAll(createConversion(ECoa.DAC1, ECfType.DAC1));
		rstList.addAll(createConversion(ECoa.DAC2, ECfType.DAC2));
		
		return rstList.stream(); 
	}
	

	private static List<RstDac> createConversion(ECoa coa, ECfType dacCfType) {
		List<RstDac> rstList = new ArrayList<RstDac>();
		
//		MstRunset mstRunset = PrvdMst.getMstRunsetMap(ECoa.EPV, ERunsetType.CURR_CLOSING);
		
		List<String> currRunsetList = PrvdMst.getMstRunsetList(ECoa.EPV).stream()
											.filter(s->ERunsetType.CURR_CLOSING.equals(s.getRunsetType()))
											.map(MstRunset::getRunsetId).collect(toList());
		
		log.info("zzzz : {},{}", currRunsetList.size(), dacCfType);
		
		Map<String, Double> dacMap =  ArkDao.getArkItemRstStream(stBssd)
											.filter(s-> currRunsetList.contains(s.getRunsetId()))		
											.peek(s->log.info("zzz : {},{},{}", s.getArkRunsetId(), s.getItemId()))
											.filter(s-> s.getItemId().equals("FUTURE_"+dacCfType.name()))
											.collect(toMap(ArkItemRst::getGocId, ArkItemRst::getItemAmt, (s,u)->s+u))
											;

		
		MstRollFwd mstRollFwd 	= PrvdMst.getMstRollFwd(ERollFwdType.CURR_CLOSE);
		
		
		String remark 			= "Conversion";
//		MstCalc	mstCalc 	  	= PrvdMst.getMstCalcDeltaSum();
		MstCalc	mstCalc 	  	= getJournalList(ERollFwdType.CURR_CLOSE, coa).isEmpty()	?	PrvdMst.getMstCalcDeltaSum(): 
											getJournalList(mstRollFwd.getRollFwdType(), coa).get(0).getMstCalc();
		
		for(Map.Entry<String, Double> entry : dacMap.entrySet()) {
			rstList.add( FacRstDac.build(stBssd, entry.getKey(), coa, mstRollFwd, mstCalc.getCalcId(), mstCalc, 0.0, 0.0, entry.getValue(), remark) );
		}
		
		return rstList;
	}
	
	
	
	
	private static List<MapJournalRollFwd> getJournalList(ERollFwdType rollFwd, ECoa coa) {	
		if(dac1JournalMap.isEmpty()) {
			dac1JournalMap =MapDao.getMapJournalRollFwd().stream()
								.filter(s->s.getUseYn().isTrueFalse())
								.filter(s->s.hasCoa(ECoa.DAC1))
								.collect(groupingBy(MapJournalRollFwd::getRollFwdType,toList()));
		}
		
		if(dac2JournalMap.isEmpty()) {
			dac2JournalMap =MapDao.getMapJournalRollFwd().stream()
								.filter(s->s.getUseYn().isTrueFalse())
								.filter(s->s.hasCoa(ECoa.DAC2))
								.collect(groupingBy(MapJournalRollFwd::getRollFwdType,toList()));
								
		}
		
		if(coa.equals(ECoa.DAC2)) {
			return dac2JournalMap.getOrDefault(rollFwd, new ArrayList<MapJournalRollFwd>());
		}
		else {
			return dac1JournalMap.getOrDefault(rollFwd, new ArrayList<MapJournalRollFwd>());
		}
	}
	
}
