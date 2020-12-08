package com.gof.process;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.gof.dao.BottomupDcntDao;
import com.gof.dao.IrCurveHisDao;
import com.gof.entity.BizIrCurveHis;
import com.gof.entity.BottomupDcnt;
import com.gof.entity.IrCurveHis;
import com.gof.entity.SmithWilsonResult;
import com.gof.model.LiquidPremiumModel;
import com.gof.model.SmithWilsonModelCore;
import com.gof.util.EsgConstant;

/**
 *   
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
//@Slf4j
public class Job19_BizIrCruveHis {
	
	public static List<BizIrCurveHis> createBizIrCruveHis(String bssd, String bizDv, String irCurveId, double ufr, double ufrt){
		List<BizIrCurveHis>  rstList = new ArrayList<BizIrCurveHis>();
		List<String> tenorList = EsgConstant.getTenorList();
		
		Map<String, IrCurveHis> curveHisMap = IrCurveHisDao.getIrCurveHis(bssd, irCurveId).stream()
														.filter(s ->tenorList.contains(s.getMatCd()))
														.collect(toMap(s->s.getMatCd(), Function.identity()));
		
		int llpMonth = Integer.parseInt(EsgConstant.getStrConstant().getOrDefault("llp", "M0240").split("M")[1]);
		
//		Constant Liq Premium
		Map<String, Double> lpMap = LiquidPremiumModel.createLiqPremium(llpMonth, 0.0);
				
		SmithWilsonModelCore sw = new SmithWilsonModelCore(bssd, irCurveId, "0", curveHisMap, lpMap,  ufr, ufrt);
		
		List<SmithWilsonResult> rst = sw.getSmithWilsionResult();
		
		rstList = rst.stream().map(s ->s.convertToBizIrCurveHis(bizDv)).collect(toList());
//		rstList.forEach(s -> log.info("zzz : {}", s.toString()));
		
		return rstList;
	}
	
	
	public static List<BizIrCurveHis> createBizIrCruveHisFromRiskAdj(String bssd, String bizDv, String irCurveId, double ufr, double ufrt){
		List<BizIrCurveHis>  rstList = new ArrayList<BizIrCurveHis>();
		List<String> tenorList = EsgConstant.getTenorList();
		
		Map<String, BottomupDcnt> curveHisMap = BottomupDcntDao.getTermStructure(bssd, irCurveId).stream()
														.filter(s ->tenorList.contains(s.getMatCd()))
														.collect(toMap(s->s.getMatCd(), Function.identity()));
		
		int llpMonth = Integer.parseInt(EsgConstant.getStrConstant().getOrDefault("llp", "M0240").split("M")[1]);
		
//		Constant Liq Premium
		Map<String, Double> lpMap = LiquidPremiumModel.createLiqPremium(llpMonth, 0.0);
				
		SmithWilsonModelCore sw = new SmithWilsonModelCore(bssd, irCurveId, "0", curveHisMap, lpMap,  ufr, ufrt);
		
		List<SmithWilsonResult> rst = sw.getSmithWilsionResult();
		
		rstList = rst.stream().map(s ->s.convertToBizIrCurveHis(bizDv)).collect(toList());
//		rstList.forEach(s -> log.info("zzz : {}", s.toString()));
		
		return rstList;
	}
}
