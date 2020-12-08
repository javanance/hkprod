package com.gof.process;

import java.util.List;

import com.gof.entity.BizDiscountRate;
import com.gof.entity.BizDiscountRateSce;
import com.gof.entity.BizEsgParam;
import com.gof.model.HullWhite;
import com.gof.model.HullWhite4j;

import lombok.extern.slf4j.Slf4j;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job39_HullWhite4j {
	
	public static List<BizDiscountRateSce> getBizDiscountScenarioAsyc(String bssd, String bizDv, String irCurveId, List<BizDiscountRate> irCurveHisList, List<BizEsgParam> esgParam, double ufr, double ufrt,  int batchNo) {
		HullWhite4j hw4j = new HullWhite4j(bssd, irCurveHisList, esgParam, ufr, ufrt, 10);
		return  hw4j.getBizDiscountScenario(bssd, irCurveId, bizDv, batchNo);

	}
}


