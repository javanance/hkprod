package com.gof.process;

import java.util.ArrayList;
import java.util.List;

import com.gof.dao.IrCurveHisDao;
import com.gof.entity.IrCurveHis;
import com.gof.entity.IrShockSce;
import com.gof.model.AFNelsonSiegel;
import com.gof.util.EsgConstant;
import com.gof.util.FinUtils;
import com.gof.model.Irmodel;

import lombok.extern.slf4j.Slf4j;

/**
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job29_AfnsScenarioOld {
	public static List<IrShockSce> createAfnsShockScenario(String bssd, String curCd, String irCurveId, int batchNo, double errorTolerance, double volAdjust) {
		List<IrShockSce> irScenarioList = new ArrayList<IrShockSce>();
//		IrShockSce tempSce;

//		List<String> tenorList = EsgConstant.getTenorList();
		
//		Map<String, List<IrCurveHis>> curveMap = IrCurveHisDao.getIrCurveListTermStructure(bssd, FinUtils.addMonth(bssd, -12), irCurveId);
		List<IrCurveHis> curveHisList  = IrCurveHisDao.getIrCurveListTermStructureForShock(bssd, FinUtils.addMonth(bssd, -100), irCurveId);
		List<IrCurveHis> curveBaseList = IrCurveHisDao.getIrCurveHis(bssd, irCurveId);		
		
		if(curveHisList.size()==0) {
			log.warn("IR Curve History of {} Data is not found at {}", irCurveId, bssd);
			return irScenarioList;
		}
		
		if(curveBaseList.size()==0) {
			log.warn("Base IR Curve  of {} Data is not found at {}", irCurveId, bssd);
			return irScenarioList;
		}		
		
//		double ufr  = EsgConstant.getSmParam().get(curCd).getUfr();		
//		int ufrt = EsgConstant.getSmParam().get(curCd).getUfrT().intValue();
		
		double ufr = 0.045;
		int ufrt = 60;
		String mode = "DNS";		

		AFNelsonSiegel afns = new AFNelsonSiegel(Irmodel.stringToDate(bssd), mode, curveHisList, curveBaseList, false, Irmodel.CMPD_MTD_CONT, 
				                                 1.0 / Irmodel.WEEK_IN_YEAR, 0.05, ufr, ufr, ufrt, volAdjust, 140);  //errorTolerance

		irScenarioList.addAll(afns.getAfnsResultList());		
		log.info("Job19(Arbitrage Free Nelson Siegel Scenario Calculation) creates  {} results.  They are inserted into EAS_IR_SHOCK_SCE Table", irScenarioList.size());
//		irScenarioList.stream().filter(s -> s.getSceNo().equals("1")).filter(s -> Integer.valueOf(s.getMatCd().substring(1, 5)) <= 12).forEach(s->log.warn("Arbitrage Free Nelson Siegle Scenario Result : {}", s.toString()));		
		
		return irScenarioList;
	}
	
	
//	public class Job19_AfnsScenario {
//		
//		//public static List<IrShockSce> createAfnsShockScenario(String bssd, String curCd, String irCurveId, int batchNo, double errorTolerance, double volAdjust) {	
//		
//		public static Map<String, List<?>> createAfnsShockScenario(String bssd, String curCd, String irCurveId, int batchNo, double errorTolerance, double volAdjust) {
//			
//			Map<String, List<?>> irShockSenario  = new TreeMap<String, List<?>>();
//			List<IrShockParam>    irShockParam   = new ArrayList<IrShockParam>();
//			List<IrShock>         irShock        = new ArrayList<IrShock>();		
//			List<IrShockSce>      irScenarioList = new ArrayList<IrShockSce>();
////			List<String> tenorList = EsgConstant.getTenorList();
//			
////			Map<String, List<IrCurveHis>> curveMap = IrCurveHisDao.getIrCurveListTermStructure(bssd, FinUtils.addMonth(bssd, -12), irCurveId);
//			List<IrCurveHis> curveHisList  = IrCurveHisDao.getIrCurveListTermStructureForShock(bssd, FinUtils.addMonth(bssd, -100), irCurveId);
//			List<IrCurveHis> curveBaseList = IrCurveHisDao.getIrCurveHis(bssd, irCurveId);		
//			
//			if(curveHisList.size()==0) {
//				log.warn("IR Curve History of {} Data is not found at {}", irCurveId, bssd);
//				return null;
//			}
//			
//			if(curveBaseList.size()==0) {
//				log.warn("Base IR Curve  of {} Data is not found at {}", irCurveId, bssd);
//				return null;
//			}		
//			
//			double ufr  = EsgConstant.getSmParam().get(curCd).getUfr();		
//			int ufrt = EsgConstant.getSmParam().get(curCd).getUfrT().intValue();
//			
//			ufr = 0.045;
//			ufrt = 60;
//			String mode = "DNS";		
//
//			AFNelsonSiegel afns = new AFNelsonSiegel(Irmodel.stringToDate(bssd), mode, curveHisList, curveBaseList, false, Irmodel.CMPD_MTD_CONT, 
//					                                 1.0 / Irmodel.WEEK_IN_YEAR, 0.05, ufr, ufr, ufrt, volAdjust, 140);  //errorTolerance
//
//			irScenarioList.addAll(afns.getAfnsResultList());
//			irShockParam.  addAll(afns.getAfnsParamList());
//			irShock.       addAll(afns.getAfnsShockList());
//			
//			log.info("Job19(Arbitrage Free Nelson Siegel Scenario Calculation) creates  {} results.  They are inserted into EAS_IR_SHOCK_SCE Table", irScenarioList.size());
////			irScenarioList.stream().filter(s -> s.getSceNo().equals("1")).filter(s -> Integer.valueOf(s.getMatCd().substring(1, 5)) <= 12).forEach(s->log.warn("Arbitrage Free Nelson Siegle Scenario Result : {}", s.toString()));
//			
//			irShockSenario.put("CURVE",  irScenarioList);
//			irShockSenario.put("PARAM",  irShockParam);
//			irShockSenario.put("SHOCK",  irShock);		
//			
//			return irShockSenario;
//		}
//	}
	
}
