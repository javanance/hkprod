package com.gof.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.process.Main;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScriptUtil {
	private static List<String> scriptContents = new ArrayList<>();
	private static Set<String> scriptContentsSet = new HashSet<>();
	private static Map<String, String> scriptMap = new HashMap<String, String>();
	
//	Default Script Read  ==>  DB Script ==> File Script ������ update ��. ������ function ���� ���������� update �� ���� �����.
	static {
		final String path = "resources";
		File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		
		String tempScript ;
		
		if(jarFile.isFile()) {
			try {
				JarFile jar = new JarFile(jarFile);
				Enumeration<JarEntry> entries = jar.entries();
				while(entries.hasMoreElements()) {
					final String name = entries.nextElement().getName();
					if(name.startsWith(path +"/")) {
						InputStream dirIns = Main.class.getClassLoader().getResourceAsStream(name);
						BufferedReader dirReader = new BufferedReader(new InputStreamReader(dirIns));
						
						Stream<String> dirStream = dirReader.lines();
						tempScript = dirStream.filter(s -> !s.trim().startsWith("#")).filter(s -> s.length() > 0).collect(Collectors.joining("\n"));
						
						scriptMap.put(name.replace("resources/", "").replace(".R", ""), tempScript);          //TODO : ��ũ��Ʈ �����Ǹ� �ּ� Ǯ��
					}
				}
				jar.close();
			} catch (Exception e) {
				log.error("ScriptUtil file error :  {}", e);
			}
		}
		else {
			try {
				log.info(" ScriptUtil in eclipse0: {},{}" , jarFile.getAbsolutePath(), jarFile.toPath());
//				Path dir = Paths.get(Main.class.getClassLoader().getResource("resources").toURI());
//				for (Path zz : Files.walk(dir).filter(Files::isRegularFile).collect(Collectors.toList())) {	
//				for (Path zz : Files.walk(jarFile.toPath()).filter(Files::isRegularFile).collect(Collectors.toList())) {
				for (Path zz : Files.walk(jarFile.toPath()).filter(Files::isRegularFile).filter(s->s.getFileName().toString().endsWith(".R")).collect(Collectors.toList())) {	
						log.info("aaa : {}",zz.getFileName());
						Stream<String> stream = Files.lines(zz);
						tempScript = stream.filter(s -> !s.trim().startsWith("#")).filter(s -> s.length() > 0).collect(Collectors.joining("\n"));
						scriptMap.put(zz.toFile().getName().replace("resources/", "").replace(".R", ""), tempScript);
						
//						scriptMap.entrySet().stream().forEach(s -> logger.info("script : {},{}", s.getKey(), s.getValue()));
						log.info("Scrip File load");
				}
				
				
			} catch (Exception e) {
				log.error("ScriptUtil file error :  {}", e);
			}
		}
		
		
//		DB Script Read 		
//		Session session = HibernateUtil.getSessionFactory().openSession();
//	    session.beginTransaction();
//	    
//	    Map<String, Object> param = new HashMap<String, Object>();
//	    param.put("useYn", EBoolean.Y);
//	    param.put("scriptType", "02");
//	    
//    	List<EsgScript> scriptList = DaoUtil.getEntities(EsgScript.class, param);
//    	for(EsgScript kk : scriptList) {
//    		scriptMap.put(kk.getScriptId(), kk.getScriptContent());
//    	}
		
    	
		scriptContents = scriptMap.values().stream().collect(Collectors.toList());
//		logger.info("script : {}",scriptContents);
	}

	public static List<String> getScriptContents(String dir) {
		String tempScript ;
		try (Stream<Path> stream = Files.walk(Paths.get(dir))){
			for(Path aa: stream.filter(s -> !s.toFile().isDirectory()).filter(s -> s.getFileName().toString().endsWith(".R")).collect(Collectors.toList())) {
				tempScript = Files.lines(aa).filter(s -> !s.trim().startsWith("#")).filter(s -> !s.startsWith("source")).collect(Collectors.joining("\n"));
				scriptMap.put(aa.toFile().getName().replace(".R", ""), tempScript);
			}
		} catch (Exception e) {
			log.info("Error : {}", e);
		}
		log.info("File Script created" );
		scriptContents = scriptMap.values().stream().collect(Collectors.toList());
		
		return scriptContents;
	}
	
	public static List<String> getScriptContents() {
		if(scriptContents.size()==0) {
//			return getScriptContents();
			load();
//			loadOld();
		}
		return scriptContents;
	}
	
	public static Set<String> getScriptContentsSet() {
		if(scriptContentsSet.size()==0) {
//			return getScriptContents();
			load();
		}
		return scriptContentsSet;
	}
	public static Map<String, String> getScriptMap() {
		return scriptMap;
	}
	
	
	 private static void load() {
			final String path = "resources";
			File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			String tempScript ;
			
			if(jarFile.isFile()) {
				
				log.info(" ScriptUtil in Jar1: {}" , jarFile.getAbsolutePath());
				try {
					JarFile jar = new JarFile(jarFile);
					Enumeration<JarEntry> entries = jar.entries();
					while(entries.hasMoreElements()) {
						final String name = entries.nextElement().getName();
						if(name.startsWith(path +"/")) {
							InputStream dirIns = Main.class.getClassLoader().getResourceAsStream(name);
							BufferedReader dirReader = new BufferedReader(new InputStreamReader(dirIns));
//							log.info("Jar entry : {},{}", name);
							
							Stream<String> dirStream = dirReader.lines();
							tempScript = dirStream.filter(s -> !s.trim().startsWith("#")).filter(s -> s.length() > 0).collect(Collectors.joining("\n"));
//							log.info("Jar entry1 : {},{}", name, tempScript);
							
							scriptMap.put(name.replace("resources/", "").replace(".R", ""), tempScript);          //TODO : 
//							scriptMap.entrySet().forEach(s->log.info("aaa : {}", s.getKey(), s.getValue().length()));
						}
					}
					jar.close();
				} catch (Exception e) {
					log.error("ScriptUtil file error 1:  {}", e);
				}
			}
			else {
				try {
//					log.info(" ScriptUtil in eclipse0: {},{}" , jarFile.getAbsolutePath(), jarFile.toPath());
					
//					Path dir = Paths.get(Main.class.getClassLoader().getResource("resources").toURI());
//					log.info(" ScriptUtil in eclipse1: {},{}" ,  dir.toFile().getAbsolutePath() );
//					Files.walk(jarFile.toPath()).forEach(s-> log.info("file : {},{},{}",  Files.isRegularFile(s), s.getFileName(), s.getFileName().toString().endsWith(".R")));
					for (Path zz : Files.walk(jarFile.toPath()).filter(Files::isRegularFile).filter(s->s.getFileName().toString().endsWith(".R")).collect(Collectors.toList())) {	
							Stream<String> stream = Files.lines(zz);
//							log.info(" ScriptUtil in eclipse1: {},{}" ,  zz.getFileName());
							tempScript = stream.filter(s -> !s.trim().startsWith("#")).filter(s -> s.length() > 0).collect(Collectors.joining("\n"));
							scriptMap.put(zz.toFile().getName().replace("resources/", "").replace(".R", ""), tempScript);
							
//							scriptMap.entrySet().stream().forEach(s -> logger.info("script : {},{}", s.getKey(), s.getValue()));
//							logger.info("Scrip File load : {}", tempScript);
					}
				} catch (Exception e) {
					log.error("ScriptUtil file error :  {}", e);
				}
			}
			
			
//			DB Script Read 		
			/*Session session = HibernateUtil.getSessionFactory().openSession();
		    session.beginTransaction();
		    
		    Map<String, Object> param = new HashMap<String, Object>();
		    param.put("useYn", EBoolean.Y);
		    param.put("scriptType", "02");
		    
	    	List<EsgScript> scriptList = DaoUtil.getEntities(EsgScript.class, param);
	    	for(EsgScript kk : scriptList) {
	    		scriptMap.put(kk.getScriptId(), kk.getScriptContent());
	    	}*/
			
//			scriptMap.values().forEach(s-> logger.info("scritp : {}", s));
	    	
//	    	scriptContents = scriptMap.entrySet().stream().filter(s->s.getKey().startsWith("Hw")).map(s ->s.getValue()).collect(Collectors.toList());
			scriptContents = scriptMap.values().stream().collect(Collectors.toList());
			scriptContentsSet =scriptMap.values().stream().collect(Collectors.toSet());
			
			
//			logger.info("script : {}",scriptContents);
		}
	 
	 
	 private static void loadOld() {
			final String path = "resources";
			File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			log.info(" ScriptUtil file: {},{}" , jarFile.getAbsolutePath(), jarFile.isFile());
			String tempScript ;
			
			if(jarFile.isFile()) {
				
				log.info(" ScriptUtil in Jar1: {}" , jarFile.getAbsolutePath());
				try {
					JarFile jar = new JarFile(jarFile);
					Enumeration<JarEntry> entries = jar.entries();
					while(entries.hasMoreElements()) {
						final String name = entries.nextElement().getName();
						if(name.startsWith(path +"/")) {
							InputStream dirIns = Main.class.getClassLoader().getResourceAsStream(name);
							BufferedReader dirReader = new BufferedReader(new InputStreamReader(dirIns));
							log.info("Jar entry : {},{}", name);
							
							Stream<String> dirStream = dirReader.lines();
							tempScript = dirStream.filter(s -> !s.trim().startsWith("#")).filter(s -> s.length() > 0).collect(Collectors.joining("\n"));
							log.info("Jar entry1 : {},{}", name, tempScript);
							
							scriptMap.put(name.replace("resources/", "").replace(".R", ""), tempScript);          //TODO : 
							scriptMap.entrySet().forEach(s->log.info("aaa : {}", s.getKey()));
						}
					}
					jar.close();
				} catch (Exception e) {
					log.error("ScriptUtil file error 1:  {}", e);
				}
			}
			else {
				try {
					log.info(" ScriptUtil in eclipse0: {},{}" , jarFile.getAbsolutePath());
					log.info(" ScriptUtil in eclipse2: {},{}" , Main.class.getClassLoader().getResource("resources").toURI());
					log.info(" ScriptUtil in eclipse3: {},{}" , Main.class.getClassLoader().getResource("resources"));
					Path dir = Paths.get(Main.class.getClassLoader().getResource("resources").toURI());
					log.info(" ScriptUtil in eclipse1: {},{}" ,  dir.getFileName());
					
					for (Path zz : Files.walk(dir).filter(Files::isRegularFile).collect(Collectors.toList())) {	
							Stream<String> stream = Files.lines(zz);
							tempScript = stream.filter(s -> !s.trim().startsWith("#")).filter(s -> s.length() > 0).collect(Collectors.joining("\n"));
//							log.info(" ScriptUtil in eclipse1: {},{}" ,  zz.getFileName(), tempScript);
							scriptMap.put(zz.toFile().getName().replace("resources/", "").replace(".R", ""), tempScript);
							
//							scriptMap.entrySet().stream().forEach(s -> logger.info("script : {},{}", s.getKey(), s.getValue()));
//							logger.info("Scrip File load : {}", tempScript);
					}
				} catch (Exception e) {
					log.error("ScriptUtil file error :  {}", e);
				}
			}
			
			
//			DB Script Read 		
			/*Session session = HibernateUtil.getSessionFactory().openSession();
		    session.beginTransaction();
		    
		    Map<String, Object> param = new HashMap<String, Object>();
		    param.put("useYn", EBoolean.Y);
		    param.put("scriptType", "02");
		    
	    	List<EsgScript> scriptList = DaoUtil.getEntities(EsgScript.class, param);
	    	for(EsgScript kk : scriptList) {
	    		scriptMap.put(kk.getScriptId(), kk.getScriptContent());
	    	}*/
			
//			scriptMap.values().forEach(s-> logger.info("scritp : {}", s));
	    	
//	    	scriptContents = scriptMap.entrySet().stream().filter(s->s.getKey().startsWith("Hw")).map(s ->s.getValue()).collect(Collectors.toList());
			scriptContents = scriptMap.values().stream().collect(Collectors.toList());
			scriptContentsSet =scriptMap.values().stream().collect(Collectors.toSet());
			
			
//			logger.info("script : {}",scriptContents);
		}
	 
	 
	 private static void loadWorked() {
			final String path = "resources";
			File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			log.info(" ScriptUtil file: {},{}" , jarFile.getAbsolutePath(), jarFile.isFile());
			String tempScript ;
			
			if(jarFile.isFile()) {
				
				log.info(" ScriptUtil in Jar1: {}" , jarFile.getAbsolutePath());
				try {
					JarFile jar = new JarFile(jarFile);
					Enumeration<JarEntry> entries = jar.entries();
					while(entries.hasMoreElements()) {
						final String name = entries.nextElement().getName();
						if(name.startsWith(path +"/")) {
							InputStream dirIns = Main.class.getClassLoader().getResourceAsStream(name);
							BufferedReader dirReader = new BufferedReader(new InputStreamReader(dirIns));
							log.info("Jar entry : {},{}", name);
							
							Stream<String> dirStream = dirReader.lines();
							tempScript = dirStream.filter(s -> !s.trim().startsWith("#")).filter(s -> s.length() > 0).collect(Collectors.joining("\n"));
							log.info("Jar entry1 : {},{}", name, tempScript);
							
							scriptMap.put(name.replace("resources/", "").replace(".R", ""), tempScript);          //TODO : 
							scriptMap.entrySet().forEach(s->log.info("aaa : {}", s.getKey(), s.getValue().length()));
						}
					}
					jar.close();
				} catch (Exception e) {
					log.error("ScriptUtil file error 1:  {}", e);
				}
			}
			else {
				try {
					log.info(" ScriptUtil in eclipse0: {},{}" , jarFile.getAbsolutePath(), jarFile.toPath());
					
//					Path dir = Paths.get(Main.class.getClassLoader().getResource("resources").toURI());
//					log.info(" ScriptUtil in eclipse1: {},{}" ,  dir.toFile().getAbsolutePath() );
//					Files.walk(jarFile.toPath()).forEach(s-> log.info("file : {},{},{}",  Files.isRegularFile(s), s.getFileName(), s.getFileName().toString().endsWith(".R")));
					for (Path zz : Files.walk(jarFile.toPath()).filter(Files::isRegularFile).filter(s->s.getFileName().toString().endsWith(".R")).collect(Collectors.toList())) {	
							Stream<String> stream = Files.lines(zz);
							log.info(" ScriptUtil in eclipse1: {},{}" ,  zz.getFileName());
							tempScript = stream.filter(s -> !s.trim().startsWith("#")).filter(s -> s.length() > 0).collect(Collectors.joining("\n"));
							scriptMap.put(zz.toFile().getName().replace("resources/", "").replace(".R", ""), tempScript);
							
//							scriptMap.entrySet().stream().forEach(s -> logger.info("script : {},{}", s.getKey(), s.getValue()));
//							logger.info("Scrip File load : {}", tempScript);
					}
				} catch (Exception e) {
					log.error("ScriptUtil file error :  {}", e);
				}
			}
			
			
//			DB Script Read 		
			/*Session session = HibernateUtil.getSessionFactory().openSession();
		    session.beginTransaction();
		    
		    Map<String, Object> param = new HashMap<String, Object>();
		    param.put("useYn", EBoolean.Y);
		    param.put("scriptType", "02");
		    
	    	List<EsgScript> scriptList = DaoUtil.getEntities(EsgScript.class, param);
	    	for(EsgScript kk : scriptList) {
	    		scriptMap.put(kk.getScriptId(), kk.getScriptContent());
	    	}*/
			
//			scriptMap.values().forEach(s-> logger.info("scritp : {}", s));
	    	
//	    	scriptContents = scriptMap.entrySet().stream().filter(s->s.getKey().startsWith("Hw")).map(s ->s.getValue()).collect(Collectors.toList());
			scriptContents = scriptMap.values().stream().collect(Collectors.toList());
			scriptContentsSet =scriptMap.values().stream().collect(Collectors.toSet());
			
			
//			logger.info("script : {}",scriptContents);
		}
}
