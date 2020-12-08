package com.gof.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ejml.data.Complex_F64;
import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;

import com.gof.entity.IrCurveHis;
import com.gof.entity.IrmodelResult;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public abstract class Irmodel {
	
	public static final char              TIME_UNIT_END        = 'E';
	public static final char              TIME_UNIT_YEAR       = 'Y';
	public static final char              TIME_UNIT_MONTH      = 'M';
	public static final char              TIME_UNIT_WEEK       = 'W';
	public static final char              TIME_UNIT_DAY        = 'D';
	                                      
	public static final int               YEAR_IN_YEAR         = 1;
	public static final int               MONTH_IN_YEAR        = 12;
	public static final int               WEEK_IN_YEAR         = 52;
	public static final int               DAY_IN_YEAR          = 252;	
		                                  
	public static final char              CMPD_MTD_SIMP        = 'S';
	public static final char              CMPD_MTD_DISC        = 'D';
	public static final char              CMPD_MTD_CONT        = 'C';
	                                      
	public static final int               DCB_ACT_365          = 1;
	public static final int               DCB_A30_360          = 2;
	public static final int               DCB_E30_360          = 3;
	public static final int               DCB_ACT_ACT          = 4;
	public static final int               DCB_ACT_360          = 5;	
	public static final int               DCB_MON_DIF          = 9;
	                                      
	public static final double            ZERO_DOUBLE          = 0.0000001;	
	public static final double            SIMPLEX_STEP_MIN     = 1e-5;
	
	
	protected LocalDate                   baseDate;
	protected String                      irCurveId;
	protected int                         modelType;
	protected char                        cmpdType             = CMPD_MTD_DISC;
	protected char                        timeUnit             = TIME_UNIT_YEAR;
	protected int                         dayCountBasis        = DCB_ACT_365;
	protected int                         decimalDigit         = 10;	
	protected boolean                     isRealNumber;
	                                      
	protected double[]                    tenor;	
	protected LocalDate[]                 tenorDate;
	protected double[]                    tenorYearFrac;	
	protected double[]                    iRateBase;
	                                      
	protected LocalDate[]                 iRateDateHis;	
	protected double[][]                  iRateHis;	
	
	protected Map<Double, Double>         termStructureBase = new TreeMap<Double, Double>();
	protected Map<String, IrmodelResult>  resultMap         = new TreeMap<String, IrmodelResult>();
	protected List<IrmodelResult>         resultList        = new ArrayList<IrmodelResult>();
	
	protected Map<String, Map<Double, Double>> termStructureHis = new TreeMap<String, Map<Double, Double>>();
	
	public Irmodel() {}	
	
//	public abstract Map<String, IrmodelResult> getIrmodelResultMap() throws Exception;	
//	
//	public abstract List<IrmodelResult> getIrmodelResultList() throws Exception;
	
	public void setTermStructureBase(List<IrCurveHis> irCurveHisList) {
		
//		this.tenor = irCurveHis.stream().map(s -> Double.parseDouble(s.getMatCd().substring(1, 5)) / 12.0).mapToDouble(Double::doubleValue).toArray();
		for(IrCurveHis curve : irCurveHisList) {
			this.termStructureBase.put(Double.parseDouble(curve.getMatCd().substring(1, 5)) / MONTH_IN_YEAR, curve.getIntRate());			
		}
		setTermStructureBase(this.termStructureBase);							
	}
	
	
	public void setTermStructureBase(Map<Double, Double> termStructureBase) {
	
		this.termStructureBase = termStructureBase;
		this.tenor = this.termStructureBase.keySet().stream().mapToDouble(Double::doubleValue).toArray();
		this.iRateBase = this.termStructureBase.values().stream().mapToDouble(Double::doubleValue).toArray();
	}
	
	
	protected double zeroBondUnitPrice(double rateCont, double mat) {
		return Math.exp(-rateCont * mat);
	}
	
	
	protected static double irDiscToCont(double discRate) {
		return Math.log(1.0 + discRate);
	}
	
	
	protected static double[] irDiscToCont(double[] discRate) {
		
		double[] contRate = new double[discRate.length]; 
		
		for(int i=0; i<discRate.length; i++) {
			contRate[i] = irDiscToCont(discRate[i]);
		}
		
		return contRate;
	}
	
	
	protected static double[][] irDiscToCont(double[][] discRate) {
		
		double[][] contRate = new double[discRate.length][discRate[0].length]; 
		
		for(int i=0; i<discRate.length; i++) {
			for(int j=0; j<discRate[0].length; j++) {
				contRate[i][j] = irDiscToCont(discRate[i][j]);	
			}			
		}
		
		return contRate;
	}
	
	
	protected static double irContToDisc( double contRate) {
		return Math.exp(contRate) - 1.0;
	}
	

	protected static double[] irContToDisc(double[] contRate) {
		
		double[] discRate = new double[contRate.length]; 
		
		for(int i=0; i<contRate.length; i++) {
			discRate[i] = irContToDisc(contRate[i]);
		}
		
		return discRate;
	}
	
	
	protected static double[][] irContToDisc(double[][] contRate) {
		
		double[][] discRate = new double[contRate.length][contRate[0].length]; 
		
		for(int i=0; i<contRate.length; i++) {
			for(int j=0; j<contRate[0].length; j++) {
				discRate[i][j] = irContToDisc(contRate[i][j]);	
			}			
		}		
		
		return discRate;
	}
	
	
	protected static double round(double number, int decimalDigit) {
		if(decimalDigit < 0) return Math.round(number);
		return Double.parseDouble(String.format("%." + decimalDigit + "f", number));
	}
	
	
	public double round(double number) {
		return Double.parseDouble(String.format("%." + this.decimalDigit + "f", number));
	}
	

	protected double[][] vecToMat(double[] vec) {
		
		double[][] mat = new double[vec.length][1];		
		for(int i=0; i<mat.length; i++) mat[i][0] = vec[i];
		return mat;		
	}	
	
	
	protected double[] matToVec(double[][] mat, int colIdx) {		
		
		double[] col = new double[mat.length];		
		for(int i=0; i<col.length; i++) col[i] = mat[i][colIdx];
		return col;
	}				
	
	
	protected double sumVector(double[] vec) {
		
		double sum = 0.0;
		for(int i=0; i<vec.length; i++) sum += vec[i];
		return sum;
	}		

		
	protected double[][] toDiagMatrix(double... elements) {
		
		double[][] diagMat = new double[elements.length][elements.length];
		
		for(int i=0; i<diagMat.length; i++) {
			for(int j=0; j<diagMat[i].length; j++) {
				diagMat[i][j] = (i==j) ? elements[i] : 0.0;
			}
		}
		return diagMat;
	}
	
	
	protected double[][] toIdentityMatrix(int dim) {
		return toDiagMatrix(1.0, dim);
	}
	
	
	protected double[][] toDiagMatrix(double element, int dim) {
		
		double[][] diagMat = new double[dim][dim];
		
		for(int i=0; i<diagMat.length; i++) {
			for(int j=0; j<diagMat[i].length; j++) {
				diagMat[i][j] = (i==j) ? element : 0.0;
			}
		}
		return diagMat;		
	}		
	
	
	protected double[] toVector(double element, int dim) {
		
		double[] vector = new double[dim];		
		for(int i=0; i<vector.length; i++) vector[i] = element;
		return vector;		
	}	
		

	protected double[][] toLowerTriangular3(double[] elements) {
		
		if(elements.length != 6) {
			log.error("Check the elements in Sigma Matrix");
			return null;
		}
		
		double[][] lowerMat = new double[3][3];
		
		lowerMat[0][0] = elements[0];  lowerMat[0][1] = 0.0;          lowerMat[0][2] = 0.0;
		lowerMat[1][0] = elements[1];  lowerMat[1][1] = elements[2];  lowerMat[1][2] = 0.0;
		lowerMat[2][0] = elements[3];  lowerMat[2][1] = elements[4];  lowerMat[2][2] = elements[5];
		
		return lowerMat;
	}
	

	protected static Map<Integer, List<Double>> eigenValueUserDefined(double[][] mat, int dim) {
		return eigenValueUserDefined(new SimpleMatrix(mat), dim);		
	}
	
	
	protected static Map<Integer, List<Double>> eigenValueOrigin(SimpleMatrix mat, int dim) {
		
		SimpleEVD<SimpleMatrix> eigmat = mat.eig();				
		Complex_F64 [] eigval = eigmat.getEigenvalues().stream().toArray(Complex_F64[]::new);			
		SimpleMatrix[] eigvec = new SimpleMatrix[] {eigmat.getEigenVector(0), eigmat.getEigenVector(1), eigmat.getEigenVector(2)};		
		
		Double[][] eigvec_user = new Double[dim][dim];		
		
		for(int j=0; j<dim; j++) {						
			eigvec_user[0][j] = eigvec[0].get(j,0);
			eigvec_user[1][j] = eigvec[1].get(j,0);
			eigvec_user[2][j] = eigvec[2].get(j,0);			
		}			
		
		Map<Double, List<Double>> eigMap = new TreeMap<Double,  List<Double>>(Collections.reverseOrder());		
		for(int i=0; i<dim; i++) eigMap.put(eigval[i].getReal(), Arrays.asList(eigvec_user[i]));		
		
		int i=0;
		Map<Integer, List<Double>> eigvMap = new TreeMap<Integer, List<Double>>();
		for(Map.Entry<Double, List<Double>> eig : eigMap.entrySet()) {
			eigvMap.put(i,  eig.getValue());
			i++;
		}		
		return eigvMap;
	}
	
	
	//TODO: for Symmetric Matrix	
	protected static Map<Integer, List<Double>> eigenValueUserDefined(SimpleMatrix mat, int dim) {
				
		if(dim != 3) return null;
		if(!mat.isIdentical(mat.transpose(), 0.000000001)) return null;
	
		SimpleEVD<SimpleMatrix> eigmat = mat.eig();				
		Complex_F64 [] eigval = eigmat.getEigenvalues().stream().toArray(Complex_F64[]::new);			
		SimpleMatrix[] eigvec = new SimpleMatrix[] {eigmat.getEigenVector(0), eigmat.getEigenVector(1), eigmat.getEigenVector(2)};		
		
//		Complex_F64    prod   = eigval[0].times(eigval[1].times(eigval[2]));		
//		log.info("{}, {}", prod, mat.determinant());
//		log.info("{}, {}, {}, {}", eigval[0], eigval[1], eigval[2]);
//		log.info("{}, {}, {}, {}", eigvec[0], eigvec[1], eigvec[2]);
		
		SimpleMatrix mat_temp1 = mat.minus(SimpleMatrix.identity(dim).scale(eigval[1].getReal()));		
		SimpleMatrix mat_temp2 = mat.minus(SimpleMatrix.identity(dim).scale(eigval[2].getReal()));
		
//		log.info("{}, {}", mat_temp1, mat_temp2);
		
//		 (2,1), (2,2),  |  (1,1), (1,2),  |  (1,1), (1,2)
//		 (3,1), (3,2),  |  (3,1), (3,2),  |  (2,1), (2,2)		
		double[] eig_temp1 = new double[] { +1 * mat_temp1.extractMatrix(1, 3, 0, 2).determinant()                
				                          , -1 * mat_temp1.extractMatrix(0, 1, 0, 2).combine(1, 0, mat_temp1.extractMatrix(2, 3, 0, 2)).determinant()
				                          , +1 * mat_temp1.extractMatrix(0, 2, 0, 2).determinant() };
		
		
        double[] eig_temp2 = new double[] { +1 * mat_temp2.extractMatrix(1, 3, 0, 2).determinant()
                                          , -1 * mat_temp2.extractMatrix(0, 1, 0, 2).combine(1, 0, mat_temp2.extractMatrix(2, 3, 0, 2)).determinant()
                                          , +1 * mat_temp2.extractMatrix(0, 2, 0, 2).determinant() };
     
        double sumsq_temp1 = 0.0, sumsq_temp2 = 0.0;
		for(int i=0; i<dim; i++) {
			sumsq_temp1 += Math.pow(eig_temp1[i], 2);
			sumsq_temp2 += Math.pow(eig_temp2[i], 2);
		}	  

		Double[][] eigvec_user = new Double[dim][dim];		
		
		for(int j=0; j<dim; j++) {						
			eigvec_user[0][j] = eigvec[0].get(j,0);
			eigvec_user[1][j] = eig_temp1[j] / Math.sqrt(sumsq_temp1);
			eigvec_user[2][j] = eig_temp2[j] / Math.sqrt(sumsq_temp2);			
		}				

		Map<Double, List<Double>> eigMap = new TreeMap<Double,  List<Double>>(Collections.reverseOrder());		
		for(int i=0; i<dim; i++) eigMap.put(eigval[i].getReal(), Arrays.asList(eigvec_user[i]));		
		
		int i=0;
		Map<Integer, List<Double>> eigvMap = new TreeMap<Integer, List<Double>>();
		for(Map.Entry<Double, List<Double>> eig : eigMap.entrySet()) {
			eigvMap.put(i,  eig.getValue());
			i++;
		}		
		return eigvMap;
	}		
	
	
	protected static List<IrCurveHis> setIrCurveHisInt(String[] dateHis, String[] matCd, double[][] iRateHis) {
		
		List<IrCurveHis> curveHis = new ArrayList<IrCurveHis>();
		
		for(int i=0; i<dateHis.length; i++) {
			for(int j=0; j<matCd.length; j++) {
				IrCurveHis crv = new IrCurveHis();
				crv.setBaseDate(dateHis[i]);
				crv.setIrCurveId("1111111");
				crv.setMatCd(matCd[j]);
				crv.setIntRate(iRateHis[i][j]);
				curveHis.add(crv);				
			}
		}		
		return curveHis;
	}

	
	protected static List<IrCurveHis> setIrCurveHisBase(String baseDate, String[] matCd, double[] baseRate) {
		
		List<IrCurveHis> curveHis = new ArrayList<IrCurveHis>();
		
		for(int i=0; i<matCd.length; i++) {
			IrCurveHis crv = new IrCurveHis();
			crv.setBaseDate(baseDate);
			crv.setIrCurveId("1111111");
			crv.setMatCd(matCd[i]);
		    crv.setIntRate(baseRate[i]);
		    curveHis.add(crv);
		}		
		return curveHis;
	}
		


	protected static double getTimeFactor(LocalDate date1, LocalDate date2, int dayCountBasis) {			      
	    
	    switch(dayCountBasis) {
	    
	        case DCB_ACT_365: return ChronoUnit.DAYS.between(date1, date2) / 365.0;
	        case DCB_A30_360: return DaysBetweenA30360(date1, date2) / 360.0;
	        case DCB_E30_360: return DaysBetweenE30360(date1, date2) / 360.0;
	        case DCB_ACT_ACT: return getTimeFactorActAct(date1, date2);
            case DCB_ACT_360: return ChronoUnit.DAYS.between(date1, date2) / 360.0;          
            case DCB_MON_DIF: return ChronoUnit.MONTHS.between(date1.withDayOfMonth(1), date2.withDayOfMonth(1)) / MONTH_IN_YEAR * 1.0;
	        default: return 0.0;	        
	    }	    
	}
	
	
    private static double getTimeFactorActAct(LocalDate date1, LocalDate date2) {    	

    	double timeFactor;    	
    	
    	timeFactor = (double) (date1.lengthOfYear() - date1.getDayOfYear()) / date1.lengthOfYear();    	
    	timeFactor += (double) date2.getDayOfYear() / date2.lengthOfYear();    	
    	timeFactor += (double) date2.getYear() - date1.getYear() - 1.0;
    	
    	//System.out.println("TF(act/act) = " + timeFactor + " : " + date1 + " | " + date2);
    	return timeFactor;    	
    }   
    
    
    private static int DaysBetweenA30360(LocalDate date1, LocalDate date2) {    	
    	
    	int day1 = date1.getDayOfMonth();
    	int day2 = date2.getDayOfMonth();
    	int daysDiff;
    	
    	daysDiff = (date2.getYear() - date1.getYear()) * 12;
    	daysDiff += date2.getMonthValue() - date1.getMonthValue();
    	daysDiff *= 30;
    	
    	if (date1.getMonth().equals(Month.FEBRUARY) && day1 == date1.lengthOfMonth()) {
    		if(date2.getMonth().equals(Month.FEBRUARY) && day2 == date2.lengthOfMonth()) {
    			day2 = 30;    			
    		}
    		day1 = 30;
    	}
    	
    	if (day2 == 31 && day1 >= 30) day2 = 30;
    	if (day1 == 31) day1 = 30;
    	
    	daysDiff += day2 - day1;
    	
    	return daysDiff;
    }
    
    
    private static int DaysBetweenE30360(LocalDate date1, LocalDate date2) {    	
    	
    	int day1 = date1.getDayOfMonth();
    	int day2 = date2.getDayOfMonth();
    	int daysDiff;
    	
    	daysDiff = (date2.getYear() - date1.getYear()) * 12;
    	daysDiff += date2.getMonthValue() - date1.getMonthValue();
    	daysDiff *= 30;
    	
    	if (day1 > 30) day1 = 30;
    	if (day2 > 30) day2 = 30;
    	
    	daysDiff += day2 - day1;
    	
    	return daysDiff;
    }
    	

    public static LocalDate stringToDate(String dateString) {
    	
		if(dateString != null && dateString.length() == 8) {
			
			int year  = Integer.parseInt(dateString.substring(0, 4));
			int month = Integer.parseInt(dateString.substring(4, 6));
			int day   = Integer.parseInt(dateString.substring(6, 8));			
			
			return LocalDate.of(year, month, day);    		
		}
		return null;    	
    }
    
    
    public static String dateToString(LocalDate date) {    	
    	
    	if(date != null) {    		
    		
    		return    String.format("%04d", date.getYear())
    				+ String.format("%02d", date.getMonthValue())
    				+ String.format("%02d", date.getDayOfMonth());
    	}    	
    	return null;    	
    }
    
	
	protected static double[][] getIntRate(String path) throws Exception {		
				
		String[][] input = readCSVtoArray(path);
		
		//log.info("{}", input[0][13]);		
		double[][] intRate = new double[input.length][input[0].length-1];
		
		for(int i=0; i<intRate.length; i++) {
			for(int j=0; j<intRate[0].length; j++) {				
				intRate[i][j] = Double.parseDouble(input[i][j+1]);				
			}
		}		
		return intRate;		
	}
	
	protected static String[] getIntDate(String path) throws Exception {		
		
		String[][] input = readCSVtoArray(path);		
		
		String[] date = new String[input.length];
		
		for(int i=0; i<date.length; i++) date[i] = input[i][0];
		
		return date;		
	}
		
	
	public static String[][] readCSVtoArray(String path) throws IOException {     // to be checked(debugging)
		
		ArrayList<ArrayList<String>> getCSVtoList = readCSVtoList(path);
		String[][] getCSVtoArray = new String[getCSVtoList.size()][getCSVtoList.get(0).size()];
		
		for (int i=0; i<getCSVtoList.size(); i++) {
			for (int j=0; j<getCSVtoList.get(i).size(); j++) {
				getCSVtoArray[i][j] = getCSVtoList.get(i).get(j);				
			}			
		}		
		return getCSVtoArray;		        
	}
	
	
	private static ArrayList<ArrayList<String>> readCSVtoList(String path) throws IOException {
		
		ArrayList<ArrayList<String>> getCSVtoList = new ArrayList<ArrayList<String>>();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		
		//System.out.println(this.path);
		int numline = 0;
		
		while(reader.ready()) {						
			String[] line = reader.readLine().split(",");						
			
			//System.out.println(line);
			ArrayList<String> tmparr = new ArrayList<String>();
			for( int i=0; i<line.length; i++) {				
				tmparr.add(line[i]);				
			}
			
			if(numline > 0) getCSVtoList.add(tmparr);
			numline++;
		}
		reader.close();		
		return getCSVtoList;		
	}	
}
