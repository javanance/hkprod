package com.gof.enums;

import java.util.Arrays;
import java.util.List;

public enum EBoolean {
//,	G_TRUE	(0, false, true)
//,	G_FALSE	(0, false, true)
	Y	(1, true, true)
,   N	(1, true, false)	
,	YES	(2, true, true)
,   NO	(2, true, false)
,	ALL	(99, false, true, EBoolean.Y, EBoolean.YES, EBoolean.N, EBoolean.NO)
;
	private int order;
	private boolean isLeaf;
	private boolean trueFalse;
	private EBoolean[] details;
	
	private EBoolean(int order, boolean isLeaf, boolean trueFalse, EBoolean... details) {
		this.order = order;
		this.isLeaf = isLeaf;
		this.trueFalse = trueFalse;
//		this.group = Arrays.asList(group);
		this.details = details;
	}
	
	public int getOrder() {
		return order;
	}

	public boolean isLeaf() {
		return isLeaf;
	}
	public EBoolean[] getDetails() {
		return details;
	}
	
	public List<EBoolean> getDetailsList() {
		return Arrays.asList(details);
	}
	
	public boolean isTrueFalse() {
		return trueFalse;
	}
	
	public boolean contains(EBoolean obj) {
		return this.equals(obj) || Arrays.asList(details).contains(obj) ;
	}
	
}
