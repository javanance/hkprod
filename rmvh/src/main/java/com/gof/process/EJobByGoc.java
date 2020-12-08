package com.gof.process;

import java.util.List;
import java.util.function.Function;
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
import com.gof.setup.SetupDfEir;
import com.gof.setup.SetupDfInitRate;
import com.gof.setup.SetupDfLv3Flat;
import com.gof.setup.SetupDfWgtRate;
import com.gof.setup.SetupElLv1;
import com.gof.setup.SetupElLv2Delta;
import com.gof.setup.SetupRaLv1;
import com.gof.setup.SetupRaLv2Delta;
import com.gof.setup.SetupRatioCsm;
import com.gof.setup.SetupRatioLv2;
import com.gof.setup.SetupRstEpvNgoc;
import com.gof.setup.SetupRstLossRcv;

import lombok.Getter;

@Getter
public enum EJobByGoc {
		
//	  GMV12	("G12", 	false,	"NewCont 61�쉶李� CF�젙由� & EPV",	gocId ->  SetupNcontCf.create(gocId))
//	, GMV13	("G13", 	false,	"NewCont Rst RA",			gocId ->  SetupNcontRstFlat.createRa(gocId))
//	, GMV14	("G14", 	false,	"NewCont Rst TVOG",			gocId ->  SetupNcontRstFlat.createTvog(gocId))
//	, GMV15	("G15", 	false,	"NewCont Rst EPV",			gocId ->  SetupNcontRstFlat.createEpv(gocId))
//	, GMV16	("G16", 	false,	"NewCont Rst Flat",			gocId ->  SetupNcontRstFlat.create(gocId))
//	, GMV17	("G17", 	false,	"Gap Cont Goc",				gocId ->  SetupGocCont.create(gocId))

  GMV21	("G21", 	true,	"Real Cash", 				gocId ->  SetupCfLv3Cash.create(gocId),			"Delete CfLv3Real a where a.baseYymm=?1 and a.gocId =?2")	
, GMV22	("G22", 	true,	"All CF ", 					gocId ->  SetupCfLv1Goc.createAll(gocId), 	 	"Delete CfLv1Goc  a where a.baseYymm=?1 and a.gocId =?2")

, GMV31	("G31", 	true,	"RA Lv1",					gocId ->  SetupRaLv1.create(gocId), 			"Delete RaLv1   a where a.baseYymm=?1 and a.gocId =?2"		)
, GMV32	("G32", 	true,	"EL Lv1",					gocId ->  SetupElLv1.create(gocId), 			"Delete ElLv1 a where a.baseYymm=?1 and a.gocId =?2"		)
//, GMV33	("G33", 	true,	"LossRec Lv1",				gocId ->  SetupLossRcvLv1.create(gocId),		"Delete LossRecLv1   a where a.baseYymm=?1 and a.gocId =?2")

, GMV36	("G36", 	true,	"RA Lv2",					gocId ->  SetupRaLv2Delta.create(gocId), 		"Delete RaLv2Delta   a where a.baseYymm=?1 and a.gocId =?2"	)
, GMV37	("G37", 	true,	"EL Lv2",					gocId ->  SetupElLv2Delta.create(gocId), 		"Delete ElLv2Delta a where a.baseYymm=?1 and a.gocId =?2"	)
//, GMV38	("G38",		true,	"LossRec Lv2",				gocId ->  SetupLossRcvLv2Delta.create(gocId), 	"Delete LossRecLv2Delta   a where a.baseYymm=?1 and a.gocId =?2")

, GMV39	("G39",		true,	"RstLossRcv",				gocId ->  SetupRstLossRcv.create(gocId), 		"Delete RstLossRcv   a where a.baseYymm=?1 and a.gocId=?2")

, GMV41	("G41",		true,	"New Goc",					gocId ->  SetupRstEpvNgoc.create(gocId), 		"Delete RstEpvNgoc a where a.baseYymm=?1 and a.gocId =?2"	)	
, GMV49	("G49",		true,	"CSM Release Ratio",		gocId ->  SetupRatioCsm.createFromRaw(gocId),	"Delete RatioCovUnit a where a.baseYymm=?1 and a.gocId =?2"	)		

, GMV50	("G50",		true,	"NewGoc EIR ", 				gocId ->  SetupDfEir.createNewgoc(gocId), 		"Delete DfLv2EirNewgoc a where a.baseYymm=?1 and a.gocId =?2")
, GMV51	("G51", 	false,	"Init Rate",				gocId ->  SetupDfInitRate.createNew(gocId))
, GMV53	("G53", 	true,	"Weighted Rate His",		gocId ->  SetupDfWgtRate.createNew(gocId), 		"Delete DfLv2WghtHis a where a.baseYymm=?1 and a.gocId =?2"	)

, GMV57	("G57", 	false,	"Wghted Rate Determined",	gocId ->  SetupDfWgtRate.determineNew(gocId))
, GMV59	("G59", 	true,	"Goc Int Flat",				gocId ->  SetupDfLv3Flat.create(gocId), 		"Delete DfLv3Flat a where a.baseYymm=?1 and a.gocId =?2")

, GMV65	("G65", 	false,	"CLOSE STEP CF+DF",			gocId ->  SetupCfLv4Df.createAlone(gocId), 		"Delete CfLv4Df   a where a.baseYymm=?1 and a.gocId =?2")
, GMV66	("G66", 	false,	"DELTA STEP CF+DF",			gocId ->  SetupCfLv4Df.createDelta(gocId))

, GMV69	("G69",		true,	"RatioLv2",					gocId ->  SetupRatioLv2.create(gocId), 			"Delete RatioLv2 a where a.baseYymm=?1 and a.gocId =?2")

, GMV71	("G71", 	true,	"Box CF Detail",			gocId ->  MdlRstBox.createBeforeEirUpdatDetail(gocId), 		"Delete RstBoxDetail a where a.baseYymm=?1 and a.gocId =?2"	)

, GMV72	("G72", 	true,	"Box Ra",					gocId ->  MdlRstBox.createRa(gocId), 			"Delete RstBoxGoc    a where a.baseYymm=?1 and a.gocId =?2"	)
, GMV73	("G73", 	false,	"Box EL ",					gocId ->  MdlRstBox.createEl(gocId))
, GMV74 ("G74", 	false,	"Box Real Cash",			gocId ->  MdlRstBox.createRealCash(gocId))
, GMV75 ("G75", 	false,	"Box Loss Recovery",		gocId ->  MdlRstBox.createLossRcv(gocId)) 		

, GMV76	("G76", 	true,	"EIR Update",				gocId ->  MdlDfLv4Eir.create(gocId), 			"Delete DfLv4Eir   a where a.baseYymm=?1 and a.gocId =?2")
, GMV77	("G77", 	false,	"CF LV4 EIR Update",		gocId ->  CurrSysRateUpdate.updateCurrEir(gocId))
, GMV78	("G78", 	true,	"Box Detail after Update",	gocId ->  MdlRstBox.createAfterEirUpdatDetail(gocId), "Delete RstBoxDetail a where a.baseYymm=?1 and a.gocId =?2 and a.lastModifiedBy like '%G78'")
, GMV79	("G79", 	true,	"Box CF",					gocId ->  MdlRstBox.createGoc(gocId), 				  "Delete RstBoxGoc a where a.baseYymm=?1 and a.gocId =?2 and a.lastModifiedBy like '%G79'")

, GMV82	("G82", 	true,	"CSM RST",					gocId ->  MdlRstCsmReins.create(gocId),			"Delete RstCsm      a where a.baseYymm=?1 and a.gocId =?2"	)

, GMV94	("G94", 	true,	"Roll Fwd ",				gocId ->  MdlRollFwd.create(gocId), 			"Delete RstRollFwd a where a.baseYymm=?1 and a.gocId =?2")
, GMV95	("G95", 	false,	"Roll Fwd",					gocId ->  MdlRollFwd.createCsm(gocId))
, GMV98	("G98", 	true,	"Journal Account",			gocId ->  MdlAcctBox.createFromRollFwd(gocId), 	"Delete AcctBoxGoc a where a.baseYymm=?1 and a.gocId =?2"	)
;

	private String jobId;
	private boolean deleteThenInsert;
	
	private String desc;
	private Function<String, Stream<? extends Object>> fn;
	
	private String deleteQuery;
	
	private EJobByGoc(String jobId,  boolean isDeleteThenInsert, String desc, Function<String, Stream<? extends Object>> fn ) {
		this.jobId = jobId;
		this.deleteThenInsert = isDeleteThenInsert;
		this.desc = desc;
		this.fn = fn;
	}
	
	private EJobByGoc(String jobId,  boolean isDeleteThenInsert, String desc, Function<String, Stream<? extends Object>> fn, String deleteQuery ) {
		this.jobId = jobId;
		this.deleteThenInsert = isDeleteThenInsert;
		this.desc = desc;
		this.fn = fn;
		this.deleteQuery = deleteQuery;
	}
		
	public String getJobName() {
		return jobId;
	}
	
	public Supplier<Integer> getDeleter(Session session, List<String> params){
		return   ()-> {
						if(this.getDeleteQuery()!=null) {
							Query q = session.createQuery(this.getDeleteQuery());
							for(int i=0; i<params.size(); i++) {
								q.setParameter(i+1, params.get(i));
							}
							return q.executeUpdate();
						}
						else {
							return 0;
						}
		};
	}			
}
