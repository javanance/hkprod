package com.gof.test;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.gof.dao.IrCurveHisDao;
import com.gof.dao.LiqPremiumDao;
import com.gof.entity.BizIrCurveSce;
import com.gof.entity.IrCurveHis;
import com.gof.entity.IrSce;
import com.gof.entity.SmithWilsonResult;
import com.gof.interfaces.IIntRate;
import com.gof.model.SmithWilsonModel;
import com.gof.model.SmithWilsonModelCore;
import com.gof.util.EsgConstant;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class SmithWilsionCoreTest {
	
	
	public static void main(String[] args) {
		aaa();
//		forwardTermStructureResult();
//		forwardBucketResult();
	}
	
	private static void aaa() {
		String bssd = "201712";
		Map<String,IrCurveHis> curveHisMap = IrCurveHisDao.getIrCurveHis(bssd, "1010000").stream().collect(toMap(s->s.getMatCd(), Function.identity()));
		Map<String, Double> lpMap = LiqPremiumDao.getBizLiqPremium(bssd).stream().collect(toMap(s->s.getMatCd(), s->s.getApplyLiqPrem()));
				
		SmithWilsonModelCore sw = new SmithWilsonModelCore(bssd, "1010000", "0", curveHisMap, lpMap, 0.05, 60.0);
		List<SmithWilsonResult> rst = sw.getSmithWilsionResult();
		
		
//		rst.stream().map(s ->s.convertToIrCurveHis()).forEach(s->log.info("zzz : {}", s.toString()));
//		rst.stream().map(s ->s.convertToBizDiscountRateSce("I", lpMap)).forEach(s->log.info("zzz : {}", s.toString()));
		rst.stream().map(s ->s.convertToBizDiscountRate("I", lpMap)).forEach(s->log.info("zzz : {}", s.toString()));
	}
	
	private static void forwardTermStructureResult() {
		String bssd = "201712";
		List<IrCurveHis> curveHisList = IrCurveHisDao.getIrCurveHis(bssd, "1010000");
		
		SmithWilsonModel sw = new SmithWilsonModel(curveHisList);
		
		for(SmithWilsonResult aa : sw.getSwForwardTermStructure(0)) {
			log.info("SmithWilson result  : {}", aa.toString());
		}
	}
	
	private static void forwardBucketResult() {
		String bssd = "201712";
		List<IrCurveHis> curveHisList = IrCurveHisDao.getIrCurveHis(bssd, "1010000");
		
		SmithWilsonModel sw = new SmithWilsonModel(curveHisList);
		
		for(SmithWilsonResult aa : sw.getSwForwardRateAtBucket(60)){
			log.info("SmithWilson result  : {}", aa.toString());
		}
	}
}
