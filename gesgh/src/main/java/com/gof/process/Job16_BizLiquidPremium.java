package com.gof.process;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gof.dao.LiqPremiumDao;
import com.gof.entity.BizLiqPremium;
import com.gof.entity.BizLiqPremiumUd;
import com.gof.entity.LiqPremium;
import com.gof.util.EsgConstant;

import lombok.extern.slf4j.Slf4j;

/**
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job16_BizLiquidPremium {
	
	public static List<BizLiqPremium> createBizLiqPremium(String bssd, String bizDv, String modelId) {
		List<BizLiqPremium> rstList = new ArrayList<BizLiqPremium>();
		
		List<LiqPremium> liqPremList    = LiqPremiumDao.getLiqPremium(bssd, modelId);
		List<BizLiqPremiumUd> lpUserRst = LiqPremiumDao.getLiqPremiumUd(bssd, bizDv);
	
//		log.info("zzzz :{},{},{}", liqPremList.size(), modelId, bssd);
//		liqPremList.forEach(s->log.info("zzz : {}", s.getModelId()));
		
//		if(lpUserRst.isEmpty()) {
//			rstList = liqPremList.stream().map(s->s.convertTo(bizDv)).collect(toList());
//		}
//		else{
//			rstList = lpUserRst.stream().map(s->s.convertToBizLiqPremium(bssd)).collect(toList());
//		}
		log.info("aaaa : {}", lpUserRst.size());
		Map<String, Double> lqMap = new HashMap<String, Double>();
		if(lpUserRst.isEmpty()) {
			lqMap = liqPremList.stream().collect(toMap(LiqPremium::getMatCd, LiqPremium::getLiqPrem));
		}
		else{
			lqMap = lpUserRst.stream().collect(toMap(BizLiqPremiumUd::getMatCd, BizLiqPremiumUd::getLiqPrem));
			
		}
		
		List<String> tenorList = EsgConstant.getTenorList();
		Collections.reverse(tenorList);
		
		double prevLiq =0.0;
		
		for(String aa : tenorList) {
			if(lqMap.containsKey(aa)) {
				prevLiq = lqMap.get(aa);
			}
			rstList.add(build(bssd, bizDv, aa, prevLiq));
		}
		
		return rstList;
	}
	
	
	private static BizLiqPremium build(String bssd, String bizDv, String matCd, double liqPrem) {
		BizLiqPremium rst = new BizLiqPremium();
		rst.setBaseYymm(bssd);
		rst.setApplyBizDv(bizDv);
		rst.setMatCd(matCd);
		rst.setLiqPrem(liqPrem);
		rst.setApplyLiqPrem(liqPrem);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
}
