package com.gof.process;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.gof.dao.SegLgdDao;
import com.gof.entity.SegLgd;
import com.gof.entity.SegLgdUd;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> 
 *  
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job86_SegLgd {

	public static List<SegLgd> getSegLgd(String bssd) {
		List<SegLgd> rstList = new ArrayList<SegLgd>();
		SegLgd tempLgd;
		
		List<SegLgdUd> lgdList = SegLgdDao.getSegLgdUd(bssd);
		
//		lgdList.forEach(s -> log.info("zzz  : {},{},{}", s.getApplStYymm(), s.getSegId(), s.getLgdCalcTypCd()));
//		Map<String, Double> volMap =  getVolMap(bssd);
//		volMap.entrySet().stream().forEach(s -> logger.info("aaa : {},{}", s.getKey(), s.getValue()));
		
		for (SegLgdUd aa : lgdList) {
			tempLgd = new SegLgd();

			tempLgd.setBaseYymm(bssd);
			tempLgd.setLgdCalcTypCd(aa.getLgdCalcTypCd());
			tempLgd.setSegId(aa.getSegId());
			tempLgd.setLgd(aa.getLgd());
//			tempLgd.setVol(Math.max(0.0001, volMap.getOrDefault(aa.getSegId(), 0.0)));

			tempLgd.setLastModifiedBy("ESG");
			tempLgd.setLastUpdateDate(LocalDateTime.now());
			
			rstList.add(tempLgd);
		}
		
		log.info("Job86( Segment LGD Calculation) creates  {} results.  They are inserted into EAS_SEG_LGD Table", rstList.size());
		rstList.stream().forEach(s->log.debug("Segment LGD Result : {}", s.toString()));
		return rstList;
	}

}
