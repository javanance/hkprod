package com.gof.process;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.dao.EsgMstDao;
import com.gof.dao.EsgParamDao;
import com.gof.entity.BizEsgParam;
import com.gof.entity.BizEsgParamUd;
import com.gof.entity.EsgMst;
import com.gof.entity.ParamCalcHis;
import com.gof.enums.EBoolean;
import com.gof.util.ParamUtil;

/**
 *  <p> 占쌥몌옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占싫몌옙叩占쏙옙, 占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占싹댐옙 占쌜억옙占쏙옙 占쏙옙占쏙옙占싹댐옙 클占쏙옙占쏙옙
 *  <p> IFRS 17 占쏙옙占쏙옙占쏙옙 占신곤옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙체占쏙옙占쏙옙占쏙옙 占쏙옙占시듸옙占쏙옙 占쏙옙占쏙옙占쏙옙 KICS 占쏙옙占쏙옙占쏙옙 占신곤옙占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙占� 占쏙옙占쏙옙構占� 占쏙옙占쏙옙.         
 *  <p> 占쌥몌옙 占쏙옙占쏙옙占싶곤옙 占쏙옙占쏙옙占싹댐옙 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쌔쇽옙占쏙옙 占쌥몌옙 占쏙옙占쏙옙占쏙옙 占싱울옙占싹울옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占� 占쏙옙占쏙옙占싹몌옙   
 *  <p> 占쏙옙占쏙옙 占쏙옙占썩에 占쏙옙占쌔쇽옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占쏙옙占� 占싱듸옙 占쏙옙占쏙옙臼占� 占쏙옙占쏙옙占쏙옙. 
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
public class Job13_BizEsgParameter {
	
	private final static Logger logger = LoggerFactory.getLogger(Job13_BizEsgParameter.class);
	
	/**
	 *  ESG 占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙 占신곤옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙.
	 *  <p> 占쏙옙占쏙옙微占� 占쌉뤄옙占쏙옙 占신곤옙占쏙옙占쏙옙占쏙옙 占쎌선占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占싹몌옙, 占쌉뤄옙占쏙옙 占신곤옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占� ESG Engine 占쏙옙 KICS 占쏙옙 占쏙옙占쏙옙極占� 占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙 占신곤옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙占쏙옙. 
	 *  @param bssd 占쏙옙占쌔놂옙占�
	 *  @return  占쏙옙占쏙옙 占신곤옙占쏙옙占쏙옙 
	*/
		
	public static List<BizEsgParam> createBizAppliedParameter(String bssd ) {
		List<BizEsgParam> bizApplied = new ArrayList<BizEsgParam>();
		List<BizEsgParamUd> userParam = EsgParamDao.getBizEsgParamUd(bssd);
		
		if(userParam.isEmpty()) {
			bizApplied =calculateBizAppliedParameter(bssd);
			logger.info("Job13 (Setting Biz Applied Parameter ) : Biz Applied parameter is used with calucated parameters");
		}
		else {
			bizApplied = userParam.stream().map(s -> s.convertToBizEsgParam(bssd)).collect(Collectors.toList());
			logger.info("Job13 (Setting Biz Applied Parameter ) : Biz Applied parameter is used with user Defined parameters");
		}

		logger.info("Job13 (Setting Biz Applied Parameter ) creates {} resutls. They are inserted into EAS_BIZ_APLY_PARAM Table", bizApplied.size());
		
		return bizApplied;
	}
	
	private static List<BizEsgParam> calculateBizAppliedParameter(String bssd ) {
		List<BizEsgParam> bizApplyRst = new ArrayList<BizEsgParam>();
		BizEsgParam temp;
		
		List<EsgMst> esgMstList = EsgMstDao.getEsgMst(EBoolean.Y);			
		
//		Map<String, Object> param1 = new HashMap<String, Object>();
		
		for(EsgMst aa : esgMstList) {
//			param1.put("baseYymm", bssd);
//			param1.put("irModelTyp", aa.getIrModelTyp());
//			param1.put("paramCalcCd", aa.getParamApplCd());
//			List<ParamCalcHis> paramHisRst = DaoUtil.getEntities(ParamCalcHis.class, param1);
			
			List<ParamCalcHis> paramHisRst = EsgParamDao.getParamCalHis(bssd, aa.getIrModelTyp(), aa.getParamApplCd());
			
			logger.debug("applied : {}", paramHisRst.size());
			
			for(ParamCalcHis bb : paramHisRst) {
				temp = new BizEsgParam();
				temp.setBaseYymm(bssd);
				temp.setApplyBizDv("I");
				temp.setIrModelId(aa.getIrModelId());
				temp.setParamTypCd(bb.getParamTypCd());
				temp.setMatCd(bb.getMatCd());
				temp.setApplParamVal(bb.getParamVal());
				temp.setVol(0.0);
				temp.setLastModifiedBy("ESG");
				temp.setLastUpdateDate(LocalDateTime.now());
				bizApplyRst.add(temp);
			}
			
			int esgParamAlphaOuterAvgNum = Integer.parseInt(ParamUtil.getParamMap().getOrDefault("esgParamAlphaOuterAvgNum", "-36")); 
//			int esgParamAlphaOuterAvgNum = Integer.parseInt(ParamUtil.getParamMap().getOrDefault("esgParamAlphaOuterAvgNum", "-36")); 
			String esgParamAlphaOuterMatCd  = ParamUtil.getParamMap().getOrDefault("esgParamAlphaOuterMatCd", "M0240");
			
			int esgParamSigmaOuterAvgNum = Integer.parseInt(ParamUtil.getParamMap().getOrDefault("esgParamSigmaOuterAvgNum", "-36"));
			String esgParamSigmaOuterMatCd  = ParamUtil.getParamMap().getOrDefault("esgParamSigmaOuterMatCd", "M0120");
					 
			if(aa.getIrModelTyp().equals("4")) {
				bizApplyRst.addAll(createBizAppliedParameterOuter(bssd, esgParamAlphaOuterAvgNum, "ALPHA", esgParamAlphaOuterMatCd))	;
				bizApplyRst.addAll(createBizAppliedParameterOuter(bssd, esgParamSigmaOuterAvgNum, "SIGMA", esgParamSigmaOuterMatCd))	;
			}
		}
		return bizApplyRst;
	}
	
	
	
	/**
	 *  占쏙옙占쏙옙 占쏙옙占썩에 占쏙옙占쏙옙 ESG 占신곤옙占쏙옙占쏙옙 占쏙옙占쏙옙 占쏙옙占� 占쌥울옙
	 *  @param bssd 占쏙옙占쌔놂옙占�
	 *  @param monthNum 占쏙옙占쏙옙 占싱듸옙占쏙옙占� 占쏙옙占쏙옙
	 *  @param paramType 占신곤옙占쏙옙占쏙옙 占쏙옙占쏙옙
	 *  @param matCd 占쏙옙占쏙옙占쌘듸옙 
	 *  @return  占쏙옙占쏙옙 占신곤옙占쏙옙占쏙옙 
	*/
	
	private static List<BizEsgParam> createBizAppliedParameterOuter(String bssd , int monthNum, String paramType, String matCd) {
		List<BizEsgParam> bizApplyRst = new ArrayList<BizEsgParam>();
		BizEsgParam temp;
		
//		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("useYn", EBoolean.Y);
//		List<EsgMst> esgMstList = DaoUtil.getEntities(EsgMst.class, param);
		
		List<EsgMst> esgMstList = EsgMstDao.getEsgMst(EBoolean.Y);		
		
		for(EsgMst aa : esgMstList) {
//			List<ParamCalcHis> paramHisRst = EsgParamDao.getParamCalHis(bssd, monthNum, paramType, matCd);
//			List<ParamCalcHis> paramHisRst = EsgParamDao.getFullLocalCalibParamCalHis(bssd, monthNum, paramType, matCd);
			List<ParamCalcHis> paramHisRst = EsgParamDao.getSigmaLocalCalibParamCalHis(bssd, monthNum, paramType, matCd);
//			logger.info("applied Outer: {}", paramHisRst.size());
			
			temp = new BizEsgParam();
			temp.setBaseYymm(bssd);
			temp.setApplyBizDv("I");
			temp.setIrModelId(aa.getIrModelId());
			temp.setParamTypCd(paramType);
			temp.setMatCd("M1200");
			temp.setApplParamVal(paramHisRst.stream().collect(Collectors.averagingDouble(s ->s.getParamVal())));
			temp.setVol(0.0);
			temp.setLastModifiedBy("ESG");
			temp.setLastUpdateDate(LocalDateTime.now());
			bizApplyRst.add(temp);
		}
		return bizApplyRst;
	}

}