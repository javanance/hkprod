package com.gof.process;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.model.CurrSysRateUpdate;
import com.gof.model.MdlDfLv4Eir;
import com.gof.model.MdlRstBox;
import com.gof.model.MdlRstCsmReins;
import com.gof.rollfwd.MdlAcctBox;
import com.gof.rollfwd.MdlRollFwd;
import com.gof.setup.SetupCfLv1Goc;
import com.gof.setup.SetupCfLv3Cash;
import com.gof.setup.SetupCfLv4Df;
import com.gof.setup.SetupDfCurrRate;
import com.gof.setup.SetupDfEir;
import com.gof.setup.SetupDfInitRate;
import com.gof.setup.SetupDfLv3Flat;
import com.gof.setup.SetupDfWgtRate;
import com.gof.setup.SetupElLv1;
import com.gof.setup.SetupElLv2Delta;
import com.gof.setup.SetupRaLv1;
import com.gof.setup.SetupRaLv1Ibnr;
import com.gof.setup.SetupRaLv2Delta;
import com.gof.setup.SetupRatioCsm;
import com.gof.setup.SetupRatioLv2;
import com.gof.setup.SetupRstEpvNgoc;
import com.gof.setup.SetupRstLossRcv;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public enum EJobModel {
	
	  GMV01	("M01", 	true,	"Curr Rate", 					() ->  SetupDfCurrRate.create(),			"Delete DfLv1CurrRate a where a.baseYymm=?1")
	, GMV02	("M02", 	false,	"Curr Rate Ark Sync", 			() ->  SetupDfCurrRate.createArkSync(),		"Delete DfLv1CurrRate a where a.baseYymm=?1")
	, GMV09	("M09", 	false,	"Curr Rate Ark Sync", 			() ->  SetupDfCurrRate.append())
	  
//	, GMV11	("N11", 	false,	"NewCont 61 st & EPV",			() ->  SetupNcontCf.createCurrent())
//	, GMV12	("N12", 	false,	"NewCont 61 st & EPV",			() ->  SetupNcontCf.createAll())
//	, GMV13	("N13", 	false,	"NewCont Rst RA",				() ->  SetupNcontRst.createRa())
//	, GMV14	("N14", 	false,	"NewCont Rst TVOG",				() ->  SetupNcontRst.createTvog())
//	, GMV15	("N15", 	false,	"NewCont Rst EPV",				() ->  SetupNcontRst.createEpv())
//	, GMV16	("N16", 	false,	"NewCont Rst Flat",				() ->  SetupNcontRst.createFlat())
//	, GMV17	("N17", 	false,	"Map Cont Goc",					() ->  SetupGocCont.create())

	, GMV21	("M21", 	true,	"Real Cash", 					() ->  SetupCfLv3Cash.create(),			"Delete CfLv3Real a where a.baseYymm=?1")	
    , GMV22	("M22", 	true,	"All CF ", 						() ->  SetupCfLv1Goc.createAll(), 		"Delete CfLv1Goc  a where a.baseYymm=?1")
    
    , GMV31	("M31", 	true,	"RA Lv1",						() ->  SetupRaLv1.create(),				"Delete RaLv1   a where a.baseYymm=?1")
	, GMV311	("M311", 	false,	"RA Lv1 IBNR",				() ->  SetupRaLv1Ibnr.create())
	, GMV32	("M32", 	true,	"EL Lv1",						() ->  SetupElLv1.create(), 			"Delete ElLv1 a where a.baseYymm=?1")
//	, GMV33	("M33", 	true,	"LossRec Lv1",					() ->  SetupLossRcvLv1.create(),		"Delete LossRecLv1   a where a.baseYymm=?1")
	
	, GMV36	("M36", 	true,	"RA Lv2",						() ->  SetupRaLv2Delta.create(),		"Delete RaLv2Delta   a where a.baseYymm=?1")
	, GMV37	("M37", 	true,	"EL Lv2",						() ->  SetupElLv2Delta.create(),		"Delete ElLv2Delta a where a.baseYymm=?1")
//	, GMV38	("M38",		true,	"LossRec Lv2",					() ->  SetupLossRcvLv2Delta.create(), 	"Delete LossRecLv2Delta   a where a.baseYymm=?1")
	
	, GMV39	("M39",		true,	"RstLossRcv",					() ->  SetupRstLossRcv.create(), 		"Delete RstLossRcv   a where a.baseYymm=?1")
	
	, GMV41	("M41",		true,	"New Goc",						() ->  SetupRstEpvNgoc.create(), 		"Delete RstEpvNgoc   a where a.baseYymm=?1")
	, GMV49	("M49",		true,	"CSM Release Ratio",			() ->  SetupRatioCsm.createFromRaw(), 	"Delete RatioCovUnit a where a.baseYymm=?1")

	, GMV50	("M50",		true,	"NewGoc EIR ", 					() ->  SetupDfEir.createNewgoc(), 		"Delete DfLv2EirNewgoc a where a.baseYymm=?1")
	, GMV51	("M51", 	false,	"Init Rate",					() ->  SetupDfInitRate.createNew())
	
//	, GMV53	("M53", 	true,	"Wghted Rate His",				() ->  SetupDfWgtRate.createNew(), 		"Delete DfLv2WghtHis a where a.baseYymm=?1")
//	, GMV54	("M54", 	false,	"Wghted His for missing",		() ->  SetupDfWgtRate.createAll())
	, GMV54	("M54", 	true,	"Wghted His for allGoc",		() ->  SetupDfWgtRate.createAll()		,"Delete DfLv2WghtHis a where a.baseYymm=?1")
	
	, GMV57	("M57", 	false,	"Wghted Rate Determined",		() ->  SetupDfWgtRate.determineNew())
	, GMV58	("M58", 	false,	"Wghted Rate for missing",		() ->  SetupDfWgtRate.determineAll())
	, GMV59	("M59", 	true,	"Goc Int Flat",					() ->  SetupDfLv3Flat.create(), 		"Delete DfLv3Flat a where a.baseYymm=?1")		

	, GMV65	("M65", 	true,	"CLOSE STEP CF+DF",				() ->  SetupCfLv4Df.createAlone(), 		"Delete CfLv4Df   a where a.baseYymm=?1")
	, GMV66	("M66", 	false,	"DELTA STEP CF+DF",				() ->  SetupCfLv4Df.createDelta())
	
	, GMV69	("M69",		true,	"RatioLv2",						() ->  SetupRatioLv2.create(), 			"Delete RatioLv2 a where a.baseYymm=?1")
	
	, GMV71	("M71", 	true,	"Box CF Detail",				() ->  MdlRstBox.createBeforeEirUpdatDetail(), 		"Delete RstBoxDetail a where a.baseYymm=?1" )

	, GMV72	("M72", 	true,	"Box Ra",						() ->  MdlRstBox.createRa(), 			"Delete RstBoxGoc    a where a.baseYymm=?1"	)
	, GMV73	("M73", 	false,	"Box EL ",						() ->  MdlRstBox.createEl())
	, GMV74 ("M74", 	false,	"Box Real Cash",				() ->  MdlRstBox.createRealCash())
//	, GMV74 ("M74", 	false,	"Box Real Cash",				() ->  MdlRealCash.create())
	, GMV75 ("M75", 	false,	"Box Loss Recovery ",			() ->  MdlRstBox.createLossRcv())	
	
	, GMV76	("M76", 	false,	"EIR Update",					() ->  MdlDfLv4Eir.create(),			 "Delete DfLv4Eir    a where a.baseYymm=?1")
	, GMV77	("M77", 	false,	"DF Flat Update",				() ->  CurrSysRateUpdate.updateCurrEir())
	, GMV78	("M78", 	false,	"BoxDetail after eirUpdate",	() ->  MdlRstBox.createAfterEirUpdatDetail())
	, GMV79	("M79", 	false,	"Box CF GOC",					() ->  MdlRstBox.createGoc())

	, GMV82	("M82", 	true,	"CSM RST",						() ->  MdlRstCsmReins.create(),			"Delete RstCsm      a where a.baseYymm=?1"	)

	, GMV94	("M94", 	true,	"Roll Fwd EPV",					() ->  MdlRollFwd.create(), 			"Delete RstRollFwd a where a.baseYymm=?1")
	, GMV95	("M95", 	false,	"Roll Fwd Csm",					() ->  MdlRollFwd.createCsm())
	, GMV98	("M98", 	true,	"Journal Account",				() ->  MdlAcctBox.createFromRollFwd(), 	"Delete AcctBoxGoc     a where a.baseYymm=?1")
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
