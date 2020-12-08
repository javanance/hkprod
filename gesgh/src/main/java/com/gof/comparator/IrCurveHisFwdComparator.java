package com.gof.comparator;

import java.util.Comparator;

import com.gof.entity.IrCurveHis;

public class IrCurveHisFwdComparator  implements Comparator<IrCurveHis>{

	@Override
	public int compare(IrCurveHis base, IrCurveHis other) {
		return 100000* ( base.getMatNum() - other.getMatNum()) 
				 + ( other.getForwardNum() - base.getForwardNum())
				 ;  
	}
}
