package com.gof.process;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao.BottomupDcntDao;
import com.gof.entity.BizDiscountRate;
import com.gof.entity.BottomupDcnt;
import com.gof.entity.IrCurve;
import com.gof.entity.SmithWilsonResult;
import com.gof.model.SmithWilsonModel;
import com.gof.model.SmithWilsonModelCore;
import com.gof.model.TermStructureModel;
import com.gof.util.EsgConstant;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> IFRS 17 의 BottomUp 방법론에 의한 할인율 산출 모형의 실행         
 *  <p> 시장에서 관측되는 무위험 금리를 기반으로 보험 부채의 비유동성 측면을 반영하여 보험부채에 적용할 할인율 산출함.
 *  <p>    1. 기산출된 무위험 금리 및 유동성 프리미엄 추출   
 *  <p>    2. 기준월의 무위험 시장금리 + 유동성 스프레를 적용하여 기간구조 생성
 *  <p>    3. Smith-Wilson 방법론( {@link SmithWilsonModel} 으로 보간/ 보외를 적용하여 전체 구간의 할인율 산출함.
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job18_BizDiscountRate {

	public static Stream<BizDiscountRate> createBizBottomUpCurve(String bssd, IrCurve curveMst) {
		List<String> tenorList = EsgConstant.getTenorList();
		
		List<BottomupDcnt> swRfAdjBottomUp = new ArrayList<BottomupDcnt>();
		List<BottomupDcnt> swRfBottomUp = new ArrayList<BottomupDcnt>();
		
		Map<String, Double> lpMap = new HashMap<String, Double>();
		Map<String, Double> curveRfMap = BottomupDcntDao.getTermStructure(bssd, curveMst.getIrCurveId()).stream()
														.filter(s-> tenorList.contains(s.getMatCd()))
														.collect(toMap(BottomupDcnt::getMatCd, BottomupDcnt::getRfRate));
		
		swRfBottomUp = createCurveBySw(bssd, "0", curveMst, curveRfMap);
		
		Map<String, Double> curveRfAdjMap = BottomupDcntDao.getTermStructure(bssd, curveMst.getIrCurveId()).stream()
														.filter(s-> tenorList.contains(s.getMatCd()))
														.collect(toMap(BottomupDcnt::getMatCd, BottomupDcnt::getRiskAdjRfRate));
		
		swRfAdjBottomUp = createCurveBySw(bssd, "0", curveMst, curveRfAdjMap);
		
		
		
		log.info("Job02A(Biz Bottom Up Ir Rate Calculation) creates {} results for {}. inserted into EAS_BOTTOMUP_DCNT", curveMst.getIrCurveId());
		return  TermStructureModel.createForward(bssd, curveMst, "0", swRfBottomUp, swRfAdjBottomUp).stream();
		
		
	}
	
	private static List<BottomupDcnt> createCurveBySw(String bssd, String sceNo, IrCurve curveMst, Map<String, Double> curveMap) {
		
		Map<String, Double> liqMap = new HashMap<String, Double>();
		
		double ufr  = EsgConstant.getSmParam().get(curveMst.getCurCd()).getUfr();
		double ufrt = EsgConstant.getSmParam().get(curveMst.getCurCd()).getUfrT();
		
		SmithWilsonModelCore sw = new SmithWilsonModelCore(bssd, curveMst.getIrCurveId(), sceNo, curveMap, ufr, ufrt);
		
		List<SmithWilsonResult> rst = sw.getSmithWilsionResult();
		
		return rst.stream().map(s->s.convertToBottomUp(liqMap)).collect(toList());
	}
	
	
	private static List<BottomupDcnt> createCurveBySw(String bssd, String sceNo, IrCurve curveMst) {
		List<String> tenorList = EsgConstant.getTenorList();
	
		Map<String, Double> liqMap = new HashMap<String, Double>();

//		Map<String, BottomupDcnt> curveMap = BottomupDcntDao.getTermStructure(bssd, curveMst.getIrCurveId()).stream()
//															.filter(s-> tenorList.contains(s.getMatCd()))
//															.collect(toMap(s->s.getMatCd(), Function.identity()));
		Map<String, Double> curveRfMap = BottomupDcntDao.getTermStructure(bssd, curveMst.getIrCurveId()).stream()
														.filter(s-> tenorList.contains(s.getMatCd()))
														.collect(toMap(BottomupDcnt::getMatCd, BottomupDcnt::getRfRate));
	
		Map<String, Double> curveRfAdjMap = BottomupDcntDao.getTermStructure(bssd, curveMst.getIrCurveId()).stream()
															.filter(s-> tenorList.contains(s.getMatCd()))
															.collect(toMap(BottomupDcnt::getMatCd, BottomupDcnt::getRiskAdjRfRate));
		
		//무위험 금리가 존재하지 않으면 Error 임  
		if(curveRfMap.isEmpty()) {
			log.error("Curve His Data Error :  His Data of {} is not found at {} ", curveMst.getIrCurveId(), bssd );
			System.exit(0);
		}
		
		double ufr  = EsgConstant.getSmParam().get(curveMst.getCurCd()).getUfr();
		double ufrt = EsgConstant.getSmParam().get(curveMst.getCurCd()).getUfrT();
		
//		curveMap.entrySet().forEach(s-> log.info("zzz : {}", s.getKey(), s.getValue().toString()));
		
		SmithWilsonModelCore sw = new SmithWilsonModelCore(bssd, curveMst.getIrCurveId(), sceNo, curveRfAdjMap, ufr, ufrt);
		
		List<SmithWilsonResult> rst = sw.getSmithWilsionResult();
		
		return rst.stream().map(s->s.convertToBottomUp(liqMap)).collect(toList());
	}

}
