package com.gof.process;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gof.dao.BizDiscountRateDao;
import com.gof.dao.BottomupDcntDao;
import com.gof.entity.BizDiscountRate;
import com.gof.entity.BizDiscountRateUd;
import com.gof.entity.BottomupDcnt;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> ESG 엔진이 산출한 할인율 대신 사용자가 외부에서 산출한 할인율을 적용하는 경우 처리          
 *  <p> Biz 영역의 데이터 산출시 사용자 입력 정보가 존재하면 우선 적용함.
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job23_BizDiscountRate {

	public static List<BizDiscountRate> getIfrsBizDcntRate(String bssd,  String irCurveId) {
		List<BizDiscountRate> rstList = getBizDcntRate(bssd, "I", irCurveId);
		
		log.info("Job 23(IFRS17 Applied Discount Rate) create {} result. They are inserted into EAS_BIZ_APLY_DCNT_RATE Table", rstList.size());
		return rstList;
	}

	public static List<BizDiscountRate> getKicsBizDcntRate(String bssd, String irCurveId) {
		List<BizDiscountRate> rstList =  getBizDcntRate(bssd, "K", irCurveId);
		
		log.info("Job 26(KICS Applied Discount Rate) create {} result. They are inserted into EAS_BIZ_APLY_DCNT_RATE Table", rstList.size());
		return rstList;
	}

	/**
	 *  <p> 사용자가 입력한 금리 기간구조가 있으면 적용하고 그외의 경우는 ESG 시스템이 산출한 BottomUp 할인율 적용   
	 *  @param baseYymm, BizDv
	 *  @return BizDiscountRate
	 *  
	 */  
	private static List<BizDiscountRate> getBizDcntRate(String bssd, String bizDv, String irCurveId) {
		List<BizDiscountRate> rstList = new ArrayList<BizDiscountRate>();
		
		List<BizDiscountRateUd> dcntRateUd = BizDiscountRateDao.getBizDiscountRateUd(bssd, bizDv);
		List<BottomupDcnt> bottomUpList = BottomupDcntDao.getTermStructure(bssd, irCurveId);
		
		if(!dcntRateUd.isEmpty()) {
			rstList.addAll(dcntRateUd.stream().map(s -> s.convertToBizDiscountRate()).collect(Collectors.toList()));
		}
		else {
			rstList.addAll(bottomUpList.stream().map(s ->s.convertTo(bizDv)).collect(toList()));
		}
		return rstList;
	}
	
	
	
}
