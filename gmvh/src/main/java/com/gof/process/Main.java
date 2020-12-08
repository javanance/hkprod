package com.gof.process;

import static java.util.stream.Collectors.toList;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.hibernate.Session;

import com.gof.dao.MstDao;
import com.gof.entity.JobLog;
import com.gof.entity.MstGoc;
import com.gof.entity.RstBoxDetail;
import com.gof.enums.ERunArgument;
import com.gof.infra.GmvConstant;
import com.gof.infra.HibernateUtil;
import com.gof.model.MdlRstBox;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
	private static final int THREAD_POOL_SIZE = 1200;
	private static final int THREAD_POOL_SIZE1 = 100;
	private static Properties properties = new Properties();
	
	private static String bssd;
	private static String stBssd;
	private static String vBssd;
	private static String job;
	private static List<String> jobList 		= new ArrayList<String>();

	private static ExecutorService exe;
	private static ExecutorService ioExe;
	private static ExecutorService ioExe1;
	
	private static Session session;
	private static int flushSize;
	private static long cnt;
	private static long elpaseTime0;
	private static long elpaseTime1;

	public static void main(String[] args) throws IOException{
		long first =0;
		long second = 0;
		long third = 0;
		first = System.nanoTime();
		
		initProperties(args);
		initConstant();
		
	// ******************************************************************Prepare  ********************************
//		session = HibernateUtil.getSessionFactory().getCurrentSession();
//		session = HibernateUtil.getSessionFactory().openSession();
		session = HibernateUtil.getSessionFactory(properties).openSession();
		
		exe = Executors.newFixedThreadPool(THREAD_POOL_SIZE, r->{
			Thread t = new Thread(r);
			t.setName("Arazor");
			t.setDaemon(true);
			return t;
		});
		
		ioExe 		= Executors.newFixedThreadPool(THREAD_POOL_SIZE1);
		ioExe1 		= Executors.newFixedThreadPool(1);
//		flushSize 	= Integer.parseInt(properties.getOrDefault("FLUSH_SIZE", "1000").toString());;
		flushSize 	= 1000;
		
		
// ******************************************************************Prepare  ********************************		
		
		String gocId = "";
		
		List<String> params1 = Arrays.asList(bssd);
		
//		Async ...
		for(EJobAsync job : EJobAsync.values()) {
			if(jobList.contains(job.getJobId())) {
//				JobLog jobLog = startJogLog(job);
				session.beginTransaction();
				try {
					if(GmvConstant.DELETE_THEN_INSERT.equals("Y") && job.isDeleteThenInsert()) {
						log.info("Delete Job : {}" , job.getDeleter(session, params1).get());
					}
					log.info("Async Job : {}" , job.getJobId());
//					job.getSupplier().get().forEach(s->saveOrUpdate(s));
					job.getFn().apply(ioExe).forEach(s->log.info("Result Size : {}", s));
//					completJob("SUCCESS", jobLog);
					
				} catch (Exception e) {
//					completJob("ERROR", jobLog);
					log.error("error :  {}", e);
				}
//				session.saveOrUpdate(jobLog);
				session.getTransaction().commit();
			}
		}
		
		List<String> convParam =Arrays.asList(stBssd);
		for(EJobConversion job : EJobConversion.values()) {
			if(jobList.contains(job.getJobId())) {
				GmvConstant.JOB_NO =job.getJobId();
				session.beginTransaction();
				JobLog jobLog = startJogLog(job.name(), job.getJobId()+"_"+job.getDesc());
				
				try {
//					TODO!!! :check Detele???
					if(GmvConstant.DELETE_THEN_INSERT.equals("Y") && job.isDeleteThenInsert()) {
						log.info("Delete Job : {},{}, {}" , job.getJobId(), convParam,  job.getDeleter(session, convParam).get());
					}
					
					job.getSupplier().get().forEach(s->saveOrUpdate(s));
					
					completJobLog("SUCCESS", jobLog);
					session.saveOrUpdate(jobLog);
					
				} catch (Exception e) {
					completJobLog("ERROR", jobLog);
					session.saveOrUpdate(jobLog);
					session.getTransaction().commit();
					log.error("error :  {}", e);
					System.exit(1);

				}finally {
					session.getTransaction().commit();
					session.clear();
//					if(session!=null && session.isOpen()) {
//						session.close();
						
//					}
				}
			}	
		}
		
		
		for(EJobArkConversion job : EJobArkConversion.values()) {
			if(jobList.contains(job.getJobId())) {
				GmvConstant.JOB_NO =job.getJobId();
				session.beginTransaction();
				JobLog jobLog = startJogLog(job.name(), job.getJobId()+"_"+job.getDesc());
				try {
					
					Thread.currentThread().sleep(10_000);
					if(GmvConstant.DELETE_THEN_INSERT.equals("Y") && job.isDeleteThenInsert()) {
						log.info("Delete Job : {},{}, {}" , job.getJobId(), convParam, job.getDeleter(session, convParam).get());
					}	

//					log.info("in the job");
					
					job.getSupplier().get().forEach(s->saveOrUpdate(s));
					
					completJobLog("SUCCESS", jobLog);
					session.saveOrUpdate(jobLog);
					session.getTransaction().commit();
					session.clear();
					
				} catch (Exception e) {
					completJobLog("ERROR", jobLog);
					session.saveOrUpdate(jobLog);
					session.getTransaction().commit();
					session.clear();
					log.error("error :  {}", e);
					System.exit(1);

				}finally {
//					session.saveOrUpdate(jobLog);
//					session.getTransaction().commit();
//					session.clear();
//					if(session!=null && session.isOpen()) {
//						session.close();
						
//					}
					
				}
			}	
		}
		
		
		int deleteCnt =0;
		for(EJobModel job : EJobModel.values()) {
			if(jobList.contains(job.getJobId())) {
				GmvConstant.JOB_NO =job.getJobId();
				session.beginTransaction();
				JobLog jobLog = startJogLog(job.name(), job.getJobId()+"_"+job.getDesc());
				try {
					if(job.equals(EJobModel.GMV71)) {
						jobFor71(EJobByGoc.GMV71);
					}
					else {
						Thread.currentThread().sleep(10_000);
						if(GmvConstant.DELETE_THEN_INSERT.equals("Y") && job.isDeleteThenInsert()) {
							log.info("Delete Job : {},{}, {}" , job.getJobId(), params1, job.getDeleter(session, params1).get());
						}	
//						log.info("in the job");
						job.getSupplier().get().forEach(s->saveOrUpdate(s));
					}
					
					completJobLog("SUCCESS", jobLog);
					session.saveOrUpdate(jobLog);
					session.getTransaction().commit();
					session.clear();
					
				} catch (Exception e) {
					completJobLog("ERROR", jobLog);
					session.saveOrUpdate(jobLog);
					session.getTransaction().commit();
					session.clear();
					log.error("error :  {}", e);
					System.exit(1);

				}finally {
//					session.saveOrUpdate(jobLog);
//					session.getTransaction().commit();
//					session.clear();
//					if(session!=null && session.isOpen()) {
//						session.close();
						
//					}
					
				}
			}	
		}
		
		for(EJobArk job : EJobArk.values()) {
			if(jobList.contains(job.getJobId())) {
				GmvConstant.JOB_NO =job.getJobId();
				session.beginTransaction();
				JobLog jobLog = startJogLog(job.name(), job.getJobId()+"_"+job.getDesc());
				try {
					
					Thread.currentThread().sleep(10_000);
					if(GmvConstant.DELETE_THEN_INSERT.equals("Y") && job.isDeleteThenInsert()) {
						log.info("Delete Job : {},{}, {}" , job.getJobId(), params1, job.getDeleter(session, params1).get());
					}	

//					log.info("in the job");
					
					job.getSupplier().get().forEach(s->saveOrUpdate(s));
					
					completJobLog("SUCCESS", jobLog);
					session.saveOrUpdate(jobLog);
					session.getTransaction().commit();
					session.clear();
					
				} catch (Exception e) {
					completJobLog("ERROR", jobLog);
					session.saveOrUpdate(jobLog);
					session.getTransaction().commit();
					session.clear();
					log.error("error :  {}", e);
					System.exit(1);

				}finally {
//					session.saveOrUpdate(jobLog);
//					session.getTransaction().commit();
//					session.clear();
//					if(session!=null && session.isOpen()) {
//						session.close();
						
//					}
					
				}
			}	
		}
		
		List<MstGoc> gocList = MstDao.getMstGoc()
				.stream().filter(s->s.getUseYn().isTrueFalse())
//				.filter(s->s.getGocId().equals("0201_Y2018_LOSS"))
//				.filter(s->s.getGocId().equals("1092_Y2018_PROF"))
				.filter(s-> GmvConstant.GOC_ID==null || GmvConstant.GOC_ID.contains(s.getGocId()) || s.getGocId().startsWith(GmvConstant.GOC_ID))
//				.stream().filter(s->s.getGocId().equals("01_2015Q1_ETC")).collect(toList())
//				.stream().filter(s->s.getGocId().equals("02_2018Q4_LOSS")).collect(toList())
				.collect(toList())
				;
		
		log.info("Goc List : {},{},{},{}", properties.containsKey("GOC_ID"), properties.get("GOC_ID"),  GmvConstant.GOC_ID, gocList.size());
		
		int gocCnt =0;
		for(MstGoc goc : gocList) {
			if(GmvConstant.GOC_ID ==null || GmvConstant.GOC_ID.contains(goc.getGocId()) || goc.getGocId().startsWith(GmvConstant.GOC_ID)) {
				log.info("Start Job for Goc : {}, {}/{} ", goc.getGocId(), ++gocCnt , gocList.size());
				for(EJobByGoc job : EJobByGoc.values()) {
					if(jobList.contains(job.getJobId())) {
						try {
							Thread.currentThread().sleep(5_000);
						} catch (Exception e) {
							
						}
						GmvConstant.JOB_NO =job.getJobId();
						session.beginTransaction();
						JobLog jobLog = startJogLog(job.name(), job.getJobId()+"_"+job.getDesc());
						
						List<String> params2 = Arrays.asList(bssd, goc.getGocId());
						
						try {
//							if(GmvConstant.DELETE_THEN_INSERT.equals("Y") && job.isDeleteThenInsert()) {
							if(GmvConstant.DELETE_THEN_INSERT.equals("Y")) {
								log.info("Delete Job : {}, {}, {}" , job.getJobId(), params2, job.getDeleter(session, params2).get());
							}
							
							job.getFn().apply(goc.getGocId()).forEach(s->saveOrUpdate(s));
							
							completJobLog("SUCCESS", jobLog);
						} catch (Exception e) {
							completJobLog("ERROR", jobLog);
							session.saveOrUpdate(jobLog);
							log.error("error 1 :  {}", e);
							System.exit(2);
						}
						
						session.saveOrUpdate(jobLog);
						session.getTransaction().commit();
						session.clear();
					}
				}
			}
		}
		
		exe.shutdown();
		ioExe.shutdown();
		ioExe1.shutdown();
		second = System.nanoTime();
		third = System.nanoTime();
//		log.info("time: {},{}", (second-first) * 100 / TimeUnit.SECONDS.toNanos(1));
		log.info("time: {},{}", (second-first) /1_000_000_000);
	}	
	
	public static void initProperties(String[] args) {
		Map<ERunArgument, String> argMap = new HashMap<>();
		
		for (String aa : args) {
			log.info("Input Arguments : {}", aa);
			for (ERunArgument bb : ERunArgument.values()) {
				if (aa.split("=")[0].replaceFirst("-D", "").toLowerCase().equals(bb.name())) {
					argMap.put(bb, aa.split("=")[1]);
					break;
				}
			}
		}
		
//		************************************ Date Argument ***************************
		try {
			bssd 	= argMap.get(ERunArgument.time).replace("-", "").replace("/", "").substring(0, 6);
			stBssd 	= argMap.get(ERunArgument.btime).replace("-", "").replace("/", "").substring(0, 6);
			vBssd 	= argMap.get(ERunArgument.vtime).replace("-", "").replace("/", "").substring(0, 6);
			job 	= argMap.getOrDefault(ERunArgument.job, "");
			
			GmvConstant.GOC_ID = argMap.get(ERunArgument.goc);
			
		} catch (Exception e) {
			log.error("Argument error : Time Parameter like -Dtime, -Dvtim, -Dbtime" );
			System.exit(0);
		}
	
//		************************************ Property Argument ***************************
		try {
			FileInputStream fis = new FileInputStream(argMap.get(ERunArgument.properties));
			properties.load(new BufferedInputStream(fis));

		} catch (Exception e) {
			log.warn("Error in Properties Loading : {}", e);
			System.exit(1);
		}
		properties.entrySet().stream().forEach(entry -> log.info("Properties : {},{}", entry.getKey(), entry.getValue()));
		
//		************************************ Property Setup ***************************
		if(properties.get("JOB")==null && job.length()==0) {
			log.error("There are no job input ");
			System.exit(1);
		}
		else {
			String jobString 	 = job.length()==0? properties.get("JOB").toString(): job;
			jobList 	 		 = Arrays.stream(jobString .split(",")).map(s -> s.trim()).collect(Collectors.toList());
			log.info("JOB LIST : {},{},{}", jobString, jobList );		
		}
		
	}

	public static void initConstant() {
		GmvConstant.BSSD 					= bssd;
		GmvConstant.ST_BSSD 				= stBssd;
		GmvConstant.V_BSSD					= vBssd;
		
		GmvConstant.IR_CURVE_ID				= properties.get("IR_CURVE_ID").toString();
		
		GmvConstant.URL 					= properties.get("url").toString();
		GmvConstant.DRIVER 					= properties.get("driver").toString();
		GmvConstant.DIALECT 				= properties.get("dialect").toString();
		GmvConstant.USERNAME				= properties.get("username").toString();
		GmvConstant.PASSWORD				= properties.get("password").toString();
		GmvConstant.TABLE_SCHEMA			= properties.get("TABLE_SCHEMA").toString();
		GmvConstant.TABLE_PREFIX			= properties.get("TABLE_PREFIX").toString();
		
		GmvConstant.ARK_RUNSET_MODE			= properties.get("ARK_RUNSET_MODE").toString();
		
		GmvConstant.DELETE_THEN_INSERT		= properties.getOrDefault("DELETE_THEN_INSERT", "N").toString();
		GmvConstant.GOC_GROUP				= properties.getOrDefault("GOC_GROUP", "DEFAULT").toString();
		GmvConstant.ROLLFWD_CLOSE_STEP		= properties.getOrDefault("ROLLFWD_CLOSE_STEP", "CLOSE_STEP").toString();
		
		GmvConstant.GOC_ID					= properties.containsKey("GOC_ID") && GmvConstant.GOC_ID==null ? properties.get("GOC_ID").toString() : GmvConstant.GOC_ID;
		
		GmvConstant.COHORT_TYPE				= properties.getOrDefault("COHORT_TYPE", "Y").toString();
		
		String tempTenorList 				= properties.getOrDefault("GROUP_TENOR_LIST", "61").toString();
		
		GmvConstant.MAX_RATE_TENOR 			= Integer.parseInt(properties.getOrDefault("MAX_RATE_TENOR", "1200").toString());
		GmvConstant.MAX_TENOR				= Integer.parseInt(properties.getOrDefault("MAX_TENOR", "61").toString());
		GmvConstant.GROUP_TENOR_LIST		= Arrays.stream(tempTenorList.split(",")).map(s -> s.trim()).map(Integer::parseInt).collect(toList());
//		GmvConstant.GROUP_TENOR_LIST 		= new ArrayList<Integer>(Arrays.asList(61, 120, 240, 360));
		
		GmvConstant.EIR_ITER_NUM			= Integer.parseInt(properties.getOrDefault("EIR_ITER_NUM", "10").toString());
		GmvConstant.EIR_START_RATE			= Double.parseDouble(properties.getOrDefault("EIR_START_RATE", "0.02").toString());
		GmvConstant.EIR_ERROR_TOLERANCE		= Double.parseDouble(properties.getOrDefault("EIR_ERROR_TOLERANCE", "0.01").toString());
		GmvConstant.EIR_SL_ADJ 				= properties.getOrDefault("EIR_SL_ADJ", "N").toString();
		
		GmvConstant.CURR_EIR_TYPE 			= properties.getOrDefault("CURR_EIR_TYPE", "ALL").toString();
		GmvConstant.INIT_CURVE_YN			= properties.getOrDefault("INIT_CURVE_YN", "Y").toString();
		
		GmvConstant.NEW_CONT_RATE			= properties.getOrDefault("NEW_CONT_RATE", "CURR").toString();
		
		GmvConstant.CALC_TYPE_DAC_RATIO		= properties.getOrDefault("CALC_TYPE_DAC_RATIO", "DEFAULT").toString();
		GmvConstant.CALC_TYPE_COV_UNIT		= properties.getOrDefault("CALC_TYPE_COV_UNIT", "DEFAULT").toString();
		
		GmvConstant.DEFAULT_CSM_RATIO		=  Double.parseDouble(properties.getOrDefault("DEFAULT_CSM_RATIO", "0.03").toString());
		GmvConstant.DEFAULT_DAC_RATIO		=  Double.parseDouble(properties.getOrDefault("DEFAULT_DAC_RATIO", "0.03").toString());
		
//		GmvConstant.CALC_TYPE_LOSS_RATIO	= properties.getOrDefault("CALC_TYPE_LOSS_RATIO", "DEFAULT").toString();
		
		GmvConstant.DAC_CF_GROUP			= properties.getOrDefault("DAC_CF_GROUP", "G_DAC").toString();
		GmvConstant.LOSS_CF_GROUP			= properties.getOrDefault("LOSS_CF_GROUP", "ALL").toString();
		GmvConstant.FV_RS_DIV_ID			= properties.getOrDefault("FV_RS_DIV_ID", "FV").toString();
		
		
		GmvConstant.RUNSET_PREV				= properties.getOrDefault("RUNSET_PREV", "PREV_109-2").toString();	
		GmvConstant.RUNSET_CURR				= properties.getOrDefault("RUNSET_CURR", "CURR_109-2").toString();
		GmvConstant.DELTA_GROUP_LOSS		= properties.getOrDefault("DELTA_GROUP_LOSS", "RUN_PREV_SETTLE").toString();;
		GmvConstant.EIR_CF_RUNSET			= properties.getOrDefault("EIR_CF_RUNSET", 	"FINC_109-1").toString();
		GmvConstant.EIR_TARGET_RUNSET		= properties.getOrDefault("EIR_TARGET_RUNSET", "DISC_108-1").toString();
		
		
		log.info("GMV CONSTANT : {},{},{},{},{}", GmvConstant.CLOSING_RS_DIV_ID, GmvConstant.GROUP_TENOR_LIST);
		
		for(Map.Entry<Object, Object> entry : properties.entrySet()) {
			GmvConstant.getStrConstant().put(entry.getKey().toString(), entry.getValue().toString());
		}
		
		for(Map.Entry<String, String> entry : GmvConstant.getStrConstant().entrySet()) {
			try {
				GmvConstant.getDoubleConstant().put(entry.getKey(), Double.parseDouble(entry.getValue()));
			} catch (Exception e) {
				continue;
			}
		}	
	}

	private static void saveOrUpdate(Object item) {
		
		
		try {
			session.saveOrUpdate(item);
//			log.info("in the flush1 : {}", cnt);
//			flushSize = 1;	
			if(cnt % flushSize ==0) {
				session.flush();
				session.clear();
				elpaseTime1 = System.nanoTime();
				log.info("in the flush : {},{},{}", cnt, (elpaseTime1 - elpaseTime0)/1_000_000_000, Thread.currentThread().getName());
				elpaseTime0 = elpaseTime1;
			}
			cnt = cnt+1;
			
		} catch (Exception e) {
			session.getTransaction().rollback();
			log.info("Error with DataBase :{}, {}" ,item.toString(), e);
			System.exit(1);
			
		} finally {
//			cnt = 0;
//			session.flush();
//			session.clear();
		}
	}

	private static void save(Object item) {
		try {
			session.save(item);
//			log.info("in the flush1 : {}", cnt);
//			flushSize = 1;	
			if(cnt % flushSize ==0) {
				session.flush();
				session.clear();
				elpaseTime1 = System.nanoTime();
				log.info("in the flush : {},{},{}", cnt, (elpaseTime1 - elpaseTime0)/1_000_000_000, Thread.currentThread().getName());
				elpaseTime0 = elpaseTime1;
			}
			cnt = cnt+1;
			
		} catch (Exception e) {
			session.getTransaction().rollback();
			log.info("Error with DataBase :{}, {}" ,item.toString(), e);
			System.exit(1);
			
		} finally {
//			cnt = 0;
//			session.flush();
//			session.clear();
		}
	}
	
	private static JobLog startJogLog(String jobId, String jobDesc) {
		JobLog jobLog = new JobLog();
		jobLog.setJobStart(LocalDateTime.now());
		
		jobLog.setJobId(jobId);
		jobLog.setCalcStart(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")));
		jobLog.setBaseYymm(bssd);
		jobLog.setCalcDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")));
		jobLog.setJobNm(jobDesc);
		
		log.info("{}({}) : Job Start !!! " , jobId, jobDesc);
		
		return jobLog;
	}
	
	private static void completJobLog(String successDiv, JobLog jobLog) {

		long timeElpse = Duration.between(jobLog.getJobStart(), LocalDateTime.now()).getSeconds();
		jobLog.setCalcEnd(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")));
		jobLog.setCalcScd(successDiv);
		jobLog.setCalcElps(String.valueOf(timeElpse));
		jobLog.setLastModifiedBy("GMV");
		jobLog.setLastUpdateDate(LocalDateTime.now());
		
		log.info("{}({}): Job Completed with {} !!!!", jobLog.getJobId(), jobLog.getJobNm(),successDiv );
	}

//	private static void jobAAAA(String gocId, EJobByGoc job) {
//		GmvConstant.JOB_NO =job.getJobId();
//		session.beginTransaction();
//		JobLog jobLog = startJogLog(job.name(), job.getJobId()+"_"+job.getDesc());
//				
//		List<String> params2 = Arrays.asList(bssd, gocId);
//				
//		try {
//			if(GmvConstant.DELETE_THEN_INSERT.equals("Y")) {
//				log.info("Delete Job : {}, {}, {}" , job.getJobId(), params2, job.getDeleter(session, params2).get());
//			}
//	
	private static void jobFor71(EJobByGoc job) {
//			List<MstGoc> gocList = MstDao.getMstGoc()
			List<String> gocList = MstDao.getMstGoc()
			.stream()
			.filter(s->s.getUseYn().isTrueFalse())
			.map(s->s.getGocId())
//			.filter(s-> GmvConstant.GOC_ID==null || GmvConstant.GOC_ID.contains(s.getGocId()) || s.getGocId().startsWith(GmvConstant.GOC_ID))
			.collect(toList())
			;
	
			
			
			List<CompletableFuture<List<RstBoxDetail>>> 	sceJobFutures 
			= gocList.stream().map(goc-> CompletableFuture.supplyAsync(()-> MdlRstBox.createDetailBeforeEirUpdateA(goc), exe))
					 .collect(toList());

//			Stream<RstBoxDetail> rst = sceJobFutures.stream().map(CompletableFuture::join);
//			Stream<RstBoxDetail> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s->s.stream()).collect(toList());
			List<RstBoxDetail> rst = sceJobFutures.stream().map(CompletableFuture::join).flatMap(s ->s.stream()).collect(Collectors.toList());
			int sceCnt =0;
			
			rst.forEach(s->saveOrUpdate(s));
			
//			JobLog jobLog = startJogLog(job.name(), job.getJobId()+"_"+job.getDesc());
//			try {
//				jobLog = startJogLog(job.name(), job.getJobId()+"_"+job.getDesc());
//				rst.forEach(s->saveOrUpdate(s));
//				
//				completJobLog("SUCCESS", jobLog);
//			} catch (Exception e) {
//				completJobLog("ERROR", jobLog);
//				session.saveOrUpdate(jobLog);
//				log.error("error 1 :  {}", e);
//				System.exit(2);
//			}
//			
//			session.saveOrUpdate(jobLog);
//			session.getTransaction().commit();
//			session.clear();
			
			
	}

}

