package com.gof.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import com.gof.dao.BottomupDcntDao;
import com.gof.dao.DiscRateMstDao;
import com.gof.dao.DiscRateStatsDao;
import com.gof.dao.IrCurveHisDao;
import com.gof.entity.BizDiscRate;
import com.gof.entity.BizDiscRateStat;
import com.gof.entity.BottomupDcnt;
import com.gof.entity.DiscRate;
import com.gof.entity.IrCurveHis;
import com.gof.enums.EBaseMatCd;
import com.gof.util.EsgConstant;
import com.gof.util.FinUtils;
import com.gof.util.ParamUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class BizDiscRateModel {

	public static List<BizDiscRate> getBizDiscRateAsync(String bssd, String bizDv,ExecutorService exe) {
		return getDiscRateAsync(bssd, bizDv, exe).stream().map(s->s.convertTo()).collect(Collectors.toList());
	}
	
	public static List<DiscRate> getDiscRateAsync(String bssd, String bizDv, ExecutorService exe) {
		String isRiskFreeString    = ParamUtil.getParamMap().getOrDefault("discIntenalDriverIsRiskFree", "N");
		boolean	isRiskFree = isRiskFreeString.equals("Y");
		
		Map<String, Map<String, Double>> curveHistoryRateByMaturity = new HashMap<String, Map<String, Double>>();
		
		for(EBaseMatCd aa : EBaseMatCd.values()) {
			curveHistoryRateByMaturity.put(aa.name(), getAllDriverCurveMapByMatCd(bssd, -36, aa.name(), isRiskFree));
		}
		
		List<String> discSetting = DiscRateMstDao.getDiscRateMstList().stream()
//														.filter(s ->s.isCalculable())
														.map(s ->s.getIntRateCd())
														.collect(Collectors.toList());

		List<BizDiscRateStat> bizStatList = DiscRateStatsDao.getBizDiscRateStat(bssd).stream()
															.filter(s-> s.getApplyBizDv().equals(bizDv))
															.filter(s-> discSetting.contains(s.getIntRateCd()))
//															.filter(s-> s.getIntRateCd().equals("2305"))
															.collect(Collectors.toList());

		
		List<CompletableFuture<List<DiscRate>>> sceJobFutures =  bizStatList.stream()
				.map(stat -> CompletableFuture.supplyAsync(() -> getDiscRateByStat(stat, bssd, curveHistoryRateByMaturity), exe))
				.collect(Collectors.toList());

		List<DiscRate> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
		
		log.info("Disc Rate for Model {} is calculated. {} Results are inserted into EAS_DISC_RATE_ table", bizDv, rst.size());
		return rst;
	}
	
	
	private static List<DiscRate> getDiscRateByStat(BizDiscRateStat stat, String bssd, Map<String, Map<String, Double>> curveHistoryRateByMaturity) {
		List<DiscRate> rstList = new ArrayList<DiscRate>();
		DiscRate temp;
		int k = stat.getAvgMonNum().intValue();
		
		Map<String, Double> curveHistoryMap = curveHistoryRateByMaturity.get(stat.getIndiVariableMatCd());
		log.info("zzz :  {},{}", stat, curveHistoryRateByMaturity.size());
		
		for(int i =0; i< 1200; i++) {
			int cnt =0;
			double intRate =0.0;
			
			for(int j= i + 1 - k; j < i+1; j++) {
				
				String fwdBssd = FinUtils.addMonth(bssd, j);
				cnt =cnt +1;
				intRate = intRate + curveHistoryMap.getOrDefault(fwdBssd, new Double(0.0));
//				logger.info("asdfasdf : {},{},{},{}", fwdBssd, curveHistoryMap.getOrDefault(fwdBssd, new Double(0.0)), intRate);
			}
			intRate = intRate/ cnt;
			
			temp = new DiscRate();
			
			temp.setBaseYymm(bssd);
			temp.setIntRateCd(stat.getIntRateCd());
			temp.setDiscRateCalcTyp(stat.getApplyBizDv());
			temp.setMatCd("M" +String.format("%04d", i+1));
			temp.setBaseDiscRate(stat.getRegrCoef() * intRate + stat.getRegrConstant());
			temp.setAdjRate(stat.getAdjRate());
			
//			log.info("zzzzz : {},{},{},{}", temp.getIntRateCd(), temp.getBaseDiscRate(), temp.getAdjRate(), intRate);
			
			temp.setDiscRate(temp.getBaseDiscRate() * temp.getAdjRate());
			
			temp.setAvgFwdRate(intRate);
			temp.setLastModifiedBy("ESG");
			temp.setLastUpdateDate(LocalDateTime.now());
			
			rstList.add(temp);
		}
//		rstList.stream().forEach(s->logger.debug("Accrete  Rate for IFRS17 Result : {}", s.toString()));
		return rstList;
	}

	private static Map<String, Double> getAllDriverCurveMapByMatCd(String bssd,  int monthNum, String matCd, boolean isRiskFree){
		
		Map<String, Double> rstMap = new HashMap<String,  Double>();
		
		Map<String, Double> matCdRateMap = new HashMap<String,  Double>();
		
		matCdRateMap = getDriverCurveMap(bssd, isRiskFree);
		
		rstMap = FinUtils.getForwardRateByMaturityZZ(bssd, matCdRateMap, matCd); 
		
		for(int k= monthNum; k < 1; k++) {
			String prevBssd = FinUtils.addMonth(bssd, k);
			matCdRateMap = getDriverCurveMap(prevBssd, isRiskFree);
			rstMap.put(prevBssd, matCdRateMap.getOrDefault(matCd, new Double(0.0)));
		}
		return rstMap;
	}
	
	private static Map<String, Double> getDriverCurveMap(String bssd,  boolean isRiskFree){
		if(isRiskFree) {
			List<IrCurveHis> curveList = IrCurveHisDao.getIrCurveHis( bssd, EsgConstant.getStrConstant().get("ESG_RF_KRW_ID"));		
				
//			List<SmithWilsonParam> swParamList =SmithWilsonDao.getParamList();
//			Map<String, SmithWilsonParam> swParamMap = swParamList.stream().collect(Collectors.toMap(s->s.getCurCd(), Function.identity()));
//				 
//			double ufr =  swParamMap.get("KRW").getUfr();
//			double ufrt =  swParamMap.get("KRW").getUfrT();
			
			double ufr =  EsgConstant.getSmParam().get("KRW").getUfr();
			double ufrt = EsgConstant.getSmParam().get("KRW").getUfrT();
				
			SmithWilsonModel swModel = new SmithWilsonModel(curveList, ufr, ufrt);
				
			return swModel.getSmithWilsionResult(false).stream().collect(Collectors.toMap(s->s.getMatCd(), s->s.getSpotAnnual()));
		}
		else {
				List<BottomupDcnt> dcntRateList = BottomupDcntDao.getTermStructure(bssd, "RF_KRW_BU");
				
				return dcntRateList.stream().collect(Collectors.toMap(s->s.getMatCd(), s-> s.getRiskAdjRfRate()))	;
		}
	}
	
	
}
