package com.gof.entity;

import java.time.LocalDateTime;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Getter
//@SuperBuilder
//@ToString(exclude = {"lastModifiedBy", "lastUpdateDate"})
@ToString
public class BaseEntity {
	
	protected String lastModifiedBy;
	protected LocalDateTime lastUpdateDate;
	
	@PrePersist
	protected void onPersist() {
		this.lastUpdateDate =LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		this.lastUpdateDate =LocalDateTime.now();
	}
}


