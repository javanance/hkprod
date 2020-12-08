package com.gof.provider;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.gof.dao.CfDao;
import com.gof.dao.DfDao;
import com.gof.dao.MapDao;
import com.gof.dao.MstDao;
import com.gof.entity.DfLv2Eir;
import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MstCalc;
import com.gof.entity.MstCode;
import com.gof.entity.MstGoc;
import com.gof.entity.MstRollFwd;
import com.gof.entity.MstRunset;
import com.gof.enums.ECoa;
import com.gof.enums.EContStatus;
import com.gof.enums.ERollFwdType;
import com.gof.enums.ERunsetType;
import com.gof.infra.GmvConstant;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrvdMst {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String gocGroup		=GmvConstant.GOC_GROUP;							//Default : DEFAULT
	private static	String closeStep	=GmvConstant.ROLLFWD_CLOSE_STEP;				//Default : CLOSE_STEP
	private static	String runsetMode   =GmvConstant.ARK_RUNSET_MODE;				//Default : N

	private static List<MstGoc> 	gocList 		= new ArrayList<MstGoc>();
	private static List<String> 	eirUpdateErrorGocIdList 		= new ArrayList<String>();
	private static Set<String> 		cfGocIdSet 		= new HashSet<String>();
	private static List<MstRollFwd> mstRollFwdList 	= new ArrayList<MstRollFwd>();
	private static List<MstCalc> 	mstCalcList 	= new ArrayList<MstCalc>();
	private static List<MstRunset> 	mstRunsetList 	= new ArrayList<MstRunset>();

	private static Map<String, MstCalc> mstCalcMap 			   = new HashMap<String, MstCalc>();
	private static Map<ERollFwdType, MstRollFwd> mstRollFwdMap = new HashMap<ERollFwdType, MstRollFwd>();
//	private static Map<ECoa, MstRunset> mstRunsetMap 		   = new HashMap<ECoa, MstRunset>();
	private static List<MapJournalRollFwd> journalRollFwdList  = new ArrayList<MapJournalRollFwd>();
	
	private static Map<String, EContStatus> contStatusMap 	= new HashMap<String, EContStatus>();
	
	public static List<MstGoc> getGocList() {
		if(gocList.isEmpty()) {
			gocList = MstDao.getMstGoc().stream()
						.filter(s->s.getUseYn().isTrueFalse())
						.filter(s->s.getGocGroup().equals(gocGroup))
						.filter(s->DateUtil.isGreaterOrEqual(bssd, s.getInitYymm()))
//						.peek(s-> log.info("aaaa :  {},{}", s.getGocId(), s.getInitYymm()))
						.collect(toList());				
		}
		return gocList;
	}
	
	public static List<String> getGocIdList() {
		return getGocList().stream().map(MstGoc::getGocId).collect(toList());				
	}
	
	public static List<String> getEirUpdateErrorGocIdList() {
		if(eirUpdateErrorGocIdList.isEmpty()) {
			eirUpdateErrorGocIdList = DfDao.getLv2Eir(bssd).stream().filter(s->s.getRemark().startsWith("-1")).map(DfLv2Eir::getGocId).collect(toList()); 
		}
		
		return eirUpdateErrorGocIdList; 
	}
	
	public static Set<String> getCfGocIdSet() {
		if(cfGocIdSet.isEmpty()) {
			cfGocIdSet = CfDao.getCfLv1GocByRunsetStream(bssd, GmvConstant.RUNSET_CURR).map(s->s.getGocId()).collect(toSet());
//			log.info("RUNSET_CURR: {},{}" , GmvConstant.RUNSET_CURR);
//			cfGocIdSet.forEach(s-> log.info("CF GOC: {}", s));
		}
		return cfGocIdSet;				
	}
	
	public static MstGoc getMstGoc(String gocId) {
		Optional<MstGoc> temp = getGocList().stream().filter(s->s.getGocId().equals(gocId)).findFirst();
		
		if(!temp.isPresent()) {
			log.error("There are no MstGoc with gocId : {}. Check gocId or MST_GOC Table", gocId);
			System.exit(1);
		}
		return temp.get();

	}
	
	public static List<MstCalc> getMstCalcList() {
		if(mstCalcList.isEmpty()) {
			mstCalcList = MstDao.getMstCalc().stream().filter(s-> s.getUseYn().equals("Y")).collect(toList());
		}
		return mstCalcList;
	}
	
	public static MstCalc  getMstCalc(String calcId) {
		if(mstCalcMap.isEmpty()) {
			mstCalcMap = getMstCalcList().stream().collect(toMap(MstCalc::getCalcId, Function.identity()));
		}
		return mstCalcMap.getOrDefault(calcId, new MstCalc());
	}
	
	public static MstCalc getMstCalcDeltaSum() {
		return getMstCalc(closeStep);				
	}
	
	public static List<MstRunset>  getMstRunsetList() {
		if(mstRunsetList.isEmpty()) {
//			if(GmvConstant.ARK_RUNSET_MODE.equals("Y")) {
//				mstRunsetList = MstDao.getArkMstRunset();
//			}
//			else {
//				mstRunsetList = MstDao.getMstRunset();
//			}
			
			mstRunsetList = MstDao.getMstRunset();
		}
		return mstRunsetList;
	}
	
	public static MstRunset  getMstRunset(String runsetId) {
		Optional<MstRunset>  temp =getMstRunsetList().stream().filter(s->s.getRunsetId().equals(runsetId)).findAny();
		if(temp.isPresent()) {
			return temp.get();
		}
		else {
			log.error("There are no MstRunset with runsetId : {}. Check runsetId or MST_RUNSET Table", runsetId);
			System.exit(1);
		}
		return new MstRunset();
		
//		return getMstRunsetList().stream().filter(s->s.getRunsetId().equals(runsetId)).findAny().orElse(new MstRunset());
	}

//	public static MstRunset getMstRunsetMap(ECoa coa, ERunsetType runsetType){
//		Map<ECoa, MstRunset> rstMap  = getMstRunsetList().stream()
//									    .filter(s->s.getCoaId().equals(coa))
////										.filter(s->s.getRsDivType().equals(rsDivType))			//TODO !!!!
//										.filter(s->s.getRunsetType().equals(runsetType))			//TODO !!!!
////										.filter(s->!s.getPrevYn().isTrueFalse())
////										.filter(s-> s.getPriorDeltaGroup()==null)
//										.collect(toMap(MstRunset::getCoaId, Function.identity(), (s,u)->s));
//		
//		if(!rstMap.containsKey(coa)) {
//			log.error("There are no MstRunset for {}, {}. Check MST_RUNSET Table!!!", coa, runsetType);
//			System.exit(1);
//		}
//		
//		return rstMap.get(coa);
//	}
	
	public static List<MstRunset>  getMstRunsetList(ECoa coa) {
		return getMstRunsetList().stream().filter(s->s.getCoaId().equals(coa)).collect(toList());
	}
	
	
	public static List<MapJournalRollFwd> getJournalRollFwdList(ERollFwdType rollFwd, ECoa coa) {
		return getJournalRollFwdList().stream().filter(s->s.getRollFwdType().equals(rollFwd)).filter(s->s.hasCoa(coa)).collect(toList());
	}
	
	public static List<MapJournalRollFwd> getJournalRollFwdList() {
		if(journalRollFwdList.isEmpty()) {
			journalRollFwdList  = MapDao.getMapJournalRollFwd().stream().filter(s->s.getUseYn().isTrueFalse()).collect(toList());
		}
		return journalRollFwdList;
	}
	
		
	public static List<MstRollFwd> getMstRollFwdList() {
		if(mstRollFwdList.isEmpty()) {
			mstRollFwdList = MstDao.getMstRollFwd().stream()
									.filter(s->s.getUseYn().equals("Y"))
									.sorted((s,u)->s.getRollFwdSeq() - u.getRollFwdSeq())			
									.collect(toList());
		}
		return mstRollFwdList;
	}
	
	
	public static MstRollFwd getMstRollFwd(ERollFwdType rollFwd){
		if(mstRollFwdMap.isEmpty()) {
			mstRollFwdMap = MstDao.getMstRollFwd().stream().collect(toMap(MstRollFwd::getRollFwdType, Function.identity()));
		}
		return mstRollFwdMap.get(rollFwd);
	}
	
//	public static  Map<ERollFwdType, ERollFwdType>  getPriorCloseMap(List<MstRollFwd> rollFwdList) {
//		Map<ERollFwdType, ERollFwdType> rstMap = new HashMap<ERollFwdType, ERollFwdType>();
//		
//		ERollFwdType prior = null;
//		for(MstRollFwd zz : rollFwdList) {
//			if(zz.isCloseStep()) {
//				if(prior !=null) {
//					rstMap.put(zz.getRollFwdType(), prior);
//				}
//				else {
//					rstMap.put(zz.getRollFwdType(), zz.getRollFwdType());
//				}
//				prior = zz.getRollFwdType();
//			}
//		}
//		return rstMap;
//	}
		
	public static  Map<ERollFwdType, ERollFwdType>  getPriorCloseStepMap(List<MstRollFwd> rollFwdList) {
		Map<ERollFwdType, ERollFwdType> rstMap = new HashMap<ERollFwdType, ERollFwdType>();
		
		ERollFwdType prior = null;
		for(MstRollFwd zz : rollFwdList) {
			if(zz.isCloseStep()) {
				if(prior ==null) {
					rstMap.put(zz.getRollFwdType(), zz.getRollFwdType());
				}
				else {
					rstMap.put(zz.getRollFwdType(), prior);
				}
				prior = zz.getRollFwdType();
			}
			else {
				rstMap.put(zz.getRollFwdType(), prior);
			}
		}
		return rstMap;
	}
	
	
	public static EContStatus getContStatus(String status){
		if(contStatusMap.isEmpty()) {
			contStatusMap = MstDao.getMstCode().stream()
								.filter(s->s.getCodeGroup().equals("STATUS_CD"))
								.collect(toMap(MstCode::getCodeOrigin, MstCode::getContStatus, (s,u)->s));
		}
		return contStatusMap.getOrDefault(status, EContStatus.NA);
	}
	
	
	
}