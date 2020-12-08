package com.gof.setup;

import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.dao.DfDao;
import com.gof.dao.RawDao;
import com.gof.entity.CfLv1Goc;
import com.gof.entity.DfLv1CurrRate;
import com.gof.entity.MstRunset;
import com.gof.enums.ECoa;
import com.gof.enums.ESlidingType;
import com.gof.factory.FacCf;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupCfLv1GocLic {
	private static	String bssd				=GmvConstant.BSSD;
	private static	String stBssd			=GmvConstant.ST_BSSD;
	private static	String irCurveId		=GmvConstant.IR_CURVE_ID;
	
	private static	List<Integer> groupTenorList = GmvConstant.GROUP_TENOR_LIST;
	private static Map<String, Map<Double, Double>> curveByYymmMap = new HashMap<String, Map<Double,Double>>();
	
	public static Stream<CfLv1Goc> createConversion() {
			bssd= stBssd;
			return createCurrent();
	}

	public static Stream<CfLv1Goc> createAll() {
		return createAll(null);
	}
	
	public static Stream<CfLv1Goc> createCurrent() {
		return createCurrent(null);
	}
	
	public static Stream<CfLv1Goc> createPrev() {
		return createPrev(null);
	}
	
	public static Stream<CfLv1Goc> createNew() {
		return createNew(null);
	}
	
	public static Stream<CfLv1Goc> createAll(String gocId) {
		return  PrvdMst.getMstRunsetList().stream()
						.filter(s->s.getCoaId().equals(ECoa.EPV))
						.flatMap(s-> create(gocId, s))
						;
	}

	public static Stream<CfLv1Goc> createCurrent(String gocId) {
		return  PrvdMst.getMstRunsetList().stream()
						.filter(s->s.getCoaId().equals(ECoa.EPV))
						.filter(s-> s.getSetlYmSlidingType().equals(ESlidingType.EOP))
						.filter(s->!s.getRunsetType().isNewContYn())
						.flatMap(s-> create(gocId, s))
						;			
	}
	
	public static Stream<CfLv1Goc> createPrev(String gocId) {
		return  PrvdMst.getMstRunsetList().stream()
						.filter(s->s.getCoaId().equals(ECoa.EPV))
						.filter(s-> s.getSetlYmSlidingType().equals(ESlidingType.BOP))
						.filter(s->!s.getRunsetType().isNewContYn())
						.flatMap(s-> create(gocId, s))
						;
		
	}
	
	public static Stream<CfLv1Goc> createNew(String gocId) {
		return  PrvdMst.getMstRunsetList().stream()
				.filter(s->s.getCoaId().equals(ECoa.EPV))
				.filter(s->s.getRunsetType().isNewContYn())
				.flatMap(s-> create(gocId, s))
				;
	}
	
	private static Stream<CfLv1Goc> create(String gocId,  MstRunset mstRunset) {

		String rsDivId = mstRunset.getRsDivId();
		
		String driveYm = mstRunset.getDriveYmSlidingType().equals(ESlidingType.EOP)?  bssd : stBssd	;
		
		String setlYm  = mstRunset.getSetlYmSlidingType().equals(ESlidingType.EOP) ?  bssd : mstRunset.getGenYymm(stBssd, bssd);
		String cfYymm  = mstRunset.getIrCurveSlidingType().equals(ESlidingType.EOP)?  bssd : mstRunset.getCashFlowYymm(stBssd, bssd) ;
		
		if(DateUtil.isGreaterOrEqual(bssd, setlYm)) {
			int adj = DateUtil.monthBetween(cfYymm, bssd);
			
//			log.info("CF LV1 : {},{},{},{},{}, {}", setlYm, driveYm, adj, rsDivId, mstRunset.getRunsetId(), gocId ); 
			
			return RawDao.getRawCfLicByRsDivStream(driveYm, setlYm, rsDivId, gocId)
					 	.map(cf-> FacCf.convertFromLic(bssd, cf, mstRunset, adj, getCurveMap(getCurveYymm(cf.getCsmGrpCd(),  mstRunset))))
					 	;
		}
		return Stream.empty();
	}
	
	
	private static String getCurveYymm(String gocId, MstRunset mstRunset) {
		if(mstRunset.getIrCurveSlidingType().equals(ESlidingType.GOC_INIT)) {
			return PrvdMst.getMstGoc(gocId).getInitCurveYymm();
		}
		return mstRunset.getCashFlowYymm(stBssd, bssd);			//DEFAULT!!!
	}
	
	private static Map<Double, Double> getCurveMap(String curveYymm){
		if(!curveByYymmMap.containsKey(curveYymm)) {
			curveByYymmMap.put(curveYymm,  DfDao.getDfLv1CurrRate(curveYymm, irCurveId).stream()
												.collect(toMap(DfLv1CurrRate::getCfMonthNum, DfLv1CurrRate::getAppliedRate))
							  );
		}
		return curveByYymmMap.get(curveYymm);
	}
}