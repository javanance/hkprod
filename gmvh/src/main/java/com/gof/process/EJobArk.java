	package com.gof.process;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.ark.model.ArkSetupArkBox;
import com.gof.ark.model.ArkSetupCfLv1;
import com.gof.ark.model.ArkSetupFutureCf;
import com.gof.ark.model.ArkSetupFwdEpv;
import com.gof.ark.model.ArkSetupItemRst;
import com.gof.ark.model.ArkSetupLossStep;
import com.gof.ark.model.ArkSetupLossStepAlt;
import com.gof.ark.model.ArkSetupReleaseCf;
import com.gof.model.CurrSysRateUpdate;
import com.gof.model.MdlDfLv4Eir;
import com.gof.model.MdlLossStep;
import com.gof.model.MdlRstBox;
import com.gof.model.MdlRstCsm;
import com.gof.model.MdlRstDacSplit;
import com.gof.model.MdlRstLoss;
import com.gof.rollfwd.MdlAcctBox;
import com.gof.rollfwd.MdlRollFwd;
import com.gof.rollfwd.MdlRollFwdLossAlt;
import com.gof.setup.SetupCfLv1Goc;
import com.gof.setup.SetupCfLv3Cash;
import com.gof.setup.SetupCfLv4Df;
import com.gof.setup.SetupDfCurrRate;
import com.gof.setup.SetupDfEir;
import com.gof.setup.SetupDfInitRate;
import com.gof.setup.SetupDfLv3Flat;
import com.gof.setup.SetupDfWghtRate;
import com.gof.setup.SetupRaLv1;
import com.gof.setup.SetupRaLv1Ibnr;
import com.gof.setup.SetupRaLv2Delta;
import com.gof.setup.SetupRatioCsm;
import com.gof.setup.SetupRatioDac;
import com.gof.setup.SetupRstEpvNgoc;
import com.gof.setup.SetupTvogLv1;
import com.gof.setup.SetupTvogLv2Delta;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public enum EJobArk {
	
	  GMV01	("A01", 	true,	"Curr Rate", 					() ->  SetupDfCurrRate.create(),			"Delete DfLv1CurrRate a where a.baseYymm=?1")
	, GMV02	("A02", 	false,	"Curr Rate Ark Sync", 			() ->  SetupDfCurrRate.createArkSync(),		"Delete DfLv1CurrRate a where a.baseYymm=?1")
	, GMV09	("A09", 	false,	"Append UnObservables", 		() ->  SetupDfCurrRate.append())
	
	, GMV21	("A21", 	true,	"Real Cash", 					() ->  SetupCfLv3Cash.create(),			"Delete CfLv3Real a where a.baseYymm=?1")
	
	, GMV22	("A22", 	true,	"All CF ", 						() ->  SetupCfLv1Goc.createAll(), 		"Delete CfLv1Goc  a where a.baseYymm=?1")
	, GMV23	("A23", 	false,	"CF LV1",						() ->  ArkSetupCfLv1.createAll())

	, GMV24	("A24", 	true,	"ARK Forward EPV", 				() ->  ArkSetupFwdEpv.createAll(),		"Delete ArkFwdEpv a where a.baseYymm=?1")
	, GMV25	("A25", 	true,	"ARK CF Release", 				() ->  ArkSetupReleaseCf.create(),		"Delete ArkReleaseCf a where a.baseYymm=?1")
	, GMV26	("A26", 	true,	"ARK Future CF", 				() ->  ArkSetupFutureCf.createAll(),	"Delete ArkFutureCf a where a.baseYymm=?1")
	
	, GMV28	("A28", 	true,	"Ark Item Rst", 				() ->  ArkSetupItemRst.create(), 		"Delete ArkItemRst a where a.baseYymm=?1")
	, GMV29	("A29", 	true,	"Box Rst From Ark",				() ->  ArkSetupArkBox.create(), 	    "Delete ArkBoxRst a where a.baseYymm=?1")
	
	, GMV31	("A31", 	true,	"TVOG Lv1",						() ->  SetupTvogLv1.create(), 			"Delete TvogLv1 a where a.baseYymm=?1")
	, GMV32	("A32", 	true,	"RA Lv1",						() ->  SetupRaLv1.create(),				"Delete RaLv1   a where a.baseYymm=?1")
	, GMV321	("A321", 	false,	"RA Lv1 IBNR",				() ->  SetupRaLv1Ibnr.create())
	
	, GMV33	("A33", 	true,	"TVOG Lv2",						() ->  SetupTvogLv2Delta.create(),		"Delete TvogLv2Delta a where a.baseYymm=?1")
	, GMV34	("A34", 	true,	"RA Lv2",						() ->  SetupRaLv2Delta.create(),		"Delete RaLv2Delta   a where a.baseYymm=?1")
	
	, GMV35	("A35",		true,	"New Goc",						() ->  SetupRstEpvNgoc.create(), 		"Delete RstEpvNgoc   a where a.baseYymm=?1")
	
	, GMV36	("A36",		true,	"DAC Ratio",					() ->  SetupRatioDac.createFromRaw(),	"Delete RatioDac a where a.baseYymm=?1")
	, GMV39	("A39",		true,	"CSM Release Ratio",			() ->  SetupRatioCsm.createFromRaw(),	"Delete RatioCovUnit a where a.baseYymm=?1")
	
	, GMV50	("A50",		true,	"NewGoc EIR ", 					() ->  SetupDfEir.createNewgoc(), 		"Delete DfLv2EirNewgoc a where a.baseYymm=?1")
//	, GMV50	("A50",		true,	"NewGoc EIR ", 					() ->  ArkSetupDfEir.createNewgoc(), 	"Delete DfLv2EirNewgoc a where a.baseYymm=?1")
//	, GMV501	("A501",	true,	"NewGoc EIR ", 				() ->  ArkSetupDfEir.createDfLv4Conversion(), 	"Delete DfLv4Eir a where a.baseYymm=?1")
//	, GMV501	("A501",	true,	"NewGoc EIR ", 				() ->  MdlDfLv4Eir.createConversion(), 	"Delete DfLv4Eir a where a.baseYymm=?1")
	
	, GMV51	("A51", 	false,	"Init Rate",					() ->  SetupDfInitRate.createNew())
//	, GMV52	("A52", 	false,	"Init Rate All for missing",	() ->  SetupDfInitRate.createAll())
	
	, GMV53	("A53", 	true,	"Wghted Rate His",				() ->  SetupDfWghtRate.createNew(), 	"Delete DfLv2WghtHis a where a.baseYymm=?1")
//	, GMV54	("A54", 	false,	"Wghted His for missing",		() ->  SetupDfWghtRate.createAll())
	
	, GMV57	("A57", 	false,	"Wghted Rate Determined",		() ->  SetupDfWghtRate.determineNew())
//	, GMV58	("A58", 	false,	"Wghted Rate for missing",		() ->  SetupDfWghtRate.determineAll())
	
	, GMV59	("A59", 	true,	"Goc Int Flat",					() ->  SetupDfLv3Flat.create(), 		"Delete DfLv3Flat a where a.baseYymm=?1")		
	, GMV65	("A65", 	true,	"CLOSE STEP CF+DF",				() ->  SetupCfLv4Df.createAlone(), 		"Delete CfLv4Df   a where a.baseYymm=?1")
	, GMV66	("A66", 	false,	"DELTA STEP CF+DF",				() ->  SetupCfLv4Df.createDelta())		
	
	, GMV71	("A71", 	true,	"Box CF Detail",				() ->  MdlRstBox.createDetailBeforeEirUpdate(), "Delete RstBoxDetail a where a.baseYymm=?1" )
	, GMV72	("A72", 	true,	"Box Ra",						() ->  MdlRstBox.createRa(), 					"Delete RstBoxGoc    a where a.baseYymm=?1"	)
	, GMV73	("A73", 	false,	"Box TVOG ",					() ->  MdlRstBox.createTvog())
	
//	, GMV74	("A74", 	false,	"BoX Real Cash",				() ->  ArkMldBoxRealCash.create())
	, GMV74 ("A74", 	false,	"Box Real Cash",				() ->  MdlRstBox.createRealCash())	
	, GMV75	("A75", 	false,	"Box EPV",						() ->  ArkSetupArkBox.createRstBoxGoc())
	
	, GMV76	("A76", 	false,	"EIR Update",					() ->  MdlDfLv4Eir.create(),			 "Delete DfLv4Eir    a where a.baseYymm=?1")
	, GMV77	("A77", 	false,	"DF Flat Update",				() ->  CurrSysRateUpdate.updateCurrEir())
	, GMV78	("A78", 	false,	"BoxDetail after eirUpdate",	() ->  MdlRstBox.createDetailAfterEirUpdate())
	, GMV79	("A79", 	false,	"Box CF GOC",					() ->  MdlRstBox.createFromDetail())
	
	, GMV80	("A80", 	true,	"Fulfill at Loss Step",			() ->  ArkSetupLossStep.create(), 		"Delete RstLossStep a where a.baseYymm=?1"	)
	, GMV801	("A801", 	true,	"Fulfill at Loss Step",			() ->  ArkSetupLossStepAlt.create(), 		"Delete RstLossStep a where a.baseYymm=?1"	)
	
	, GMV81	("A81", 	true,	"Fulfill at Loss Step",			() ->  MdlLossStep.create(), 			"Delete RstLossStep a where a.baseYymm=?1"	)
	, GMV82	("A82", 	true,	"CSM Rst",						() ->  MdlRstCsm.create(),				"Delete RstCsm      a where a.baseYymm=?1"	)
	, GMV83	("A83", 	true,	"Loss Allo Rst",				() ->  MdlRstLoss.create(),				"Delete RstLoss      a where a.baseYymm=?1"	)
	
	, GMV87	("A87",		true,	"DAC Calc",						() ->  MdlRstDacSplit.create(), 		"Delete RstDac      a where a.baseYymm=?1"	)
	
//	, GMV93	("M93", 	true,	"Roll Fwd ",					() ->  MdlRollFwd.createPrev(), 		"Delete RstRollFwd a where a.baseYymm=?1")
	, GMV94	("A94", 	true,	"Roll Fwd ",					() ->  MdlRollFwd.create(), 			"Delete RstRollFwd a where a.baseYymm=?1")
	, GMV95	("A95", 	false,	"Roll Fwd",						() ->  MdlRollFwd.createCsm())
	, GMV96	("A96", 	false,	"Roll Fwd",						() ->  MdlRollFwd.createDac())

//	, GMV97	("A97", 	true,	"Roll Fwd",						() ->  MdlRollFwdLoss.create(), 		"Delete RstRollFwdLoss a where a.baseYymm=?1")
	, GMV97	("A97", 	true,	"Roll Fwd",						() ->  MdlRollFwdLossAlt.create(), 		"Delete RstRollFwdLoss a where a.baseYymm=?1")
	
	, GMV98	("A98", 	true,	"Journal Account",				() ->  MdlAcctBox.createFromRollFwd(), 	"Delete AcctBoxGoc     a where a.baseYymm=?1")
	, GMV99	("A99", 	false,	"Journal Account",				() ->  MdlAcctBox.createFromRollFwdLoss())
//	, GMV100("A100",	true,	"Curr  EIR ", 					() ->  ArkSetupDfEir.create(), 			"Delete DfLv2Eir       a where a.baseYymm=?1")
//	, GMV100("A100",	true,	"Curr  EIR ", 					() ->  ArkSetupDfEir.createDfLv4(), 	"Delete DfLv4Eir       a where a.baseYymm=?1")
	;

	private String jobId;
	private boolean deleteThenInsert;
	
	private String desc;
	private Supplier<Stream<? extends Object>> supplier; 		   
	
	private String deleteQuery;
	
	private EJobArk(String jobId,  boolean isDeleteThenInsert, String desc, Supplier<Stream<? extends Object>> supplier ) {
		this.jobId = jobId;
		this.deleteThenInsert = isDeleteThenInsert;
		this.desc = desc;
		this.supplier = supplier;
	}
	
	private EJobArk(String jobId,  boolean isDeleteThenInsert, String desc, Supplier<Stream<? extends Object>> supplier, String deleteQuery ) {
		this.jobId = jobId;
		this.deleteThenInsert = isDeleteThenInsert;
		this.desc = desc;
		this.supplier = supplier;
		this.deleteQuery = deleteQuery;
	}
	
	
	public Supplier<Integer> getDeleter(Session session, List<String> params){
		log.info("Delete Param for {} :{}", this.getJobId(), params);
	
		return   ()-> {
						Query q = session.createQuery(this.getDeleteQuery());
						for(int i=0; i<params.size(); i++) {
							q.setParameter(i+1, params.get(i));
						}
						return q.executeUpdate();
					 };
	}			
}
