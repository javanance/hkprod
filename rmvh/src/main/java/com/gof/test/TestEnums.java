package com.gof.test;

import com.gof.entity.DfLv2InitRate;
import com.gof.enums.ERollFwdType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestEnums {

	public static void main(String[] args) {
		for(ERollFwdType currRollfwd : ERollFwdType.values()) {
			log.info("roll fwd :  {},{}", currRollfwd, currRollfwd.getOrder());
		}
		log.info("roll aaaa :  {},{}", new DfLv2InitRate().getInitLicEir());
	}
}
