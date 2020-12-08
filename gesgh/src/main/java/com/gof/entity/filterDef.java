package com.gof.entity;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@FilterDef(name="eqBaseDate", parameters= @ParamDef(name ="bssd",  type="string"))
public class filterDef {

}
