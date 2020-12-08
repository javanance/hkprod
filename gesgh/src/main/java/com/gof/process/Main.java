package com.gof.process;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.math3.random.CorrelatedRandomVectorGenerator;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.NormalizedRandomGenerator;
import org.hibernate.Session;

import com.gof.dao.BizDiscRateFwdSceDao;
import com.gof.dao.BizDiscountRateDao;
import com.gof.dao.BottomupDcntDao;
import com.gof.dao.DiscRateMstDao;
import com.gof.dao.DiscRateStatsDao;
import com.gof.dao.EsgMetaDao;
import com.gof.dao.EsgMstDao;
import com.gof.dao.EsgParamDao;
import com.gof.dao.IrCurveHisDao;
import com.gof.dao.LiqPremiumDao;
import com.gof.dao.SmithWilsonDao;
import com.gof.dao.StdAssetDao;
import com.gof.dao.StdAssetVolDao;
import com.gof.dao.SwaptionVolDao;
import com.gof.entity.BizDiscRate;
import com.gof.entity.BizDiscRateFwdSce;
import com.gof.entity.BizDiscRateSce;
import com.gof.entity.BizDiscRateStat;
import com.gof.entity.BizDiscountRate;
import com.gof.entity.BizDiscountRateSce;
import com.gof.entity.BizEsgParam;
import com.gof.entity.BizIrCurveHis;
import com.gof.entity.BizIrCurveSce;
import com.gof.entity.BizStockSce;
import com.gof.entity.BottomupDcnt;
import com.gof.entity.EsgMst;
import com.gof.entity.EsgRandom;
import com.gof.entity.IrCurve;
import com.gof.entity.IrCurveHis;
import com.gof.entity.IrSce;
import com.gof.entity.JobLog;
import com.gof.entity.ParamCalcHis;
import com.gof.entity.SmithWilsonParamHis;
import com.gof.entity.StdAssetCorr;
import com.gof.entity.StdAssetMst;
import com.gof.entity.SwaptionVol;
import com.gof.enums.EAdvJob;
import com.gof.enums.EBoolean;
import com.gof.enums.EJob;
import com.gof.enums.ERunArgument;
import com.gof.interfaces.IIntRate;
import com.gof.model.HullWhite4j;
import com.gof.model.HullWhiteContBond4j;
import com.gof.model.LogNormal4j;
import com.gof.model.SmithWilsonModelCore;
import com.gof.util.EsgConstant;
import com.gof.util.FinUtils;
import com.gof.util.HibernateUtil;
import com.gof.util.ParamUtil;
import com.gof.util.ScriptUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * ESG Main Process.
 * @author takion77@gofconsulting.co.kr
 * @version 1.0
 *
 */
@Slf4j
public class Main {
	private static Map<ERunArgument, String> argInputMap = new HashMap<>();
	private static String output;
	private static int batchNum 			= 10;
	private static double dnsErrorTolerance = 0.00001;
	private static double kicsVolAdjust 	= 0.0032;

	private static double hwErrorTolerance  = 0.00000001;
	private static double hw2ErrorTolerance = 0.00000001;
	private static String irSceGenSmithWilsonApply = "Y";
	
	private static long cnt = 0;
	private static long totalSize = 0;
	private static int projectionYear=100;
	
	private static String paramGroup ;
	private static String jobString;
	private static String irSceCurrencyString;
	private static String lqFittingModel;
	
	
	
	private static String bssd;
	private static int poolSize;
	private static ExecutorService exe ;
	private static Session session;
	private static int flushSize = 10000; 
	private static double decayFactor = 0.97;
	private static String ewmaYn 	="Y";
	private static int volDataSize 	=250;
	private static String volCalcId  ="SMA";			//Defalut Vol Calc Id
	private static String localVolYn = "Y";
	private static double targetDuration = 3.0;
	
//	private static int flushSize = 50;
	
	private static Map<String, IrCurve> rfCurveMap 	= new HashMap<String, IrCurve>();
	private static Map<String, IrCurve> bottomUpMap 	= new HashMap<String, IrCurve>();
	private static Map<String, IrCurve> kicsMap 		= new HashMap<String, IrCurve>();
	
//	private static List<IrCurve> rfCurveList 	= new ArrayList<IrCurve>();
//	private static List<IrCurve> bottomUpList 	= new ArrayList<IrCurve>();
//	private static List<IrCurve> kicsList 		= new ArrayList<IrCurve>(); 
	
	private static List<String> jobList 		= new ArrayList<String>();
	private static Set<String> irSceCurrency 	= new HashSet<>();
	
// ******************************************************************For AFNS ********************************	
	
	private static String stBssd      = "20100101";
	private static String mode            = "DNS";	
	
	private static String irCurveId       = "111111C";
	private static String baseCurveId     = "111111C";
	private static String realNumberStr   = "N"; 
	private static boolean isRealNumber   = false;
	private static String cmpdTypeStr     = "DISC";
	private static char   cmpdType        = 'D';
	private static double dt              = 1.0 / 52.0;
	private static String tenorListStr    = "M0003";
	private static List<String> tenorList = new ArrayList<String>();
	
	private static double errorTolerance  = 0.0000000001;
	private static int    kalmanItrMax    = 100;
	private static double confInterval    = 0.995;
	private static double sigmaInit       = 0.05;
	private static double lambdaMin       = 0.05;
	private static double lambdaMax       = 2.00;
	private static double epsilonInit     = 0.001;
	private static int    dayCountBasis   = 1;
	
	private static int    prjYear         = 140;
	private static double ltfrL           = 0.045;
	private static double ltfrA           = 0.045;
	private static int    ltfrT           = 60;	
	private static double liqPrem         = 0.0032;	
	
// *********************************************************************************************************	
	
	
	
	public static void main(String[] args) {
// ******************************************************************Run Argument &  Common Data Setting ********************************
		init(args);		// Input Data Setting
// ******************************************************************Pre Validation ********************************
//		job1();			// Validation


// ******************************************************************ESG Stock Parameter Job ********************************
		job4();			// Historical Vol 
		job5();			// Historical Correlation
		job6();			// Stock Param
		job7();			// Biz Stock Param
		
// ******************************************************************ESG Ir Parameter Job ********************************
		
		job11();		// Job11 : ESG Parameter  : Vasicek, CIR, HULL AND WHITE, Hull and White 2 Factor
		job12();		// Job11 : ESG Parameter  : Vasicek, CIR, HULL AND WHITE, Hull and White 2 Factor
		job13();		// job13 : Biz ESG Param
		
//****************************************************************** LIQ PREMIUM & TERM STRUCTURE *******************************
		job15();		// job 15: Avg Liq Prem & Eom Liq Prem ( new!!!!)
		job16();		// job 16: Biz Liq prem
		job17();		// job 17: Bottom Up Curve : Risk Adj Bottomup ( add liq prem )
		job18();		// job 18: Biz Bottom Up Curve for ifrs
		job19();		// job 19: Biz IrCurve His with Smith Wilson
		
// ****************************************************************** Bottom Up Scenario Job *********************************************************
		job24A();		// job 24 : Biz Bottom Up Scenario Async Mode IFRS 
		job24B();		// job 24 : Biz Bottom Up Scenario Async Mode KICS
		job25A();		// job 25 : Biz Bottom Up Scenario Async Mode IFRS_JAVA
		job25B();		// job 25 : Biz Bottom Up Scenario Async Mode KICS_JAVA
		
		job29();		// Job 29 : KICS AFNS
		
// ******************************************************************ESG Stock Simulation Job ********************************		
		
		job31();		// Random Number under Correlation
		job32();		// Random Number for Hull White
		
		job34();		// Std Asset Yield Deterministic Scenario from irCurve
		job34A();		// Std Asset Yield Deterministic Scenario from irCurve_JAVA
		
		job35();		// Std Asset Yield Scenaio for KOSPI200_IFRS
		job36();		// Std Asset Yield Scenaio for KOSPI200_KICS
		
		job38();		// Std Asset Yield (Bond Yield) Scenaio_Hull White4j_IFRS
		job39();		// Std Asset Yield (Bond Yield) Scenaio_Hull White4j_KICS
		
// ******************************************************************ESG Ir Simulation Job ********************************
		
		job41();		// job41 : Ir Scenario from bottomUp IFRS
		job42();		// job42 : Ir Scenario from bottomUp KICS
		job43();		// job43 : HULL WHITE Scenario DB
		
//		job48();		// job48 : DNS Scenario TODO
		

// ****************************************************************** 공시이율 Job **********************************************************

		job51();		// job51 : 내부모형 기준의 자산운용수익률, 외부금리, 공시이율  통계분석 결과 산출( 통계산출 불가능한 이율코드임.: 데이터 부족 등등)
		job52();		// job52 : 산출한 통계모형 또는 사용자가 입력한 통계모형 중 적용할 통계모형을 결정하며, 현재 공시이율 수준 Fitting 작업을 수행함. 
		job53();		// Job53 : Forward Rate Generaion for IFRS
		job54();		// Job54 : IFRS 공시이율 
		job56();		// Job56 : IFRS 공시이율 시나리오   

// ****************************************************************** 공시이율 Job KICS**********************************************************		
		
		job61();		// Job61 : 감독원 기준의 통계모형 산출   
		job62();		// Job62 : 사용자가 입력한 통계모형으로 변환 & 현재 공시이율 수준 Fitting 작업을 수행함. (구분자 K : No Fitting,   S : Fitting)
		job63();		// Job63 : Forward Rate Generaion for KICS
		job64();		// Job64 : KICS 공시이율 
		
		job65();		// Job65 : KICS 공시이율 시나리오
		
// ****************************************************************** Inflation ********************************		
		job71();		// Job 71 : CPI Inflation !!!!
		job72();		// Job 72 : BIZ INFLATION IFRS!!!
		job73();		// Job 73 : BIZ INFLATION KICS!!!

// ****************************************************************** RC Job ********************************
		job81();		// Job 81 : TM
		job82();		// Job 82 : IFRS Corp PD
		job83();		// Job 83 : KICS Corp PD
		
		job86();		// Job 86 : LGD
		job87();		// Job 87 : IFRS 17 LGD
		job88();		// Job 88 : KIcS LGD

// *********************************************************** End Job ********************************
//		HibernateUtil.shutdown();
		
		exe.shutdown();
	}

	private static void init(String[] args) {
		for (String aa : args) {
			log.info("Input Arguments : {}", aa);
			for (ERunArgument bb : ERunArgument.values()) {
				if (aa.split("=")[0].toLowerCase().contains(bb.name())) {
					argInputMap.put(bb, aa.split("=")[1]);
					break;
				}
			}
		}
		try {
			bssd = argInputMap.get(ERunArgument.time).replace("-", "").replace("/", "").substring(0, 6);
		} catch (Exception e) {
			log.error("Argument error : -Dtime" );
			System.exit(0);
		}
		Properties properties = new Properties();
		try {
			FileInputStream fis = new FileInputStream(argInputMap.get(ERunArgument.properties));
			properties.load(new BufferedInputStream(fis));

		} catch (Exception e) {
			log.warn("Error in Properties Loading : {}", e);
			System.exit(1);
		}

		session = HibernateUtil.getSessionFactory(properties).openSession();
//		session = HibernateUtil.getSessionFactory(properties).getCurrentSession();
		
		paramGroup = properties.getOrDefault("PARAM_GROUP", "BASE").toString();
		
//		Map<String, String> argumentMap = EsgMstDao.getEsgParam(paramGroup).stream().collect(Collectors.toMap(s->s.getParamKey(), s->s.getParamValue()));
//		Map<String, String> argInDBMap = ParamUtil.getParamList(paramGroup).stream().collect(Collectors.toMap(s->s.getParamKey(), s->s.getParamValue()));
		Map<String, String> argInDBMap = EsgMetaDao.getEsgMeta(paramGroup).stream().collect(toMap(s->s.getParamKey(), s->s.getParamValue()));
		
		for(Map.Entry<Object, Object> entry : properties.entrySet()) {
			EsgConstant.getStrConstant().put(entry.getKey().toString(), entry.getValue().toString());
		}
		
		for(Map.Entry<String, String> entry : argInDBMap.entrySet()) {
			EsgConstant.getStrConstant().putIfAbsent(entry.getKey(), entry.getValue());
//			EsgConstant.getStrConstant().put(entry.getKey(), entry.getValue());
		}
		for(Map.Entry<String, String> entry : EsgConstant.getStrConstant().entrySet()) {
			try {
				EsgConstant.getNumConstant().put(entry.getKey(), Double.parseDouble(entry.getValue()));
			} catch (Exception e) {
				continue;
			}
		}	
		
		output 			  			= argInDBMap.getOrDefault("OUTPUT_DIR", 	 properties.getOrDefault("OUTPUT_DIR", "C:\\Dev\\ESG\\").toString()) ;
		lqFittingModel    			= argInDBMap.getOrDefault("LP_FIT_MODEL",    properties.getOrDefault("LP_FIT_MODEL", "POLY_FITTING").toString()) ;
		irSceCurrencyString  		= argInDBMap.getOrDefault("IR_SCE_CUR_CD", 	 properties.getOrDefault("IR_SCE_CUR_CD", "KRW").toString());
		irSceGenSmithWilsonApply	= argInDBMap.getOrDefault("IR_SCE_SW_APPLY", properties.getOrDefault("IR_SCE_SW_APPLY", "Y").toString()) ;
		ewmaYn			  			= argInDBMap.getOrDefault("EWMA_YN", properties.getOrDefault("EWMA_YN", "Y").toString()) ;
		localVolYn		  			= argInDBMap.getOrDefault("LOCAL_VOL_YN", properties.getOrDefault("LOCAL_VOL_YN", "Y").toString()) ;
		
		batchNum 		  = Integer.parseInt(argInDBMap.getOrDefault("BATCH_NUM", 		properties.getOrDefault("BATCH_NUM", "10").toString()));
		projectionYear 	  = Integer.parseInt(argInDBMap.getOrDefault("PROJECTION_YEAR", properties.getOrDefault("PROJECTION_YEAR", "101").toString()));
		volDataSize		  =	Integer.parseInt(argInDBMap.getOrDefault("DATA_NUM_VOL_CALC", properties.getOrDefault("DATA_NUM_VOL_CALC", "250").toString()));

		kicsVolAdjust     = Double.parseDouble(argInDBMap.getOrDefault("KICS_VOL_ADJ", 	properties.getOrDefault("KICS_VOL_ADJ", "0.0032").toString()));
		dnsErrorTolerance = Double.parseDouble(argInDBMap.getOrDefault("dnsErrorTolerance", properties.getOrDefault("dnsErrorTolerance", "0.00001").toString()));
		hwErrorTolerance  = Double.parseDouble(argInDBMap.getOrDefault("hwErrorTolerance",  properties.getOrDefault("hwErrorTolerance", "0.0001").toString()));
		hw2ErrorTolerance = Double.parseDouble(argInDBMap.getOrDefault("hw2ErrorTolerance", properties.getOrDefault("hw2ErrorTolerance", "0.0001").toString()));
		decayFactor		  =	Double.parseDouble(argInDBMap.getOrDefault("DECAY_FACTOR", properties.getOrDefault("DECAY_FACTOR", "0.97").toString()));
		
		targetDuration	  =	Double.parseDouble(argInDBMap.getOrDefault("BOND_YIELD_TARGET_DURATION", properties.getOrDefault("BOND_YIELD_TARGET_DURATION", "3.0").toString()));
		
		jobString 		  = properties.get("JOB").toString();
		
//		for(Map.Entry<String, String> entry : argInDBMap.entrySet()) {
//			if(entry.getKey().toUpperCase().contains("JOB")) {
//				jobString = entry.getValue();
//			}
//		}
		
		jobList 	 = Arrays.stream(jobString .split(",")).map(s -> s.trim()).collect(Collectors.toList());
		irSceCurrency= Arrays.stream(irSceCurrencyString.split(",")).map(s -> s.trim()).collect(Collectors.toSet());
		
		properties.entrySet().forEach(entry -> log.info("Property : {} = {}", entry.getKey(), entry.getValue()));
		argInDBMap.entrySet().forEach(s ->log.info("Effective Arguments in DB : {},{}", s.getKey(), s.getValue()));
		argInputMap.entrySet().stream().forEach(s -> log.info("Effective Arguments Input : {},{}", s.getKey(), s.getValue()));
		
		
		
		jobList.stream().forEach(s -> log.info("Effective Job List : {}", s));
		List<IrCurve> curveList = IrCurveHisDao.getRiskFreeIrCurve().stream().filter(s ->irSceCurrency.contains(s.getCurCd())).collect(toList());
		
//		rfCurveList  = curveList.stream().filter(s -> s.getRefCurveId()==null || s.getRefCurveId().equals("")).collect(toList());
//		bottomUpList = curveList.stream().filter(s -> s.getApplBizDv().equals("I")).collect(toList());
//		kicsList     = curveList.stream().filter(s -> s.getApplBizDv().equals("K")).collect(toList());
		
		rfCurveMap  = curveList.stream().filter(s -> s.getRefCurveId()==null || "".equals(s.getRefCurveId().trim())).collect(toMap(s->s.getCurCd(), Function.identity(), (s,t)-> s));
//		rfCurveMap  = curveList.stream().filter(s -> s.getRefCurveId()==null ).collect(toMap(s->s.getCurCd(), Function.identity(), (s,t)-> s));
		bottomUpMap = curveList.stream().filter(s -> "I".equals(s.getApplBizDv()) && s.getRefCurveId()!=null).collect(toMap(s->s.getCurCd(), Function.identity(), (s,t)-> s));
		kicsMap     = curveList.stream().filter(s -> "K".equals(s.getApplBizDv()) && s.getRefCurveId()!=null).collect(toMap(s->s.getCurCd(), Function.identity(), (s,t)-> s));
		
		
//		Hibernate Context flush Size
		flushSize 	 = Integer.parseInt(argInDBMap.getOrDefault("flushSize", properties.getOrDefault("flushSize", "10000").toString()));
		volCalcId    = ewmaYn.equals("Y") ? "EWMA_"+decayFactor: "SMA";
		
//		Parallel Running
		int maxThreadNum = Integer.parseInt(argInDBMap.getOrDefault("maxThreadNum", properties.getOrDefault("maxThreadNum", "4").toString()));
//		poolSize = Math.min(maxThreadNum, Runtime.getRuntime().availableProcessors()) -1 ;
		poolSize = maxThreadNum;
//		poolSize = 32;
		log.info("Number of Thread to Run in case of parallel process : {}" , poolSize);
		
		exe = Executors.newFixedThreadPool(poolSize);
//		exe = Executors.newFixedThreadPool(poolSize, new ThreadFactory() {
//			@Override
//			public Thread newThread(Runnable r) {
//				Thread t = new Thread(r);
//				t.setDaemon(true);
//				return t;
//			}
//		});
		
		
		EsgConstant.setSmParam(SmithWilsonDao.getParamHisList(bssd).stream().collect(toMap(s->s.getCurCd(), Function.identity(), (s,t)-> s)));
		for(String aa : irSceCurrency) {
			EsgConstant.getSmParam().putIfAbsent(aa, 
					new SmithWilsonParamHis(aa, EsgConstant.getNumConstant().get("UFR"), EsgConstant.getNumConstant().get("UFRT")));
		}
		
		String tenorString = EsgConstant.getStrConstant().getOrDefault("TENOR_LIST", "M0003,M0006,M0009,M0012,M0024,M0036,M0060,M0084,M0120,M0240");
		EsgConstant.setTenorList(Arrays.stream(tenorString.split(",")).map(s->s.trim()).collect(toList()));

		EsgConstant.getStrConstant().entrySet().forEach(s ->log.info("ESG Constant : {},{}", s.getKey(), s.getValue()));
		EsgConstant.getNumConstant().entrySet().forEach(s ->log.info("ESG Number Constant : {},{}", s.getKey(), s.getValue()));
		EsgConstant.getSmParam().entrySet().forEach(s ->log.info("ESG Constant for SmithWilson : {},{},{}", s.getKey(), s.getValue().getUfr(), s.getValue().getUfrT()));
		EsgConstant.getTenorList().forEach(s -> log.info("Tenor List : {}", s));
		
//		ScriptUtil.getScriptContents().forEach(s-> log.info("Script Size : {}", s.subSequence(0, 10)));
		ScriptUtil.getScriptMap().entrySet().forEach(s-> log.info("Script File : {}", s.getKey()));
		
//****************************************for AFNS *********************************************	
		//TODO
		try {
			
			mode            = properties.getOrDefault("AFNS_MODE"         , "AFNS"    ).toString().trim().toUpperCase();
			stBssd          = properties.getOrDefault("AFNS_START_DATE"   , "20100101").toString().trim().toUpperCase();
			irCurveId       = properties.getOrDefault("AFNS_HIST_CURVE_ID", "AFNS_KRW").toString().trim().toUpperCase();
			baseCurveId     = properties.getOrDefault("AFNS_BASE_CURVE_ID", "1010000" ).toString().trim().toUpperCase();
			realNumberStr   = properties.getOrDefault("AFNS_REAL_INT_RATE", "Y"       ).toString().trim().toUpperCase();
			isRealNumber    = realNumberStr.equals("Y"); 
			cmpdTypeStr     = properties.getOrDefault("AFNS_CMPD_TYPE"    , "DISC"    ).toString().trim().toUpperCase();
            cmpdType        = (cmpdTypeStr.equals("CONT") ? 'C' : 'D');		
			dt              = 1.0 / Double. valueOf((String) properties.getOrDefault("AFNS_DT_DENOM"  , "52"));			
			
			errorTolerance  = Double. valueOf((String) properties.getOrDefault("AFNS_ERROR_TOL"       , "0.0000000001"));
			kalmanItrMax    = Integer.valueOf((String) properties.getOrDefault("AFNS_KALMAN_ITR_MAX"  , "100"));
			confInterval    = Double. valueOf((String) properties.getOrDefault("AFNS_CONF_INTERVAL"   , "0.995"));
			sigmaInit       = Double. valueOf((String) properties.getOrDefault("AFNS_SIGMA_INIT"      , "0.05"));
			lambdaMin       = Double. valueOf((String) properties.getOrDefault("AFNS_LAMBDA_MIN"      , "0.05"));
			lambdaMax       = Double. valueOf((String) properties.getOrDefault("AFNS_LAMBDA_MAX"      , "2.00"));
			epsilonInit     = Double. valueOf((String) properties.getOrDefault("AFNS_EPSILON_INIT"    , "0.001"));
			dayCountBasis   = Integer.valueOf((String) properties.getOrDefault("AFNS_DAY_COUNT_BASIS" , "1"));			
			prjYear         = Integer.valueOf((String) properties.getOrDefault("AFNS_PROJ_YEAR"       , "140"));
			ltfrL           = Double. valueOf((String) properties.getOrDefault("AFNS_LTFR_INSU"       , "0.045"));
			ltfrA           = Double. valueOf((String) properties.getOrDefault("AFNS_LTFR_ASSET"      , "0.045"));
			ltfrT           = Integer.valueOf((String) properties.getOrDefault("AFNS_LTFR_TERM"       , "60"));
			liqPrem         = Double. valueOf((String) properties.getOrDefault("AFNS_KICS_VOL_ADJ"    , "0.0032"));
			
			tenorListStr    = properties.getOrDefault("AFNS_TENOR_LIST", "M0003,M0006,M0009,M0012,M0018,M0024,M0030,M0036,M0048,M0060,M0084,M0120,M0240").toString();
			tenorList       = Arrays.stream(tenorListStr.split(",")).map(s -> s.trim()).collect(Collectors.toList());			
		}
		catch (Exception e) {			
			e.printStackTrace();
			log.error("Error in Properties File Loading: {}", e);			
			System.exit(0);
		}
		
//****************************************for AFNS *********************************************
		
	}
	
	private static void saveOrUpdate(Object item) {
		session.saveOrUpdate(item);
//		log.info("in the flush1 : {}", cnt);
//		flushSize = 10000;	
		if(cnt % flushSize ==0) {
			session.flush();
			session.clear();
			log.info("in the flush : {}", cnt);
		}
		cnt = cnt+1;
	}
	
//	private static void saveOrUpdate(Object item) {
//		
//		try {
//			session.saveOrUpdate(item);
////			log.info("in the flush1 : {}", cnt);
////			flushSize = 1;	
//			if(cnt % flushSize ==0) {
//				session.flush();
//				session.clear();
//				elpaseTime1 = System.nanoTime();
//				log.info("in the flush : {},{},{}", cnt, (elpaseTime1 - elpaseTime0)/1_000_000_000, Thread.currentThread().getName());
//				elpaseTime0 = elpaseTime1;
//			}
//			cnt = cnt+1;
//			
//		} catch (Exception e) {
//			session.getTransaction().rollback();
//			log.info("Error with DataBase :{}, {}" ,item.toString(), e);
//			System.exit(1);
//			
//		} finally {
////			cnt = 0;
////			session.flush();
////			session.clear();
//		}
//	}

	private static void save(Object item) {
//		log.info("in the flush1 : {},{}", cnt, ((ArkCashFlowPivot)item).getPk());
		session.save(item);
			
		if(cnt % flushSize ==0) {
			session.flush();
			session.clear();
			log.info("in the flush : {}", cnt);
		}
		cnt = cnt+1;
	}

	private static JobLog startJogLog(EAdvJob job) {
		JobLog jobLog = new JobLog();
		jobLog.setJobStart(LocalDateTime.now());
		
		jobLog.setJobId(String.valueOf(job.getJobNo()));
		jobLog.setCalcStart(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")));
		jobLog.setBaseYymm(bssd);
		jobLog.setCalcDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")));
		jobLog.setJobNm(job.getJobName());
		
		log.info("{}({}) : Job Start !!! " , job.name(), job.getJobName());
		
		return jobLog;
	}
	
	private static JobLog startJogLog(EJob job) {
		JobLog jobLog = new JobLog();
		jobLog.setJobStart(LocalDateTime.now());
		
		jobLog.setJobId(job.name());
		jobLog.setCalcStart(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")));
		jobLog.setBaseYymm(bssd);
		jobLog.setCalcDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")));
		jobLog.setJobNm(job.getJobName());
		
		log.info("{}({}) : Job Start !!! " , job.name(), job.getJobName());
		
		return jobLog;
	}
	
	private static void completJob(String successDiv, JobLog jobLog) {

		long timeElpse = Duration.between(jobLog.getJobStart(), LocalDateTime.now()).getSeconds();
		jobLog.setCalcEnd(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")));
		jobLog.setCalcScd(successDiv);
		jobLog.setCalcElps(String.valueOf(timeElpse));
		jobLog.setLastModifiedBy("ESG"+jobLog.getJobId());
		jobLog.setLastUpdateDate(LocalDateTime.now());
		
		log.info("{}({}): Job Completed with {} !!!!", jobLog.getJobId(), jobLog.getJobNm(),successDiv );
	}
		
	private static void job0() {
		if (jobList.contains("0")) {
			log.info("Job 0 : Validation start !!!");
			session.beginTransaction();

			// Job1_PreValidation.validateIrCurve(bssd);

			Job01_PreValidation.validateSwaptionVol(bssd);
			Job01_PreValidation.validateUsedEsgModel();
			
			Job01_PreValidation.validateBottomUpIrCurve();
//			Job1_PreValidation.validDisclosureRateHis(bssd);
//			Job1_PreValidation.validDiscRateCumAssetYield(bssd);
//			Job1_PreValidation.validDiscRateAssetYield(bssd);
//			Job1_PreValidation.validExternalIntRate(bssd);
			
			Job01_PreValidation.validTransitionMatrix(bssd);
			Job01_PreValidation.validTransitionMatrixSumEqualOne(bssd);
			session.getTransaction().commit();

			log.info("Job 1 : Validation  is Completed !!!");
		}		
	}
	
	private static void job4() {
		if (jobList.contains("4")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG4);
			try {
				session.createQuery("delete StdAssetVol a where substr(a.baseDate,1,6) =:param and a.volCalcId = :volCalcId")
									.setParameter("param", bssd)
									.setParameter("volCalcId", volCalcId)
									.executeUpdate();
				
				Job04_HisVol.createHisVol(bssd, ewmaYn, decayFactor, volDataSize).forEach(s->saveOrUpdate(s));
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.warn("Error : {}", e);
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}

	private static void job5() {
		if (jobList.contains("5")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG5);
			try {

				Job04_HisVol.createCorr(bssd, ewmaYn, decayFactor, volDataSize).forEach(s->saveOrUpdate(s));
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.warn("Error : {}", e);
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job6() {
		if (jobList.contains("6")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG6);
			try {
				session.createQuery("delete StockParamHis a where a.baseYymm=:param ").setParameter("param", bssd).executeUpdate();
				
//				Implied Vol
				StdAssetDao.getStdAssetMst().stream().map(s-> Job06_StockParam.createStockParamHis(bssd, s)).flatMap(s->s.stream()).forEach(s->saveOrUpdate(s));
				
//				Historical Vol
				StdAssetVolDao.getStdAssetVolByMonth(bssd, volCalcId).stream().map(s->s.convert(bssd, volCalcId)).forEach(s->saveOrUpdate(s));

				completJob("SUCCESS", jobLog);
			}
			catch (Exception e) {
				log.warn("Error : {}", e);
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job7() {
		if (jobList.contains("7")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG7);
			try {
				session.createQuery("delete BizStockParam a where a.baseYymm=:param ").setParameter("param", bssd).executeUpdate();
				
				Job07_BizStockParam.createBizStockParam(bssd, "I", localVolYn).forEach(s->saveOrUpdate(s));
				Job07_BizStockParam.createBizStockParam(bssd, "K", localVolYn).forEach(s->saveOrUpdate(s));
				
				completJob("SUCCESS", jobLog);
			}
			catch (Exception e) {
				log.warn("Error : {}", e);
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}

	private static void job11() {
			if (jobList.contains("11")) {
				session.beginTransaction();
				JobLog jobLog = startJogLog(EJob.ESG11);
				try {
					String rfKrwId = EsgConstant.getStrConstant().get("ESG_RF_KRW_ID");
					List<String> tenorList = EsgConstant.getTenorList();
					double ufr  = EsgConstant.getSmParam().get("KRW").getUfr();
					double ufrt = EsgConstant.getSmParam().get("KRW").getUfrT();
					
					log.info("SmithWilson Param : {},{},{}", ufr, ufrt, tenorList.toString());
					
					List<SwaptionVol> volRst = SwaptionVolDao.getSwaptionVol(bssd);
					Map<String, Double> nullLpMap = new HashMap<String, Double>();
					List<IrCurveHis> curveRst = IrCurveHisDao.getIrCurveHis(bssd, rfKrwId);
					
					if(volRst.size()==0 ) {
						log.error("Error in Loading Swaption Vol");
						System.exit(0);
					}else if(curveRst.size()==0 ) {
						log.error("Error in Loading IntRate Curve");
						System.exit(0);
					}
					
					Map<String, IrCurveHis> curveHisMap = curveRst.stream().filter(s ->tenorList.contains(s.getMatCd())).collect(toMap(s->s.getMatCd(), Function.identity()));
					SmithWilsonModelCore sw = new SmithWilsonModelCore(bssd, rfKrwId, "0", curveHisMap, nullLpMap,  ufr, ufrt);
					List<IrCurveHis> swTermStructure = sw.getSmithWilsionResult().stream().map(s-> s.convertToIrCurveHis()).collect(toList());
					
	//				Job11_EsgParameter.createVasicekParamCalcHisAsync(bssd, swTermStructure, ufr, ufrt,hwErrorTolerance).forEach(s->session.saveOrUpdate(s));
	//				Job11_EsgParameter.createCirParamCalcHisAsync(bssd, swTermStructure, ufr, ufrt, hwErrorTolerance).forEach(s->session.saveOrUpdate(s));
	//				Job11_EsgParameter.createHwParamCalcHisAsync(bssd, curveRst, volRst, ufr, ufrt, hwErrorTolerance).forEach(s->session.saveOrUpdate(s));
					
					Job11_EsgParameter.createHwKicsParamCalcHisAsync(bssd, curveRst, volRst, ufr, ufrt, hwErrorTolerance).forEach(s->session.saveOrUpdate(s));
	//				Job11_EsgParameter.createHw2FactorParamCalcHisAsync(bssd, curveRst, volRst, ufr, ufrt, hwErrorTolerance).forEach(s->session.saveOrUpdate(s));
					
					session.flush();
					
					completJob("SUCCESS", jobLog);
					
				} catch (Exception e) {
					completJob("ERROR", jobLog);
				}
				
				session.saveOrUpdate(jobLog);
				session.getTransaction().commit();
			}
		}

	private static void job12() {
			if (jobList.contains("12")) {
				session.beginTransaction();
				JobLog jobLog = startJogLog(EJob.ESG12);
				log.info("Async Mode start !!!");
				try {
					String rfKrwId = EsgConstant.getStrConstant().get("ESG_RF_KRW_ID");
					List<String> tenorList = EsgConstant.getTenorList();
					double ufr  = EsgConstant.getSmParam().get("KRW").getUfr();
					double ufrt = EsgConstant.getSmParam().get("KRW").getUfrT();
					log.info("SmithWilson Param : {},{},{}", ufr, ufrt, tenorList.toString());
					
					List<SwaptionVol> volRst = SwaptionVolDao.getSwaptionVol(bssd);
					Map<String, Double> nullLpMap = new HashMap<String, Double>();
					List<IrCurveHis> curveRst = IrCurveHisDao.getIrCurveHis(bssd, rfKrwId);
					
					if(volRst.size()==0 ) {
						log.error("Error in Loading Swaption Vol");
						System.exit(0);
					}else if(curveRst.size()==0 ) {
						log.error("Error in Loading IntRate Curve");
						System.exit(0);
					}
					
					Map<String, IrCurveHis> curveHisMap = curveRst.stream().filter(s ->tenorList.contains(s.getMatCd())).collect(toMap(s->s.getMatCd(), Function.identity()));
					SmithWilsonModelCore sw = new SmithWilsonModelCore(bssd, rfKrwId, "0", curveHisMap, nullLpMap,  ufr, ufrt);
					List<IrCurveHis> swTermStructure = sw.getSmithWilsionResult().stream().map(s-> s.convertToIrCurveHis()).collect(toList());
					
					List<CompletableFuture<List<ParamCalcHis>>> futureList = new ArrayList<CompletableFuture<List<ParamCalcHis>>>();
					
	//				CompletableFuture<List<ParamCalcHis>> 	vasicekFut 	= CompletableFuture.supplyAsync(()-> Job11_EsgParameter.createVasicekParamCalcHisAsync(bssd, swTermStructure, ufr, ufrt,hwErrorTolerance), exe);
	//				CompletableFuture<List<ParamCalcHis>> 	cirkFut 	= CompletableFuture.supplyAsync(()-> Job11_EsgParameter.createCirParamCalcHisAsync(bssd, swTermStructure, ufr, ufrt, hwErrorTolerance), exe);
	//				CompletableFuture<List<ParamCalcHis>> 	hwFut 		= CompletableFuture.supplyAsync(()-> Job11_EsgParameter.createHwParamCalcHisAsync(bssd, curveRst, volRst, ufr, ufrt, hwErrorTolerance), exe);
					CompletableFuture<List<ParamCalcHis>> 	hwKicsFut 	= CompletableFuture.supplyAsync(()-> Job11_EsgParameter.createHwKicsParamCalcHisAsync(bssd, curveRst, volRst, ufr, ufrt, hwErrorTolerance), exe);
	//				CompletableFuture<List<ParamCalcHis>> 	hw2Fut 		= CompletableFuture.supplyAsync(()-> Job11_EsgParameter.createHw2FactorParamCalcHisAsync(bssd, curveRst, volRst, ufr, ufrt, hwErrorTolerance), exe);
					
	//				futureList.add(vasicekFut);
	//				futureList.add(cirkFut);
	//				futureList.add(hwFut);
					futureList.add(hwKicsFut);
	//				futureList.add(hw2Fut);
					
					List<ParamCalcHis> rst = futureList.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
					
					rst.stream().forEach(s ->session.saveOrUpdate(s));
					
					session.flush();	
					completJob("SUCCESS", jobLog);
				} catch (Exception e) {
					completJob("ERROR", jobLog);
				}
				session.saveOrUpdate(jobLog);
				session.getTransaction().commit();
			}
		}

	private static void job13() {
		if (jobList.contains("13")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG13);
			try {
				session.createQuery("delete BizEsgParam a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				Job13_BizEsgParameter.createBizAppliedParameter(bssd).stream().forEach(s -> session.save(s));
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}	
	}

	private static void job15() {
		if (jobList.contains("15")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG15);
			try {
				session.createQuery("delete LiqPremium a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				
				String lqModelId = "COVERED_BOND_KDB_EOM";
				Job15_LiquidPremium.createLiquidPremiumEom(bssd, lqModelId).stream().forEach(s -> session.saveOrUpdate(s));
				
				
				String lqModelId2 = "COVERED_BOND_KDB";
				Job15_LiquidPremium.createLiquidPremium(bssd, lqModelId2).stream().forEach(s -> session.saveOrUpdate(s));
				
				
				String kicsVolAddjId = "KICS_VOL_ADJ";
				
				Job15_LiquidPremium.createLiquidPremiumFrom(bssd, kicsVolAddjId, kicsVolAdjust).stream().forEach(s -> session.saveOrUpdate(s));
				completJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.error("ERROR : {}", e);
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}	
	}
	
	private static void job16() {
		if (jobList.contains("16")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG16);
			try {
				
				session.createQuery("delete BizLiqPremium a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				
				String lqModelId = ParamUtil.getParamMap().getOrDefault("lqModelId", "COVERED_BOND_KDB");
				String kicsVolAdjId = "KICS_VOL_ADJ";
				
				log.info("aaa : {},{}", lqModelId, kicsVolAdjId);
				
				Job16_BizLiquidPremium.createBizLiqPremium(bssd, "I", lqModelId).stream().forEach(s -> session.saveOrUpdate(s));
				Job16_BizLiquidPremium.createBizLiqPremium(bssd, "K", kicsVolAdjId).stream().forEach(s -> session.saveOrUpdate(s));
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.error("ERROR : {}", e);
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}

	private static void job17() {
		if (jobList.contains("17")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG17);
			try {
				session.createQuery("delete BottomupDcnt a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				
//				String lqModelId = ParamUtil.getParamMap().getOrDefault("lqModelId", "COVERED_BOND_KDB");
//				String kicsVolAddjId = "KICS_VOL_ADJ";
				
				kicsMap.values().stream().flatMap(s-> Job17_LiqAdjBottomUp.createBottomUpAddLiqPremium(bssd, s)).forEach(s -> session.save(s));

				bottomUpMap.values().stream().flatMap(s-> Job17_LiqAdjBottomUp.createBottomUpAddLiqPremium(bssd, s)).forEach(s->session.save(s));
				
				completJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.info("zzz :  {},{}", e);
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}

	private static void job18() {
		if (jobList.contains("18")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG18);
			try {
				session.createQuery("delete BizDiscountRate a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				
				bottomUpMap.values().stream().flatMap(s-> Job18_BizDiscountRate.createBizBottomUpCurve(bssd, s)).forEach(s->session.save(s));
				
				kicsMap.values().stream().flatMap(s-> Job18_BizDiscountRate.createBizBottomUpCurve(bssd, s)).forEach(s->session.save(s));
				
				completJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.info("zzz :  {},{}", e);
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}

	private static void job19() {
		if (jobList.contains("19")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG19);
			try {
				session.createQuery("delete BizIrCurveHis a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				
				for(IrCurve aa : rfCurveMap.values()) {
					double ufr  = EsgConstant.getSmParam().get(aa.getCurCd()).getUfr();
					double ufrt = EsgConstant.getSmParam().get(aa.getCurCd()).getUfrT();
					Job19_BizIrCruveHis.createBizIrCruveHis(bssd, "A", aa.getIrCurveId(), ufr, ufrt).stream().forEach(s -> session.save(s));
				}
				
				for(IrCurve aa : bottomUpMap.values()) {
					double ufr  = EsgConstant.getSmParam().get(aa.getCurCd()).getUfr();
					double ufrt = EsgConstant.getSmParam().get(aa.getCurCd()).getUfrT();
					Job19_BizIrCruveHis.createBizIrCruveHisFromRiskAdj(bssd, aa.getApplBizDv(), aa.getIrCurveId(), ufr, ufrt).stream().forEach(s -> session.save(s));
					
				}
				
				for(IrCurve aa : kicsMap.values()) {
					double ufr  = EsgConstant.getSmParam().get(aa.getCurCd()).getUfr();
					double ufrt = EsgConstant.getSmParam().get(aa.getCurCd()).getUfrT();
					Job19_BizIrCruveHis.createBizIrCruveHisFromRiskAdj(bssd, aa.getApplBizDv(), aa.getIrCurveId(), ufr, ufrt).stream().forEach(s -> session.save(s));
				}
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}	
	}

	private static void job25A() {
			if (jobList.contains("25A")) {
				session.beginTransaction();
				JobLog jobLog = startJogLog(EJob.ESG25);
				try {
					String bizDv ="I";
					
					session.createQuery("delete BizDiscountRateSce a where a.baseYymm=:param  and a.applyBizDv=:bizDv")
									.setParameter("param", bssd)
									.setParameter("bizDv", bizDv)
									.executeUpdate();
					
					for(IrCurve aa : bottomUpMap.values()) {	
						log.info("Bottotmup Scenario for {} : {}", aa.getCurCd(), aa.getIrCurveId(), aa.getIrCurveNm());
						
						double ufr  = EsgConstant.getSmParam().get(aa.getCurCd()).getUfr();
						double ufrt = EsgConstant.getSmParam().get(aa.getCurCd()).getUfrT();
						
	//					List<IrCurveHis> irCurveHisList = IrCurveHisDao.getIrCurveHis(bssd, aa.getIrCurveId());
						List<BizDiscountRate> bottomHisList1 = BizDiscountRateDao.getTermStructure(bssd, aa.getApplBizDv(), aa.getIrCurveId());
						
						List<EsgMst> esgMstList = EsgMstDao.getEsgMst(EBoolean.Y);
						
						if(bottomHisList1.size()==0) {
							log.error("No Ir Curve His Data exist for {}", bssd);
							System.exit(0);
						}
						if(esgMstList.size()==0) {
							log.error("No ESG MST with UseYn 'Y' exist for Hull and White Model");
							System.exit(0);
						}
						
	//					String irCurveId = irCurveHisList.get(0).getIrCurveId();
						EsgMst tempEsgMst       = esgMstList.get(0);
						List<BizEsgParam> bizParamHis = EsgParamDao.getBizEsgParam(bssd, tempEsgMst.getIrModelId());			
						
						if(bizParamHis.size()==0) {
							log.error("No ESG Parameter exist for Hull and White Model");
							System.exit(0);
						}
						
						
						HullWhite4j hw4f = new HullWhite4j(bssd, bottomHisList1, bizParamHis, ufr, ufrt, batchNum);
//						HullWhiteCont4j hw4f = new HullWhiteCont4j(bssd, bottomHisList1, bizParamHis, ufr, ufrt, batchNum, 0.0);
						
						List<CompletableFuture<List<BizDiscountRateSce>>> 	sceJobFutures =	
								IntStream.rangeClosed(1,batchNum)
								.mapToObj(batch-> CompletableFuture.supplyAsync(() 
										->  hw4f.getBizDiscountScenario(bssd, aa.getIrCurveId(), bizDv, batch), exe))
	//									->  Job24_EsgScenarioAsync.createEsgBizScenarioAsync1(bssd, aa, bottomHisList, bizParamHis, tempEsgMst, ufr, ufrt, shortRate, sceNo), exe))
	//									->  Job39_HullWhite4j.getBizDiscountScenarioAsyc(bssd, bizDv,aa.getIrCurveId(), bottomHisList1, bizParamHis, ufr, ufrt, sceNo), exe))
								.collect(Collectors.toList());
						
						List<BizDiscountRateSce> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
						
						int sceCnt = 1;
						for (BizDiscountRateSce bb :rst) {
							session.save(bb);
							if (sceCnt % 50 == 0) {
								session.flush();
								session.clear();
							}
							if (sceCnt % flushSize == 0) {
								log.info("Biz Bottom Scenario for {}  is processed {}/{} in Job 25A {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
							}
							sceCnt = sceCnt + 1;
						}
					}
					
					completJob("SUCCESS", jobLog);
				} catch (Exception e) {
					log.error("error : {}", e);
					completJob("ERROR", jobLog);
				}
				
				session.saveOrUpdate(jobLog);
				session.getTransaction().commit();
			}
		}

		private static void job25B() {
			if (jobList.contains("25B")) {
				session.beginTransaction();
				JobLog jobLog = startJogLog(EJob.ESG25);
				try {
					String bizDv ="K";
					
					session.createQuery("delete BizDiscountRateSce a where a.baseYymm=:param  and a.applyBizDv=:bizDv")
									.setParameter("param", bssd)
									.setParameter("bizDv", bizDv)
									.executeUpdate();
					
					for(IrCurve aa : kicsMap.values()) {	
						log.info("Bottotmup Scenario for {} : {}", aa.getCurCd(), aa.getIrCurveId(), aa.getIrCurveNm());
						
						double ufr  = EsgConstant.getSmParam().get(aa.getCurCd()).getUfr();
						double ufrt = EsgConstant.getSmParam().get(aa.getCurCd()).getUfrT();
						
	//					List<IrCurveHis> irCurveHisList = IrCurveHisDao.getIrCurveHis(bssd, aa.getIrCurveId());
						List<BizDiscountRate> bottomHisList1 = BizDiscountRateDao.getTermStructure(bssd, aa.getApplBizDv(), aa.getIrCurveId());
						
						List<EsgMst> esgMstList = EsgMstDao.getEsgMst(EBoolean.Y);
						
						if(bottomHisList1.size()==0) {
							log.error("No Ir Curve His Data exist for {}", bssd);
							System.exit(0);
						}
						if(esgMstList.size()==0) {
							log.error("No ESG MST with UseYn 'Y' exist for Hull and White Model");
							System.exit(0);
						}
						
	//					String irCurveId = irCurveHisList.get(0).getIrCurveId();
						EsgMst tempEsgMst       = esgMstList.get(0);
						List<BizEsgParam> bizParamHis = EsgParamDao.getBizEsgParam(bssd, tempEsgMst.getIrModelId());			
						
						if(bizParamHis.size()==0) {
							log.error("No ESG Parameter exist for Hull and White Model");
							System.exit(0);
						}
						
						
						HullWhite4j hw4f = new HullWhite4j(bssd, bottomHisList1, bizParamHis, ufr, ufrt, batchNum);
						
						List<CompletableFuture<List<BizDiscountRateSce>>> 	sceJobFutures =	
								IntStream.rangeClosed(1,batchNum)
								.mapToObj(batch-> CompletableFuture.supplyAsync(() 
										->  hw4f.getBizDiscountScenario(bssd, aa.getIrCurveId(), bizDv, batch), exe))
	//									->  Job24_EsgScenarioAsync.createEsgBizScenarioAsync1(bssd, aa, bottomHisList, bizParamHis, tempEsgMst, ufr, ufrt, shortRate, sceNo), exe))
	//									->  Job39_HullWhite4j.getBizDiscountScenarioAsyc(bssd, bizDv,aa.getIrCurveId(), bottomHisList1, bizParamHis, ufr, ufrt, sceNo), exe))
								.collect(Collectors.toList());
						
						List<BizDiscountRateSce> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
						
						int sceCnt = 1;
						for (BizDiscountRateSce bb :rst) {
							session.save(bb);
							if (sceCnt % 50 == 0) {
								session.flush();
								session.clear();
							}
							if (sceCnt % flushSize == 0) {
								log.info("Biz Bottom Scenario for {}  is processed {}/{} in Job 24B {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
							}
							sceCnt = sceCnt + 1;
						}
					}
					
					completJob("SUCCESS", jobLog);
				} catch (Exception e) {
					log.error("error : {}", e);
					completJob("ERROR", jobLog);
				}
			
				session.saveOrUpdate(jobLog);
				session.getTransaction().commit();
		}
	}

	private static void job24A() {
		if (jobList.contains("24A")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG24);
			try {
				String bizDv ="I";
				
				session.createQuery("delete BizDiscountRateSce a where a.baseYymm=:param  and a.applyBizDv=:bizDv")
								.setParameter("param", bssd)
								.setParameter("bizDv", bizDv)
								.executeUpdate();
				
				for(IrCurve aa : bottomUpMap.values()) {	
					log.info("Bottotmup Scenario for {} : {}", aa.getCurCd(), aa.getIrCurveId(), aa.getIrCurveNm());
					
					double ufr  = EsgConstant.getSmParam().get(aa.getCurCd()).getUfr();
					double ufrt = EsgConstant.getSmParam().get(aa.getCurCd()).getUfrT();
					
//					List<IrCurveHis> irCurveHisList = IrCurveHisDao.getIrCurveHis(bssd, aa.getIrCurveId());
					List<BottomupDcnt> bottomHisList = BottomupDcntDao.getTermStructure(bssd, aa.getIrCurveId());
//					List<BizDiscountRate> bottomHisList1 = BizDiscountRateDao.getTermStructure(bssd, aa.getApplBizDv(), aa.getIrCurveId());
					
					List<EsgMst> esgMstList = EsgMstDao.getEsgMst(EBoolean.Y);
					
					if(bottomHisList.size()==0) {
						log.error("No Ir Curve His Data exist for {}", bssd);
						System.exit(0);
					}
					if(esgMstList.size()==0) {
						log.error("No ESG MST with UseYn 'Y' exist for Hull and White Model");
						System.exit(0);
					}
					
//					String irCurveId = irCurveHisList.get(0).getIrCurveId();
					EsgMst tempEsgMst       = esgMstList.get(0);
					List<BizEsgParam> bizParamHis = EsgParamDao.getBizEsgParam(bssd, tempEsgMst.getIrModelId());			
					
					if(bizParamHis.size()==0) {
						log.error("No ESG Parameter exist for Hull and White Model");
						System.exit(0);
					}
					
//					double shortRate = IrCurveHisDao.getBizIrCurveHis(bssd, "A", aa.getIrCurveId()).stream()
//													.filter(s ->s.getMatCd().equals("M0001"))
//													.map(s -> s.getIntRate()).findFirst().orElse(0.01);
					
					double shortRate = BottomupDcntDao.getTermStructure(bssd, aa.getIrCurveId()).stream()
													.filter(s ->s.getMatCd().equals("M0001"))
													.map(s -> s.getIntRate()).findFirst().orElse(0.01);
					
					List<CompletableFuture<List<BizDiscountRateSce>>> 	sceJobFutures =	
							IntStream.rangeClosed(1,batchNum)
							.mapToObj(sceNo-> CompletableFuture.supplyAsync(() 
									->  Job24_EsgScenarioAsync.createEsgBizScenarioAsync1(bssd, aa, bottomHisList, bizParamHis, tempEsgMst, ufr, ufrt, shortRate, sceNo), exe))
//									->  Job39_HullWhite4j.getBizDiscountScenarioAsyc(bssd, bizDv,aa.getIrCurveId(), bottomHisList1, bizParamHis, ufr, ufrt, sceNo), exe))
							.collect(Collectors.toList());
					
					List<BizDiscountRateSce> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
					
					int sceCnt = 1;
					for (BizDiscountRateSce bb :rst) {
						session.save(bb);
						if (sceCnt % 50 == 0) {
							session.flush();
							session.clear();
						}
						if (sceCnt % flushSize == 0) {
							log.info("Biz Bottom Scenario for {}  is processed {}/{} in Job 24A {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
						}
						sceCnt = sceCnt + 1;
					}
				}
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.error("error : {}", e);
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	
	private static void job24B() {
		if (jobList.contains("24B")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG24);
			try {
				String bizDv ="K";
				
				session.createQuery("delete BizDiscountRateSce a where a.baseYymm=:param  and a.applyBizDv=:bizDv")
								.setParameter("param", bssd)
								.setParameter("bizDv", bizDv)
								.executeUpdate();
				
				for(IrCurve aa : kicsMap.values()) {	
					log.info("Bottotmup Scenario for {} : {}", aa.getCurCd(), aa.getIrCurveId(), aa.getIrCurveNm());
					
					double ufr  = EsgConstant.getSmParam().get(aa.getCurCd()).getUfr();
					double ufrt = EsgConstant.getSmParam().get(aa.getCurCd()).getUfrT();
					
//					List<IrCurveHis> irCurveHisList = IrCurveHisDao.getIrCurveHis(bssd, aa.getIrCurveId());
					List<BottomupDcnt> bottomHisList = BottomupDcntDao.getTermStructure(bssd, aa.getIrCurveId());
					
					List<EsgMst> esgMstList = EsgMstDao.getEsgMst(EBoolean.Y);
					
					if(bottomHisList.size()==0) {
						log.error("No Ir Curve His Data exist for {}", bssd);
						System.exit(0);
					}
					if(esgMstList.size()==0) {
						log.error("No ESG MST with UseYn 'Y' exist for Hull and White Model");
						System.exit(0);
					}
					
//				String irCurveId = irCurveHisList.get(0).getIrCurveId();
					EsgMst tempEsgMst       = esgMstList.get(0);
					List<BizEsgParam> bizParamHis = EsgParamDao.getBizEsgParam(bssd, tempEsgMst.getIrModelId());			
					
					if(bizParamHis.size()==0) {
						log.error("No ESG Parameter exist for Hull and White Model");
						System.exit(0);
					}
					
//					double shortRate = IrCurveHisDao.getBizIrCurveHis(bssd, "A", aa.getIrCurveId()).stream()
//													.filter(s ->s.getMatCd().equals("M0001"))
//													.map(s -> s.getIntRate()).findFirst().orElse(0.01);
					
					double shortRate = BottomupDcntDao.getTermStructure(bssd, aa.getIrCurveId()).stream()
													.filter(s ->s.getMatCd().equals("M0001"))
													.map(s -> s.getIntRate()).findFirst().orElse(0.01);
					
					List<CompletableFuture<List<BizDiscountRateSce>>> 	sceJobFutures =	
							IntStream.rangeClosed(1,batchNum)
							.mapToObj(sceNo-> CompletableFuture.supplyAsync(() 
									->  Job24_EsgScenarioAsync.createEsgBizScenarioAsync1(bssd, aa, bottomHisList, bizParamHis, tempEsgMst, ufr, ufrt, shortRate, sceNo), exe))
							.collect(Collectors.toList());
					
					List<BizDiscountRateSce> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
					
					int sceCnt = 1;
					for (BizDiscountRateSce bb :rst) {
						session.save(bb);
						if (sceCnt % 50 == 0) {
							session.flush();
							session.clear();
						}
						if (sceCnt % flushSize == 0) {
							log.info("Biz Bottom Scenario for {}  is processed {}/{} in Job 24B {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
						}
						sceCnt = sceCnt + 1;
					}
				}
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.error("error : {}", e);
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	


	private static void job24() {
	//		금리 시나리오의 적용 방법에 따라 발산형과 수렴형으로 분기 (Y : 수렴형으로 smith wilson 의 장기금리로 수렴함)		
		if (jobList.contains("24")) {
			log.info("Bottom Up Scenario Async Mode Start !!!");
			if(irSceGenSmithWilsonApply.equals("Y")) {
				job24Sw();
//				job24SwLoop();
			}
			else {
				job24Add();
			}
		}
	}

	private static void job24Sw() {
		if (jobList.contains("24")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG24);
			log.info("Bottom Up Scenario With SmithWilson Method Start !!!");
			try {
				session.createQuery("delete BizDiscountRateSce a where a.baseYymm=:param and a.applyBizDv =:param2")
						.setParameter("param", bssd)
						.setParameter("param2", "I")
						.executeUpdate();
				
				Map<String, Double> lpMap = LiqPremiumDao.getBizLiqPremium(bssd).stream().collect(toMap(s->s.getMatCd(), s->s.getApplyLiqPrem()));
				log.info("Load Liquidity Premium. Loaded Data Size :{}.", lpMap.size());
				
				
				for(IrCurve aa : bottomUpMap.values()) {		
					log.info("IrCurveId :  {},{}", aa.getIrCurveId(), aa.getRefCurveId());
					log.info("Current Thread 1:  {},{},{}", Thread.currentThread().getId(), Thread.currentThread().getName(), irSceGenSmithWilsonApply);
					
					double ufr  =  EsgConstant.getSmParam().get(aa.getCurCd()).getUfr();
					double ufrt =  EsgConstant.getSmParam().get(aa.getCurCd()).getUfrT();
					int sceCnt = 1;
					
					log.info("chect Time : {},{}", LocalDateTime.now() );
					
//				Stream<IrSce> rStream = IrCurveHisDao.getIrCurveSce(bssd, aa.getRefCurveId()).stream(); 
					Map<String, List<IrSce>> sceMap = IrCurveHisDao.getIrCurveSce(bssd, aa.getRefCurveId()).stream()
//						.filter(s-> s.getSceNo().contains("100"))
							.collect(groupingBy(s -> s.getSceNo(), toList()));
					
					log.info("zzz : {},{}", LocalDateTime.now(), sceMap.size());
					
					List<CompletableFuture<List<BizDiscountRateSce>>> sceJobFutures ;
//				Stream<CompletableFuture<List<BizDiscountRateSce>>> sceJobFutures ;
					
//				금리 시나리오의 적용 방법에 따라 발산형과 수렴형으로 분기 (Y : 수렴형으로 smith wilson 의 장기금리로 수렴함)					
					sceJobFutures =
							sceMap.entrySet().stream()
							.map(entry -> CompletableFuture.supplyAsync(() 
									->  Job24_BottomUpScenario.createBottomUpScenario(bssd, aa.getIrCurveId(),entry.getKey(), entry.getValue(), lpMap, ufr, ufrt), exe))
							.collect(toList());
					
					
					List<BizDiscountRateSce> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(toList());
					
					log.info("rstSize : {}", rst.size());
					for(BizDiscountRateSce cc : rst) {
						session.save(cc);
						if (sceCnt % 50 == 0) {
							session.flush();
							session.clear();
						}
						if (sceCnt % flushSize == 0) {
							log.info("Biz Discount Scenario for {}  is processed {}/{} in Job 24 {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
						}
						sceCnt = sceCnt + 1;
					}
				}
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}

	private static void job24Add() {
		if (jobList.contains("24")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG24);
			log.info("Bottom Up Scenario with Spreading Method Start !!!");
			try {
				session.createQuery("delete BizDiscountRateSce a where a.baseYymm=:param and a.applyBizDv =:param2")
				.setParameter("param", bssd)
				.setParameter("param2", "I")
				.executeUpdate();
				
				Map<String, Double> lpMap = LiqPremiumDao.getBizLiqPremium(bssd).stream().collect(toMap(s->s.getMatCd(), s->s.getApplyLiqPrem()));
				log.info("Load Liquidity Premium. Loaded Data Size :{}.", lpMap.size());
				
				for(IrCurve aa : bottomUpMap.values()) {		
					log.info("IrCurveId :  {},{},{}", aa.getIrCurveId(), aa.getRefCurveId(), LocalTime.now());
					
					int sceCnt = 1;
					Map<String, List<BizIrCurveSce>> sceMap = IrCurveHisDao.getBizIrCurveSce(bssd, "A", aa.getRefCurveId()).stream().collect(groupingBy(s -> s.getSceNo(), toList()));
					
					List<CompletableFuture<List<BizDiscountRateSce>>> sceJobFutures ;
					sceJobFutures =sceMap.entrySet().stream()
							.map(entry -> CompletableFuture.supplyAsync(
									()->Job24_BottomUpScenario.createBottomUpScenarioAdd(bssd, "I", aa.getIrCurveId(),entry.getKey(), entry.getValue(), lpMap), exe))
							.collect(toList());
					
					List<BizDiscountRateSce> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(toList());
					log.info("rstSize : {}", rst.size());
					
					for(BizDiscountRateSce cc : rst) {
						session.save(cc);
						if (sceCnt % 50 == 0) {
							session.flush();
							session.clear();
						}
						if (sceCnt % flushSize == 0) {
							log.info("Biz Discount Scenario for {}  is processed {}/{} in Job 24 {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
						}
						sceCnt = sceCnt + 1;
					}	
				}
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}

	private static void job25() {
		if (jobList.contains("25")) {
			log.info("Bottom Up Scenario Sync Start !!!");
			if(irSceGenSmithWilsonApply.equals("Y")) {
				job25SwLoop();
			}
			else {
				job24Add();
			}
		}
	}

	private static void job25SwLoop() {
		if (jobList.contains("25")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG24);
			log.info("Bottom Up Scenario with SmithWilson Method Start !!!");
			try {
				session.createQuery("delete BizDiscountRateSce a where a.baseYymm=:param and a.applyBizDv =:param2")
				.setParameter("param", bssd)
				.setParameter("param2", "I")
				.executeUpdate();
				
				Map<String, Double> lpMap = LiqPremiumDao.getBizLiqPremium(bssd).stream().collect(toMap(s->s.getMatCd(), s->s.getApplyLiqPrem()));
				log.info("Load Liquidity Premium. Loaded Data Size :{}.", lpMap.size());
				
				for(IrCurve aa : bottomUpMap.values()) {		
					log.info("IrCurveId :  {},{}", aa.getIrCurveId(), aa.getRefCurveId());
					
					int sceCnt =0;
					List<BizDiscountRateSce> rst= Job24_BottomUpScenario.createBottomUpScenarioSw(bssd, "I", aa, lpMap);
					log.info("rstSize : {}", rst.size());
					
					for(BizDiscountRateSce cc : rst) {
						session.save(cc);
						if (sceCnt % 50 == 0) {
							session.flush();
							session.clear();
						}
						if (sceCnt % flushSize == 0) {
							log.info("Biz Discount Scenario for {}  is processed {}/{} in Job 24 {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
						}
						sceCnt = sceCnt + 1;
					}
				}
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}

	private static void job26() {
			if (jobList.contains("26")) {
				session.beginTransaction();
				JobLog jobLog = startJogLog(EJob.ESG26);
				try {
					List<String> curveList =kicsMap.values().stream().map(s->s.getIrCurveId()).collect(toList());
					session.createQuery("delete BottomupDcnt a where a.baseYymm=:param and a.irCurveId in :param2")
					.setParameter("param", bssd).setParameter("param2", curveList).executeUpdate();
					
	//				int llpMonth = Integer.parseInt(EsgConstant.getStrConstant().get("llp").split("M")[1]);
	//				Map<String, Double> lpMap = LiquidPremiumModel.createLiqPremium(llpMonth, kicsVolAdjust);
					
					for(IrCurve aa : kicsMap.values()) {
						Job22_BottomUp.createKicsTermStructureSw(bssd, aa, kicsVolAdjust).stream().forEach(s -> session.save(s));
					}
					
					completJob("SUCCESS", jobLog);
				} catch (Exception e) {
					completJob("ERROR", jobLog);
				}
				
				session.saveOrUpdate(jobLog);
				session.getTransaction().commit();
			}
		}

	private static void job27() {
		if (jobList.contains("27")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG27);
			try {
				String bizDv ="K";
				
				session.createQuery("delete BizDiscountRate a where a.baseYymm=:param  and a.applyBizDv=:bizDv")
				.setParameter("param", bssd).setParameter("bizDv", bizDv).executeUpdate();
				
				for(IrCurve aa : kicsMap.values()) {
					log.info("IrCurve for Kics : {},{}", aa.getIrCurveId(), aa.getRefCurveId());
					Job23_BizDiscountRate.getKicsBizDcntRate(bssd, aa.getIrCurveId()).stream().forEach(s -> session.saveOrUpdate(s));
				}
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}

//	private static void job28() {
//		if (jobList.contains("28")) {
//			log.info("KICS IR Scenario with SmithWilsion Method Async Mode Start !!!");
//			if(irSceGenSmithWilsonApply.equals("Y")) {
//				job29SwLoop();
//			}else {
//				job28AddAsync(); 
//			}
//		}	
//	}

//	private static void job28AddAsync() {
//		if (jobList.contains("28")) {
//			session.beginTransaction();
//			JobLog jobLog = startJogLog(EJob.ESG28);
//			log.info("KICS IR Scenario with Spreading Method Start !!!");
//			try {
//				session.createQuery("delete BizDiscountRateSce a where a.baseYymm=:param and a.applyBizDv =:param2")
//				.setParameter("param", bssd)
//				.setParameter("param2", "K")
//				.executeUpdate();
//				
//				int llpMonth = Integer.parseInt(EsgConstant.getStrConstant().get("llp").split("M")[1]);
//				Map<String, Double> lpMap = LiquidPremiumModel.createLiqPremium(llpMonth, kicsVolAdjust);
//				
//				for(IrCurve aa : kicsMap.values()) {		
//					log.info("IrCurveId :  {},{}", aa.getIrCurveId(), aa.getRefCurveId());
//					int sceCnt = 1;
////				List<BizDiscountRateSce> rst = Job24_BottomUpScenario.createBottomUpScenarioAddAsync(bssd, "K", aa, lpMap, exe);
//					
//					List<CompletableFuture<List<BizDiscountRateSce>>> sceJobFutures ;
//					Map<String, List<BizIrCurveSce>> sceMap = IrCurveHisDao.getBizIrCurveSce(bssd, "A", aa.getRefCurveId()).stream().collect(groupingBy(s -> s.getSceNo(), toList()));
//					
//					sceJobFutures =sceMap.entrySet().stream()
//							.map(entry -> CompletableFuture.supplyAsync(
//									()->Job24_BottomUpScenario.createBottomUpScenarioAdd(bssd, "K", aa.getIrCurveId(),entry.getKey(), entry.getValue(), lpMap), exe))
//							.collect(toList());
//					
//					List<BizDiscountRateSce> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(toList());
//					
//					log.info("rstSize : {}", rst.size());
//					
//					for(BizDiscountRateSce cc : rst) {
//						session.save(cc);
//						if (sceCnt % 100 == 0) {
//							session.flush();
//							session.clear();
//						}
//						if (sceCnt % flushSize == 0) {
//							log.info("Biz Discount Scenario for {}  is processed {}/{} in Job 27 {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
//						}
//						sceCnt = sceCnt + 1;
//					}	
//				}
//				
//				completJob("SUCCESS", jobLog);
//			} catch (Exception e) {
//				completJob("ERROR", jobLog);
//			}
//			
//			session.saveOrUpdate(jobLog);
//			session.getTransaction().commit();
//		}
//	}
	
	//TODO
	private static void job29() {
		if (jobList.contains("29")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG29);
			
			try {		
	//			Job19_AfnsScenarioOld.createAfnsShockScenario(FinUtils.toEndOfMonth(bssd), "KRW", "111111C", 1, 0.0000000001, 0.0032).forEach(s -> session.saveOrUpdate(s));			
				Map<String, List<?>> irShockSenario = new TreeMap<String, List<?>>();
				
				irShockSenario = Job29_AfnsScenario.createAfnsShockScenario(FinUtils.toEndOfMonth(bssd), mode, irCurveId, baseCurveId, tenorList, 
						                                                    stBssd, isRealNumber, cmpdType, dt, sigmaInit, dayCountBasis,
						                                                    ltfrL, ltfrA, ltfrT, liqPrem, lambdaMin, lambdaMax, 
						                                                    prjYear, errorTolerance, kalmanItrMax, confInterval, epsilonInit);
				
				if(irShockSenario != null) {				
					for(Map.Entry<String, List<?>> rslt : irShockSenario.entrySet()) {
						rslt.getValue().forEach(s -> session.saveOrUpdate(s));
						
	//					if(!rslt.getKey().equals("CURVE")) rslt.getValue().forEach(s -> session.saveOrUpdate(s));
	//					rslt.getValue().forEach(s -> log.info("Arbitrage Free Nelson Siegle Scenario Result : {}", s.toString()));
					}
				}
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				e.printStackTrace();
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}	
	
//	private static void job29SwLoop() {
//		if (jobList.contains("29")) {
//			session.beginTransaction();
//			JobLog jobLog = startJogLog(EJob.ESG28);
//			log.info("KICS IR Scenario with SmithWilsion Method Start !!!");
//			try {
//				session.createQuery("delete BizDiscountRateSce a where a.baseYymm=:param and a.applyBizDv =:param2")
//						.setParameter("param", bssd)
//						.setParameter("param2", "K")
//						.executeUpdate();
//				
//				int llpMonth = Integer.parseInt(EsgConstant.getStrConstant().get("llp").split("M")[1]);
//				Map<String, Double> lpMap = LiquidPremiumModel.createLiqPremium(llpMonth, kicsVolAdjust);
//				
//				for(IrCurve aa : kicsMap.values()) {		
//					log.info("IrCurveId :  {},{}", aa.getIrCurveId(), aa.getRefCurveId());
//		
//					int sceCnt =0;
//					List<BizDiscountRateSce> rst= Job24_BottomUpScenario.createBottomUpScenarioSw(bssd, "K", aa, lpMap);
//					log.info("rstSize : {}", rst.size());
//					
//					for(BizDiscountRateSce cc : rst) {
//						session.save(cc);
//						if (sceCnt % 50 == 0) {
//							session.flush();
//							session.clear();
//						}
//						if (sceCnt % flushSize == 0) {
//							log.info("KICS Discount Scenario for {}  is processed {}/{} in Job 27 {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
//						}
//						sceCnt = sceCnt + 1;
//					}
//				}
//				completJob("SUCCESS", jobLog);
//			} catch (Exception e) {
//				completJob("ERROR", jobLog);
//			}
//			
//			session.saveOrUpdate(jobLog);
//			session.getTransaction().commit();
//		}
//	}
//
//	private static void job29Add() {
//		if (jobList.contains("29")) {
//			session.beginTransaction();
//			JobLog jobLog = startJogLog(EJob.ESG28);
//			log.info("KICS IR Scenario with Spreading Method Start !!!");
//			try {
//				session.createQuery("delete BizDiscountRateSce a where a.baseYymm=:param and a.applyBizDv =:param2")
//				.setParameter("param", bssd)
//				.setParameter("param2", "K")
//				.executeUpdate();
//				
//				int llpMonth = Integer.parseInt(EsgConstant.getStrConstant().get("llp").split("M")[1]);
//				Map<String, Double> lpMap = LiquidPremiumModel.createLiqPremium(llpMonth, kicsVolAdjust);
//				
//				
//				for(IrCurve aa : kicsMap.values()) {		
//					log.info("IrCurveId :  {},{}", aa.getIrCurveId(), aa.getRefCurveId());
//					int sceCnt = 1;
//					
//					List<BizDiscountRateSce> rst = new ArrayList<BizDiscountRateSce>();
//					
//					Map<String, List<BizIrCurveSce>> sceMap = IrCurveHisDao.getBizIrCurveSce(bssd, "A", aa.getRefCurveId()).stream().collect(groupingBy(s -> s.getSceNo(), toList()));
//					
//					for(Map.Entry<String, List<BizIrCurveSce>> entry : sceMap.entrySet()) {
//						log.info("sceMap : {}", entry.getKey());
//						rst.addAll(Job24_BottomUpScenario.createBottomUpScenarioAdd(bssd, "K", aa.getIrCurveId(),entry.getKey(), entry.getValue(), lpMap));
//					}
//					
//					log.info("rstSize : {}", rst.size());
//					
//					for(BizDiscountRateSce cc : rst) {
//						session.save(cc);
//						if (sceCnt % 100 == 0) {
//							session.flush();
//							session.clear();
//						}
//						if (sceCnt % flushSize == 0) {
//							log.info("Biz Discount Scenario for {}  is processed {}/{} in Job 27 {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
//						}
//						sceCnt = sceCnt + 1;
//					}	
//					
//					rst.clear();
//					sceMap.clear();
//				}
//				
//				completJob("SUCCESS", jobLog);
//			} catch (Exception e) {
//				completJob("ERROR", jobLog);
//			}
//			
//			session.saveOrUpdate(jobLog);
//			session.getTransaction().commit();
//		}
//	}

	private static void job31() {
		if (jobList.contains("31")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG31);
			try {
				List<String> entryList = new ArrayList<String>();
				
				Map<String, Map<String, Double>> corrMap  =StdAssetVolDao.getStdAssetCorrByMonth(bssd, volCalcId).stream()
						.collect(groupingBy(StdAssetCorr::getMvId, toMap(StdAssetCorr::getRefMvId, StdAssetCorr::getMvHisCorr)));

				CorrelatedRandomVectorGenerator gen = Job30_EsgStockScenario.createRandom(bssd, corrMap);

				entryList.addAll(corrMap.keySet());
				
				for(int i =1 ; i<= batchNum *100; i++) {
					List<EsgRandom> rndList = new ArrayList<EsgRandom>();
					for(int j=1 ; j<= projectionYear * 12; j++) {	
						double[] sce = gen.nextVector();
						for(int k=0; k < sce.length; k++) {
							rndList.add(EsgRandom.builder()
											.baseYymm(bssd)
											.stdAsstCd(entryList.get(k))
											.volCalcId(volCalcId)
											.sceNo(i)
											.matNum(j)
											.rndNum(sce[k])
											.lastModifiedBy("ESG")
											.lastUpdateDate(LocalDateTime.now())
											.build()
										);	
						}
					}
//					rndList.forEach(s->log.info("random : {},{},{},{}", s.getStdAsstCd(), s.getMatNum(), s.getSceNo(), s.getRndNum()));
					rndList.forEach(s-> saveOrUpdate(s));
				}
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.warn("Error : {}", e);
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	
	private static void job32() {
		if (jobList.contains("32")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG32);
			try {
				
//				NormalizedRandomGenerator gen = Job30_EsgStockScenario.createRandom(bssd);
//				
//				List<CompletableFuture<List<EsgRandom>>> 	sceJobFutures =	
//						IntStream.rangeClosed(1,batchNum)
//						.mapToObj(batch-> CompletableFuture.supplyAsync(() 
//								->  hw4f.getBizDiscountScenario(bssd, aa.getIrCurveId(), bizDv, batch), exe))
////									->  Job24_EsgScenarioAsync.createEsgBizScenarioAsync1(bssd, aa, bottomHisList, bizParamHis, tempEsgMst, ufr, ufrt, shortRate, sceNo), exe))
////									->  Job39_HullWhite4j.getBizDiscountScenarioAsyc(bssd, bizDv,aa.getIrCurveId(), bottomHisList1, bizParamHis, ufr, ufrt, sceNo), exe))
//						.collect(Collectors.toList());
//				
//				List<EsgRandom> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
//				
//				int sceCnt = 1;
//				for (BizDiscountRateSce bb :rst) {
//					session.save(bb);
//					if (sceCnt % 50 == 0) {
//						session.flush();
//						session.clear();
//					}
//					if (sceCnt % flushSize == 0) {
//						log.info("Biz Bottom Scenario for {}  is processed {}/{} in Job 25A {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
//					}
//					sceCnt = sceCnt + 1;
//				}
//				batchNum=2;
				
				NormalizedRandomGenerator gen = Job30_EsgStockScenario.createRandom(bssd);
				
				for(int i =1 ; i<= batchNum *100; i++) {
					List<EsgRandom> rndList = new ArrayList<EsgRandom>();
					for(int j=1 ; j<= projectionYear * 12; j++) {	
						double sce = gen.nextNormalizedDouble();
							rndList.add(EsgRandom.builder()
											.baseYymm(bssd)
											.stdAsstCd("SHORT_RATE")
											.volCalcId("N(0,1)")
											.sceNo(i)
											.matNum(j)
											.rndNum(sce)
											.lastModifiedBy("ESG")
											.lastUpdateDate(LocalDateTime.now())
											.build()
										);	
						
					}
					rndList.forEach(s-> saveOrUpdate(s));
				}
				completJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				log.warn("Error : {}", e);
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
//	private static void job32() {
//		if (jobList.contains("32")) {
//			session.beginTransaction();
//			JobLog jobLog = startJogLog(EJob.ESG32);
//			try {
//				String bizDv ="I";
//				
//				Map<String, Map<String, Double>> corrMap  =StdAssetVolDao.getStdAssetCorrByMonth(bssd, volCalcId).stream()
//															.collect(groupingBy(StdAssetCorr::getMvId, toMap(StdAssetCorr::getRefMvId, StdAssetCorr::getMvHisCorr)));
//				
//				List<String> entryList = new ArrayList<String>();
//				entryList.addAll(corrMap.keySet().stream().filter(s-> !s.equals("MSCIEM"))
//														  .filter(s-> !s.equals("MSCI"))
//														  .collect(toList())
//								);
//				
//				rfCurveMap.entrySet().forEach(s-> log.info("zzzz : {},{}", s.getKey(), s.getValue()));
//				bottomUpMap.entrySet().forEach(s-> log.info("qqqqq : {},{}",s.getKey(), s.getValue()));
//				
//				
////				Map<Integer, double[]> driftMap = Job09_EsgStockScenario.createDrift(bssd, entryList, rfCurveMap, projectionYear);
////				Map<Integer, double[]> sigmaMap = Job09_EsgStockScenario.createSigma(bssd, bizDv, 	entryList, projectionYear);
////				Map<Integer, Map<String, Double>> driftMap = Job09_EsgStockScenario.createDrift1(bssd, entryList, rfCurveMap, projectionYear);
////				Map<Integer,  Map<String, Double>> sigmaMap = Job09_EsgStockScenario.createSigma1(bssd, bizDv, 	entryList, projectionYear);
////				Map<String, Map<Integer, Double>> driftMap = Job09_EsgStockScenario.createDrift3(bssd, entryList, "A", rfCurveMap, projectionYear);
//				
//				Map<String, Map<Integer, Double>> driftMap = Job30_EsgStockScenario.createDrift3(bssd, bizDv, entryList, bottomUpMap, projectionYear);
//				Map<String, Map<Integer, Double>> sigmaMap = Job30_EsgStockScenario.createSigma2(bssd, bizDv, entryList, projectionYear);
//				
//				
////				for(Map.Entry<String, Map<Integer, Double>> entry : driftMap.entrySet()) {
////						for(Map.Entry<Integer, Double> inner : entry.getValue().entrySet()) {
////							log.info("zzzz1111 : {},{},{}",entry.getKey(), inner.getKey(), inner.getValue());
////						}
////				}
////				for(Map.Entry<String, Map<Integer, Double>> entry : sigmaMap.entrySet()) {
////						for(Map.Entry<Integer, Double> inner : entry.getValue().entrySet()) {
////							log.info("zzzz1111 : {},{},{}",entry.getKey(), inner.getKey(), inner.getValue());
////						}
////				}
////				entryList.forEach(s->log.info("zzzz1111 : {}", s));
//
//				Map<String, Double> asstHisMap = new HashMap<String, Double>();									// 산출기준일 종가 S0
//				List<StdAssetMst> assetList = StdAssetDao.getStdAssetMst();
//				
//				for(StdAssetMst asset : assetList) {
//					String mvId =asset.getStdAsstCd();
//					if(asset.getStdAsstTypCd().equals("STOCK")){							
//						asstHisMap.putIfAbsent(asset.getStdAsstCd(), StdAssetDao.getStdStockAssetHis(bssd,mvId).getStdAsstPrice());
//					}else if(asset.getStdAsstTypCd().equals("BOND")){
//						asstHisMap.putIfAbsent(asset.getStdAsstCd(), StdAssetDao.getStdBondAssetHis(bssd, mvId).getIntRate());
//					}
//					else if(asset.getStdAsstTypCd().equals("SHORT_RATE")) {
////						asstHisMap.putIfAbsent(asset.getStdAsstCd(), IrCurveHisDao.getShortRateHis(bssd, "1010000").getIntRate());
//						asstHisMap.putIfAbsent(asset.getStdAsstCd(), BottomupDcntDao.getShortRateHis(bssd, "RF_KRW_BU").getIntRate());
//					}
//				}
//				log.info("aaaa : {},{}", asstHisMap.size(), driftMap.size());
//				CorrelatedRandomVectorGenerator gen = Job30_EsgStockScenario.createRandom(bssd, corrMap);
//				
//				for(int i =1 ; i<= batchNum *100 ; i++) {
////				for(int i =1 ; i<= 2 ; i++) {
////					Job09_EsgStockScenario.createEsgScenario(bssd, bizDv, entryList, asstHisMap, driftMap, sigmaMap, gen, i, projectionYear).forEach(s->saveOrUpdate(s));
////					Job09_EsgStockScenario.createEsgScenario1(bssd, bizDv, entryList, asstHisMap, driftMap, sigmaMap, gen, i, projectionYear).forEach(s->saveOrUpdate(s));
//					
//					Job30_EsgStockScenario.createEsgScenario2(bssd, bizDv, entryList, asstHisMap, driftMap, sigmaMap, gen, i, projectionYear).forEach(s->saveOrUpdate(s));
//				}
//					
//				completJob("SUCCESS", jobLog);
//			}	
//			catch (Exception e) {
//				log.warn("Error : {}", e);
//				completJob("ERROR", jobLog);
//			}
//			session.saveOrUpdate(jobLog);
//			session.getTransaction().commit();
//		}
//	}
	
//	private static void runJob(EAdvJob jobNo) {
//		session.beginTransaction();
//		JobLog jobLog = startJogLog(jobNo);
//		try {
//			String bizDv ="I";
//				
//			StdAssetDao.getStdStockYield(bssd, bizDv).forEach(s->saveOrUpdate(s));
//				
//			completJob("SUCCESS", jobLog);
//		}	
//		catch (Exception e) {
//			log.warn("Error : {}", e);
//			completJob("ERROR", jobLog);
//		}
//		session.saveOrUpdate(jobLog);
//		session.getTransaction().commit();
//	}
	
	private static void job33() {
		if (jobList.contains("33")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG33);
			try {
				String bizDv ="I";
				
				StdAssetDao.getStdStockYield(bssd, bizDv).forEach(s->saveOrUpdate(s));
				
				completJob("SUCCESS", jobLog);
			}	
			catch (Exception e) {
				log.warn("Error : {}", e);
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job34() {
		if (jobList.contains("34")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG34);
			try {
				session.createQuery("delete BizStockYield a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				
				
//				List<String> assetList = StdAssetDao.getStdAssetMst().stream()
//													.filter(s->s.getCurCd().equals("KRW"))
//													.map(StdAssetMst::getStdAsstCd).collect(toList());
				
//				Map<String, List<String>> assetMap = StdAssetDao.getStdAssetMst().stream()
//															.collect(groupingBy(StdAssetMst::getCurCd, mapping(StdAssetMst::getStdAsstCd, toList())));
				Map<String, List<StdAssetMst>> assetMap = StdAssetDao.getStdAssetMst().stream()
																.collect(groupingBy(StdAssetMst::getCurCd, toList()));
															
//													
				
				bottomUpMap.values().stream().flatMap(s-> Job34_EsgDetermieSce.createDetermineSce(bssd, s, assetMap.get(s.getCurCd()))).forEach(s->saveOrUpdate(s));
				kicsMap.values().stream().flatMap(s-> Job34_EsgDetermieSce.createDetermineSce(bssd, s, assetMap.get(s.getCurCd()))).forEach(s->saveOrUpdate(s));
				
				completJob("SUCCESS", jobLog);
			}	
			catch (Exception e) {
				log.warn("Error : {}", e);
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	
	private static void job34A() {
		if (jobList.contains("34A")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG34);
			try {
				session.createQuery("delete BizStockYield a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				
				
//				List<String> assetList = StdAssetDao.getStdAssetMst().stream()
//													.filter(s->s.getCurCd().equals("KRW"))
//													.map(StdAssetMst::getStdAsstCd).collect(toList());
				
//				Map<String, List<String>> assetMap = StdAssetDao.getStdAssetMst().stream()
//															.collect(groupingBy(StdAssetMst::getCurCd, mapping(StdAssetMst::getStdAsstCd, toList())));
				Map<String, List<StdAssetMst>> assetMap = StdAssetDao.getStdAssetMst().stream()
																.collect(groupingBy(StdAssetMst::getCurCd, toList()));
															
//													
				
				bottomUpMap.values().stream().flatMap(s-> Job34_EsgDetermieSce.createDetermineSce1(bssd,  s, assetMap.get(s.getCurCd()))).forEach(s->saveOrUpdate(s));
				kicsMap.values().stream().flatMap(s-> Job34_EsgDetermieSce.createDetermineSce1(bssd, s, assetMap.get(s.getCurCd()))).forEach(s->saveOrUpdate(s));
				
				completJob("SUCCESS", jobLog);
			}	
			catch (Exception e) {
				log.warn("Error : {}", e);
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job35A() {
		if (jobList.contains("35")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG35);
			try {
				String bizDv ="I";
				List<String> entryList = new ArrayList<String>();
				entryList.add("KOSPI200");
				
				int deleteNum = session.createQuery("delete BizStockSce a where a.baseYymm=:param and a.applBizDv=:bizDv and a.stdAsstCd=:stdAsstCd")
										.setParameter("param", bssd)
										.setParameter("bizDv", bizDv)
										.setParameter("stdAsstCd", "KOSPI200")
										.executeUpdate();
				
				log.info("Delete Data : {} for Job 35", deleteNum );
				
				
				Map<String, Map<Integer, Double>> driftMap = Job30_EsgStockScenario.createDrift3(bssd, bizDv, entryList, bottomUpMap, projectionYear);
				Map<String, Map<Integer, Double>> sigmaMap = Job30_EsgStockScenario.createSigma2(bssd, bizDv, entryList, projectionYear);
				
				Map<String, Double> asstHisMap = new HashMap<String, Double>();									// 산출기준일 종가 S0
				List<StdAssetMst> assetList = StdAssetDao.getStdAssetMst().stream()
														 .filter(s->s.getStdAsstCd().equals("KOSPI200"))
														 .collect(toList());
				
				double prevStockSce=0.0;
				double currStockSce=0.0;
				double yieldSce =0.0;
				int k=0;
				double dt = 1/12.0;
				
				for(StdAssetMst asset : assetList) {
					String mvId =asset.getStdAsstCd();
					if(asset.getStdAsstTypCd().equals("STOCK")){							
						asstHisMap.putIfAbsent(asset.getStdAsstCd(), StdAssetDao.getStdStockAssetHis(bssd,mvId).getStdAsstPrice());
					}else if(asset.getStdAsstTypCd().equals("BOND") && !asset.getStdAsstCd().equals("KTB")){
						log.info("aaaa : {}", asset.getStdAsstCd());
						asstHisMap.putIfAbsent(asset.getStdAsstCd(), StdAssetDao.getStdBondAssetHis(bssd, mvId).getIntRate());
					}
					else if(asset.getStdAsstTypCd().equals("SHORT_RATE")) {
						asstHisMap.putIfAbsent(asset.getStdAsstCd(), BottomupDcntDao.getShortRateHis(bssd, "RF_KRW_BU").getIntRate());
					}
				}
				String assetCd = "KOSPI200";
				List<BizStockSce> rstList = new ArrayList<BizStockSce>();
				
				for(EsgRandom aa : createRandom()) {
//					log.info("aaaaa :  {},{}", aa.getStdAsstCd(), aa.getSceNo());
					int matNum = aa.getMatNum();
					prevStockSce = asstHisMap.get(assetCd);

					double drift = driftMap.get(assetCd).get(aa.getMatNum());
					double sigma = sigmaMap.get(assetCd).get(aa.getMatNum());
				
					currStockSce = prevStockSce * Math.exp( ( drift -  0.5* sigma * sigma ) * dt   + sigma * Math.sqrt(dt) * aa.getRndNum());
					prevStockSce = currStockSce;
					
					yieldSce =  ( drift ) * dt   + sigma * Math.sqrt(dt) * aa.getRndNum();
					
					rstList.add(BizStockSce.builder()
							.baseYymm(bssd)
							.applBizDv(bizDv)
							.sceNo(String.valueOf(aa.getSceNo()))
							.matCd( "M" + String.format("%04d", matNum))
							.stdAsstCd(assetCd)
//							.asstYield(yieldSce)
//							.asstYield(Math.exp(yieldSce / dt)-1 )
//							.asstYield(Math.exp(yieldSce / dt)-1 )
							.asstYield( yieldSce / (12.0 * dt) )			//TODO :Check !!!!!
							.lastModifiedBy("ESG")
							.lastUpdateDate(LocalDateTime.now())
							.build()
							);
				}
				
				log.info("zzz :  {}",rstList.size());
				rstList.forEach(s-> saveOrUpdate(s));
					
				completJob("SUCCESS", jobLog);
			}	
			catch (Exception e) {
				log.warn("Error : {}", e);
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	private static void job35() {
		if (jobList.contains("35")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG35);
			try {
				String bizDv ="I";
				List<String> entryList = new ArrayList<String>();
				entryList.add("KOSPI200");
				
				int deleteNum = session.createQuery("delete BizStockSce a where a.baseYymm=:param and a.applBizDv=:bizDv and a.stdAsstCd=:stdAsstCd")
										.setParameter("param", bssd)
										.setParameter("bizDv", bizDv)
										.setParameter("stdAsstCd", "KOSPI200")
										.executeUpdate();
				
				log.info("Delete Data : {} for Job 35", deleteNum );
				
				Map<String, Map<Integer, Double>> driftMap = Job30_EsgStockScenario.createDrift3(bssd, bizDv, entryList, bottomUpMap, projectionYear);
				Map<String, Map<Integer, Double>> sigmaMap = Job30_EsgStockScenario.createSigma2(bssd, bizDv, entryList, projectionYear);

				Map<String, Double> asstHisMap = new HashMap<String, Double>();									// 산출기준일 종가 S0
				List<StdAssetMst> assetList = StdAssetDao.getStdAssetMst().stream()
														 .filter(s->s.getStdAsstCd().equals("KOSPI200"))
														 .collect(toList());
				
				log.info("zzzz :  {},{}", driftMap.get("KOSPI200").size(), sigmaMap.get("KOSPI200").size());
//				driftMap.entrySet().forEach(s-> log.info("aaa : {},{}", s.getKey(), s.getValue()));
				
				double prevStockSce=0.0;
				double currStockSce=0.0;
				double yieldSce =0.0;
				int k=0;
				double dt = 1/12.0;
				
				for(StdAssetMst asset : assetList) {
					String mvId =asset.getStdAsstCd();
					if(asset.getStdAsstTypCd().equals("STOCK")){							
						asstHisMap.putIfAbsent(asset.getStdAsstCd(), StdAssetDao.getStdStockAssetHis(bssd,mvId).getStdAsstPrice());
					}else if(asset.getStdAsstTypCd().equals("BOND") && !asset.getStdAsstCd().equals("KTB")){
						log.info("aaaa : {}", asset.getStdAsstCd());
						asstHisMap.putIfAbsent(asset.getStdAsstCd(), StdAssetDao.getStdBondAssetHis(bssd, mvId).getIntRate());
					}
					else if(asset.getStdAsstTypCd().equals("SHORT_RATE")) {
//						asstHisMap.putIfAbsent(asset.getStdAsstCd(), IrCurveHisDao.getShortRateHis(bssd, "1010000").getIntRate());
						asstHisMap.putIfAbsent(asset.getStdAsstCd(), BottomupDcntDao.getShortRateHis(bssd, "RF_KRW_KICS").getIntRate());
					}
				}
				
				
				String assetCd = "KOSPI200";
				
				if(!driftMap.containsKey(assetCd)) {
					log.error("Error : No Drift Parameter for {}", assetCd);
					System.exit(1);
				}
				if(!sigmaMap.containsKey(assetCd)) {
					log.error("Error : No Sigma Parameter for {}", assetCd);
					System.exit(1);
				}
				
				LogNormal4j logNormal = new LogNormal4j(bssd, driftMap.get(assetCd), sigmaMap.get(assetCd), projectionYear*12, batchNum);
				
				List<CompletableFuture<List<BizStockSce>>> 	sceJobFutures =	
						IntStream.rangeClosed(1,batchNum)
						.mapToObj(batch-> CompletableFuture.supplyAsync(() ->  logNormal.getBizStockScenario(bssd, assetCd , bizDv, batch), exe))
						.collect(Collectors.toList());
				
				List<BizStockSce> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
				
				log.info("rst Size :  {},{}", rst.size());
				
				int sceCnt = 1;
				for (BizStockSce bb :rst) {
//					log.info("zzzz : {}", bb.toString());
					session.save(bb);
					if (sceCnt % 50 == 0) {
						session.flush();
						session.clear();
					}
					if (sceCnt % flushSize == 0) {
//						log.info("Biz Bottom Scenario for {}  is processed {}/{} in Job 24A {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
						log.info("BizStockSce Scenario for {}  is processed {}/{} in Job 38 {}", sceCnt, batchNum * 100 * projectionYear * 12);
					}
					sceCnt = sceCnt + 1;
				}
					
				completJob("SUCCESS", jobLog);
			}	
			catch (Exception e) {
				log.warn("Error : {}", e);
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job36A() {
		if (jobList.contains("36")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG36);
			try {
				String bizDv ="K";
				List<String> entryList = new ArrayList<String>();
				entryList.add("KOSPI200");
				
				int deleteNum = session.createQuery("delete BizStockSce a where a.baseYymm=:param and a.applBizDv=:bizDv and a.stdAsstCd=:stdAsstCd")
										.setParameter("param", bssd)
										.setParameter("bizDv", bizDv)
										.setParameter("stdAsstCd", "KOSPI200")
										.executeUpdate();
				
				log.info("Delete Data : {} for Job 35", deleteNum );
				
				
				Map<String, Map<Integer, Double>> driftMap = Job30_EsgStockScenario.createDrift3(bssd, bizDv, entryList, kicsMap, projectionYear);
				Map<String, Map<Integer, Double>> sigmaMap = Job30_EsgStockScenario.createSigma2(bssd, bizDv, entryList, projectionYear);

				Map<String, Double> asstHisMap = new HashMap<String, Double>();									// 산출기준일 종가 S0
				List<StdAssetMst> assetList = StdAssetDao.getStdAssetMst().stream()
														 .filter(s->s.getStdAsstCd().equals("KOSPI200"))
														 .collect(toList());
				
				double prevStockSce=0.0;
				double currStockSce=0.0;
				double yieldSce =0.0;
				int k=0;
				double dt = 1/12.0;
				
				for(StdAssetMst asset : assetList) {
					String mvId =asset.getStdAsstCd();
					if(asset.getStdAsstTypCd().equals("STOCK")){							
						asstHisMap.putIfAbsent(asset.getStdAsstCd(), StdAssetDao.getStdStockAssetHis(bssd,mvId).getStdAsstPrice());
					}else if(asset.getStdAsstTypCd().equals("BOND") && !asset.getStdAsstCd().equals("KTB")){
						log.info("aaaa : {}", asset.getStdAsstCd());
						asstHisMap.putIfAbsent(asset.getStdAsstCd(), StdAssetDao.getStdBondAssetHis(bssd, mvId).getIntRate());
					}
					else if(asset.getStdAsstTypCd().equals("SHORT_RATE")) {
//						asstHisMap.putIfAbsent(asset.getStdAsstCd(), IrCurveHisDao.getShortRateHis(bssd, "1010000").getIntRate());
						asstHisMap.putIfAbsent(asset.getStdAsstCd(), BottomupDcntDao.getShortRateHis(bssd, "RF_KRW_KICS").getIntRate());
					}
				}
				String assetCd = "KOSPI200";
				List<BizStockSce> rstList = new ArrayList<BizStockSce>();
				
				for(EsgRandom aa : createRandom()) {
//					log.info("aaaaa :  {},{}", aa.getStdAsstCd(), aa.getSceNo());
					int matNum = aa.getMatNum();
					prevStockSce = asstHisMap.get(assetCd);

					double drift = driftMap.get(assetCd).get(aa.getMatNum());
					double sigma = sigmaMap.get(assetCd).get(aa.getMatNum());
				
					currStockSce = prevStockSce * Math.exp( ( drift -  0.5* sigma * sigma ) * dt   + sigma * Math.sqrt(dt) * aa.getRndNum());
					prevStockSce = currStockSce;
					
					yieldSce =  ( drift ) * dt   + sigma * Math.sqrt(dt) * aa.getRndNum();
					
					rstList.add(BizStockSce.builder()
							.baseYymm(bssd)
							.applBizDv(bizDv)
							.sceNo(String.valueOf(aa.getSceNo()))
							.matCd( "M" + String.format("%04d", matNum))
							.stdAsstCd(assetCd)
//							.asstYield(yieldSce)
//							.asstYield(Math.exp(yieldSce / dt)-1 )
//							.asstYield(Math.exp(yieldSce / dt)-1 )
							.asstYield( yieldSce / (12.0 * dt) )			//TODO :Check !!!!!
							.lastModifiedBy("ESG")
							.lastUpdateDate(LocalDateTime.now())
							.build()
							);
				}
				
				log.info("zzz :  {}",rstList.size());
				rstList.forEach(s-> saveOrUpdate(s));
					
				completJob("SUCCESS", jobLog);
			}	
			catch (Exception e) {
				log.warn("Error : {}", e);
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	
	private static void job36() {
		if (jobList.contains("36")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG36);
			try {
				String bizDv ="K";
				List<String> entryList = new ArrayList<String>();
				entryList.add("KOSPI200");
				
				int deleteNum = session.createQuery("delete BizStockSce a where a.baseYymm=:param and a.applBizDv=:bizDv and a.stdAsstCd=:stdAsstCd")
										.setParameter("param", bssd)
										.setParameter("bizDv", bizDv)
										.setParameter("stdAsstCd", "KOSPI200")
										.executeUpdate();
				
				log.info("Delete Data : {} for Job 35", deleteNum );
				
//				Map<String, Map<Integer, Double>> driftMap = Job30_EsgStockScenario.createDrift3(bssd, bizDv, entryList, bottomUpMap, projectionYear);
				Map<String, Map<Integer, Double>> driftMap = Job30_EsgStockScenario.createDrift3(bssd, bizDv, entryList, kicsMap, projectionYear);
				Map<String, Map<Integer, Double>> sigmaMap = Job30_EsgStockScenario.createSigma2(bssd, bizDv, entryList, projectionYear);

				Map<String, Double> asstHisMap = new HashMap<String, Double>();									// 산출기준일 종가 S0
				List<StdAssetMst> assetList = StdAssetDao.getStdAssetMst().stream()
														 .filter(s->s.getStdAsstCd().equals("KOSPI200"))
														 .collect(toList());
				
				log.info("zzzz :  {},{}", driftMap.get("KOSPI200").size(), sigmaMap.get("KOSPI200").size());
//				driftMap.entrySet().forEach(s-> log.info("aaa : {},{}", s.getKey(), s.getValue()));
				
				double prevStockSce=0.0;
				double currStockSce=0.0;
				double yieldSce =0.0;
				int k=0;
				double dt = 1/12.0;
				
				for(StdAssetMst asset : assetList) {
					String mvId =asset.getStdAsstCd();
					if(asset.getStdAsstTypCd().equals("STOCK")){							
						asstHisMap.putIfAbsent(asset.getStdAsstCd(), StdAssetDao.getStdStockAssetHis(bssd,mvId).getStdAsstPrice());
					}else if(asset.getStdAsstTypCd().equals("BOND") && !asset.getStdAsstCd().equals("KTB")){
						log.info("aaaa : {}", asset.getStdAsstCd());
						asstHisMap.putIfAbsent(asset.getStdAsstCd(), StdAssetDao.getStdBondAssetHis(bssd, mvId).getIntRate());
					}
					else if(asset.getStdAsstTypCd().equals("SHORT_RATE")) {
//						asstHisMap.putIfAbsent(asset.getStdAsstCd(), IrCurveHisDao.getShortRateHis(bssd, "1010000").getIntRate());
						asstHisMap.putIfAbsent(asset.getStdAsstCd(), BottomupDcntDao.getShortRateHis(bssd, "RF_KRW_KICS").getIntRate());
					}
				}
				
				
				String assetCd = "KOSPI200";
				
				if(!driftMap.containsKey(assetCd)) {
					log.error("Error : No Drift Parameter for {}", assetCd);
					System.exit(1);
				}
				if(!sigmaMap.containsKey(assetCd)) {
					log.error("Error : No Sigma Parameter for {}", assetCd);
					System.exit(1);
				}
				
				LogNormal4j logNormal = new LogNormal4j(bssd, driftMap.get(assetCd), sigmaMap.get(assetCd), projectionYear*12, batchNum);
				
				List<CompletableFuture<List<BizStockSce>>> 	sceJobFutures =	
						IntStream.rangeClosed(1,batchNum)
						.mapToObj(batch-> CompletableFuture.supplyAsync(() ->  logNormal.getBizStockScenario(bssd, assetCd , bizDv, batch), exe))
						.collect(Collectors.toList());
				
				List<BizStockSce> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
				
				log.info("rst Size :  {},{}", rst.size());
				
				int sceCnt = 1;
				for (BizStockSce bb :rst) {
//					log.info("zzzz : {}", bb.toString());
					session.save(bb);
					if (sceCnt % 50 == 0) {
						session.flush();
						session.clear();
					}
					if (sceCnt % flushSize == 0) {
//						log.info("Biz Bottom Scenario for {}  is processed {}/{} in Job 24A {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
						log.info("BizStockSce Scenario for {}  is processed {}/{} in Job 38 {}", sceCnt, batchNum * 100 * projectionYear * 12);
					}
					sceCnt = sceCnt + 1;
				}
					
				completJob("SUCCESS", jobLog);
			}	
			catch (Exception e) {
				log.warn("Error : {}", e);
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job38() {
		if (jobList.contains("38")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG38);
			try {
				String bizDv ="I";			
				IrCurve curveMst = bottomUpMap.get("KRW");
				
				int deleteNum = session.createQuery("delete BizStockSce a where a.baseYymm=:param and a.applBizDv=:bizDv and a.stdAsstCd=:stdAsstCd")
											.setParameter("param", bssd)
											.setParameter("bizDv", bizDv)
											.setParameter("stdAsstCd", "KTB")
											.executeUpdate();
				
				log.info("Delete Data  for Job 38 : {}", deleteNum );
				
				double ufr  = EsgConstant.getSmParam().get(curveMst.getCurCd()).getUfr();
				double ufrt = EsgConstant.getSmParam().get(curveMst.getCurCd()).getUfrT();
				
				
				List<BizDiscountRate> bottomHisList = BizDiscountRateDao.getTermStructure(bssd, curveMst.getApplBizDv(), curveMst.getIrCurveId());
				
				List<EsgMst> esgMstList = EsgMstDao.getEsgMst(EBoolean.Y);
				
				if(bottomHisList.size()==0) {
					log.error("No Bottom Up Curve His Data exist for {}", bssd);
					System.exit(0);
				}
				if(esgMstList.size()==0) {
					log.error("No ESG MST with UseYn 'Y' exist for Hull and White Model");
					System.exit(0);
				}
				
//				String irCurveId = irCurveHisList.get(0).getIrCurveId();
				EsgMst tempEsgMst       = esgMstList.get(0);
				List<BizEsgParam> bizParamHis = EsgParamDao.getBizEsgParam(bssd, tempEsgMst.getIrModelId());			
				
				if(bizParamHis.size()==0) {
					log.error("No ESG Parameter exist for Hull and White Model");
					System.exit(0);
				}
				
//				batchNum =1;
				log.info("aaa : {},{}", bottomHisList.size(), targetDuration);
				
//				HullWhiteCont4j hw4f = new HullWhiteCont4j(bssd, bottomHisList, bizParamHis, ufr, ufrt, batchNum, targetDuration);
				HullWhiteContBond4j hw4f = new HullWhiteContBond4j(bssd, bottomHisList, bizParamHis, ufr, ufrt, batchNum, targetDuration);
//				hw4f.getBondYieldScenario(bssd, "KTB" , bizDv,1).forEach(s->log.info("aaa: {},{},{},{}", s.getSceNo(), s.getMatCd(), s.getStdAsstCd(), s.getAsstYield()));
				
				List<CompletableFuture<List<BizStockSce>>> 	sceJobFutures =	
						IntStream.rangeClosed(1,batchNum)
						.mapToObj(batch-> CompletableFuture.supplyAsync(() 
								->  hw4f.getBondYieldScenario(bssd, "KTB" , bizDv, batch), exe))
						.collect(Collectors.toList());
				
				List<BizStockSce> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
				
				int sceCnt = 1;
				for (BizStockSce bb :rst) {
//					log.info("zzzz : {}", bb.toString());
					session.save(bb);
					if (sceCnt % 50 == 0) {
						session.flush();
						session.clear();
					}
					if (sceCnt % flushSize == 0) {
//						log.info("Biz Bottom Scenario for {}  is processed {}/{} in Job 24A {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
						log.info("BizStockSce Scenario for {}  is processed {}/{} in Job 38 {}", sceCnt, batchNum * 100 * projectionYear * 12);
					}
					sceCnt = sceCnt + 1;
				}
				
//				hw4f.getBizDiscountScenario(bssd, curveMst.getIrCurveId(), bizDv).forEach(s->saveOrUpdate(s));
					
				completJob("SUCCESS", jobLog);
			}	
			catch (Exception e) {
				log.warn("Error : {}", e);
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	
	private static void job39() {
		if (jobList.contains("39")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG39);
			try {
				String bizDv ="K";			
				IrCurve curveMst = kicsMap.get("KRW");
				
				int deleteNum = session.createQuery("delete BizStockSce a where a.baseYymm=:param and a.applBizDv=:bizDv and a.stdAsstCd=:stdAsstCd")
											.setParameter("param", bssd)
											.setParameter("bizDv", bizDv)
											.setParameter("stdAsstCd", "KTB")
											.executeUpdate();
				
//				int deleteNum = session.createQuery("delete BizDiscountRateSce a where a.baseYymm=:param  and a.applyBizDv=:bizDv")
//				.setParameter("param", bssd)
//				.setParameter("bizDv", bizDv)
//				.executeUpdate();
				
				log.info("Delete Data  for Job 39 : {}", deleteNum );
				
				double ufr  = EsgConstant.getSmParam().get(curveMst.getCurCd()).getUfr();
				double ufrt = EsgConstant.getSmParam().get(curveMst.getCurCd()).getUfrT();
				
				
				List<BizDiscountRate> bottomHisList = BizDiscountRateDao.getTermStructure(bssd, curveMst.getApplBizDv(), curveMst.getIrCurveId());
				
				List<EsgMst> esgMstList = EsgMstDao.getEsgMst(EBoolean.Y);
				
				if(bottomHisList.size()==0) {
					log.error("No Bottom Up Curve His Data exist for {}", bssd);
					System.exit(0);
				}
				if(esgMstList.size()==0) {
					log.error("No ESG MST with UseYn 'Y' exist for Hull and White Model");
					System.exit(0);
				}
				
//				String irCurveId = irCurveHisList.get(0).getIrCurveId();
				EsgMst tempEsgMst       = esgMstList.get(0);
				List<BizEsgParam> bizParamHis = EsgParamDao.getBizEsgParam(bssd, tempEsgMst.getIrModelId());			
				
				if(bizParamHis.size()==0) {
					log.error("No ESG Parameter exist for Hull and White Model");
					System.exit(0);
				}
				
//				batchNum =1;
				log.info("aaa : {},{}", bottomHisList.size(), targetDuration);
				
//				HullWhiteCont4j hw4f = new HullWhiteCont4j(bssd, bottomHisList, bizParamHis, ufr, ufrt, batchNum, targetDuration);
				HullWhiteContBond4j hw4f = new HullWhiteContBond4j(bssd, bottomHisList, bizParamHis, ufr, ufrt, batchNum, targetDuration);
//				hw4f.getBondYieldScenario(bssd, "KTB" , bizDv,1).forEach(s->log.info("aaa: {},{},{},{}", s.getSceNo(), s.getMatCd(), s.getStdAsstCd(), s.getAsstYield()));
				
				List<CompletableFuture<List<BizStockSce>>> 	sceJobFutures =	
						IntStream.rangeClosed(1,batchNum)
						.mapToObj(batch-> CompletableFuture.supplyAsync(() 
								->  hw4f.getBondYieldScenario(bssd, "KTB" , bizDv, batch), exe))
						.collect(Collectors.toList());
				
				List<BizStockSce> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
				
				int sceCnt = 1;
				for (BizStockSce bb :rst) {
//					log.info("zzzz : {}", bb.toString());
					session.save(bb);
					if (sceCnt % 50 == 0) {
						session.flush();
						session.clear();
					}
					if (sceCnt % flushSize == 0) {
//						log.info("Biz Bottom Scenario for {}  is processed {}/{} in Job 24A {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
						log.info("BizStockSce Scenario for {}  is processed {}/{} in Job 39 {}", sceCnt, batchNum * 100 * projectionYear * 12);
					}
					sceCnt = sceCnt + 1;
				}
				
//				hw4f.getBizDiscountScenario(bssd, curveMst.getIrCurveId(), bizDv).forEach(s->saveOrUpdate(s));
					
				completJob("SUCCESS", jobLog);
			}	
			catch (Exception e) {
				log.warn("Error : {}", e);
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job41() {
		if (jobList.contains("41")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG41);
			try {
				String bizDv ="I";
				session.createQuery("delete IrSce a where a.baseDate=:param and a.irModelId=:bizDv")
								.setParameter("param", bssd).setParameter("bizDv", bizDv).executeUpdate();
				session.createQuery("delete BizIrCurveSce a where a.baseYymm=:param and a.applBizDv=:bizDv")
								.setParameter("param", bssd).setParameter("bizDv", bizDv).executeUpdate();
				
				for(IrCurve aa : bottomUpMap.values()) {	
					log.info("Ir Scenario for {} : {}", aa.getCurCd(), aa.getIrCurveId(), aa.getIrCurveNm());
					
					double ufr  = EsgConstant.getSmParam().get(aa.getCurCd()).getUfr();
					double ufrt = EsgConstant.getSmParam().get(aa.getCurCd()).getUfrT();
					
//					List<IrCurveHis> irCurveHisList = IrCurveHisDao.getIrCurveHis(bssd, aa.getIrCurveId());
					List<BottomupDcnt> bottomHisList = BottomupDcntDao.getTermStructure(bssd, aa.getIrCurveId());
					
					List<EsgMst> esgMstList = EsgMstDao.getEsgMst(EBoolean.Y);
					
					if(bottomHisList.size()==0) {
						log.error("No Ir Curve His Data exist for {}", bssd);
						System.exit(0);
					}
					if(esgMstList.size()==0) {
						log.error("No ESG MST with UseYn 'Y' exist for Hull and White Model");
						System.exit(0);
					}
					
//				String irCurveId = irCurveHisList.get(0).getIrCurveId();
					EsgMst tempEsgMst       = esgMstList.get(0);
					List<BizEsgParam> bizParamHis = EsgParamDao.getBizEsgParam(bssd, tempEsgMst.getIrModelId());			
					
					if(bizParamHis.size()==0) {
						log.error("No ESG Parameter exist for Hull and White Model");
						System.exit(0);
					}
					
//					double shortRate = IrCurveHisDao.getBizIrCurveHis(bssd, "A", aa.getIrCurveId()).stream()
//													.filter(s ->s.getMatCd().equals("M0001"))
//													.map(s -> s.getIntRate()).findFirst().orElse(0.01);
					
					double shortRate = BottomupDcntDao.getTermStructure(bssd, aa.getIrCurveId()).stream()
													.filter(s ->s.getMatCd().equals("M0001"))
													.map(s -> s.getIntRate()).findFirst().orElse(0.01);
					
					List<CompletableFuture<List<BizIrCurveSce>>> 	sceJobFutures =	
							IntStream.rangeClosed(1,batchNum)
							.mapToObj(sceNo-> CompletableFuture.supplyAsync(() 
									->  Job40_EsgScenarioAsync.createEsgBizScenarioAsync1(bssd, aa, bottomHisList, bizParamHis, tempEsgMst, ufr, ufrt, shortRate, sceNo), exe))
							.collect(Collectors.toList());
					
					List<BizIrCurveSce> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
					
					int sceCnt = 1;
					for (BizIrCurveSce bb :rst) {
						session.save(bb);
						if (sceCnt % 50 == 0) {
							session.flush();
							session.clear();
						}
						if (sceCnt % flushSize == 0) {
							log.info("Biz IR Scenario for {}  is processed {}/{} in Job 15 {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
						}
						sceCnt = sceCnt + 1;
					}
					
					List<String> tenorList = EsgConstant.getTenorList();
					List<IrSce> irSceRst = rst.stream().filter(s->tenorList.contains(s.getMatCd())).map(s->s.convertTo()).collect(toList());
					
					int irSceCnt = 1;
					for (IrSce bb :irSceRst) {
						session.save(bb);
						if (sceCnt % 50 == 0) {
							session.flush();
							session.clear();
						}
						if (irSceCnt % flushSize == 0) {
							log.info("IR Scenario for {}  is processed {}/{} in Job 15 {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
						}
						irSceCnt = irSceCnt + 1;
					}
				}
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.error("error : {}", e);
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job42() {
		if (jobList.contains("42")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG42);
			try {
				String bizDv ="K";
				session.createQuery("delete IrSce a where a.baseDate=:param and a.irModelId=:bizDv")
								.setParameter("param", bssd).setParameter("bizDv", bizDv).executeUpdate();
				
				session.createQuery("delete BizIrCurveSce a where a.baseYymm=:param and a.applBizDv=:bizDv")
								.setParameter("param", bssd).setParameter("bizDv", bizDv).executeUpdate();
				
				for(IrCurve aa : kicsMap.values()) {	
					log.info("Ir Scenario for {} : {}", aa.getCurCd(), aa.getIrCurveId(), aa.getIrCurveNm());
					
					double ufr  = EsgConstant.getSmParam().get(aa.getCurCd()).getUfr();
					double ufrt = EsgConstant.getSmParam().get(aa.getCurCd()).getUfrT();
					
//					List<IrCurveHis> irCurveHisList = IrCurveHisDao.getIrCurveHis(bssd, aa.getIrCurveId());
					List<BottomupDcnt> bottomHisList = BottomupDcntDao.getTermStructure(bssd, aa.getIrCurveId());
					
					List<EsgMst> esgMstList = EsgMstDao.getEsgMst(EBoolean.Y);
					
					if(bottomHisList.size()==0) {
						log.error("No Ir Curve His Data exist for {}", bssd);
						System.exit(0);
					}
					if(esgMstList.size()==0) {
						log.error("No ESG MST with UseYn 'Y' exist for Hull and White Model");
						System.exit(0);
					}
					
//				String irCurveId = irCurveHisList.get(0).getIrCurveId();
					EsgMst tempEsgMst       = esgMstList.get(0);
					List<BizEsgParam> bizParamHis = EsgParamDao.getBizEsgParam(bssd, tempEsgMst.getIrModelId());			
					
					if(bizParamHis.size()==0) {
						log.error("No ESG Parameter exist for Hull and White Model");
						System.exit(0);
					}
					
//					double shortRate = IrCurveHisDao.getBizIrCurveHis(bssd, "A", aa.getIrCurveId()).stream()
//													.filter(s ->s.getMatCd().equals("M0001"))
//													.map(s -> s.getIntRate()).findFirst().orElse(0.01);
					
					double shortRate = BottomupDcntDao.getTermStructure(bssd, aa.getIrCurveId()).stream()
													.filter(s ->s.getMatCd().equals("M0001"))
													.map(s -> s.getIntRate()).findFirst().orElse(0.01);
					
					List<CompletableFuture<List<BizIrCurveSce>>> 	sceJobFutures =	
							IntStream.rangeClosed(1,batchNum)
							.mapToObj(sceNo-> CompletableFuture.supplyAsync(() 
									->  Job40_EsgScenarioAsync.createEsgBizScenarioAsync1(bssd, aa, bottomHisList, bizParamHis, tempEsgMst, ufr, ufrt, shortRate, sceNo), exe))
							.collect(Collectors.toList());
					
					List<BizIrCurveSce> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
					
					int sceCnt = 1;
					for (BizIrCurveSce bb :rst) {
						session.save(bb);
						if (sceCnt % 50 == 0) {
							session.flush();
							session.clear();
						}
						if (sceCnt % flushSize == 0) {
							log.info("Biz IR Scenario for {}  is processed {}/{} in Job 15 {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
						}
						sceCnt = sceCnt + 1;
					}
					
					List<String> tenorList = EsgConstant.getTenorList();
					List<IrSce> irSceRst = rst.stream().filter(s->tenorList.contains(s.getMatCd())).map(s->s.convertTo()).collect(toList());
					
					int irSceCnt = 1;
					for (IrSce bb :irSceRst) {
						session.save(bb);
						if (sceCnt % 50 == 0) {
							session.flush();
							session.clear();
						}
						if (irSceCnt % flushSize == 0) {
							log.info("IR Scenario for {}  is processed {}/{} in Job 15 {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
						}
						irSceCnt = irSceCnt + 1;
					}
				}
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.error("error : {}", e);
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	private static void job43() {
		if (jobList.contains("43")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG43);
			try {
				session.createQuery("delete IrSce a where a.baseDate=:param")
				.setParameter("param", bssd)
				.executeUpdate();
				
				session.createQuery("delete BizIrCurveSce a where a.baseYymm=:param")
				.setParameter("param", bssd)
				.executeUpdate();
				
				for(IrCurve aa : rfCurveMap.values()) {	
					log.info("Ir Scenario for {} : {}", aa.getCurCd(), aa.getIrCurveId(), aa.getIrCurveNm());
					
					double ufr  = EsgConstant.getSmParam().get(aa.getCurCd()).getUfr();
					double ufrt = EsgConstant.getSmParam().get(aa.getCurCd()).getUfrT();
					
					List<IrCurveHis> irCurveHisList = IrCurveHisDao.getIrCurveHis(bssd, aa.getIrCurveId());
					List<EsgMst> esgMstList = EsgMstDao.getEsgMst(EBoolean.Y);
					
					if(irCurveHisList.size()==0) {
						log.error("No Ir Curve His Data exist for {}", bssd);
						System.exit(0);
					}
					if(esgMstList.size()==0) {
						log.error("No ESG MST with UseYn 'Y' exist for Hull and White Model");
						System.exit(0);
					}
					
//				String irCurveId = irCurveHisList.get(0).getIrCurveId();
					EsgMst tempEsgMst       = esgMstList.get(0);
					List<BizEsgParam> bizParamHis = EsgParamDao.getBizEsgParam(bssd, tempEsgMst.getIrModelId());			
					
					if(bizParamHis.size()==0) {
						log.error("No ESG Parameter exist for Hull and White Model");
						System.exit(0);
					}
					
					double shortRate = IrCurveHisDao.getBizIrCurveHis(bssd, "A", aa.getIrCurveId()).stream()
							.filter(s ->s.getMatCd().equals("M0001"))
							.map(s -> s.getIntRate()).findFirst().orElse(0.01);
					
					List<CompletableFuture<List<BizIrCurveSce>>> 	sceJobFutures =	
							IntStream.rangeClosed(1,batchNum)
							.mapToObj(sceNo-> CompletableFuture.supplyAsync(() 
									->  Job40_EsgScenarioAsync.createEsgBizScenarioAsync(bssd, irCurveHisList, bizParamHis, tempEsgMst, ufr, ufrt, shortRate, sceNo), exe))
							.collect(Collectors.toList());
					
					List<BizIrCurveSce> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
					
					int sceCnt = 1;
					for (BizIrCurveSce bb :rst) {
						session.save(bb);
						if (sceCnt % 50 == 0) {
							session.flush();
							session.clear();
						}
						if (sceCnt % flushSize == 0) {
							log.info("Biz IR Scenario for {}  is processed {}/{} in Job 15 {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
						}
						sceCnt = sceCnt + 1;
					}
					
					List<String> tenorList = EsgConstant.getTenorList();
					List<IrSce> irSceRst = rst.stream().filter(s->tenorList.contains(s.getMatCd())).map(s->s.convertTo()).collect(toList());
					
					int irSceCnt = 1;
					for (IrSce bb :irSceRst) {
						session.save(bb);
						if (sceCnt % 50 == 0) {
							session.flush();
							session.clear();
						}
						if (irSceCnt % flushSize == 0) {
							log.info("IR Scenario for {}  is processed {}/{} in Job 15 {}", aa.getCurCd(),sceCnt, batchNum * 100 * projectionYear * 12);
						}
						irSceCnt = irSceCnt + 1;
					}
				}
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job48() {
		if (jobList.contains("48")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG48);
			try {
				String rfId = EsgConstant.getStrConstant().get("ESG_RF_KRW_ID");
				Job18_DnsScenario.createDnsShockScenario(bssd, "KRW", rfId, 1, dnsErrorTolerance, kicsVolAdjust).forEach(s -> session.saveOrUpdate(s));
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}

	private static void job51() {
		if (jobList.contains("51")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG51);
			try {
				session.createQuery("delete DiscRateStats a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				
				Job51_DiscRateStatIfrs.createDiscRateStat(bssd).forEach(s->session.save(s));
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}	
	}
	
	private static void job52() {
		if (jobList.contains("52")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG52);
			try {
				session.createQuery("delete BizDiscRateStat a where a.baseYymm=:param and a.applyBizDv=:param2")
					.setParameter("param", bssd).setParameter("param2", "I").executeUpdate();
				
				boolean isFitting = EsgConstant.getStrConstant().getOrDefault("IFRS_DISC_FITTING_YN", "Y").equals("Y");
				Job52_BizDiscRateStat.createIfrsDiscRateStat(bssd, isFitting).forEach(s->session.save(s));
				
//				if(fittingYn.equals("Y")) {
//					Job32_BizDiscRateStat.createIfrsDiscRateStat(bssd, true).forEach(s->session.save(s));
//				}else {
//					Job32_BizDiscRateStat.createIfrsDiscRateStat(bssd, false).forEach(s->session.save(s));
//				}
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.info("Error :{}", e);
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job53(){
		if (jobList.contains("53")) {
			String bizDv ="I";
			log.info("Job 53 (Average Forward Rate for DiscRate and DiscRate Scenario under BizDv {} Start !!! ", bizDv);
			
			String pastCurveId = EsgConstant.getStrConstant().getOrDefault("IFRS_DISC_PAST_DRIVER", "1010000");
			String irCurveId = EsgConstant.getStrConstant().getOrDefault("IRFS_DISC_IR_DRIVER", "1010000");
			String matCd     = EsgConstant.getStrConstant().getOrDefault("IFRS_DICS_MAT" , "M0084");
			String avgNumCd  = EsgConstant.getStrConstant().getOrDefault("IFRS_DICS_AVG" , "24");
			int avgNum 		 = -1*Integer.parseInt(avgNumCd);
	
			genAvgFwdSce(bizDv, pastCurveId, irCurveId, matCd, avgNum, EJob.ESG53);
			
			log.info("Job 53 (Average Forward Rate for DiscRate and DiscRate Scenario under BizDv {} is Completed !!! ", "I");
		}
	}

	private static void job54() {
		if (jobList.contains("54")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG54);
			try {
				session.createQuery("delete BizDiscRate a where a.baseYymm=:param and a.applyBizDv=:param2")
						.setParameter("param", bssd)
						.setParameter("param2", "I")
						.executeUpdate();
				
				String irCurveId = EsgConstant.getStrConstant().getOrDefault("IRFS_DISC_IR_DRIVER", "1010000");
				List<BizDiscRate> bizDiscRateList = Job54_BizDiscRate.createBizDiscRate(bssd, "I", irCurveId);
				
				cnt = 1;
				totalSize = bizDiscRateList.size();
				
				for (BizDiscRate aa : bizDiscRateList) {
					session.save(aa);
					if (cnt % 50 == 0) {
						session.flush();
						session.clear();
					}
					if (cnt % flushSize == 0) {
						log.info("Biz Disc Rate IFRS are Flushed :  {} / {}", cnt, totalSize);
					}
					cnt = cnt + 1;
				}
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}

	private static void job55() {
		if (jobList.contains("55")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG55);
			try {
				session.createQuery("delete BizDiscRateSce a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				List<BizDiscRateSce> bizDiscSce =  new ArrayList<BizDiscRateSce>();
				
//				TODO
				String irCurveId = EsgConstant.getStrConstant().getOrDefault("IRFS_DISC_IR_DRIVER", "1010000");
				bizDiscSce.addAll(Job55_BizDiscRateScenario.createBizDiscRateSce(bssd, "I", irCurveId, "1000"));
				for (BizDiscRateSce aa : bizDiscSce) {
					session.save(aa);
					if (cnt % 50 == 0) {
						session.flush();
						session.clear();
					}
				}
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				completJob("ERROR", jobLog);
				log.error("Error : {}", e);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job56() {
		if (jobList.contains("56")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG56);
			try {
				String bizDv ="I";
				session.createQuery("delete BizDiscRateSce a where a.baseYymm=:param and a.applBizDv =:bizDv")
									.setParameter("param", bssd)
									.setParameter("bizDv", bizDv)		
									.executeUpdate();
//				TODO
				String irCurveId = EsgConstant.getStrConstant().getOrDefault("IRFS_DISC_IR_DRIVER", "1010000");
				
				List<String> discSetting = DiscRateMstDao.getDiscRateMstList().stream()
														.filter(s ->s.isCalculable())
														.map(s ->s.getIntRateCd())
														.collect(Collectors.toList());
				List<BizDiscRateStat> bizStatList = DiscRateStatsDao.getBizDiscRateStat(bssd).stream()
															.filter(s-> s.getApplyBizDv().equals(bizDv))
															.filter(s-> discSetting.contains(s.getIntRateCd()))
															.collect(toList());
				
				BizDiscRateFwdSceDao.getForwardRatesSce(bssd, bizDv, irCurveId)
				 					 .map(s->s.createSce(bizStatList))
				 					 .flatMap(s->s.stream())
				 					 .forEach(s->saveOrUpdate(s));
				
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.error("Error : {}", e);
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}

	private static void job61() {
		if (jobList.contains("61")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG61);
			try {
				session.createQuery("delete DiscRateStats a where a.baseYymm=:param and a.discRateCalcTyp=:param2")
						.setParameter("param", bssd).setParameter("param2", "K").executeUpdate();
				
				Job61_DiscRateStatKics.createDiscRateStat(bssd).forEach(s->session.save(s));
//				Job36_DiscRateStatKics.createDiscRateStat(bssd).forEach(s->log.info("aaa : {}", s.toString()));
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				//Job36_DiscRateStatKics.createDiscRateStat(bssd).forEach(s->log.info("aaa : {}", s.toString()));
				completJob("ERROR", jobLog);
				log.error("Error : {}", e);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job62() {
		if (jobList.contains("62")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG62);
			try {
				session.createQuery("delete BizDiscRateStat a where a.baseYymm=:param and a.applyBizDv=:param2")
								.setParameter("param", bssd).setParameter("param2", "K").executeUpdate();
				
				boolean isFitting = EsgConstant.getStrConstant().getOrDefault("KICS_DISC_FITTING_YN", "Y").equals("Y");
				
				Job52_BizDiscRateStat.createKicsDiscRateStat(bssd, isFitting).forEach(s->session.save(s));
	//			Job32_BizDiscRateStat.createKicsDiscRateStat(bssd, isFitting).forEach(s->log.info("aaa : {}", s.toString()));
	//			if(fittingYn.equals("Y")) {
	//				Job32_BizDiscRateStat.createKicsDiscRateStat(bssd, true).forEach(s->session.save(s));
	//			}
	//			else {
	//				Job32_BizDiscRateStat.createKicsDiscRateStat(bssd, false).forEach(s->session.save(s));
	//			}
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				log.error("Error : {}", e);
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job63(){
		if (jobList.contains("63")) {
//			session.beginTransaction();
//			JobLog jobLog = startJogLog(EJob.ESG63);
			
			String bizDv ="K";
			log.info("Job 63 (Average Forward Rate for DiscRate and DiscRate Scenario under BizDv {} Start !!! ", bizDv);
			
			String pastCurveId = EsgConstant.getStrConstant().getOrDefault("KICS_DISC_PAST_DRIVER", "1010000");
			String irCurveId = EsgConstant.getStrConstant().getOrDefault("KICS_DISC_IR_DRIVER", "RF_KRW_KICS");
			String matCd     = EsgConstant.getStrConstant().getOrDefault("KICS_DICS_MAT" , "M0001");
			String avgNumCd  = EsgConstant.getStrConstant().getOrDefault("KICS_DICS_AVG" , "1");
			int avgNum 		 = -1*Integer.parseInt(avgNumCd);
			
			genAvgFwdSce(bizDv, pastCurveId, irCurveId, matCd, avgNum, EJob.ESG63);
			
			log.info("Job 63 (Average Forward Rate for DiscRate and DiscRate Scenario under BizDv {} is Completed !!! ", "K");
		}
	}

	private static void job64() {
		if (jobList.contains("64")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG64);
			try {
				session.createQuery("delete BizDiscRate a where a.baseYymm=:param and a.applyBizDv=:param2")
				.setParameter("param", bssd)
				.setParameter("param2", "K")
				.executeUpdate();
//				TODO
				List<BizDiscRate> bizDiscRateList = Job54_BizDiscRate.createBizDiscRate(bssd, "K", "RF_KRW_BU");
				
				cnt = 1;
				totalSize = bizDiscRateList.size();
				
				for (BizDiscRate aa : bizDiscRateList) {
					session.save(aa);
					if (cnt % 50 == 0) {
						session.flush();
						session.clear();
					}
					if (cnt % flushSize == 0) {
						log.info("KICS Disc Rate  are Flushed :  {} / {}", cnt, totalSize);
					}
					cnt = cnt + 1;
				}
				
				completJob("SUCCESS", jobLog);

			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}

	private static void job65() {
		if (jobList.contains("65")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG65);
			try {
//				session.createQuery("delete DiscRateSce a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				session.createQuery("delete BizDiscRateSce a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				
				List<BizDiscRateSce> bizDiscSce =  new ArrayList<BizDiscRateSce>();
				
				bizDiscSce.addAll(Job55_BizDiscRateScenario.createBizDiscRateSce(bssd, "K", "RF_KRW_BU", "10"));
				for (BizDiscRateSce aa : bizDiscSce) {
					session.save(aa);
					if (cnt % 50 == 0) {
						session.flush();
						session.clear();
					}
				}
				completJob("SUCCESS", jobLog);

			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	
	private static void job66() {
		if (jobList.contains("66")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG66);
			try {
				String bizDv ="K";
				session.createQuery("delete BizDiscRateSce a where a.baseYymm=:param and a.applBizDv =:bizDv")
									.setParameter("param", bssd)
									.setParameter("bizDv", bizDv)		
									.executeUpdate();
//				TODO
				String irCurveId = EsgConstant.getStrConstant().getOrDefault("KICS_DISC_IR_DRIVER", "1010000");
				
				List<String> discSetting = DiscRateMstDao.getDiscRateMstList().stream()
														.filter(s ->s.isCalculable())
														.map(s ->s.getIntRateCd())
														.collect(Collectors.toList());
				
				List<BizDiscRateStat> bizStatList = DiscRateStatsDao.getBizDiscRateStat(bssd).stream()
															.filter(s-> s.getApplyBizDv().equals(bizDv))
															.filter(s-> discSetting.contains(s.getIntRateCd()))
															.collect(toList());
				
				 BizDiscRateFwdSceDao.getForwardRatesSce(bssd, bizDv, irCurveId)
				 					 .map(s->s.createSce(bizStatList))
				 					 .flatMap(s->s.stream())
				 					 .forEach(s->saveOrUpdate(s));
				
				 
				completJob("SUCCESS", jobLog);

			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job71() {
		if (jobList.contains("71")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG71);
			try {
				Job71_Inflation.getInflationRate(bssd).stream().forEach(s -> session.saveOrUpdate(s));
				completJob("SUCCESS", jobLog);

			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}

	private static void job72() {
		if (jobList.contains("72")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG72);
			try {
				session.createQuery("delete BizInflation a where a.baseYymm=:param and a.applyBizDv=:param2" )
				.setParameter("param", bssd).setParameter("param2", "I").executeUpdate();
				
				Job72_BizInflation.createIfrsBizInflationMA(bssd).stream().forEach(s -> session.saveOrUpdate(s));
				completJob("SUCCESS", jobLog);

			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job73() {
		if (jobList.contains("73")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG73);
			try {
				session.createQuery("delete BizInflation a where a.baseYymm=:param and a.applyBizDv=:param2" )
				.setParameter("param", bssd).setParameter("param2", "K").executeUpdate();
				
				Job72_BizInflation.createKicsBizInflationMA(bssd).stream().forEach(s -> session.saveOrUpdate(s));
				completJob("SUCCESS", jobLog);

			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
//	private static void job74() {
//		if (jobList.contains("74")) {
//			session.beginTransaction();
//			JobLog jobLog = startJogLog(EJob.ESG73);
//			try {
//				session.createQuery("delete BizInflation a where a.baseYymm=:param" ).setParameter("param", bssd).executeUpdate();
//				
//				Job72_BizInflation.createIfrsBizInflationMA(bssd).stream().forEach(s -> session.saveOrUpdate(s));
//				Job72_BizInflation.createKicsBizInflationMA(bssd).stream().forEach(s -> session.saveOrUpdate(s));
//				completJob("SUCCESS", jobLog);
//
//			} catch (Exception e) {
//				completJob("ERROR", jobLog);
//			}
//			
//			session.saveOrUpdate(jobLog);
//			session.getTransaction().commit();
//		}
//	}
	
	private static void job81() {
		cnt = 0;
		if (jobList.contains("81")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG81);
			try {
				session.createQuery("delete CorpCumPd a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				session.createQuery("delete CorpCrdGrdPd a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				
				Job81_CorporatePd.createCorpPd(bssd).stream().forEach(s -> session.save(s));
				Job81_CorporatePd.createCorpCumPd(bssd).stream().forEach(s -> session.save(s));
				completJob("SUCCESS", jobLog);

			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job82() {
		cnt = 0;
		if (jobList.contains("82")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG82);
			try {
				session.createQuery("delete BizCorpPd a where a.baseYymm=:param and a.applyBizDv=:param2").setParameter("param", bssd).setParameter("param2", "I").executeUpdate();
				
				Job82_BizCorpPd.getBizCorpPdFromCumPd(bssd, "I").stream().forEach(s ->session.saveOrUpdate(s));
				completJob("SUCCESS", jobLog);

			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job83() {
		if (jobList.contains("83")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG83);
			try {
				session.createQuery("delete BizCorpPd a where a.baseYymm=:param and a.applyBizDv=:param2").setParameter("param", bssd).setParameter("param2", "K").executeUpdate();
				Job82_BizCorpPd.getBizCorpPdFromCumPd(bssd, "K").stream().forEach(s ->session.saveOrUpdate(s));
				completJob("SUCCESS", jobLog);

			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}

	private static void job86() {
		if (jobList.contains("86")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG86);
			try {
				session.createQuery("delete SegLgd a where a.baseYymm=:param").setParameter("param", bssd).executeUpdate();
				Job86_SegLgd.getSegLgd(bssd).stream().forEach(s -> session.save(s));
				completJob("SUCCESS", jobLog);

			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}
	
	private static void job87() {
		if (jobList.contains("87")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG87);
			try {
				session.createQuery("delete BizSegLgd a where a.baseYymm=:param and a.applyBizDv=:param2").setParameter("param", bssd).setParameter("param2", "I").executeUpdate();
				
				Job87_BizSegLgd.getBizSegLgd(bssd, "I").stream().forEach(s->session.saveOrUpdate(s));
				completJob("SUCCESS", jobLog);
			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}	
	}
	
	private static void job88() {
		if (jobList.contains("88")) {
			session.beginTransaction();
			JobLog jobLog = startJogLog(EJob.ESG88);
			try {
				session.createQuery("delete BizSegLgd a where a.baseYymm=:param and a.applyBizDv=:param2").setParameter("param", bssd).setParameter("param2", "K").executeUpdate();
				
				Job87_BizSegLgd.getBizSegLgd(bssd, "K").stream().forEach(s->session.saveOrUpdate(s));
				
				completJob("SUCCESS", jobLog);
				
			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
		}
	}

	private static List<EsgRandom> createRandom() {
		GaussianRandomGenerator gen = new GaussianRandomGenerator(new MersenneTwister(Long.valueOf(bssd)));
		
		List<EsgRandom> rndList = new ArrayList<EsgRandom>();
		for(int i =1 ; i<= batchNum * 50; i++) {
			for(int j=1 ; j<= projectionYear * 12; j++) {
					double randomNum = gen.nextNormalizedDouble();
					
					rndList.add(EsgRandom.builder()
										.baseYymm(bssd)
										.stdAsstCd("Ran")
										.volCalcId(volCalcId)
										.sceNo(2*i-1)
										.matNum(j)
										.rndNum(randomNum)
										.lastModifiedBy("ESG")
										.lastUpdateDate(LocalDateTime.now())
										.build()
										);
					rndList.add(EsgRandom.builder()
							.baseYymm(bssd)
							.stdAsstCd("Ran")
							.volCalcId(volCalcId)
							.sceNo(2*i)
							.matNum(j)
							.rndNum(-1.0* randomNum)
							.lastModifiedBy("ESG")
							.lastUpdateDate(LocalDateTime.now())
							.build()
							);
			}
		}
		return rndList;
	}

	private static void createCorrelatedRandom() {
		List<String> entryList = new ArrayList<String>();
				
		Map<String, Map<String, Double>> corrMap  =StdAssetVolDao.getStdAssetCorrByMonth(bssd, volCalcId).stream()
														.collect(groupingBy(StdAssetCorr::getMvId, toMap(StdAssetCorr::getRefMvId, StdAssetCorr::getMvHisCorr)));
				
		CorrelatedRandomVectorGenerator gen = Job30_EsgStockScenario.createRandom(bssd, corrMap);
				
		entryList.addAll(corrMap.keySet());
		
		for(int i =1 ; i<= batchNum *50; i++) {
			List<EsgRandom> rndList = new ArrayList<EsgRandom>();
			for(int j=1 ; j<= projectionYear * 12; j++) {	
				double[] sce = gen.nextVector();
				
				for(int k=0; k < sce.length; k++) {
					rndList.add(EsgRandom.builder()
										.baseYymm(bssd)
										.stdAsstCd(entryList.get(k))
										.volCalcId(volCalcId)
										.sceNo(2*i-1)
										.matNum(j)
										.rndNum(sce[k])
										.lastModifiedBy("ESG")
										.lastUpdateDate(LocalDateTime.now())
										.build()
										);	
			
					rndList.add(EsgRandom.builder()
								.baseYymm(bssd)
								.stdAsstCd(entryList.get(k))
								.volCalcId(volCalcId)
								.sceNo(2*i)
								.matNum(j)
								.rndNum(sce[k])
								.lastModifiedBy("ESG")
								.lastUpdateDate(LocalDateTime.now())
								.build()
								);	
					
				}
			}	
		}
	}

	private static void genAvgFwdSce(String bizDv, String pastCurveId, String irCurveId, String matCd, int avgNum, EJob job) {
		List<String> rfCurveIdList = rfCurveMap.values().stream().map(s-> s.getIrCurveId()).collect(toList());
		
		List<? extends IIntRate> pastIntRate;
		if(rfCurveIdList.contains(pastCurveId)) {
			pastIntRate = IrCurveHisDao.getEomTimeSeries(bssd, pastCurveId, matCd, avgNum);
		}else{
			pastIntRate = BizDiscountRateDao.getBizPrecedingByMaturity(bssd, avgNum, irCurveId, matCd);
		}
		
		if(rfCurveIdList.contains(irCurveId)) {
			genAvgFwdWithRfAync(bssd, bizDv, irCurveId, matCd, avgNum, pastIntRate, job);
			
		}else {
			genAvgFwdWithAdjAync(bssd, bizDv, irCurveId, matCd, avgNum, pastIntRate, job);
		}
	}

	private static void genAvgFwdWithRfAync(String bssd, String bizDv, String irCurveId, String matCd, int avgNum, List<? extends IIntRate> pastIntRate, EJob job){
			log.info("Job(Average Forward Rate for DiscRate and DiscRate Scenario under BizDv {} Start !!! ", bizDv);
			session.beginTransaction();
			JobLog jobLog = startJogLog(job);
			try {
				session.createQuery("delete BizDiscRateFwdSce a where a.baseYymm=:param and a.applyBizDv =:bizDv")
				.setParameter("param", bssd)
				.setParameter("bizDv", bizDv)
				.executeUpdate();
				
				
				//Current Term Structure
				List<BizIrCurveHis> currTermStructure = IrCurveHisDao.getBizIrCurveHis(bssd, "A", irCurveId);
				Job33_DiscRateFwdGen.createBizDiscRateFwdSce(bssd, bizDv, irCurveId, "0", matCd, avgNum, currTermStructure, pastIntRate).forEach(s->session.save(s));
				
				//	Scenario Term Structure ...			
				List<CompletableFuture<List<BizDiscRateFwdSce>>> sceJobFutures ;
				
				Map<String, List<BizIrCurveSce>> sceMap = IrCurveHisDao.getBizIrCurveSce(bssd, "A", irCurveId).stream().collect(groupingBy(s->s.getSceNo(), toList()));
				log.info("read sce :{}", LocalTime.now());
				
				sceJobFutures =sceMap.entrySet().stream()
						.map(entry -> CompletableFuture.supplyAsync(
								()->Job33_DiscRateFwdGen.createBizDiscRateFwdSce(bssd, bizDv, irCurveId, entry.getKey(), matCd, avgNum, entry.getValue(), pastIntRate), exe))
						.collect(toList());
				
				List<BizDiscRateFwdSce> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(toList());
				
				log.info("Forward Rate From Scenario Term Structure  : {}", rst.size());
				cnt = 1;
				totalSize = rst.size();
				
				for (BizDiscRateFwdSce aa : rst) {
					session.save(aa);
					if (cnt % 50 == 0) {
						session.flush();
						session.clear();
//					log.info("Forward Rate for Disc {}", cnt);
					}
					if (cnt % flushSize == 0) {
						log.info("Forward Rate for Disc Rate {} are Flushed :  {} / {}", bizDv, cnt, totalSize);
					}
					cnt = cnt + 1;
				}
				completJob("SUCCESS", jobLog);

			} catch (Exception e) {
				completJob("ERROR", jobLog);
			}
			session.saveOrUpdate(jobLog);
			session.getTransaction().commit();
	}

	private static void genAvgFwdWithAdjAync(String bssd, String bizDv, String irCurveId, String matCd, int avgNum, List<? extends IIntRate> pastIntRate, EJob job){
		log.info("Job (Average Forward Rate for DiscRate and DiscRate Scenario under BizDv {} Start !!! ", bizDv);
		session.beginTransaction();
		JobLog jobLog = startJogLog(job);
		try {
			session.createQuery("delete BizDiscRateFwdSce a where a.baseYymm=:param and a.applyBizDv =:bizDv")
					.setParameter("param", bssd)
					.setParameter("bizDv", bizDv)
					.executeUpdate();
			
			//Current Term Structure
			List<BizDiscountRate> currTermStructure = BizDiscountRateDao.getTermStructure(bssd, bizDv, irCurveId);
			Job33_DiscRateFwdGen.createBizDiscRateFwdSce(bssd, bizDv, irCurveId, "0", matCd, avgNum, currTermStructure, pastIntRate).forEach(s->session.save(s));
			
	//			Scenario Term Structure ...			
			List<CompletableFuture<List<BizDiscRateFwdSce>>> sceJobFutures ;
			
			Map<String, List<BizDiscountRateSce>> sceMap = BizDiscountRateDao.getTermStructureAllScenario(bssd, bizDv, irCurveId).stream()
																			.collect(groupingBy(s->s.getSceNo(), toList()));
			log.info("read sce :{}", LocalTime.now());
			
			sceJobFutures =sceMap.entrySet().stream()
					.map(entry -> CompletableFuture.supplyAsync(
								()->Job33_DiscRateFwdGen.createBizDiscRateFwdSce(bssd, bizDv, irCurveId, entry.getKey(), matCd, avgNum, entry.getValue(), pastIntRate), exe))
					.collect(toList());
			
			List<BizDiscRateFwdSce> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(toList());
			
			log.info("Forward Rate From Scenario Term Structure  : {}", rst.size());
			cnt = 1;
			totalSize = rst.size();
	
			for (BizDiscRateFwdSce aa : rst) {
				session.save(aa);
				if (cnt % 50 == 0) {
					session.flush();
					session.clear();
	//					log.info("Forward Rate for Disc {}", cnt);
				}
				if (cnt % flushSize == 0) {
					log.info("Forward Rate for Disc Rate {} are Flushed :  {} / {}", bizDv, cnt, totalSize);
				}
				cnt = cnt + 1;
			}
			completJob("SUCCESS", jobLog);

		} catch (Exception e) {
			completJob("ERROR", jobLog);
		}
		session.saveOrUpdate(jobLog);
		session.getTransaction().commit();
	}
	
}
