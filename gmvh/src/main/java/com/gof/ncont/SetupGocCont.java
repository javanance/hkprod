package com.gof.ncont;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao.MstDao;
import com.gof.dao.NcontDao;
import com.gof.entity.MstContGoc;
import com.gof.entity.MstGoc;
import com.gof.entity.MstProdGoc;
import com.gof.entity.NcontRstFlat;
import com.gof.enums.ECohortQurt;
import com.gof.enums.ECohortYear;
import com.gof.enums.EProfitDiv;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetupGocCont {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String gocGroup		=GmvConstant.GOC_GROUP;
	private static	String cohortType	=GmvConstant.COHORT_TYPE;
	private static List<MstGoc> gocList = new ArrayList<MstGoc>();
	
	public static Stream<MstContGoc> createConversion() {
		bssd= stBssd;
		return create();
	}
	
	public static Stream<MstContGoc> create() {
		
		String cohort 		= cohortType.equals("Q")? ECohortQurt.getECohrtQurt(bssd): ECohortYear.getECohortYear(bssd);
		
		Map<String, MstProdGoc> prodGocMap = MstDao.getMstProdGoc().stream().collect(toMap(MstProdGoc::getPrmctrProdCd, Function.identity()));
		
		List<String> contList = MstDao.getMstContGocStream().map(s->s.getCtrPolno()).collect(toList());
		
		return NcontDao.getNcontRstFlat(bssd).filter(s-> !contList.contains(s.getCtrPolno())).map(s-> build(s, cohort, prodGocMap));
	}
	
	private static String getGocId(String csmGroupCd, String cohort, String profitDiv) {
		String gocId 		="MST_NA";
		
		if(gocList.isEmpty()) {
			gocList = MstDao.getMstGoc().stream().filter(s->s.getUseYn().isTrueFalse()).filter(s->s.getGocGroup().equals(gocGroup)).collect(toList());
		}
		
		for(MstGoc aa : gocList) {
			if(profitDiv.equals(aa.getProfitCd()) 
					&& cohort.equals(aa.getCohort()) 
						&&  csmGroupCd.equals(aa.getCsmGroupCd())) {
				gocId = aa.getGocId();
				break;
			}
		}
		return gocId;
	}

	private static MstContGoc build(NcontRstFlat rst, String cohort, Map<String, MstProdGoc> prodGocMap) {
		if(!prodGocMap.containsKey(rst.getProdCd())) {
			log.error("Error in {}. There are no PROD_CD : {} in MST_PROD_GOC.", GmvConstant.JOB_NO, rst.getProdCd());
			System.exit(1);
		}
		
		MstProdGoc mstProdGoc = prodGocMap.get(rst.getProdCd());
		EProfitDiv profitDiv  = rst.getProfitDiv();
		String gocId 		  = getGocId(mstProdGoc.getCsmGroupCd(), cohort, profitDiv.name());
		
		return MstContGoc.builder()
						.ctrPolno(rst.getCtrPolno())
						.prodCd(mstProdGoc.getPrmctrProdCd())
						.ctrDt(rst.getBaseYymm())
						.initYymm(rst.getBaseYymm())
						.cohort(cohort)
						.portCd(mstProdGoc.getPortCd())
						.csmGroupCd(mstProdGoc.getCsmGroupCd())
						.raGroupId(mstProdGoc.getRaGroupId())
						.profitCd(profitDiv)
						.gocId(gocId)
						.remark(rst.getBaseYymm())
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build()
						;
	}
}