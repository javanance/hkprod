package com.gof.factory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.gof.entity.DfLv2WghtHis;
import com.gof.entity.DfLv2WghtRate;
import com.gof.infra.GmvConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FacDfWghtRate {
	
	public static Stream<DfLv2WghtHis> build(String bssd, String gocId, Map<Double, Double> wghtRateMap) {
		List<DfLv2WghtHis> rstList = new ArrayList<DfLv2WghtHis>();
		
		for(Map.Entry<Double, Double> entry : wghtRateMap.entrySet()) {
			rstList.add(DfLv2WghtHis.builder()
					.baseYymm(bssd)
					.gocId(gocId)
					.cfMonthNum(entry.getKey())
//					.initYymm(bssd)
					.wghtRate(entry.getValue())
//					.wghtFwdRate(.entry)
					.lastModifiedBy(GmvConstant.getLastModifier())
					.lastModifiedDate(LocalDateTime.now())
					.build()
					);
		}
		return rstList.stream();
	}

	public static DfLv2WghtRate convert(DfLv2WghtHis wghtHis) {
		return DfLv2WghtRate.builder()
				.gocId(wghtHis.getGocId())
				.cfMonthNum(wghtHis.getCfMonthNum())
//				.initYymm(bssd)
				.wghtRate(wghtHis.getWghtRate())
//				.wghtFwdRate(.entry)
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
	}
}
