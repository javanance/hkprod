package com.gof.comparator;

import java.util.Comparator;

import com.gof.entity.SmithWilsonResult;

public class SmithWilsonComparator  implements Comparator<SmithWilsonResult>{

	@Override
	public int compare(SmithWilsonResult base, SmithWilsonResult other) {
		return 100000* ( base.getMonthNum() - other.getMonthNum()) 
				 + ( other.getFwdMonthNum() - base.getFwdMonthNum())
				 ;  
	}
}
