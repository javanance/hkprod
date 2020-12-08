package com.gof.enums;

import java.util.function.Function;

import com.gof.entity.RstCsm;

public enum EAlloDiv {

	
    RATIO			(-1.0	)
  , LOSS_TVOM		(-1.0	)
  , LOSS_FACE		(-1.0	)
  , LOSS_RA			(-1.0	)
  , NA				(1.0	)
;

	private double signAdj;
	
	

	private EAlloDiv(double signAdj ) {
		this.signAdj = signAdj;
	}
	
}
