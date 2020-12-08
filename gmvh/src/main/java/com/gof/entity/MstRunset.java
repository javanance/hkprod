package com.gof.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.gof.enums.EBoolean;
import com.gof.enums.ECoa;
import com.gof.enums.ELiabType;
import com.gof.enums.ERunsetType;
import com.gof.enums.ESlidingType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * MstRunset generated by hbm2java
 */
@Entity
@Table(name = "MST_RUNSET")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MstRunset implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;

	@Id private String runsetId;
	
	@Enumerated(EnumType.STRING)
	private ECoa coaId;
	
	@Enumerated(EnumType.STRING)
	private ELiabType liabType;
	
	private int seq;
	
	private String deltaGroup;				//TODO ?? convert to Enum
	private String priorDeltaGroup;
	
	@Enumerated(EnumType.STRING)
	private ERunsetType runsetType;
//	private ERsDivType rsDivType;
	
	private String rsDivId;
	
	
	@Enumerated(EnumType.STRING)
	private EBoolean cfReleaseYn;
	
	@Enumerated(EnumType.STRING)
	private EBoolean eirAplyYn;

	@Enumerated(EnumType.STRING)
	private EBoolean beforeEirUpdateYn;
	
	@Enumerated(EnumType.STRING)
	private EBoolean tenorAdjYn;
	
	@Enumerated(EnumType.STRING)
	private ESlidingType driveYmSlidingType;
	
	@Enumerated(EnumType.STRING)
	private ESlidingType setlYmSlidingType;
	
	@Enumerated(EnumType.STRING)
	private ESlidingType irCurveSlidingType;
	

	private String comments;
	private String lastModifiedBy;
	private LocalDateTime lastModifiedDate;
	
//	public EBoolean getPrevYn() {
//		return rsDivType.getPrevYn();
//	}

	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	public int getTenorAdjNum() {
		return  tenorAdjYn.isTrueFalse()? setlYmSlidingType.getSlidingNum() -1 :  setlYmSlidingType.getSlidingNum() ;
	}
	
//	public String getCurveYymm(String stBssd, String newContRateDiv) {
//		if(newContRateDiv.equals("CURR")) {
//			return getCashFlowYymm(stBssd);
//		}
//		return stBssd;
//	}
	
	public String getCashFlowYymm(String stBssd, String bssd) {
		return irCurveSlidingType.getKeyFn().apply(stBssd, bssd);
	}
	
	public String getGenYymm(String stBssd, String bssd) {
		return setlYmSlidingType.getKeyFn().apply(stBssd, bssd);
	}
	
//	public String getCashFlowYymm(String stBssd) {
////		return DateUtil.addMonthToString(stBssd, slidingNum);
////		int adj = tenorAdjYn.isTrueFalse()? slidingNum -1 :  slidingNum ;
//		int adj = getTenorAdjNum();
//		return  DateUtil.addMonthToString(stBssd, adj );
//	}
	
//	public String getGenYymm(String stBssd) {
//		return DateUtil.addMonthToString(stBssd, setlYmSlidingType.getSlidingNum());
//	}
	
	public String getRsDivTypeName() {
		return runsetType.name();
	}
	
	@Override
	public String toString() {
		return runsetId;
	}
}