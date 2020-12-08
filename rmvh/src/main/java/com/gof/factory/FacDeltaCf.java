package com.gof.factory;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.gof.dao.MstDao;
import com.gof.entity.CfLv1Goc;
import com.gof.entity.CfLv2Delta;
import com.gof.entity.MstRunset;
import com.gof.enums.EBoolean;
import com.gof.enums.ECoa;
import com.gof.enums.ESlidingType;
import com.gof.infra.GmvConstant;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FacDeltaCf {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	
	private static List<MstRunset> deltaGroupList = new ArrayList<MstRunset>();
	private static Map<String, MstRunset> deltaGroupMap = new HashMap<String, MstRunset>();
	private static Map<String, String> runsetIdMap = new HashMap<String, String>();
	
	
	public static List<CfLv2Delta>  createFromLv2Cf(List<CfLv1Goc> cfList) {
			List<CfLv2Delta> rstList = new ArrayList<CfLv2Delta>();
	
			CfLv2Delta temp;
			if(cfList.size() == 0) {
				return new ArrayList<CfLv2Delta>();
			}
			CfLv1Goc cf = cfList.get(0);
			
			
			double currAmt=0.0;
			double prevAmt=0.0;
			double deltaAmt=0.0;
			
			Map<String, Double> runsetAmt = cfList.stream().collect(toMap(CfLv1Goc::getDeltaGroup,CfLv1Goc::getCfAmt, (s,u)->s+u));
			
	//		eventAmt.entrySet().forEach(s-> log.info("zzz : {},{}", s.getKey(), s.getValue()));
	//		cfList.forEach(s-> log.info("cf :{},{},{}", s.getRunsetId(), s.toCsv1()));
			
			for(MstRunset aa : getDeltaGroupList()) {
				currAmt = runsetAmt.getOrDefault(aa.getDeltaGroup(), 0.0); 			//TODO ;
				prevAmt = runsetAmt.getOrDefault(aa.getPriorDeltaGroup(), 0.0);
				deltaAmt = currAmt - prevAmt;
				
				
	//			log.info("End Flow : {},{}",  eventAmt.size(),aa.getRunsetId(), currAmt,prevAmt);
				
	//			if(aa.getEndFlowYn().isTrueFalse()) {
	//				log.info("End Flow : {},{}", aa.getEndFlowYn().isTrueFalse(), currAmt);
	//			}
	
	//			if(aa.getEndFlowYn().isTrueFalse() ) {
	//				deltaAmt = currAmt;
	//			}
				
				if(deltaAmt ==0.0) {
					
				}else {
					temp= CfLv2Delta.builder()
							.baseYymm(cf.getBaseYymm())					
							.gocId(cf.getGocId())
							.liabType(cf.getLiabType())					
							.runsetId(aa.getRunsetId())
//							.runsetType(aa.getDeltaCfType())
							.stStatus(cf.getStStatus())
							.endStatus(cf.getEndStatus())
							.newContYn(cf.getNewContYn())	 
							.cfKeyId(cf.getCfKeyId())
							.cfType(cf.getCfType())
							.cfTiming(cf.getCfTiming())
							.outflowYn(cf.getOutflowYn())
							.cfMonthNum(cf.getCfMonthNum())
							.cfAmt(currAmt)
							.prevCfAmt(prevAmt)
							.deltaCfAmt(deltaAmt)
							.lastModifiedBy(GmvConstant.getLastModifier())
							.lastModifiedDate(LocalDateTime.now())
							.build()
							;
					
					rstList.add(temp);
				}
			}	
			return rstList;
					
		}
		public static List<CfLv2Delta>  createFromMap(List<CfLv1Goc> cfList) {
			List<CfLv2Delta> rstList = new ArrayList<CfLv2Delta>();
	
			CfLv2Delta temp;
			if(cfList.size() == 0) {
				return new ArrayList<CfLv2Delta>();
			}
			CfLv1Goc cf = cfList.get(0);
			
			double currAmt=0.0;
			double prevAmt=0.0;
			double deltaAmt=0.0;
			
			int adjSlidingNum = 0;
			
			Map<String, Double> runsetAmt = cfList.stream().collect(toMap(CfLv1Goc::getDeltaGroup,CfLv1Goc::getCfAmt, (s,u)->s+u));
			
			
	//		eventAmt.entrySet().forEach(s-> log.info("zzz : {},{}", s.getKey(), s.getValue()));
	//		cfList.forEach(s-> log.info("cf :{},{},{}", s.getRunsetId(), s.toCsv1()));
			
			for(Map.Entry<String, MstRunset> entry : getDeltaGroupMap().entrySet()) {
				
				currAmt = runsetAmt.getOrDefault(entry.getKey(), 0.0); 			//TODO ;
				prevAmt = runsetAmt.getOrDefault(entry.getValue().getPriorDeltaGroup(), 0.0);
				deltaAmt = currAmt - prevAmt;
				
//				adjSlidingNum = !entry.getValue().getPrevYn().isTrueFalse()? 0 : DateUtil.monthBetween(entry.getValue().getCashFlowYymm(stBssd), bssd);  
				adjSlidingNum = entry.getValue().getSetlYmSlidingType().equals(ESlidingType.EOP)? 0 : DateUtil.monthBetween(entry.getValue().getCashFlowYymm(stBssd, bssd), bssd);
				
				if( entry.getValue().getSetlYmSlidingType().equals(ESlidingType.BOP) || cf.isFutureCf(0.0)) {
//				if( entry.getValue().getPrevYn().isTrueFalse() || cf.isFutureCf(0.0)) {
					if(deltaAmt !=0.0 ) {
						temp= CfLv2Delta.builder()
								.baseYymm(cf.getBaseYymm())					
								.gocId(cf.getGocId())
								.liabType(cf.getLiabType())					
								.stStatus(cf.getStStatus())
								.endStatus(cf.getEndStatus())
								//						.newContYn(cf.getNewContYn())	 
								.newContYn(EBoolean.N)
								.subKey(cf.getSubKey())
								.runsetId(getRunsetIdMap().get(entry.getKey()))
								.cfKeyId(cf.getCfKeyId())
								.cfType(cf.getCfType())
								.cfTiming(cf.getCfTiming())
								.outflowYn(cf.getOutflowYn())
								.cfMonthNum(cf.getCfMonthNum())
								.cfAmt(currAmt)
								.prevCfAmt(prevAmt)
								.deltaCfAmt(deltaAmt)
								.lastModifiedBy(GmvConstant.getLastModifier())
								.lastModifiedDate(LocalDateTime.now())
								.build()
								;
						
						rstList.add(temp);
						
					}
				}
					
			}	
			return rstList;
					
		}

	private static List<MstRunset> getDeltaGroupList(){
		if (deltaGroupList.isEmpty()) {
			deltaGroupList =  MstDao.getMstRunset().stream()
									.filter(s-> s.getCoaId().equals(ECoa.EPV))
				//					.filter(s-> !s.getPrevYn().isTrueFalse())
									.filter(s->s.getPriorDeltaGroup()!=null)
									.collect(toList());
//			deltaCfTypeList =  MstDao.getMstRunset();
			
			deltaGroupList.forEach(s-> log.info("zzz : {},{}", s.getDeltaGroup(), s.getPriorDeltaGroup()));
		}
		return deltaGroupList;
	}
	
	
	private static Map<String, String> getRunsetIdMap(){
		if (runsetIdMap.isEmpty()) {
//			runsetTypeList =  MstDao.getMstRunset().stream().filter(s->s.getPriorRunsetType()!=null).collect(toList());
			runsetIdMap = getDeltaGroupMap().values().stream().collect(toMap(MstRunset::getDeltaGroup, MstRunset::getRunsetId, (s,u)->s));
			
			runsetIdMap.entrySet().forEach(s-> log.info("zzz2 : {},{}", s.getKey(), s.getValue()));
		}
		return runsetIdMap;
	}
	
	private static  Map<String, MstRunset> getDeltaGroupMap(){
		if (deltaGroupMap.isEmpty()) {
//			runsetTypeList =  MstDao.getMstRunset().stream().filter(s->s.getPriorRunsetType()!=null).collect(toList());
			deltaGroupMap =  MstDao.getMstRunset().stream()
					.filter(s-> s.getCoaId().equals(ECoa.EPV))
					.filter(s->s.getPriorDeltaGroup()!=null)
					.collect(toMap(MstRunset::getDeltaGroup, Function.identity(), (s,u)->s ));
			
			deltaGroupMap.entrySet().forEach(s-> log.info("zzz1 : {},{}", s.getKey(), s.getValue().getPriorDeltaGroup()));
		}
		return deltaGroupMap;
	}
}
