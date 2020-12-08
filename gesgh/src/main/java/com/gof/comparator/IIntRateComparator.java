package com.gof.comparator;

import java.util.Comparator;

import com.gof.interfaces.IIntRate;

public class IIntRateComparator  implements Comparator<IIntRate>{

	@Override
	public int compare(IIntRate base, IIntRate other) {
		return  base.getMatCd().compareTo(other.getMatCd()) ;
	}
	
}
