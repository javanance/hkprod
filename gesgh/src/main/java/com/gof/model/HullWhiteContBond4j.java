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
public class HullWhiteContBond4j implements Rrunnable {

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
	
	private double[] a_t;
	private double[] c_t;
	private double[] drift_t;
	
	private double[][] X_t;
	private double[][] b_t;
	private double[][] randomNum;
	private double[][] shortRate;
	private double[][] pv;
	private int tenorSize; 
	private int sceNum; 
	private int durationMonth;
	private double dt;
	private double ufr;
	private double ufrt;
//	private int batchNum;
//	private List<ParamApply> paramHisList = new ArrayList<ParamApply>();
	private List<BizEsgParam> bizParamHisList = new ArrayList<BizEsgParam>();
	
	

	public HullWhiteContBond4j() {
	
	}

	public HullWhiteContBond4j(String baseYymm, List<BizDiscountRate> curveHisList, List<BizEsgParam> param,  double ufr, double ufrt, int batchNum, double durationYear) {
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
		this.durationMonth = (int)(durationYear * 12.0); 
				
		this.intRate 	 	= new double[tenorSize];
		this.fwdRate 	 	= new double[tenorSize];
		this.contFwdRate 	= new double[tenorSize];
		this.speed 		 	= new double[tenorSize];
		this.cumSpeed 	 	= new double[tenorSize];
		this.sumExpCumSpeed	= new double[tenorSize];
		this.sigma 		 	= new double[tenorSize];
		this.theta 		 	= new double[tenorSize];
		this.c_t 		 	= new double[tenorSize];
		this.a_t 		 	= new double[tenorSize];
		this.drift_t 	 	= new double[tenorSize];
		
		this.shortRate = new double[sceNum +1][tenorSize];
		this.pv 	   = new double[sceNum +1][tenorSize];
		this.b_t 	   = new double[sceNum +1][tenorSize];
		this.X_t 	   = new double[sceNum +1][tenorSize];
		 
		this.dt				= 1.0/12.0;
		double prevRate  	= 0.0;
		double prevFwdRate  = 0.0;
		
		double prevSpeed = speedMap.getOrDefault("M1200", 0.025);
		double prevSigma = sigmaMap.getOrDefault("M1200", 0.005);
		
		this.randomNum = createRandom(sceNum +1, tenorSize);
//		for (int i = 1; i <= intRate.length; i++) {
		for (int i = tenorSize-1; i >= 0; i--) {
			String matCd ="M"+ String.format("%04d", i);
//			log.info("mat cd : {}", matCd);
//			log.info("mat cd1 : {}", curveHisMap.containsKey(matCd));
			
			intRate[i] = curveHisMap.getOrDefault(matCd, prevRate);
			fwdRate[i] = fwdHisMap.getOrDefault(matCd, prevFwdRate);
			contFwdRate[i] = Math.log(1+fwdRate[i]);
			
			speed[i] =   speedMap.getOrDefault(matCd, prevSpeed);
			sigma[i] =   sigmaMap.getOrDefault(matCd, prevSigma);
			
			prevRate 	= intRate[i];
			prevFwdRate = fwdRate[i];
			prevSpeed 	= speed[i];
			prevSigma 	= sigma[i];
		}

//		theta[0] = 0;
//		for(int j =1; j< tenorSize; j++) {
//			theta[j] = (contFwdRate[j] - contFwdRate[j-1]) /dt + speed[j]*fwdRate[j] + (sigma[j] * sigma[j])*(1-Math.exp(-2*speed[j]* j * dt))/(2*speed[j]);
//			theta[j] = (contFwdRate[j] - contFwdRate[j-1]) /dt + speed[j]*fwdRate[j] + (sigma[j] * sigma[j])*(1-Math.exp(-2*speed[j]* j * dt))/(2*speed[j]);
//		}
		
		initParam();
//		createHullWhite();
//		createHullWhiteCont();
//		createBondYield();
		createBondYield1();
	}
	
	public List<BizStockSce> getBondYieldScenario(String bssd, String stdAsstCd, String bizDv, int batch){
		List<BizStockSce> rstList = new ArrayList<BizStockSce>();
		
		for(int j = (batch-1) * 100 + 1 ; j <= batch * 100; j++) {
			for(int k =1 ; k<tenorSize; k++) {
				String matCd ="M"+ String.format("%04d", k);
				String sceNo =String.valueOf(j);
				
//				double prevIntRate = -1.0 * Math.log(b_t[j][k-1]) / durationMonth ;
//				double currIntRate = -1.0 * Math.log(b_t[j][k]) / durationMonth ;
//				double bondYield =  prevIntRate / 12.0  + durationMonth * ( currIntRate - prevIntRate);
				
									  	
				double prevIntRate = Math.pow(b_t[j][k-1], -1.0 / ( dt * durationMonth)) -1 ;
				double currIntRate = Math.pow(b_t[j][k],   -1.0 / ( dt * durationMonth)) -1 ;
				
//				double bondYield   = Math.pow(1+ currIntRate, dt)-1  - dt * durationMonth * ( currIntRate - prevIntRate);
//				double bondYield1  = Math.pow(1+ currIntRate, dt)-1 ;
//				double bondYield1  = b_t[j][k];
				double bondYield1  = b_t[j][k] / (12.0 * dt) ;
				
//				log.info("aaaa : {},{},{},{},{},{}", j, k, b_t[j][k-1], prevIntRate, bondYield, currIntRate - prevIntRate);
				
				rstList.add(BizStockSce.builder().baseYymm(bssd)
										   .applBizDv(bizDv)
										   .stdAsstCd(stdAsstCd)
										   .sceNo(sceNo)
										   .matCd(matCd)
										   .asstYield(bondYield1)
										   .lastModifiedBy("ESG:"+ bondYield1)
										   .lastUpdateDate(LocalDateTime.now())
										   .build()
							);
			}
		}
		log.info("BizStockSce : {},{}", batch, rstList.size());
		return rstList;
	}
	
	private void createHullWhiteCont() {
		for(int j=1 ; j <= sceNum ; j++) {
			shortRate[j][0] = contFwdRate[1];
			shortRate[j][1] = contFwdRate[1];
			pv[j][0] 		= 1.0;
			pv[j][1] 		= Math.exp(-1.0* contFwdRate[1] * dt);
			
			X_t[j][0] 		= 0.0;
			
//			for(int k =1 ; k < j; k++) {
//				X_t[j][k] 		= X_t[j][k-1] + Math.exp(cumSpeed[k]) * sigma[k] * Math.sqrt(dt) * randomNum[j][k];
////				log.info("aaaaaaaaaaaaaaaaaa: {},{},{}", X_t[j][k], shortRate[j][k], pv[j][k]);
//			}
			
			for(int k =1 ; k < tenorSize; k++) {
				X_t[j][k] 		= X_t[j][k-1] + Math.exp(cumSpeed[k]) * sigma[k] * Math.sqrt(dt) * randomNum[j][k];
				shortRate[j][k] = Math.exp(-1.0 * cumSpeed[k]) * ( contFwdRate[1] + drift_t[k] + X_t[j][k] );
				pv[j][k] 		= pv[j][k-1] * Math.exp(-1.0 * shortRate[j][k]* dt);
//				log.info("aaaaaaaaaaaaaaaaaa: {},{},{},{},{}", j,k, X_t[j][k], shortRate[j][k], pv[j][k]);
			}
		}

	}
	
	
//	private void createHullWhite() {
//		for(int j=1 ; j <= sceNum ; j++) {
//			shortRate[j][0] = contFwdRate[1];
//			shortRate[j][1] = contFwdRate[1];
//			pv[j][0] 		= 1.0;
//			pv[j][1] 		= Math.exp(-1.0* contFwdRate[1] * dt);
//			for(int k =2 ; k<tenorSize; k++) {
////				shortRate[j][k] = shortRate[j][k-1] + (theta[k] - speed[k]* shortRate[j][k-1])*dt + sigma[k]*Math.sqrt((1-Math.exp(-2*speed[k]*dt))/(2*speed[k]))*randomNum[j][k];
//				shortRate[j][k] = shortRate[j][k-1] + (theta[k] - speed[k]* shortRate[j][k-1])*dt + sigma[k]*Math.sqrt(dt)*randomNum[j][k];
//				pv[j][k] = pv[j][k-1] * Math.exp(-1.0 * shortRate[j][k]* dt);
//				
////				log.info("random : {},{},{},{},{},{},{}", j,k, randomNum[j][k], shortRate[j][k], pv[j][k], Math.pow(pv[j][k], -1.0 / (dt *k))-1, Math.exp(shortRate[j][k])- 1);
//			}
//		}
////		log.info(" construct  hull white");
//	}
	
	private void initParam() {
		cumSpeed[0]		= 0.0;
		drift_t[0]		= 0.0;
		theta[0] 		= 0.0;
		contFwdRate[0]	= 0.0;
		
		for(int k= 1; k < tenorSize; k++) {
//			cumSpeed[k] = cumSpeed[k-1] +  ( speed[k-1] + speed[k] )*dt / 2.0 ;			//integral for speed(u) 
			cumSpeed[k] = cumSpeed[k-1] + speed[k] * dt;
		}
		
		for(int k= 1; k < tenorSize-1; k++) {
//			theta[k] = (contFwdRate[k] - contFwdRate[k-1]) /dt + speed[k]*contFwdRate[k] + (sigma[k] * sigma[k])*(1-Math.exp(-2*cumSpeed[k]))/(2*speed[k]);
			
//			theta[k] = ( contFwdRate[k+1] * Math.exp(cumSpeed[k+1]) - contFwdRate[k] * Math.exp(cumSpeed[k]) ) / (Math.exp(cumSpeed[k+1]) * dt);
			theta[k] = ( 2.0* ( contFwdRate[k+1] * Math.exp(cumSpeed[k+1]) - contFwdRate[k] * Math.exp(cumSpeed[k]) ) / dt - Math.exp(cumSpeed[k]) * theta[k-1] ) / Math.exp(cumSpeed[k+1]);
					
			drift_t[k] = drift_t[k-1] + Math.exp(cumSpeed[k])  * theta[k] * dt ;
		}
		
		theta[tenorSize-1] 		= theta[tenorSize-2];
		drift_t[tenorSize-1] 	= drift_t[tenorSize-2];
		sumExpCumSpeed[0]= 0.0;
		
		for(int i= 0; i < tenorSize; i++) {
			double tempSum =0.0;
			for(int j= i; j < i + durationMonth; j++) {
				if(j >= tenorSize) {
					tempSum = tempSum +   Math.exp(-1.0 * cumSpeed[tenorSize-1]) * dt ;											//integral for exp(-k(u))
				}else {
//					log.info("aaaaa : {},{}", i,j);
//					tempSum = tempSum + ( Math.exp(-1.0 * cumSpeed[j]) + Math.exp(-1.0 * cumSpeed[j+1]) ) * dt / 2.0 ;			//integral for exp(-k(u))
					tempSum = tempSum +   Math.exp(-1.0 * cumSpeed[j]) * dt  ;													//integral for exp(-k(u))
				}
			}
			sumExpCumSpeed[i] = tempSum;
		}
	}
	
	private void createBondYield() {
		
		for(int i= 0; i < tenorSize; i++) {
			double tempSum =0.0;
			for(int j=i; j< i+ durationMonth; j++ ) {
				if(j >= tenorSize) {
					tempSum = tempSum +  dt * (  Math.exp(cumSpeed[tenorSize-1]) * theta[tenorSize-1] * sumExpCumSpeed[tenorSize-1]
										- 0.5 *Math.exp(2*cumSpeed[tenorSize-1])* Math.pow(sigma[tenorSize-1] * sumExpCumSpeed[tenorSize-1], 2.0) );
				}else {
					//integral for a_t(t))
					tempSum = tempSum +  dt * (  Math.exp(cumSpeed[j]) * theta[j] * sumExpCumSpeed[j] - 0.5 *Math.exp(2*cumSpeed[j])* Math.pow(sigma[j] * sumExpCumSpeed[j], 2.0) );
//					tempSum = tempSum +  dt * (  Math.exp(cumSpeed[j]) * theta[j] * sumExpCumSpeed[j] - 0.5 *Math.exp(2*cumSpeed[j])* Math.pow(sigma[j] * sumExpCumSpeed[j], 2.0) );
					
				}
//				log.info("kkkk : {},{},{},{},{},{},{},{}",i, j, tempSum, sumExpCumSpeed[j], cumSpeed[j], theta[j], sigma[j]);
			}
//			log.info("qqqqqq : {},{},{},{},{},{},{}", i, cumSpeed[i], theta[i], sigma[i],   sumExpCumSpeed[i], tempSum);
			c_t[i] = Math.exp(cumSpeed[i]) * sumExpCumSpeed[i];
			a_t[i] = tempSum;
//			log.info("qqqqqq : {},{},{},{}",i, c_t[i], a_t[i], tempSum);
		}
		
		for(int s= 1; s <= sceNum; s++) {
			for(int i= 0; i < tenorSize; i++) {
				b_t[s][i] = Math.exp(-1.0 * shortRate[s][i] * c_t[i] - a_t[i]);
//				log.info("zzzz1 : {},{},{},{},{},{},{}", s,i, shortRate[s][i], c_t[i], a_t[i], b_t[s][i]);
				
			}
		}
	}
	
	
	private void createBondYield1() {
		for(int i= 0; i < tenorSize; i++) {
			double tempSum =0.0;
			for(int j=i; j< i+ durationMonth; j++ ) {
				if(j >= tenorSize) {
					tempSum = tempSum +  dt * (  Math.exp(cumSpeed[tenorSize-1]) * theta[tenorSize-1] * sumExpCumSpeed[tenorSize-1]
										- 0.5 *Math.exp(2*cumSpeed[tenorSize-1])* Math.pow(sigma[tenorSize-1] * sumExpCumSpeed[tenorSize-1], 2.0) );
				}else {
					//integral for a_t(t))
					tempSum = tempSum +  dt * (  Math.exp(cumSpeed[j]) * theta[j] * sumExpCumSpeed[j] - 0.5 *Math.exp(2*cumSpeed[j])* Math.pow(sigma[j] * sumExpCumSpeed[j], 2.0) );
//					tempSum = tempSum +  dt * (  Math.exp(cumSpeed[j]) * theta[j] * sumExpCumSpeed[j] - 0.5 *Math.exp(2*cumSpeed[j])* Math.pow(sigma[j] * sumExpCumSpeed[j], 2.0) );
					
				}
//				log.info("kkkk : {},{},{},{},{},{},{},{}",i, j, tempSum, sumExpCumSpeed[j], cumSpeed[j], theta[j], sigma[j]);
			}
//			log.info("qqqqqq : {},{},{},{},{},{},{}", i, cumSpeed[i], theta[i], sigma[i],   sumExpCumSpeed[i], tempSum);
			c_t[i] = Math.exp(cumSpeed[i]) * sumExpCumSpeed[i];
			a_t[i] = tempSum;
//			log.info("qqqqqq : {},{},{},{}",i, c_t[i], a_t[i], tempSum);
		}
		
		for(int s= 1; s <= sceNum; s++) {
			b_t[s][0] = contFwdRate[0] *dt;
			for(int i= 1; i < tenorSize; i++) {
				b_t[s][i] = contFwdRate[i] *dt - sigma[i] * c_t[i] * Math.sqrt(dt) * randomNum[s][i];
			}
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
