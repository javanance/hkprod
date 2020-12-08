package com.gof.model;

import java.util.ArrayList;
import java.util.HashMap;
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

import com.gof.entity.DiscRateHis;
import com.gof.entity.IrCurveHis;
import com.gof.entity.LinearRegResult;
import com.gof.interfaces.Rrunnable;
import com.gof.util.ScriptUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> ȸ�ͺм�  ����	</p>        
 *  <p> R Script �� �����ϱ� ����  Input Data ���� �� ���� , R Script ����, Output Converting �۾��� ������.</p>
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class DiscRateStatAllModel implements Rrunnable {
	private List<String> matCdList;
	private int[] termSet ;
	private Map<String, List<IrCurveHis>> indepVariableMap = new HashMap<String, List<IrCurveHis>>();
	private List<String> baseYymmList = new ArrayList<String>();
	
//	private Map<String,List<DiscRateHis>> discRateMap = new HashMap<String, List<DiscRateHis>>();
	private List<DiscRateHis> discRateHis = new ArrayList<DiscRateHis>();
	
	public DiscRateStatAllModel() {
		
	}
	
//	public DiscRateStatAllModel(Map<String, List<IrCurveHis>> indepVariable, Map<String, List<DiscRateHis>> discRateHis, int[] termSet, List<String> matCdList) {
//		this.termSet = termSet;
//		this.indepVariableMap = indepVariable;
//		this.discRateMap = discRateHis;
//		this.matCdList = matCdList;
//	}
	
	public DiscRateStatAllModel(Map<String, List<IrCurveHis>> indepVariable, List<DiscRateHis> discRateHis, int[] termSet, List<String> matCdList) {
		this.termSet = termSet;
		this.indepVariableMap = indepVariable;
		this.discRateHis = discRateHis;
		this.matCdList = matCdList;
		
	}
	
	public List<LinearRegResult> getExtirReg() {
		return getRegressionResult(getDependantBuilder("EXT_IR"));
	}
	
	/**
	 *  �����ڿ� �Է��� �ð迭 �����͸� Ȱ���Ͽ� �ڻ�����ͷ��� ����ä���� ȸ�ͺм� ��� ���� 
	 *  
	 *  @return   ȸ�ͺм����
	*/
	public List<LinearRegResult> getAssetYieldReg() {
		return getRegressionResult(getDependantBuilder("ASSET_YIELD"));
	}
	
	/**
	 *  @return   ȸ�ͺм����
	*/
//	public List<LinearRegResult> getBaseDiscRate() {
//		return getRegressionResult(getDependantBuilder("BASE_DISC"));
//		
//	}
	
	public LinearRegResult getBaseDiscRate() {
		return getRegressionResult(getDependantBuilder("BASE_DISC")).get(0);
		
	}
	
	private List<LinearRegResult> getRegressionResult(ListVector.NamedBuilder dependVariable) {
		List<LinearRegResult> rst = new ArrayList<>();
		LinearRegResult temp;
		
		SEXP regRst = getRegression(dependVariable);
		
//		logger.info("Sexp : {}", regRst);
//		logger.info("Sexp1 : {}", regRst.getElementAsSEXP(0).length());
		
		for( int i =0; i< regRst.getElementAsSEXP(0).length(); i++) {
			temp = new LinearRegResult();
			temp.setBaseYymm(     regRst.getElementAsSEXP(0).getElementAsSEXP(i).asString());
			temp.setDepVariable(  regRst.getElementAsSEXP(1).getElementAsSEXP(i).asString());
			temp.setIndepVariable(regRst.getElementAsSEXP(2).getElementAsSEXP(i).asString());
			temp.setAvgMonNum(regRst.getElementAsSEXP(3).getElementAsSEXP(i).asReal());
			
			temp.setRegConstant(regRst.getElementAsSEXP(4).getElementAsSEXP(i).asReal());
			temp.setRegCoef(regRst.getElementAsSEXP(5).getElementAsSEXP(i).asReal());
			temp.setRegRsqr(regRst.getElementAsSEXP(6).getElementAsSEXP(i).asReal());
			
			rst.add(temp);
		}
		return rst;
	}

	public List<String> getBaseYymmList() {
		if(baseYymmList.size()==0) {
			for(DiscRateHis aa : discRateHis) {
				baseYymmList.add(aa.getBaseYymm());
			}
		}
		return baseYymmList;
	}

	public void setBaseYymmList(List<String> baseYymmList) {
		this.baseYymmList = baseYymmList;
	}

	private SEXP getRegression(ListVector.NamedBuilder dependVariable) {
		System.setProperty("com.github.fommil.netlib.BLAS", "com.github.fommil.netlib.F2jBLAS");
		System.setProperty("com.github.fommil.netlib.LAPACK", "com.github.fommil.netlib.F2jLAPACK");

		List<String> scriptString = ScriptUtil.getScriptContents();

				
		RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();
		ScriptEngine engine = factory.getScriptEngine();

		try {
			for (String aa : scriptString) {
				engine.eval(aa);
			}
			engine.put("int.indep", getIndiVariableBuilder().build());
			engine.put("int.dep",   dependVariable.build());
			engine.put("ma.term.set", termSet);
			
			String script = "Gs.int.lm.run(int.indep, int.dep, ma.term.set=ma.term.set)";
			String script1 = "int.dep";
			
//			String script = "Gs.int.lm.run(int.indep, int.dep)";
					
			SEXP swRst = (SEXP) engine.eval(script);
//			SEXP swRst1 = (SEXP) engine.eval(script1);
//			log.info("zzz :  {}", swRst1);

			return swRst;
		} catch (Exception e) {
			log.error("Renjin Error : {}", e);
			System.exit(20);
		}
		return null;
	}
	
	private ListVector.NamedBuilder getIndiVariableBuilder(){
		StringVector.Builder baseDateBuilder   = new StringVector.Builder();
		DoubleArrayVector.Builder ktb3YBuilder = new DoubleArrayVector.Builder();
		DoubleArrayVector.Builder ktb5YBuilder = new DoubleArrayVector.Builder();

		ListVector.NamedBuilder dfProc = new ListVector.NamedBuilder();
		
		dfProc.setAttribute(Symbols.CLASS, StringVector.valueOf("data.frame"));
		dfProc.setAttribute(Symbols.ROW_NAMES, new RowNamesVector(indepVariableMap.size()));
		
		
		int i = 0;
		String firstName =matCdList.get(0);
		String secondtName =matCdList.size() > 1 ? matCdList.get(1) : "NA"  ;
		
		
		double firstRate =0.0;
		double secontRate =0.0;
		
		/*for(String zz : getBaseYymmList()) {
			firstRate =0.0;
			secontRate =0.0;
			for(IrCurveHis aa : indepVariableMap.get(zz)) {
				
				if(matCdList.contains(aa.getMatCd())){
					if(matCdList.get(0).equals(aa.getMatCd())) {
						firstRate = aa.getIntRate();
					}
					else {
						secontRate = aa.getIntRate();
					}
				}
			}
			logger.info("rate  : {},{},{},{}", zz, firstRate, secontRate);
			
			baseDateBuilder.add(zz);
			ktb3YBuilder.add(firstRate);
			ktb5YBuilder.add(secontRate);
		}*/
		
//		log.info("size : {}", indepVariableMap.size());
		
		for(Map.Entry<String, List<IrCurveHis>> entry : indepVariableMap.entrySet()) {
			firstRate =0.0;
			secontRate =0.0;
			for(IrCurveHis aa : entry.getValue()) {
				
				if(matCdList.contains(aa.getMatCd())){
					if(matCdList.get(0).equals(aa.getMatCd())) {
						firstRate = aa.getIntRate();
					}
					else {
						secontRate = aa.getIntRate();
					}
				}
			}
//			logger.info("rate  : {},{},{},{}", entry.getKey(), firstRate, secontRate);
			
			baseDateBuilder.add(entry.getKey());
			ktb3YBuilder.add(firstRate);
			ktb5YBuilder.add(secontRate);
		}
		
		dfProc.add("BASE_YYMM", baseDateBuilder.build());
		dfProc.add(firstName,  ktb3YBuilder.build());
//		dfProc.add(secondtName, ktb5YBuilder.build());
		
		
		
		return dfProc;
	}
	
/*	private ListVector.NamedBuilder getDependantBuilder(boolean isAssetYield){
		StringVector.Builder baseDateBuilder = new StringVector.Builder();
		
		DoubleArrayVector.Builder indiPensonBuilder = new DoubleArrayVector.Builder();

		ListVector.NamedBuilder dfProc = new ListVector.NamedBuilder();
		dfProc.setAttribute(Symbols.CLASS, StringVector.valueOf("data.frame"));
		dfProc.setAttribute(Symbols.ROW_NAMES, new RowNamesVector(discRateMap.size()));
		int i = 0;

		double depVal =0.0;
		String depName ="";
		for (Map.Entry<String, List<DiscRateHis>> entry : discRateMap.entrySet()) {
			depVal =0.0;
			for(DiscRateHis aa : entry.getValue()) {
				if(isAssetYield) {
					depName ="ASSET_YIELD";
					depVal = aa.getMgtAsstYield();
				}
				else {
					depName ="BASE_DISC";
					depVal = aa.getBaseDiscRate();
				}
			}
			
			baseDateBuilder.add(entry.getKey());
			indiPensonBuilder.add(depVal);
		}
		dfProc.add("BASE_YYMM", baseDateBuilder.build());
		dfProc.add(depName, indiPensonBuilder.build());
		
		return dfProc;
	}*/
	
	
	private ListVector.NamedBuilder getDependantBuilder(String dependType){
		StringVector.Builder baseDateBuilder = new StringVector.Builder();
		
		DoubleArrayVector.Builder dependVariBuilder = new DoubleArrayVector.Builder();

		ListVector.NamedBuilder dfProc = new ListVector.NamedBuilder();
		dfProc.setAttribute(Symbols.CLASS, StringVector.valueOf("data.frame"));
		dfProc.setAttribute(Symbols.ROW_NAMES, new RowNamesVector(discRateHis.size()));
		int i = 0;

		double depVal =0.0;
		String depName ="BASE_DISC";
		
//		log.info("size1 : {}", discRateHis.size());
		
		for(DiscRateHis aa : discRateHis) {
			depVal =0.0;
			if(dependType.equals("ASSET_YIELD")) {
				depName =dependType;
//				depVal = aa.getMgtAsstYield() /100.0 ;
				depVal = aa.getMgtAsstYield() ;
			}
			else if(dependType.equals("EXT_IR")) {
				depName =dependType;
//				depVal = aa.getExBaseIr() /100.0;
				depVal = aa.getExBaseIr();
			}
			else {
				depName ="BASE_DISC";
//				depVal = aa.getBaseDiscRate() /100.0;
				depVal = aa.getBaseDiscRate() ;
//				depVal = aa.getApplDiscRate();
			}
			
//			logger.info("zz :{},{}", aa.getBaseYymm(), depVal);
			
			baseDateBuilder.add(aa.getBaseYymm());
			dependVariBuilder.add(depVal);
		}
		dfProc.add("BASE_YYMM", baseDateBuilder.build());
		dfProc.add(depName, dependVariBuilder.build());
		
		return dfProc;
	}
	
	
	
}
