package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class EsgMetaId implements Serializable {	

	private static final long serialVersionUID = 8896041712907223964L;

	@Column(name="GROUP_ID", nullable=false)
	private String groupId;
	
	@Column(name="PARAM_KEY", nullable=false)
	private String paramKey;
	
	public EsgMetaId() {}

	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
