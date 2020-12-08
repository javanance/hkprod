package com.gof.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BuilderUtil {

	public static void main(String[] args) throws IOException {
		Class klass ;
		String pName = "com.hkl.ifrs.bat.entity.";
//		String pName = "com.hkl.ifrs.bat.prejob.";
//		String pName = "com.gof.entity.flat.";

//		String cName = "JournalResult";
//		String cName = "ArkCashFlow";
//		String cName = "IrCurveDetail";
//		String cName = "AccountResult";
//		String cName = "GocCsmFlat";
		String cName = "RstLoss";
		
		
		try {
			klass = Class.forName(pName+cName);
			
			long start = System.nanoTime();
//			System.out.println(Arrays.stream(klass.getDeclaredFields()).map(s->s.getName()).collect(Collectors.toList()).contains("baseDate"));
			long second = System.nanoTime();
			
//			System.out.println(Arrays.stream(klass.getDeclaredFields()).filter(s->s.getName().equals("baseDate")).count());
			long third = System.nanoTime();
			
			log.info("time : {},{}", (second-start)/1000000, (third-second)/1000000);
			
//			bulid(FeResult.class);
			bulid(klass);
			bulidCsvMapper(klass);
//			bulidAnnotation(klass);
			bulidJsonPropOrder(klass);
			bulidLombokBuiler(klass);
			
//			logger.info("csv: {}",ReflectionUtil.buildHeaderCsv(klass), new MovePvRst().toCsv1());

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void bulid(Class klass) {
		StringBuilder builder = new StringBuilder();
		builder.append("public String toCsv(){ \n \t "
			  		+ "StringBuilder builder = new StringBuilder();\n \t "
			  		+ "return builder");
		
		int len = klass.getDeclaredFields().length;
		int i =1;
		for(Field aa : klass.getDeclaredFields()) {
			if(aa.getName().equals("log")) {
				
			}
			else if(i == len ) {
				builder.append(".append(").append(aa.getName()).append(")\n \t \t");
			}
			else {
				builder.append(".append(").append(aa.getName()).append(").append(\",\")\n \t \t");
			}
			i= i+1;
		};
		System.out.println(builder.append(".toString(); \n }").toString())
		;
	}
	public static void bulidJsonPropOrder(Class klass) {
		StringBuilder builder = new StringBuilder();
		builder.append("@JsonPropertyOrder(value={");
		
		int len = klass.getDeclaredFields().length;
		int i =1;
		for(Field aa : klass.getDeclaredFields()) {
			if(!aa.getName().equals("log")){
				builder.append(",\"").append(aa.getName()).append("\"");
			}
			i= i+1;
		};
		System.out.println(builder.append("})").toString().replaceFirst(",", ""));
		;
	}
	
	
	public static void bulidLombokBuiler(Class klass) {
		StringBuilder builder = new StringBuilder();
		builder.append("return ").append(klass.getSimpleName()).append(".builder()\n");
		
		int len = klass.getDeclaredFields().length;
		int i =1;
		for(Field aa : klass.getDeclaredFields()) {
			if(!aa.getName().equals("log")){
				builder.append(".").append(aa.getName()).append("(").append(aa.getName()).append(")\n");
			}
			i= i+1;
		};
		builder.append(".build();");
		System.out.println(builder.toString().replaceFirst(",", ""));
		;
	}
	
	
//	 NewContGocRst.builder().baseYymm(baseYymm)
//		.gocId(goc==null ? "XXXX": goc.getCsmGrpCd())
//		.stStatus("01")
//		.endStatus("01")
//		.irCurveId(irCurveId)
//		.initYymm(initYymm)
//		.irCurveId(irCurveId)
//		.currSysRate(0.0)
//		.outCfAmt(outCfAmt)
//		.inCfAmt(inCfAmt)
//		.cfAmt(cfAmt)
//		.outEpvAmt(outEpvAmt )
//		.inEpvAmt(inEpvAmt)
//		.epvAmt(epvAmt)
//		.tvom(tvom)
//		.csmAmt(csmAmt)
//		.lossAmt(lossAmt)
//		.lossRatio(lossAmt / outEpvAmt)
//		.dacAmt(dacAmt)
//		.lastModifiedBy("GMV")
//		.lastModifiedDate(LocalDateTime.now())
//		.build()
//		;
	public static void bulid(List<String> columns, Class klass) {
		StringBuilder builder = new StringBuilder();
		
		int i =1;
		for(String aa : columns) {
			if(i == columns.size() ) {
				builder.append(".append(").append(StringUtil.ColNametoCarmel(aa)).append(")\n \t \t");
			}
			else {
				builder.append(".append(").append(StringUtil.ColNametoCarmel(aa)).append(").append(\",\")\n \t \t");
			}
			i= i+1;
		};
		System.out.println(builder.append(".toString(); \n }").toString())
		;
	}
	
	
	private static void bulidCsvMapper(Class klass) {
		int len = klass.getDeclaredFields().length;
		int i =0;
		String mName="";
		String tName="";
		StringBuilder builder = new StringBuilder();
		
		for(Field aa : klass.getDeclaredFields()) {
//			mName = "" ;
			tName = aa.getType().getSimpleName();
			
			for(Method bb : klass.getDeclaredMethods()) {
				if(bb.getName().toUpperCase().contains("set".concat(aa.getName()).toUpperCase())){
					mName = bb.getName();
					builder.append("mapProperty(");
					builder.append(i).append(",");
					builder.append(tName.substring(0, 1).toUpperCase().concat(tName.substring(1))).append(".class,");
					builder.append(klass.getSimpleName()).append("::").append(mName).append(");\n \t \t");
					
					i= i+1;
					break;
				}
			}
		}
		System.out.println(builder.toString());
	}
	
	
//	private static void bulidAnnotation(Class klass) {
//		int len = klass.getDeclaredFields().length;
//		int i =0;
//		String mName="";
//		String tName="";
//		StringBuilder builder = new StringBuilder();
//		
//		List<String> propList = new ArrayList<>();
//		
//		for(Field aa : klass.getDeclaredFields()) {
//			for(Annotation anno : aa.getDeclaredAnnotations()) {
//				logger.info("anno : {}", anno);
//				if(anno instanceof ToCsv) {
//					propList.add(i++, aa.getName());
//				}
//			}
////			temp = aa.getDeclaredAnnotation(ToCsv.class);
////			temp != null ?  propLit.add(temp.
////					logger.info("anno : {}", aa.getDeclaredAnnotation(ToCsv.class));
//		}
//		logger.info("anno : {}", propList.toString().replace("[", "").replace("]", ""));
//	}
}
