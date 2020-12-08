package com.gof.provider;

import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MstRunset;
import com.gof.enums.ECoa;
import com.gof.enums.ESlidingType;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrvdAcct {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;

//	public static double getCoaValue(ECoa coa, MapJournalRollFwd journal, double boxValue, double lossRatio) {
//		double lossAdjBoxValue = boxValue * lossRatio;
//		return getCoaValue(coa, journal, lossAdjBoxValue);
//	}
	
	public static double getCoaValue(ECoa coa, MapJournalRollFwd journal, double lossAdjBoxValue) {
		double coaValue =0.0;
		
		if(coa.equals(journal.getDebitCoa())) {
			coaValue = journal.getDebitSignValue() * lossAdjBoxValue;
		}
		else {
			coaValue = journal.getCreditSignValue() * lossAdjBoxValue;
		}
		return coaValue ;
	}
	
	
	public static String getCurveYymm(String gocId, String newContRateDiv, MstRunset mstRunset) {
		if(newContRateDiv.equals("INIT")){
			return PrvdMst.getMstGoc(gocId).getInitCurveYymm();
		}
		else if(newContRateDiv.equals("PREV")){
			return stBssd;
		}
		else if(newContRateDiv.equals("CURR")){				
			return mstRunset.getCashFlowYymm(stBssd, bssd);			//DEFAULT!!!
		}
		return mstRunset.getCashFlowYymm(stBssd, bssd);
	}
	
	public static String getCurveYymm(String gocId, MstRunset mstRunset) {
		if(mstRunset.getIrCurveSlidingType().equals(ESlidingType.GOC_INIT)) {
			return PrvdMst.getMstGoc(gocId).getInitCurveYymm();
		}
		return mstRunset.getCashFlowYymm(stBssd, bssd);			//DEFAULT!!!
	}
}