package com.gof.comparator;

import java.util.Comparator;

import com.gof.entity.TransitionMatrixUd;

public class TmComparator  implements Comparator<TransitionMatrixUd>{

	@Override
	public int compare(TransitionMatrixUd base, TransitionMatrixUd other) {
		return 100* ( base.getFromGradeEnum().getOrder() - other.getFromGradeEnum().getOrder()) 
				 + ( base.getToGradeEnum().getOrder()  - other.getToGradeEnum().getOrder())
				 ;  
	}
}
