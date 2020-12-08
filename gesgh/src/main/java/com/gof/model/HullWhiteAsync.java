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

import com.gof.entity.BizEsgParam;
import com.gof.entity.IrCurveHis;
import com.gof.entity.IrSce;
import com.gof.interfaces.Rrunnable;
import com.gof.util.ScriptUtil;

/**
 *  <p> Hull White 1 Factor ����	</p>        
 *  <p> R Script �� �����ϱ� ����  Input Data ���� �� ���� , R Script ����, Output Converting �۾��� ������.</p>
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

public class HullWhiteAsync implements Rrunnable {
	private final static Logger logger = LoggerFactory.getLogger("HullWhite");

	private String baseYymm;
	private double[] intRate;
	private double[] matOfYear;
	private double ufr;
	private double ufrt;
//	private List<ParamApply> paramHisList = new ArrayList<ParamApply>();
	private List<BizEsgParam> bizParamHisList = new ArrayList<BizEsgParam>();
	
	public HullWhiteAsync() {
	}

	/**
	 *  ��������, ���� �ݸ��Ⱓ���� , HullWhite ������ �Ű�����, UFR, UFR ���űⰣ �� �Ű������� ������ ������
	 *   
	 * @param baseDate  : ��������
	 * @param curveHisList : ���� �ݸ� �Ⱓ���� 
	 * @param param       : HullWhite ������ �Ű�����
	 * @param ufr	   : UFR
	 * @param ufrt	   : UFR ���űⰣ
 	 */
	/*public HullWhite(String baseDate, List<IrCurveHis> curveHisList, List<ParamApply> param,  double ufr, double ufrt) {
		this.baseYymm = baseDate;
		this.intRate = new double[curveHisList.size()];
		this.matOfYear = new double[curveHisList.size()];
		for (int i = 0; i < matOfYear.length; i++) {
			intRate[i] = curveHisList.get(i).getIntRate();
			matOfYear[i] = Double.valueOf(curveHisList.get(i).getMatCd().split("M")[1]) / 12;
		}
		this.ufr = ufr;
		this.ufrt = ufrt;
		this.paramHisList = param;
	}*/

	public HullWhiteAsync(String baseDate, List<IrCurveHis> curveHisList, List<BizEsgParam> param,  double ufr, double ufrt) {
		this.baseYymm = baseDate;								//baseYymm �� ���� + bacthNo �� ������ ==> seed Number baseYymm ���� �����ϴµ� batch No �� ���� �ٸ� �õ带 �ֱ� ������.											
		this.intRate = new double[curveHisList.size()];
		this.matOfYear = new double[curveHisList.size()];
		for (int i = 0; i < matOfYear.length; i++) {
			intRate[i] = curveHisList.get(i).getIntRate();
			matOfYear[i] = Double.valueOf(curveHisList.get(i).getMatCd().split("M")[1]) / 12;
		}
		this.ufr = ufr;
		this.ufrt = ufrt;
		this.bizParamHisList = param;
		logger.info("intSize : {},{}", intRate.length, matOfYear.length);
	}
	/**
	 *  R Script �� ������ ����� �ݸ� �ó������� ���·� ��ȯ�ϴ� ����� ������.
	 *   
	 * @param bssd 	  	   : ���س��
	 * @param irCurveId    : �ݸ�� ID 
	 * @param modelId	   : �ݸ� �ó������� �����ϱ� ���� ����
	 * @param sceNum	   : �ó����� ���� ����
	 * @param batchNo	   : �ó����� ���� ��ġ�� ���� ( �ó������� �κ������� �����ϱ� ���� �Ű�������)
     * 
	 * @return List			   :�ݸ� �ó����� 
	 */
	public List<IrSce> getHullWhiteScenario(String bssd, String irCurveId, String modelId, int sceNum, int batchNo){
//		logger.info("Call Hw scenario for {} ,  batchNo : {}, {}, {}" , irCurveId, batchNo, baseYymm, sceNum);
		logger.info("Call Hw scenario for {} ,  batchNo : {}" , irCurveId, batchNo);
		List<IrSce> irScenarioList = new ArrayList<IrSce>();
		IrSce tempSce;
		int sceNo ;
		SEXP hwRst = getModelResult(sceNum);
		
		for(int k =0; k< hwRst.getElementAsSEXP(0).length(); k++) {
			sceNo = ( batchNo - 1) * sceNum + hwRst.getElementAsSEXP(2).getElementAsSEXP(k).asInt() ;
					
			tempSce = new IrSce();
			tempSce.setBaseDate(bssd);
			tempSce.setIrCurveId(irCurveId);
			
			tempSce.setIrModelId(modelId);
			tempSce.setSceNo( String.valueOf(sceNo));
			tempSce.setMatCd("M"+ String.format("%04d", hwRst.getElementAsSEXP(4).getElementAsSEXP(k).asInt()));
			tempSce.setRfRate(hwRst.getElementAsSEXP(6).getElementAsSEXP(k).asReal());
			
			tempSce.setLastModifiedBy("ESG");
			tempSce.setLastUpdateDate(LocalDateTime.now());
			
			irScenarioList.add(tempSce);
		}
		return irScenarioList;
	}
	
	/**
	 *  R Script �� �̿��� Hull White 1 Factor Model ����
	 *  @param sceNum		: ������ �ó����� ����
	 *  @return SEXP (Renjin ����� ���� ����� Data Type)  
	*/
	private SEXP getModelResult(int sceNum) {
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
			engine.put("params", getParamBuilder().build());
			engine.put("bse.ym", baseYymm);							//�õ� �ѹ��� ������ ������.
			engine.put("num.of.scen", sceNum);
			engine.put("int.type", "annu");
			
			String scriptHW = "Hw1f.simulation.run(bse.ym, int, mat, params, num.of.scen = num.of.scen, int.type= int.type, ufr=ufr, ufr.t=ufr.t)";
			SEXP hwRst = (SEXP) engine.eval(scriptHW);

			return hwRst;
		} catch (Exception e) {
			logger.error("Renjin Error : {}", e);
		}
		return null;
	}
	
	private ListVector.NamedBuilder getParamBuilder(){
		StringVector.Builder paramTypeBuilder = new StringVector.Builder();
		DoubleArrayVector.Builder matYearBuilder = new DoubleArrayVector.Builder();
		DoubleArrayVector.Builder valBuilder = new DoubleArrayVector.Builder();

		ListVector.NamedBuilder dfProc = new ListVector.NamedBuilder();
		dfProc.setAttribute(Symbols.CLASS, StringVector.valueOf("data.frame"));
//		dfProc.setAttribute(Symbols.ROW_NAMES, new RowNamesVector(paramHisList.size()));
		dfProc.setAttribute(Symbols.ROW_NAMES, new RowNamesVector(bizParamHisList.size()));
		
/*		for (ParamApply aa : paramHisList) {
			paramTypeBuilder.add(aa.getParamTypCd());
			matYearBuilder.add(Double.valueOf(aa.getMatCd().split("M")[1]));
			valBuilder.add(aa.getApplParamVal());
		}*/
		
		for (BizEsgParam aa : bizParamHisList) {
			logger.info("paramHis : {},{}", aa.getParamTypCd(),Double.valueOf(aa.getMatCd().split("M")[1]) /12 );
			paramTypeBuilder.add(aa.getParamTypCd());
			matYearBuilder.add(Double.valueOf(aa.getMatCd().split("M")[1]));
			valBuilder.add(aa.getApplParamVal());
//			if(aa.getParamTypCd().contains("SIGMA")) {
//				valBuilder.add(aa.getApplParamVal()/0.5);
//			}
//			else {
//				valBuilder.add(aa.getApplParamVal());
//			}
		}
		logger.info("paramHis : {},{},{}", paramTypeBuilder.length(), matYearBuilder.length(), valBuilder.length());
		
		dfProc.add("PARAM_TYP_CD", paramTypeBuilder.build());
		dfProc.add("MAT_CD", matYearBuilder.build());
		dfProc.add("PARAM_VAL", valBuilder.build());
		
		return dfProc;
	}
}
