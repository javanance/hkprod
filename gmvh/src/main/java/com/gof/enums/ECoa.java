package com.gof.enums;

import java.util.ArrayList;
import java.util.List;

public enum ECoa {
	
	EPV			(true, true)
,   LOSS_EPV	(true, true)
,   FACE_AMT	(true, true)
,   FACE_LIC	(true, true)
,   TVOM		(false, false)
,   TVOM_LIC		(false, false)
,   LOSS_FACE   (true, true)
,   LOSS_TVOM	(false, false)
,	RA			(true, true)
,	RA_LIC			(true, true)
,   LOSS_RA		(true, true)
,	TVOG		(true, true)
, 	LOSS_TVOG	(true, true)
,   CSM			(true, true)	
,   CSM_EX		(true, true)	
,   CSM_MV		(true, true)	
,   AOCI		(true, true)
,   AOCI_LIC	(true, true)
,   AOCI_MV		(true, true)

,   SURPLUS		(true, true)

, 	CASH		(false,true)
, 	ASSET		(false,true)
,   IREV		(true, false)
,   IREV_EX		(true, false)
,   ICOST		(false,false)
,   ICOST_EX	(false,false)
, 	FREV		(true, false)
, 	FREV_EX		(true, false)
,   FCOST		(false,false)
,   FCOST_EX	(false,false)
,   FCOST_MV	(false,false)
,   OCI			(true, false)


,   LOSS_COMP	(true,false)
,   LOSS_ALLC	(true,false)
,   DAC			(true,false)
,   DAC1		(true,false)
,   DAC2		(true,false)
,   DAC_COMP	(true,false)
,   DMC_COMP	(true,false)
,   CSM_AC		(true,false)
,   CSM_MC		(true,false)
,   EXP_ADJ		(true,false)
,	LOSS		(true,false)			//TODO ::::;
, 	NA			(false,false)
, 	CALC_CSM	(true,false)		//TODO:
;
	
	private boolean isCreditEntry;
	private boolean isBs;
	
	private ECoa(boolean isCreditEntry, boolean isBs) {
		this.isCreditEntry =isCreditEntry;
		this.isBs=isBs;
	}
	
	public boolean isCreditEntry() {
		return isCreditEntry;
	}

	public boolean isBs() {
		return isBs;
	}
	public int getSign() {
		return isCreditEntry? 1: -1;
	}
	public static List<ECoa> getLossCoaList() {
		List<ECoa> rstList = new ArrayList<ECoa>();
		rstList.add(ECoa.LOSS);
		rstList.add(ECoa.LOSS_FACE);
		rstList.add(ECoa.LOSS_TVOM);
		rstList.add(ECoa.LOSS_RA);
		rstList.add(ECoa.LOSS_EPV);
		return rstList;
	}
	
	public static List<ECoa> getCoaList() {
		List<ECoa> rstList = new ArrayList<ECoa>();
//		rstList.add(ECoa.LOSS);
		rstList.add(ECoa.FACE_AMT);
		rstList.add(ECoa.FACE_LIC);
		rstList.add(ECoa.TVOM);
		rstList.add(ECoa.TVOM_LIC);
//		rstList.add(ECoa.CSM);
		rstList.add(ECoa.RA);
		rstList.add(ECoa.RA_LIC);
		rstList.add(ECoa.TVOG);
		rstList.add(ECoa.AOCI);
		rstList.add(ECoa.AOCI_LIC);
//		rstList.add(ECoa.LOSS_FACE);
//		rstList.add(ECoa.LOSS_TVOM);
//		rstList.add(ECoa.LOSS_RA);
		rstList.add(ECoa.FCOST);
		rstList.add(ECoa.ICOST);
		rstList.add(ECoa.IREV);
//		rstList.add(ECoa.DAC);
		rstList.add(ECoa.CASH);
		
		return rstList;
	}
}
