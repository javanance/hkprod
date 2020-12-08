package com.gof.setup;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import com.gof.dao.CfDao;
import com.gof.entity.CfLv1Goc;
import com.gof.entity.CfLv2Delta;
import com.gof.factory.FacDeltaCf;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;

import lombok.extern.slf4j.Slf4j;

//TODO :deprecated!!!!   Delete 
@Slf4j
public class SetupCfLv2Delta {
	private static	String bssd			=GmvConstant.BSSD;
	private static  String gocGroup		=GmvConstant.GOC_GROUP;

	public static Stream<CfLv2Delta> create(String gocId){
		return build(gocId).stream();
	}

	public static Stream<CfLv2Delta> create(){
		List<CfLv2Delta> rstList = new ArrayList<CfLv2Delta>();
			
		for(String gocId: PrvdMst.getGocIdList()) {
			rstList.addAll(build(gocId));
		}
		return rstList.stream();
	}
	
	private static List<CfLv2Delta> build(String gocId){
		List<CfLv2Delta> rstList = new ArrayList<CfLv2Delta>();
		
		Map<String, List<CfLv1Goc>> cfMap = CfDao.getCfLv1GocStream(bssd, gocId)
											.filter(s->s.isFutureCf(0.0))
											.collect(groupingBy(s->s.getDeltaCashFlowPk(), TreeMap::new, toList()));
		
		for(Map.Entry<String, List<CfLv1Goc>> entry : cfMap.entrySet()) {
			if(!entry.getValue().isEmpty()) {
				rstList.addAll(FacDeltaCf.createFromMap(entry.getValue() ));
			}	
		}
		return rstList;
	}
}