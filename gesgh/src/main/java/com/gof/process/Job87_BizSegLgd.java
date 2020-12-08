package com.gof.process;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.gof.dao.SegLgdDao;
import com.gof.entity.BizSegLgd;
import com.gof.entity.SegLgd;

import lombok.extern.slf4j.Slf4j;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job87_BizSegLgd {

	public static List<BizSegLgd> getBizSegLgd(String bssd, String bizDv) {
		
		List<BizSegLgd> rstList = new ArrayList<BizSegLgd>();
		BizSegLgd tempPd;
		
		List<SegLgd> lgdList = SegLgdDao.getSegLgd(bssd, bizDv);   
		
		for (SegLgd aa : lgdList) {
			tempPd = new BizSegLgd();

			tempPd.setBaseYymm(bssd);
			tempPd.setApplyBizDv(bizDv);
			tempPd.setSegId(aa.getSegId());
			tempPd.setLgdCalcTypCd(aa.getLgdCalcTypCd());
			
			tempPd.setApplyLgd(aa.getLgd());
			tempPd.setVol(0.0);
			
			tempPd.setLastModifiedBy("ESG");
			tempPd.setLastUpdateDate(LocalDateTime.now());
			
			rstList.add(tempPd);
			
		}
		log.info("Job (Biz {}  Applied Segment LGD) create {} result and insert into EAS_BIZ_APPL_SEG_LGD Table ", bizDv, rstList.size());
		return rstList;
	}
	
}
