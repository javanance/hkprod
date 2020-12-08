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
 *  <p> Dynamic Nelson Siegel ����	</p>        
 *  <p> R Script �� �����ϱ� ����  Input Data ���� �� ���� , R Script ����, Output Converting �۾��� ������.</p>
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
public class DynamicNelsonSiegel {
	private final static Logger logger = LoggerFactory.getLogger("DNS");
	private String baseYymm;
//	private double[] matOfYear;
	private double ufr;
	private double ufrt;
	private Map<String, List<IrCurveHis>> curveMap;
	
	public DynamicNelsonSiegel() {
	}

	public DynamicNelsonSiegel(String baseDate, Map<String, List<IrCurveHis>> curveMap,  double ufr, double ufrt) {
		this.baseYymm = baseDate;
		this.curveMap = curveMap;
		this.ufr = ufr;
		this.ufrt = ufrt;
	}
	
	public List<IrShockSce> getDnsScenario(String bssd, String irCurveId, double errorTolerance, double volAdjust){
		List<IrShockSce> irScenarioList = new ArrayList<IrShockSce>();
		IrShockSce tempSce;
		double rfRate =0.0;
		double riskAdjRate =0.0;
		int matNum ;
		
		SEXP dnsRst = getModelResult(errorTolerance, volAdjust);
		logger.debug("Dns :{}, {},{}", curveMap.size(), dnsRst.length(), dnsRst.getElementAsSEXP(0));
		
		for(int j=6; j < dnsRst.length(); j+=2) {
			String sceName = dnsRst.getName(j);
			
			SEXP tempSexp = dnsRst.getElementAsSEXP(j);
			SEXP tempVaSexp = dnsRst.getElementAsSEXP(j+1);
				
			for(int k =0; k< tempSexp.getElementAsSEXP(0).length(); k++) {
//				TODO: un comment!!! test!!!
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
				
				tempSce.setIrModelId("DNS");
				tempSce.setSceNo(sceName);
				tempSce.setMatCd("M"+ String.format("%04d", matNum));
				
				tempSce.setRfRate(Double.isNaN(rfRate)?0.0: rfRate);
				tempSce.setRiskAdjIr(Double.isNaN(riskAdjRate)?0.0: riskAdjRate);
				
				tempSce.setLastModifiedBy("ESG");
				tempSce.setLastUpdateDate(LocalDateTime.now());
				
				irScenarioList.add(tempSce);
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
			engine.put("int", getIntRateBuilder().build());
			engine.put("mat", EBaseMatCd.yearFracs());
			engine.put("ufr", ufr);
			engine.put("ufrt", ufrt);
			engine.put("bseDt", FinUtils.toEndOfMonth(baseYymm));
			engine.put("intType", "annu");
			engine.put("accuracy", errorTolerance);
			engine.put("volAdj", volAdjust);
			
			String script = "Dns.run(int, mat, ufr, ufrt, int.type= intType, bse.dt=bseDt, accuracy= accuracy, VA=volAdj)";
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
					if(aa.name().equals("M0003")) {
						baseDateBuilder.add(bb.getBaseDate());
					}
					valBuilder.add(bb.getIntRate());
				}
				if(aa.name().equals("M0003")) {
					dfProc.add("BASE_DATE", baseDateBuilder.build());
				}
				dfProc.add(aa.name(), valBuilder.build());
			}
		}
		
		return dfProc;
	}
}
