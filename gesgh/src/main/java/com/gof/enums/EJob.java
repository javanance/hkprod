package com.gof.enums;

public enum EJob {
//	  ESG1	("LIQ PREM")
//	, ESG2	("Liq Adj Rf Rate")
//	, ESG2A	("Biz BottomUp for ifrs")
	  ESG15	("LIQ PREM")
	, ESG16	("BIZ LIQ PREM")
	, ESG17	("Liq Adj Rf Rate")
	, ESG18	("Biz BottomUp for ifrs")
	, ESG19	("Biz BottomUp for ifrs")
	
	
//	, ESG3	("BizIrCurveHis")
	, ESG4	("Historical Vol")
	, ESG5	("Histocical Correlation")
	, ESG6	("Stock Parameter Creation")
	, ESG7	("Biz Stock Param")
	
	, ESG31	("Random Seed under Correlation")
	, ESG32	("Random Seed For HullWhite")

	, ESG33	("Std Asset Yield Scenario AVG")
	, ESG34	("Std Asset Yield Deterministic Scenario")
	, ESG35	("Std Asset Yield Scenario from irCurve_IFRS")
	, ESG36	("Std Asset Yield Scenario from irCurve_KICS")
	, ESG38	("HullWhite Bond Yield Scenario from irCurve_IFRS")
	, ESG39	("HullWhite Bond Yield Scenario from irCurve_KICS")
	
	
//	, ESG8	("Random Seed under Correlation")
//	, ESG9	("Std Asset Yield Scenario")
//	, ESG9A	("Std Asset Yield Scenario AVG")
//	, ESG9B	("Std Asset Yield Scenario")
	
	
	,ESG11	("EsgParameter")
	,ESG12	("EsgParameter Asyc Mode")
	,ESG13	("BizEsgParameter")
	
//	,ESG14	("BizIrCurveHis & BottomDcnt Sec")
//	,ESG15	("HullWhiteScenario")
//	,ESG18	("DNS Shock Scenario")
	,ESG41	("BizIrCurveHis & BottomDcnt Sec")
	,ESG42	("BizIrCurveHis & BottomDcnt Sec")
	,ESG43	("HullWhiteScenario")
	,ESG48	("DNS Shock Scenario")
	
	
	
	,ESG20	("LiquidityPremium")
//	,ESG21	("BizLiquidityPremium")

//	,ESG22	("BottomUp")
//	,ESG23	("BizBottomUpCurve")
	,ESG24	("BizBottomUpScenario")
	,ESG25	("BizBottomUpScenario_JAVA")
//	,ESG24	("BizBottomUpScenario IFRS")
//	,ESG24	("BizBottomUpScenario KICS")
	
	,ESG26	("KicsDiscountCurve")
	,ESG27	("KicsBizDiscountCurve")
	,ESG28	("KicsScenario")
	,ESG29	("AFNS Shock Scenario")
	
//	,ESG31	("DiscRateStatIFRS")
//	,ESG32	("BizDiscRateStat")
//	,ESG33	("DiscRateFwdGen")
//	,ESG34	("BizDiscRate")
//	,ESG35	("BizDiscRateSce")
	
//	,ESG36	("DiscRateStatKics")
//	,ESG37	("BizDiscRateStatKics ")
//	,ESG38	("DiscRateFwdGenKics")
//	,ESG39	("BizDiscRateKics")
//	,ESG40	("BizDiscRateSceKics")
	
	,ESG51	("DiscRateStatIFRS")
	,ESG52	("BizDiscRateStat")
	,ESG53	("DiscRateFwdGen")
	,ESG54	("BizDiscRate")
	,ESG55	("BizDiscRateSce")
	,ESG56	("BizDiscRateSce")
	
	,ESG61	("DiscRateStatKics")
	,ESG62	("BizDiscRateStatKics ")
	,ESG63	("DiscRateFwdGenKics")
	,ESG64	("BizDiscRateKics")
	,ESG65	("BizDiscRateSceKics")
	,ESG66	("BizDiscRateSceKics")
	
	,ESG71	("CpiInflation")
	,ESG72	("BizInflationIfrs")
	,ESG73	("BizInflationKics")

	,ESG81	("TM")
	,ESG82	("IFRSCorpPd")
	,ESG83	("KICSCorpPd")
	
	,ESG86	("LGD")
	,ESG87	("IFRSLGD")
	,ESG88	("KICSLGD")
	;
	
	private String jobName;

	private EJob(String jobName) {
		this.jobName = jobName;
	}

	public String getJobName() {
		return jobName;
	}
	
}
