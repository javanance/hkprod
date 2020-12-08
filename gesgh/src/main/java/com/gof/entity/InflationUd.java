package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name ="EAS_USER_INFLATION")
@Getter
@Setter
public class InflationUd implements Serializable, EntityIdentifier {

	private static final long serialVersionUID = 7932117728201576987L;

	@Id
	private String baseYymm;	
	
	private Double inflationIndex;
	private Double ifrsTgtIndex;
	private Double kicsTgtIndex;
	
	public InflationUd() {}

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return "InflationUd [baseYymm=" + baseYymm + ", inflationIndex=" + inflationIndex + "]";
	}
	
}


