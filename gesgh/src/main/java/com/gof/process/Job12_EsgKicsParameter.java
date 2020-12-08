package com.gof.process;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.dao.IrCurveHisDao;
import com.gof.dao.SwaptionVolDao;
import com.gof.entity.IrCurveHis;
import com.gof.entity.ParamCalcHis;
import com.gof.entity.SwaptionVol;
import com.gof.model.HullWhiteParameter;
import com.gof.util.EsgConstant;

/**
 *  <p> �ݸ������� ������ ���ȸ�Ͱ��, �������� �����ϴ� �۾��� �����ϴ� Ŭ����
 *  <p> IFRS 17 ������ �Ű����� �������� ��ü������ ���õ��� ������ KICS ������ �Ű����� ���� ����� ����ϰ� ����.         
 *  <p> �ݸ� �����Ͱ� �����ϴ� ���� ������ ���ؼ��� �ݸ� ������ �̿��Ͽ� ������ ����� �����ϸ�   
 *  <p> ���� ���⿡ ���ؼ��� ���� ������ ���� ����� �̵� ����Ͽ� ������. 
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
public class Job12_EsgKicsParameter {
	
	private final static Logger logger = LoggerFactory.getLogger(Job12_EsgKicsParameter.class);
	
	/**
	 *  ���س���� �ݸ� �����͸� �ݿ��Ͽ� ������ �Ű�����
	 *  @param bssd ���س��
	 *  @return  ���� �Ű����� 
	*/
	public static List<ParamCalcHis> createHwParamCalcHis(String bssd, double errorTolerance) {
		List<ParamCalcHis> rst ;
//		List<IrCurveHis> curveRst = IrCurveHisDao.getKTBIrCurveHis(bssd);
		List<IrCurveHis> curveRst = IrCurveHisDao.getIrCurveHis(bssd, EsgConstant.getStrConstant().get("ESG_RF_KRW_ID"));
		List<SwaptionVol> volRst = SwaptionVolDao.getSwaptionVol(bssd);
		
		logger.info("Swpation vol Size : {},{}", volRst.size());
		
//		List<SmithWilsonParam> swParam = DaoUtil.getEntities(SmithWilsonParam.class, new HashMap<String, Object>());
//		List<SmithWilsonParam> swParamList = swParam.stream().filter(s->s.getCurCd().equals("KRW")).collect(Collectors.toList());
//		double ufr = swParamList.isEmpty()? 0.045: swParamList.get(0).getUfr();
//		double ufrt = swParamList.isEmpty()? 60: swParamList.get(0).getUfrT();

		double ufr = EsgConstant.getSmParam().get("KRW").getUfr();
		double ufrt = EsgConstant.getSmParam().get("KRW").getUfrT();
		
		
		HullWhiteParameter hullWhiteParameter = new HullWhiteParameter(curveRst, volRst, ufr, ufrt);
		rst = hullWhiteParameter.getKicsParamCalcHis(bssd, "4", errorTolerance);
		
		logger.info("Job11 (Historical Hull White Parameter for Kics) creates {} results. They are inserted into EAS_PARAM_CALC_HIS Table", rst.size());
		
		return rst;
	}
	
	public static List<ParamCalcHis> createHwParamCalcHisAsync(String bssd, List<IrCurveHis> curveRst, List<SwaptionVol> volRst, double ufr, double ufrt, double errorTolerance) {
		logger.info("ESG Parameter for Hw Kics :  Thread Name: {}", Thread.currentThread().getName());
		List<ParamCalcHis> rst ;
				
		HullWhiteParameter hullWhiteParameter = new HullWhiteParameter(curveRst, volRst, ufr, ufrt);
		rst = hullWhiteParameter.getKicsParamCalcHis(bssd, "4", errorTolerance);
		
		logger.info("Job11 (Historical Hull White Parameter for Kics) creates {} results. They are inserted into EAS_PARAM_CALC_HIS Table", rst.size());
		
		return rst;
	}
}