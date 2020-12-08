package com.gof.process;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import com.gof.dao.IrCurveHisDao;
import com.gof.entity.BizDiscountRateSce;
import com.gof.entity.BizIrCurveSce;
import com.gof.entity.BottomupDcnt;
import com.gof.entity.IrCurve;
import com.gof.entity.IrSce;
import com.gof.entity.SmithWilsonResult;
import com.gof.interfaces.IIntRate;
import com.gof.model.SmithWilsonModelCore;
import com.gof.model.TermStructureModel;
import com.gof.util.EsgConstant;

import lombok.extern.slf4j.Slf4j;

/**
*  
* @author takion77@gofconsulting.co.kr 
* @version 1.0
*/
@Slf4j
public class Job24A_BottomUpScenario {

	public static List<BizDiscountRateSce> createBottomUpScenarioSw(String bssd, String bizDv, IrCurve curveMst) {
		List<BizDiscountRateSce> rst = new ArrayList<BizDiscountRateSce>();
		List<String> tenorList = EsgConstant.getTenorList();
		int cnt=1;
		
		Map<String, Double> lpMap =new HashMap<String, Double>();
//		1. 시나리오 번호별로 Term Structure 시나리오 를 추출
		Map<String, List<IrSce>> sceMap = IrCurveHisDao.getIrCurveSce(bssd, curveMst.getIrCurveId()).stream().collect(groupingBy(s -> s.getSceNo(), toList()));
		
//		2. 주어진 Tenor 의  curve + spread 로 Term Structure 구성 ==> 3. smith wilson 적용 
		double ufr  = EsgConstant.getSmParam().get(curveMst.getCurCd()).getUfr();
		double ufrt = EsgConstant.getSmParam().get(curveMst.getCurCd()).getUfrT();	
		
//		3. 시나리오 번호별로 반복 수행
		for(Map.Entry<String, List<IrSce>> entry: sceMap.entrySet()){
			Map<String, IrSce> curveMap = entry.getValue().stream()
												.filter(s-> tenorList.contains(s.getMatCd()))
												.collect(toMap(s->s.getMatCd(), Function.identity()));
			
			SmithWilsonModelCore sw = new SmithWilsonModelCore(bssd, curveMst.getIrCurveId(), entry.getKey(), curveMap, lpMap, ufr, ufrt);
			rst.addAll(sw.getSmithWilsionResult().stream().map(s->s.convertToBizDiscountRateSce(bizDv, lpMap)).collect(toList()));
			
			log.info("Scenario Creation : {}/{} is processed ", cnt++, sceMap.size());
		}
		log.info("Job(Bottom Up Ir Rate Calculation) creates {} results for {}. inserted into EAS_BOTTOMUP_DCNT",rst.size(),curveMst.getIrCurveId());
		return rst;
	}


//	For Async Method
	public static List<BizDiscountRateSce> createBottomUpScenario(String bssd, IrCurve curveMst,  String sceNo, List<IrSce> curveList, double ufr, double ufrt) {
				
			log.info("Biz Dcnt Scenario for {} SceNo and Thread Name: {}, {}",sceNo, Thread.currentThread().getName());
			
			Map<String, Double> lpMap =new HashMap<String, Double>();
			Map<String, ? extends IIntRate> curveMap = curveList.stream().collect(toMap(s->s.getMatCd(), Function.identity()));
			
			if(curveMap.isEmpty()) {
				log.warn("Curve His Data Error :  His Data of {} is not found at {} ", curveMst.getIrCurveId(), bssd );
				System.exit(10);
			}
//			log.info("smithWilson Start : {},{}", sceNo, LocalTime.now());
			SmithWilsonModelCore sw = new SmithWilsonModelCore(bssd, curveMst.getIrCurveId(), sceNo, curveMap, lpMap, ufr, ufrt);
			List<SmithWilsonResult> rst = sw.getSmithWilsionResult();
			
	//		List<SmithWilsonResult> rst = SmithWilsonModelCoreStatic.getSmithWilsionResult(bssd, irCurveId, sceNo, curveMap, lpMap, ufr, ufrt);
	
//			log.info("smithWilson End : {},{}", sceNo, LocalTime.now());
			
			return rst.stream().map(s-> s.convertToBizDiscountRateSce(curveMst.getApplBizDv(), lpMap)).collect(toList());
	
		}
}
