package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;

@Entity
@IdClass(IrSceId.class)
@Table(name ="EAS_IR_SHOCK_SCE")
@Getter
@Setter
public class IrShockSce implements Serializable, EntityIdentifier {

	private static final long serialVersionUID = 4458482460359847563L;

	@Id
	private String baseDate;
	
    @Id
	private String irModelId;

    @Id
	private String matCd;

    @Id
	private String sceNo;

    @Id
	private String irCurveId;	
	
	private Double rfRate;
	
	private Double riskAdjIr;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name ="IR_MODEL_ID", insertable=false, updatable=false)
//	@NotFound(action=NotFoundAction.IGNORE)
//	private EsgMst esgMst;	
//
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name ="IR_CURVE_ID", insertable=false, updatable=false)
//	@NotFound(action=NotFoundAction.IGNORE)
//	private IrCurve irCurve;	
	
	public IrShockSce() {}

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
		
		builder.append(baseDate).append(delimeter)
			   .append(irModelId).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(sceNo).append(delimeter)
			   .append(irCurveId).append(delimeter)
			   .append(rfRate).append(delimeter)
			   .append(riskAdjIr).append(delimeter)
			   .append(lastModifiedBy).append(delimeter)
			   .append(lastUpdateDate);
		
		return builder.toString();
	}
}


