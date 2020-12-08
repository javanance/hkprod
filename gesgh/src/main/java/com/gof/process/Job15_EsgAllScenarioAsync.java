package com.gof.process;

import java.util.ArrayList;
import java.util.List;

import com.gof.entity.BizEsgParam;
import com.gof.entity.EsgMst;
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
public class Job15_EsgAllScenarioAsync {
	public static List<IrSce> createEsgScenarioAsync(String bssd,  String irCurveId, List<IrCurveHis> irCurveHisList, List<BizEsgParam> esgParam, EsgMst esgMst, double ufr, double ufrt,  double shortRate, int batchNo) {
		
		log.info("IR Scenario for {} Batch No and Thread Name: {}, {}", irCurveHisList.get(0).getIrCurveId(),batchNo, Thread.currentThread().getName());
		List<IrSce> irScenarioList = new ArrayList<IrSce>();
		String baseYymm = bssd + String.format("%02d", batchNo);  
		
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
}
