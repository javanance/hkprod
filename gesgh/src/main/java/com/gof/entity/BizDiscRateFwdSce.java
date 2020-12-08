package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


@Entity
@IdClass(BizDiscRateFwdSceId.class)
@Table(name ="EAS_BIZ_APLY_FWD_RATE_SCE")
@Getter
@Setter
public class BizDiscRateFwdSce implements Serializable {
	private static final long serialVersionUID = -4252300668894647002L;
	
	@Id
	private String baseYymm;
	@Id
	@Column(name="APPL_BIZ_DV", nullable=false)
	private String applyBizDv;
	@Id
	private String irCurveId;
	@Id
	private String sceNo;

	@Id
	private String matCd;

	@Id
	private Double avgMonNum;

	@Id
	private String fwdNo;
	
	private Double fwdRate;
	
	private Double avgFwdRate;
	
	private Double riskAdjFwdRate;
	
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
	
	public BizDiscRateFwdSce() {}


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
		
		builder.append(baseYymm).append(delimeter)
			   .append("I").append(delimeter)
			   .append(irCurveId).append(delimeter)
			   .append(sceNo).append(delimeter)
			   .append(matCd).append(delimeter)
			   .append(avgMonNum).append(delimeter)
			   .append(fwdNo).append(delimeter)
			   
			   .append(fwdRate).append(delimeter)
			   .append(avgFwdRate).append(delimeter)
			   .append(riskAdjFwdRate)
			   
			   ;
		
		return builder.toString();
	}
	
	public List<BizDiscRateSce> createSce(List<BizDiscRateStat> bizStatList){
		double baseRate =0.0;
		BizDiscRateSce temp;
		List<BizDiscRateSce> rstList = new ArrayList<BizDiscRateSce>();
		
		for(BizDiscRateStat stat : bizStatList) {
				baseRate = stat.getRegrConstant() + stat.getRegrCoef() * this.getAvgFwdRate();
				
				temp = new BizDiscRateSce();
				temp.setBaseYymm(this.getBaseYymm());
				temp.setApplBizDv(this.getApplyBizDv());
				temp.setIntRateCd(stat.getIntRateCd());
				temp.setSceNo(this.getSceNo());
				temp.setMatCd("M" + String.format("%04d", Integer.parseInt(this.getFwdNo())));
				
				temp.setBaseDiscRate(baseRate);
				temp.setAdjRate(stat.getAdjRate());
				temp.setDiscRate(baseRate * stat.getAdjRate());
				
				rstList.add(temp);
			}
		return rstList;
	}
}


