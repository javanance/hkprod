package com.gof.model;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;

import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.MersenneTwister;
import org.netlib.util.doubleW;

import com.gof.entity.BizDiscountRate;
import com.gof.entity.BizDiscountRateSce;
import com.gof.entity.BizEsgParam;
import com.gof.entity.BizStockSce;
import com.gof.entity.BottomupDcnt;
import com.gof.entity.EsgRandom;
import com.gof.entity.IrSce;
import com.gof.interfaces.Rrunnable;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> Hull White 1 Factor ����	</p>        
 *  <p> R Script �� �����ϱ� ����  Input Data ���� �� ���� , R Script ����, Output Converting �۾��� ������.</p>
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class LogNormal4j implements Rrunnable {

	private String baseYymm;
	private Map<Integer, Double> driftMap ;
	private Map<Integer, Double> sigmaMap ;
	
	private double[][] randomNum;
	private int tenorSize; 
	private int sceNum; 
	
	public LogNormal4j() {
	
	}

	public LogNormal4j(String baseYymm, Map<Integer, Double> driftMap, Map<Integer, Double> sigmaMap, int tenorSize, int batchNum) {
		this.baseYymm = baseYymm;
		
		this.driftMap  = driftMap;
		this.sigmaMap  = sigmaMap;
		this.sceNum    = batchNum * 100 ;
		this.tenorSize = tenorSize;
		
		this.randomNum = createRandom(sceNum +1, tenorSize);
	}
	
	
	public List<BizStockSce> getBizStockScenario(String bssd, String assetCd, String bizDv, int batch){
		List<BizStockSce> rstList = new ArrayList<BizStockSce>();
		
		double prevStockSce=0.0;
		double currStockSce=0.0;
		double yieldSce =0.0;
		double dt = 1/12.0;
		
		for(int j = (batch-1) * 100 + 1 ; j <= batch * 100; j++) {
			for(int k =1 ; k<tenorSize; k++) {
				String matCd ="M"+ String.format("%04d", k);
				String sceNo =String.valueOf(j);
				
				double drift = driftMap.get(k);
				double sigma = sigmaMap.get(k);
//				double drift = drift[k];
//				double sigma = sigma[k];
			
//				currStockSce = prevStockSce * Math.exp( ( drift -  0.5* sigma * sigma ) * dt   + sigma * Math.sqrt(dt) * randomNum[j][k]);
//				prevStockSce = currStockSce;
				
				yieldSce =  ( drift ) * dt   + sigma * Math.sqrt(dt) *  randomNum[j][k];
				
				rstList.add(BizStockSce.builder()
						.baseYymm(bssd)
						.applBizDv(bizDv)
						.sceNo(String.valueOf(sceNo))
//						.sceNo(String.valueOf(aa.getSceNo()))
//						.matCd( "M" + String.format("%04d", k))
						.matCd( matCd)
						.stdAsstCd(assetCd)
//						.asstYield(yieldSce)
//						.asstYield(Math.exp(yieldSce / dt)-1 )
//						.asstYield(Math.exp(yieldSce / dt)-1 )
						.asstYield( yieldSce / (12.0 * dt) )			//TODO :Check !!!!!
						.lastModifiedBy("ESG")
						.lastUpdateDate(LocalDateTime.now())
						.build()
						);
			}
		}
		log.info("BizStocktSce : {},{}", batch, rstList.size());
		return rstList;
	}
	
	private double[][] createRandom(int sceNo, int projectionTerm) {
		int genSceNum =  (sceNo - 1)/2;
		double[][] random = new double[sceNo][projectionTerm];
		
		GaussianRandomGenerator gen = new GaussianRandomGenerator(new MersenneTwister(Long.valueOf(baseYymm)));
		
		for(int i =1 ; i<= genSceNum ; i++) {
			for(int j=1 ; j< projectionTerm; j++) {
				double randomNum = gen.nextNormalizedDouble();
				random[2*i-1][j] = randomNum;
				random[2*i][j] = -1.0 * randomNum;
			}
		}
		return random;
	}
}
