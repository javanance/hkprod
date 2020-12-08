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
public class HullWhite4j implements Rrunnable {

	private String baseYymm;
	private Map<String, Double> curveHisMap ;
	private Map<String, Double> fwdHisMap ;
	private Map<String, Double> speedMap ;
	private Map<String, Double> sigmaMap ;
	private Map<String, Double> thetaMap ;
	
	private double[] intRate;
	private double[] fwdRate;
	private double[] contFwdRate;
	private double[] speed;
	private double[] cumSpeed;					//K(t) at time t
	private double[] sumExpCumSpeed;
	
	private double[] sigma;
	private double[] theta;
	private double[] avgPv;
	
	private double[][] randomNum;
	private double[][] shortRate;
	private double[][] pv;
	private int tenorSize; 
	private int sceNum; 
	private double dt;
	private double ufr;
	private double ufrt;
//	private int batchNum;
//	private List<ParamApply> paramHisList = new ArrayList<ParamApply>();
	private List<BizEsgParam> bizParamHisList = new ArrayList<BizEsgParam>();
	
	
	public HullWhite4j() {
	
	}

	public HullWhite4j(String baseYymm, List<BizDiscountRate> curveHisList, List<BizEsgParam> param,  double ufr, double ufrt, int batchNum) {
		this.bizParamHisList = param;
		this.baseYymm = baseYymm;
		this.ufr = ufr;
		this.ufrt = ufrt;
		this.fwdHisMap = curveHisList.stream().collect(toMap(BizDiscountRate::getMatCd,BizDiscountRate::getRiskAdjRfFwdRate));
		this.curveHisMap = curveHisList.stream().collect(toMap(BizDiscountRate::getMatCd,BizDiscountRate::getRiskAdjRfRate));
//		curveHisMap.entrySet().forEach(s->log.info("zzzz : {},{}", s.getKey(), s.getValue()));
		
		this.speedMap = param.stream().filter(s->s.getParamTypCd().equals("ALPHA")).collect(toMap(BizEsgParam::getMatCd, BizEsgParam::getAppliedVal));
		this.sigmaMap = param.stream().filter(s->s.getParamTypCd().equals("SIGMA")).collect(toMap(BizEsgParam::getMatCd, BizEsgParam::getAppliedVal));
		
		this.tenorSize = curveHisList.size()+1;
		this.sceNum    = batchNum * 100 ;
		
		this.intRate 	 = new double[tenorSize];
		this.fwdRate 	 = new double[tenorSize];
		this.contFwdRate = new double[tenorSize];
		this.speed 		 = new double[tenorSize];
		this.cumSpeed 	 = new double[tenorSize];
		this.sigma 		 = new double[tenorSize];
		this.theta 		 = new double[tenorSize];
		this.avgPv 		 = new double[tenorSize];
		
		this.randomNum = createRandom(sceNum +1, tenorSize);
		this.shortRate = new double[sceNum +1][tenorSize];
		this.pv 	   = new double[sceNum +1][tenorSize];
		 
		this.dt			= 1.0/12.0;
		double prevRate  	= 0.0;
		double prevFwdRate  = 0.0;
		
		double prevSpeed = speedMap.getOrDefault("M1200", 0.015);
		double prevSigma = sigmaMap.getOrDefault("M1200", 0.005);
		
//		for (int i = 1; i <= intRate.length; i++) {
		for (int i = tenorSize-1; i >= 0; i--) {
			String matCd ="M"+ String.format("%04d", i);
//			log.info("mat cd : {}", matCd);
//			log.info("mat cd1 : {}", curveHisMap.containsKey(matCd));
			
			intRate[i] = curveHisMap.getOrDefault(matCd, prevRate);
			fwdRate[i] = fwdHisMap.getOrDefault(matCd, prevFwdRate);
			contFwdRate[i] = Math.log(1+fwdRate[i]);
			
			speed[i] =  speedMap.getOrDefault(matCd, prevSpeed);
			sigma[i] =  sigmaMap.getOrDefault(matCd, prevSigma);
			
//			speed[i] = 0.025;
			
			prevRate 	= intRate[i];
			prevFwdRate = fwdRate[i];
			prevSpeed 	= speed[i];
			prevSigma 	= sigma[i];
		}

		initParam();
		createHullWhite();
	}
	
	public List<IrSce> getIrScenario(String bssd, String irCurveId, String irModelId){
		List<IrSce> rstList = new ArrayList<IrSce>();
		
		for(int j=1 ; j <= sceNum; j++) {
			for(int k =1 ; k<tenorSize; k++) {
				String matCd ="M"+ String.format("%04d", k);
				String sceNo =String.valueOf(j);
				
				rstList.add(IrSce.builder().baseDate(bssd)
										   .irModelId(irModelId)
										   .irCurveId(irCurveId)
										   .matCd(matCd)
										   .sceNo(sceNo)
										   .rfRate(Math.pow(pv[j][k], -1.0 / (dt *k))-1)
										   .lastModifiedBy("ESG")
										   .lastUpdateDate(LocalDateTime.now())
										   .build()
							);
			}
		}
		return rstList;
	}

	public List<BizDiscountRateSce> getBizDiscountScenario(String bssd, String irCurveId, String bizDv){
		List<BizDiscountRateSce> rstList = new ArrayList<BizDiscountRateSce>();
		
		for(int j=1 ; j <= sceNum; j++) {
			for(int k =1 ; k<tenorSize; k++) {
				String matCd ="M"+ String.format("%04d", k);
				String sceNo =String.valueOf(j);
				
				rstList.add(BizDiscountRateSce.builder().baseYymm(bssd)
										   .applyBizDv(bizDv)
										   .irCurveId(irCurveId)
										   .sceNo(sceNo)
										   .matCd(matCd)
										   .rfRate(Math.pow(pv[j][k], -1.0 / (dt *k))-1)
										   .liqPrem(0.0)
										   .refYield(0.0)
										   .crdSpread(0.0)
										   .riskAdjRfRate(Math.pow(pv[j][k], -1.0 / (dt *k))-1)
										   .riskAdjRfFwdRate(Math.exp(shortRate[j][k])- 1)
										   .lastModifiedBy("ESG")
										   .lastUpdateDate(LocalDateTime.now())
										   .build()
							);
			}
			log.info("BizDiscountSce : {}", j);
		}
		return rstList;
	}
	
	public List<BizDiscountRateSce> getBizDiscountScenario(String bssd, String irCurveId, String bizDv, int batch){
		List<BizDiscountRateSce> rstList = new ArrayList<BizDiscountRateSce>();
		
		for(int j = (batch-1) * 100 + 1 ; j <= batch * 100; j++) {
			for(int k =1 ; k<tenorSize; k++) {
				String matCd ="M"+ String.format("%04d", k);
				String sceNo =String.valueOf(j);
				
				rstList.add(BizDiscountRateSce.builder().baseYymm(bssd)
										   .applyBizDv(bizDv)
										   .irCurveId(irCurveId)
										   .sceNo(sceNo)
										   .matCd(matCd)
										   .rfRate(Math.pow(pv[j][k], -1.0 / (dt *k))-1)
										   .liqPrem(0.0)
										   .refYield(0.0)
										   .crdSpread(0.0)
										   .riskAdjRfRate(Math.pow(pv[j][k], -1.0 / (dt *k))-1)
										   .riskAdjRfFwdRate(Math.exp(shortRate[j][k])- 1)
										   .lastModifiedBy("ESG")
										   .lastUpdateDate(LocalDateTime.now())
										   .build()
							);
			}
		}
		log.info("BizDiscountSce : {},{}", batch, rstList.size());
		return rstList;
	}
	
	private void initParam() {
		cumSpeed[0]		= 0.0;
		theta[0] 		= contFwdRate[1];
		contFwdRate[0]	= 0.0;
		
		for(int k= 1; k < tenorSize; k++) {
			cumSpeed[k] = cumSpeed[k-1] + speed[k] * dt;
		}
		
		for(int k= 1; k < tenorSize-1; k++) {
			theta[k] = (contFwdRate[k+1] - contFwdRate[k]) /dt + speed[k]*contFwdRate[k] + (sigma[k] * sigma[k])*(1-Math.exp(-2*cumSpeed[k]))/(2*speed[k]);
		}
		theta[tenorSize-1] 		= theta[tenorSize-2];
	}
	
	
	private void createHullWhite() {
		for(int k =2 ; k < tenorSize; k++) {
			double tempAvgPv = 0.0;
			double cumPv 	 = 1.0;
			double cumEpv 	 = 1.0;
			for(int j=1 ; j <= sceNum ; j++) {
				shortRate[j][0] = contFwdRate[1];
				shortRate[j][1] = contFwdRate[1];
				pv[j][0] 		= 1.0;
				pv[j][1] 		= Math.exp(-1.0* contFwdRate[1] * dt);
				
				shortRate[j][k] = shortRate[j][k-1] + dt * ( theta[k-1] - speed[k-1] * shortRate[j][k-1]) + sigma[k-1] * Math.sqrt(dt) * randomNum[j][k];
				pv[j][k] 		= pv[j][k-1] * Math.exp(-1.0 * shortRate[j][k]* dt);
				
//				tempAvgPv = tempAvgPv + Math.exp(-1.0 * shortRate[j][k]* dt);
				tempAvgPv = tempAvgPv + pv[j][k];
//				log.info("aaaaaaaaaaaaaaaaaa: {},{},{},{},{}", j,k, X_t[j][k], shortRate[j][k], pv[j][k]);
			}
			cumEpv = Math.pow(1+ intRate[k], -1.0* k * dt);
			tempAvgPv= tempAvgPv / sceNum;
			
			double adj = -1.0 * Math.log( cumEpv / tempAvgPv ) / dt;
			
			for(int j=1 ; j <= sceNum ; j++) {
				shortRate[j][k] = shortRate[j][k] + adj;
				pv[j][k] 		= pv[j][k-1] * Math.exp(-1.0 * shortRate[j][k]* dt);
			}

		}
//		Check Full Martingale!!!!!
		double[] fwdPv = new double[tenorSize];
		double[] avgPv = new double[tenorSize];
		for(int k = 1 ; k< tenorSize; k++) {
			double avg = 0.0;
			
			for(int j =1 ; j<=sceNum; j++) {
				avg 	 = avg + shortRate[j][k];
				avgPv[k] = avgPv[k] + pv[j][k];
				fwdPv[k] = fwdPv[k] + Math.exp(-1.0 * shortRate[j][k]* dt);
			}
			
			avgPv[k] 	= avgPv[k] / sceNum;
			fwdPv[k] 	= fwdPv[k] / sceNum;
			
			double qq 	= Math.pow(avgPv[k] / avgPv[k-1], -1.0 * 12.0) - 1.0;
			double zz 	= Math.pow(fwdPv[k], -1.0 * 12.0) -1.0;
			
//			log.info("aaaaa : {},{},{}"   , k, avg/1000.0, Math.pow(1+ intRate[k], -k/12.0));
//			log.info("aaaaa : {},{},{},{}", k, avg/1000.0, contFwdRate[k], Math.pow(1+ intRate[k], -k/12.0));
			
			log.info("HullWhit4j : {},{},{},{},{},{},{}", k, avg/sceNum, fwdRate[k], contFwdRate[k], qq, zz, Math.pow(1+ intRate[k], -k/12.0));
		}
	}
	
	private void createHullWhiteStepwise() {
		for(int k =2 ; k < tenorSize; k++) {
			double tempAvgPv = 0.0;
			for(int j=1 ; j <= sceNum ; j++) {
				shortRate[j][0] = contFwdRate[1];
				shortRate[j][1] = contFwdRate[1];
				pv[j][0] 		= 1.0;
				pv[j][1] 		= Math.exp(-1.0* contFwdRate[1] * dt);
				
				shortRate[j][k] = shortRate[j][k-1] + dt * ( theta[k-1] - speed[k-1] * shortRate[j][k-1]) + sigma[k-1] * Math.sqrt(dt) * randomNum[j][k];
				
				tempAvgPv = tempAvgPv + Math.exp(-1.0 * shortRate[j][k]* dt);
//				log.info("aaaaaaaaaaaaaaaaaa: {},{},{},{},{}", j,k, X_t[j][k], shortRate[j][k], pv[j][k]);
			}
			
			tempAvgPv= tempAvgPv / sceNum;
			
			double adj = -1.0 * Math.log(Math.exp(-1.0 * contFwdRate[k] * dt) / tempAvgPv ) / dt;
			
			for(int j=1 ; j <= sceNum ; j++) {
				shortRate[j][k] = shortRate[j][k] + adj;
				pv[j][k] 		= pv[j][k-1] * Math.exp(-1.0 * shortRate[j][k]* dt);
			}
		}
		
//		Check StepWise Martingale!!!!!
		double[] fwdPv = new double[tenorSize];
		double[] avgPv = new double[tenorSize];
		for(int k = 1 ; k< tenorSize; k++) {
			double avg = 0.0;
			
			for(int j =1 ; j<=sceNum; j++) {
				avg 	 = avg + shortRate[j][k];
				avgPv[k] = avgPv[k] + pv[j][k];
				fwdPv[k] = fwdPv[k] + Math.exp(-1.0 * shortRate[j][k]* dt);
			}
			
			avgPv[k] 	= avgPv[k] / sceNum;
			fwdPv[k] 	= fwdPv[k] / sceNum;
			
			double qq 	= Math.pow(avgPv[k] / avgPv[k-1], -1.0 * 12.0) - 1.0;
			double zz 	= Math.pow(fwdPv[k], -1.0 * 12.0) -1.0;
			
//			log.info("aaaaa : {},{},{}"   , k, avg/1000.0, Math.pow(1+ intRate[k], -k/12.0));
//			log.info("aaaaa : {},{},{},{}", k, avg/1000.0, contFwdRate[k], Math.pow(1+ intRate[k], -k/12.0));
			
			log.info("HullWhit4j : {},{},{},{},{},{},{}", k, avg/sceNum, fwdRate[k], contFwdRate[k], qq, zz, Math.pow(1+ intRate[k], -k/12.0));
		}
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
