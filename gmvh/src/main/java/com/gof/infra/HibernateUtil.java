
package com.gof.infra;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HibernateUtil {
	private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;
    private static int cnt =0;
    
    
//	public static SessionFactory getSessionFactory() {
//		if(sessionFactory==null) {
//			Map<String, String> settings = new HashMap<String,String>();
//			settings.put(Environment.DRIVER, GmvConstant.DRIVER);
//			settings.put(Environment.DIALECT,GmvConstant.DIALECT);
//			settings.put(Environment.URL, 	GmvConstant.URL);
//			
//			settings.put(Environment.USER, GmvConstant.USERNAME);
//			settings.put(Environment.PASS, GmvConstant.PASSWORD);
//			
//			return genSessionFactory(settings);
//			
//		}
//		return sessionFactory;
//	}

	public static SessionFactory getSessionFactory() {
		if(sessionFactory==null) {
			Map<String, String> settings = new HashMap<String,String>();
			settings.put(Environment.DRIVER, "oracle.jdbc.driver.OracleDriver");
			settings.put(Environment.DIALECT, "org.hibernate.dialect.Oracle10gDialect");
			settings.put(Environment.URL, "jdbc:oracle:thin:@10.10.132.31:3521/HLIFRSP");
			
//			settings.put(Environment.USER, "IESGOWN");
//			settings.put(Environment.PASS, "ZQCP^9XL");
			
			settings.put(Environment.USER, "IGMVOWN");
			settings.put(Environment.PASS, "IGMVOWN#32");

//			logger.info("getSesson Factory no Arg");
			return genSessionFactory(settings);
			
		}
		return sessionFactory;
	 }
	
	public static SessionFactory getSessionFactory(Properties prop) {
		
		Map<String, String> settings = new HashMap<String,String>();
		settings.put(Environment.DRIVER, prop.getProperty("driver"));
		settings.put(Environment.URL, prop.getProperty("url"));
		settings.put(Environment.USER, prop.getProperty("username"));
		settings.put(Environment.PASS, prop.getProperty("password"));
		settings.put(Environment.DIALECT, prop.getProperty("dialect"));
		
		settings.entrySet().forEach(s->  log.info("getSesson Factory with Arg {}: {}", s.getKey(), s.getValue()));
		return genSessionFactory(settings);
		
	}
	
	public static void shutdown() {
	  if (registry != null) {
	     StandardServiceRegistryBuilder.destroy(registry);
	  }
	}

	private  static SessionFactory genSessionFactory(Map<String, String> settings) {
	    if (sessionFactory == null) {
	      try {
	        // Create registry
	    	  
	    	URL configFile = HibernateUtil.class.getResource("/com/hkl/ifrs/bat/gmv/hibernate.cfg.gmv.xml");
	    	if(configFile == null) {
	    		configFile = HibernateUtil.class.getClassLoader().getResource("../hibernate.cfg.gmv.xml");
	    	}
	    	
	        registry = new StandardServiceRegistryBuilder()
//			        		.configure()
			        		.configure(configFile)
	        				.applySettings(settings)
	        				.build();

	        // Create MetadataSources
	        MetadataSources sources = new MetadataSources(registry);

	        // Create Metadata
	        Metadata metadata = sources.getMetadataBuilder().build();

	        // Create SessionFactory
	        sessionFactory = metadata.getSessionFactoryBuilder().build();
	        log.info("Generate Session : new Session is generated with {}", settings);

	      } catch (Exception e) {
	        e.printStackTrace();
	        if (registry != null) {
	        	log.info("Generate Session : already Session is generated with {}", settings);
	          StandardServiceRegistryBuilder.destroy(registry);
	        }
	      }
	   }
	   return sessionFactory;
	}
	 
	 
	 
//	private  static SessionFactory createSessionFactory(Map<String, String> settings) {
//	      try {
//	        // Create registry
//	        registry = new StandardServiceRegistryBuilder()
//			        		.configure()
//	        				.applySettings(settings)
//	        				.build();
//	
//	        // Create MetadataSources
//	        MetadataSources sources = new MetadataSources(registry);
//	
//	        // Create Metadata
//	        Metadata metadata = sources.getMetadataBuilder().build();
//	
//	        // Create SessionFactory
//	        sessionFactory = metadata.getSessionFactoryBuilder().build();
//	
//	      } catch (Exception e) {
//	        e.printStackTrace();
//	        if (registry != null) {
//	          StandardServiceRegistryBuilder.destroy(registry);
//	        }
//	      }
//	
//	    return sessionFactory;
//	 }

	
	public static void saveOrUpdate(Session session, int flushSize, Object item) {
		try {
			session.saveOrUpdate(item);
			if(cnt % flushSize == flushSize-1) {
				session.flush();
				session.clear();
//				log.info("in the flush : {},{}", cnt+1, Thread.currentThread().getName());
			}
			cnt = cnt+1;
			
		} catch (Exception e) {
			session.getTransaction().rollback();
			log.info("Error with SaveOrUpdate :  {}" , e);
		}
	}
}
