package com.gof.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.annotation.ToCsv;
import com.gof.enums.EBoolean;
import com.gof.interfaces.EntityIdentifier;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name ="EAS_ESG_MST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter(value = AccessLevel.PROTECTED)
@ToString(callSuper = false)
@EqualsAndHashCode(of= {"irModelId", "irModelNm", "irModelTyp"}, callSuper = false)
public class EsgMst extends BaseEntity implements Serializable, EntityIdentifier{
	private static final long serialVersionUID = -8151467682976876533L;
	@Id
	@ToCsv
	private String irModelId;
	
	@ToCsv
	private String irModelNm;
	
	@ToCsv
	private String irModelTyp;

	@Column(name ="PARAM_APPL_CD")
	@ToCsv
	private String paramApplCd;
	
	@Enumerated(EnumType.STRING)
	@ToCsv
	private EBoolean useYn;
}
