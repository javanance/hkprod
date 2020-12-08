package com.gof.util;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gof.dao.DaoUtil;
import com.gof.entity.EsgMeta;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParamUtil {
	private static Map<String, List<EsgMeta>> dbParamMap = new HashMap<String, List<EsgMeta>>();
	private static Map<String, String> paramMap = new HashMap<String, String>();

	static {
		Session session = HibernateUtil.getSessionFactory().openSession();
	    session.beginTransaction();
	    
	    Map<String, Object> param = new HashMap<String, Object>();
	    
    	List<EsgMeta> paramList = DaoUtil.getEntities(EsgMeta.class, param);
    	dbParamMap = paramList.stream().filter(s-> s.getUseYn().isTrueFalse()).collect(groupingBy(s->s.getGroupId(), toList()));

	}
	
	public static List<EsgMeta> getParamList(String groupId) {
		if(!dbParamMap.containsKey(groupId)) {
			groupId ="BASE";
		}
		paramMap = dbParamMap.getOrDefault(groupId, new ArrayList<EsgMeta>()).stream().collect(toMap(s->s.getParamKey(), s->s.getParamValue()));
		return dbParamMap.getOrDefault(groupId, new ArrayList<EsgMeta>());
	}
	
	public static Map<String, String> getParamMap() {
		if(paramMap.size()!=0) {
			return paramMap; 
		}
		else {
			log.info("call size zero param Map");;
			return dbParamMap.getOrDefault("BASE", new ArrayList<EsgMeta>()).stream().collect(toMap(s->s.getParamKey(), s->s.getParamValue()));
		}
	}
	
	public static Map<String, List<EsgMeta>>  getDbParamMap() {
		return dbParamMap;
	}
}
