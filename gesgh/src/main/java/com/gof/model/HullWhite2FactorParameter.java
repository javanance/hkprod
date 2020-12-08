package com.gof.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;

import org.renjin.primitives.vector.RowNamesVector;
import org.renjin.script.RenjinScriptEngineFactory;
import org.renjin.sexp.DoubleArrayVector;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.SEXP;
import org.renjin.sexp.StringVector;
import org.renjin.sexp.Symbols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.entity.IrCurveHis;
import com.gof.entity.ParamCalcHis;
import com.gof.entity.SwaptionVol;
import com.gof.interfaces.Rrunnable;
import com.gof.util.ScriptUtil;

/**
 *  <p> ���ȸ�� �ӵ�, ������ ���� �Ű������� �����ϴ� Ŭ����        
 *  <p> Hull and White 2 Factor  ������ ����� 
 *  <p>  Script �� �����ϱ� ����  Input Data ���� �� ���� , R Script ����, Output Converting �۾��� ������.</p>
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

public class HullWhite2FactorParameter implements Rrunnable {
	private final static Logger logger = LoggerFactory.getLogger("HullWhite");
	
//	private String modelType;
	private double[] intRate;
	private double[] matOfYear;
	private double[] swaptionMat;
	private double[] swapTenor;
	private double ufr;
	private double ufrt;
	private List<SwaptionVol> volList = new ArrayList<SwaptionVol>();

	private String baseYymm;
	
	public HullWhite2FactorParameter() {

	}

	public HullWhite2FactorParameter(List<IrCurveHis> curveHisList, List<SwaptionVol> volList, double ufr, double ufrt) {
//		this.modelType = modelType;
		this.intRate = new double[curveHisList.size()];
		this.matOfYear = new double[curveHisList.size()];

		for (int i = 0; i < matOfYear.length; i++) {
			intRate[i]   = curveHisList.get(i).getIntRate();
			matOfYear[i] = Double.valueOf(curveHisList.get(i).getMatCd().split("M")[1]) / 12;
		}
		this.ufr = ufr;
		this.ufrt = ufrt;

		this.volList = volList;
		this.swaptionMat = new double[] { 1.0, 2.0, 3.0, 5.0, 7.0, 10.0 };				//TODO : �Էµ� ��� �迭 ���ϱ�..
		this.swapTenor = new double[] { 1.0, 2.0, 3.0, 5.0, 7.0, 10.0 };
	}
	
	public List<ParamCalcHis> getParamCalcHis(String bssd, String irModelType, double errorTolerance){
		List<ParamCalcHis> paramHisRst = new ArrayList<ParamCalcHis>();
		ParamCalcHis temp;
		String paramType ="";
		
		SEXP hwRst = getHwParamCalcRst("HW2", errorTolerance);
		logger.info("HullWhite 2 Factor Parameter : {}", hwRst);
		
		for(int k =0; k< hwRst.getElementAsSEXP(0).length(); k++) {
			if(k == 0) {
				paramType ="ALPHA1";
			}
			else if(k == 1){
				paramType ="ALPHA2";
			}
			else if(k == 2){
				paramType ="SIGMA1";
			}
			else if(k == 3){
				paramType ="SIGMA2";
			}
			else if(k == 4){
				paramType ="RHO";
			}
			
			temp = new ParamCalcHis();
			temp.setBaseYymm(bssd);
			temp.setIrModelTyp(irModelType);
			temp.setParamCalcCd("FULL_CALIB");
			temp.setMatCd("M1200");
			
			temp.setParamTypCd(paramType);
			temp.setParamVal(hwRst.getElementAsSEXP(0).getElementAsSEXP(k).asReal());
			temp.setVol(0.0);
			temp.setLastModifiedBy("ESG");
			temp.setLastUpdateDate(LocalDateTime.now());
			
			paramHisRst.add(temp);
		}
		return paramHisRst;
	}
	
	private SEXP getHwParamCalcRst(String modelType, double errorTolerance) {
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
			engine.put("mat", matOfYear);
			engine.put("ufr", ufr);
			engine.put("ufr.t", ufrt);
			engine.put("vol.info", getVolBuilder().build());					//dataFrame ó���� ���� private method ���� ������. 
			engine.put("swaption.Maturities", swaptionMat);
			engine.put("swap.Tenors", swapTenor);
			engine.put("int.type", "annual");
			engine.put("bse.ym", baseYymm);
			engine.put("model", modelType);
			engine.put("accuracy", errorTolerance);
			
		
			String script = "Calibration.g2.hw2(int, mat, ufr, ufr.t, int.type, vol.info, swaption.Maturities, swap.Tenors,  model = model, accuracy=accuracy)";
			
			SEXP swRst = (SEXP) engine.eval(script);

			return swRst;
		} catch (Exception e) {
			logger.error("Renjin Error : {}", e);
		}
		return null;
	}
	
	private ListVector.NamedBuilder getVolBuilder(){
		StringVector.Builder baseDateBuilder = new StringVector.Builder();
		DoubleArrayVector.Builder swaptionMatBuilder = new DoubleArrayVector.Builder();
		DoubleArrayVector.Builder swapTenorBuilder = new DoubleArrayVector.Builder();
		DoubleArrayVector.Builder volBuilder = new DoubleArrayVector.Builder();

		ListVector.NamedBuilder dfProc = new ListVector.NamedBuilder();
		dfProc.setAttribute(Symbols.CLASS, StringVector.valueOf("data.frame"));
		dfProc.setAttribute(Symbols.ROW_NAMES, new RowNamesVector(volList.size()));
		int i = 0;

		for (SwaptionVol aa : volList) {
			baseDateBuilder.add(aa.getBaseYymm());
			swaptionMatBuilder.add(aa.getSwaptionMaturity());
			swapTenorBuilder.add(aa.getSwapTenor());
			volBuilder.add(aa.getVol());
			i = i + 1;
		}

		dfProc.add("BSE_DT", baseDateBuilder.build());
		dfProc.add("SWAPTION_MAT", swaptionMatBuilder.build());
		dfProc.add("SWAP_TENOR", swapTenorBuilder.build());
		dfProc.add("VOL", volBuilder.build());
		
		return dfProc;
	}
}
