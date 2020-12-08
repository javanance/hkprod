package com.gof.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SmithWilsonResult {
	
	private String baseYymm;
	private String sceNo;
	private String irCurveId;
	private String matCd;
	
	private double  timeFactor;
	private int		monthNum;
	private double  spotCont;
	private double	spotAnnual;
	private double	discountFactor;
	private double	fwdCont;				//1M forward Rate 
	private double	fwdAnnual;
	private int		fwdMonthNum;			//1M forward Rate 

	public SmithWilsonResult() {
	}

	
	@Override
	public String toString() {
		return toString(",");
	}
	
	public String toString(String delimeter) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(timeFactor).append(delimeter)
			   .append(monthNum).append(delimeter)
			   .append(spotCont).append(delimeter)
			   .append(spotAnnual).append(delimeter)
			   .append(discountFactor).append(delimeter)
			   .append(fwdCont).append(delimeter)
			   .append(fwdAnnual).append(delimeter)
			   .append(fwdMonthNum).append("\n")
			   ;
		return builder.toString();
	}
	
	public IrCurveHis convertToIrCurveHis() {
		IrCurveHis rst = new IrCurveHis();
		
		rst.setBaseDate(this.baseYymm);
		rst.setIrCurveId(this.irCurveId);
		rst.setSceNo(this.sceNo);
		rst.setMatCd(this.matCd);
		rst.setIntRate(this.spotAnnual);
		
		return rst;
	}
	
	
	public BizIrCurveHis convertToBizIrCurveHis(String bizDv) {
		BizIrCurveHis rst = new BizIrCurveHis();
		
		rst.setBaseYymm(this.baseYymm);
		rst.setApplBizDv(bizDv);
		rst.setIrCurveId(this.irCurveId);
		rst.setMatCd(this.matCd);
		rst.setIntRate(this.spotAnnual);
		rst.setForwardRate(this.fwdAnnual);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
	
	public BottomupDcnt convertToBottomUp(Map<String, Double> lpMap) {
		BottomupDcnt rst = new BottomupDcnt();
		double liqPremium = lpMap.getOrDefault(this.matCd, 0.0);
		
		rst.setBaseYymm(this.baseYymm);
		rst.setIrCurveId(this.irCurveId);
		rst.setMatCd(this.matCd);

		rst.setRfRate(this.spotAnnual - liqPremium);
		rst.setLiqPrem(liqPremium);
		rst.setRiskAdjRfRate(this.spotAnnual);
		rst.setRiskAdjRfFwdRate(this.fwdAnnual);
		rst.setVol(0.0);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
	public BizDiscountRate convertToBizDiscountRate(Map<String, Double> lpMap) {
		BizDiscountRate rst = new BizDiscountRate();
		double liqPremium = lpMap.getOrDefault(this.matCd, 0.0);
		
		rst.setBaseYymm(this.baseYymm);
		rst.setIrCurveId(this.irCurveId);
		rst.setMatCd(this.matCd);

		rst.setRfRate(this.spotAnnual - liqPremium);
		rst.setLiqPrem(liqPremium);
		rst.setRiskAdjRfRate(this.spotAnnual);
		rst.setRiskAdjRfFwdRate(this.fwdAnnual);
		rst.setVol(0.0);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
	public BottomupDcnt convertToKicsTermStructure(Map<String, Double> lpMap) {
		BottomupDcnt rst = new BottomupDcnt();
		double liqPremium = lpMap.getOrDefault(this.matCd, 0.0);
		
		rst.setBaseYymm(this.baseYymm);
		rst.setIrCurveId(this.irCurveId);
		rst.setMatCd(this.matCd);

		rst.setRfRate(this.spotAnnual - liqPremium);
		rst.setLiqPrem(liqPremium);
		rst.setRiskAdjRfRate(this.spotAnnual);
		rst.setRiskAdjRfFwdRate(this.fwdAnnual);
		rst.setVol(0.0);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
	public DcntSce convertToDcntSce(Map<String, Double> lpMap) {
		DcntSce rst = new DcntSce();
		double liqPremium = lpMap.getOrDefault(this.matCd, 0.0);
		
		rst.setBaseYymm(this.baseYymm);
		rst.setIrCurveId(this.irCurveId);
		rst.setMatCd(this.matCd);

		rst.setRfRate(this.spotAnnual - liqPremium);
		rst.setLiqPrem(liqPremium);
		rst.setRiskAdjRfRate(this.spotAnnual);
		rst.setRiskAdjRfFwdRate(this.fwdAnnual);
		rst.setVol(0.0);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
	public BizDiscountRate convertToBizDiscountRate(String bizDv, Map<String, Double> lpMap) {
		BizDiscountRate rst = new BizDiscountRate();
		double liqPremium = lpMap.getOrDefault(this.matCd, 0.0);
		
		rst.setBaseYymm(this.baseYymm);
		rst.setApplyBizDv(bizDv);
		
		rst.setIrCurveId(this.irCurveId);
		rst.setMatCd(this.matCd);
		
		
		rst.setRfRate(this.spotAnnual - liqPremium);
		rst.setLiqPrem(liqPremium);
		rst.setRiskAdjRfRate(this.spotAnnual);
		rst.setRiskAdjRfFwdRate(this.fwdAnnual);
		
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
	public BizDiscountRate convertToBizDiscountRate(String bizDv) {
//		BizDiscountRate rst = new BizDiscountRate();
//		
//		rst.setBaseYymm(this.baseYymm);
//		rst.setApplyBizDv(bizDv);
//		
//		rst.setIrCurveId(this.irCurveId);
//		rst.setMatCd(this.matCd);
//		
//		
//		rst.setRfRate(this.spotAnnual );
//		rst.setLiqPrem(0.0);
//		rst.setRiskAdjRfRate(this.spotAnnual);
//		rst.setRiskAdjRfFwdRate(this.fwdAnnual);
//		
//		return rst;
		return convertToBizDiscountRate(bizDv, new HashMap<String, Double>());
	}
	
	
	public BizDiscountRateSce convertToBizDiscountRateSce(String bizDv, Map<String, Double> lpMap) {
		BizDiscountRateSce rst = new BizDiscountRateSce();
		
		double liqPremium = lpMap.getOrDefault(this.matCd, 0.0);
		
		rst.setBaseYymm(this.baseYymm);
		rst.setApplyBizDv(bizDv);
		
		rst.setIrCurveId(this.irCurveId);
		rst.setSceNo(this.sceNo);
		rst.setMatCd(this.matCd);
		
		rst.setRfRate(this.spotAnnual - liqPremium);
		rst.setLiqPrem(liqPremium);
		rst.setRiskAdjRfRate(this.spotAnnual);
		rst.setRiskAdjRfFwdRate(this.fwdAnnual);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
	
	public BizDiscountRateSce convertToBizDiscountRateSce(String bizDv, double spread) {
		BizDiscountRateSce rst = new BizDiscountRateSce();
		
		double liqPremium = spread;
		
		rst.setBaseYymm(this.baseYymm);
		rst.setApplyBizDv(bizDv);
		
		rst.setIrCurveId(this.irCurveId);
		rst.setSceNo(this.sceNo);
		rst.setMatCd(this.matCd);
		
		rst.setRfRate(this.spotAnnual - liqPremium);
		rst.setLiqPrem(liqPremium);
		rst.setRiskAdjRfRate(this.spotAnnual);
		rst.setRiskAdjRfFwdRate(this.fwdAnnual);
		
		return rst;
	}
	public BizDiscountRateSce convertToBizDiscountRateSce(String bizDv) {
		return convertToBizDiscountRateSce(bizDv, new HashMap<String, Double>());
	}
}
