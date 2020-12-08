package com.gof.process;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gof.dao.EsgRandomDao;
import com.gof.dao.IrCurveHisDao;
import com.gof.dao.StdAssetDao;
import com.gof.dao.StockImpliedVolDao;
import com.gof.entity.BizEsgParam;
import com.gof.entity.EsgMst;
import com.gof.entity.EsgRandom;
import com.gof.entity.IrCurveHis;
import com.gof.entity.IrSce;
import com.gof.entity.StdAssetMst;
import com.gof.entity.StockImpliedVolUd;
import com.gof.entity.StockParamHis;
import com.gof.enums.EIrModelType;
import com.gof.enums.EJob;
import com.gof.model.CIR;
import com.gof.model.HullWhite;
import com.gof.model.HullWhite2Factor;
import com.gof.model.Vasicek;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job06_StockParam {
	
	public static List<StockParamHis> createStockParamHis(String bssd, StdAssetMst asset) {
		LocalDate baseDate = DateUtil.convertFrom(DateUtil.toEndOfMonth(bssd));
		String stdAsstCd =asset.getStdAsstCd();
		
		List<StockParamHis> rstList = new ArrayList<StockParamHis>();
		List<StockImpliedVolUd> impliedVol = StockImpliedVolDao.getStockImpliedVolUd(bssd, stdAsstCd);
		
		int currDateNum=0;
		int prevDateNum =0;
		int diffDateNum = 0;
		
		double currVariance = 0.0;
		double prevVariance = 0.0;
		double diffWeigthedVariance = 0.0;
		
		double fwdVariance = 0.0;
		
		for(StockImpliedVolUd aa : impliedVol) {
			currDateNum = aa.getResidualDateNum(baseDate) ;
			currVariance =aa.getImpliedVol() * aa.getImpliedVol();
			
			diffWeigthedVariance = currDateNum * currVariance - prevDateNum * prevVariance;
			diffDateNum = currDateNum - prevDateNum;
			
			fwdVariance = diffWeigthedVariance / diffDateNum ;
			
//			log.info("fwd vol :{}, {}", aa.getResidualDateNum(baseDate), fwdVariance);
//			localVolMap.put(diffDateNum, fwdVariance);
			
			prevDateNum = currDateNum;
			prevVariance = currVariance;
			
			rstList.add(StockParamHis.builder()
							.baseYymm(aa.getBaseYymm())
							.stdAsstCd(aa.getStdAsstCd())
							.paramCalcCd("LOCAL_VOL")
							.paramTypCd("SIGMA")
							.matDayNum(currDateNum)
							.paramVal(Math.sqrt(fwdVariance))
							.lastModifiedBy(EJob.ESG6.name())
							.lastUpdateDate(LocalDateTime.now())
							.build()
					);	
		}
		return rstList;
	}
	
}

