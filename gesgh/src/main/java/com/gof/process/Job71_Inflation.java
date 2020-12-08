package com.gof.process;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.gof.dao.InflationDao;
import com.gof.entity.Inflation;
import com.gof.entity.InflationUd;
import com.gof.util.EsgConstant;
import com.gof.util.FinUtils;

import lombok.extern.slf4j.Slf4j;
/**
 *  <p> 
 *  <p> 
 *  
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job71_Inflation {

	public static List<Inflation> getInflationRate(String bssd) {
		List<Inflation> rstList = new ArrayList<Inflation>();
		Inflation tempInflation;

		int slideWindowNum = -1 * Integer.parseInt(EsgConstant.getStrConstant().getOrDefault("CPI_SLIDING_NUM", "12")); // 전년동월 대비 수익률
		
		List<InflationUd> infList = InflationDao.getPrecedingInflationUd(bssd, slideWindowNum);
	
		double curInfIndex = 0.0;
		double prevInfIndex =0.0;
		double tempInf =0.0;
	
		for (InflationUd zz : infList) {
			if(zz.getBaseYymm().equals(bssd) && zz.getInflationIndex()!= null) {
				curInfIndex = zz.getInflationIndex();
			}
			if(zz.getBaseYymm().equals(FinUtils.addMonth(bssd, slideWindowNum)) && zz.getInflationIndex()!= null) {
				prevInfIndex = zz.getInflationIndex();
			}
		}
		if(curInfIndex ==0.0 || prevInfIndex==0.0) {
			log.error("USER input CPI index error") ;
			System.exit(0);
		}
		
		tempInf = prevInfIndex==0 ? 0.0 : 100* (curInfIndex/prevInfIndex - 1);
		log.info("inflation cal rst : {},{},{},{},{}", curInfIndex, prevInfIndex, tempInf);
		
		if(curInfIndex > 0.0) {
			tempInflation = new Inflation();
			
			tempInflation.setBaseYymm(bssd);
			tempInflation.setInflationId("CPI");
			tempInflation.setInflationIndex(curInfIndex);
			tempInflation.setInflation(tempInf);		       										// 전년동월 대비 수익률
			
			tempInflation.setLastModifiedBy("ESG");
			tempInflation.setLastUpdateDate(LocalDateTime.now());
			
			rstList.add(tempInflation);
		}
		
		log.info("Job71(Inflation Calculation) creates  {} results.  They are inserted into EAS_INFLATION Table", rstList.size());
		rstList.stream().forEach(s->log.debug("Inflation Result : {}", s.toString()));
				
		return rstList;
	}
}
