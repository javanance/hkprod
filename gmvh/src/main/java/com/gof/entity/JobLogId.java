package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Getter;

@Embeddable
@Getter
public class JobLogId implements Serializable  {
	
	private static final long serialVersionUID = 205371050298889931L;
	
	private String jobId;
	private String calcStart;

	@Override
	public boolean equals(Object arg0) {

		return super.equals(arg0);
	}

	@Override
	public int hashCode() {

		return super.hashCode();
	}

			
}
