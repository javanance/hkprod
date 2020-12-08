package com.gof.process;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.gof.dao.IrCurveHisDao;
import com.gof.entity.IrCurveHis;
import com.gof.entity.IrShock;
import com.gof.entity.IrShockParam;
import com.gof.entity.IrShockSce;
import com.gof.model.AFNelsonSiegel;
import com.gof.model.Irmodel;

import lombok.extern.slf4j.Slf4j;

/**
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job29_AfnsScenario {	
	
	public static Map<String, List<?>> createAfnsShockScenario(String bssd, String mode, String irCurveId, String baseCurveId, List<String> tenorList, 
			                                                   String stBssd, boolean isRealNumber, char cmpdMtd, double dt, double initSigma, int dayCountBasis,
			                                                   double ltfrL, double ltfrA, int ltfrT, double liqPrem, double minLambda, double maxLambda, 
			                                                   int prjYear, double errorTolerance, int itrMax, double confInterval, double epsilon) 
	{	
		Map<String, List<?>> irShockSenario  = new TreeMap<String, List<?>>();
		List<IrShockParam>    irShockParam   = new ArrayList<IrShockParam>();
		List<IrShock>         irShock        = new ArrayList<IrShock>();		
		List<IrShockSce>      irScenarioList = new ArrayList<IrShockSce>();		

		List<IrCurveHis> curveHisList  = IrCurveHisDao.getIrCurveListTermStructureForShock(bssd, stBssd, irCurveId, tenorList);
		List<IrCurveHis> curveBaseList = IrCurveHisDao.getIrCurveHis(bssd, baseCurveId, tenorList);
		
		if(curveHisList.size()==0) {
			log.warn("IR Curve History of {} Data is not found at from {} to {}", irCurveId, stBssd, bssd);
			return null;
		}
		
		if(curveBaseList.size()==0) {
			log.warn("Base IR Curve  of {} Data is not found at {}", irCurveId, bssd);
			return null;
		}
		
		
		AFNelsonSiegel afns = new AFNelsonSiegel(Irmodel.stringToDate(bssd), mode, null, curveHisList, curveBaseList,
				                                 isRealNumber, cmpdMtd, dt, initSigma, dayCountBasis, ltfrL, ltfrA, ltfrT, liqPrem, 1.0 / 12, 
				                                 minLambda, maxLambda, 3, prjYear, errorTolerance, itrMax, confInterval, epsilon);

		irScenarioList.addAll(afns.getAfnsResultList());
		irShockParam.  addAll(afns.getAfnsParamList());
		irShock.       addAll(afns.getAfnsShockList());
		
		log.info("Job19(Arbitrage Free Nelson Siegel Scenario Calculation) creates  {} results.  They are inserted into EAS_IR_SHOCK_SCE Table", irScenarioList.size());
//		irScenarioList.stream().filter(s -> s.getSceNo().equals("1")).filter(s -> Integer.valueOf(s.getMatCd().substring(1, 5)) <= 12).forEach(s->log.warn("Arbitrage Free Nelson Siegle Scenario Result : {}", s.toString()));
		
		irShockSenario.put("CURVE",  irScenarioList);
		irShockSenario.put("PARAM",  irShockParam);
		irShockSenario.put("SHOCK",  irShock);		
		
		return irShockSenario;
	}
}
