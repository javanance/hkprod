package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.enums.EBoolean;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(EsgMetaId.class)
@Table(name ="EAS_ESG_META")
@Getter
@Setter
public class EsgMeta implements Serializable {

	private static final long serialVersionUID = -8105176349509184506L;

	@Id	private String groupId;
	@Id	private String paramKey;
	
	private String paramValue;
	@Enumerated(EnumType.STRING)
	private EBoolean useYn;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	public EsgMeta() {}

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
		return toString(",");
	}

	public String toString(String delimeter) {
		StringBuilder builder = new StringBuilder();
		builder.append(groupId).append(delimeter)
				.append(paramKey).append(delimeter)
				.append(paramValue).append(delimeter)
				.append(useYn)
				;

		return builder.toString();
	}	
}


