package com.gof.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.gof.comparator.IrCurveHisComparator;
import com.gof.entity.IrCurveHis;
import com.gof.entity.IrShockSce;
import com.gof.enums.EBaseMatCd;
import com.gof.util.FinUtils;
import com.gof.util.ScriptUtil;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
public class ArbitrageFreeNelsonSiegelR {
	
	private final static Logger logger = LoggerFactory.getLogger("AFNS");
	private String baseYymm;
//	private double[] matOfYear;
	private double ufr;
	private double ufrt;
	private Map<String, List<IrCurveHis>> curveMap;
	private String mode;
	
	public ArbitrageFreeNelsonSiegelR() {}

	public ArbitrageFreeNelsonSiegelR(String baseDate, Map<String, List<IrCurveHis>> curveMap,  double ufr, double ufrt, String mode) {
		
		this.baseYymm = baseDate;
		this.curveMap = curveMap;
		this.ufr = ufr;
		this.ufrt = ufrt;
		this.mode = mode;
	}
	
	
	//for ASSET
	public List<IrShockSce> getAfnsScenario(String bssd, String irCurveId, double errorTolerance, double volAdjust) {
		
		List<IrShockSce> irScenarioList = new ArrayList<IrShockSce>();
		IrShockSce tempSce;
		double rfRate =0.0;
		double riskAdjRate =0.0;
		int matNum ;
		String sceName;
		
		SEXP afnsRst = getModelResult(errorTolerance, volAdjust);
		logger.info("Afns :{}, {},{}", curveMap.size(), afnsRst.length(), afnsRst.getElementAsSEXP(0));
		logger.info("Afns :{}, {},{}", afnsRst.length(), afnsRst.getElementAsSEXP(0));
		
		//for(int j=6; j < afnsRst.length(); j+=2) {
		for(int j=4; j < 16; j++) {
			//String sceName = afnsRst.getName(j);
			if(j>=4 && j<10) {
				sceName = String.valueOf(j-3);  //first group: 6 scen
			}
			else {
				sceName = String.valueOf(j+1);  //second group: 6 scen
			}
			
			SEXP tempSexp = afnsRst.getElementAsSEXP(j);
			SEXP tempVaSexp = afnsRst.getElementAsSEXP(j+0);
			//SEXP tempVaSexp = afnsRst.getElementAsSEXP(j+1);
				
			for(int k =0; k< tempSexp.getElementAsSEXP(0).length(); k++) {
				
//				TODO: un comment!!! test!!!

//list(SHOCK = shock.gen, PARAS_F = opt.paras$PARAS, LSC_HIST = opt.paras$LSC_HIST, PARAS_I = init.paras,                                                                    
//	     SCEN_A01 = sw.curve.a$BASE, SCEN_A02 = sw.curve.a$MEAN, SCEN_A03 = sw.curve.a$UP, SCEN_A04 = sw.curve.a$DOWN, SCEN_A05 = sw.curve.a$FLAT, SCEN_A06 = sw.curve.a$STEEP,
//	     SCEN_L01 = sw.curve.l$BASE, SCEN_L02 = sw.curve.l$MEAN, SCEN_L03 = sw.curve.l$UP, SCEN_L04 = sw.curve.l$DOWN, SCEN_L05 = sw.curve.l$FLAT, SCEN_L06 = sw.curve.l$STEEP 

				
				if(tempSexp==null && tempVaSexp==null) {
					continue;
				}
				
				rfRate      = tempSexp  == null? 0.0 : tempSexp.getElementAsSEXP(3).getElementAsSEXP(k).asReal();
//				riskAdjRate = tempVaSexp== null? 0.0 : tempVaSexp.getElementAsSEXP(3).getElementAsSEXP(k).asReal();
//				matNum  =  tempVaSexp== null?  tempSexp.getElementAsSEXP(1).getElementAsSEXP(k).asInt() :
//						 							tempVaSexp.getElementAsSEXP(1).getElementAsSEXP(k).asInt();
				
				matNum  =  tempSexp.getElementAsSEXP(1).getElementAsSEXP(k).asInt() ;		
				
				
						
				tempSce = new IrShockSce();
				tempSce.setBaseDate(bssd);
				tempSce.setIrCurveId(irCurveId);
				
				//tempSce.setIrModelId("AFNS");				
				tempSce.setIrModelId(mode);
//				if(j>=4 && j<10) tempSce.setIrModelId("DNS_A");
//				else   			 tempSce.setIrModelId("DNS_L");
				
				tempSce.setSceNo(sceName);
				tempSce.setMatCd("M"+ String.format("%04d", matNum));
				
				tempSce.setRfRate(   Double.isNaN(rfRate)?0.0: rfRate);
				tempSce.setRiskAdjIr(Double.isNaN(riskAdjRate)?0.0: riskAdjRate);
				
				tempSce.setLastModifiedBy("ESG");
				tempSce.setLastUpdateDate(LocalDateTime.now());
				
				irScenarioList.add(tempSce);
				//logger.info("{}", tempSce);
			}
		}
		return irScenarioList;
	}
	
	
	private SEXP getModelResult(double errorTolerance, double volAdjust) {
		System.setProperty("com.github.fommil.netlib.BLAS", "com.github.fommil.netlib.F2jBLAS");
		System.setProperty("com.github.fommil.netlib.LAPACK", "com.github.fommil.netlib.F2jLAPACK");
		
		List<String> scriptString = ScriptUtil.getScriptContents();		
		
		RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();
		ScriptEngine engine = factory.getScriptEngine();
		
		try {
			for (String aa : scriptString) {
				engine.eval(aa);
			}
			engine.put("bseDt", FinUtils.toEndOfMonth(baseYymm));
			engine.put("mode", mode);	
			engine.put("int", getIntRateBuilder().build());
			engine.put("intType", "CONT");   // DISC CONT
			engine.put("mat", EBaseMatCd.yearFracs());
			engine.put("dt", 1.0/52);
			engine.put("ltfr.l", ufr);
			engine.put("ltfr.a", ufr);
			engine.put("ltfr.t", ufrt);			
			engine.put("accuracy", errorTolerance);
			engine.put("volAdj", volAdjust);
			
			
//			logger.info("{}", EBaseMatCd.yearFracs());
//			logger.info("{},{},{},{}", engine.get("bseDt"), engine.get("mode"));	
//			logger.info("{},{},{},{}", engine.get("intType"), engine.get("mat"),engine.get("dt"));
//			logger.info("{},{},{},{}", engine.get("ltfr.l"), engine.get("ltfr.a"),engine.get("ltfr.t"));
//			logger.info("{},{},{},{}", engine.get("accuracy"), engine.get("volAdj"));
//			logger.info("{},{},{},{}", engine.get("int"));
			
			
			
			//String script = "Dns.run(int, mat, ufr, ufrt, int.type= intType, bse.dt=bseDt, accuracy= accuracy, VA=volAdj)";
			
//			Dns.run <- function(int.full, obs.mat, ufr, ufr.t, int.type = "cont", 
//          obs.term = 1/52 , VA = 0.0032,
//          max.lambda = 2, min.lambda = 0.05,
//          accuracy = 1e-10, method = "Nelder-Mead",
//          llp = max(obs.mat), conf.interval = 0.995,bse.dt )

			
			String script = "AFNS.run(base.date=bseDt, mode=mode, int.data=int, int.type=intType, obs.mat=mat, dt=dt, "
					      + "ltfr.l=ltfr.l, ltfr.a=ltfr.a, ltfr.t=ltfr.t, VA=volAdj, acc.itr.kalman=accuracy)";			

			
//			AFNS.run <- function(base.date, mode = "AFNS", input.paras = NULL, int.data, int.type = "DISC", obs.mat, dt = 1/12,
//                    ltfr.l = 0.052, ltfr.a = NA, ltfr.t = 60, VA = 0.00468, term = 1/12, min.lambda = 0.05, max.lambda = 2, nf = 3,
//                    prj.year = 140, acc.itr.kalman = 1.0e-10, mat.itr.kalman = 100, conf.interval = 0.995)			
			
			return (SEXP) engine.eval(script);
			
		} catch (Exception e) {
			logger.error("Renjin Error : {}", e);
		}
		return null;
	}
	
	private ListVector.NamedBuilder getIntRateBuilder(){
		
		ListVector.NamedBuilder dfProc = new ListVector.NamedBuilder();
		dfProc.setAttribute(Symbols.CLASS, StringVector.valueOf("data.frame"));
		dfProc.setAttribute(Symbols.ROW_NAMES, new RowNamesVector(curveMap.get("M0003").size()));

		StringVector.Builder baseDateBuilder = new StringVector.Builder();
		DoubleArrayVector.Builder valBuilder = new DoubleArrayVector.Builder();
		
		List<IrCurveHis> tempList;		

		for(EBaseMatCd aa : EBaseMatCd.values()) {
			if(curveMap.containsKey(aa.name())) {
				valBuilder = new DoubleArrayVector.Builder();
				tempList = curveMap.get(aa.name());
				tempList.sort(new IrCurveHisComparator());
				
				for(IrCurveHis bb : tempList) {
//					if(aa.name().equals("M0003")) {
//						baseDateBuilder.add(bb.getBaseDate());
//					}
					valBuilder.add(bb.getIntRate());
				}
				if(aa.name().equals("M0003")) {
//					dfProc.add("BASE_DATE", baseDateBuilder.build());
				}
				dfProc.add(aa.name(), valBuilder.build());
			}
		}
				
		return dfProc;
	}
}
