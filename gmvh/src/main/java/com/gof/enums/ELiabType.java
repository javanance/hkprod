package com.gof.enums;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum ELiabType {
	 
	 ALL    (1 , false) 
	,LRC	(2 , true, ELiabType.ALL)  
	,LIC	(3 , true, ELiabType.ALL)	 
	,OFF	(3 , true, ELiabType.ALL)	 
	;
	
	private int order;
	private boolean isLeaf;
	private List<ELiabType> group;
	
	private ELiabType(int order, boolean isLeaf, ELiabType... group) {
		this.order = order;
		this.isLeaf =isLeaf;
		this.group = Arrays.asList(group);
	}
	
	public boolean isContained(ELiabType obj){
		return getGroup().contains(obj) || this.equals(obj);
	}
}
