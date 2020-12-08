package com.gof.ark.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.gof.entity.MstRunset;
import com.gof.enums.EBoolean;
import com.gof.enums.ERunsetType;
import com.gof.enums.ESlidingType;
import com.gof.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
* MstRunset generated by hbm2java
*/
@Entity
@Table(name = "ARK_MST_RUNSET")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ArkMstRunset implements java.io.Serializable {
	private static final long serialVersionUID = -8151467682976876533L;

	@Id private String arkRunsetId;
	
	private int seq;
//	private String runsetId;
	
	@ManyToOne
	@JoinColumn(name = "RUNSET_ID")
	private MstRunset mstRunset;
	
	private String rsDivId;
	
	@Enumerated(EnumType.STRING)
	private ERunsetType runsetType;

	@Enumerated(EnumType.STRING)
	private EBoolean cfReleaseYn;
	
	@Enumerated(EnumType.STRING)
	private EBoolean tenorAdjYn;
	
	@Enumerated(EnumType.STRING)
	private EBoolean newContYn;

	@Enumerated(EnumType.STRING)
	private EBoolean eirAplyYn;
	
	@Enumerated(EnumType.STRING)
	private EBoolean futCfAplyYn;
	
	@Enumerated(EnumType.STRING)
	private ESlidingType driveYmSlidingType;
	
	@Enumerated(EnumType.STRING)
	private ESlidingType setlYmSlidingType;
	
	@Enumerated(EnumType.STRING)
	private ESlidingType irCurveSlidingType;
	
	private String intRateDiv;
	private String comments;
	private String lastModifiedBy;
	private LocalDateTime lastModifiedDate;
	
	@Override
	public boolean equals(Object arg0) {
		return super.equals(arg0);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
//	public int getTenorAdjNum() {
//		return  tenorAdjYn.isTrueFalse()? setlYmSlidingType.getSlidingNum() -1 :  setlYmSlidingType.getSlidingNum();
//	}
//	public int getTenorAdjNum() {
//		return  tenorAdjYn.isTrueFalse()? slidingNum -1 :  slidingNum ;
//	}
	
	
	public int getTenorAdjNum(String stBssd, String bssd) {
		int adj = DateUtil.monthBetween(stBssd, bssd)- setlYmSlidingType.getSlidingNum();
		
		switch (setlYmSlidingType) {
		case EOP :	
			return 0;
		case NA	: 
			return 0;
		default : 
			return  tenorAdjYn.isTrueFalse()? adj + 1 :  adj ;
		}
	}	
	
	public String getCashFlowYymm(String stBssd, String bssd) {
		return irCurveSlidingType.getKeyFn().apply(stBssd, bssd);
	}
	
	public String getSetlYm(String stBssd, String bssd) {
		return setlYmSlidingType.getKeyFn().apply(stBssd, bssd);
	}

	public String getDriveYm(String stBssd, String bssd) {
		return driveYmSlidingType.getKeyFn().apply(stBssd, bssd);
	}
	
	@Override
	public String toString() {
		return arkRunsetId;
	}
}