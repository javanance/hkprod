package com.gof.comparator;

import java.util.Comparator;

import com.gof.entity.IrCurveHis;

public class IrCurveHisComparator  implements Comparator<IrCurveHis>{

	@Override
	public int compare(IrCurveHis base, IrCurveHis other) {
		return  10000* base.getBaseDate().compareTo(other.getBaseDate()) 
					+ 100*base.getMatCd().compareTo(other.getMatCd())
						+ ( other.getForwardNum() - base.getForwardNum());
	}
	
}
