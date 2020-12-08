package com.gof.model;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.gof.entity.BizDiscountRate;
import com.gof.entity.BottomupDcnt;
import com.gof.entity.IrCurve;
import com.gof.interfaces.IIntRate;
import com.gof.util.EsgConstant;

/**
 *  <p> Term Structure 가 구성된 후 , spread 를 가산( 유동성 프리미엄 또는 변동성 조정) 하여 Term Structure 를 새로 구성하는 방안
 *  <p>
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
public class TermStructureModel {
	public static List<BottomupDcnt> createTermStructure(String bssd, String irCurveId, String sceNo, Map<String, ? extends IIntRate> curveMap,Map<String, Double> lpMap){
		List<BottomupDcnt> rstList = new ArrayList<BottomupDcnt>();
		BottomupDcnt temp;
		double beforeIntFactor =1.0;
		double currentIntFactor=1.0;
		double fwdRate=0.0;
		double spotRate =0.0;
		double beforeRate =0.0;
		double beforeTimeFactor =0.0;
		double currentTimeFactor =0.0;
		String matCd ="";
		
		for(int i=0; i< curveMap.size(); i++) {
			matCd= "M" + String.format("%04d", i+1);
			if(curveMap.containsKey(matCd)){
				beforeIntFactor = Math.pow(1+beforeRate, beforeTimeFactor);
				
				currentTimeFactor = (i+1) /12.0;
				spotRate = curveMap.get(matCd).getIntRate() + lpMap.getOrDefault(matCd, 0.0);
				
				currentIntFactor = Math.pow(1+spotRate, currentTimeFactor);
				fwdRate = Math.pow(currentIntFactor/beforeIntFactor, 1.0 / (currentTimeFactor - beforeTimeFactor))-1.0;
						
				beforeTimeFactor = currentTimeFactor;
				beforeRate  = spotRate;
				
				temp = new BottomupDcnt();
				temp.setBaseYymm(bssd);
				temp.setIrCurveId(irCurveId);
				temp.setSceNo(sceNo);
				temp.setMatCd(curveMap.get(matCd).getMatCd());
				temp.setRfRate(curveMap.get(matCd).getIntRate());
				temp.setLiqPrem(lpMap.getOrDefault(matCd, 0.0));
				temp.setRiskAdjRfRate(spotRate);
				temp.setRiskAdjRfFwdRate(fwdRate);
				
				temp.setLastModifiedBy("ESG");
				temp.setLastUpdateDate(LocalDateTime.now());
				
				rstList.add(temp);
			}
		}
		
		return rstList;
	}
	
	
	public static List<BizDiscountRate> createForward(String bssd, IrCurve irCurve, String sceNo, List<BottomupDcnt> rfBottomUp, List<BottomupDcnt> rfAdjBottomUp){
		List<BizDiscountRate> rstList = new ArrayList<BizDiscountRate>();
		BizDiscountRate temp;
		Map<String, BottomupDcnt> rfMap = rfBottomUp.stream().collect(toMap(BottomupDcnt::getMatCd, Function.identity()));
		Map<String, BottomupDcnt> rfAdjMap = rfAdjBottomUp.stream().collect(toMap(BottomupDcnt::getMatCd, Function.identity()));
		
		double beforeIntFactor =1.0;
		double currentIntFactor=1.0;
		double rfBeforeIntFactor =1.0;
		double rfCurrentIntFactor=1.0;
		
		double spotRate =0.0;
		double beforeRate =0.0;
		double fwdRate=0.0;
		
		double rfSpotRate =0.0;
		double rfBeforeRate =0.0;
		double rfFwdRate =0.0;
		
		double beforeTimeFactor =0.0;
		double currentTimeFactor =0.0;
		String matCd ="";
		
		for(int i=0; i< rfAdjMap.size(); i++) {
			matCd= "M" + String.format("%04d", i+1);
			if(rfAdjMap.containsKey(matCd)){
				beforeIntFactor = Math.pow(1+beforeRate, beforeTimeFactor);
				rfBeforeIntFactor = Math.pow(1+rfBeforeRate, beforeTimeFactor);
				
				currentTimeFactor = (i+1) /12.0;
				spotRate = rfAdjMap.get(matCd).getRiskAdjRfRate();
				rfSpotRate = rfMap.get(matCd).getRfRate();
				
				currentIntFactor = Math.pow(1+spotRate, currentTimeFactor);
				rfCurrentIntFactor = Math.pow(1+rfSpotRate, currentTimeFactor);
				
				fwdRate = Math.pow(currentIntFactor/beforeIntFactor, 1.0 / (currentTimeFactor - beforeTimeFactor))-1.0;
				rfFwdRate = Math.pow(rfCurrentIntFactor/rfBeforeIntFactor, 1.0 / (currentTimeFactor - beforeTimeFactor))-1.0;
						
				beforeTimeFactor = currentTimeFactor;
				beforeRate  = spotRate;
				rfBeforeRate  = rfSpotRate;
				
				
				
				temp = new BizDiscountRate();
				temp.setBaseYymm(bssd);
				temp.setIrCurveId(irCurve.getIrCurveId());
				temp.setApplyBizDv(irCurve.getApplBizDv());
				temp.setMatCd(rfAdjMap.get(matCd).getMatCd());
				temp.setRfRate(rfSpotRate);
				temp.setRfFwdRate(rfFwdRate);
				temp.setLiqPrem(spotRate- rfSpotRate);
				temp.setCrdSpread(fwdRate- rfFwdRate);
				temp.setRiskAdjRfRate(spotRate);
				temp.setRiskAdjRfFwdRate(fwdRate);
				temp.setLastModifiedBy("ESG");
				temp.setLastUpdateDate(LocalDateTime.now());
				
				rstList.add(temp);
			}
		}
		
		return rstList;
	}
	
	public static List<BottomupDcnt> createTermStructure(String bssd, String irCurveId, String sceNo, Map<String, ? extends IIntRate> curveMap, double spread){
		Map<String, Double> lpMap = new HashMap<String, Double>();
		String llp = EsgConstant.getStrConstant().getOrDefault("llp", "M0240");
		
		for(Map.Entry<String, ? extends IIntRate> entry : curveMap.entrySet()) {
			if(entry.getKey().compareTo(llp) <= 0) {
				lpMap.put(entry.getKey(), spread);
			}
		}
		return createTermStructure(bssd, irCurveId, sceNo, curveMap, lpMap);
			
	}

	public static List<BottomupDcnt> createTermStructure(String bssd, String irCurveId, String sceNo, List<? extends IIntRate> curveList, Map<String, Double> lpMap){
		Map<String, ? extends IIntRate> curveMap = curveList.stream().collect(toMap(s->s.getMatCd(), Function.identity()));
		return createTermStructure(bssd, irCurveId, sceNo, curveMap, lpMap);
	}
	
	public static List<BottomupDcnt> createTermStructure(String bssd, String irCurveId, String sceNo, List<? extends IIntRate> curveList, double spread){
		Map<String, ? extends IIntRate> curveMap = curveList.stream().collect(toMap(s->s.getMatCd(), Function.identity()));
		return createTermStructure(bssd, irCurveId, sceNo, curveMap, spread);
	}
}
