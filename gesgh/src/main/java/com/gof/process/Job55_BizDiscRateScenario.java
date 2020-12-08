package com.gof.process;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gof.dao.BizDiscRateFwdSceDao;
import com.gof.dao.DiscRateMstDao;
import com.gof.dao.DiscRateStatsDao;
import com.gof.entity.BizDiscRateFwdSce;
import com.gof.entity.BizDiscRateSce;
import com.gof.entity.BizDiscRateStat;

import lombok.extern.slf4j.Slf4j;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job55_BizDiscRateScenario {
	
	public static List<BizDiscRateSce> createBizDiscRateSce(String bssd, String bizDv, String irCurveId, String sceNo) {
		List<BizDiscRateSce> rstList = new ArrayList<BizDiscRateSce>();
		BizDiscRateSce temp;
		
		List<String> discSetting = DiscRateMstDao.getDiscRateMstList().stream()
				.filter(s ->s.isCalculable())
				.map(s ->s.getIntRateCd())
				.collect(Collectors.toList());
		
		
		List<BizDiscRateStat> bizStatList = DiscRateStatsDao.getBizDiscRateStat(bssd).stream()
													.filter(s-> s.getApplyBizDv().equals(bizDv))
													.filter(s-> discSetting.contains(s.getIntRateCd()))
													.collect(toList());
		
//		List<BizDiscRateFwdSce> fwdSceList = BizDiscRateFwdSceDao.getForwardRatesAll(bssd, bizDv, irCurveId);
		List<BizDiscRateFwdSce> fwdSceList = BizDiscRateFwdSceDao.getForwardRatesPart(bssd, bizDv, irCurveId, sceNo);
		
		double baseRate =0.0;
		for(BizDiscRateStat stat : bizStatList) {
			for(BizDiscRateFwdSce sce : fwdSceList) {
				baseRate = stat.getRegrConstant() + stat.getRegrCoef() * sce.getAvgFwdRate();
				
				temp = new BizDiscRateSce();
				temp.setBaseYymm(bssd);
				temp.setApplBizDv(bizDv);
				temp.setIntRateCd(stat.getIntRateCd());
				temp.setSceNo(sce.getSceNo());
				temp.setMatCd("M" + String.format("%04d", Integer.parseInt(sce.getFwdNo())));
				
				temp.setBaseDiscRate(baseRate);
				temp.setAdjRate(stat.getAdjRate());
				temp.setDiscRate(baseRate * stat.getAdjRate());
				
				rstList.add(temp);
			}
			
		}
		
		return rstList;
		
	}
}
