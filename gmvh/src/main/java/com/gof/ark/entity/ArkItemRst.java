package com.gof.ark.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.gof.enums.EBoolean;
import com.gof.enums.EContStatus;
import com.gof.enums.ELiabType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@IdClass(ArkItemRstId.class)
@Table(name = "ARK_ITEM_RST")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArkItemRst {

	@Id	private String baseYymm;
	@Id	private String gocId;
	
	@Enumerated(EnumType.STRING)
	@Id	private ELiabType liabType;

	@Enumerated(EnumType.STRING)
	@Id	private EContStatus stStatus;
	
	@Enumerated(EnumType.STRING)
	@Id	private EContStatus endStatus;
	
	@Enumerated(EnumType.STRING)
	@Id	private EBoolean newContYn;
	
	@Id	private String arkRunsetId;
		private String runsetId;
	
	@Id	private String itemId;
	
	private Double itemAmt;
	
	private String coaId;
	private Integer seqNum;
	
	private String remark;
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
	
	@Override
	public String toString() {
		StringBuilder 	builder = new StringBuilder();
	 	 return builder
	 		.append(baseYymm).append(",")
	 		.append(gocId).append(",")
	 	 	.append(liabType).append(",")
	 	 	.append(stStatus).append(",")
	 	 	.append(endStatus).append(",")
	 	 	.append(newContYn).append(",")
	 	 	.append(arkRunsetId).append(",")
	 	 	.append(itemId).append(",")
	 	 	.append(itemAmt).append(",")
	 	 	.toString();
	}
	
	public String getMapKey() {
		return arkRunsetId+itemId;
	}
}
