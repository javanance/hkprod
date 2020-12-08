package com.gof.comparator;

import java.util.Comparator;

import com.gof.entity.DcntSce;

public class DcntSceComparator  implements Comparator<DcntSce>{
	@Override
	public int compare(DcntSce base, DcntSce other) {
		return  10000* base.getBaseYymm().compareTo(other.getBaseYymm())
					+ 1000*base.getSceNo().compareTo(other.getSceNo())
						+ 100*base.getMatCd().compareTo(other.getMatCd())
							;
	}
	
}
