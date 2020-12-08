package com.gof.process;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gof.dao.DiscRateDao;
import com.gof.dao.DiscRateMstDao;
import com.gof.dao.DiscRateStatsDao;
import com.gof.entity.DiscRateHis;
import com.gof.entity.DiscRateMst;
import com.gof.entity.DiscRateStats;
import com.gof.entity.InvestManageCostUd;
import com.gof.enums.EBaseMatCd;
import com.gof.util.EsgConstant;

import lombok.extern.slf4j.Slf4j;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job61_DiscRateStatKics {
	
	public static List<DiscRateStats> createDiscRateStat(String bssd) {
		List<DiscRateStats> rstList = new ArrayList<DiscRateStats>();
		
		String matCd    = EsgConstant.getStrConstant().getOrDefault("KICS_DICS_MAT", "M0001");
		String avgNum   =  EsgConstant.getStrConstant().getOrDefault("KICS_DICS_AVG", "1");
		
		String indiVari = EBaseMatCd.getBaseMatCdEnum(matCd).getKTBCode();
		
		List<DiscRateMst> settingList = DiscRateMstDao.getDiscRateMstList();
		List<DiscRateMst> calcSettingList = settingList.stream()
//													.filter(s -> s.isCalculable())
													.collect(Collectors.toList());

		Map<String, Double> adjMap = getDiscRateAdjForCalc(bssd, calcSettingList);
		Map<String, Double> costMap = getInvCostForCalc(bssd, calcSettingList);
		
		double adj=0.0;
		double costRate=0.0;
		for(DiscRateMst aa : calcSettingList) {
			
			DiscRateStats rst = new DiscRateStats();
			
			adj = adjMap.getOrDefault(aa.getIntRateCd(), 0.0);
			costRate = costMap.getOrDefault(aa.getIntRateCd(), 0.0);
					
			rst.setBaseYymm(bssd);
//			rst.setDiscRateCalcTyp(indiVari +"_"+ avgNum);
			rst.setDiscRateCalcTyp("K");
			
			rst.setIntRateCd(aa.getIntRateCd());
			rst.setDepnVariable("BASE_DISC");
			rst.setIndpVariable(indiVari);
			
			rst.setRegrCoef(adj);
			rst.setRegrConstant(-1.0* adj* costRate);
			rst.setRemark("INV_COST_RATE:"+costRate);
			rst.setAvgNum(Double.parseDouble(avgNum));
			rst.setLastModifiedBy("ESG");
			rst.setLastUpdateDate(LocalDateTime.now());
			
			rstList.add(rst);
		}
		log.info("RST : {}", rstList.size());
		return rstList;
	}

	
	
	
	private static Map<String, Double> getDiscRateAdjForCalc(String bssd, List<DiscRateMst> discMstList) {
		Map<String, Double> rstMap = new HashMap<String, Double>();

		Map<String, List<DiscRateHis>> discRateHisMap = DiscRateDao.getDiscRateHis(bssd, -11).stream().collect(groupingBy(s-> s.getIntRateCd(), toList()));
		
		
		for(DiscRateMst aa : discMstList) {
			double sumAdj 	= 0.0;
			int    cntAdj	= 0  ;
			double avgAdj 	= 0.0;

			if(discRateHisMap.containsKey(aa.getIntRateCd())) {
				for(DiscRateHis zz : discRateHisMap.get(aa.getIntRateCd())) {
					if(zz.getMgtAsstYield()!=null) {
						sumAdj = sumAdj + (zz.getMgtAsstYield()==0.0 ? 0.0 : zz.getApplDiscRate() / zz.getMgtAsstYield());
						cntAdj = cntAdj + (zz.getMgtAsstYield()==0.0 ?   0 : 1); 
					}
				}
				if(cntAdj==0) {
					avgAdj = 1.0;
				}else {
					avgAdj = sumAdj / cntAdj;
				}
//				log.info("kkk : {},{},{},{}", aa.getIntRateCd(), avgAdj, sumAdj, cntAdj);
				rstMap.put(aa.getIntRateCd(), avgAdj);
			}
		}	
			
		return  rstMap;
	}

	private static Map<String, Double> getInvCostForCalc(String bssd, List<DiscRateMst> discMstList) {
		Map<String, Double> rstMap = new HashMap<String, Double>();
		
		List<InvestManageCostUd> investCostList = DiscRateStatsDao.getUserInvMgtCost(bssd);
		
		Map<String, Double> investCostMap = investCostList.stream().collect(toMap(s->s.getMgtAsstTyp(), s->s.getInvCostRate()));
					
		for(DiscRateMst mm : discMstList) {
			if(investCostMap.containsKey(mm.getMgtAsstTyp())) {
				rstMap.put(mm.getIntRateCd(), investCostMap.get(mm.getMgtAsstTyp()));
			}
		}
		return rstMap;
	}
	
	
	
	
}
