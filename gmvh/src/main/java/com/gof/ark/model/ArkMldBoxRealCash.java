package com.gof.ark.model;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao.CfDao;
import com.gof.dao.MapDao;
import com.gof.entity.CfLv3Real;
import com.gof.entity.MapRunsetCalc;
import com.gof.entity.MstRunset;
import com.gof.entity.RstBoxGoc;
import com.gof.enums.ECoa;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ArkMldBoxRealCash {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	
	public static Stream<RstBoxGoc> create() {

		Optional<MstRunset> tempRunset = PrvdMst.getMstRunsetList(ECoa.CASH).stream().findFirst();
		String runsetId = tempRunset.isPresent()? tempRunset.get().getRunsetId(): "REAL";
		
		List<MapRunsetCalc> mapRunsetCalcList = MapDao.getMapRunsetCalc().stream()
									.filter(s->"Y".equals(s.getMstCalc().getUseYn()))
									.filter(s->s.getRunsetId().equals(runsetId))			
									.collect(toList());
//		mapRunsetCalcList.forEach(s->log.info("aaaa : {},{},{}", s.getCalcId(), s.getRunsetId()));
		
		  return CfDao.getCfLv3RealGroupByStream(bssd)
//				  	.filter(s->s.getGocId().equals("0692_2019_1"))
				  	.map(cf-> build(cf, mapRunsetCalcList, runsetId))
					.flatMap(s->s.stream())
					 .collect(toMap(RstBoxGoc::getPk, Function.identity(), (s,u)-> s.merge(u)))
					 .entrySet().stream()
//					 .peek(s-> log.info("bbb :  {},{}", s.getKey(), s.getValue()))
					 .map(s-> s.getValue())
					 ;
				
	}
	
	private static List<RstBoxGoc> build(CfLv3Real cf, List<MapRunsetCalc> mapRunsetCalcList, String runsetId) {
		List<RstBoxGoc> rstList = new ArrayList<RstBoxGoc>();
		for(MapRunsetCalc aa :mapRunsetCalcList) {
			if(aa.getMstCalc().contains(cf.getCfKeyId())) {
//				log.info("zzzz : {},{},{}", cf.getCfType(), aa.getCalcId(), cf.toString());
				double boxValue   = aa.getMstCalc().getSignAdjust().getAdj() * cf.getCfAmt();
				
				rstList.add(RstBoxGoc.builder()
						.baseYymm(cf.getBaseYymm())
						.gocId(cf.getGocId())
						.liabType(cf.getLiabType())
						.mstRunset(aa.getMstRunset())
						.calcId(aa.getCalcId())
						.stStatus(cf.getStStatus())
						.endStatus(cf.getEndStatus())
						.newContYn(cf.getNewContYn())
						.slidingNum(0)
						.cfAmt(cf.getCfAmt())
						.prevCfAmt(0.0)
						.deltaCfAmt(cf.getCfAmt())
						.boxValue(boxValue)
						.appliedRate(0.0)
						.remark("")
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build()
						);
				
			}
		}
		
		return rstList;

	}
	
//	private static List<MstCalc> getCalcList(){
//		return  MapDao.getMapRunsetCalc().stream()
//						.filter(s->"Y".equals(s.getMstCalc().getUseYn()))
//						.filter(s->s.getRunsetId().equals("REAL"))					//TODO;
//						.map(s->s.getMstCalc())
//						.collect(toList());
//		
//	}
}
