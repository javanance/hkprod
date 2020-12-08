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

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> ���ȸ�� �ӵ�, ������ ���� �Ű������� �����ϴ� Ŭ����        
 *  <p> Hull and White 1 Factor, CIR, Vacicek ���� ������ ����� 
 *  <p>  Script �� �����ϱ� ����  Input Data ���� �� ���� , R Script ����, Output Converting �۾��� ������.</p>
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class CIRParameter implements Rrunnable {
	
	private double[] intRate;

	public CIRParameter() {

	}
	public CIRParameter(List<IrCurveHis> curveHisList) {
		this.intRate = new double[curveHisList.size()];
		for (int i = 0; i < intRate.length; i++) {
			intRate[i]   = curveHisList.get(i).getIntRate();
		}
	}
	
	
	public List<ParamCalcHis> getParamCalcHis(String bssd, String irModelType, double errorTolerance){
		List<ParamCalcHis> paramHisRst = new ArrayList<ParamCalcHis>();
		ParamCalcHis temp;
		
		SEXP cirParamRst = getParamCalcRst(errorTolerance).getElementAsSEXP(0);  //list(opt.paras = c(0.02110657279034, 0.00907207701497, 0.00324004632769), LL = <deferred sum>)
		
		Map<String, Double> calcParam = new HashMap<String, Double>();
		calcParam.put("REV_SPEED", cirParamRst.getElementAsSEXP(0).asReal());
		calcParam.put("REV_LEVEL", cirParamRst.getElementAsSEXP(1).asReal());
		calcParam.put("SIGMA", cirParamRst.getElementAsSEXP(2).asReal());
		
//		logger.info("CIR : {},{}", cirParamRst, calcParam);
		
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
			
//			log.info("aaaa : {},{},{}", intRate, errorTolerance);
			
			String scriptHW = "Cir.calib.mle(int, int.type=intType, accuracy=accuracy)";
			SEXP swRst = (SEXP) engine.eval(scriptHW);

			return swRst;
		} catch (Exception e) {
			log.error("Renjin Error : {}", e);
		}
		return null;
	}
}
