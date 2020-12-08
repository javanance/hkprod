package com.gof.process;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.gof.comparator.TupleComparator;
import com.gof.dao.IrCurveHisDao;
import com.gof.dao.StdAssetDao;
import com.gof.entity.StdAssetCorr;
import com.gof.entity.StdAssetMst;
import com.gof.entity.StdAssetVol;
import com.gof.interfaces.Pricable;
import com.gof.model.Tuple3;
import com.gof.util.FinUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Job04_HisVol {
	
	public static List<StdAssetVol> createHisVol(String bssd, String ewmaYn,  double decayFactor, int volDataSize) {
		List<? extends Pricable> assetHisList = new ArrayList<Pricable>();
		List<StdAssetVol> volList = new ArrayList<StdAssetVol>();
		
		String stBssd = FinUtils.addMonth(bssd, -24);								//EWMA , SMA 산출시 적용 기간 : 24개월
		List<StdAssetMst> assetList = StdAssetDao.getStdAssetMst();
				
		for(StdAssetMst asset : assetList) {
			String mvId =asset.getStdAsstCd();

			if(asset.getStdAsstTypCd().equals("STOCK")){							
				assetHisList = StdAssetDao.getStdStockAssetBtw(stBssd, bssd, mvId);
			}else if(asset.getStdAsstTypCd().equals("BOND")){
				assetHisList = StdAssetDao.getStdBondAssetBtw(stBssd, bssd, mvId);
			}else if(asset.getStdAsstTypCd().equals("SHORT_RATE")) {
				assetHisList = IrCurveHisDao.getShortRateBtw(stBssd, bssd, "1010000").stream().filter(s->s.getMatCd().equals("M0003")).collect(toList());
				
//				assetHisList.forEach(s-> log.info("zzz : {},{},{}", s.getBaseDate(), s.getPrice()));
			}
			
			
			List<String> calcDateList = assetHisList.stream().map(Pricable::getBaseDate)
													.filter(s-> s.compareTo(bssd+"01") >=1)
													.collect(toList());
			
//			최근 데이터에서 과거 데이터로 LOOP 돌면서 가중치, 가중치 반영 수익률 계산함. 
			for(String  calcDate: calcDateList) {
				List<Pricable> calcList = assetHisList.stream().filter(s->s.getBaseDate().compareTo(calcDate)<1).limit(volDataSize).collect(toList());
				
				int index =0;
				double currPrice =0.0;
				double prevPrice =0.0;
				double priceYield =0.0;
				double weight = 1-decayFactor;			//TODO : decay Factor : 0.94
				double squareYieldSum=0.0;
				
				for(Pricable aa : calcList) {
					prevPrice = aa.getPrice();
					if(currPrice > 0.0) {
						if(ewmaYn.equals("Y")) {
							weight = (1-decayFactor) * Math.pow(decayFactor, index);
						}
						else {
							weight = 1 /(double)(volDataSize-1);
						}

						priceYield = Math.log(currPrice / prevPrice);
						squareYieldSum = squareYieldSum + priceYield* priceYield * weight;
						
//								log.info("in the calc : {},{},{},{},{},{},{},{},{}", calcDate, aa.getBaseDate(), index, currPrice, prevPrice, weight,  priceYield, priceYield * priceYield *weight, squareYieldSum);
						index = index +1;
					}
					currPrice = prevPrice;
					
				}
				volList.add(StdAssetVol.builder()
										.baseDate(calcDate)
										.volCalcId(ewmaYn.equals("Y")? "EWMA_"+decayFactor: "SMA")
										.mvId(mvId)
										.mvTypCd(asset.getStdAsstTypCd())
										.curCd(asset.getCurCd())
										.mvHisVol(Math.sqrt(squareYieldSum * volDataSize))
										.lastModifiedBy("ESG")
										.lastUpdateDate(LocalDateTime.now())
										.build()
						);
			}
			
		}
		return volList;
	}
	
	
	public static List<StdAssetCorr> createCorr(String bssd, String ewmaYn,  double decayFactor, int volDataSize) {

		List<StdAssetCorr> corrList = new ArrayList<StdAssetCorr>();
		List<StdAssetMst> assetList = StdAssetDao.getStdAssetMst()
//						.stream()
//						.filter(s ->s.getStdAsstCd().equals("RF_KRW_3M"))
//						.collect(toList())
				;
		
		Map<String, List<? extends Pricable>> asstHisMap = new HashMap<String, List<? extends Pricable>>();
		
		String stBssd = FinUtils.addMonth(bssd, -24);
		
//		Data 추출
		for(StdAssetMst asset : assetList) {
			String mvId =asset.getStdAsstCd();
			
			if(asset.getStdAsstTypCd().equals("STOCK")){							
				asstHisMap.putIfAbsent(asset.getStdAsstCd(), StdAssetDao.getStdStockAssetBtw(stBssd, bssd,mvId));
			}else if(asset.getStdAsstTypCd().equals("BOND")){
				asstHisMap.putIfAbsent(asset.getStdAsstCd(), StdAssetDao.getStdBondAssetBtw(stBssd, bssd, mvId));
			}else if(asset.getStdAsstTypCd().equals("SHORT_RATE")) {
				asstHisMap.putIfAbsent(asset.getStdAsstCd(), IrCurveHisDao.getShortRateBtw(stBssd, bssd, "1010000").stream()
											.filter(s->s.getMatCd().equals("M0003")).collect(toList()));
			}
		}
		
//		asstHisMap.entrySet().stream().map(s->s.getValue())
//		 					.flatMap(s->s.stream())
//		 					.forEach(s->log.info("his data in corr: {},{}", s.getBaseDate(), s.getPrice()));
		
		for(StdAssetMst asset : assetList) {
			Map<String, Pricable> baseCalcMap = asstHisMap.get(asset.getStdAsstCd()).stream().collect(toMap(Pricable::getBaseDate, Function.identity()));
			for(StdAssetMst refAsset : assetList) {
				Map<String, Pricable> refCalcMap  = asstHisMap.get(refAsset.getStdAsstCd()).stream()
																.filter(s->baseCalcMap.containsKey(s.getBaseDate()))
															   .collect(toMap(Pricable::getBaseDate, Function.identity()));
				
//				동일날짜의 Data 정리
				List<Tuple3> calcTuple = new ArrayList<Tuple3>();
				List<String> calcDateList = new ArrayList<String>();
				for(Map.Entry<String, Pricable> entry :refCalcMap.entrySet()) {
					if(entry.getKey().compareTo(bssd+"01") >=1) {
						calcDateList.add(entry.getKey());
					}
					calcTuple.add(new Tuple3(entry.getKey(), baseCalcMap.get(entry.getKey()).getPrice(), entry.getValue().getPrice()));
				}
				
				calcTuple.sort(new TupleComparator());
				
				for(String calcDate : calcDateList) {
//							log.info("aaaa : {}", calcDate);
					int index =0;
					double currPrice =0.0;
					double currRefPrice =0.0;
					double prevPrice =0.0;
					double prevRefPrice =0.0;
					double priceYield =0.0;
					double priceRefYield =0.0;
					double weight = 1-decayFactor;			//TODO : decay Factor : 0.94
					double squareYieldSum=0.0;
					double squareRefYieldSum=0.0;
					double crossYieldSum=0.0;

					for(Tuple3  data: calcTuple) {
						if(data.getKey().compareTo(calcDate)<1 && index < volDataSize) {
//							log.info("calc Date his : {},{},{}", calcDate, data.getKey(), data.getV1());
							prevPrice	 = data.getV1();
							prevRefPrice = data.getV2();
							if(currPrice > 0.0) {
								if(ewmaYn.equals("Y")) {
									weight = (1-decayFactor) * Math.pow(decayFactor, index);
								}
								else {
									weight = 1 /(double)(volDataSize-1);
								}
								
								priceYield = Math.log(currPrice / prevPrice);
								squareYieldSum = squareYieldSum + priceYield* priceYield * weight;
								
								priceRefYield = Math.log(currRefPrice / prevRefPrice);
								squareRefYieldSum = squareRefYieldSum + priceRefYield* priceRefYield * weight;
								
								crossYieldSum = crossYieldSum + priceYield* priceRefYield * weight;
								
	//							log.info("in the calc : {},{},{},{},{},{},{},{},{}", calcDate, data.getKey(), index, weight,  priceYield, priceRefYield, crossYieldSum);
								index = index +1;
							}
							currPrice 	 = prevPrice;
							currRefPrice = prevRefPrice;
						}
					}
					
					corrList.add(StdAssetCorr.builder()
								.baseDate(calcDate)
								.volCalcId(ewmaYn.equals("Y")? "EWMA_"+decayFactor: "SMA")
								.mvId(asset.getStdAsstCd())
								.refMvId(refAsset.getStdAsstCd())
								.mvHisCov(crossYieldSum * volDataSize)
								.mvHisCorr(crossYieldSum / ( Math.sqrt(squareYieldSum) * Math.sqrt(squareRefYieldSum)))
								.lastModifiedBy("ESG")
								.lastUpdateDate(LocalDateTime.now())
								.build()
								);
				}
					
			}
		}

//		corrList.forEach(s->log.info("aaaa : {},{},{},{}", s.getBaseDate(), s.getMvId(), s.getRefMvId()));
		return corrList;
	}

}
