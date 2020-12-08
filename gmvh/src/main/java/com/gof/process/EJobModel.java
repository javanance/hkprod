package com.gof.process;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.model.CurrSysRateUpdate;
import com.gof.model.MdlDfLv4Eir;
import com.gof.model.MdlLossStep;
import com.gof.model.MdlRstBox;
import com.gof.model.MdlRstCsm;
import com.gof.model.MdlRstDacSplit;
import com.gof.model.MdlRstLoss;
import com.gof.ncont.SetupGocCont;
import com.gof.ncont.SetupNcontCf;
import com.gof.ncont.SetupNcontRst;
import com.gof.rollfwd.MdlAcctBox;
import com.gof.rollfwd.MdlRollFwd;
import com.gof.rollfwd.MdlRollFwdLoss;
import com.gof.rollfwd.MdlRollFwdLossAlt;
import com.gof.setup.SetupCfLv1Goc;
import com.gof.setup.SetupCfLv1GocLic;
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
public enum EJobModel {
	
	  GMV01	("M01", 	true,	"Curr Rate", 					() ->  SetupDfCurrRate.create(),			"Delete DfLv1CurrRate a where a.baseYymm=?1")
	, GMV02	("M02", 	false,	"Curr Rate Ark Sync", 			() ->  SetupDfCurrRate.createArkSync(),		"Delete DfLv1CurrRate a where a.baseYymm=?1")
	, GMV09	("M09", 	false,	"Curr Rate Ark Sync", 			() ->  SetupDfCurrRate.append())
	  
	, GMV11	("N11", 	false,	"NewCont 61 st & EPV",	() ->  SetupNcontCf.createCurrent())
//	, GMV12	("N12", 	false,	"NewCont 61 st & EPV",	() ->  SetupNcontCf.createAll())
	, GMV13	("N13", 	false,	"NewCont Rst RA",				() ->  SetupNcontRst.createRa())
	, GMV14	("N14", 	false,	"NewCont Rst TVOG",				() ->  SetupNcontRst.createTvog())
	, GMV15	("N15", 	false,	"NewCont Rst EPV",				() ->  SetupNcontRst.createEpv())
	, GMV16	("N16", 	false,	"NewCont Rst Flat",				() ->  SetupNcontRst.createFlat())
	, GMV17	("N17", 	false,	"Map Cont Goc",					() ->  SetupGocCont.create())

	, GMV21	("M21", 	true,	"Real Cash", 					() ->  SetupCfLv3Cash.create(),			"Delete CfLv3Real a where a.baseYymm=?1")
    , GMV22	("M22", 	true,	"All CF ", 						() ->  SetupCfLv1Goc.createAll(), 		"Delete CfLv1Goc  a where a.baseYymm=?1")
    
    , GMV221	("M221", 	false,	"Goc Current CF", 			() ->  SetupCfLv1Goc.createCurrent())
    , GMV222	("M222", 	false,	"Goc Previous CF", 			() ->  SetupCfLv1Goc.createPrev())
    , GMV223	("M223", 	false,	"Goc Newcont CF", 			() ->  SetupCfLv1Goc.createNew())
    
	, GMV31	("M31", 	true,	"TVOG Lv1",						() ->  SetupTvogLv1.create(), 			"Delete TvogLv1 a where a.baseYymm=?1")
	, GMV32	("M32", 	true,	"RA Lv1",						() ->  SetupRaLv1.create(),				"Delete RaLv1   a where a.baseYymm=?1")
	, GMV321	("M321", 	false,	"RA Lv1 IBNR",				() ->  SetupRaLv1Ibnr.create())
	
	, GMV33	("M33", 	true,	"TVOG Lv2",						() ->  SetupTvogLv2Delta.create(),		"Delete TvogLv2Delta a where a.baseYymm=?1")
	, GMV34	("M34", 	true,	"RA Lv2",						() ->  SetupRaLv2Delta.create(),		"Delete RaLv2Delta   a where a.baseYymm=?1")
	, GMV35	("M35",		true,	"New Goc",						() ->  SetupRstEpvNgoc.create(), 		"Delete RstEpvNgoc   a where a.baseYymm=?1")
	
	, GMV36	("M36",		true,	"DAC Ratio",					() ->  SetupRatioDac.createFromRaw(), 	"Delete RatioDac a where a.baseYymm=?1")
//	, GMV37	("M37",		false,	"RA   Release if exists",		() ->  SetupRatioDac.create())			
//	, GMV38	("M38",		false,	"TVOG Release if exists",		() ->  SetupRatioDac.create())			
	
	, GMV39	("M39",		true,	"CSM Release Ratio",			() ->  SetupRatioCsm.createFromRaw(), 	"Delete RatioCovUnit a where a.baseYymm=?1")

	, GMV50	("M50",		true,	"NewGoc EIR ", 					() ->  SetupDfEir.createNewgoc(), 		"Delete DfLv2EirNewgoc a where a.baseYymm=?1")
	, GMV51	("M51", 	false,	"Init Rate",					() ->  SetupDfInitRate.createNew())
	
	, GMV53	("M53", 	true,	"Wghted Rate His",				() ->  SetupDfWghtRate.createNew(), 		"Delete DfLv2WghtHis a where a.baseYymm=?1")
	, GMV54	("M54", 	false,	"Wghted His for missing",		() ->  SetupDfWghtRate.createAll())
	
	, GMV57	("M57", 	false,	"Wghted Rate Determined",		() ->  SetupDfWghtRate.determineNew())
	, GMV58	("M58", 	false,	"Wghted Rate for missing",		() ->  SetupDfWghtRate.determineAll())
	, GMV59	("M59", 	true,	"Goc Int Flat",					() ->  SetupDfLv3Flat.create(), 		"Delete DfLv3Flat a where a.baseYymm=?1")		

	, GMV65	("M65", 	true,	"CLOSE STEP CF+DF",				() ->  SetupCfLv4Df.createAlone(), 		"Delete CfLv4Df   a where a.baseYymm=?1")
	, GMV66	("M66", 	false,	"DELTA STEP CF+DF",				() ->  SetupCfLv4Df.createDelta())
//	, GMV67	("M67", 	false,	"NEW CONTP CF+DF",				() ->  SetupCfLv4Df.createNew())
	
	, GMV71	("M71", 	true,	"Box CF Detail",				() ->  MdlRstBox.createDetailBeforeEirUpdate(), 	"Delete RstBoxDetail a where a.baseYymm=?1" )

	, GMV72	("M72", 	false,	"Box Ra",						() ->  MdlRstBox.createRa(), 			"Delete RstBoxGoc    a where a.baseYymm=?1"	)
	, GMV73	("M73", 	false,	"Box TVOG ",					() ->  MdlRstBox.createTvog())
	, GMV74 ("M74", 	false,	"Box Real Cash",				() ->  MdlRstBox.createRealCash())
//	, GMV74 ("M74", 	false,	"Box Real Cash",				() ->  MdlRealCash.create())
	
	, GMV76	("M76", 	false,	"EIR Update",					() ->  MdlDfLv4Eir.create(),			 "Delete DfLv4Eir    a where a.baseYymm=?1")
	, GMV761	("M761", 	false,	"EIR Update",				() ->  MdlDfLv4Eir.createAllEir())
	, GMV762	("M762", 	false,	"EIR Update",				() ->  MdlDfLv4Eir.createLrcEir())
	, GMV763	("M763", 	false,	"EIR Update",				() ->  MdlDfLv4Eir.createLicEir())
	
	, GMV77	("M77", 	false,	"DF Flat Update",				() ->  CurrSysRateUpdate.updateCurrEir())
	, GMV78	("M78", 	false,	"BoxDetail after eirUpdate",	() ->  MdlRstBox.createDetailAfterEirUpdate())
	, GMV79	("M79", 	false,	"Box CF GOC",					() ->  MdlRstBox.createFromDetail())
	
	, GMV81	("M81", 	true,	"Fulfill at Loss Step",			() ->  MdlLossStep.create(), 			"Delete RstLossStep a where a.baseYymm=?1"	)
	, GMV82	("M82", 	true,	"CSM RST",						() ->  MdlRstCsm.create(),				"Delete RstCsm      a where a.baseYymm=?1"	)
	, GMV83	("M83", 	true,	"Loss Allo Rst",				() ->  MdlRstLoss.create(),				"Delete RstLoss      a where a.baseYymm=?1"	)
	
	, GMV87	("M87",		true,	"DAC Calc",						() ->  MdlRstDacSplit.create(), 		"Delete RstDac      a where a.baseYymm=?1"	)

//	, GMV93	("M93", 	true,	"Roll Fwd ",					() ->  MdlRollFwd.createPrev(), 		"Delete RstRollFwd a where a.baseYymm=?1")
//	, GMV94	("M94", 	true,	"Roll Fwd ",					() ->  MdlRollFwd.create(), 			"Delete RstRollFwd a where a.baseYymm=?1")
	, GMV94	("M94", 	true,	"Roll Fwd ",					() ->  MdlRollFwd.create(), 		"Delete RstRollFwd a where a.baseYymm=?1")
	, GMV95	("M95", 	false,	"Roll Fwd",						() ->  MdlRollFwd.createCsm())
	, GMV96	("M96", 	false,	"Roll Fwd",						() ->  MdlRollFwd.createDac())

//	, GMV97	("M97", 	true,	"Roll Fwd",						() ->  MdlRollFwdLoss.create(), 		"Delete RstRollFwdLoss a where a.baseYymm=?1")
	, GMV97	("M97", 	true,	"Roll Fwd",						() ->  MdlRollFwdLossAlt.create(), 		"Delete RstRollFwdLoss a where a.baseYymm=?1")
	
	, GMV98	("M98", 	true,	"Journal Account",				() ->  MdlAcctBox.createFromRollFwd(), 	"Delete AcctBoxGoc     a where a.baseYymm=?1")
	, GMV99	("M99", 	false,	"Journal Account",				() ->  MdlAcctBox.createFromRollFwdLoss())
	
	;

	private String jobId;
	private boolean deleteThenInsert;
	
	private String desc;
	private Supplier<Stream<? extends Object>> supplier; 		   
	
	private String deleteQuery;
	
	private EJobModel(String jobId,  boolean isDeleteThenInsert, String desc, Supplier<Stream<? extends Object>> supplier ) {
		this.jobId = jobId;
		this.deleteThenInsert = isDeleteThenInsert;
		this.desc = desc;
		this.supplier = supplier;
	}
	
	private EJobModel(String jobId,  boolean isDeleteThenInsert, String desc, Supplier<Stream<? extends Object>> supplier, String deleteQuery ) {
		this.jobId = jobId;
		this.deleteThenInsert = isDeleteThenInsert;
		this.desc = desc;
		this.supplier = supplier;
		this.deleteQuery = deleteQuery;
	}
	
	
	public Supplier<Integer> getDeleter(Session session, List<String> params){
		log.info("Delete Param for {} :{}", this.getJobId(), params);
//		if(params.isEmpty()) {
//			return ()-> {
//							Query q = session.createQuery(this.getDeleteQuery());
//							return q.executeUpdate();
//			 			};
//		}
		return   ()-> {
						Query q = session.createQuery(this.getDeleteQuery());
						for(int i=0; i<params.size(); i++) {
							q.setParameter(i+1, params.get(i));
						}
						return q.executeUpdate();
					 };
	}			
}
