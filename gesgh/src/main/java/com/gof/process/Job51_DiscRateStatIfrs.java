package com.gof.process;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.gof.comparator.IrCurveHisComparator;
import com.gof.dao.DiscRateDao;
import com.gof.dao.DiscRateMstDao;
import com.gof.dao.IrCurveHisDao;
import com.gof.entity.DiscRateHis;
import com.gof.entity.DiscRateMst;
import com.gof.entity.DiscRateStats;
import com.gof.entity.IrCurveHis;
import com.gof.enums.EBaseMatCd;
import com.gof.util.EsgConstant;
import com.gof.util.FinUtils;
/**
 *  <p> 내부 기준(IFRS)의  공시이율 추정 모형
 *  <p> 내부산출기준을 적용하여, 자산운용이익률, 외부지표금리, 공시기준이율등의 공시이율 구성요소와 국고채 금리간읜  통계 분석 결과를 적용함.
 *  <p>    1. 예측 Driver 로 국고채 1개월, 3년물, 5년물 등을 통계모형의 독립변수로 지정 
 *  <p>    2. 자산운용이익률, 외부지표금리, 공시기준이율등을 통계모형의 종속변수로 지정    
 *  <p>    3. 예측 Driver 와  종속변수간의 최적 통계모형 산출( 과거 이동평균 12m, 24m, 36m 등을 적용하여 설명력이 최대인 모형을 산출함.
 *  <p>    4. 통계모형과 현재의 금리 수준을 적용하여 공시이율 최선추정치를 산출함.
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */

//@Slf4j
public class Job51_DiscRateStatIfrs {
	
//	통계모형 산출대상과 비대상을 나누어 산출함.   
	public static List<DiscRateStats> createDiscRateStat(String bssd) {
		List<DiscRateStats> rstList   = new ArrayList<DiscRateStats>();
		List<DiscRateStats> calcRst   = new ArrayList<DiscRateStats>();
		List<DiscRateStats> nonCalRst = new ArrayList<DiscRateStats>();
		
		calcRst = createDiscRateStatForCalc(bssd);
		rstList.addAll(calcRst);

		nonCalRst = createDiscRateStatForNonCalc(bssd, calcRst);  		//산출대상이외의 이율코드 적용
		rstList.addAll(nonCalRst);
		
		return rstList;
	}


	//	이율코드가 1.회귀모형을 통해 공시이율 모형을 산출할 수 있는 대상 &  2.공시이율 이력 데이터가 있는 대상 인 경우 적용됨  
	private static List<DiscRateStats> createDiscRateStatForCalc(String bssd) {
		List<DiscRateStats> rstList = new ArrayList<DiscRateStats>();
		DiscRateStats rst; 
		
		String irCurveId  = EsgConstant.getStrConstant().get("ESG_RF_KRW_ID");
		String matList    = EsgConstant.getStrConstant().getOrDefault("DISC_STAT_MAT_LIST", "M0012,M0036,M0060,M0084");
		String avgTermSet = EsgConstant.getStrConstant().getOrDefault("DISC_STAT_AVG_MONTH", "12,24");
		
		
		List<String> matCdList = Arrays.asList(matList.split(","));				// 독립변수로 적용할 만기임
		List<String> termList  = Arrays.asList(avgTermSet.split(","));			//평균 기간을 의미함
		
		
		List<DiscRateMst> discRateMstList = DiscRateMstDao.getDiscRateMstList();
		List<DiscRateHis> discRateHis = DiscRateDao.getDiscRateHis(bssd, -60);   				//공시이율 과거 5년 데이터로 통계분석
		
//		201908 수정 : 2개 이상인 경우만 통계모형 산출 대상임.
		List<String> discRateHisIntRate = new ArrayList<>();
		Map<String, Long> discRateHisMap = discRateHis.stream().collect(groupingBy(s->s.getIntRateCd(), counting()));
		for(Map.Entry<String, Long> zz : discRateHisMap.entrySet()) {
			if(zz.getValue() > 1) {
				discRateHisIntRate.add(zz.getKey());
			}
		}
		List<DiscRateMst> calcSettingList = discRateMstList.stream().filter(s -> s.isCalculable())
																	.filter(s -> discRateHisIntRate.contains(s.getIntRateCd()))
																	.collect(Collectors.toList());
		
		//과거 5년 데이터의 통계분석을 위해 7년치 데이터 추출 : < 만기, 과거데이터 내역>
		Map<String, List<IrCurveHis>> rfCurveMap = getPastCurveMap(bssd, -84, irCurveId, matCdList);		
		
		List<IrCurveHis> rfCurveList = new ArrayList<IrCurveHis>();
		
		Map<String, Double> maRate ;
		for(String matCd : matCdList) {
			rfCurveList = rfCurveMap.get(matCd);
			rfCurveList.sort(new IrCurveHisComparator());
			
			String indiVari = EBaseMatCd.getBaseMatCdEnum(matCd).getKTBCode();
			
//			rfCurveList.forEach(s->log.info("kkkk : {},{}", s.getBaseYymm()));
			for(String term : termList) {
				//이동평균 적용 내역
				maRate = getMovingAvgCurve(rfCurveList, Integer.valueOf(term));
				
//				maRate.entrySet().forEach(s-> log.info("zzz : {},{}", s.getKey(), s.getValue()));
				
				for(DiscRateMst aa : calcSettingList) {
					List<DiscRateHis> assetYieldList = discRateHis.stream()
														.filter(s -> s.getIntRateCd().equals(aa.getIntRateCd()))
														.collect(Collectors.toList());
					
					SimpleRegression reg = new SimpleRegression();
					for(DiscRateHis zz : assetYieldList) {
//						log.info("ccc :  {}, {},{},{}",matCd, zz.getIntRateCd(), zz.getBaseDiscRate(), maRate.get(zz.getBaseYymm()));
						reg.addData(maRate.get(zz.getBaseYymm()), zz.getBaseDiscRate());
					}
					
					rst = new DiscRateStats();
					
					rst.setBaseYymm(bssd);
					rst.setDiscRateCalcTyp(indiVari+"_"+ term);
					
					rst.setIntRateCd(aa.getIntRateCd());
					rst.setDepnVariable("BASE_DISC");
					rst.setIndpVariable(indiVari);
					
					rst.setRegrCoef(reg.getSlope());
					rst.setRegrConstant(reg.getIntercept());
					rst.setRemark("RSquare_"+String.valueOf(reg.getRSquare()));
					rst.setAvgNum( Double.valueOf(term));
					rst.setLastModifiedBy("ESG");
					rst.setLastUpdateDate(LocalDateTime.now());
					
//					logger.info("RST : {}", rst);
					rstList.add(rst);
				}
			}
		}
		return  rstList;
	}
	
//	산출대상이외의 이율코드에 대해서는 현형 공시이율을 표현하는 통계모형 적용
	private static List<DiscRateStats> createDiscRateStatForNonCalc(String bssd, List<DiscRateStats> calcList) {
		List<DiscRateStats> rstList = new ArrayList<DiscRateStats>();
		DiscRateStats rst;
		
		String bizMatCd = EsgConstant.getStrConstant().getOrDefault("IFRS_DICS_MAT", "M0084");
		String bizAvgNum = EsgConstant.getStrConstant().getOrDefault("IFRS_DICS_AVG", "24");
		
		String indiVari = EBaseMatCd.getBaseMatCdEnum(bizMatCd).getKTBCode();

		Set<String> statCodeSet = calcList.stream().map(s-> s.getIntRateCd()).collect(Collectors.toSet());
		
		List<DiscRateMst> discRateMstList = DiscRateMstDao.getDiscRateMstList();
//		List<DiscRateMst> nonCalcSettingList = discRateMstList.stream().filter(s -> !s.isCalculable()).collect(Collectors.toList());
		List<DiscRateMst> nonCalcSettingList = discRateMstList.stream().filter(s -> !statCodeSet.contains(s.getIntRateCd())).collect(Collectors.toList());
		
		List<DiscRateHis> discRateHis = DiscRateDao.getDiscRateHis(bssd, -1); 	
		
		for(DiscRateMst aa : nonCalcSettingList) {
			double nonCalcDiscRate = discRateHis.stream()
					.filter(s -> s.getIntRateCd().equals(aa.getIntRateCd()))
					.filter(s -> bssd.equals(s.getBaseYymm()))
					.map(s-> s.getApplDiscRate())
					.findFirst().orElse(new Double(0.0));
			
			
			rst = new DiscRateStats();
			
			rst.setBaseYymm(bssd);
//			rst.setDiscRateCalcTyp("ASIS");
			rst.setDiscRateCalcTyp(indiVari+"_"+ bizAvgNum);
			
			rst.setIntRateCd(aa.getIntRateCd());
			rst.setDepnVariable("BASE_DISC");
			rst.setIndpVariable(indiVari);
			
			rst.setRegrCoef(0.0);
			rst.setRegrConstant(nonCalcDiscRate);
			rst.setRemark("Rsquare_");
			rst.setAvgNum(Double.valueOf(bizAvgNum));
			rst.setLastModifiedBy("ESG31");
			rst.setLastUpdateDate(LocalDateTime.now());
			
//			logger.info("RST : {}", rst);
			rstList.add(rst);
		}
		return rstList;
	}
	

	//테너별  과거 금리내역 : <테너 : 금리내역>
	private static Map<String, List<IrCurveHis >> getPastCurveMap(String bssd, int monthNum,  String irCurveId, List<String> matCdList){
		List<IrCurveHis> curveList = IrCurveHisDao.getCurveHisBetween(bssd, FinUtils.addMonth(bssd, monthNum), irCurveId);		
		Map<String, String> eomDate = IrCurveHisDao.getEomMap(bssd, irCurveId);
		
		Map<String, List<IrCurveHis>> eomTimeSeriesByMatCd 
				= curveList.stream().filter(s -> eomDate.containsValue(s.getBaseDate()))
									.filter(s-> matCdList.contains(s.getMatCd()))
									.collect(Collectors.groupingBy(s->s.getMatCd(), Collectors.toList()));

		
		return eomTimeSeriesByMatCd;
	}
	
	//일자별 이동평균 금리 : <일자 : 평균금리>
	private static Map<String, Double > getMovingAvgCurve(List<IrCurveHis> curveHis, int maTerm){
		Map<String, Double> rstMap = new HashMap<String, Double>();
		
		double sum=0.0;
		if(curveHis.size() <maTerm) {
			
		}
		else {
			for(int i = maTerm-1 ; i< curveHis.size(); i++) {
				sum=0.0;
				for(int j=i-maTerm+1; j<=i; j++) {
//					log.info("ma input : {},{}", curveHis.get(j).getBaseYymm(), curveHis.get(j).getIntRate());
					sum = sum + curveHis.get(j).getIntRate();
				}
//				log.info("ma rst : {},{}", curveHis.get(i).getBaseYymm(), sum);
				rstMap.put(curveHis.get(i).getBaseYymm(), sum / maTerm);
			}
		}
		return rstMap;
	}
}
