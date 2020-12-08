package com.gof.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.gof.interfaces.EntityIdentifier;

import lombok.Getter;
import lombok.Setter;



//@Entity
//@IdClass(IrSceId.class)
//@Table(schema="QCM", name ="EAS_IR_SCE")
@Getter
@Setter
public class MvSce implements Serializable, EntityIdentifier {

	private static final long serialVersionUID = 4458482460359847563L;

//	@Id
	private String baseDate;
	
//    @Id
	private String modelId;

//    @Id
    private String sceNo;

//    @Id
	private String mvId;	
	
	private Double mvValue;
	
	private String lastModifiedBy;
	private LocalDateTime lastUpdateDate;
	
		
	public MvSce() {}

	
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
			   .append(modelId).append(delimeter)
			   .append(sceNo).append(delimeter)
			   .append(mvId).append(delimeter)
			   .append(mvValue).append(delimeter)
			   .append(lastModifiedBy).append(delimeter)
			   .append(lastUpdateDate);
		
		return builder.toString();
	}
}


