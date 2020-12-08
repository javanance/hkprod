package com.gof.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;

import org.renjin.script.RenjinScriptEngineFactory;
import org.renjin.sexp.SEXP;

import com.gof.entity.SmithWilsonResult;
import com.gof.enums.EMatCd;
import com.gof.interfaces.IIntRate;
import com.gof.util.EsgConstant;
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
public class SmithWilsonModelCore {
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
	private String  compound = "disc";
	private double  projectionYear = 101;
	private String sceNo;
	private String bssd;
	private String irCurveId;
	private SEXP smithWilsonSEXP;
	
	
	public SmithWilsonModelCore() {
	}
	
	public SmithWilsonModelCore(String bssd, String irCurveId, String sceNo, Map<String, Double> curveHisMap,  double ufr, double ufrt) {
		this();
		if(curveHisMap.size()>0) {
			this.intRate = new double[curveHisMap.size()];
			this.matOfYear = new double[curveHisMap.size()];
			int i=0;
//			for(Map.Entry<String , ? extends IIntRate> entry : curveHisMap.entrySet()) {
//				intRate[i] = entry.getValue().getIntRate() + lpMap.getOrDefault(entry.getKey(), 0.0);
//				matOfYear[i] = Double.valueOf(entry.getKey().split("M")[1]) / 12;
//				i++;
//			}
			
			List<String> tenorList = EsgConstant.getTenorList();
			double prevRate=0.0;
			
			for(String matCd: tenorList) {
//				log.info("zzz : {}", matCd);
				if(curveHisMap.containsKey(matCd)) {
					intRate[i] = curveHisMap.getOrDefault(matCd, prevRate);
					matOfYear[i] = Double.valueOf(matCd.split("M")[1]) / 12;
					prevRate = intRate[i];
					i++;
				}
			}
//			curveHisMap.entrySet().forEach(s-> log.info("curve : {},{}", s.getKey(), s.getValue()));
//			Arrays.stream(intRate).forEach(s-> log.info("intRate : {}", s));
//			Arrays.stream(matOfYear).forEach(s-> log.info("mat : {}", s));
			
			this.ufr = ufr;
			this.ufrt = ufrt;
			this.sceNo = sceNo;
			this.bssd= bssd;
			this.irCurveId =irCurveId;
		}
		else {
			log.error("Smith Wilson Error : input Curve Size =0");
			System.exit(0);
		}
	}
	
	public SmithWilsonModelCore(String bssd, String irCurveId, String sceNo, Map<String, ? extends IIntRate> curveHisMap,  Map<String, Double> lpMap, double ufr, double ufrt) {
		this();
		if(curveHisMap.size()>0) {
			this.intRate = new double[curveHisMap.size()];
			this.matOfYear = new double[curveHisMap.size()];
			int i=0;
//			for(Map.Entry<String , ? extends IIntRate> entry : curveHisMap.entrySet()) {
//				intRate[i] = entry.getValue().getIntRate() + lpMap.getOrDefault(entry.getKey(), 0.0);
//				matOfYear[i] = Double.valueOf(entry.getKey().split("M")[1]) / 12;
//				i++;
//			}
			
			List<String> tenorList = EsgConstant.getTenorList();
			
			
			for(String matCd: tenorList) {
//				log.info("zzz : {}", matCd);
				if(curveHisMap.containsKey(matCd)) {
					intRate[i] = curveHisMap.get(matCd).getIntRate() + lpMap.getOrDefault(matCd, 0.0);
					matOfYear[i] = Double.valueOf(matCd.split("M")[1]) / 12;
					i++;
				}
			}
//			curveHisMap.entrySet().forEach(s-> log.info("curve : {},{}", s.getKey(), s.getValue()));
//			Arrays.stream(intRate).forEach(s-> log.info("intRate : {}", s));
//			Arrays.stream(matOfYear).forEach(s-> log.info("mat : {}", s));
			
			this.ufr = ufr;
			this.ufrt = ufrt;
			this.sceNo = sceNo;
			this.bssd= bssd;
			this.irCurveId =irCurveId;
		}
		else {
			log.error("Smith Wilson Error : input Curve Size =0");
			System.exit(0);
		}
	}
//	public SmithWilsonModelCore(String bssd, String irCurveId, String sceNo, Map<String, ? extends IIntRate> curveHisMap, double spread, double ufr, double ufrt) {
//		this();
//		
//		if(curveHisMap.size()>0) {
//			this.intRate = new double[curveHisMap.size()];
//			this.matOfYear = new double[curveHisMap.size()];
//			int i=0;
//			
//			List<String> tenorList = EsgConstant.getTenorList();
//			for(String matCd: tenorList) {
//				if(curveHisMap.containsKey(matCd)) {
//					intRate[i] = curveHisMap.get(matCd).getIntRate() + spread;
//					matOfYear[i] = Double.valueOf(matCd.split("M")[1]) / 12;
//					i++;
//				}
//			}
//			
//			this.ufr = ufr;
//			this.ufrt = ufrt;
//			this.sceNo = sceNo;
//			this.bssd= bssd;
//			this.irCurveId =irCurveId;
//		}
//		else {
//			log.error("Smith Wilson Error : input Curve Size =0");
//			System.exit(0);
//		}
//	}
	
	
	public List<SmithWilsonResult> getSmithWilsionResult(){
		return getSmithWilsionResult(true);
	}
	
	public double getSmithWilsonAlphaValue() {
		SEXP modelRst = runSmithWilson(true);
		return modelRst.getElementAsSEXP(0).asReal();
	}

	private List<SmithWilsonResult> getSmithWilsionResult(boolean isFwdGen){
		List<SmithWilsonResult> rstList = new ArrayList<SmithWilsonResult>();
		SmithWilsonResult temp;
		
		//smith wilsion �� spot term structure...
		SEXP modelRst = runSmithWilson(isFwdGen).getElementAsSEXP(0);
//		logger.info("in the sw Model : {}", modelRst);
		int cnt = modelRst.getElementAsSEXP(0).length();
//		log.info("sw calc : {},{}", sceNo, LocalTime.now());
		
		double alpha = runSmithWilson(isFwdGen).getElementAsSEXP(2).asReal();
		
		log.info("alpha :{}, {}", irCurveId,alpha);
		log.info("alpha1 :{}, {}", cnt, modelRst.getElementAsSEXP(1).length());
		
		for(int k =0; k < cnt; k++) {
			temp = new SmithWilsonResult();
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
	
	private SEXP runSmithWilson(boolean isFwdGen) {
		if(smithWilsonSEXP ==null) {
//			logger.info("Run Smith Wilson ....");
			System.setProperty("com.github.fommil.netlib.BLAS","com.github.fommil.netlib.F2jBLAS");
			System.setProperty("com.github.fommil.netlib.LAPACK","com.github.fommil.netlib.F2jLAPACK");
			
			List<String> scriptString = ScriptUtil.getScriptContents();
			
//			log.info("sw calc1 : {},{},{}", sceNo,Thread.currentThread().getName(),  LocalTime.now());
//			ScriptEngine engine = ENGINE.get();
//			if(engine==null) {
//				RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();		
//				engine = factory.getScriptEngine();
//				ENGINE.set(engine);
//			}

			//			engine.getContext().getBindings(0).entrySet().stream().forEach(s-> log.info("zzzz  : {},{}", s.getKey(), s.getValue()));
			
			RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();
			ScriptEngine engine = factory.getScriptEngine();
//			log.info("sw calc2 : {},{},{}", sceNo, Thread.currentThread().getName(), LocalTime.now());
			
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
				
//				engine.put("alpha", 0.1298580);
//				engine.put("term", 1.0/12.0);
				
//				engine.put("min.alpha", minAlpha);
//				engine.put("max.alpha" ,maxAlpha);
//				engine.put("tol", tolerance);
//				engine.put("llp" ,llp);
//				engine.put("base.date", baseDate);
//				engine.put("isTerm", dateApplyType);
//				engine.put("l2", 1/12);
//				log.info("apll : {}" ,engine.eval("alpha"));
				
//				String script = "SW.run(int, mat, ufr, ufr.t, proj.y = proj.y, ts.proj.tf= isFwdCurve, int.type = compound)";
				String script = "SW.run(int, mat, ufr, ufr.t, proj.y = proj.y, int.type = compound)";
//				String script = "SW.run(int, mat, ufr, ufr.t, proj.y = proj.y, term=term,  alpha = alpha, ts.proj.tf= isFwdCurve, int.type = compound)";
				
				smithWilsonSEXP = (SEXP)engine.eval(script);
//				log.info("sw calc3 : {},{}", sceNo, LocalTime.now());
				return smithWilsonSEXP;
			} catch (Exception e) {
				log.error("Renjin Error : {}", e);
			}
		}
		return smithWilsonSEXP;
	}

//	private SEXP runSmithWilsonAlpha() {
//		System.setProperty("com.github.fommil.netlib.BLAS","com.github.fommil.netlib.F2jBLAS");
//		System.setProperty("com.github.fommil.netlib.LAPACK","com.github.fommil.netlib.F2jLAPACK");
//		
//		List<String> scriptString = ScriptUtil.getScriptContents();
//		
//		RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();		
//		ScriptEngine engine = factory.getScriptEngine();
//		try {
//			for(String aa : scriptString) {
//				engine.eval( aa);
//			}
//			
//			engine.put("int", intRate);
//			engine.put("mat", matOfYear);
//			engine.put("ufrc", ufr);
//			engine.put("ufr.t", ufrt);
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
//			String script = "SW.alpha.find(int, mat, ufrc, ufr.t)";
//			
//			SEXP rst = (SEXP)engine.eval(script);
////			logger.error("Renjin aaa : {},{}", matOfYear, rst);
//			return rst;
//		} catch (Exception e) {
//			log.error("Renjin Error : {}", e);
//		}
//		return null;
//	}
}
