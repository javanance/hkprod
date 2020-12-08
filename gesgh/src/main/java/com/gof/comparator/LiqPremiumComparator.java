package com.gof.comparator;

import java.util.Comparator;

import com.gof.entity.LiqPremium;

public class LiqPremiumComparator  implements Comparator<LiqPremium>{

	@Override
	public int compare(LiqPremium base, LiqPremium other) {
		return  base.getMatCd().compareTo(other.getMatCd()) ;
	}
	
}
