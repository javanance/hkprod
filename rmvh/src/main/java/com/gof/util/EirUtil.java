package com.gof.util;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.gof.entity.CfLv1Goc;
import com.gof.entity.DfLv2Eir;
import com.gof.entity.DfLv2EirNewgoc;
import com.gof.entity.DfLv4Eir;
import com.gof.enums.EBoolean;
import com.gof.enums.ECompound;
import com.gof.enums.ELiabType;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class EirUtil {

	public static DfLv4Eir createDfLv4Eir(List<CfLv1Goc> cfList, ELiabType liabType, String runsetList, double startRate, double targetOci, double errorTerm, int iterNum,  Function<CfLv1Goc, Double> fun) {
		String gocId ="";
		String bssd ="";
		CfLv1Goc cfLv2Goc = new CfLv1Goc(); 
		if(cfList.size()==0) {
			log.error("Error : cash flow size is zero in Eir Calc"); 
			System.exit(1);
		}else {
			cfLv2Goc = cfList.get(0);
			gocId = cfLv2Goc.getGocId();
			bssd =  cfLv2Goc.getBaseYymm();
		}
		
		double epvAmt = cfList.stream().map(s->s.getEpvAmt()).reduce(0.0, (s,u)->s+u);
		double targetValue = epvAmt - targetOci;
		
		log.info("EIR Calc :  {},{},{}, {}", gocId, epvAmt, targetValue);
		
		Map<String, Double> rstMap = factory(cfList, startRate, targetValue, errorTerm, iterNum, fun);
		
		return DfLv4Eir.builder()
				.baseYymm(bssd)
				.gocId(gocId)
				.liabType(liabType)
				.eirAplyRunset(runsetList)
				.epvAmt(epvAmt)
				.targetOci(targetOci)						//TODO :Check Sign ==> 1.0 to sum of parts...
				.errorAmt(rstMap.get("ERROR_AMT")) 
				.eir(rstMap.get("EIR"))
				.remark(String.valueOf(rstMap.get("DIV") +"_"+ rstMap.get("COUNT")))
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
		
	}

	public static DfLv2Eir createEir(List<CfLv1Goc> cfList, double startRate, double targetOci, double errorTerm, int iterNum,  Function<CfLv1Goc, Double> fun) {
		String gocId ="";
		String bssd ="";
		CfLv1Goc cfLv2Goc = new CfLv1Goc(); 
		if(cfList.size()==0) {
			log.error("Error : cash flow size is zero in Eir Calc"); 
			System.exit(1);
		}else {
			cfLv2Goc = cfList.get(0);
			gocId = cfLv2Goc.getGocId();
			bssd =  cfLv2Goc.getBaseYymm();
		}
		
		double epvAmt = cfList.stream().map(s->s.getEpvAmt()).reduce(0.0, (s,u)->s+u);
		double targetValue = epvAmt - targetOci;
		log.info("EIR Calc :  {},{},{}, {}", gocId, epvAmt, targetValue);
		
		Map<String, Double> rstMap = factory(cfList, startRate, targetValue, errorTerm, iterNum, fun);
		
		return DfLv2Eir.builder()
				.baseYymm(bssd)
				.gocId(gocId)
//				.stStatus(cfLv2Goc.getStStatus())
//				.endStatus(cfLv2Goc.getEndStatus())
//				.newContYn(cfLv2Goc.getNewContYn())
				.newContYn(EBoolean.N)
				.fincEpvAmt(epvAmt)
				.targetRunSysAmt(0.0)
				.targetRunOci(0.0)
				.deltaOci(0.0)
				.fincOci(0.0)
				.targetOci(targetOci)			//TODO :Check Sign ==> 1.0 to sum of parts...
				.errorAmt(rstMap.get("ERROR_AMT")) 
				.eir(rstMap.get("EIR"))
				.remark(String.valueOf(rstMap.get("DIV") +"_"+ rstMap.get("COUNT")))
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
		
	}
	
	public static DfLv2Eir createEir(List<CfLv1Goc> cfList, double startRate, double targetRunSysEpv, double targetRunOci, double deltaOci, double fincOci, double errorTerm, int iterNum,  Function<CfLv1Goc, Double> fun) {
		String gocId ="";
		String bssd ="";
		double targetValue2 = targetRunSysEpv - deltaOci;
		double targetOci    = targetRunOci + deltaOci + fincOci;
		
		CfLv1Goc cfLv2Goc = new CfLv1Goc(); 
		if(cfList.size()==0) {
			log.error("Error : cash flow size is zero in Eir Calc"); 
			System.exit(1);
		}else {
			cfLv2Goc = cfList.get(0);
			gocId = cfLv2Goc.getGocId();
			bssd =  cfLv2Goc.getBaseYymm();
		}
		
		double epvAmt = cfList.stream().map(s->s.getEpvAmt()).reduce(0.0, (s,u)->s+u);
		double targetValue = epvAmt - targetOci;
		log.info("EIR Calc :  {},{},{}, {}", gocId, epvAmt, targetValue2, targetValue);
		
		Map<String, Double> rstMap = factory(cfList, startRate, targetValue, errorTerm, iterNum, fun);
		
		return DfLv2Eir.builder()
				.baseYymm(bssd)
				.gocId(gocId)
//				.stStatus(cfLv2Goc.getStStatus())
//				.endStatus(cfLv2Goc.getEndStatus())
//				.newContYn(cfLv2Goc.getNewContYn())
				.newContYn(EBoolean.N)
				.fincEpvAmt(epvAmt)
				.targetRunSysAmt(targetRunSysEpv)
				.targetRunOci(targetRunOci)
				.deltaOci(deltaOci)
				.fincOci(fincOci)
				.targetOci(1.0* ( epvAmt - targetValue))			//TODO :Check Sign ==> 1.0 to sum of parts...
				.errorAmt(rstMap.get("ERROR_AMT")) 
				.eir(rstMap.get("EIR"))
				.remark(String.valueOf(rstMap.get("DIV") +"_"+ rstMap.get("COUNT")))
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
	}



	public static DfLv2EirNewgoc createEirNewgoc(List<CfLv1Goc> cfList, double startRate, double targetValue, double errorTerm, int iterNum,  Function<CfLv1Goc, Double> fun) {
		String gocId ="";
		String bssd ="";
		CfLv1Goc cfLv2Goc = new CfLv1Goc(); 
		if(cfList.size()==0) {
			log.warn("warning : cash flow size is zero in Ark Eir Calc"); 
		}else {
			cfLv2Goc = cfList.get(0);
			gocId 	 = cfList.get(0).getCsmGrpCd();
			bssd 	 = cfList.get(0).getBaseYymm();
		}
		
		double epvAmt = cfList.stream().map(s->s.getEpvAmt()).reduce(0.0, (s,u)->s+u);
		
		Map<String, Double> rstMap = factory(cfList, startRate, targetValue, errorTerm, iterNum, fun);
		
		return DfLv2EirNewgoc.builder()
				.baseYymm(bssd)
				.gocId(gocId)
				.stStatus(cfLv2Goc.getStStatus())
				.endStatus(cfLv2Goc.getEndStatus())
//				.newContYn(cfLv2Goc.getNewContYn())
//				.stStatus("01")
//				.endStatus("01")
				.newContYn(EBoolean.Y)
				.fincEpvAmt(epvAmt)
				.targetRunSysAmt(targetValue)
				.targetRunOci(0.0)
				.deltaOci(0.0)
				.fincOci(0.0)
				.targetOci(1.0 * (epvAmt - targetValue))			//TODO :Check Sign
				.errorAmt(rstMap.get("ERROR_AMT")) 
				.eir(rstMap.get("EIR"))
				.remark(String.valueOf(rstMap.get("DIV"))+"_"+rstMap.get("COUNT") )
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
	}

	private static double getDfDerivative(double irRate, double timeFactor) {
//		return Math.pow((1+irRate/12), -1 * timeFactor * settleTerm - 1 ) * -1 * timeFactor * settleTerm / 12 ;			monthly
		return -1 * timeFactor *  Math.pow((1+irRate), -1 * timeFactor  - 1 )  ;			// annually;
	}
	
	private static Map<String, Double> factory(List<CfLv1Goc> cfList, double startRate, double targetValue, double errorTerm, int iterNum,  Function<CfLv1Goc, Double> fun) {
		Map<String, Double> rstMap = new HashMap<String, Double>();
		
		double calcValue = 0.0;
		double newRate 	 = 0.0;
		double irRate 	 = startRate;

		int cnt =0;
		boolean cont= true;
		boolean flag =false;
		
		while (cont) {
			cnt =cnt+1;
			newRate   = calcNewEir(cfList, irRate, targetValue, flag, fun);
			calcValue = calcEpv(cfList, newRate, flag, fun);
			
//			if( Math.abs(calcValue-targetValue)< errorTerm ) {
			if( Math.abs(calcValue-targetValue)< errorTerm && Math.abs(newRate) < 0.1) {
				rstMap.put("CALC_VALUE", calcValue);
				rstMap.put("TARGET_VALUE", targetValue);
				rstMap.put("ERROR_AMT", calcValue - targetValue);
				rstMap.put("EIR", newRate);
				rstMap.put("COUNT", (double)cnt);
				rstMap.put("DIV", 0.0);
				cont = false;
				
			}
			else if( cnt > iterNum ) {
				if(!flag) {
					flag = true;
					cnt = 0;
					irRate = -1.0 * startRate;
//					iterNum =20;
					
				}else {
//					TODO : bisection or alternative!!!!
					newRate = calcNewEir(cfList, irRate, targetValue, flag, fun);
					calcValue = calcEpv(cfList, newRate, flag, fun);
					
					rstMap.put("CALC_VALUE", 0.0);
//					rstMap.put("CALC_VALUE", calcValue);
					rstMap.put("TARGET_VALUE", targetValue);
//					rstMap.put("AOCI", calcValue - targetValue);
					rstMap.put("ERROR_AMT", 0.0);
					rstMap.put("EIR", startRate);
//					rstMap.put("EIR", newRate);
					rstMap.put("COUNT", (double)cnt);
					rstMap.put("DIV", -1.0);
					cont =false;
				}
			}
			else {
				irRate = newRate;
			}
		}
		return rstMap;
	}
	
	private static double calcNewEir(List<CfLv1Goc> cfList, double irRate, double targetValue, boolean flag, Function<CfLv1Goc, Double> fun) {
		double timeFactor=0.0;
		double derivativeSum =0.0;
		double dfSum=0.0;
		double newRate =0.0;

		for(CfLv1Goc aa : cfList) {
			timeFactor = fun.apply(aa) / 12.0;
			derivativeSum = derivativeSum + aa.getCfAmt() * getDfDerivative(irRate, timeFactor);
			dfSum = dfSum + aa.getCfAmt()  * ECompound.Annualy.getDf(irRate, timeFactor);
			
//			log.info("RATE0 : {}, {},{},{},{}", aa.getCfMonthNum(),  aa.getCfAmt() , ECompound.Annualy.getDf(irRate, timeFactor), getDfDerivative(irRate, timeFactor));
//			log.info("RATE0 : {}, {},{},{},{}", getDfDerivative1(irRate, timeFactor) , getDfDerivative(irRate, timeFactor));
//			log.info("RATE1 : {}, {},{},{},{},{}", cnt, timeFactor, aa.getCfAmt(), irRate, dfSum, derivativeSum);
		}
		newRate = irRate - ( dfSum- targetValue )/ derivativeSum;
		return newRate;
	}
	
	private static double calcEpv(List<CfLv1Goc> cfList, double eir, boolean flag, Function<CfLv1Goc, Double> fun) {
		double timeFactor=0.0;
		double calcValue =0.0;

		for(CfLv1Goc aa : cfList) {
			timeFactor = fun.apply(aa) / 12.0;
			calcValue = calcValue + aa.getCfAmt() * ECompound.Annualy.getDf(eir, timeFactor);
		}
		return calcValue;
	}
	
	
	private static double getEpv(List<CfLv1Goc> cfList, double irRate){
		double dfSum=0.0;
		double timeFactor=0.0;
		for(CfLv1Goc aa : cfList) {
			timeFactor = aa.getCfMonthNum() / 12.0 ;
			dfSum = dfSum + aa.getCfAmt()  * ECompound.Annualy.getDf(irRate, timeFactor);
		}
		return dfSum;
	}
	
	private static double getEpv(List<CfLv1Goc> cfList, double irRate, Function<CfLv1Goc, Double> fun){
		double dfSum=0.0;
		double timeFactor=0.0;
		for(CfLv1Goc aa : cfList) {
			timeFactor = fun.apply(aa) / 12.0 ;
			dfSum = dfSum + aa.getCfAmt()  * ECompound.Annualy.getDf(irRate, timeFactor);
		}
		return dfSum;
	}
	
	private static double getNewEirBisection(List<CfLv1Goc> cfList, double targetValue) {
		double dfSum=0.0;
		double irRate = 0.0;
		double upperRate = 0.5;
		double lowerRate = -0.5;
		
		if( (getEpv(cfList, upperRate) -targetValue) * (getEpv(cfList, lowerRate)-targetValue) > 0) {
			log.warn("No Eir for given Cf and target Value :{},{},{},{}", cfList.get(0).getGocId(), targetValue, getEpv(cfList, upperRate), getEpv(cfList, lowerRate));
			return 0.0;
		}
		
		for(int i =0 ; i< 10; i++) {
			dfSum = getEpv(cfList, irRate);
			
			if(Math.abs(dfSum - targetValue) < 0.01) {
				return irRate;
			}
			else if( dfSum > targetValue) {
				irRate = ( upperRate + irRate ) / 2.0;
				upperRate =irRate;
			}
			else {
				irRate = ( lowerRate + irRate ) / 2.0;
				lowerRate = irRate;
			}
		}
		return irRate;
	}
}
