package com.gof.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;

import org.renjin.script.RenjinScriptEngineFactory;
import org.renjin.sexp.SEXP;

import com.gof.dao.SmithWilsonDao;
import com.gof.entity.BizLiqPremium;
import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveHis;
import com.gof.entity.SmithWilsonParamHis;
import com.gof.entity.SmithWilsonResult;
import com.gof.util.ScriptUtil;

import lombok.extern.slf4j.Slf4j;


/**
 *  <p> Smith Wilson ����        
 *  <p> Hull and White 1 Factor, 2 Factor, CIR, Vacicek ������ ������ �ݸ��Ⱓ ������ ���ܹ��� �����Ͽ� �����ݸ��Ⱓ ���� ������.  
 *  <p>  Script �� �����ϱ� ����  Input Data ���� �� ���� , R Script ����, Output Converting �۾��� ������.</p>
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class SmithWilsonModel {
	private final static ThreadLocal<ScriptEngine> ENGINE = new ThreadLocal<ScriptEngine>();
	
	private double[] intRate;
	private double[] matOfYear;
	private double  ufr;
	private double  ufrt ;
//	private double  minAlpha = 0.001;
//	private double  maxAlpha = 1;
//	private double  tolerance = 0.001;
//	private double  llp	=20;
//	private String  baseDate;	
//	private String  dateApplyType ="F";
	private String  compound = "annu";
	private double  projectionYear = 100;
	private String sceNo;
	private String bssd;
	private String irCurveId;
	private List<IrCurveHis> irCurveHisList = new ArrayList<IrCurveHis>();
	private SEXP smithWilsonSEXP;
	
	
	public SmithWilsonModel() {
	
	}
	
	public SmithWilsonModel(double[] intRate, double[] matOfYear, double ufr, double ufrt) {
		this.intRate = intRate;
		this.matOfYear = matOfYear;
		this.ufr = ufr;
		this.ufrt = ufrt;
	}
//	public SmithWilsonModel(Map<String, Double> curveHisMap, double ufr, double ufrt) {
//		this();
//		this.irCurveHisList = curveHisList;
//		this.intRate = new double[curveHisList.size()];
//		this.matOfYear = new double[curveHisList.size()];
//		for(Map.Entry<String, Double> entry : curveHisMap.entrySet()) {
//			int i = Integer.valueOf(entry.getKey().split("M")[1]);
//			matOfYear[i] = i / 12.0;
//			intRate[i] = entry.getValue();
//		}
//		
//		for (int i = 0; i < matOfYear.length; i++) {
//			intRate[i] = curveHisList.get(i).getIntRate();
//			matOfYear[i] = Double.valueOf(curveHisList.get(i).getMatCd().split("M")[1]) / 12;
//		}
//		this.ufr = ufr;
//		this.ufrt = ufrt;
//		init();
//	}
	
	public SmithWilsonModel(List<IrCurveHis> curveHisList, double ufr, double ufrt) {
		this();
		this.irCurveHisList = curveHisList;
		this.intRate = new double[curveHisList.size()];
		this.matOfYear = new double[curveHisList.size()];
		for (int i = 0; i < matOfYear.length; i++) {
			intRate[i] = curveHisList.get(i).getIntRate();
			matOfYear[i] = Double.valueOf(curveHisList.get(i).getMatCd().split("M")[1]) / 12;
		}
		this.ufr = ufr;
		this.ufrt = ufrt;
		init();
	}
	
	public SmithWilsonModel(List<IrCurveHis> curveHisList, double volAdj, double ufr, double ufrt) {
		this();
		this.irCurveHisList = curveHisList;
		this.intRate = new double[curveHisList.size()];
		this.matOfYear = new double[curveHisList.size()];
		
		for (int i = 0; i < matOfYear.length; i++) {
			intRate[i] = curveHisList.get(i).getIntRate() + volAdj;
			matOfYear[i] = Double.valueOf(curveHisList.get(i).getMatCd().split("M")[1]) / 12;
		}
		
		this.ufr = ufr;
		this.ufrt = ufrt;
		init();
	}
	
	public SmithWilsonModel(List<IrCurveHis> curveHisList, List<BizLiqPremium> liqList, double ufr, double ufrt) {
		this();
		this.irCurveHisList = curveHisList;
		this.intRate = new double[curveHisList.size()];
		this.matOfYear = new double[curveHisList.size()];
		
		Map<String, BizLiqPremium> lpMap = liqList.stream().collect(Collectors.toMap(s -> s.getMatCd(), Function.identity()));
		double tempLp =0.0;
		for (int i = 0; i < matOfYear.length; i++) {
			if (lpMap.containsKey(curveHisList.get(i).getMatCd())) {
				tempLp = lpMap.get(curveHisList.get(i).getMatCd()).getLiqPrem();
			}

			if (curveHisList.get(i).getMatCd().equals("M0240")) {
				tempLp = 0.0;
			}
			intRate[i] = curveHisList.get(i).getIntRate() + tempLp;
			matOfYear[i] = Double.valueOf(curveHisList.get(i).getMatCd().split("M")[1]) / 12;
		}
		this.ufr = ufr;
		this.ufrt = ufrt;
		init();
	}
	
	//	public SmithWilsonModel(List<IrCurveHis> curveHisList, List<BizLiqPremiumUd> liqList, double ufr, double ufrt) {
	//		this();
	//		this.intRate = new double[curveHisList.size()];
	//		this.matOfYear = new double[curveHisList.size()];
	//		
	//		Map<String, BizLiqPremiumUd> lpMap = liqList.stream().collect(Collectors.toMap(s -> s.getMatCd(), Function.identity()));
	//		double tempLp =0.0;
	//		for (int i = 0; i < matOfYear.length; i++) {
	//			if (lpMap.containsKey(curveHisList.get(i).getMatCd())) {
	////				tempLp = lpMap.get(curveHisList.get(i).getMatCd()).getLiqPrem();
	//				tempLp = lpMap.get(curveHisList.get(i).getMatCd()).getApplyLiqPrem();
	//			}
	//			if (curveHisList.get(i).getMatCd().equals("M0240")) {
	//				tempLp = 0.0;
	//			}
	//			intRate[i] = curveHisList.get(i).getIntRate() + tempLp;
	//			matOfYear[i] = Double.valueOf(curveHisList.get(i).getMatCd().split("M")[1]) / 12;
	//		}
	//		
	//		this.ufr = ufr;
	//		this.ufrt = ufrt;
	//	}
		
		public SmithWilsonModel(List<IrCurveHis> curveHisList) {
			this();
			
			this.intRate = new double[curveHisList.size()];
			this.matOfYear = new double[curveHisList.size()];
			for (int i = 0; i < matOfYear.length; i++) {
				intRate[i] = curveHisList.get(i).getIntRate();
				matOfYear[i] = Double.valueOf(curveHisList.get(i).getMatCd().split("M")[1]) / 12;
			}
			
			Map<String, Object> param = new HashMap<String, Object>();
			log.info("Curve currencny : {}", curveHisList.get(0).toString());
			param.put("CUR_CD",curveHisList.get(0).getIrCurve().getCurCd());
			
			
			List<SmithWilsonParamHis> swParam = SmithWilsonDao.getParamHisList(bssd);
			this.ufr  = swParam.get(0) ==null? 0.045: swParam.get(0).getUfr();
			this.ufrt = swParam.get(0) ==null? 60   : swParam.get(0).getUfrT();
	
		}

	//	public SmithWilsonModel(List<IrCurveHis> curveHisList, List<BizLiqPremiumUd> liqList, double ufr, double ufrt) {
		//		this();
		//		this.intRate = new double[curveHisList.size()];
		//		this.matOfYear = new double[curveHisList.size()];
		//		
		//		Map<String, BizLiqPremiumUd> lpMap = liqList.stream().collect(Collectors.toMap(s -> s.getMatCd(), Function.identity()));
		//		double tempLp =0.0;
		//		for (int i = 0; i < matOfYear.length; i++) {
		//			if (lpMap.containsKey(curveHisList.get(i).getMatCd())) {
		////				tempLp = lpMap.get(curveHisList.get(i).getMatCd()).getLiqPrem();
		//				tempLp = lpMap.get(curveHisList.get(i).getMatCd()).getApplyLiqPrem();
		//			}
		//			if (curveHisList.get(i).getMatCd().equals("M0240")) {
		//				tempLp = 0.0;
		//			}
		//			intRate[i] = curveHisList.get(i).getIntRate() + tempLp;
		//			matOfYear[i] = Double.valueOf(curveHisList.get(i).getMatCd().split("M")[1]) / 12;
		//		}
		//		
		//		this.ufr = ufr;
		//		this.ufrt = ufrt;
		//	}
			
	public SmithWilsonModel(IrCurve curveMst, Map<String, Double> curveHisMap) {
		this.irCurveId = curveMst.getIrCurveId();
		this.intRate = new double[curveHisMap.size()];
		this.matOfYear = new double[curveHisMap.size()];

		int idx=0;
		for(Map.Entry<String, Double> entry : curveHisMap.entrySet()) {
			matOfYear[idx] = Double.valueOf(entry.getKey().split("M")[1]) / 12.0;
			intRate[idx] = entry.getValue();
			idx++;
		}
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("CUR_CD",curveMst.getCurCd());
		
		List<SmithWilsonParamHis> swParam = SmithWilsonDao.getParamHisList(bssd);
		
		this.ufr  = swParam.get(0) ==null? 0.045: swParam.get(0).getUfr();
		this.ufrt = swParam.get(0) ==null? 60   : swParam.get(0).getUfrT();
	}

	
	
//	public SmithWilsonModel(List<IrCurveHis> curveHisList, List<BizLiqPremiumUd> liqList, double ufr, double ufrt) {
//		this();
//		this.intRate = new double[curveHisList.size()];
//		this.matOfYear = new double[curveHisList.size()];
//		
//		Map<String, BizLiqPremiumUd> lpMap = liqList.stream().collect(Collectors.toMap(s -> s.getMatCd(), Function.identity()));
//		double tempLp =0.0;
//		for (int i = 0; i < matOfYear.length; i++) {
//			if (lpMap.containsKey(curveHisList.get(i).getMatCd())) {
////				tempLp = lpMap.get(curveHisList.get(i).getMatCd()).getLiqPrem();
//				tempLp = lpMap.get(curveHisList.get(i).getMatCd()).getApplyLiqPrem();
//			}
//			if (curveHisList.get(i).getMatCd().equals("M0240")) {
//				tempLp = 0.0;
//			}
//			intRate[i] = curveHisList.get(i).getIntRate() + tempLp;
//			matOfYear[i] = Double.valueOf(curveHisList.get(i).getMatCd().split("M")[1]) / 12;
//		}
//		
//		this.ufr = ufr;
//		this.ufrt = ufrt;
//	}
	
	public SEXP getSmithWilsonSEXP() {
		return getSmithWilsonSEXP(true);
	}
	
	/**
	 *  R Script �� �̿��� Smith Wilson �� ��� ����
	 *  @param isFwdGen		: forward rate ���� ���� 
	 *  @return SEXP (Renjin ����� ���� ����� Data Type)  
	*/
	public SEXP getSmithWilsonSEXP(boolean isFwdGen) {
		if(smithWilsonSEXP ==null) {
//			logger.info("Run Smith Wilson ....");
			System.setProperty("com.github.fommil.netlib.BLAS","com.github.fommil.netlib.F2jBLAS");
			System.setProperty("com.github.fommil.netlib.LAPACK","com.github.fommil.netlib.F2jLAPACK");
			
			List<String> scriptString = ScriptUtil.getScriptContents();
			
			ScriptEngine engine = ENGINE.get();
			if(engine==null) {
				RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();		
				engine = factory.getScriptEngine();
				ENGINE.set(engine);
			}
			
			try {
				for(String aa : scriptString) {
					engine.eval( aa);
				}
				
				engine.put("int", intRate);
				engine.put("mat", matOfYear);
				engine.put("ufr", ufr);
				engine.put("ufr.t", ufrt);
				engine.put("proj.y", projectionYear);
				
				engine.put("isFwdCurve", isFwdGen);
				engine.put("compound", compound);
				
//				engine.put("min.alpha", minAlpha);
//				engine.put("max.alpha" ,maxAlpha);
//				engine.put("tol", tolerance);
//				engine.put("llp" ,llp);
//				engine.put("base.date", baseDate);
//				engine.put("isTerm", dateApplyType);
//				engine.put("l2", 1/12);
				
				String script = "SW.run(int, mat, ufr, ufr.t, proj.y = proj.y, ts.proj.tf= isFwdCurve, int.type = compound)";
				
				
				smithWilsonSEXP = (SEXP)engine.eval(script);
				return smithWilsonSEXP;
			} catch (Exception e) {
				log.error("Renjin Error : {}", e);
			}
		}
		return smithWilsonSEXP;
	}
	
	
	public List<SmithWilsonResult> getSmithWilsionResult(){
		return getSmithWilsionResult(true);
	}
	
	public List<SmithWilsonResult> getSmithWilsionResult(boolean isFwdGen){
		List<SmithWilsonResult> rstList = new ArrayList<SmithWilsonResult>();
		SmithWilsonResult temp;
		
		//smith wilsion �� spot term structure...
		SEXP modelRst = getSmithWilsonSEXP(isFwdGen).getElementAsSEXP(0);
//		logger.info("in the sw Model : {}", modelRst);
		int cnt = modelRst.getElementAsSEXP(0).length();
		
		for(int k =0; k < cnt; k++) {
			temp = new  SmithWilsonResult();
			temp.setBaseYymm(bssd);
			temp.setIrCurveId(irCurveId);
			temp.setSceNo(sceNo);
			
			temp.setTimeFactor(    modelRst.getElementAsSEXP(0).getElementAsSEXP(k).asReal());
			temp.setMonthNum(      modelRst.getElementAsSEXP(1).getElementAsSEXP(k).asInt());
			temp.setSpotCont(      modelRst.getElementAsSEXP(2).getElementAsSEXP(k).asReal());
			temp.setSpotAnnual(    modelRst.getElementAsSEXP(3).getElementAsSEXP(k).asReal());
			temp.setDiscountFactor(modelRst.getElementAsSEXP(4).getElementAsSEXP(k).asReal());
			temp.setFwdCont(       modelRst.getElementAsSEXP(5).getElementAsSEXP(k).asReal());
			temp.setFwdAnnual(     modelRst.getElementAsSEXP(6).getElementAsSEXP(k).asReal());
			temp.setFwdMonthNum(1);
			
			temp.setMatCd("M" + String.format("%04d", temp.getMonthNum()));
			
			rstList.add(k, temp);
		}
		return rstList;
	}
	
	public List<IrCurveHis> getIrCurveHisList(String bssd){
		List<IrCurveHis> rstList = new ArrayList<IrCurveHis>();
		IrCurveHis temp;

		SEXP modelRst = getSmithWilsonSEXP(false).getElementAsSEXP(0);
		int cnt = modelRst.getElementAsSEXP(0).length();
		
		for(int k =0; k < cnt; k++) {
			temp = new  IrCurveHis();
			
			temp.setBaseDate(bssd);
			temp.setMatCd("M" + String.format("%04d", modelRst.getElementAsSEXP(1).getElementAsSEXP(k).asInt()));
			temp.setSceNo("0");
			temp.setIntRate(    modelRst.getElementAsSEXP(3).getElementAsSEXP(k).asReal());
			temp.setForwardNum(0);
			
			
			
			rstList.add(k, temp);
		}
		return rstList;
	}
		
	public List<SmithWilsonResult> getSwForwardRateAtBucket(String matCd){
		int maturityTerm = Integer.parseInt(matCd.split("M")[1]);
		return getSwForwardRateAtBucket(maturityTerm);
	}
	
	
	/**
	 *  Smith Wilson �� ������ ��   , ���� Bucket �� ���� �̷� �������
	 *  @param maturityTerm  ����Bucket 
	 *  @return  Ư�� ������ �̷� �ݸ� ����ġ   
	*/
	public List<SmithWilsonResult> getSwForwardRateAtBucket(int maturityTerm){
		List<SmithWilsonResult> rstList = new ArrayList<SmithWilsonResult>();
		SmithWilsonResult temp;
		
		//Smithwilsion �� Forward TermStructure ...
		SEXP modelRst = getSmithWilsonSEXP().getElementAsSEXP(1);					// sw dataFrame �� 2��° �������� forward term structure..					
		
		int cnt = modelRst.length();												//���� : time, month, forward term 1200 �� ==> 1202
//		int cnt1 = modelRst.getElementAsSEXP(0).length();							//���� : maturity term : 1200�� 
//		int matNum =0; 
		
		for(int j =2; j < cnt; j++) {
			temp = new  SmithWilsonResult();
			
			temp.setTimeFactor(modelRst.getElementAsSEXP(0).getElementAsSEXP(maturityTerm-1).asReal());
			temp.setMonthNum(modelRst.getElementAsSEXP(1).getElementAsSEXP(maturityTerm-1).asInt());
			
			temp.setFwdMonthNum(j-1);
			temp.setSpotAnnual(modelRst.getElementAsSEXP(j).getElementAsSEXP(maturityTerm-1).asReal());
			rstList.add(temp);
		}
		return rstList;
	}
	
	/**
	 *  Smith Wilson �� ������ �� , �Է��� �̷����� ���� ������ �ݸ� �Ⱓ ����  
	 *  @param forwardTerm   �̷����� ����
	 *  @return  �̷� �ݸ��Ⱓ����  
	*/
	public List<SmithWilsonResult> getSwForwardTermStructure(int forwardTerm){
		List<SmithWilsonResult> rstList = new ArrayList<SmithWilsonResult>();
		SmithWilsonResult temp;
		
		//Smithwilsion �� Forward TermStructure ...
		SEXP modelRst = getSmithWilsonSEXP().getElementAsSEXP(1);					// sw dataFrame �� 2��° �������� fowrader term structure..					
		
//		int cnt = modelRst.length();												//���� : time, month, forward term 1200 �� ==> 1202
		int cnt1 = modelRst.getElementAsSEXP(0).length();							//���� : maturity term : 1200�� 
		
//		int matNum =0; 
		
		if(forwardTerm < 1) {
			forwardTerm = 1;
		}
		
		for(int j =0; j < cnt1; j++) {
			temp = new  SmithWilsonResult();
			
			temp.setTimeFactor(modelRst.getElementAsSEXP(0).getElementAsSEXP(j).asReal());
			temp.setMonthNum(modelRst.getElementAsSEXP(1).getElementAsSEXP(j).asInt());
			
			temp.setFwdMonthNum(forwardTerm);
			temp.setSpotAnnual(modelRst.getElementAsSEXP(1 + forwardTerm).getElementAsSEXP(j).asReal());
			rstList.add(temp);
		}
		return rstList;
	}

	public double getSmithWilsonAlphaValue() {
		SEXP modelRst = getSmithWilsonSEXP();
		return modelRst.getElementAsSEXP(0).asReal();
	}
	
//	private SEXP getSmithWilsonAlpha() {
//			System.setProperty("com.github.fommil.netlib.BLAS","com.github.fommil.netlib.F2jBLAS");
//			System.setProperty("com.github.fommil.netlib.LAPACK","com.github.fommil.netlib.F2jLAPACK");
//			
//			List<String> scriptString = ScriptUtil.getScriptContents();
//			
//			RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();		
//			ScriptEngine engine = factory.getScriptEngine();
//			try {
//				for(String aa : scriptString) {
//					engine.eval( aa);
//				}
//				
//				engine.put("int", intRate);
//				engine.put("mat", matOfYear);
//				engine.put("ufrc", ufr);
//				engine.put("ufr.t", ufrt);
//				
////				engine.put("min.alpha", minAlpha);
////				engine.put("max.alpha" ,maxAlpha);
////				engine.put("tol", tolerance);
////				engine.put("llp" ,llp);
////				engine.put("base.date", baseDate);
////				engine.put("isTerm", dateApplyType);
////				engine.put("compound", compound);
////				engine.put("l2", 1/12);
////				engine.put("proj.y", projectionYear);
////				engine.put("isFwdCurve", true);
//				
//				String script = "SW.alpha.find(int, mat, ufrc, ufr.t)";
//				
//				SEXP rst = (SEXP)engine.eval(script);
//	//			logger.error("Renjin aaa : {},{}", matOfYear, rst);
//				return rst;
//			} catch (Exception e) {
//				log.error("Renjin Error : {}", e);
//			}
//			return null;
//		}
	
	public List<IrCurveHis> convertToIrCurveHis(boolean isFwdGen){
		List<SmithWilsonResult> swRst = getSmithWilsionResult(isFwdGen);
		
		return swRst.stream().map(s -> s.convertToIrCurveHis()).collect(Collectors.toList());
	}

	private void init() {
		IrCurveHis temp = irCurveHisList.get(0);
		this.sceNo = "0";
		if(temp ==null) {
			
		}
		else {
			this.bssd= temp.getBaseYymm();
			this.irCurveId = temp.getIrCurveId();
			this.sceNo = temp.getSceNo()==null ? "0": temp.getSceNo();
		}
	}
	
//	public List<BottomupDcnt> convertToBottomuDcnt(boolean isFwdGen){
//		List<SmithWilsonResult> swRst = getSmithWilsionResult(isFwdGen);
//		
//		return swRst.stream().map(s -> s.convertToIrCurveHis()).collect(Collectors.toList());
//	}
//	public List<DcntSce> convertToDcntSce(boolean isFwdGen){
//		List<SmithWilsonResult> swRst = getSmithWilsionResult(isFwdGen);
//		
//		return swRst.stream().map(s -> s.convertToIrCurveHis()).collect(Collectors.toList());
//	}
}
