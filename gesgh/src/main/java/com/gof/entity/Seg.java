package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name ="EAS_SEG")
@Getter
@Setter
public class Seg implements Serializable, EntityIdentifier {

	private static final long serialVersionUID = 4501378275214851933L;

	@Id	
	@Column(name ="SEG_ID")	
	private String segId;
	
	private String segNm;
	
	private String rcTypCd;
	
	public Seg() {}


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
		return "Seg [segId=" + segId + ", segNm=" + segNm + ", rcTypCd=" + rcTypCd + "]";
	}	

}
