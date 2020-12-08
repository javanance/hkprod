package com.gof.process;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gof.entity.BizEsgParam;
import com.gof.entity.BizIrCurveSce;
import com.gof.entity.BottomupDcnt;
import com.gof.entity.EsgMst;
import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveHis;
import com.gof.entity.IrSce;
import com.gof.enums.EIrModelType;
import com.gof.model.CIR;
import com.gof.model.HullWhite;
import com.gof.model.HullWhite2Factor;
import com.gof.model.Vasicek;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> ESG ������ �ݸ�/���庯�� �ó������� �����۾� ������.       
 *  <p> Hull White 1Factor/ 2Factor ����, CIR, Vasicek �� �Ϲ����� �ݸ� �ó����� ������ �����ϰ� ����.
 *  <p> �ֽ� ���� ���庯���� ���ؼ��� �⺻���� Brownian ����, Black ���� ������ �ó������� ������.
 *  <p> �ֽ� ���� �Ϲݽ��庯���� ��� ������ ������ Random Number �� ESG ������ �Է��Ͽ� �ó������� �����ϴ� 2�ܰԷ� ������
 *  <p> �ݸ� �ó������� �ݸ� �Ⱓ ���� Fitting �ܰ谡 �߰���.  
 *  <p>  1. �ݸ� ������ ��� ���� 
 *  <p>  2. �ݸ����� ,  �Ű����� ,  Random Number �� �������� Short Rate �� �ó����� ����
 *  <p>  3. �ݸ� �Ⱓ ���� Fitting �������� Bucket �� �ݸ� ����� ��ü �ó����� ���� 
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job40_EsgScenarioAsync {
	public static List<IrSce> createEsgScenarioAsync(String bssd,  String irCurveId, List<IrCurveHis> irCurveHisList, List<BizEsgParam> esgParam, EsgMst esgMst, double ufr, double ufrt,  double shortRate, int batchNo) {
		log.info("IR Scenario for {} Batch No and Thread Name: {}, {}", irCurveHisList.get(0).getIrCurveId(),batchNo, Thread.currentThread().getName());
		List<IrSce> irScenarioList = new ArrayList<IrSce>();
		String baseYymm = bssd + String.format("%02d", batchNo);   //�������ڷ� �õ� �ѹ��� �����ϰ� ����. batchNo �� ���� �ٸ� �õ带 �ֱ����� ��¥�� ������.
		
			switch (EIrModelType.getEIrModelType(esgMst.getIrModelTyp())) {
				case MERTON :
						break;
					
				case VASICEK:	
						Vasicek vasicek = new Vasicek(baseYymm, shortRate, esgParam);
						irScenarioList.addAll(vasicek.getVasicekScenario(bssd, irCurveId, esgMst.getIrModelId(), 100, batchNo));
						break;
						
				case HOLEE:		
						
						break;
					
				case HULLWHITE:	
						
						HullWhite hullWhite = new HullWhite(baseYymm, irCurveHisList, esgParam, ufr, ufrt);
//						irScenarioList.addAll(hullWhite.getHullWhiteScenario(bssd, irCurveId, esgMst.getIrModelId(), 100, batchNo));
						return  hullWhite.getHullWhiteScenario(bssd, irCurveId, esgMst.getIrModelId(), 100, batchNo);
//						break;
						
				case CIR:		
						CIR cir = new CIR(baseYymm, shortRate, esgParam);
						irScenarioList.addAll(cir.getCirScenario(bssd, irCurveId, esgMst.getIrModelId(), 100, batchNo));
						break;
						
				case HW2:		 
						HullWhite2Factor hw2 = new HullWhite2Factor(baseYymm, irCurveHisList, esgParam, ufr, ufrt);
						irScenarioList.addAll(hw2.getHW2Scenario(bssd, irCurveId, esgMst.getIrModelId(), 100, batchNo));
						break;
				default:
						break;
		}
		return irScenarioList;
	}
	
	public static List<BizIrCurveSce> createEsgBizScenarioAsync(String bssd, List<IrCurveHis> irCurveHisList, List<BizEsgParam> esgParam, EsgMst esgMst, double ufr, double ufrt,  double shortRate, int batchNo) {
		log.info("IR Scenario for {} Batch No and Thread Name: {}, {}", irCurveHisList.get(0).getIrCurveId(),batchNo, Thread.currentThread().getName());
		List<BizIrCurveSce> irScenarioList = new ArrayList<BizIrCurveSce>();
		String baseYymm = bssd + String.format("%02d", batchNo);  
		String irCurveId = irCurveHisList.get(0).getIrCurveId();
		
		switch (EIrModelType.getEIrModelType(esgMst.getIrModelTyp())) {
				case MERTON :
						break;
					
				case VASICEK:	
						Vasicek vasicek = new Vasicek(baseYymm, shortRate, esgParam);
						irScenarioList.addAll(vasicek.getVasicekScenario1(bssd, irCurveId, "A", 100, batchNo));
						break;
						
				case HOLEE:		
						break;
				case HULLWHITE:	
						
						HullWhite hullWhite = new HullWhite(baseYymm, irCurveHisList, esgParam, ufr, ufrt);
//						irScenarioList.addAll(hullWhite.getHullWhiteScenario(bssd, irCurveId, esgMst.getIrModelId(), 100, batchNo));
						return  hullWhite.getHullWhiteScenario1(bssd, irCurveId, "A", 100, batchNo);
//						break;
				case CIR:		
						CIR cir = new CIR(baseYymm, shortRate, esgParam);
						irScenarioList.addAll(cir.getCirScenario1(bssd, irCurveId, "A", 100, batchNo));
						break;
				case HW2:		 
						HullWhite2Factor hw2 = new HullWhite2Factor(baseYymm, irCurveHisList, esgParam, ufr, ufrt);
						irScenarioList.addAll(hw2.getHW2Scenario1(bssd, irCurveId, esgMst.getIrModelId(), 100, batchNo));
						break;
				default:
						break;
		}
		return irScenarioList;
	}
	
	
	
	public static List<BizIrCurveSce> createEsgBizScenarioAsync1(String bssd, IrCurve curveMst, List<BottomupDcnt> irCurveHisList, List<BizEsgParam> esgParam, EsgMst esgMst, double ufr, double ufrt,  double shortRate, int batchNo) {
		log.info("IR Scenario for {} Batch No and Thread Name: {}, {}", irCurveHisList.get(0).getIrCurveId(),batchNo, Thread.currentThread().getName());
		List<BizIrCurveSce> irScenarioList = new ArrayList<BizIrCurveSce>();
		String baseYymm = bssd + String.format("%02d", batchNo);  
		String irCurveId = curveMst.getIrCurveId();
		
		Map<String, Double> curveMap = irCurveHisList.stream().collect(toMap(BottomupDcnt::getMatCd, BottomupDcnt::getRiskAdjRfRate));
		
		switch (EIrModelType.getEIrModelType(esgMst.getIrModelTyp())) {
			case MERTON :
					break;
				
			case VASICEK:	
					Vasicek vasicek = new Vasicek(baseYymm, shortRate, esgParam);
					irScenarioList.addAll(vasicek.getVasicekScenario1(bssd, irCurveId, "A", 100, batchNo));
					break;
					
			case HOLEE:		
					
					break;
				
			case HULLWHITE:	
					
					HullWhite hullWhite = new HullWhite(baseYymm, curveMap, esgParam, ufr, ufrt);
//						irScenarioList.addAll(hullWhite.getHullWhiteScenario(bssd, irCurveId, esgMst.getIrModelId(), 100, batchNo));
					return  hullWhite.getHullWhiteScenario1(bssd, irCurveId, curveMst.getApplBizDv(), 100, batchNo);
//						break;
					
			case CIR:		
					CIR cir = new CIR(baseYymm, shortRate, esgParam);
					irScenarioList.addAll(cir.getCirScenario1(bssd, irCurveId, "A", 100, batchNo));
					break;
					
			case HW2:		 
					HullWhite2Factor hw2 = new HullWhite2Factor(baseYymm, curveMst, curveMap, esgParam, ufr, ufrt);
					irScenarioList.addAll(hw2.getHW2Scenario1(bssd, irCurveId, esgMst.getIrModelId(), 100, batchNo));
					break;
					
			default:
					break;
		}
		return irScenarioList;
	}
	
}
