package com.gof.process;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.gof.dao.IrCurveHisDao;
import com.gof.dao.LiqPremiumDao;
import com.gof.entity.BizIrCurveHis;
import com.gof.entity.BottomupDcnt;
import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveHis;
import com.gof.entity.SmithWilsonResult;
import com.gof.model.LiquidPremiumModel;
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
public class Job22_BottomUp {

	public static List<BottomupDcnt> createBottomUpCurveSw(String bssd, IrCurve curveMst) {
		List<BottomupDcnt> rst = new ArrayList<BottomupDcnt>();
		
		Map<String, Double> lpMap = LiqPremiumDao.getBizLiqPremium(bssd).stream().collect(toMap(s->s.getMatCd(), s->s.getApplyLiqPrem()));
		rst = createCurveBySw(bssd, "0", curveMst, lpMap);
		
		log.info("Job22( Bottom Up Ir Rate Calculation) creates {} results for {}. inserted into EAS_BOTTOMUP_DCNT",rst.size(),curveMst.getIrCurveId());
		
		return rst;
	}
	
	public static List<BottomupDcnt> createKicsTermStructureSw(String bssd, IrCurve curveMst,  double volAdj) {
		List<BottomupDcnt> rst = new ArrayList<BottomupDcnt>();
		
		int llpMonth = Integer.parseInt(EsgConstant.getStrConstant().get("llp").split("M")[1]); 
		Map<String, Double> lpMap = LiquidPremiumModel.createLiqPremium(llpMonth, volAdj);
		
		rst = createCurveBySw(bssd, "0", curveMst, lpMap);
	
		log.info("Job 25 (KICS Term Structure)  create {} result for {}. insert into EAS_BOTTOMUP_DCNT", rst.size(),curveMst.getIrCurveId());
		return rst;
	}

	//	Smith Wilson 으로 산출한 무위험 금리 + 유동성 프리미엄 가산으로 Term Structure 산출
	public static List<BottomupDcnt> createBottomUpCurveAdd(String bssd, IrCurve curveMst) {
//		1.유동성 프리미엄 정보 추출 : KRW 는 적용, 다른 통화는 적용여부 확인 필요 : TODO
		Map<String, Double> lpMap = LiqPremiumDao.getBizLiqPremium(bssd).stream()
												.collect(toMap(s->s.getMatCd(), s->s.getApplyLiqPrem()));

//		1. 무위험 금리(Ref Curve) 의 All Bucket 별 금리 추출
		Map<String, BizIrCurveHis> curveMap = IrCurveHisDao.getBizIrCurveHis(bssd, "A", curveMst.getRefCurveId()).stream()
															.collect(toMap(s->s.getMatCd(), Function.identity()));
			
// 		무위험 금리가 존재하지 않으면 Error 임  
		if(curveMap.isEmpty()) {
			log.error("Curve His Data Error :  His Data of {} is not found at {} ", curveMst.getIrCurveId(), bssd );
			System.exit(0);
		}
			
//		2. 모든 Tenor 의  curve + spread 로 Term Structure 구성 ==> 3. Forward 산출후  적용
//		curveMap.entrySet().forEach(s -> log.info("zzzz : {},{}", s.getKey(), s.getValue().getIntRate()));
		List<BottomupDcnt> rst = TermStructureModel.createTermStructure(bssd, curveMst.getIrCurveId(), "0", curveMap, lpMap);
		
		log.info("Job22( Bottom Up Ir Rate Calculation) creates  {} results.  They are inserted into EAS_BOTTOMUP_DCNT Table", rst.size());
//		rst.stream().forEach(s->log.debug("Bottom Up Result : {}", s.toString()));
		
		return rst;
	}

	private static List<BottomupDcnt> createCurveBySw(String bssd, String sceNo, IrCurve curveMst, Map<String, Double> lpMap) {
		List<String> tenorList = EsgConstant.getTenorList();
	
		Map<String, IrCurveHis> curveMap = IrCurveHisDao.getIrCurveHis(bssd, curveMst.getRefCurveId()).stream()
				.filter(s-> tenorList.contains(s.getMatCd()))
				.collect(toMap(s->s.getMatCd(), Function.identity()));
	
		//무위험 금리가 존재하지 않으면 Error 임  
		if(curveMap.isEmpty()) {
			log.error("Curve His Data Error :  His Data of {} is not found at {} ", curveMst.getIrCurveId(), bssd );
			System.exit(0);
		}
		
		//2. 주어진 Tenor 의  curve + spread 로 Term Structure 구성 ==> 3. smith wilson 적용 
		double ufr  = EsgConstant.getSmParam().get(curveMst.getCurCd()).getUfr();
		double ufrt = EsgConstant.getSmParam().get(curveMst.getCurCd()).getUfrT();
		
//		curveMap.entrySet().forEach(s-> log.info("zzz : {}", s.getKey(), s.getValue().toString()));
		
		SmithWilsonModelCore sw = new SmithWilsonModelCore(bssd, curveMst.getIrCurveId(), sceNo, curveMap, lpMap, ufr, ufrt);
		List<SmithWilsonResult> rst = sw.getSmithWilsionResult();
		
		return rst.stream().map(s->s.convertToBottomUp(lpMap)).collect(toList());
	}

//	private static List<BottomupDcnt> createBottomUpCurveSwAll(String bssd, IrCurve curveMst) {
//		List<String> tenorList = EsgConstant.getTenorList();
//		
////		1.유동성 프리미엄 정보 추출 : KRW 는 적용, 다른 통화는 적용여부 확인 필요 : TODO
//		Map<String, Double> lpMap = LiqPremiumDao.getBizLiqPremium(bssd).stream()
////												.filter(s-> tenorList.contains(s.getMatCd()))
//												.collect(toMap(s->s.getMatCd(), s->s.getApplyLiqPrem()));
//
////		1. 무위험 금리 ( Ref Curve) 의 Tenor Bucket 별 금리 추출
//		Map<String, IrCurveHis> curveMap = IrCurveHisDao.getIrCurveHis(bssd, curveMst.getRefCurveId()).stream()
//														.filter(s-> tenorList.contains(s.getMatCd()))
//														.collect(toMap(s->s.getMatCd(), Function.identity()));
//		
//// 		무위험 금리가 존재하지 않으면 Error 임  
//		if(curveMap.isEmpty()) {
//			log.error("Curve His Data Error :  His Data of {} is not found at {} ", curveMst.getIrCurveId(), bssd );
//			System.exit(0);
//		}
//		
////		2. 주어진 Tenor 의  curve + spread 로 Term Structure 구성 ==> 3. smith wilson 적용 
//		double ufr  = EsgConstant.getSmParam().get(curveMst.getCurCd()).getUfr();
//		double ufrt = EsgConstant.getSmParam().get(curveMst.getCurCd()).getUfrT();
//		
//		SmithWilsonModelCore sw = new SmithWilsonModelCore(bssd, curveMst.getIrCurveId(), "0", curveMap, lpMap, ufr, ufrt);
//		List<SmithWilsonResult> rst = sw.getSmithWilsionResult();
//
//
//		log.info("Job22( Bottom Up Ir Rate Calculation) creates  {} results.  inserted into EAS_BOTTOMUP_DCNT", rst.size());
//		return rst.stream().map(s->s.convertToBottomUp(lpMap)).collect(toList());
//	}
}
