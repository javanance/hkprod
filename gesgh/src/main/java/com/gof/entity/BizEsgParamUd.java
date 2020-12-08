package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(BizEsgParamUdId.class)
@Table(name ="EAS_USER_ESG_PARAM")
@FilterDef(name="paramApplyEqBaseYymm", parameters= { @ParamDef(name="baseYymm", type="string") })
@Getter
@Setter
public class BizEsgParamUd implements Serializable, EntityIdentifier {
	private static final long serialVersionUID = 1524655691890282755L;

	@Id
	@Column(name="APPL_ST_YYMM", nullable=false)		
	private String applyStartYymm;
	
	
	@Id
	@Column(name="APPL_BIZ_DV", nullable=false)		
	private String applyBizDv;
	
	@Id
	private String irModelId;

	@Id
	private String paramTypCd;
	
	@Id
	private String matCd;
	
	@Column(name="APPL_ED_YYMM", nullable=false)		
	private String applyEndYymm;
	
	
	private Double applParamVal;

	private Double vol;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	@ManyToOne()
	@JoinColumn(name ="IR_MODEL_ID", insertable=false, updatable=false)
	private EsgMst esgMst ;
	
	public BizEsgParamUd() {}
	
	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	public BizEsgParam convertToBizEsgParam(String bssd) {
		BizEsgParam rst = new BizEsgParam();
		rst.setBaseYymm(bssd);
		rst.setApplyBizDv(this.applyBizDv);
		rst.setIrModelId(this.irModelId);
		rst.setParamTypCd(this.paramTypCd);
		rst.setMatCd(this.matCd);
		rst.setApplParamVal(this.applParamVal);
		rst.setVol(0.0);
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());
		
		return rst;
	}
}


