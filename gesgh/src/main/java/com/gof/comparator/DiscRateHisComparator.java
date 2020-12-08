package com.gof.comparator;

import java.util.Comparator;

import com.gof.entity.DiscRateHis;

public class DiscRateHisComparator  implements Comparator<DiscRateHis>{

	@Override
	public int compare(DiscRateHis base, DiscRateHis other) {
		return  10000* base.getBaseYymm().compareTo(other.getBaseYymm())
//						+ 100*base.getMatCd().compareTo(other.getMatCd())
							;
	}
	
}
