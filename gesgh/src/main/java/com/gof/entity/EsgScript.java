package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.enums.EBoolean;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table( name ="EAS_ESG_SCRIPT")
@Getter
@Setter
public class EsgScript implements Serializable{

	private static final long serialVersionUID = 5370330968175334208L;

	@Id	
	@Column(name ="SCRIPT_ID")	
	private String scriptId;
	
	@Column(name ="SCRIPT_NM")
	private String scriptNm;	
	
	@Column(name ="SCRIPT_TYP")
	private String scriptType;
	
	@Enumerated(EnumType.STRING)
	private EBoolean useYn;

	@Column(name="SCRIPT_CONTENT")
	private String scriptContent;	
	
	public EsgScript() {}


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
		return "EsgMstTmp [scriptId=" + scriptId + ", scriptNm=" + scriptNm + ", scriptContent=" + scriptContent + "]";
	}	

}
