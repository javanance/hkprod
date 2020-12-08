package com.gof.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;

import org.renjin.script.RenjinScriptEngineFactory;
import org.renjin.sexp.SEXP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.entity.BizDiscountRateSce;
import com.gof.entity.BizEsgParam;
import com.gof.entity.BizIrCurveSce;
import com.gof.entity.IrSce;
import com.gof.interfaces.Rrunnable;
import com.gof.util.ScriptUtil;

/**
 *  Controller Class for Vacicek Model.        
 *  <p> R Script �� �����ϱ� ����  Input Data ���� �� ���� , R Script ����, Output Converting �۾��� ������.
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

public class CIR implements Rrunnable {
	private final static Logger logger = LoggerFactory.getLogger(CIR.class);

	private String baseYymm;
	private double shortRate =0.01;
	private double revSpeed =0.02;
	private double revLevel =0.02;
	private double sigma =0.01;
	private List<BizEsgParam> bizParamHisList = new ArrayList<BizEsgParam>();
	
	public CIR() {
	}
	
	/**
	 *  ��������, �ݸ� ������ �Ű������� �Ű������� ������ ������
	 * @param  shortRate     : ShortRate( ���������ݸ�)  
	 * @param  param     : �ݸ����� �Ű����� 
 	 */
	public CIR(double shortRate, List<BizEsgParam> param) {
		this.bizParamHisList = param;
		this.shortRate = shortRate;
		for(BizEsgParam aa : param) {
			if(aa.getParamTypCd().toUpperCase().equals("REV_SPEED")) {
				revSpeed = aa.getApplParamVal();
			}
			else if(aa.getParamTypCd().toUpperCase().equals("REV_LEVEL")) {
				revLevel = aa.getApplParamVal();
			}
			else if(aa.getParamTypCd().toUpperCase().equals("SIGMA")) {
				sigma = aa.getApplParamVal();
			}
		}
	}
	/**
	 *  ��������, �ݸ� ������ �Ű������� �Ű������� ������ ������
	 *   
	 * @param  baseDate  : ��������
	 * @param  shortRate     : ShortRate( ���������ݸ�)
	 * @param  param     : �ݸ����� �Ű����� 
 	 */
	public CIR(String baseYymm, double shortRate , List<BizEsgParam> param) {
		this(shortRate, param);
		this.baseYymm = baseYymm;
	}

	/**
	 *  R Script �� ������ ����� �ݸ� �ó������� ���·� ��ȯ�ϴ� ����� ������.
	 *   
	 * @param  bssd 	  : ���س��
	 * @param  irCurveId : �ݸ�� ID 
	 * @param  modelId   : �ݸ� �ó������� �����ϱ� ���� ����
	 * @param  sceNum	   : �ó����� ���� ����
	 * @param  batchNo	   : �ó����� ���� ��ġ�� ���� ( �ó������� �κ������� �����ϱ� ���� �Ű�������)
     * 
	 * @return List			   :�ݸ� �ó����� 
	 */
	public List<IrSce> getCirScenario(String bssd, String irCurveId, String modelId, int sceNum, int batchNo){
		List<IrSce> irScenarioList = new ArrayList<IrSce>();
		IrSce tempSce;
		int sceNo ;
		
		SEXP cirRst = getModelResult(sceNum).getElementAsSEXP(0);
		
//		logger.info("CIR : {},{}" , shortRate, getModelResult(sceNum));
		
		for(int k =0; k< cirRst.getElementAsSEXP(0).length(); k++) {
			sceNo =( batchNo -1) * sceNum + cirRst.getElementAsSEXP(2).getElementAsSEXP(k).asInt() ;
					
			tempSce = new IrSce();
			tempSce.setBaseDate(bssd);
			tempSce.setIrCurveId(irCurveId);
			
			tempSce.setIrModelId(modelId);
			tempSce.setSceNo( String.valueOf(sceNo));
			tempSce.setMatCd("M"+ String.format("%04d", cirRst.getElementAsSEXP(4).getElementAsSEXP(k).asInt()));
			tempSce.setRfRate(cirRst.getElementAsSEXP(6).getElementAsSEXP(k).asReal());
			
			tempSce.setLastModifiedBy("ESG");
			tempSce.setLastUpdateDate(LocalDateTime.now());
			
			irScenarioList.add(tempSce);
		}
		return irScenarioList;
	}
	
	public List<BizIrCurveSce> getCirScenario1(String bssd, String irCurveId, String bizDv, int sceNum, int batchNo){
		List<BizIrCurveSce> irScenarioList = new ArrayList<BizIrCurveSce>();
		BizIrCurveSce tempSce;
		int sceNo ;
		
		SEXP cirRst = getModelResult(sceNum).getElementAsSEXP(0);
		
//		logger.info("CIR : {},{}" , shortRate, getModelResult(sceNum));
		
		for(int k =0; k< cirRst.getElementAsSEXP(0).length(); k++) {
			sceNo =( batchNo -1) * sceNum + cirRst.getElementAsSEXP(2).getElementAsSEXP(k).asInt() ;
			
			tempSce = new BizIrCurveSce();
			tempSce.setBaseYymm(bssd);
			tempSce.setApplBizDv(bizDv);

			tempSce.setIrCurveId(irCurveId);
			
			tempSce.setSceNo( String.valueOf(sceNo));
			tempSce.setMatCd("M"+ String.format("%04d", cirRst.getElementAsSEXP(4).getElementAsSEXP(k).asInt()));
			tempSce.setRfRate(cirRst.getElementAsSEXP(6).getElementAsSEXP(k).asReal());
			tempSce.setRfRate(cirRst.getElementAsSEXP(9).getElementAsSEXP(k).asReal());
			
			tempSce.setLastModifiedBy("ESG");
			tempSce.setLastUpdateDate(LocalDateTime.now());
			
			irScenarioList.add(tempSce);
		}
		return irScenarioList;
	}
	
	
	public List<BizDiscountRateSce> getBizDiscountRateSce(String bssd, String irCurveId, String bizDv, int sceNum, int batchNo){
		List<BizDiscountRateSce> irScenarioList = new ArrayList<BizDiscountRateSce>();
		BizDiscountRateSce tempSce;
		int sceNo ;
		
		SEXP cirRst = getModelResult(sceNum).getElementAsSEXP(0);
		
//		logger.info("CIR : {},{}" , shortRate, getModelResult(sceNum));
		
		for(int k =0; k< cirRst.getElementAsSEXP(0).length(); k++) {
			sceNo =( batchNo -1) * sceNum + cirRst.getElementAsSEXP(2).getElementAsSEXP(k).asInt() ;
			
			tempSce = new BizDiscountRateSce();
			tempSce.setBaseYymm(bssd);
			tempSce.setApplyBizDv(bizDv);

			tempSce.setIrCurveId(irCurveId);
			
			tempSce.setSceNo( String.valueOf(sceNo));
			tempSce.setMatCd("M"+ String.format("%04d", cirRst.getElementAsSEXP(4).getElementAsSEXP(k).asInt()));
			tempSce.setRiskAdjRfRate(cirRst.getElementAsSEXP(6).getElementAsSEXP(k).asReal());
			tempSce.setRiskAdjRfFwdRate(cirRst.getElementAsSEXP(9).getElementAsSEXP(k).asReal());
			
			tempSce.setLastModifiedBy("ESG");
			tempSce.setLastUpdateDate(LocalDateTime.now());
			
			irScenarioList.add(tempSce);
		}
		return irScenarioList;
	}
	
	/**
	 *  R Script �� �̿��� ESG �ݸ����� ����
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

			engine.put("shortRate", shortRate);
			engine.put("revSpeed", revSpeed);
			engine.put("revLevel", revLevel);
			engine.put("baseYymm", baseYymm);
			engine.put("sigma", sigma);
			engine.put("sceNum", sceNum);
			engine.put("intType", "annu");
			
			String scriptHW = "Cir.run(bse.ym =baseYymm, r0=shortRate, rev_speed=revSpeed, rev_level=revLevel, sigma=sigma, num.of.scen = sceNum, int.type= intType)";
			SEXP hwRst = (SEXP) engine.eval(scriptHW);

			return hwRst;
		} catch (Exception e) {
			logger.error("Renjin Error : {}", e);
		}
		return null;
	}
}
