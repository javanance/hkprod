package com.gof.comparator;

import java.util.Comparator;

import com.gof.model.Tuple3;

public class TupleComparator  implements Comparator<Tuple3>{

	@Override
	public int compare(Tuple3 base, Tuple3 other) {
//		return base.getKey().compareTo(other.getKey()) ;
		return other.getKey().compareTo(base.getKey()) ;
	}
	
}
