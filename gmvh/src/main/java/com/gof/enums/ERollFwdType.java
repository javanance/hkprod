package com.gof.enums;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import com.gof.interfaces.INcontRst;

public enum ERollFwdType {

	PREV_CLOSE				(1,  1, 	true,	false, 	true, 	ELossDiv.ALLO,				s-> s.getCtrPolno() )
	
,	PREV_LOSS_ALLO			(2,  2,		false,	false,	false, 	ELossDiv.NA,				s-> s.getProdCd())
,	PREV_REVERSAL			(3,  2, 	false,	false, 	true, 	ELossDiv.REV_ALLO,			s-> s.getProdCd())

,	INIT_RECOG				(4,  1,		false,	false,	true, 	ELossDiv.REVERSE_DELTA,		s-> s.getProdCd())
, 	LIC_RECOG				(5,  1,		true,	false,	false, 	ELossDiv.NA,				s-> s.getProdCd())
,   INIT_CLOSE				(9,  1,		true,	false,	true, 	ELossDiv.ALLO,				s-> s.getProdCd())	

,	LOSS_RECOG				(13,  3,	false,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())
,	INIT_LOSS_CLOSE			(19,  3,	true,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())

,   PUB_CHANGE				(21,  1,	 false,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())
,   PUB_CLOSE				(22,  1,	 true,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())
,	INIT_LOSS_ALLO			(29,  3,	true,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())

,	LOSS_TIME_RELEASE		(31, 4,		false,	false,	true, 	ELossDiv.LOSS_TVOM,		s-> s.getProdCd())
,	LOSS_CF_RELEASE			(32, 4,		false,	false,	true, 	ELossDiv.LOSS_FACE,		s-> s.getProdCd())
,	LOSS_RA_RELEASE			(33, 4,	 	false,	false,	true, 	ELossDiv.LOSS_RA,		s-> s.getProdCd())
,	LOSS_RELEASE_CLOSE		(34, 4,	 	true,	false,	true, 	ELossDiv.CLOSE,		 	s-> s.getProdCd())

,   BEFORE_RELEASE			(39, 1,	 	false,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())

,   TIME_RELEASE			(41, 1,	 	false,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())	
,   TIME_EFFECT				(42, 1,	 	false,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())	
,   RATE_EFFECT				(43, 1,	 	false,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())
,   CURR_CHANGE_RELEASE		(44, 1,	 	false,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())
,	CF_RELEASE				(45, 1,	 	false,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())


//,	LOSS_TIME_RELEASE		(31, 4,	false,	false,s-> s.getProdCd())
//,   TIME_RELEASE			(32, 1,	 false,	false,s-> s.getProdCd())	
//,   TIME_EFFECT				(33, 1,	 false,	false,s-> s.getProdCd())	
//,   RATE_EFFECT				(34, 1,	 false,	false,s-> s.getProdCd())	
//,   UNWIND_CLOSE			(35, 1,	 true,	false,s-> s.getProdCd())
//
//,	LOSS_CF_RELEASE			(41, 4,	false,	false,s-> s.getProdCd())
//,	CF_RELEASE				(42, 1,	 false,	false,s-> s.getProdCd())
//
//,	LOSS_RA_RELEASE			(43, 4,	 false,	false,s-> s.getProdCd())
//,   CURR_CHANGE_RELEASE		(44, 1,	 false,	false,s-> s.getProdCd())
//
//,	LOSS_RELEASE_CLOSE		(45, 4,	 true,	false,s-> s.getProdCd())
//,   BEFORE_RELEASE			(51, 1,	 false,	false,s-> s.getProdCd())

,   ACCRET_INT				(54, 1,	 false,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())

,   CASH_RELEASE			(56, 1,	 false,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())
,   SERVICE_CLOSE			(59, 1,	 true,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())	

,   TIME_CHANGE				(61, 1,	 false,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())	
,   ACTU_CHANGE				(62, 1,	 false,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())	
,   DISC_CHANGE				(64, 1,	 false,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())
,   FINC_CHANGE				(65, 1,	 false,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())	
,   RESI_CHANGE				(66, 1,	 false,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())
,   MOVE_CLOSE				(69, 1,	 true,	false,	true, 	ELossDiv.NA, 	s-> s.getProdCd())	

,   LOSS_REVERSAL			(71, 5,	 false,	true,	true, 	ELossDiv.REVERSE_CLOSE,	s-> s.getProdCd())	
,   LOSS_RESERVE			(72, 5,	 false,	true,	false, 	ELossDiv.NA,			s-> s.getProdCd())	
,   LOSS_REVERSAL_CLOSE		(73, 5,	 true,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())	

,   COVERAGE_RELEASE		(91, 5,	 false,	false, 	false, 	ELossDiv.NA,			s-> s.getProdCd())	
,   CURR_CLOSE				(92, 1,	 true,	false,	true, 	ELossDiv.ALLO,			s-> s.getProdCd())	
,   CURR_LOSS_ALLO			(93, 6,	 false,	false,	false, 	ELossDiv.NA,			s-> s.getProdCd())	
;

	private int order;
	private int runGroup;
	private boolean isClose;
	
	private boolean applyOnlyPositive;
	private boolean isLossRollFwd;
	private ELossDiv  lossDiv;
	
	private Function<INcontRst, String> keyFn;
	private Predicate<INcontRst>		keyPredicate;
	
	private ERollFwdType(int order,int runGroup, boolean isClose, boolean applyOnlyPositive, boolean isLossRollFwd, ELossDiv lossDiv, Function<INcontRst, String> keyFn) {
		this.order = order;
		this.runGroup = runGroup;
		this.isClose = isClose;
		this.applyOnlyPositive = applyOnlyPositive;
		
		this.isLossRollFwd = isLossRollFwd;
		this.lossDiv = lossDiv;
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
	
	public boolean isLossRollFwd() {
		return isLossRollFwd;
	}

	public ELossDiv getLossDiv() {
		return lossDiv;
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
}
