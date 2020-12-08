package com.gof.process;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.renjin.sexp.SEXP;

import com.gof.dao.IrCurveHisDao;
import com.gof.entity.IrCurveHis;
import com.gof.entity.ParamCalcHis;
import com.gof.entity.SmithWilsonResult;
import com.gof.entity.SwaptionVol;
import com.gof.model.CIRParameter;
import com.gof.model.HullWhite2FactorParameter;
import com.gof.model.HullWhiteParameter;
import com.gof.model.LiquidPremiumModel;
import com.gof.model.SmithWilsonModel;
import com.gof.model.SmithWilsonModelCore;
import com.gof.model.VasicekParameter;
import com.gof.util.EsgConstant;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> �ݸ������� ������ ���ȸ�Ͱ��, �������� �����ϴ� �۾��� �����ϴ� Ŭ����
 *  <p> IFRS 17 ������ �Ű����� �������� ��ü������ ���õ��� ������ KICS ������ �Ű����� ���� ����� ����ϰ� ����.         
 *  <p> �ݸ� �����Ͱ� �����ϴ� ���� ������ ���ؼ��� �ݸ� ������ �̿��Ͽ� ������ ����� �����ϸ�   
 *  <p> ���� ���⿡ ���ؼ��� ���� ������ ���� ����� �̵� ����Ͽ� ������. 
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job11_EsgParameter {
	public static List<ParamCalcHis> createHwParamCalcHisAsync(String bssd, List<IrCurveHis> curveRst , List<SwaptionVol> volRst, double ufr, double ufrt, double errorTolerance) {
		log.info("ESG Parameter for HW :  Thread Name: {}", Thread.currentThread().getName());
		List<ParamCalcHis> rst ;
		HullWhiteParameter hullWhiteParameter = new HullWhiteParameter(curveRst, volRst, ufr, ufrt);
		rst = hullWhiteParameter.getParamCalcHis(bssd, "4", errorTolerance);
		
		log.info("Job11 (Historical Hull White Parameter) creates {} results. They are inserted into EAS_PARAM_CALC_HIS Table", rst.size());
		
		return rst;
	}

	public static List<ParamCalcHis> createHwKicsParamCalcHisAsync(String bssd, List<IrCurveHis> curveRst, List<SwaptionVol> volRst, double ufr, double ufrt, double errorTolerance) {
		log.info("ESG Parameter for Hw Kics :  Thread Name: {}", Thread.currentThread().getName());
		List<ParamCalcHis> rst ;
				
		HullWhiteParameter hullWhiteParameter = new HullWhiteParameter(curveRst, volRst, ufr, ufrt);
		rst = hullWhiteParameter.getKicsParamCalcHis(bssd, "4", errorTolerance);
		
		log.info("Job11 (Historical Hull White Parameter for Kics) creates {} results. They are inserted into EAS_PARAM_CALC_HIS Table", rst.size());
		
		return rst;
	}

	public static List<ParamCalcHis> createCirParamCalcHisAsync(String bssd, List<IrCurveHis> currentTermStructure, double ufr, double ufrt, double errorTolerance) {
		log.info("ESG Parameter for CIR :  Thread Name: {}", Thread.currentThread().getName());
		List<ParamCalcHis> rst ;
		List<IrCurveHis> curveRst= new ArrayList<IrCurveHis>();
		
		if(currentTermStructure.size() == 0) {
			log.error("No IrCurve His Data for CIR parma ");
			System.exit(0);
		}
		
		CIRParameter cirParameter = new CIRParameter(currentTermStructure);
		rst = cirParameter.getParamCalcHis(bssd, "5", errorTolerance);
		
		log.info("Job11 (Historical CIR Parameter) creates {} results. They are inserted into EAS_PARAM_CALC_HIS Table", rst.size());
		return rst;
	}
	
	public static List<ParamCalcHis> createVasicekParamCalcHisAsync(String bssd, List<IrCurveHis> currentTermStructure, double ufr, double ufrt, double errorTolerance) {
		log.info("ESG Parameter for Vasicek :  Thread Name: {}", Thread.currentThread().getName());
		
		List<ParamCalcHis> rst ;
		if(currentTermStructure.size() == 0) {
			log.info("No IrCurve His Data for vasicek Param");
			System.exit(0);
		}
		
		VasicekParameter vasicekParameter = new VasicekParameter(currentTermStructure);
		rst = vasicekParameter.getParamCalcHis(bssd, "2", errorTolerance);
		
		log.info("Job11 (Historical Vasicek Parameter) creates {} results. They are inserted into EAS_PARAM_CALC_HIS Table", rst.size());
		
		return rst;
	}

	public static List<ParamCalcHis> createHw2FactorParamCalcHisAsync(String bssd, List<IrCurveHis> curveRst, List<SwaptionVol> volRst, double ufr, double ufrt, double errorTolerance) {
		log.info("ESG Parameter for HW2 :  Thread Name: {}", Thread.currentThread().getName());
		List<ParamCalcHis> rst ;
		
		HullWhite2FactorParameter hw2FactorParameter = new HullWhite2FactorParameter(curveRst, volRst, ufr, ufrt);
		rst = hw2FactorParameter.getParamCalcHis(bssd, "6", errorTolerance); 			// 6 : HW2 Model
		
		log.info("Job11 (Historical HW 2 Factor Parameter) creates {} resutls. They are inserted into EAS_PARAM_CALC_HIS Table", rst.size());
		
		return rst;
	}

	private static List<ParamCalcHis> createVasicekParamCalcHisAsyncSw(String bssd, List<IrCurveHis> currentTermStructure, double ufr, double ufrt, double errorTolerance) {
		log.info("ESG Parameter for Vasicek :  Thread Name: {}", Thread.currentThread().getName());
		
		List<ParamCalcHis> rst ;
		List<IrCurveHis> curveRst= new ArrayList<IrCurveHis>();
		if(currentTermStructure.size() == 0) {
			log.info("IrCurve His Data : {}" );
			System.exit(0);
		}
		
		SmithWilsonModel rf = new SmithWilsonModel(currentTermStructure, ufr, ufrt);
		SEXP rfRst         = rf.getSmithWilsonSEXP(false).getElementAsSEXP(0);			// Spot:  [Time , Month_Seq, spot, spot_annu, df, fwd, fwd_annu] , Forward Matrix:
		IrCurveHis temp;
		
		for(int i =0; i< 1200; i++) {
			temp = new IrCurveHis();
			temp.setBaseDate(bssd);
//			temp.setIrCurveId("A100");
			temp.setMatCd("M" + String.format("%04d", i+1));
			temp.setIntRate(rfRst.getElementAsSEXP(6).getElementAsSEXP(i).asReal());
			curveRst.add(temp);
			
		}
		
		VasicekParameter vasicekParameter = new VasicekParameter(curveRst);
		rst = vasicekParameter.getParamCalcHis(bssd, "2", errorTolerance);
		
		log.info("Job11 (Historical Vasicek Parameter) creates {} results. They are inserted into EAS_PARAM_CALC_HIS Table", rst.size());
		
		return rst;
	}
	
	private static List<ParamCalcHis> createVasicekParamCalcHisAsync1(String bssd, List<IrCurveHis> shortRateTimeSeries, double ufr, double ufrt, double errorTolerance) {
		log.info("ESG Parameter for Vasicek :  Thread Name: {}", Thread.currentThread().getName());
		
		List<ParamCalcHis> rst ;
	
		VasicekParameter vasicekParameter = new VasicekParameter(shortRateTimeSeries);
		rst = vasicekParameter.getParamCalcHis(bssd, "2", errorTolerance);
		
		log.info("Job11 (Historical Vasicek Parameter) creates {} results. They are inserted into EAS_PARAM_CALC_HIS Table", rst.size());
		
		return rst;
	}

	private static List<ParamCalcHis> createCirParamCalcHisAsyncSw(String bssd, List<IrCurveHis> currentTermStructure, double ufr, double ufrt, double errorTolerance) {
		log.info("ESG Parameter for CIR :  Thread Name: {}", Thread.currentThread().getName());
		List<ParamCalcHis> rst ;
		List<IrCurveHis> curveRst= new ArrayList<IrCurveHis>();
		
		SmithWilsonModel rf = new SmithWilsonModel(currentTermStructure, ufr, ufrt);
		SEXP rfRst         = rf.getSmithWilsonSEXP(false).getElementAsSEXP(0);			// Spot:  [Time , Month_Seq, spot, spot_annu, df, fwd, fwd_annu] , Forward Matrix:
		IrCurveHis temp;
		
		for(int i =0; i< 1200; i++) {
			temp = new IrCurveHis();
			temp.setBaseDate(bssd);
			temp.setIrCurveId("A100");
			temp.setMatCd("M" + String.format("%04d", i+1));
			temp.setIntRate(rfRst.getElementAsSEXP(6).getElementAsSEXP(i).asReal());
			curveRst.add(temp);
		}
		
		CIRParameter cirParameter = new CIRParameter(curveRst);
		rst = cirParameter.getParamCalcHis(bssd, "5", errorTolerance);
		
		log.info("Job11 (Historical CIR Parameter) creates {} results. They are inserted into EAS_PARAM_CALC_HIS Table", rst.size());
		return rst;
	}

}