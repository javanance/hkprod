package com.gof.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;

import org.renjin.script.RenjinScriptEngineFactory;
import org.renjin.sexp.SEXP;

import com.gof.comparator.IIntRateComparator;
import com.gof.entity.LiqPremium;
import com.gof.interfaces.IIntRate;
import com.gof.util.ScriptUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> ������ �����̾� ���� ���� 	</p>        
 *  <p> R Script �� �����ϱ� ����  Input Data ���� �� ���� , R Script ����, Output Converting �۾��� ������.</p>
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class LiquidPremiumModel {
	
	private double[] intRate;
	private double[] matOfYear;
//	private double  ufr;
//	private double  ufrt ;
//	private double  minAlpha = 0.001;
//	private double  maxAlpha = 1;
//	private double  tolerance = 0.001;
//	private double  llp	=20;
//	private String  baseDate;	
//	private String  dateApplyType ="F";
//	private String  compound = "cont";
//	private double  projectionYear = 100;
	private String  lqModel;
	private SEXP modelResult;
	
	public LiquidPremiumModel() {
	
	}
	
	public LiquidPremiumModel(String lqModel, List<? extends IIntRate> curveHisList, String irCurveId, double ufr, double ufrt) {
		this();
		this.lqModel = lqModel;
		this.intRate = new double[curveHisList.size()];
		this.matOfYear = new double[curveHisList.size()];
		curveHisList.sort(new IIntRateComparator()) ;
		
		for (int i = 0; i < matOfYear.length; i++) {
			log.info("Curve Sort : {},{},{},{}", curveHisList.get(i), i);
			intRate[i] = curveHisList.get(i).getIntRate();
			matOfYear[i] = Double.valueOf(curveHisList.get(i).getMatCd().split("M")[1]) / 12;
		}
//		this.ufr = ufr;
//		this.ufrt = ufrt;
	}
	
	/**
	 *  �����ڿ� �Է��� �������带 �̿��Ͽ� ������ �����̾��� ������.
	 *  @param bssd	���س�� 
	 *  @return ���⺰ ������ �����̾�   
	*/
	public List<LiqPremium> getLiqPremium(String bssd){
		List<LiqPremium> rstList = new ArrayList<LiqPremium>();
		LiqPremium temp;
//		String modelId = ParamUtil.getParamMap().getOrDefault("lqModelId", "COVERED_BOND_KDB");
		
//		SEXP modelRst = getModelResult();					//Model RST : [Curve Info , coefficient if any] , Curve Info : [Time, Month, Rate]  		
		SEXP modelRst = createCurveFittingLiqPremium();		//Model RST : [Curve Info , coefficient if any] , Curve Info : [Time, Month, Rate]  		
		if(lqModel.toUpperCase().equals("SW_FITTING")) {
			modelRst = createSwLiqPremium();				  		
		}
		
//		logger.info("SEXP : {},{},{}", modelRst.length(), modelRst.getElementAsSEXP(0).length(), modelRst.getElementAsSEXP(0).getElementAsSEXP(1));

		for(int i=0 ; i< modelRst.getElementAsSEXP(0).getElementAsSEXP(0).length(); i++) {
			temp = new LiqPremium();
			temp.setBaseYymm(bssd);
			temp.setModelId("COVERED_BOND_KDB");
			temp.setMatCd("M" + String.format("%04d", modelRst.getElementAsSEXP(0).getElementAsSEXP(1).getElementAsSEXP(i).asInt()));
			temp.setLiqPrem(modelRst.getElementAsSEXP(0).getElementAsSEXP(2).getElementAsSEXP(i).asReal());
			temp.setVol(0.0);
			temp.setLastModifiedBy("ESG");
			temp.setLastUpdateDate(LocalDateTime.now());
			
			rstList.add(temp);
		}
		
		return rstList;
	}
	
	private SEXP createSwLiqPremium() {
		log.info("Run Liquid Premium Model  ....");
		System.setProperty("com.github.fommil.netlib.BLAS","com.github.fommil.netlib.F2jBLAS");
		System.setProperty("com.github.fommil.netlib.LAPACK","com.github.fommil.netlib.F2jLAPACK");
		
		List<String> scriptString = ScriptUtil.getScriptContents();
//		Map<String, String> scriptMap = ScriptUtil.getScriptMap();
		
		RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();		
		ScriptEngine engine = factory.getScriptEngine();
		try {
//			for(String aa : scriptMap.values()) {
			for(String aa : scriptString) {
				engine.eval( aa);
			}
			
			engine.put("int", intRate);
			engine.put("mat", matOfYear);
			
			String scriptSW   = "Lp.fitting.run.with.sw(int, mat)";
			
			log.info("Running Liquidity Premium Logic is  Smith Wilson");
			modelResult = (SEXP)engine.eval(scriptSW);

		} catch (Exception e) {
			log.error("Renjin Error : {}", e);
		}
		return modelResult;
	}
	
	private SEXP createCurveFittingLiqPremium() {
		log.info("Run Liquid Premium Model  ....");
		System.setProperty("com.github.fommil.netlib.BLAS","com.github.fommil.netlib.F2jBLAS");
		System.setProperty("com.github.fommil.netlib.LAPACK","com.github.fommil.netlib.F2jLAPACK");
		
		List<String> scriptString = ScriptUtil.getScriptContents();
//		Map<String, String> scriptMap = ScriptUtil.getScriptMap();
		
		RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();		
		ScriptEngine engine = factory.getScriptEngine();
		try {
//			for(String aa : scriptMap.values()) {
			for(String aa : scriptString) {
				engine.eval( aa);
			}
			
			engine.put("int", intRate);
			engine.put("mat", matOfYear);
			
			String scriptPoly = "Lp.fitting.run(int, mat)";
			
			log.info("Running Liquidity Premium Logic is Polynomial Fitting");
			modelResult = (SEXP)engine.eval(scriptPoly);

		} catch (Exception e) {
			log.error("Renjin Error : {}", e);
		}
		return modelResult;
	}
	
	public static Map<String, Double> createLiqPremium(int llpMonth, double spread){
		Map<String, Double> rstMap = new HashMap<String, Double>();
		for(int i=0; i<llpMonth; i++) {
			String matCd = "M" + String.format("%04d", i+1);
			rstMap.put(matCd, spread);
		}
		return rstMap;
	}
}
