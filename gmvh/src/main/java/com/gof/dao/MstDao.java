package com.gof.dao;

import java.util.List;
import java.util.stream.Stream;

import org.hibernate.Session;

import com.gof.entity.MstCalc;
import com.gof.entity.MstCfClass;
import com.gof.entity.MstCfGroup;
import com.gof.entity.MstCode;
import com.gof.entity.MstContGoc;
import com.gof.entity.MstGoc;
import com.gof.entity.MstProdGoc;
import com.gof.entity.MstRollFwd;
import com.gof.entity.MstRunset;
import com.gof.infra.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class MstDao {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static List<MstGoc> getMstGoc(){
		String query = "select a from MstGoc a" ;
		return session.createQuery(query, MstGoc.class).getResultList();
	}
	
	public static List<MstCalc> getMstCalc(){
		String query = "select a from MstCalc a" ;
		return session.createQuery(query, MstCalc.class).getResultList();
	}
	
	public static List<MstCfGroup> getMstCfGroup(){
		String query = "select a from MstCfGroup a" ;
		return session.createQuery(query, MstCfGroup.class).getResultList();
	}
	
	public static List<MstCfClass> getMstCfClass(){
		String query = "select a from MstCfClass a" ;
		return session.createQuery(query, MstCfClass.class).getResultList();
	}
	
//	public static List<MstRunset> getMstRunset(){
//		String query = "select a from MstRunset a where a.runsetType in (:runsetType, :allRunset)";
//		return session.createQuery(query, MstRunset.class).setParameter("runsetType", "BOX").setParameter("allRunset", "ALL").getResultList();
//	}
	
	public static List<MstRunset> getMstRunset(){
		String query = "select a from MstRunset a ";
		return session.createQuery(query, MstRunset.class).getResultList();
	}
	
//	public static List<MstRunset> getArkMstRunset(){
//		String query = "select a from MstRunset a where a.runsetType in (:runsetType, :allRunset)" ;
//		
//		return session.createQuery(query, MstRunset.class).setParameter("runsetType", "ARK").setParameter("allRunset", "ALL").getResultList();
//	}
	
//	public static List<MstRunsetOther> getMstRunsetOther(){
//		String query = "select a from MstRunsetOther a " ;
//		return session.createQuery(query, MstRunsetOther.class).getResultList();
//	}
	
	public static List<MstProdGoc> getMstProdGoc(){
		String query = "select a from MstProdGoc a " ;
		return session.createQuery(query, MstProdGoc.class).getResultList();
	}
	
//	public static List<MstBoxCoa> getMstBoxCoa(){
//		String query = "select a from MstBoxCoa a where a.useYn =:useYn";
//		return session.createQuery(query, MstBoxCoa.class).setParameter("useYn", EBoolean.Y).getResultList();
//	}
	
	public static List<MstRollFwd> getMstRollFwd(){
		String query = "select a from MstRollFwd a ";
		return session.createQuery(query, MstRollFwd.class).getResultList();
	}
	
//	public static List<MstRfwd> getMstRollFwdNew(){
//		String query = "select a from MstRfwd a ";
//		return session.createQuery(query, MstRfwd.class).getResultList();
//	}
//	
//	public static List<MstRsDiv> getMstRsDiv(){
//		String query = "select a from MstRsDiv a ";
//		return session.createQuery(query, MstRsDiv.class).getResultList();
//	}
	
	
	public static List<MstContGoc> getMstContGoc(){
		String query = "select a from MstContGoc a ";
		return session.createQuery(query, MstContGoc.class).getResultList();
	}
	
	public static Stream<MstContGoc> getMstContGocStream(){
		String query = "select a from MstContGoc a ";
		return session.createQuery(query, MstContGoc.class).stream();
	}
	
	public static List<String> getUsedGocForUpdate(){
		String query = "select a.gocId from MstContGoc a group by a.gocId";
		return session.createQuery(query, String.class).getResultList();
	}
	
	public static List<MstCode> getMstCode(){
		String query = "select a from MstCode a";
		return session.createQuery(query, MstCode.class).getResultList();
	}
	
//	public static List<MstCfLv2> getMstCfLv2(){
//		String query = "select a from MstCfLv2 a ";
//		return session.createQuery(query, MstCfLv2.class).getResultList();
//	}
}
