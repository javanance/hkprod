package com.gof.enums;

public enum ERunArgument {
	 	time ("TIME")
	  , btime("BTIME")
	  , ctime ("CTIME")
	  , vtime("VTIME")
	  , goc("GOC")
	  , properties ( "PROPERTIES")
	  , job("JOB")

	;
	
	
	private String alias;

	private ERunArgument(String alias) {
		this.alias = alias;
	}
	
	


	
}
