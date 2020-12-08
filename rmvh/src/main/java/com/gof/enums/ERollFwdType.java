package com.gof.enums;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import com.gof.interfaces.INcontRst;

public enum ERollFwdType {

	PREV_CLOSE				(0, 	0,	10,  1, 	true,	false, s-> s.getCtrPolno() )

,	PREV_LOSS_ALLO			(2, 	0,	10,	2,	false,	false,s-> s.getProdCd())
,	PREV_REVERSAL			(3, 	0, 	10,	2, 	false,	false,s-> s.getProdCd())
,	INIT_RECOG				(4, 	0, 	10,	1,	false,	false,s-> s.getProdCd())
//,   INIT_CLOSE				(5, 	0,	5,	1,	true,	false,s-> s.getProdCd())	

//,	LOSS_RECOG				(6, 			3,	false,	false,s-> s.getProdCd())
,	LOSS_RCV_RECOG			(6, 	0, 	10,	3,	false,	false,s-> s.getProdCd())
,	INIT_LOSS_CLOSE			(10, 	0, 	10, 3,	true,	false,s-> s.getProdCd())

,   PUB_CHANGE				(11, 	10, 15,	1,	false,	false,s-> s.getProdCd())
,   PUB_CLOSE				(12,	10, 15,	1,	true,	false,s-> s.getProdCd())
,	INIT_LOSS_ALLO			(15,	10, 15,	3,	true,	false,s-> s.getProdCd())

,	LOSS_TIME_RELEASE		(16, 	15, 20,	4,	false,	false,s-> s.getProdCd())
,	LOSS_CF_RELEASE			(17, 	15, 20,	4,	false,	false,s-> s.getProdCd())
,	LOSS_RA_RELEASE			(18, 	15, 20,	4,	false,	false,s-> s.getProdCd())
,	LOSS_RELEASE_CLOSE		(20, 	15, 20,	4,	true,	false,s-> s.getProdCd())

,   BEFORE_RELEASE			(21,	20, 40,	1,	false,	false,s-> s.getProdCd())

,   TIME_RELEASE			(26,	20, 40,	1,	false,	false,s-> s.getProdCd())	
,   TIME_EFFECT				(27,	20, 40,	1,	false,	false,s-> s.getProdCd())	
,   RATE_EFFECT				(28,	20, 40,	1,	false,	false,s-> s.getProdCd())	
,   ACCRET_INT				(29,	20, 40,	1,	false,	false,s-> s.getProdCd())

,	CF_RELEASE				(31,	20, 40,	1,	false,	false,s-> s.getProdCd())
,   CASH_RELEASE			(32,	20, 40,	1,	false,	false,s-> s.getProdCd())	

,   CURR_CHANGE_RELEASE		(36,	20, 40,	1,	false,	false,s-> s.getProdCd())
,   LOSS_RCV_REVERSAL		(37,	20, 40,	1,	false,	false,s-> s.getProdCd())
,   SERVICE_CLOSE			(40,	20, 40,	1,	true,	false,s-> s.getProdCd())	

,   TIME_CHANGE				(41,	40, 50,	1,	false,	false,s-> s.getProdCd())	
,   ACTU_CHANGE				(42,	40, 50,	1,	false,	false,s-> s.getProdCd())	
,   DISC_CHANGE				(44,	40, 50,	1,	false,	false,s-> s.getProdCd())
,   FINC_CHANGE				(46,	40, 50,	1,	false,	false,s-> s.getProdCd())	
,   LOSS_RCV_CHANGE			(47,	40, 50,	1,	false,	false,s-> s.getProdCd())
,   MOVE_CLOSE				(50,	40, 50,	1,	true,	false,s-> s.getProdCd())	

,   LOSS_REVERSAL			(61,	50, 65,	5,	false,	true,s-> s.getProdCd())	
,   LOSS_RESERVE			(62,	50, 65,	5,	false,	true,s-> s.getProdCd())	
,   LOSS_REVERSAL_CLOSE		(65,	50, 65,	5,	true,	false,s-> s.getProdCd())	

,   COVERAGE_RELEASE		(91,	65, 95,	5,	false,	false,s-> s.getProdCd())	
,   CURR_CLOSE				(95,	65, 95,	1,	true,	false,s-> s.getProdCd())

,   CURR_LOSS_ALLO			(96,	95, 100,6,	false,	false,s-> s.getProdCd())	
;
	private int order;
	private int priorCloseStepNum;
	private int myCloseStepNum;
	private int runGroup;
	private boolean isClose;
	
	private boolean applyOnlyPositive;
	
	private Function<INcontRst, String> keyFn;
	private Predicate<INcontRst>		keyPredicate;
	
	private ERollFwdType(int order, int priorCloseStepNum, int myCloseStepNum, int runGroup, boolean isClose, boolean applyOnlyPositive, Function<INcontRst, String> keyFn) {
		this.order = order;
		this.priorCloseStepNum = priorCloseStepNum;
		this.myCloseStepNum = myCloseStepNum;
		this.runGroup = runGroup;
		this.isClose = isClose;
		this.applyOnlyPositive = applyOnlyPositive;
		this.keyFn = keyFn;
//		this.keyPredicate = keyPredicate;
	}
	
	public int getOrder() {
		return order;
	}
	
	public boolean isClose() {
		return isClose;
	}
		
	public boolean isApplyOnlyPositive() {
		return applyOnlyPositive;
	}
	
	public boolean isApply(double boxValue) {
		if(applyOnlyPositive) {
			return boxValue >=0 ? true: false;
		}
		return true;
	}
	
	public Function<INcontRst, String> getKeyFn() {
		return keyFn;
	}

	public Predicate<INcontRst> getKeyPredicate() {
		return keyPredicate;
	}
	
	public int getRunGroup() {
		return runGroup;
	}
	
	public static Map<Integer, List<ERollFwdType>> getRollFwdTypeByGroup(){
		return Arrays.stream(ERollFwdType.values()).collect(groupingBy(ERollFwdType::getRunGroup, toList()));
	}
	
	public static List<ERollFwdType> getRollFwdTypeByGroup(int runGroup){
		return Arrays.stream(ERollFwdType.values()).filter(s->s.getRunGroup()==runGroup).collect(toList());
	}
	
	public static Map<Integer, ERollFwdType> getCloseStep(){
		return Arrays.stream(ERollFwdType.values()).filter(s->s.isClose()).collect(toMap(ERollFwdType::getOrder, Function.identity()));
	}
	
	public ERollFwdType getMyCloseStep(){
		return getCloseStep().getOrDefault(this.myCloseStepNum, this);
	}
	
	public ERollFwdType getPriorCloseStep(){
		return getCloseStep().getOrDefault(this.priorCloseStepNum, this);
	}
	
	public static Map<ERollFwdType, ERollFwdType> getMyCloseStepMap(){
		Map<ERollFwdType, ERollFwdType> rstMap 	= new HashMap<ERollFwdType, ERollFwdType>();
		Map<Integer, ERollFwdType> closeStepMap	= getCloseStep();
		
		for(ERollFwdType aa : ERollFwdType.values()) {
			rstMap.put(aa, closeStepMap.get(aa.myCloseStepNum));
		}
		
		return rstMap;
	}
}
