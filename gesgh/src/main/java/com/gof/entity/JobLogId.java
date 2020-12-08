package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;

import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
public class JobLogId implements Serializable, EntityIdentifier {
	
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
