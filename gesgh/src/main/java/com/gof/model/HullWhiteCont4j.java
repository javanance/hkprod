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
public class HullWhiteCont4j implements Rrunnable {

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
	private double[] a_0t;
	private double[] c_0t;
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
	
	

	public HullWhiteCont4j() {
	
	}

	public HullWhiteCont4j(String baseYymm, List<BizDiscountRate> curveHisList, List<BizEsgParam> param,  double ufr, double ufrt, int batchNum, double durationYear) {
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
		this.c_0t 		 	= new double[tenorSize];
		this.a_0t 		 	= new double[tenorSize];
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
//			speed[i] =   0.001;
//			sigma[i] =   0.005;
			
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
		
//		initParam();
//		initParam1();
//		createHullWhiteCont();
		
		initParamFlat();
		createHullWhiteContFlat();
//		createHullWhiteContFlat1();
	}
	
	public List<IrSce> getIrScenario(String bssd, String irCurveId, String irModelId){
		List<IrSce> rstList = new ArrayList<IrSce>();
		
		for(int j=1 ; j <= sceNum; j++) {
			for(int k =1 ; k < tenorSize; k++) {
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
			for(int k =1 ; k < tenorSize; k++) {
				String matCd ="M"+ String.format("%04d", k);
				String sceNo =String.valueOf(j);
				
//				log.info("qqqqqqq1 : {},{},{},{}", j,k, Math.exp(shortRate[j][k])- 1, Math.pow(pv[j][k], -1.0 / (dt *k))-1 );
//				log.info("qqqqqqq2 : {},{},{},{}", j, k, pv[j][k], shortRate[j][k]);
				
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
				
				double bondYield   = Math.pow(1+ currIntRate, dt)-1  - dt * durationMonth * ( currIntRate - prevIntRate);
				double bondYield1  = Math.pow(1+ currIntRate, dt)-1 ;
				
//				log.info("aaaa : {},{},{},{},{},{}", j, k, b_t[j][k-1], prevIntRate, bondYield, currIntRate - prevIntRate);
				
				rstList.add(BizStockSce.builder().baseYymm(bssd)
										   .applBizDv(bizDv)
										   .stdAsstCd(stdAsstCd)
										   .sceNo(sceNo)
										   .matCd(matCd)
										   .asstYield(bondYield1)
										   .lastModifiedBy("ESG:"+ bondYield)
										   .lastUpdateDate(LocalDateTime.now())
										   .build()
							);
			}
		}
		log.info("BizStockSce : {},{}", batch, rstList.size());
		return rstList;
	}
	
	private void initParam() {
		double[] a_0t_prime1	 	= new double[tenorSize];
		double[] a_0t_prime2	 	= new double[tenorSize];
		double[] a_0t_prime3	 	= new double[tenorSize];
		
		cumSpeed[0]		= 0.0;
		drift_t[0]		= 0.0;
		theta[0] 		= contFwdRate[1];
		contFwdRate[0]	= 0.0;
		c_0t[0]			= 0.0;
		a_0t[0]			= 0.0;
		a_0t_prime1[0]  = 0.0;
		a_0t_prime2[0]  = 0.0;
		a_0t_prime3[0]  = 0.0;
		double temp =0.0;
		double r0 = contFwdRate[1];
		
		for(int k= 1; k < tenorSize; k++) {
//			cumSpeed[k] = cumSpeed[k-1] +  ( speed[k-1] + speed[k] )*dt / 2.0 ;			//integral for speed(u) 
			cumSpeed[k] = cumSpeed[k-1] + speed[k] * dt;
			c_0t[k] = c_0t[k-1]+ Math.exp(-1.0 *cumSpeed[k]);
			a_0t[k] = Math.log(1+ intRate[k]) * k * dt  - r0 * c_0t[k];
			
			
//				a_0t_prime1[k] = Math.exp(cumSpeed[k]) + ( a_0t[k]-a_0t[k-1] ) / dt;
//				a_0t_prime2[k] = Math.exp(cumSpeed[k]) + ( a_0t_prime1[k]-a_0t_prime1[k-1] ) / dt;
//				a_0t_prime3[k] = ( a_0t_prime2[k]-a_0t_prime2[k-1] ) / dt;
		}
		
		a_0t_prime1[1] = Math.exp(cumSpeed[1]) + ( a_0t[1]-a_0t[0] ) / dt;
		a_0t_prime2[1] = Math.exp(cumSpeed[1]) + ( a_0t_prime1[1]-a_0t_prime1[0] ) / dt;
		a_0t_prime3[1] = ( a_0t_prime2[1]-a_0t_prime2[0] ) / dt;

		for(int k= 2; k < tenorSize; k++) {
			a_0t_prime1[k] = Math.exp(cumSpeed[k]) + ( a_0t[k]- 2.0 * a_0t[k-1] + a_0t[k-2]) / ( 2.0 * dt);
			a_0t_prime2[k] = Math.exp(cumSpeed[k]) + ( a_0t_prime1[k]- 2.0 * a_0t_prime1[k-1] + a_0t_prime1[k-2]) /(2.0 * dt);
			a_0t_prime3[k] = ( a_0t_prime2[k]-2.0* a_0t_prime2[k-1] + a_0t_prime2[k-2]) / ( 2.0* dt) ;
		}
		
		for(int k= 1; k < tenorSize-1; k++) {

			temp = ( a_0t_prime3[k] + Math.exp(2 * cumSpeed[k-1]) * sigma[k-1]* sigma[k-1] - 2 * theta[k-1] * speed[k-1] * Math.exp(2 * cumSpeed[k-1])) / Math.exp(2 * cumSpeed[k-1]);
			theta[k] = dt * ( temp + theta[k-1]);
			
			theta[k] = (contFwdRate[k] - contFwdRate[k-1]) /dt + speed[k]*contFwdRate[k] + (sigma[k] * sigma[k])*(1-Math.exp(-2*cumSpeed[k]))/(2*speed[k]);
			
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

		
	private void initParam1() {
		double[] a_0t_prime1	 	= new double[tenorSize];
		double[] a_0t_prime2	 	= new double[tenorSize];
		double[] a_0t_prime3	 	= new double[tenorSize];
		
		cumSpeed[0]		= 0.0;
		drift_t[0]		= 0.0;
		theta[0] 		= contFwdRate[1];
		contFwdRate[0]	= 0.0;
		c_0t[0]			= 0.0;
		a_0t[0]			= 0.0;
		a_0t_prime1[0]  = 0.0;
		a_0t_prime2[0]  = 0.0;
		a_0t_prime3[0]  = 0.0;
		double temp =0.0;
		double r0 = contFwdRate[1];
		
		for(int k= 1; k < tenorSize; k++) {
//			cumSpeed[k] = cumSpeed[k-1] +  ( speed[k-1] + speed[k] )*dt / 2.0 ;			//integral for speed(u) 
			cumSpeed[k] = cumSpeed[k-1] + speed[k] * dt;
			c_0t[k] = c_0t[k-1]+ Math.exp(-1.0 *cumSpeed[k]);
			a_0t[k] = Math.log(1+ intRate[k]) * k * dt  - r0 * c_0t[k];
			
//			log.info("zzzz : {},{},{}", cumSpeed[k], speed[k]* dt * k);
		}
		
		
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
		
		for(int k= 1; k < tenorSize-1; k++) {

//			theta[k] = (contFwdRate[k] - contFwdRate[k-1]) /dt + speed[k]*contFwdRate[k] + (sigma[k] * sigma[k])*(1-Math.exp(-2*cumSpeed[k]))/(2*speed[k]);
			theta[k] = ( contFwdRate[k+1] * Math.exp(cumSpeed[k+1]) - contFwdRate[k] * Math.exp(cumSpeed[k]) ) / (Math.exp(cumSpeed[k+1]) * dt);

//			theta[k] = ( 2.0* ( contFwdRate[k+1] * Math.exp(cumSpeed[k+1]) - contFwdRate[k] * Math.exp(cumSpeed[k]) ) / dt - Math.exp(cumSpeed[k]) * theta[k-1] ) / Math.exp(cumSpeed[k+1]);
			
//			temp = ( a_0t_prime3[k] + Math.exp(2 * cumSpeed[k-1]) * sigma[k-1]* sigma[k-1] - 2 * theta[k-1] * speed[k-1] * Math.exp(2 * cumSpeed[k-1])) / Math.exp(2 * cumSpeed[k-1]);
//			theta[k] = dt * ( temp + theta[k-1]);
			
			
			drift_t[k] = drift_t[k-1] + Math.exp(cumSpeed[k])  * theta[k] * dt ;
		}
		
		theta[tenorSize-1] 		= theta[tenorSize-2];
		drift_t[tenorSize-1] 	= drift_t[tenorSize-2];
	}

		
	private void initParamFlat() {
		double[] a_0t_prime1	 	= new double[tenorSize];
		double[] a_0t_prime2	 	= new double[tenorSize];
		double[] a_0t_prime3	 	= new double[tenorSize];
		
		cumSpeed[0]		= 0.0;
		drift_t[0]		= 0.0;
		theta[0] 		= contFwdRate[1];
		contFwdRate[0]	= 0.0;
		c_0t[0]			= 0.0;
		a_0t[0]			= 0.0;
		a_0t_prime1[0]  = 0.0;
		a_0t_prime2[0]  = 0.0;
		a_0t_prime3[0]  = 0.0;
		double temp =0.0;
		double r0 = contFwdRate[1];
		
		for(int k= 1; k < tenorSize; k++) {
//			cumSpeed[k] = cumSpeed[k-1] +  ( speed[k-1] + speed[k] )*dt / 2.0 ;			//integral for speed(u) 
			cumSpeed[k] = cumSpeed[k-1] + speed[k] * dt;
			c_0t[k] = c_0t[k-1]+ Math.exp(-1.0 *cumSpeed[k]);
			a_0t[k] = Math.log(1+ intRate[k]) * k * dt  - r0 * c_0t[k];
			
//				log.info("zzzz : {},{},{}", cumSpeed[k], speed[k]* dt * k);
//				log.info("zzzz : {},{},{},{}", k, intRate[k], fwdRate[k], contFwdRate[k]);
		}
		
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
		
		for(int k= 1; k < tenorSize-1; k++) {

			theta[k] = (contFwdRate[k+1] - contFwdRate[k]) /dt + speed[k]*contFwdRate[k] + (sigma[k] * sigma[k])*(1-Math.exp(-2*cumSpeed[k]))/(2*speed[k]);
//			theta[k] = (contFwdRate[k] - contFwdRate[k-1]) /dt + speed[k]*contFwdRate[k] + (sigma[k] * sigma[k])*(1-Math.exp(-2*cumSpeed[k]))/(2*speed[k]);
//			theta[k] = speed[k]*contFwdRate[k] + (sigma[k] * sigma[k])*(1-Math.exp(-2*cumSpeed[k]))/(2*speed[k]);
//			theta[k] = speed[k]*contFwdRate[k] + (sigma[k] * sigma[k])*(1-Math.exp(-2*cumSpeed[k]))/(2*1);
			
//			theta[k] = ( contFwdRate[k+1] * Math.exp(cumSpeed[k+1]) - contFwdRate[k] * Math.exp(cumSpeed[k]) ) / (Math.exp(cumSpeed[k+1]) * dt);
			
//			temp = ( a_0t_prime3[k] + Math.exp(2 * cumSpeed[k-1]) * sigma[k-1]* sigma[k-1] - 2 * theta[k-1] * speed[k-1] * Math.exp(2 * cumSpeed[k-1])) / Math.exp(2 * cumSpeed[k-1]);
//			theta[k] = dt * ( temp + theta[k-1]);
			
			drift_t[k] = drift_t[k-1] + Math.exp(cumSpeed[k])  * theta[k] * dt ;
		}
		
		theta[tenorSize-1] 		= theta[tenorSize-2];
		drift_t[tenorSize-1] 	= drift_t[tenorSize-2];
	}
		
	private void createHullWhiteContFlat() {
		for(int k =2 ; k < tenorSize; k++) {
			
			double tempSum =0.0;
			double avgPv =0.0;
			for(int j=1 ; j <= sceNum ; j++) {
				shortRate[j][0] = contFwdRate[1];
				shortRate[j][1] = contFwdRate[1];
				pv[j][0] 		= 1.0;
				pv[j][1] 		= Math.exp(-1.0* contFwdRate[1] * dt);
				
//				shortRate[j][k] = shortRate[j][k-1] + dt * ( theta[k] - speed[k] * shortRate[j][k-1]) + sigma[k] * Math.sqrt(dt) * randomNum[j][k];
				
				shortRate[j][k] = shortRate[j][k-1] + dt * ( theta[k-1] - speed[k-1] * shortRate[j][k-1]) + sigma[k-1] * Math.sqrt(dt) * randomNum[j][k];
				
				avgPv = avgPv + Math.exp(-1.0 * shortRate[j][k]* dt);
//				log.info("aaaaaaaaaaaaaaaaaa: {},{},{},{},{}", j,k, X_t[j][k], shortRate[j][k], pv[j][k]);
			}
			
			avgPv = avgPv / sceNum;
			double adj = -1.0 * Math.log(Math.exp(-1.0 * contFwdRate[k] * dt) / avgPv ) / dt;
			for(int j=1 ; j <= sceNum ; j++) {
				shortRate[j][k] = shortRate[j][k-1] + adj;
				pv[j][k] 		= pv[j][k-1] * Math.exp(-1.0 * shortRate[j][k]* dt);
			}
		}
		
		double fwdPv = 1.0;
		double[] avgPv = new double[tenorSize];
		for(int k = 1 ; k< tenorSize; k++) {
			double avg = 0.0;
			
			for(int j =1 ; j<=sceNum; j++) {
				avg = avg + shortRate[j][k];
				if(k==1) {
//						log.info("aaaaa : {},{},{}", j, avgPv , pv[j][1]);
				}
				avgPv[k] = avgPv[k] + pv[j][k];
				
				fwdPv = fwdPv * Math.exp(-1.0 * contFwdRate[k] * dt );
			}
			avgPv[k] = avgPv[k] / sceNum;
			double qq = Math.pow(avgPv[k] / avgPv[k-1], -1.0 * 12.0) - 1.0;
//			log.info("aaaaa : {},{},{}", k, avg/1000.0, Math.pow(1+ intRate[k], -k/12.0));
//			log.info("aaaaa : {},{},{},{}", k, avg/1000.0, contFwdRate[k], Math.pow(1+ intRate[k], -k/12.0));
			log.info("aaaaa : {},{},{},{},{},{},{}", k, avg/sceNum, fwdRate[k], contFwdRate[k], qq, fwdPv, Math.pow(1+ intRate[k], -k/12.0));
			
		}
		log.info("aaa");
		
	}
	private void createHullWhiteContFlat1() {
		for(int j=1 ; j <= sceNum ; j++) {
			shortRate[j][0] = contFwdRate[1];
			shortRate[j][1] = contFwdRate[1];
//			double shift[] = new double[tenorSize];
			pv[j][0] 		= 1.0;
			pv[j][1] 		= Math.exp(-1.0* contFwdRate[1] * dt);
			
			double shift =0.0;
			double tempSum =0.0;
			for(int k =1 ; k < tenorSize-1; k++) {
				
				shift  =  contFwdRate[k] + Math.pow( sigma[k] * (1- Math.exp(-1.0 * speed[k] * k * dt) ) / speed[k], 2) / 2.0; 
				
				shortRate[j][k+1] = shortRate[j][k] - speed[k] * shortRate[j][k]*dt  + sigma[k-1] * Math.sqrt(dt) * randomNum[j][k] + shift;
//				shortRate[j][k+1] =  - speed[k] * shortRate[j][k]*dt  + sigma[k-1] * Math.sqrt(dt) * randomNum[j][k] + shift;
				
				pv[j][k] 		= pv[j][k-1] * Math.exp(-1.0 * shortRate[j][k]* dt);
				log.info("aaaaaaaaaaaaaaaaaa: {},{},{},{},{}", j,k, shift, sigma[k-1] * Math.sqrt(dt) * randomNum[j][k], shortRate[j][k+1]);
			}
		}
		
		for(int k = 1 ; k< tenorSize; k++) {
			double avg = 0.0;
			
			for(int j =1 ; j< sceNum; j++) {
//				shortRate[j][k] = shortRate[j][k] + contFwdRate[k] + Math.pow( sigma[k] * (1- Math.exp(-1.0 * speed[k] * k * dt) ) / speed[k], 2) / 2.0; 
				avg = avg + shortRate[j][k];
//				avg = avg + pv[j][k];
			}
//			log.info("aaaaa : {},{},{}", k, avg/1000.0, Math.pow(1+ intRate[k], -k/12.0));
//			log.info("aaaaa : {},{},{},{}", k, avg/sceNum, contFwdRate[k], Math.pow(1+ intRate[k], -k/12.0));
			
		}
		log.info("aaa");
	}
	
	private void createHullWhiteCont() {
		for(int j=1 ; j <= sceNum ; j++) {
			shortRate[j][0] = contFwdRate[1];
			shortRate[j][1] = contFwdRate[1];
			pv[j][0] 		= 1.0;
			pv[j][1] 		= Math.exp(-1.0* contFwdRate[1] * dt);
			
			X_t[j][0] 		= 0.0;
			double tempSum =0.0;
			
			for(int k =2 ; k < tenorSize; k++) {
//				for(int z =1 ; z <= k; z++) {
//					tempSum		= tempSum + Math.exp(cumSpeed[z]) * sigma[z] * Math.sqrt(dt) * gen.nextNormalizedDouble();
//				}
//				X_t[j][k] 		= tempSum;
				X_t[j][k] 		= X_t[j][k-1] + Math.exp(cumSpeed[k]) * sigma[k] * Math.sqrt(dt) * randomNum[j][k];
				
				shortRate[j][k] = Math.exp(-1.0 * cumSpeed[k]) * ( contFwdRate[1] + drift_t[k] + X_t[j][k] );
				pv[j][k] 		= pv[j][k-1] * Math.exp(-1.0 * shortRate[j][k]* dt);
				
//				pv[j][k] 		= Math.exp(-1.0 * shortRate[j][k]* dt);
//				log.info("aaaaaaaaaaaaaaaaaa: {},{},{},{},{}", j,k, X_t[j][k], shortRate[j][k], pv[j][k]);
			}
		}
		
		double fwdPv = 1.0;
		for(int k = 1 ; k< tenorSize; k++) {
			double avg = 0.0;
			double avgPv = 0.0;
			
			for(int j =1 ; j< sceNum; j++) {
				avg = avg + shortRate[j][k];
				if(k==1) {
//					log.info("aaaaa : {},{},{}", j, avgPv , pv[j][1]);
				}
				avgPv = avgPv + pv[j][k];
			}
			
			fwdPv = fwdPv * Math.exp(-1.0 * contFwdRate[k] * dt );
//			log.info("aaaaa : {},{},{}", k, avg/1000.0, Math.pow(1+ intRate[k], -k/12.0));
			log.info("aaaaa : {},{},{},{},{},{},{}", k, avg/( sceNum-1), fwdRate[k], contFwdRate[k], avgPv/( sceNum-1), fwdPv, Math.pow(1+ intRate[k], -k/12.0));
		}
		
		log.info("aaa");
	}
	
	private double[][] createRandom(int sceNum, int projectionTerm) {
		int genSceNum =  (sceNum - 1)/2;
		double[][] random = new double[sceNum][projectionTerm];
		
//		GaussianRandomGenerator gen = new GaussianRandomGenerator(new MersenneTwister(Long.valueOf(baseYymm)));
		GaussianRandomGenerator gen = new GaussianRandomGenerator(new MersenneTwister(10));
		
		for(int i =1 ; i<= genSceNum ; i++) {
			for(int j=1 ; j< projectionTerm; j++) {
				double genNum = gen.nextNormalizedDouble();
//				log.info("rnadom1 :  {},{},{}",i, j );
				random[2*i-1][j] = genNum;
				random[2*i][j]   = -1.0 * genNum;
			}
		}
//		double avg=0.0;
//		double std=0.0;
//		
//		for(int j=1 ; j< projectionTerm; j++) {
//			avg =0.0;
//			std =0.0;
//			for( int i = 1 ; i<sceNum; i++) {
//				avg = avg + random[i][j] ;
//				std = std + random[i][j] * random[i][j];
//			}
//			log.info("rnadom :  {},{},{}",j,  avg/sceNum, std / sceNum);
//		}
		return random;
		
	}
}
