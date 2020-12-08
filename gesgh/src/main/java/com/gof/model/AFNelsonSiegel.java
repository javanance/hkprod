package com.gof.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.AbstractSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariateOptimizer;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;

import com.gof.entity.IrCurveHis;
import com.gof.entity.IrShock;
import com.gof.entity.IrShockParam;
import com.gof.entity.IrShockSce;
import com.gof.entity.IrmodelResult;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class AFNelsonSiegel extends Irmodel {		
	
	protected String        mode;
	protected double[]      inputParas;
	protected double[]      initParas;
	protected double[]      optParas;
	protected double[]      optLSC;	                        
		
	protected SimpleMatrix  IntShock;
	protected String[]      IntShockName;
	
	protected int           nf;
	protected double        dt; 
	protected double        accuracy;
	protected int           itrMax;
	protected double        confInterval;	
	protected double        initSigma;
	                        
	protected double        ltfrL;
	protected double        ltfrA;
	protected int           ltfrT;
	protected double        liqPrem;
	protected double        term;	                        
	protected double        minLambda;
	protected double        maxLambda;	
	protected int           prjYear;
	                        
	protected double[]      coeffLt;
	protected double[]      coeffSt;
	protected double[]      coeffCt;
	protected double[]      residue;
	                        
	protected double        lambda;
	protected double        thetaL;
	protected double        thetaS;
	protected double        thetaC;	
	protected double        kappaL;
	protected double        kappaS;
	protected double        kappaC;	
	protected double        epsilon;
	
	protected List<IrShockSce> rsltList = new ArrayList<IrShockSce>();
	
		
	public AFNelsonSiegel(LocalDate baseDate, List<IrCurveHis> iRateHisList, List<IrCurveHis> iRateBaseList, double dt, double initSigma) {				
		this(baseDate, "AFNS", null, iRateHisList, iRateBaseList, false       , CMPD_MTD_DISC, dt, initSigma, DCB_ACT_365, 0.045, 0.045, 60  , 0.0032  , 1.0/12, 0.05, 2.0, 3, 140    , 1e-10, 100, 0.995, 0.001);		
	}	
	
	public AFNelsonSiegel(LocalDate baseDate, String mode, List<IrCurveHis> iRateHisList, List<IrCurveHis> iRateBaseList, boolean isRealNumber, char cmpdType, double dt, double initSigma, 
			              double ltfrL, double ltfrA, int ltfrT, double liqPrem, int prjYear) {		
		this(baseDate, mode  , null, iRateHisList, iRateBaseList, isRealNumber, cmpdType     , dt, initSigma, DCB_ACT_365, ltfrL, ltfrA, ltfrT, liqPrem, 1.0/12, 0.05, 2.0, 3, prjYear, 1e-10, 100, 0.995, 0.001);		
	}	

	public AFNelsonSiegel(LocalDate baseDate, String mode, double[] inputParas, List<IrCurveHis> iRateHisList, List<IrCurveHis> iRateBaseList, boolean isRealNumber, char cmpdType, double dt, double initSigma, int dayCountBasis,
		                  double ltfrL, double ltfrA, int ltfrT, double liqPrem, double term, double minLambda, double maxLambda, int nf, int prjYear, double accuracy, int itrMax, double confInterval, double epsilon) {		
		
		this.baseDate      = baseDate;		
		this.mode          = mode;
		this.inputParas    = inputParas;		
		this.setTermStructureHis(iRateHisList, iRateBaseList);
		this.isRealNumber  = isRealNumber;
		this.cmpdType      = cmpdType;		
		this.dt            = dt;	
		this.initSigma     = initSigma;
		this.dayCountBasis = dayCountBasis;
		this.ltfrL         = ltfrL;
		this.ltfrA         = ltfrA;
		this.ltfrT         = ltfrT;
		this.liqPrem       = liqPrem;
		this.term          = term;
		this.minLambda     = minLambda;
		this.maxLambda     = maxLambda;
		this.nf            = nf;
		this.prjYear       = prjYear;
		this.accuracy      = accuracy;
		this.itrMax        = itrMax;
		this.confInterval  = confInterval;
		this.epsilon       = epsilon;
	}
	

	//TODO:
	public void setTermStructureHis(List<IrCurveHis> iRateHisList, List<IrCurveHis> iRateBaseList) {		
				
		Map<String, Map<String, Double>> tsHisArg = new TreeMap<String, Map<String, Double>>();		
		tsHisArg = iRateHisList.stream().collect(Collectors.groupingBy(s -> s.getBaseDate(), TreeMap::new, Collectors.toMap(IrCurveHis::getMatCd, IrCurveHis::getIntRate)));
		this.setTermStructureHis(tsHisArg, iRateBaseList);		
		
//		iRateHisList.stream().filter(s -> Double.parseDouble(s.getMatCd().substring(1, 5)) <= 12 ).forEach(s -> log.info("{}, {}, {}", s.getBaseDate(), s.getMatCd(), s.getIntRate()));
	}	
	

	private void setTermStructureHis(Map<String, Map<String, Double>> iRateHisMap, List<IrCurveHis> iRateBaseList) {			

		Map<String, Map<Double, Double>> tsHis    = new TreeMap<String, Map<Double, Double>>();
		
		for(Map.Entry<String, Map<String, Double>> hisArg : iRateHisMap.entrySet()) {					
			Map<Double, Double> ts = new TreeMap<Double, Double>();			
			
			for(Map.Entry<String, Double> arg : hisArg.getValue().entrySet()) {						
				ts.put(Double.parseDouble(arg.getKey().substring(1,5)) / MONTH_IN_YEAR, arg.getValue());				
				tsHis.put(hisArg.getKey(), ts);
			}
		}		
		this.irCurveId = iRateBaseList.get(0).getIrCurveId();
		setTermStructureBase(iRateBaseList);		
		setTermStructureHis(tsHis, this.termStructureBase);		
	}		

	
	private void setTermStructureHis(Map<String, Map<Double, Double>> termStructureHis, Map<Double, Double> termStructureBase) {
		
		this.termStructureHis   = termStructureHis;
		this.termStructureBase  = termStructureBase;			
		int numObs              = termStructureHis.keySet().size();
		int numTenor            = ((TreeMap<String, Map<Double, Double>>) termStructureHis).firstEntry().getValue().size();
		                        
		this.iRateDateHis       = new LocalDate[numObs];			
		this.iRateHis           = new double   [numObs][numTenor];			
		this.tenor              = new double   [numTenor];
		this.iRateBase          = new double   [numTenor];			
		
		int tau = 0;
		for(Map.Entry<Double, Double> base : termStructureBase.entrySet()) {
			this.tenor[tau]     = base.getKey();				
			this.iRateBase[tau] = base.getValue();
			tau++;
		}
		
		int obs = 0;			
		for(Map.Entry<String, Map<Double, Double>> ts : termStructureHis.entrySet()) {				
			int mat = 0;				
			this.iRateDateHis[obs] = stringToDate(ts.getKey());
			
			for(Map.Entry<Double, Double> pts : ts.getValue().entrySet()) {					
				this.iRateHis[obs][mat] = pts.getValue();
				mat++;				
			}
			obs++;
		}
	}
	
	
	public void setTermStructureHis(String[] date, double[] tenor, double[][] iRateHis, double[] iRateBase) {
		
		int numObs        = date .length;
		int numTenor      = tenor.length;
		
		this.iRateDateHis = new LocalDate[numObs];		
		this.iRateHis     = new double   [numObs][numTenor];
		this.tenor        = tenor;
		this.iRateBase    = iRateBase;		
		
		for(int i=0; i<numObs; i++) {					
			this.iRateDateHis[i] = stringToDate(date[i]);			
			for(int j=0; j<numTenor; j++) this.iRateHis[i][j] = iRateHis[i][j];
		}	
	}		

	
	public List<IrShockParam> getAfnsParamList() {

		List<IrShockParam> paramList = new ArrayList<IrShockParam>();
		
		String[] optParaNames = new String[] {"LAMBDA"  , "THETA_1" , "THETA_2" , "THETA_3" , "KAPPA_1" , "KAPPA_2" , "KAPPA_3" , 
				                              "SIGMA_11", "SIGMA_21", "SIGMA_22", "SIGMA_31", "SIGMA_32", "SIGMA_33", "EPSILON" };		
        String[] optLSCNames  = new String[] {"L0", "S0", "C0"};        
				
		if(this.optParas != null && this.optLSC != null) {			
			
			for(int i=0; i<this.optParas.length; i++) {
				
				IrShockParam param = new IrShockParam();
				param.setBaseYymm(dateToString(this.baseDate).substring(0,6));
				param.setIrShockTyp(this.mode);
				param.setIrCurveId(this.irCurveId);				
				param.setParamTypCd(optParaNames[i]);
				param.setParamVal(optParas[i]);
				param.setLastModifiedBy("ESG");
				param.setLastUpdateDate(LocalDateTime.now());				
				paramList.add(param);
			}
			
			for(int i=0; i<this.optLSC.length; i++) {

				IrShockParam param = new IrShockParam();
				param.setBaseYymm(dateToString(this.baseDate).substring(0,6));
				param.setIrShockTyp(this.mode);
				param.setIrCurveId(this.irCurveId);				
				param.setParamTypCd(optLSCNames[i]);
				param.setParamVal(optLSC[i]);
				param.setLastModifiedBy("ESG");
				param.setLastUpdateDate(LocalDateTime.now());				
				paramList.add(param);				
			}
		}
		return paramList;
	}
	
	
	public List<IrShock> getAfnsShockList() {		
		
		List<IrShock> shockList = new ArrayList<IrShock>();			
        
		if(this.IntShock != null) {			
			
			for(int i=0; i<this.IntShock.numCols(); i++) {
				for(int j=0; j<this.IntShock.numRows(); j++) {
					
					IrShock shock = new IrShock();
					shock.setBaseYymm(dateToString(this.baseDate).substring(0,6));
					shock.setIrShockTyp(this.mode);
					shock.setIrCurveId(this.irCurveId);				
					shock.setShockTypCd(this.IntShockName[i]);
					shock.setMatCd(String.format("%s%04d", 'M', (int) round(this.tenor[j] * MONTH_IN_YEAR, 0)));
					//shock.setMatCd(String.valueOf((int) round(this.tenor[j] * MONTH_IN_YEAR, 0) ));
					shock.setShockVal(this.IntShock.get(j,i));
					shock.setLastModifiedBy("ESG");
					shock.setLastUpdateDate(LocalDateTime.now());				
					shockList.add(shock);			
				}
			}			
		}
		return shockList;
	}
	

	public List<IrShockSce> getAfnsResultList() {

//		this.inputParas = new double[] {   // JAVA DNS
//				  0.47616903890804885
//				  , 0.03359240577156057
//				  , -0.017727177222447068
//				  , 0.0052010476166935125
//				  , 0.10108455380215074
//				  , 0.3835600867519039
//				  , 0.30911298636085494
//				  , 0.0061779793087011516
//				  , -0.005882429491797533
//				  , 0.0030903458308824414
//				  , 9.78032246045241E-4
//				  , -4.097899522242011E-4
//				  , 0.010729307098797226
//				  , 0.3160995199327225
//		};
//		//this.optParas = this.inputParas;
//		
//		this.optLSC = new double[] {
//				  0.024716768598674374
//				  , -0.010274616958204577
//				  , 0.009894981910181386   };		

		// Initializing AFNS Parameter
		initializeAfnsParas();
		
		// Determine this.initParas
		if(this.inputParas != null) this.initParas = this.inputParas;                       		
			
		// To set this.optParas, this.optLSC
		kalmanFiltering();                  
		
		// To set this.IntShock
		afnsShockGenerating();		
		
		// Applying Smith-Wilson inter - extrapolation for Asset, Insurance
		this.rsltList.addAll(applySmithWilsonInterpoloation(this.ltfrA,  0.0         ,  "A"));		
		this.rsltList.addAll(applySmithWilsonInterpoloation(this.ltfrL,  this.liqPrem,  "L"));		
		
//		this.rsltList.stream().filter(s -> Double.parseDouble(s.getMatCd().substring(1, 5)) <= 12 ).forEach(s -> log.info("{}, {}, {}", s));
		
		
		return this.rsltList;
	}


	private void initializeAfnsParas() {	
		
		setIrmodelAttributes();
		findInitialLambda();
		findInitailThetaKappa();		
		
		this.initParas = new double[14];
		
		this.initParas[0]  = this.lambda;
		this.initParas[1]  = this.thetaL;  this.initParas[2]  = this.thetaS;  this.initParas[3]  = this.thetaC;
		this.initParas[4]  = Math.max(this.kappaL, 1e-4); 
		this.initParas[5]  = Math.max(this.kappaS, 1e-4);		
		this.initParas[6]  = Math.max(this.kappaC, 1e-4);		
		this.initParas[7]  = this.initSigma; this.initParas[8]  = 0.0; this.initParas[9]  = this.initSigma;
		this.initParas[10] = 0.0;            this.initParas[11] = 0.0; this.initParas[12] = this.initSigma;
		this.initParas[13] = this.epsilon * 1000;
	}	
	

	private void setIrmodelAttributes() {		

		double toRealScale = this.isRealNumber ? 1 : 0.01;		
		
		for(int i=0; i<this.iRateHis.length; i++) {
			for(int j=0; j<this.iRateHis[i].length; j++) {		
				this.iRateHis[i][j] = (this.cmpdType == CMPD_MTD_DISC) ? irDiscToCont(toRealScale*this.iRateHis[i][j]) : toRealScale*this.iRateHis[i][j];				
			}
		}	
		
		for(int j=0; j<this.iRateBase.length; j++) {
			this.iRateBase[j] = (this.cmpdType == CMPD_MTD_DISC) ? irDiscToCont(toRealScale*this.iRateBase[j]) : toRealScale*this.iRateBase[j];
		}
		
		coeffLt = new double[this.iRateHis.length];
		coeffSt = new double[this.iRateHis.length];
		coeffCt = new double[this.iRateHis.length];
		residue = new double[this.iRateHis.length];
	}		
		

	protected void findInitialLambda() {
		
		UnivariateFunction fp = new UnivariateFunction() {
			public double value(double lambda) {
				return residualSumOfSquares(lambda);
			}
		};
		
		UnivariateOptimizer optimizer = new BrentOptimizer(1e-10, 1e-14);
		this.lambda = optimizer.optimize(new MaxEval(10000)
				                       , new UnivariateObjectiveFunction(fp)
				                       , GoalType.MINIMIZE
				                       , new SearchInterval(this.minLambda, this.maxLambda)).getPoint();		
	}
	
	
	private double residualSumOfSquares(double lambda) {
		
		double residualSum = 0.0;		
		double[][] xArray = factorLoad(lambda, this.tenor, false);		

		for(int i=0; i<this.iRateHis.length; i++) {		

			double[] yArray = this.iRateHis[i];			
			
			OLSMultipleLinearRegression reg = new OLSMultipleLinearRegression();
			reg.newSampleData(yArray, xArray);				
			
			double[] rslt = reg.estimateRegressionParameters();			
			coeffLt[i] = rslt[0]; coeffSt[i] = rslt[1]; coeffCt[i] = rslt[2]; residue[i] = reg.calculateResidualSumOfSquares();			
			
			residualSum += residue[i];
		}
		return residualSum;		
	}	
	
	
	private void findInitailThetaKappa() {	
		
		SimpleRegression linRegL = new SimpleRegression(true);
		SimpleRegression linRegS = new SimpleRegression(true);
		SimpleRegression linRegC = new SimpleRegression(true);		
				
		for(int i=0; i<coeffLt.length-1; i++) {
			
			linRegL.addData(coeffLt[i], coeffLt[i+1]);
			linRegS.addData(coeffSt[i], coeffSt[i+1]);
			linRegC.addData(coeffCt[i], coeffCt[i+1]);			
		}				
		
		this.thetaL = linRegL.getIntercept() / (1.0 - linRegL.getSlope());
		this.thetaS = linRegS.getIntercept() / (1.0 - linRegS.getSlope());
		this.thetaC = linRegC.getIntercept() / (1.0 - linRegC.getSlope());
		
		this.kappaL = -Math.log(linRegL.getSlope()) / this.dt;
		this.kappaS = -Math.log(linRegS.getSlope()) / this.dt;
		this.kappaC = -Math.log(linRegC.getSlope()) / this.dt;		
	}		
	

	private void kalmanFiltering() {		
		kalmanFiltering(this.initParas);
	}
	

	private void kalmanFiltering(double[] paras) {
		
		MultivariateFunction fp = new MultivariateFunction() {			
			public double value(double[] inputParas) {
				return logLikelihood(inputParas);
			}
		};			
		
		double[] preParas = paras;
		double   preValue = 0.0;
		
		log.info("{}, {}, {}", LocalDateTime.now(), paras);		
		try {			
			for(int i=0; i<this.itrMax; i++) {		
				
				SimplexOptimizer optimizer = new SimplexOptimizer(1e-12, 1e-12);
				AbstractSimplex  ndsimplex = new NelderMeadSimplex(nelderMeadStep(preParas, 0.001));
				PointValuePair   result    = optimizer.optimize(new MaxEval(100000)
						                                      , new ObjectiveFunction(fp)
						                                      , ndsimplex
						                                      , GoalType.MINIMIZE
						                                      , new InitialGuess(preParas));

				log.info("{}, {}, {}, {}", i, result.getValue(), LocalDateTime.now(), result.getPoint());			

				if(Math.abs(result.getValue() - preValue) < this.accuracy) break;	
				preParas   = constraints(result.getPoint());
				preValue   = result.getValue();				
			}
			
			this.optParas = preParas;			
			this.optLSC   = new double[] {this.coeffLt[this.iRateHis.length-1], this.coeffSt[this.iRateHis.length-1], this.coeffCt[this.iRateHis.length-1]};
		}
		catch (Exception e) {
			log.error("Error in finding Kalman Gain in AFNS module");
			e.printStackTrace();
		}		
		log.info("{}, {}, {}", LocalDateTime.now(), this.optLSC, this.optParas);
	}		
	

    /**
     * TODO: 
     * Currently,  To set init sigma as 1.0e-6 is too small...
     * [Sigma, Step] = [0.05, e-5] ok, However [0.001, E-5] failed, [0.001, E-6] is ok but unstable !
     * Mode: AFNS, Sigma: 0.001, dt: 0.003968253968253968, stepMin: 1.0E-5, -6, -7 failed!! -> initSigma is not to be set 0.001
     * Mode: AFNS, Sigma: 0.05,  dt: 0.003968253968253968, stepMin: 1.0E-5 Success(only),  if -6 then Failed!!		
     */	
	private double[] nelderMeadStep(double[] inputParas, double scale) {
		
		double[] step = new double[inputParas.length];
		for(int i=0; i<step.length; i++) {
			step[i] = Math.max(Math.abs(inputParas[i] * scale), SIMPLEX_STEP_MIN);
		}
//		log.info("step: {}", step);
		return step;		
	}
	
	
	private double logLikelihood(double[] inputParas) {
		
		double[] paras = constraints(inputParas).clone();		
		
		double       Lambda = paras[0];
		SimpleMatrix Theta  = new SimpleMatrix(vecToMat(new double[] {paras[1], paras[2], paras[3]}));
		SimpleMatrix Kappa  = new SimpleMatrix(toDiagMatrix(paras[4], paras[5], paras[6]));
		SimpleMatrix Sigma  = new SimpleMatrix(toLowerTriangular3(new double[] {paras[7], paras[8], paras[9], paras[10], paras[11], paras[12]}));				
		
		SimpleMatrix H      = new SimpleMatrix(toDiagMatrix(Math.pow((paras[13] * 0.001), 2), this.tenor.length));
		SimpleMatrix B      = new SimpleMatrix(factorLoad(Lambda, this.tenor, true));		
		SimpleMatrix C      = new SimpleMatrix(vecToMat(afnsC(Sigma, Lambda, this.tenor)));
		
		// Conditional and Unconditional covariance matrix : Q, Q0
		SimpleEVD<SimpleMatrix> eig    = new SimpleMatrix(Kappa).eig();		
		
		//if there is zero eigenvalue then Vmat, Vlim is not consistent
		double []    Eval   = eig.getEigenvalues().stream().map(s -> s.getReal()).mapToDouble(Double::doubleValue).toArray();		
		SimpleMatrix Evec   = new SimpleMatrix(eig.getEigenVector(0)).combine(0, 1, eig.getEigenVector(1)).combine(0,  2,  eig.getEigenVector(2));
		SimpleMatrix iEvec  = Evec.invert();
		SimpleMatrix Smat   = iEvec.mult(Sigma).mult(Sigma.transpose()).mult(iEvec.transpose());

		SimpleMatrix Vmat   = new SimpleMatrix(toDiagMatrix(0.0, 0.0, 0.0));
		SimpleMatrix Vlim   = new SimpleMatrix(toDiagMatrix(0.0, 0.0, 0.0));		
		
		for(int i=0; i<Smat.numRows(); i++) {
			for(int j=0; j<Smat.numCols(); j++) {
				Vmat.set(i, j, Smat.get(i,j) * (1.0 - Math.exp(-(Eval[i]+Eval[j])*this.dt)) / (Eval[i] + Eval[j]));
				Vlim.set(i, j, Smat.get(i,j)                                                / (Eval[i] + Eval[j]));
			}
		}
		
		// Analytical Covariance matrix
		SimpleMatrix Q  = Evec.mult(Vmat).mult(Evec.transpose());
		SimpleMatrix Q0 = Evec.mult(Vlim).mult(Evec.transpose());
		
		// Initialization of vector and matrix
		SimpleMatrix PrevX  = Theta;
		SimpleMatrix PrevV  = Q0;			
		SimpleMatrix Phi1   = new SimpleMatrix(toDiagMatrix(Math.exp(-Kappa.get(0,0)*this.dt)
				                                          , Math.exp(-Kappa.get(1,1)*this.dt)
				                                          , Math.exp(-Kappa.get(2,2)*this.dt)));
		SimpleMatrix Phi0   = new SimpleMatrix(toDiagMatrix(1.0, 1.0, 1.0)).minus(Phi1).mult(PrevX);
		
		double logLike = 0.0;		
		
		for(int i=0; i<this.iRateHis.length; i++) {
			
			SimpleMatrix Xhat  = Phi0.plus(Phi1.mult(PrevX));
			SimpleMatrix Vhat  = Phi1.mult(PrevV).mult(Phi1.transpose()).plus(Q);			
			
			// The model-implied yields
			SimpleMatrix Y     = new SimpleMatrix(vecToMat(this.iRateHis[i]));			
			SimpleMatrix Yimp  = B.mult(Xhat).plus(C.scale(mode.equals("AFNS") ? 1 : 0));			
			SimpleMatrix Er    = Y.minus(Yimp);			
			
			// Updating
			SimpleMatrix Ev    = B.mult(Vhat).mult(B.transpose()).plus(H);
		    SimpleMatrix Evinv = Ev.invert();
			SimpleMatrix KG    = Vhat.mult(B.transpose()).mult(Evinv);			
			PrevX              = Xhat.plus(KG.mult(Er));
			PrevV              = Vhat.minus(KG.mult(B).mult(Vhat));
			
			// Log-Likelihood function
			logLike           += - 0.5 * this.tenor.length * Math.log(2 * Math.PI) - 0.5 * Math.log(Ev.determinant()) - 0.5 * Er.transpose().mult(Evinv).dot(Er);
			
			this.coeffLt[i] = PrevX.get(0,0);  this.coeffSt[i] = PrevX.get(1,0);  this.coeffCt[i] = PrevX.get(2,0);
		}		
		return -logLike;
	}
	
	
	private double[] constraints(double[] paras) {		
		
		double[] paraCon = paras.clone();
		
		paraCon[0]  = Math.min(Math.max(paraCon[0] ,  this.minLambda), this.maxLambda);  // 0: lambda, 1/2/3: theta LSC 
		paraCon[4]  = Math.min(Math.max(paraCon[4] ,  1e-4), 100000);                    // 4: kappaL
		paraCon[5]  = Math.min(Math.max(paraCon[5] ,  1e-4), 100000);                    // 5: kappaS
		paraCon[6]  = Math.min(Math.max(paraCon[6] ,  1e-4), 100000);                    // 6: kappaC
		paraCon[7]  = Math.min(Math.max(paraCon[7] ,  0e-4), 1.0000);                    // 7: sigma11		
		paraCon[9]  = Math.min(Math.max(paraCon[9] ,  0e-4), 1.0000);                    // 9: sigma22
		paraCon[12] = Math.min(Math.max(paraCon[12],  0e-4), 1.0000);                    //12: sigma33

		return(paraCon);		
	}	


	private void afnsShockGenerating() {
		
		double       Lambda     = this.optParas[0];
		SimpleMatrix Theta      = new SimpleMatrix(vecToMat(new double[] {this.optParas[1], this.optParas[2], this.optParas[3]}));
		SimpleMatrix Kappa      = new SimpleMatrix(toDiagMatrix(this.optParas[4], this.optParas[5], this.optParas[6]));
		SimpleMatrix Sigma      = new SimpleMatrix(toLowerTriangular3(new double[] {this.optParas[7], this.optParas[8], this.optParas[9], this.optParas[10], this.optParas[11], this.optParas[12]}));
		SimpleMatrix X0         = new SimpleMatrix(vecToMat(new double[] {this.optLSC[0], this.optLSC[1], this.optLSC[2]}));		
		
		
		// AFNS factor loading matrix based on LLP weight
		double[]     tenorLLP   = new double[(int) (Math.round(this.tenor[this.tenor.length-1]))];
		for(int i=0; i<tenorLLP.length; i++) tenorLLP[i] = i+1;
		SimpleMatrix factorLLP  = new SimpleMatrix(factorLoad(Lambda, tenorLLP, true));

		
		// Declare M, N and Calculate NTN | eKappa ~ Kappa^-1 x (I-exp(-Kappa)) x Sigma  |  N ~ W.mat x M  |  NTN ~ t(N) x N
		SimpleMatrix eKappa     = new SimpleMatrix(toDiagMatrix(Math.exp(-Kappa.get(0,0)), Math.exp(-Kappa.get(1,1)), Math.exp(-Kappa.get(2,2))));
		SimpleMatrix IminusK    = new SimpleMatrix(toIdentityMatrix(this.nf)).minus(eKappa);
		SimpleMatrix M          = Kappa.invert().mult(IminusK).mult(Sigma);
		SimpleMatrix N          = new SimpleMatrix(toDiagMatrix(factorLLP.extractMatrix(0, tenorLLP.length, 0, 1).elementSum()
				                                              , factorLLP.extractMatrix(0, tenorLLP.length, 1, 2).elementSum()
				                                              , factorLLP.extractMatrix(0, tenorLLP.length, 2, 3).elementSum())).mult(M);
		SimpleMatrix NTN        = N.transpose().mult(N);		
		
		
		// Eigen Decomposition & get rotation angle
		Map<Integer, List<Double>> eigVec =  eigenValueUserDefined(NTN, 3);		
//		Map<Integer, List<Double>> eigVec =  eigenValueOrigin(NTN, 3);
		SimpleMatrix Me1        = M.mult(new SimpleMatrix(vecToMat(eigVec.get(0).stream().mapToDouble(Double::doubleValue).toArray())));
		SimpleMatrix Me2        = M.mult(new SimpleMatrix(vecToMat(eigVec.get(1).stream().mapToDouble(Double::doubleValue).toArray())));
		SimpleMatrix S1         = factorLLP.mult(Me1);
		SimpleMatrix S2         = factorLLP.mult(Me2);		
		double rotation         = Math.atan(S2.elementSum() / S1.elementSum());
		
		
		// Mean-Reversion, Level and Twist Shock
		SimpleMatrix MeanR      = new SimpleMatrix(toIdentityMatrix(this.nf)).minus(eKappa).mult(Theta.minus(X0));
		SimpleMatrix Level      = new SimpleMatrix(Me1.scale( Math.cos(rotation)).plus(Me2.scale(Math.sin(rotation)))).scale(new NormalDistribution().inverseCumulativeProbability(this.confInterval));
		SimpleMatrix Twist      = new SimpleMatrix(Me1.scale(-Math.sin(rotation)).plus(Me2.scale(Math.cos(rotation)))).scale(new NormalDistribution().inverseCumulativeProbability(this.confInterval));		

		SimpleMatrix CoefInt    = new SimpleMatrix(factorLoad(Lambda, this.tenor, true));		                        		
		SimpleMatrix BaseShock  = CoefInt.mult(new SimpleMatrix(vecToMat(new double[] {0.0, 0.0, 0.0})));
		SimpleMatrix MeanRShock = CoefInt.mult(MeanR);
		SimpleMatrix LevelShock = CoefInt.mult(Level);
		SimpleMatrix TwistShock = CoefInt.mult(Twist);
		
		//TODO: To adjust Shock Scale in case of daily history(only in AFNS)
		double levelScale = LevelShock.get(LevelShock.numRows()-1,0) > ZERO_DOUBLE ? 1.0 : -1.0;
		double twistScale = TwistShock.get(TwistShock.numRows()-1,0) > ZERO_DOUBLE ? 1.0 : -1.0;
						
		this.IntShock           = new SimpleMatrix(BaseShock).concatColumns(MeanRShock)
			                                                 .concatColumns(LevelShock.scale(+levelScale)).concatColumns(LevelShock.scale(-levelScale))
			                                                 .concatColumns(TwistShock.scale(-twistScale)).concatColumns(TwistShock.scale(+twistScale));
//				                                             .concatColumns(TwistShock.scale(+twistScale)).concatColumns(TwistShock.scale(-twistScale));
		
		this.IntShockName       = new String[] {"BASE", "MEAN", "UP", "DOWN", "FLAT", "STEEP"};
	}		
	

	protected List<IrShockSce> applySmithWilsonInterpoloation(double ltfr, double liqPrem, String type) {

		List<IrShockSce> curveList = new ArrayList<IrShockSce>();

		SimpleMatrix spotShock = toSpotShock(this.IntShock, liqPrem);		
//		log.info("{}, {}", this.IntShock, spotShock);
		
		for(int i=0; i<spotShock.numCols(); i++) {
			
			Map<Double, Double> ts = new TreeMap<Double, Double>();
			for(int j=0; j<this.tenor.length; j++) ts.put(this.tenor[j], spotShock.get(j,i));			
			
			SmithWilson sw = new SmithWilson(this.baseDate, ts, CMPD_MTD_CONT, true, irDiscToCont(ltfr), this.ltfrT, this.prjYear, 20, this.dayCountBasis);			
			List<IrmodelResult> swRslt= sw.getIrmodelResultList();
					
			for(IrmodelResult rslt : swRslt) {				
				IrShockSce ir  = new IrShockSce();
				
				ir.setBaseDate(dateToString(this.baseDate).substring(0,6));
				ir.setIrModelId(this.mode);
				ir.setMatCd(rslt.getMatCd());
//				ir.setSceNo(type + String.valueOf(i+1) + "_" + this.IntShockName[i]);
				ir.setSceNo(String.valueOf((type.equals("A") ? 0 : 10) + (i+1)));
				ir.setIrCurveId(this.irCurveId);
				ir.setRfRate(rslt.getSpotDisc());
				ir.setRiskAdjIr(0.0);
				ir.setLastModifiedBy("ESG");
				ir.setLastUpdateDate(LocalDateTime.now());				
				curveList.add(ir);
			}						
//			log.info("scenNo: {}, swAlpha: {}", type + String.valueOf(i+1), sw.getAlphaApplied());
		}		
		return curveList;
	}
		
	
	private SimpleMatrix toSpotShock(SimpleMatrix intShock, double liqPrem) {
		
		SimpleMatrix baseRate = new SimpleMatrix(vecToMat(this.iRateBase));
		SimpleMatrix intBase  = baseRate;
		for(int i=1; i<intShock.numCols(); i++) intBase = intBase.concatColumns(baseRate);				

//		TODO:
		SimpleMatrix spotShock = intBase.plus(intShock).elementExp().plus(liqPrem).elementLog();   // to_cont(to_disc(intBase + intShock) + VA)
//		SimpleMatrix spotShock = intBase.plus(intShock).plus(liqPrem);		
		
		return spotShock;
	}	


	private double[][] factorLoad(double lambda, double[] tau, boolean isfull) {
		
		double[][] fLoadFull =  factorLoad(lambda, tau);		
		if(isfull) return fLoadFull;
		
		double[][] fLoad = new double[tau.length][2];			
		
		for(int i=0; i<fLoad.length; i++) {
			for(int j=0; j<2; j++) fLoad[i][j] = fLoadFull[i][j+1];
//			log.info("{}, {}, {}, {}, {}", lambda, tau[i], fLoad[i][0], fLoad[i][1]);
		}			
		return fLoad;
	}
	

	private double[][] factorLoad(double lambda, double[] tau) {
		
		double[][] fLoad = new double[tau.length][3];                                 // fLoad[i] = [L1, S1, C1], ... , [Ln, Sn, Cn]
		
		for(int i=0; i<fLoad.length; i++) {			
			fLoad[i][0] = 1.0;                                                        // L component in LSC
			fLoad[i][1] = (1.0 - Math.exp(-lambda * tau[i])) / (lambda * tau[i]);     // S component in LSC
			fLoad[i][2] = fLoad[i][1] - Math.exp(-lambda * tau[i]);			          // C component in LSC
//			log.info("{}, {}, {}, {}, {}", lambda, tau[i], fLoad[i][0], fLoad[i][1], fLoad[i][2]);
		}		
		return fLoad;
	}
	
	
	protected static double[] afnsC(SimpleMatrix sigma, double lambda, double[] tau) {
		
		double s11 = sigma.get(0,0),  s12 = sigma.get(0,1),  s13 = sigma.get(0,2);
		double s21 = sigma.get(1,0),  s22 = sigma.get(1,1),  s23 = sigma.get(1,2);
		double s31 = sigma.get(2,0),  s32 = sigma.get(2,1),  s33 = sigma.get(2,2);
				
		double A = s11*s11 + s12*s12 + s13*s13;  double D = s11*s21 + s12*s22 + s13*s23;		
		double B = s21*s21 + s22*s22 + s23*s23;  double E = s11*s31 + s12*s32 + s13*s33;
		double C = s31*s31 + s32*s32 + s33*s33;  double F = s21*s31 + s22*s32 + s23*s33;		
		
		double r1 = 0, r2 = 0, r3 = 0, r4 = 0, r5 = 0, r6 = 0;
		double la = lambda, la2 = Math.pow(lambda, 2), la3 = Math.pow(lambda, 3);
		
		double[] afnsC = new double[tau.length];
		
		for(int i=0; i<tau.length; i++) {			
						
			r1 = -A * tau[i]*tau[i]/6;
			r2 = -B * (  1/(2*la2) - (1-Math.exp(-la*tau[i]))/(la3*tau[i]) + (1-Math.exp(-2*la*tau[i]))/(4*la3*tau[i])  );
			r3 = -C * (  1/(2*la2) + Math.exp(-la*tau[i])/(la2) - tau[i]*Math.exp(-2*la*tau[i])/(4*la) 
			           - 3*Math.exp(-2*la*tau[i])/(4*la2) - 2*(1-Math.exp(-la*tau[i]))/(la3*tau[i])
			           + 5*(1-Math.exp(-2*la*tau[i]))/(8*la3*tau[i])  );
			r4 = -D * (  tau[i]/(2*la) + Math.exp(-la*tau[i])/(la2) - (1-Math.exp(-la*tau[i]))/(la3*tau[i])  );
			r5 = -E * (  3*Math.exp(-la*tau[i])/(la2) + tau[i]/(2*la) + tau[i]*Math.exp(-la*tau[i])/(la) - 3*(1-Math.exp(-la*tau[i]))/(la3*tau[i])  );
			r6 = -F * (  1/(la2) + Math.exp(-la*tau[i])/(la2) - Math.exp(-2*la*tau[i])/(2*la2)
				       - 3*(1-Math.exp(-la*tau[i]))/(la3*tau[i]) + 3*(1-Math.exp(-2*la*tau[i]))/(4*la3*tau[i])  );
			
			afnsC[i] = r1 + r2 + r3 + r4 + r5 + r6;
			//log.info("{}, {}, {}, {}, {}, {}, {}", r1, r2, r3, r4, r5, r6, afnsC[i]);
						
		}		
		return afnsC;		
	}
	
	
	protected static double[] afnsC(double[][] sigma, double lambda, double[] tau) {		
		return afnsC(new SimpleMatrix(sigma), lambda, tau);
	}

	
	protected static double[] nelsonSiegelTermStructure(double lambda, double[] tenor, double[] Lt, double[] St, double[] Ct) {
		
		double[] iRate = new double[tenor.length];
		for(int i=0; i<iRate.length; i++) iRate[i] = nelsonSiegelFn(lambda, tenor[i], Lt[i], St[i], Ct[i]);
		
		return iRate;
	}
	
	
	private static double nelsonSiegelFn(double lambda, double tenor, double Lt, double St, double Ct) {
		return nelsonSiegelFn(lambda, tenor, Lt, St, Ct, 0.0);
	}	
	
	
	private static double nelsonSiegelFn(double lambda, double tenor, double Lt, double St, double Ct, double epsilon) {
		
		double lamTau = lambda *  tenor;
		return Lt * 1.0 + St * ((1 - Math.exp(-lamTau)) / lamTau) + Ct * ((1 - Math.exp(-lamTau)) / lamTau - Math.exp(-lamTau)) + epsilon;
	}
	
	
//	public static void main(String[] args) throws Exception {
//	
//	String     mode        = "AFNS";   // DNS, AFNS
//	char       cmpdType    = 'D';
//	double     sigma       = 0.05;     // 0.001 0.05    
//	int        prjYear     = 140;
//	
////	String  path = "D:/Rproject/esg/irate_2017_full.csv";		
////	double  dt   = 1.0/252;   
////	boolean real = false;
//	
//	String  path = "D:/Rproject/esg/spot_2011_2019.csv";
//	double  dt   = 1.0/12;
//	boolean real = true;
//	
//	String[]   intDate     = getIntDate(path);
//	double[][] intRate     = getIntRate(path);		
//	String[]   matCd       = new String[] {"M0003", "M0006", "M0009", "M0012", "M0018", "M0024", "M0030", "M0036", "M0048", "M0060", "M0084", "M0120", "M0240"};
////	double[]   baseRate    = new double[] {1.52, 1.66, 1.79, 1.87, 2.01, 2.09, 2.15, 2.15, 2.31, 2.38, 2.44, 2.46, 2.44};                             //FY2017 RF
//	double[]   baseRate    = new double[] {0.015151269, 0.014934104, 0.014833336, 0.015013807, 0.014943706, 0.014793234, 0.014843986, 0.014591374, 0.015171678, 0.014995766, 0.015781226, 0.015918747, 0.016253058}; //FY2019 1st HALF 
//			
////	log.info("Mode: {}, Sigma: {}, dt: {}, stepMin: {}", mode, sigma, dt, SIMPLEX_STEP_MIN);
//	AFNelsonSiegel afns = new AFNelsonSiegel(LocalDate.of(2017, 12, 31), mode, setIrCurveHisInt(intDate, matCd, intRate), setIrCurveHisBase("20171231", matCd, baseRate)
//			                              , real, cmpdType, dt, sigma, 0.045, 0.045, 60, 0.0032, prjYear);		
//	
//	afns.getAfnsResultList().stream().filter(s -> Double.parseDouble(s.getMatCd().substring(1, 5)) <= 1 ).forEach(s -> log.info("{}", s.toString()));				
//}
		
	
}
