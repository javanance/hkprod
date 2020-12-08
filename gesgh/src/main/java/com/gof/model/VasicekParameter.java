package com.gof.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;

import org.renjin.script.RenjinScriptEngineFactory;
import org.renjin.sexp.SEXP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.entity.IrCurveHis;
import com.gof.entity.ParamCalcHis;
import com.gof.interfaces.Rrunnable;
import com.gof.util.ScriptUtil;

/**
 *  <p> 평균회귀 속도, 변동성 등의 매개변수를 생성하는 클래스        
 *  <p> Hull and White 1 Factor, CIR, Vacicek 등의 모형에 적용됨 
 *  <p>  Script 를 실행하기 위한  Input Data 생성 및 관리 , R Script 실행, Output Converting 작업을 수행함.</p>
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

public class VasicekParameter implements Rrunnable {
	private final static Logger logger = LoggerFactory.getLogger(VasicekParameter.class);
	
	private double[] intRate;

	public VasicekParameter() {

	}
	public VasicekParameter(List<IrCurveHis> curveHisList) {
		this.intRate = new double[curveHisList.size()];
//		this.matOfYear = new double[curveHisList.size()];
		for (int i = 0; i < intRate.length; i++) {
			intRate[i]   = curveHisList.get(i).getIntRate();
//			matOfYear[i] = Double.valueOf(curveHisList.get(i).getMatCd().split("M")[1]) / 12;
		}
	}
	
	
	public List<ParamCalcHis> getParamCalcHis(String bssd, String irModelType, double errorTolerance){
		List<ParamCalcHis> paramHisRst = new ArrayList<ParamCalcHis>();
		ParamCalcHis temp;
		
		SEXP vasicekParamRst = getParamCalcRst(errorTolerance);
		Map<String, Double> calcParam = new HashMap<String, Double>();
		calcParam.put("REV_SPEED", vasicekParamRst.getElementAsSEXP(0).asReal());
		calcParam.put("REV_LEVEL", vasicekParamRst.getElementAsSEXP(1).asReal());
		calcParam.put("SIGMA", vasicekParamRst.getElementAsSEXP(2).asReal());
		
		logger.info("vasicek : {},{}", calcParam);
		
		for(Map.Entry<String, Double> entry : calcParam.entrySet() ) {
			temp = new ParamCalcHis();
			temp.setBaseYymm(bssd);
			temp.setIrModelTyp(irModelType);
			temp.setParamCalcCd("FULL_CALIB");
			temp.setParamTypCd(entry.getKey());
			temp.setMatCd("M1200");
			temp.setParamVal(entry.getValue());
			temp.setVol(0.0);
			temp.setLastModifiedBy("ESG");
			temp.setLastUpdateDate(LocalDateTime.now());
			
			paramHisRst.add(temp);
		}
		return paramHisRst;
	}
	
	private SEXP getParamCalcRst(double errorTolerance) {
		System.setProperty("com.github.fommil.netlib.BLAS", "com.github.fommil.netlib.F2jBLAS");
		System.setProperty("com.github.fommil.netlib.LAPACK", "com.github.fommil.netlib.F2jLAPACK");

		List<String> scriptString = ScriptUtil.getScriptContents();
		
		RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();
		ScriptEngine engine = factory.getScriptEngine();

		try {
			for (String aa : scriptString) {
				engine.eval(aa);
			}

			engine.put("int", intRate);
			engine.put("intType", "annu");
			engine.put("accuracy", errorTolerance);
			
			String scriptHW = "Vasi.calib.linear(int, int.type=intType)";
			SEXP swRst = (SEXP) engine.eval(scriptHW);

			return swRst;
		} catch (Exception e) {
			logger.error("Renjin Error : {}", e);
		}
		return null;
	}
}
