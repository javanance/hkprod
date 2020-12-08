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

import com.gof.entity.BizDiscountRateSce;
import com.gof.entity.BizEsgParam;
import com.gof.entity.BizIrCurveSce;
import com.gof.entity.BizStockSce;
import com.gof.entity.BottomupDcnt;
import com.gof.entity.IrCurveHis;
import com.gof.entity.IrSce;
import com.gof.interfaces.Rrunnable;
import com.gof.util.ScriptUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> Hull White 1 Factor ����	</p>        
 *  <p> R Script �� �����ϱ� ����  Input Data ���� �� ���� , R Script ����, Output Converting �۾��� ������.</p>
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class HullWhiteBondYield implements Rrunnable {

	private String baseYymm;
	private double[] intRate;
	private double[] matOfYear;
	private double ufr;
	private double ufrt;
//	private List<ParamApply> paramHisList = new ArrayList<ParamApply>();
	private List<BizEsgParam> bizParamHisList = new ArrayList<BizEsgParam>();
	
	public HullWhiteBondYield() {
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

	public HullWhiteBondYield(String baseDate, Map<String, Double> curveHisMap, List<BizEsgParam> param,  double ufr, double ufrt) {
		this.baseYymm = baseDate;								//baseYymm �� ���� + bacthNo �� ������ ==> seed Number baseYymm ���� �����ϴµ� batch No �� ���� �ٸ� �õ带 �ֱ� ������.											
		this.intRate = new double[curveHisMap.size()];
		this.matOfYear = new double[curveHisMap.size()];
		log.info("hullwhite : {},{}", curveHisMap.size(), matOfYear.length);
		int idx=0;
		for(Map.Entry<String, Double> entry : curveHisMap.entrySet()) {
			matOfYear[idx] = Double.valueOf(entry.getKey().split("M")[1]) / 12.0;
			intRate[idx] = entry.getValue();
			idx++;
		}
		
		this.ufr = ufr;
		this.ufrt = ufrt;
		this.bizParamHisList = param;
	
	}
	
	public HullWhiteBondYield(String baseDate, List<BottomupDcnt> curveHisList, List<BizEsgParam> param,  double ufr, double ufrt) {
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
	
	public List<BizStockSce> getScenario(String bssd, String bizDv, int sceNum, double duration,  int batchNo){
//		logger.info("Call Hw scenario for {} ,  batchNo : {}, {}, {}" , irCurveId, batchNo, baseYymm, sceNum);
//		logger.info("Call Hw scenario for {} ,  batchNo : {}" , irCurveId, batchNo);
		
		List<BizStockSce> irScenarioList = new ArrayList<BizStockSce>();
		
		int sceNo ;
		SEXP hwRst = getModelResult(sceNum, duration);
		
		for(int k =0; k< hwRst.getElementAsSEXP(0).length(); k++) {
			sceNo = ( batchNo - 1) * sceNum + hwRst.getElementAsSEXP(2).getElementAsSEXP(k).asInt() ;
			irScenarioList.add(BizStockSce.builder()
						.baseYymm(bssd)
						.applBizDv(bizDv)
						.sceNo(String.valueOf(sceNo))
						.matCd("M"+ String.format("%04d", hwRst.getElementAsSEXP(3).getElementAsSEXP(k).asInt()))
						.stdAsstCd(hwRst.getElementAsSEXP(1).getElementAsSEXP(k).asString())
						
						.asstYield( hwRst.getElementAsSEXP(4).getElementAsSEXP(k).asReal() )
						.lastModifiedBy("ESG")
						.lastUpdateDate(LocalDateTime.now())
						.build()
						);
			
//			c("BASE_YM","STD_ASST_CD", "SCEN_NO","MONTH_SEQ","YIELD_RATE","BOND_PRICE")
			
		
		}
		return irScenarioList;
	}
	
	
	/**
	 *  R Script �� �̿��� Hull White 1 Factor Model ����
	 *  @param sceNum		: ������ �ó����� ����
	 *  @return SEXP (Renjin ����� ���� ����� Data Type)  
	*/
	private SEXP getModelResult(int sceNum, double duration) {
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
			engine.put("proj.y" , 101);
			
			engine.put("bse.ym", baseYymm);							
			engine.put("num.of.scen", sceNum * 10);
			engine.put("int.type", "annu");
			engine.put("llp", 20);
			engine.put("duration", duration);
			
			String scriptHW = "Hw1f.bond.yield.simulation( int, mat, params, bse.ym, num.of.scen = num.of.scen, int.type= int.type, ufr=ufr, ufr.t=ufr.t, proj.y=proj.y, llp=llp , duration=duration)";
//			String scriptHW = "Hw1f.simulation.run(bse.ym, int, mat, params, num.of.scen = num.of.scen, int.type= int.type, ufr=ufr, ufr.t=ufr.t)";
			
			SEXP hwRst = (SEXP) engine.eval(scriptHW);
			
			return hwRst;
		} catch (Exception e) {
			log.error("Renjin Error : {}", e);
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
//			logger.info("paramHis : {},{}", aa.getParamTypCd(),Double.valueOf(aa.getMatCd().split("M")[1]) /12 );
			paramTypeBuilder.add(aa.getParamTypCd());
			matYearBuilder.add(Double.valueOf(aa.getMatCd().split("M")[1]));
			valBuilder.add(aa.getApplParamVal());
			
//			if(aa.getParamTypCd().contains("SIGMA")) {
//				valBuilder.add(aa.getApplParamVal()* 0.5);
//			}
//			else {
//				valBuilder.add(aa.getApplParamVal());
//			}
		}
//		logger.info("paramHis : {},{},{}", paramTypeBuilder.length(), matYearBuilder.length(), valBuilder.length());
		
		dfProc.add("PARAM_TYP_CD", paramTypeBuilder.build());
		dfProc.add("MAT_CD", matYearBuilder.build());
		dfProc.add("APPL_PARAM_VAL", valBuilder.build());
		
		return dfProc;
	}
}
