package com.gof.enums;

public enum EAdvJob {
	  ESG1	(1, "ALL", "LIQ PREM")
	, ESG2	(2, "ALL", "Liq Adj Rf Rate")
	, ESG2A	(2, "I", "Biz BottomUp for ifrs")
	
	
	, ESG3	(3, "ALL", "BizIrCurveHis")
	, ESG4	(4, "ALL", "Historical Vol")
	, ESG5	(5, "ALL", "Histocical Correlation")
	, ESG6	(6, "ALL", "Stock Parameter Creation")
	, ESG7	(7, "ALL", "Biz Stock Param")
	
	, ESG8	(8, "ALL", "Random Seed under Correlation")
	, ESG9	(9, "ALL", "Std Asset Yield Scenario")
	, ESG9A	(9, "ALL", "Std Asset Yield Scenario AVG")
	, ESG9B	(9, "ALL", "Std Asset Yield Scenario")
	
	
	,ESG11	(11, "ALL", "EsgParameter")
	,ESG13	(13, "ALL", "BizEsgParameter")
	,ESG14	(14, "ALL", "BizIrCurveHis")
	,ESG15	(15, "ALL", "HullWhiteScenario")
	
	,ESG18	(18, "ALL", "DNS Shock Scenario")
	
	,ESG20	(20, "ALL", "LiquidityPremium")
	,ESG21	(21, "ALL", "BizLiquidityPremium")

	,ESG22	(22, "ALL", "BottomUp")
	,ESG23	(23, "ALL", "BizBottomUpCurve")
	,ESG24	(24, "ALL", "BizBottomUpScenario")
	,ESG24A	(24, "ALL", "BizBottomUpScenario")
	
	,ESG26	(26, "ALL", "KicsDiscountCurve")
	,ESG27	(27, "ALL", "KicsBizDiscountCurve")
	,ESG28	(28, "ALL", "KicsScenario")
	
	,ESG31	(31, "ALL", "DiscRateStatIFRS")
	,ESG32	(32, "ALL", "BizDiscRateStat")
	,ESG33	(33, "ALL", "DiscRateFwdGen")
	,ESG34	(34, "ALL", "BizDiscRate")
	,ESG35	(35, "ALL", "BizDiscRateSce")
	
	,ESG36	(36, "ALL", "DiscRateStatKics")
	,ESG37	(37, "ALL", "BizDiscRateStatKics ")
	,ESG38	(38, "ALL", "DiscRateFwdGenKics")
	,ESG39	(39, "ALL", "BizDiscRateKics")
	,ESG40	(40, "ALL", "BizDiscRateSceKics")
	
	,ESG41	(41, "ALL", "CpiInflation")
	,ESG42	(42, "ALL", "BizInflationIfrs")
	,ESG43	(43, "ALL", "BizInflationKics")

	,ESG51	(51, "ALL", "TM")
	,ESG52	(52, "ALL", "IFRSCorpPd")
	,ESG53	(53, "ALL", "KICSCorpPd")
	
	,ESG61	(61, "ALL", "LGD")
	,ESG62	(62, "ALL", "IFRSLGD")
	,ESG63	(63, "ALL", "KICSLGD")
	;
	
	private int jobNo;
	private String jobName;
	private String bizDv;

	private EAdvJob(int jobNo, String bizDv, String jobName) {
		this.jobNo = jobNo;
		this.bizDv = bizDv;
		this.jobName = jobName;
	}
	

	public int getJobNo() {
		return jobNo;
	}
	
	public String getBizDv() {
		return bizDv;
	}

	public String getJobName() {
		return jobName;
	}
	
}
