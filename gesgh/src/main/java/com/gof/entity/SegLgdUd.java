package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(SegLgdUdId.class)
@Table( name ="EAS_USER_SEG_LGD")
@Getter
@Setter
public class SegLgdUd implements Serializable, EntityIdentifier {

	private static final long serialVersionUID = -5982886374650308033L;

	@Id
	private String applStYymm;
	
	@Id
	private String lgdCalcTypCd;
	
	@Id
	private String segId;	
	
    private String applEdYymm;
	
	private Double lgd;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
    
	@Transient
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name ="SEG_ID", insertable=false, updatable=false)
//	@NotFound(action=NotFoundAction.IGNORE)
	private Seg seg;
    
	public SegLgdUd() {}

	

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
		return "SegLgdUd [applStYymm=" + applStYymm + ", lgdCalcTypCd=" + lgdCalcTypCd + ", segId=" + segId
				+ ", applEdYymm=" + applEdYymm + ", lgd=" + lgd + ", seg=" + seg + "]";
	}	

}


