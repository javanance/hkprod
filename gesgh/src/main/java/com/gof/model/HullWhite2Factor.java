package com.gof.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;

import org.renjin.script.RenjinScriptEngineFactory;
import org.renjin.sexp.SEXP;

import com.gof.entity.BizDiscountRateSce;
import com.gof.entity.BizEsgParam;
import com.gof.entity.BizIrCurveSce;
import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveHis;
import com.gof.entity.IrSce;
import com.gof.entity.SmithWilsonResult;
import com.gof.util.ScriptUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> Hull White 2 Factor  ����	</p>        
 *  <p> R Script �� �����ϱ� ����  Input Data ���� �� ���� , R Script ����, Output Converting �۾��� ������.</p>
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class HullWhite2Factor {
	
//	private String baseYymm;
	private double[] intRate;
	private double[] matOfYear;
	private double[] parameter;
	private double ufr;
	private double ufrt;
	
//	private List<ParamApply> paramHisList = new ArrayList<ParamApply>();
	private List<BizEsgParam> bizParamHisList = new ArrayList<BizEsgParam>();
	private List<IrCurveHis> curveHisList = new ArrayList<IrCurveHis>();
	public HullWhite2Factor() {
	}
	
	/*public HullWhite2Factor(String baseDate, List<IrCurveHis> curveHisList, List<ParamApply> param,  double ufr, double ufrt) {
		this.baseYymm = baseDate;
		this.intRate = new double[curveHisList.size()];
		this.matOfYear = new double[curveHisList.size()];
		for (int i = 0; i < matOfYear.length; i++) {
			intRate[i] = curveHisList.get(i).getIntRate();
			matOfYear[i] = Double.valueOf(curveHisList.get(i).getMatCd().split("M")[1]) / 12;
		}
		this.parameter = new double[5];
		Map<String, Double> paramMap = paramHisList.stream().collect(Collectors.toMap(s->s.getParamTypCd(), S->S.getApplParamVal()));
		
		parameter[0]= paramMap.getOrDefault("ALPHA1", 0.01);
		parameter[1]= paramMap.getOrDefault("ALPHA2", 0.01);
		parameter[2]= paramMap.getOrDefault("SIGMA1", 0.01);
		parameter[3]= paramMap.getOrDefault("SIGMA2", 0.01);
		parameter[4]= paramMap.getOrDefault("RHO", 0.01);
//		for(int k =0; k<5; k++) {
//			parameter[k] = param.get(k).getApplParamVal();
//		}
		this.ufr = ufr;
		this.ufrt = ufrt;
		this.paramHisList = param;
		this.curveHisList = curveHisList;
	} */
	
	public HullWhite2Factor(String baseDate, IrCurve curveMst, Map<String, Double> curveHisMap, List<BizEsgParam> param,  double ufr, double ufrt) {
//		this.baseYymm = baseDate;
		this.intRate = new double[curveHisMap.size()];
		this.matOfYear = new double[curveHisMap.size()];
		int idx=0;
		for(Map.Entry<String, Double> entry : curveHisMap.entrySet()) {
			matOfYear[idx] = Double.valueOf(entry.getKey().split("M")[1]) / 12.0;
			intRate[idx] = entry.getValue();
			idx++;
		}
		
		this.parameter = new double[5];
		Map<String, Double> paramMap = bizParamHisList.stream().collect(Collectors.toMap(s->s.getParamTypCd(), S->S.getApplParamVal()));
		
		parameter[0]= paramMap.getOrDefault("ALPHA1", 0.01);
		parameter[1]= paramMap.getOrDefault("ALPHA2", 0.01);
		parameter[2]= paramMap.getOrDefault("SIGMA1", 0.01);
		parameter[3]= paramMap.getOrDefault("SIGMA2", 0.01);
		parameter[4]= paramMap.getOrDefault("RHO", 0.01);
//		for(int k =0; k<5; k++) {
//			parameter[k] = param.get(k).getApplParamVal();
//		}
		this.ufr = ufr;
		this.ufrt = ufrt;
		this.bizParamHisList = param;
//		this.curveHisList = curveHisList;
	} 
	
	public HullWhite2Factor(String baseDate, List<IrCurveHis> curveHisList, List<BizEsgParam> param,  double ufr, double ufrt) {
//		this.baseYymm = baseDate;
		this.intRate = new double[curveHisList.size()];
		this.matOfYear = new double[curveHisList.size()];
		for (int i = 0; i < matOfYear.length; i++) {
			intRate[i] = curveHisList.get(i).getIntRate();
			matOfYear[i] = Double.valueOf(curveHisList.get(i).getMatCd().split("M")[1]) / 12;
		}
		this.parameter = new double[5];
		Map<String, Double> paramMap = bizParamHisList.stream().collect(Collectors.toMap(s->s.getParamTypCd(), S->S.getApplParamVal()));
		
		parameter[0]= paramMap.getOrDefault("ALPHA1", 0.01);
		parameter[1]= paramMap.getOrDefault("ALPHA2", 0.01);
		parameter[2]= paramMap.getOrDefault("SIGMA1", 0.01);
		parameter[3]= paramMap.getOrDefault("SIGMA2", 0.01);
		parameter[4]= paramMap.getOrDefault("RHO", 0.01);
//		for(int k =0; k<5; k++) {
//			parameter[k] = param.get(k).getApplParamVal();
//		}
		this.ufr = ufr;
		this.ufrt = ufrt;
		this.bizParamHisList = param;
		this.curveHisList = curveHisList;
	} 
	
	public List<IrSce> getHW2Scenario(String bssd, String irCurveId, String modelId, int sceNum, int batchNo){
		List<IrSce> irScenarioList = new ArrayList<IrSce>();
		IrSce tempSce;
		String matCd;
		int sceNo ;
		double sceRate=0.0;
//		Map<String, ParamApply> paramMap = paramHisList.stream().collect(Collectors.toMap(s->s.getParamTypCd(), Function.identity()));
//		Map<String, BizEsgParam> paramMap = bizParamHisList.stream().collect(Collectors.toMap(s->s.getParamTypCd(), Function.identity()));
		
//		SmithWilsonModel sw = new SmithWilsonModel(curveHisList, ufr, ufrt);
		SmithWilsonModel sw = new SmithWilsonModel(curveHisList, ufr, ufrt);
		
		List<SmithWilsonResult> swRstList = sw.getSmithWilsionResult(false);
		Map<Integer, SmithWilsonResult> swRstMap = swRstList.stream().collect(Collectors.toMap(s->s.getMonthNum(), Function.identity())); 
		
		SEXP hwRst = getModelResult(sceNum);
		
//		"BSE_DT","SCEN_ID","SCEN_NO","TIME","MONTH_SEQ","SPOT_CONT","SPOT_DISC","DISCOUNT","FWD_CONT","FWD_DISC")
		
		for(int k =0; k< hwRst.getElementAsSEXP(0).length(); k++) {
			sceNo =( batchNo - 1)* sceNum + hwRst.getElementAsSEXP(2).getElementAsSEXP(k).asInt() ;
			matCd = "M"+ String.format("%04d", hwRst.getElementAsSEXP(4).getElementAsSEXP(k).asInt());
			
			if(Double.isNaN(hwRst.getElementAsSEXP(6).getElementAsSEXP(k).asReal())
					|| Double.isInfinite(hwRst.getElementAsSEXP(6).getElementAsSEXP(k).asReal())) {
				sceRate = swRstMap.get(hwRst.getElementAsSEXP(4).getElementAsSEXP(k).asInt()).getSpotAnnual();
			};
			
			tempSce = new IrSce();
			tempSce.setBaseDate(bssd);
			tempSce.setIrCurveId(irCurveId);
			
			tempSce.setIrModelId(modelId);
			tempSce.setSceNo(String.valueOf(sceNo));
			tempSce.setMatCd(matCd);
			tempSce.setRfRate(sceRate);
			
			tempSce.setLastModifiedBy("ESG");
			tempSce.setLastUpdateDate(LocalDateTime.now());
			
			irScenarioList.add(tempSce);
		}
		return irScenarioList;
	}
	
	public List<BizIrCurveSce> getHW2Scenario1(String bssd, String irCurveId, String bizDv, int sceNum, int batchNo){
		List<BizIrCurveSce> irScenarioList = new ArrayList<BizIrCurveSce>();
		BizIrCurveSce tempSce;
		String matCd;
		int sceNo ;
		double sceRate=0.0;
		double sceFwdRate =0.0;
		
//		Map<String, ParamApply> paramMap = paramHisList.stream().collect(Collectors.toMap(s->s.getParamTypCd(), Function.identity()));
//		Map<String, BizEsgParam> paramMap = bizParamHisList.stream().collect(Collectors.toMap(s->s.getParamTypCd(), Function.identity()));
		
		SmithWilsonModel sw = new SmithWilsonModel(curveHisList, ufr, ufrt);
		
		List<SmithWilsonResult> swRstList = sw.getSmithWilsionResult(false);
		Map<Integer, SmithWilsonResult> swRstMap = swRstList.stream().collect(Collectors.toMap(s->s.getMonthNum(), Function.identity())); 
		
		SEXP hwRst = getModelResult(sceNum);
		
//		"BSE_DT","SCEN_ID","SCEN_NO","TIME","MONTH_SEQ","SPOT_CONT","SPOT_DISC","DISCOUNT","FWD_CONT","FWD_DISC")
		
		for(int k =0; k< hwRst.getElementAsSEXP(0).length(); k++) {
			sceNo =( batchNo - 1)* sceNum + hwRst.getElementAsSEXP(2).getElementAsSEXP(k).asInt() ;
			matCd = "M"+ String.format("%04d", hwRst.getElementAsSEXP(4).getElementAsSEXP(k).asInt());
			
			if(Double.isNaN(hwRst.getElementAsSEXP(6).getElementAsSEXP(k).asReal())
					|| Double.isInfinite(hwRst.getElementAsSEXP(6).getElementAsSEXP(k).asReal())) {
				sceRate = swRstMap.get(hwRst.getElementAsSEXP(6).getElementAsSEXP(k).asInt()).getSpotAnnual();
				sceFwdRate= swRstMap.get(hwRst.getElementAsSEXP(9).getElementAsSEXP(k).asInt()).getSpotAnnual();
			};
			
			tempSce = new BizIrCurveSce();
			tempSce.setBaseYymm(bssd);
			tempSce.setApplBizDv(bizDv);
			
			tempSce.setIrCurveId(irCurveId);
			tempSce.setSceNo(String.valueOf(sceNo));
			tempSce.setMatCd(matCd);
			tempSce.setRfRate(sceRate);
			tempSce.setForwardRate(sceFwdRate);
			
			tempSce.setLastModifiedBy("ESG");
			tempSce.setLastUpdateDate(LocalDateTime.now());
			
			irScenarioList.add(tempSce);
		}
		return irScenarioList;
	}
	
	
	public List<BizDiscountRateSce> getBizDiscountRateSce(String bssd, String irCurveId, String bizDv, int sceNum, int batchNo){
		List<BizDiscountRateSce> irScenarioList = new ArrayList<BizDiscountRateSce>();
		BizDiscountRateSce tempSce;
		String matCd;
		int sceNo ;
		double sceRate=0.0;
		double sceFwdRate =0.0;
		
//		Map<String, ParamApply> paramMap = paramHisList.stream().collect(Collectors.toMap(s->s.getParamTypCd(), Function.identity()));
//		Map<String, BizEsgParam> paramMap = bizParamHisList.stream().collect(Collectors.toMap(s->s.getParamTypCd(), Function.identity()));
		
		SmithWilsonModel sw = new SmithWilsonModel(curveHisList, ufr, ufrt);
		
		List<SmithWilsonResult> swRstList = sw.getSmithWilsionResult(false);
		Map<Integer, SmithWilsonResult> swRstMap = swRstList.stream().collect(Collectors.toMap(s->s.getMonthNum(), Function.identity())); 
		
		SEXP hwRst = getModelResult(sceNum);
		
//		"BSE_DT","SCEN_ID","SCEN_NO","TIME","MONTH_SEQ","SPOT_CONT","SPOT_DISC","DISCOUNT","FWD_CONT","FWD_DISC")
		
		for(int k =0; k< hwRst.getElementAsSEXP(0).length(); k++) {
			sceNo =( batchNo - 1)* sceNum + hwRst.getElementAsSEXP(2).getElementAsSEXP(k).asInt() ;
			matCd = "M"+ String.format("%04d", hwRst.getElementAsSEXP(4).getElementAsSEXP(k).asInt());
			
			if(Double.isNaN(hwRst.getElementAsSEXP(6).getElementAsSEXP(k).asReal())
					|| Double.isInfinite(hwRst.getElementAsSEXP(6).getElementAsSEXP(k).asReal())) {
				sceRate = swRstMap.get(hwRst.getElementAsSEXP(6).getElementAsSEXP(k).asInt()).getSpotAnnual();
				sceFwdRate= swRstMap.get(hwRst.getElementAsSEXP(9).getElementAsSEXP(k).asInt()).getSpotAnnual();
			};
			
			tempSce = new BizDiscountRateSce();
			tempSce.setBaseYymm(bssd);
			tempSce.setApplyBizDv(bizDv);
			
			tempSce.setIrCurveId(irCurveId);
			tempSce.setSceNo(String.valueOf(sceNo));
			tempSce.setMatCd(matCd);
			tempSce.setRiskAdjRfRate(sceRate);
			tempSce.setRiskAdjRfFwdRate(sceFwdRate);
			
			tempSce.setLastModifiedBy("ESG");
			tempSce.setLastUpdateDate(LocalDateTime.now());
			
			irScenarioList.add(tempSce);
		}
		return irScenarioList;
	}
	
	public List<BizIrCurveSce> getHW2Scenario1(String bssd, IrCurve curveMst, String bizDv, int sceNum, int batchNo){
		List<BizIrCurveSce> irScenarioList = new ArrayList<BizIrCurveSce>();
		BizIrCurveSce tempSce;
		String matCd;
		int sceNo ;
		double sceRate=0.0;
		double sceFwdRate =0.0;
		
//		Map<String, ParamApply> paramMap = paramHisList.stream().collect(Collectors.toMap(s->s.getParamTypCd(), Function.identity()));
//		Map<String, BizEsgParam> paramMap = bizParamHisList.stream().collect(Collectors.toMap(s->s.getParamTypCd(), Function.identity()));
		
		SmithWilsonModel sw = new SmithWilsonModel(curveHisList, ufr, ufrt);
		
		List<SmithWilsonResult> swRstList = sw.getSmithWilsionResult(false);
		Map<Integer, SmithWilsonResult> swRstMap = swRstList.stream().collect(Collectors.toMap(s->s.getMonthNum(), Function.identity())); 
		
		SEXP hwRst = getModelResult(sceNum);
		
//		"BSE_DT","SCEN_ID","SCEN_NO","TIME","MONTH_SEQ","SPOT_CONT","SPOT_DISC","DISCOUNT","FWD_CONT","FWD_DISC")
		
		for(int k =0; k< hwRst.getElementAsSEXP(0).length(); k++) {
			sceNo =( batchNo - 1)* sceNum + hwRst.getElementAsSEXP(2).getElementAsSEXP(k).asInt() ;
			matCd = "M"+ String.format("%04d", hwRst.getElementAsSEXP(4).getElementAsSEXP(k).asInt());
			
			if(Double.isNaN(hwRst.getElementAsSEXP(6).getElementAsSEXP(k).asReal())
					|| Double.isInfinite(hwRst.getElementAsSEXP(6).getElementAsSEXP(k).asReal())) {
				sceRate = swRstMap.get(hwRst.getElementAsSEXP(6).getElementAsSEXP(k).asInt()).getSpotAnnual();
				sceFwdRate= swRstMap.get(hwRst.getElementAsSEXP(9).getElementAsSEXP(k).asInt()).getSpotAnnual();
			};
			
			tempSce = new BizIrCurveSce();
			tempSce.setBaseYymm(bssd);
			tempSce.setApplBizDv(bizDv);
			
			tempSce.setIrCurveId(curveMst.getIrCurveId());
			tempSce.setSceNo(String.valueOf(sceNo));
			tempSce.setMatCd(matCd);
			tempSce.setRfRate(sceRate);
			tempSce.setForwardRate(sceFwdRate);
			
			tempSce.setLastModifiedBy("ESG");
			tempSce.setLastUpdateDate(LocalDateTime.now());
			
			irScenarioList.add(tempSce);
		}
		return irScenarioList;
	}
	
	/**
	 *  R Script �� �̿��� Hull White 2 Factor Model ����
	 *  @param sceNum		: ������ �ó����� ����
	 *  @return SEXP (Renjin ����� ���� ����� Data Type)  
	*/
	private SEXP getModelResult(int sceNum) {
		System.setProperty("com.github.fommil.netlib.BLAS", "com.github.fommil.netlib.F2jBLAS");
		System.setProperty("com.github.fommil.netlib.LAPACK", "com.github.fommil.netlib.F2jLAPACK");
		
		List<String> scriptString = ScriptUtil.getScriptContents();
		
		RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();
		ScriptEngine engine = factory.getScriptEngine();
		log.info("aaa: {}", ufrt);
		try {
			for (String aa : scriptString) {
				engine.eval(aa);
			}
			engine.put("int", intRate);
			engine.put("mat", matOfYear);
			engine.put("param", parameter);
			engine.put("ufr", ufr);
			engine.put("ufrt", ufrt);
			engine.put("sceNum", sceNum);
			engine.put("intType", "annu");
			engine.put("modelType", "HW2");
			
			String script = "G2.HW2.simulation.run(int, mat, param, num.of.scen = sceNum, int.type = intType,  ufr = ufr, ufr.t = ufrt, model = modelType )";
			SEXP hwRst = (SEXP) engine.eval(script);

			return hwRst;
		} catch (Exception e) {
			log.error("Renjin Error : {}", e);
		}
		return null;
	}
}
