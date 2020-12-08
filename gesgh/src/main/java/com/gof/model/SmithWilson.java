package com.gof.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import com.gof.entity.IrCurveHis;
import com.gof.entity.IrmodelResult;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class SmithWilson extends Irmodel {
	
	private final static int              NegativeDirection = -1;
	private final static int              PositiveDirection =  1;	
	                                      
	private int                           prjYear;
	private int                           prjInterval       = 1;
	private char                          prjTimeUnit       = TIME_UNIT_MONTH;
	private LocalDate[]                   prjDate;
	private double[]                      prjYearFrac;	                         
	                                      
	private double                        ltfr;
	private double                        ltfrCont;
	private int                           ltfrT;    
	private final static double           ltfrEpsilon       = 0.0001;
	private double                        irateAdj;
	                                      
	private double                        alphaMaxInit      = 1.000;
	private double                        alphaMinInit      = 0.001;
	private int                           alphaItrNum       = 20;
	private double                        lastLiquidPoint   = 20;
	                                                        
	private boolean                       initializeFlag    = false;
	private int                           dirApproach       = 0;
	private double                        alphaApplied      = 0.0;
	private double                        kappaApplied      = 0.0;
	private int                           dirAlphaApplied   = 0;
	private double                        extendApplied     = 0.0;
	private double                        alphaMaxApplied   = 0.0;
	private double                        alphaMinApplied   = 0.0;
	                                      
	private List<Double>                  alphaList         = new ArrayList<Double>();
	private List<Double>                  kappaList         = new ArrayList<Double>();
	private List<Integer>                 dirAlphaList      = new ArrayList<Integer>();
	private List<Double>                  extendList        = new ArrayList<Double>();
	private List<Double>                  alphaMaxList      = new ArrayList<Double>();
	private List<Double>                  alphaMinList      = new ArrayList<Double>();
	                                      
	private RealMatrix                    zetaColumn;
	
	
	public static void main(String[] args) throws Exception {
		
		double[] testTenor = new double[] {0.25, 0.50, 0.75, 1.00, 1.50, 2.00, 2.50, 3.00, 4.00, 5.00, 7.00, 10.0, 20.0};
		String[] testMatCd = new String[] {"M0003", "M0006", "M0009", "M0012", "M0018", "M0024", "M0030", "M0036", "M0048", "M0060", "M0084", "M0120", "M0240"};
		double[] testRate  = new double[] {1.52, 1.66, 1.79, 1.87, 2.01, 2.09, 2.15, 2.15, 2.31, 2.38, 2.44, 2.46, 2.44};   //FY2017 RF
//		double[] testRate  = new double[] {0.0152, 0.0166, 0.0179, 0.0187, 0.0201, 0.0209, 0.0215, 0.0215, 0.0231, 0.0238, 0.0244, 0.0246, 0.0244};   //FY2017 RF
		
//		Map<Double, Double> ts = new TreeMap<Double, Double>();		
//		for(int i=0; i<testTenor.length; i++) ts.put(testTenor[i],  testRate[i]);		
//		
//		SmithWilson sw = new SmithWilson(LocalDate.of(2017, 12, 31), ts, CMPD_MTD_DISC, false, 4.5, 60, 100, 1);
		
		List<IrCurveHis> curveList = new ArrayList<IrCurveHis>();		
		for(int i=0; i<testTenor.length; i++) {
			IrCurveHis curve = new IrCurveHis();			
			curve.setMatCd(testMatCd[i]);
			curve.setIntRate(testRate[i]);
			curveList.add(curve);
		}				
		SmithWilson sw = new SmithWilson(LocalDate.of(2017, 12, 31), curveList, CMPD_MTD_DISC, false, 4.5, 60, 100, 1);		
		
//		sw.getIrmodelResultMap().entrySet().stream().filter(s-> Double.parseDouble(s.getKey().substring(1, 5)) <=   12).forEach(s->log.info("{}, {}", s.getKey(), s.getValue().getSpotDisc()));
//		sw.getIrmodelResultMap().entrySet().stream().filter(s-> Double.parseDouble(s.getKey().substring(1, 5)) >= 1190).forEach(s->log.info("{}, {}, {}", s.getKey(), s.getValue().getSpotDisc(), s.getValue().getFwdDisc()));
//		log.info("{}", sw.getAlphaApplied());		
		sw.getIrmodelResultList().stream().filter(s-> Double.parseDouble(s.getMatCd().substring(1, 5)) <=   12)
		                         .forEach(s->log.info("{}, {}, {}, {}", s.getBaseDate(), s.getResultType(), s.getMatCd(), s.getSpotDisc(), s.getFwdDisc()));
	}	

	//TODO; 
	public SmithWilson(LocalDate baseDate, List<IrCurveHis> irCurveHisList,                boolean isRealNumber, double ltfr, int ltfrT, int prjYear) {
		this(baseDate, irCurveHisList, CMPD_MTD_DISC, isRealNumber, ltfr, ltfrT, prjYear, 20, 1);		
	}	

	public SmithWilson(LocalDate baseDate, List<IrCurveHis> irCurveHisList, char cmpdType, boolean isRealNumber, double ltfr, int ltfrT, int prjYear, int dayCountBasis) {
		this(baseDate, irCurveHisList, cmpdType     , isRealNumber, ltfr, ltfrT, prjYear, 20, dayCountBasis);		
	}
	
	public SmithWilson(LocalDate baseDate, List<IrCurveHis> irCurveHisList, char cmpdType, boolean isRealNumber, double ltfr, int ltfrT, int prjYear, int alphaItrNum, int dayCountBasis) {				
		super();		
		this.baseDate = baseDate;		
		this.setTermStructureBase(irCurveHisList);
		this.setLastLiquidPoint(this.tenor[this.tenor.length-1]);
		this.cmpdType = cmpdType;
		this.isRealNumber = isRealNumber;
		this.ltfr = ltfr;
		this.ltfrT = ltfrT;
		this.prjYear = prjYear;		
		this.alphaItrNum = alphaItrNum;
		this.dayCountBasis = dayCountBasis;
	}		
	
	public SmithWilson(LocalDate baseDate, Map<Double, Double> termStructure,                boolean isRealNumber, double ltfr, int ltfrT, int prjYear) {
		this(baseDate, termStructure,  CMPD_MTD_DISC, isRealNumber, ltfr, ltfrT, prjYear, 20, 1);		
	}

	public SmithWilson(LocalDate baseDate, Map<Double, Double> termStructure, char cmpdType, boolean isRealNumber, double ltfr, int ltfrT, int prjYear, int dayCountBasis) {
		this(baseDate, termStructure,  cmpdType     , isRealNumber, ltfr, ltfrT, prjYear, 20, dayCountBasis);		
	}	
	
	public SmithWilson(LocalDate baseDate, Map<Double, Double> termStructure, char cmpdType, boolean isRealNumber, double ltfr, int ltfrT, int prjYear, int alphaItrNum, int dayCountBasis) {
		super();		
		this.baseDate = baseDate;		
		this.setTermStructureBase(termStructure);
		this.setLastLiquidPoint(this.tenor[this.tenor.length-1]);
		this.cmpdType = cmpdType;
		this.isRealNumber = isRealNumber;
		this.ltfr = ltfr;
		this.ltfrT = ltfrT;
		this.prjYear = prjYear;		
		this.alphaItrNum = alphaItrNum;
		this.dayCountBasis = dayCountBasis;
	}
	

	public List<IrmodelResult> getIrmodelResultList() {
		
		setIrmodelAttributes();
		setProjectionTenor();			
		this.resultList.addAll(swProjectionList(smithWilsonAlphaFinding()));
		
		return this.resultList;
	}
	
	
	public Map<String, IrmodelResult> getIrmodelResultMap() {
		
		if(this.termStructureBase == null || this.termStructureBase.isEmpty()) {
			log.error("Term Structure is null");			
		}
		else {
			setIrmodelAttributes();
			setProjectionTenor();			
			this.resultMap.putAll(swProjectionMap(smithWilsonAlphaFinding()));			
		}		
		return this.resultMap;
	}
	
	
	private void setIrmodelAttributes() {
		
		tenorDate          = new LocalDate[this.tenor.length];
		tenorYearFrac      = new double[this.tenor.length];
		int yearToMonth    = (this.timeUnit == TIME_UNIT_YEAR) ? 12 : 1;
		
		double toRealScale = this.isRealNumber ? 1 : 0.01;
		this.ltfrCont = (this.cmpdType == CMPD_MTD_DISC) ? irDiscToCont(toRealScale*this.ltfr) : toRealScale*this.ltfr;				
		
		for(int i=0; i<this.tenor.length; i++) {
			
			this.tenorDate[i] = baseDate.plusMonths((long) Math.round(this.tenor[i]*yearToMonth));
			this.tenorYearFrac[i] = getTimeFactor(baseDate,  tenorDate[i],  this.dayCountBasis);
			this.iRateBase[i] = (this.cmpdType == CMPD_MTD_DISC) ? irDiscToCont(toRealScale*this.iRateBase[i]) : toRealScale*this.iRateBase[i];
		}		
//		log.info("{}, {}, {}", this.tenorDate, this.tenorYearFrac, this.iRate);
	}
	
	
	private void setProjectionTenor() {
		
		int monthToYear = (this.prjTimeUnit == TIME_UNIT_MONTH) ? 12 : 1;
		int yearToMonth = (this.prjTimeUnit == TIME_UNIT_YEAR)  ? 12 : 1;
		int prjNum      = this.prjYear * monthToYear / ((this.prjInterval > 0) ? this.prjInterval : 1);
		
		prjDate      = new LocalDate[prjNum];
		prjYearFrac  = new double[prjNum];
		
		for(int i=0; i<this.prjDate.length; i++) {
			
			prjDate[i] = baseDate.plusMonths((long) Math.round((i+1) * this.prjInterval * yearToMonth));
			prjYearFrac[i] = getTimeFactor(baseDate,  prjDate[i],  this.dayCountBasis);
//			log.info("prjYearFrac: {}", prjYearFrac[i]);
		}		
	}
	
	
	private double smithWilsonAlphaFinding() {
		
		for(int i=0; i<=this.alphaItrNum; i++) {
			
			//Initialization Process(for i=0)
			if(i==0) {
				this.alphaMaxApplied = this.alphaMaxInit;
				this.alphaMinApplied = this.alphaMinInit;	
			}
			
			//Alpha Finding Process (i=1 to ItrNum)
			else {
				if(this.dirAlphaApplied == NegativeDirection) {
					this.alphaMaxApplied = this.alphaMaxApplied - this.extendApplied;
				}
				else {
					this.alphaMinApplied = this.alphaMinApplied + this.extendApplied;
					if(this.dirAlphaApplied != PositiveDirection) log.warn("Check the Direction in Smith-Wilson Alpha Finding Process");
				}
			}			
			
			this.alphaApplied  = round(0.5 * (alphaMaxApplied + alphaMinApplied), this.decimalDigit);
			this.extendApplied = round(0.5 * (alphaMaxApplied - alphaMinApplied), this.decimalDigit);
			smithWilsonZeta(this.alphaApplied);
			
			alphaList.add(this.alphaApplied);
			kappaList.add(round(this.kappaApplied));
			dirAlphaList.add(this.dirAlphaApplied);
			extendList.add(this.extendApplied);
			alphaMaxList.add(round(this.alphaMaxApplied));
			alphaMinList.add(round(this.alphaMinApplied));			
		}
//		log.info("alphaOpt: {}", round(0.5 * (alphaMaxApplied + alphaMinApplied), this.decimalDigit));
		
		return round(0.5 * (alphaMaxApplied + alphaMinApplied), 6);		
	}
	
	
	private void smithWilsonZeta(double alpha) {
		
		RealMatrix tenorCol = MatrixUtils.createColumnRealMatrix(tenorYearFrac);
		RealMatrix weight   = MatrixUtils.createRealMatrix(smithWilsonWeight(tenorYearFrac, tenorYearFrac, alpha, ltfrCont));
		//RealMatrix trsWeight = weight.transpose();
		RealMatrix invWeight = MatrixUtils.inverse(weight);
		
		double[] pVal = new double[tenorYearFrac.length];
		double[] mean = new double[tenorYearFrac.length];
		double[] loss = new double[tenorYearFrac.length];
		double[] sinh = new double[tenorYearFrac.length];
		
		for(int i=0; i<loss.length; i++) {
			pVal[i] = zeroBondUnitPrice(iRateBase[i], tenorYearFrac[i]);
			mean[i] = zeroBondUnitPrice(ltfrCont, tenorYearFrac[i]);
			loss[i] = smithWilsonLoss(iRateBase[i], tenorYearFrac[i], ltfrCont);
			sinh[i] = Math.sinh(alpha *  tenorYearFrac[i]);
		}
		
		RealMatrix lossCol = MatrixUtils.createColumnRealMatrix(loss);
		RealMatrix zetaCol = invWeight.multiply(lossCol);
		
		RealMatrix sinhCol = MatrixUtils.createColumnRealMatrix(sinh);
		RealMatrix qMatDiag = MatrixUtils.createRealDiagonalMatrix(mean);
		
		double kappaNum = tenorCol.transpose().multiply(qMatDiag).multiply(zetaCol).scalarMultiply(alpha).scalarAdd(1.0).getEntry(0,0);
		double kappaDenom = sinhCol.transpose().multiply(qMatDiag).multiply(zetaCol).getEntry(0,0);
		this.kappaApplied = kappaNum / (Math.abs(kappaDenom) < ZERO_DOUBLE ? 1.0 : kappaDenom);
		
		if(!this.initializeFlag) {
			this.initializeFlag = true;
			this.dirApproach = (int) Math.signum(alpha / (1 - this.kappaApplied * Math.exp(alpha*lastLiquidPoint) ) );
		}
		
		this.dirAlphaApplied = (int) Math.signum(this.dirApproach * alpha / (1 - this.kappaApplied * Math.exp(alpha*ltfrT) ) - ltfrEpsilon);
		this.zetaColumn = zetaCol;		
	}
	
	
	private List<IrmodelResult> swProjectionList(double alpha) {
		
		List<IrmodelResult> irResultlList = new ArrayList<IrmodelResult>();
		smithWilsonZeta(alpha);
		
		double[] df = new double[prjYearFrac.length];
		for(int i=0; i<df.length; i++) df[i] = zeroBondUnitPrice(ltfrCont,  prjYearFrac[i]);
		
		RealMatrix weightPrjTenor = MatrixUtils.createRealMatrix(smithWilsonWeight(prjYearFrac, tenorYearFrac, alpha, ltfrCont));
		
		RealMatrix dfCol = MatrixUtils.createColumnRealMatrix(df);
		RealMatrix sigmaCol = weightPrjTenor.multiply(this.zetaColumn);
		RealMatrix priceCol = dfCol.add(sigmaCol);
		
		double[] spotCont = new double[prjYearFrac.length];
		double[] fwdCont  = new double[prjYearFrac.length];
		
		for(int i=0; i<this.prjYearFrac.length; i++) {
			spotCont[i] = -1.0 / prjYearFrac[i] * Math.log(priceCol.getEntry(i,0));
			fwdCont[i]  = (i > 0) ? (spotCont[i] * prjYearFrac[i] - spotCont[i-1] * prjYearFrac[i-1]) / (prjYearFrac[i] - prjYearFrac[i-1]) : spotCont[i];
			
			IrmodelResult irResult = new IrmodelResult();
			
			irResult.setBaseDate(baseDate.toString());
			irResult.setResultType("Smith-Wilson");
			irResult.setScenType("1");
			irResult.setMatCd(String.format("%s%04d",  this.prjTimeUnit, i+1));
//			irResult.setMatTerm(round(prjYearFrac[i]));
//			irResult.setMatDate(prjDate[i]);
			irResult.setSpotCont(round(spotCont[i]));
			irResult.setFwdCont(round(fwdCont[i]));
			irResult.setSpotDisc(round(irContToDisc(spotCont[i])));
			irResult.setFwdDisc(round(irContToDisc(fwdCont[i])));			
			
			irResultlList.add(irResult);
		}
		return irResultlList;
	}	
	
	
	private Map<String, IrmodelResult> swProjectionMap(double alpha) {
		
		Map<String, IrmodelResult> swModelMap = new TreeMap<String, IrmodelResult>();
		smithWilsonZeta(alpha);
		
		double[] df = new double[prjYearFrac.length];
		for(int i=0; i<df.length; i++) df[i] = zeroBondUnitPrice(ltfrCont,  prjYearFrac[i]);
		
		RealMatrix weightPrjTenor = MatrixUtils.createRealMatrix(smithWilsonWeight(prjYearFrac, tenorYearFrac, alpha, ltfrCont));
		
		RealMatrix dfCol = MatrixUtils.createColumnRealMatrix(df);
		RealMatrix sigmaCol = weightPrjTenor.multiply(this.zetaColumn);
		RealMatrix priceCol = dfCol.add(sigmaCol);
		
		double[] spotCont = new double[prjYearFrac.length];
		double[] fwdCont  = new double[prjYearFrac.length];
		
		for(int i=0; i<this.prjYearFrac.length; i++) {
			spotCont[i] = -1.0 / prjYearFrac[i] * Math.log(priceCol.getEntry(i,0));
			fwdCont[i]  = (i > 0) ? (spotCont[i] * prjYearFrac[i] - spotCont[i-1] * prjYearFrac[i-1]) / (prjYearFrac[i] - prjYearFrac[i-1]) : spotCont[i];
			
			IrmodelResult irResult = new IrmodelResult();
			
			irResult.setBaseDate(baseDate.toString());
			irResult.setResultType("Smith-Wilson");
			irResult.setScenType("1");
			irResult.setMatCd(String.format("%s%04d",  this.prjTimeUnit, i+1));
//			irResult.setMatTerm(round(prjYearFrac[i]));
//			irResult.setMatDate(prjDate[i]);
			irResult.setSpotCont(round(spotCont[i]));
			irResult.setFwdCont(round(fwdCont[i]));
			irResult.setSpotDisc(round(irContToDisc(spotCont[i])));
			irResult.setFwdDisc(round(irContToDisc(fwdCont[i])));			
			
			swModelMap.put(String.format("%s%04d",  this.prjTimeUnit, i+1),  irResult);
		}
		return swModelMap;
	}
	
	
	private double[][] smithWilsonWeight(double[] prjYearFrac, double[] tenorYearFrac, double alpha, double ltfrCont) {
		
		double[][] weight = new double[prjYearFrac.length][tenorYearFrac.length];
		double min, max;
		
		for(int i=0; i<prjYearFrac.length; i++) {
			for(int j=0; j<tenorYearFrac.length; j++) {
				
				min = Math.min(prjYearFrac[i], tenorYearFrac[j]);
				max = Math.max(prjYearFrac[i], tenorYearFrac[j]);
				//weight[i][j] = Math.exp(-ltfrCont * (prjYearFrac[i] + tenorYearFrac[j])) * (alpha * min - 0.5 * Math.exp(-alpha*max) * (Math.exp(alpha*min) - Math.exp(-alpha*min)));
				weight[i][j] = Math.exp(-ltfrCont * (prjYearFrac[i] + tenorYearFrac[j])) * (alpha * min - Math.exp(-alpha*max) * Math.sinh(alpha*min));				
			}
		}
		return weight;
	}
	
		
	private double smithWilsonLoss(double rateCont, double mat, double ltfrCont) {
		return zeroBondUnitPrice(rateCont, mat) - zeroBondUnitPrice(ltfrCont, mat);
	}
	
	
//	public void paramToString() {
//		log.info("BaseDate: {}, ItrNum: {}, Alpha: {}, TimeUnit: {}, CONT/DISK: {}, DCB: {}, RateReal: {}, PrjYear: {}, LTFR: {}, LTFR Term: {}"
//				              , this.baseDate
//				              , this.alphaItrNum
//				              , this.alphaApplied
//				              , this.timeUnit
//				              , this.cmpdType
//				              , this.dayCountBasis
//				              , this.isRealNumber
//				              , this.prjYear
//				              , this.ltfr
//				              , this.ltfrT
//				              );
//		
//	}
	
}

