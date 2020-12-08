package com.gof.factory;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.gof.dao.MstDao;
import com.gof.entity.CfLv1Goc;
import com.gof.entity.CfLv4Df;
import com.gof.entity.DfLv3Flat;
import com.gof.entity.MstRunset;
import com.gof.enums.EBoolean;
import com.gof.enums.ECoa;
import com.gof.enums.ELiabType;
import com.gof.enums.ESlidingType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdAcct;
import com.gof.provider.PrvdMst;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FacCfDf {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String currEirType	=GmvConstant.CURR_EIR_TYPE;
	
	private static List<MstRunset> deltaGroupList = new ArrayList<MstRunset>();
	
	public static CfLv4Df build(CfLv1Goc cf,  DfLv3Flat df) {
		double prevRate= 0.0;
		
		int slide 		=  DateUtil.monthBetween(stBssd, bssd);
//		String prevYymm =  DateUtil.addMonthToString(cf.getBaseYymm(), -1 * cf.getSlidingNum());
//		
		if(cf.getSlidingNum() == slide) {
			 prevRate =  df.getFirstNewContRate(); 
		}else if(cf.getSlidingNum() == slide -1) {
			 prevRate =  df.getSecondNewContRate(); 
		}else if (cf.getSlidingNum() == slide - 2 ) {
			 prevRate =  df.getThirdNewContRate(); 
		}
		else {
			 prevRate =  df.getPrevRate(); 
		}
		
		MstRunset mstRunset  = PrvdMst.getMstRunset(cf.getRunsetId());
//		String prevCurveYymm = mstRunset.getNewContYn().isTrueFalse()? PrvdAcct.getCurveYymm(cf.getGocId(), GmvConstant.NEW_CONT_RATE, mstRunset): stBssd;
		String prevCurveYymm = mstRunset.getRunsetType().isNewContYn()? PrvdAcct.getCurveYymm(cf.getGocId(), GmvConstant.NEW_CONT_RATE, mstRunset): stBssd;

//		if(prevCurveYymm.equals(df.getInitCurveYymm())) {
//			prevRate = df.getInitRate();
//		}
//		else if(prevCurveYymm.equals(stBssd)) {
////			prevRate = df.getFirstNewContRate();
//			prevRate = df.getPrevRate();
//		}
//		else if(prevCurveYymm.equals(DateUtil.addMonthToString(stBssd, 1))) {
//			prevRate = df.getSecondNewContRate();
//		}
//		else if(prevCurveYymm.equals(DateUtil.addMonthToString(stBssd, 2))) {
//			prevRate = df.getThirdNewContRate();
//		}
//		else {
//			prevRate =df.getPrevRate();
//		}
		
		
		
		return CfLv4Df.builder().baseYymm(cf.getBaseYymm())
				.gocId(cf.getGocId())
				.liabType(cf.getLiabType())
				.stStatus(cf.getStStatus())
				.endStatus(cf.getEndStatus())
				.newContYn(cf.getNewContYn())
				.subKey(cf.getSubKey())
				.runsetId(cf.getRunsetId())
				.deltaGroup(cf.getDeltaGroup())
				.cfKeyId(cf.getCfKeyId())
				.cfType(cf.getCfType())
				.cfTiming(cf.getCfTiming())
				.outflowYn(cf.getOutflowYn())
				.cfMonthNum(cf.getCfMonthNum())
				.setlAftPassMmcnt(cf.getSetlAftPassMmcnt())
				.cfAmt(cf.getCfAmt())
//				.epvAmt(cf.getEpvAmt())
				.epvAmt(0.0)
				.prevCfAmt(0.0)
				.deltaCfAmt(cf.getCfAmt())
				
				.slidingNum(cf.getSlidingNum())
				.eirYn(df.getEirYn())
				.initCurveYymm(df.getInitCurveYymm())
				.prevCurveYymm(prevCurveYymm)
				
				.initRate(df.getInitRate())
				.prevRate(prevRate)
				.prevSysRate(getPrevSysRate(cf.getLiabType(), df))
				.currRate(df.getCurrRate())
				.currSysRate(df.getCurrSysRate())
				
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
	}
	
	public static List<CfLv4Df>  buildFromGeneration(List<CfLv1Goc> cfList, DfLv3Flat df) {
		List<CfLv4Df> rstList = new ArrayList<CfLv4Df>();
		
		double currAmt		= 0.0;
		double prevAmt		= 0.0;
		double deltaAmt		= 0.0;
		int adjSlidingNum 	= 0;
		double prevRate 	= 0.0;
		int slide			= DateUtil.monthBetween(stBssd, bssd);
		
		Map<String, CfLv1Goc> cfMap   = cfList.stream().collect(toMap(CfLv1Goc::getDeltaGroup, Function.identity(), (s,u)->s));
		Map<String, Double> runsetAmt = cfList.stream().collect(toMap(CfLv1Goc::getDeltaGroup, CfLv1Goc::getCfAmt, (s,u)->s+u));
		
		for(MstRunset aa : getDeltaGroupList()) {
			if(cfMap.containsKey(aa.getDeltaGroup()) || cfMap.containsKey(aa.getPriorDeltaGroup())) {
				currAmt = runsetAmt.getOrDefault(aa.getDeltaGroup(), 0.0); 			
				prevAmt = runsetAmt.getOrDefault(aa.getPriorDeltaGroup(), 0.0);
				deltaAmt = currAmt - prevAmt;
				
//				adjSlidingNum = !aa.getPrevYn().isTrueFalse()? 0 : DateUtil.monthBetween(aa.getCashFlowYymm(stBssd), bssd);
				
				adjSlidingNum = aa.getSetlYmSlidingType().equals(ESlidingType.EOP)? 0 : DateUtil.monthBetween(aa.getCashFlowYymm(stBssd, bssd), bssd);
				
//				log.info("zzzz : {},{}", aa.getDeltaGroup(), cfList);
				
				CfLv1Goc cf = cfMap.getOrDefault(aa.getDeltaGroup(), cfMap.get(aa.getPriorDeltaGroup()));
				
				if(cf.getSlidingNum() == slide ) {
					prevRate =  df.getFirstNewContRate(); 
				}else if(cf.getSlidingNum() == slide -1) {
					prevRate =  df.getSecondNewContRate(); 
				}else if (cf.getSlidingNum() == slide - 2 ) {
					prevRate =  df.getThirdNewContRate(); 
				}
				else {
					prevRate =  df.getPrevRate(); 
				}
				
				if( !aa.getSetlYmSlidingType().equals(ESlidingType.EOP) || cf.isFutureCf(0.0)) {
//				if( aa.getPrevYn().isTrueFalse() || cf.isFutureCf(0.0)) {
					if(currAmt!=0 || prevAmt!=0) {
						rstList.add( 
								CfLv4Df.builder().baseYymm(cf.getBaseYymm())
								.gocId(cf.getGocId())
								.liabType(cf.getLiabType())
								.stStatus(cf.getStStatus())
								.endStatus(cf.getEndStatus())
//								.newContYn(cf.getNewContYn())
								.newContYn(EBoolean.N)				//TODO : check!!!!! 
								.subKey(cf.getSubKey())
								.runsetId(aa.getRunsetId())
								.deltaGroup(aa.getDeltaGroup())
								.cfKeyId(cf.getCfKeyId())
								.cfType(cf.getCfType())
								.cfTiming(cf.getCfTiming())
								.outflowYn(cf.getOutflowYn())
								.cfMonthNum(cf.getCfMonthNum())
								.setlAftPassMmcnt(cf.getSetlAftPassMmcnt())
								.cfAmt(currAmt)
								.epvAmt(0.0)
								.prevCfAmt(prevAmt)
								.deltaCfAmt(deltaAmt)
//								.slidingNum(0)						//TODO :CHECK !!!! DELTA_CF ==> SLIDING_NUM =0			
								.slidingNum(adjSlidingNum)			//TODO :CHECK !!!! DELTA_CF ==> SLIDING_NUM =0			
								.eirYn(df.getEirYn())
								.initCurveYymm(df.getInitCurveYymm())
								.prevCurveYymm(df.getPrevCurveYymm())
								.initRate(df.getInitRate())
//								.prevRate(df.getPrevRate())							
								.prevRate(prevRate)							
//								.prevSysRate(df.getPrevSysRate())
								.prevSysRate(getPrevSysRate(cf.getLiabType(), df))
								.currRate(df.getCurrRate())
								.currSysRate(df.getCurrSysRate())
								
								.lastModifiedBy(GmvConstant.getLastModifier())
								.lastModifiedDate(LocalDateTime.now())
								.build()
								);
						if(cf.getCfMonthNum()==9) {
							log.info("qqqqq : {},{},{},{},{},{},{}", aa.getDeltaGroup(), cf.getRunsetId(),cf.getSlidingNum(), slide, prevRate, df.getFirstNewContRate(), df.getSecondNewContRate());
						}
					}
				}
			}
			
			
		}
		return rstList;
	}
	
	private static double getPrevSysRate(ELiabType liabType, DfLv3Flat df) {
		double prevSysRate =0.0;
		if(currEirType.equals("EACH")) {
			prevSysRate = liabType.equals(ELiabType.LRC)? df.getPrevLrcSysRate(): df.getPrevLicSysRate();
		}
		else if(currEirType.equals("LRC")) {
			prevSysRate =  df.getPrevLrcSysRate();
		}
		else {
			prevSysRate =  df.getPrevSysRate();
		}
		return prevSysRate;
	}
	private static List<MstRunset> getDeltaGroupList(){
		if (deltaGroupList.isEmpty()) {
			deltaGroupList =  MstDao.getMstRunset().stream()
									.filter(s-> s.getCoaId().equals(ECoa.EPV))
//									.filter(s-> !s.getPrevYn().isTrueFalse())
									.filter(s->s.getPriorDeltaGroup()!=null)
									.collect(toList());
//			deltaCfTypeList =  MstDao.getMstRunset();
			deltaGroupList.forEach(s-> log.info("zzz : {},{}", s.getDeltaGroup(), s.getPriorDeltaGroup()));
		}
		return deltaGroupList;
	}
}
