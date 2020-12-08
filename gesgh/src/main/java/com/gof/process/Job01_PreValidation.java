package com.gof.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.dao.EsgMstDao;
import com.gof.dao.IrCurveHisDao;
import com.gof.dao.SwaptionVolDao;
import com.gof.dao.TransitionMatrixDao;
import com.gof.entity.EsgMst;
import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveHis;
import com.gof.entity.SwaptionVol;
import com.gof.entity.TransitionMatrixUd;
import com.gof.enums.EBoolean;

/**
 *  <p> �۾��� �ʿ��� �������� ���� �� ���� ���θ� ������ üũ
 *  <p> ������ �����Ϳ� �� �۾����� �������� ���� ���� üũ          
 *  <p>    
 *  <p>  
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
public class Job01_PreValidation {
	
	private final static Logger logger = LoggerFactory.getLogger(Job01_PreValidation.class);
	
	/**
	 *  ��õ���� �Լ��� �ݸ� �������� ���ռ� ����
	 *  <p>�ݸ� �Ⱓ������ Time Bucket �� �����ϰ� �ԷµǾ����� ����
	 *  <p>Time Bucket �� �ݸ��� ����/������ ������ ���� ���ؿ� �ִ��� Ȯ���� (  0.9~1.1 �� ���� ������ ������. )
	 *  @param bssd ���س��
	*/
	public static void validateIrCurve(String bssd) {
		logger.info("Validation Rule 1 : IrCurve Bucket size & Bucket rate ratio");
		
		List<IrCurve> curveMst = IrCurveHisDao.getIrCurveByCrdGrdCd("000");
		double ratio;
		String curveDate ; 
		for( IrCurve aa : curveMst) {
			curveDate = IrCurveHisDao.getMaxBaseDate(bssd, aa.getIrCurveId());
			
			List<IrCurveHis> currentCurveRst = IrCurveHisDao.getIrCurveHis(bssd, aa.getIrCurveId());
			List<IrCurveHis> beforeCurveRst  = IrCurveHisDao.getIrCurveHis(bssd, aa.getIrCurveId());
			
			
			Map<String, List<IrCurveHis>> currentMap = currentCurveRst.stream().collect(Collectors.groupingBy(s->s.getIrCurveId(), Collectors.toList())); 
			Map<String, List<IrCurveHis>> beforetMap = beforeCurveRst.stream().collect(Collectors.groupingBy(s->s.getIrCurveId(), Collectors.toList()));
			Map<String, IrCurveHis> beforeMaturityMap ; 
			
			
			for(Map.Entry<String, List<IrCurveHis>> entry : currentMap.entrySet()) {
				logger.info("Bucket Size of IrCurve {} at {} : Current Month {}, Previous Month : {}. Two size may be the same. "
											, entry.getKey() , curveDate
											, entry.getValue().size(), beforetMap.getOrDefault(entry.getKey(), new ArrayList<IrCurveHis>()).size());
				
				beforeMaturityMap = beforetMap.getOrDefault(entry.getKey(), new ArrayList<IrCurveHis>())
											  .stream().collect(Collectors.toMap(s->s.getMatCd(), Function.identity()));
				
				for(IrCurveHis bb : entry.getValue()) {
					ratio = beforeMaturityMap.getOrDefault(bb.getMatCd(), new IrCurveHis(bssd, bb.getMatCd(), 0.0)).getIntRate() / bb.getIntRate();
					if(ratio <=0.9 || ratio >= 1.1) {
						logger.warn("The ratio {} before Ir Rate to current Ir rate of maturity {} is too big or too small  ", ratio, bb.getMatCd());
					}
				}
				
				entry.getValue().forEach(s -> logger.debug("Current Curve : {}", s.toString()));
			}
			
		}
	}
	/**
	 *  BottomUp �������� Ref �ݸ��� ���� ����
	 *  <p>BottomUp ��������  Reference �ݸ��� �����Ǿ� ���� ������ ������ �߻���.
	 *  <p>BottomUp ��������  Reference �ݸ��� ���� �����Ͱ� �����ؾ���.
	 *  
	*/
	
	public static void validateBottomUpIrCurve() {
//		public static void validateBottomUpIrCurve(String bssd) {
		logger.info("Validation Rule 2 : Reference Rate for BottomUp Curve");
		
		List<IrCurve> curveMst = IrCurveHisDao.getBottomUpIrCurve();
		if( curveMst.size() ==0) {
			logger.warn("There are no BottomUp Curve Info to generate BottomUp Discount Rate, Look up table EAS_IR_CURVE and check USE_YN column");
		}else {
			curveMst.stream().filter(s -> s.getRefCurveId()==null)
					.forEach(s -> logger.warn("Ref Curve is not set for BottomUp Curve {}. Check REF_CURVE_ID column in table EAS_IR_CURVE ", s.getIrCurveId()));
			
		}
		
		curveMst.stream().forEach(s -> logger.debug("Bottom Up Discount Rate for {} will be created using RF Curve {}",s.getCurCd(),  s.getRefCurveId()  ));
		
	}
	/**
	 *  ����ڰ� �Է��� swaption vol  �������� ���ռ� ����
	 *  @param bssd ���س��
	 *   
	*/
	
	public static void validatePrecedingSwaptionVol(String bssd) {
		List<SwaptionVol> volRst = SwaptionVolDao.getPrecedingSwaptionVol(bssd, -36);

		Map<String, List<SwaptionVol>> volBssdMap = volRst.stream().collect(Collectors.groupingBy(s->s.getBaseYymm(), Collectors.toList()));
		
		logger.info("ESG parameter need Swaption Vol 6*6 Data ( total 36 Data) for past 36 Months");
		volBssdMap.entrySet().stream()
							 .filter(s->s.getValue().size()==36)
							 .forEach(s-> logger.debug("Swaption Vol at {} has {} Data", s.getKey(), s.getValue().size()));

		volBssdMap.entrySet().stream()
							 .filter(s->s.getValue().size()!=36)
							 .forEach(s-> logger.warn("Swaption Vol Size at {} doesn't have  {} not 36 Data", s.getKey(), s.getValue().size()));
		
	}
	
	public static void validateSwaptionVol(String bssd) {
		List<SwaptionVol> volRst = SwaptionVolDao.getSwaptionVol(bssd);

		Map<String, List<SwaptionVol>> volBssdMap = volRst.stream().collect(Collectors.groupingBy(s->s.getBaseYymm(), Collectors.toList()));
		
		logger.info("ESG parameter need Swaption Vol 6*6 Data ( total 36 Data) for given date");
		
		volBssdMap.entrySet().stream()
							 .filter(s->s.getValue().size()==36)
							 .forEach(s-> logger.debug("Swaption Vol at {} has {} Data", s.getKey(), s.getValue().size()));

		volBssdMap.entrySet().stream()
							 .filter(s->s.getValue().size()!=36)
							 .forEach(s-> logger.warn("Swaption Vol Size at {} doesn't have  {} not 36 Data", s.getKey(), s.getValue().size()));
		
	}
	
	public static void validateUsedEsgModel() {
	
		List<EsgMst> esgMstList = EsgMstDao.getEsgMst(EBoolean.Y);			//TODO : irCurveId �� filtering ó�� ���???
		
		
//		No ESG Model
		if(esgMstList.size()==0) {
			logger.error("ESG Model used to generate scenario are not found. Check if the column USE_YN has no \"Y\" record in the table  ");
			System.exit(1);
		}
//		Multi ESE Model !!!
		else if( esgMstList.size() > 1) {
			logger.error("ESG Model used to generate scenario are {} Model ", esgMstList.size());
			logger.error("Please check if the column USE_YN has only one \"Y\" record in the table EAS_ESG_MST table");
			System.exit(1);
		}
//		ESG Info
		esgMstList.forEach(s-> logger.info("Active ESG Models is {}. You can change active Model to switch the USE_YN Column in the table EAS_ESG_MST", s.getIrModelId()));
	}
	
	
//	public static void validDisclosureRateHis(String bssd) {
//		List<DiscRateCalcSetting> settingList = DiscRateSettingDao.getDiscRateSettings();
//		for(DiscRateCalcSetting setting: settingList) {
//			DiscRateHis discHis = DiscRateSettingDao.getDiscRateHis(bssd, setting.getIntRateCd());
//			if (discHis==null ) {
//				logger.error("Disclosure Rate History Data is null for {} : {}. Check EAS_DISC_RATE_HIS", setting.getIntRateCd(), setting.getRemark());
//			}
//			else if ( discHis.getApplDiscRate() == null ||discHis.getApplDiscRate() ==0) {
//				logger.warn("Applyed Disclousre Rate is  null in the DiscRateHis for {}:  {}. Check EAS_DISC_RATE_HIS and column APPL_DISC_RATE", setting.getIntRateCd(), setting.getRemark());
//			}
//			else if ( discHis.getBaseDiscRate() == null|| discHis.getBaseDiscRate() ==0 ) {
//				logger.warn(" Disclousre Base Rate is  null in the DiscRateHis for {} : {}. Check EAS_DISC_RATE_HIS and column BASE_DISC_RATE", setting.getIntRateCd(), setting.getRemark());
//						
//			}
//			else if ( discHis.getExBaseIr() == null|| discHis.getExBaseIr() ==0 ) {
//				logger.warn(" External Rate is  null in the DiscRateHis for {} :  {}. Check record and column EX_BASE_IR", setting.getIntRateCd(), setting.getRemark());
//			}
//		}
//	}
	
//	public static void validExternalIntRate(String bssd) {
//		List<String> extIrList = DiscRateSettingDao.getDiscRateExternalIntRateUd(bssd);
//		
//		for (String aa : extIrList) {
//			if(aa == null || aa.trim() =="") {
//				logger.info("External Rate may be positve. Please check the input data in EAS_USER_DISC_RATE_EX_BASE_IR at given date {}",  bssd);
//			}
//			else {
//				double rate = Double.parseDouble(aa);
//				if(rate <= 0.0) {
//					logger.info("External Rate may be positve. Please check the input data in EAS_USER_DISC_RATE_EX_BASE_IR at given date {}",  bssd);
//				}
//			}
//		}
//	}
	
//	public static void validDiscRateCumAssetYield(String bssd) {
//		List<String> yieldList = DiscRateSettingDao.getDiscRateAssetYieldUd(bssd);
//		if (yieldList.isEmpty() || yieldList.size()<2 ) {
//			logger.warn("There must be 2 data in the table EAS_USER_DISC_RATE_ASST_REVN_CUM_RATE at given date {} for Session Date {}", FinUtils.addMonth(bssd, -2), bssd);
//			logger.warn("But only {} data  are managed.  Users should input the Asset Yield ", yieldList.size());
//		}
//	}
	
//	public static void validDiscRateAssetYield(String bssd) {
//		List<String> yieldList = DiscRateSettingDao.getDiscRateAssetYieldUd(bssd);
//		if (yieldList.isEmpty() || yieldList.size()<2 ) {
//			logger.warn("There must be 2 data in the table EAS_USER_DISC_RATE_ASST_REVN_RATE at given date {} for Session Date", FinUtils.addMonth(bssd, -2), bssd);
//			logger.warn("But only {} data  are managed.  Users should input the Asset Yield ", yieldList.size());
//		}
//	}
	
	public static void validTransitionMatrix(String bssd) {
		List<TransitionMatrixUd> tmList = TransitionMatrixDao.getTMUd(bssd);
		Map<String , List<TransitionMatrixUd>> tmMap = tmList.stream().collect(Collectors.groupingBy(s-> s.getFromGrade(), Collectors.toList()));
		
//		int tmSize =0;
		for(Map.Entry<String, List<TransitionMatrixUd>> entry : tmMap.entrySet()) {
			if(entry.getValue().size() != tmMap.keySet().size() + 1) {
				logger.warn("Transition Element of {} should be {} but input Data is only {}.", entry.getKey(), tmMap.keySet().size() + 1, entry.getValue().size() );
			}
		}
	}
	
	public static void validTransitionMatrixSumEqualOne(String bssd) {
		List<TransitionMatrixUd> tmList = TransitionMatrixDao.getTMUd(bssd);
		Map<String , List<TransitionMatrixUd>> tmMap = tmList.stream().collect(Collectors.groupingBy(s-> s.getFromGrade(), Collectors.toList()));
		
		for(Map.Entry<String, List<TransitionMatrixUd>> entry : tmMap.entrySet()) {
			double tmRate =0.0;
			for(TransitionMatrixUd  tm : entry.getValue()) {
				tmRate = tmRate + tm.getTmRate();
			}
			if(tmRate !=1.0) {
				logger.warn("Sum of Transition Rate of Credit Grade {} is not equal to 1.0. It is {}", entry.getKey(), tmRate );
			}
		}
	}
}