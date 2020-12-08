package com.gof.process;

import java.util.List;
import java.util.function.Function;
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
import com.gof.rollfwd.MdlAcctBox;
import com.gof.rollfwd.MdlRollFwd;
import com.gof.rollfwd.MdlRollFwdLoss;
import com.gof.setup.SetupCfLv1Goc;
import com.gof.setup.SetupCfLv1GocLic;
import com.gof.setup.SetupCfLv3Cash;
import com.gof.setup.SetupCfLv4Df;
import com.gof.setup.SetupDfEir;
import com.gof.setup.SetupDfInitRate;
import com.gof.setup.SetupDfLv3Flat;
import com.gof.setup.SetupDfWghtRate;
import com.gof.setup.SetupRaLv1;
import com.gof.setup.SetupRaLv2Delta;
import com.gof.setup.SetupRatioCsm;
import com.gof.setup.SetupRatioDac;
import com.gof.setup.SetupRstEpvNgoc;
import com.gof.setup.SetupTvogLv1;
import com.gof.setup.SetupTvogLv2Delta;

import lombok.Getter;

@Getter
public enum EJobByGoc {
		
//	  GMV12	("G12", 	false,	"NewCont 61�쉶李� CF�젙由� & EPV",	gocId ->  SetupNcontCf.create(gocId)	)
//	, GMV13	("G13", 	false,	"NewCont Rst RA",			gocId ->  SetupNcontRstFlat.createRa(gocId)	)
//	, GMV14	("G14", 	false,	"NewCont Rst TVOG",			gocId ->  SetupNcontRstFlat.createTvog(gocId)	)
//	, GMV15	("G15", 	false,	"NewCont Rst EPV",			gocId ->  SetupNcontRstFlat.createEpv(gocId)	)
//	, GMV16	("G16", 	false,	"NewCont Rst Flat",			gocId ->  SetupNcontRstFlat.create(gocId)	)
//	, GMV17	("G17", 	false,	"Gap Cont Goc",				gocId ->  SetupGocCont.create(gocId)	)

	  GMV21	("G21", 	true,	"Real Cash", 				gocId ->  SetupCfLv3Cash.create(gocId),			"Delete CfLv3Real a where a.baseYymm=?1 and a.gocId =?2")	
    , GMV22	("G22", 	true,	"All CF ", 					gocId ->  SetupCfLv1Goc.createAll(gocId), 	 	"Delete CfLv1Goc  a where a.baseYymm=?1 and a.gocId =?2")
    , GMV221	("G221", 	false,	"Goc Current CF", 			gocId ->  SetupCfLv1Goc.createCurrent(gocId))
    , GMV222	("G222", 	false,	"Goc Previous CF", 			gocId ->  SetupCfLv1Goc.createPrev(gocId))
    , GMV223	("G223", 	false,	"Goc Newcont CF", 			gocId ->  SetupCfLv1Goc.createNew(gocId))
    
//    , GMV23	("G23", 	false,	"All CF ", 					gocId ->  SetupCfLv1GocLic.createAll(gocId))
//    , GMV231	("G231", 	false,	"Goc Current CF", 			gocId ->  SetupCfLv1GocLic.createCurrent(gocId))
//    , GMV232	("G232", 	false,	"Goc Previous CF", 			gocId ->  SetupCfLv1GocLic.createPrev(gocId))
//    , GMV233	("G233", 	false,	"Goc Newcont CF", 			gocId ->  SetupCfLv1GocLic.createNew(gocId))
	
//	, GMV29	("G29", 	true,	"Delta CF ", 				gocId ->  SetupCfLv2Delta.create(gocId), 		"Delete CfLv2Delta a where a.baseYymm=?1 and a.gocId =?2")			//Don't need for CfLv4!!!

	, GMV31	("G31", 	true,	"TVOG Lv1",					gocId ->  SetupTvogLv1.create(gocId), 			"Delete TvogLv1 a where a.baseYymm=?1 and a.gocId =?2"		)
	, GMV32	("G32", 	true,	"RA Lv1",					gocId ->  SetupRaLv1.create(gocId), 			"Delete RaLv1   a where a.baseYymm=?1 and a.gocId =?2"		)
	, GMV33	("G33", 	true,	"TVOG Lv2",					gocId ->  SetupTvogLv2Delta.create(gocId), 		"Delete TvogLv2Delta a where a.baseYymm=?1 and a.gocId =?2"	)
	, GMV34	("G34", 	true,	"RA Lv2",					gocId ->  SetupRaLv2Delta.create(gocId), 		"Delete RaLv2Delta   a where a.baseYymm=?1 and a.gocId =?2"	)
	
	, GMV35	("G35",		true,	"New Goc",					gocId ->  SetupRstEpvNgoc.create(gocId), 		"Delete RstEpvNgoc a where a.baseYymm=?1 and a.gocId =?2"	)	
	
	
	, GMV36	("G36",		true,	"DAC Ratio",				gocId ->  SetupRatioDac.createFromRaw(gocId),	"Delete RatioDac a where a.baseYymm=?1 and a.gocId =?2"	)
//	, GMV37	("G37",		false,	"RA   Release if exists",	gocId ->  SetupRatioDac.create(gocId)		)		//TODO : logic adddddd!!!!
//	, GMV38	("G38",		false,	"TVOG Release if exists",	gocId ->  SetupRatioDac.create(gocId)		)		//TODO : logic adddddd!!!!
	, GMV39	("G39",		true,	"CSM Release Ratio",		gocId ->  SetupRatioCsm.createFromRaw(gocId),	"Delete RatioCovUnit a where a.baseYymm=?1 and a.gocId =?2"	)		
	
	
	, GMV50	("G50",		true,	"NewGoc EIR ", 				gocId ->  SetupDfEir.createNewgoc(gocId), 		"Delete DfLv2EirNewgoc a where a.baseYymm=?1 and a.gocId =?2")
	
	, GMV51	("G51", 	false,	"Init Rate",				gocId ->  SetupDfInitRate.createNew(gocId))
	, GMV53	("G53", 	true,	"Weighted Rate His",		gocId ->  SetupDfWghtRate.createNew(gocId), 		"Delete DfLv2WghtHis a where a.baseYymm=?1 and a.gocId =?2"	)
	
//	, GMV56	("G56",		true,	"Curr  EIR ", 				gocId ->  SetupDfEir.create(gocId), 			"Delete DfLv2Eir       a where a.baseYymm=?1 and a.gocId =?2")
	
	, GMV57	("G57", 	false,	"Wghted Rate Determined",	gocId ->  SetupDfWghtRate.determineNew(gocId))
	, GMV59	("G59", 	true,	"Goc Int Flat",				gocId ->  SetupDfLv3Flat.create(gocId), 		"Delete DfLv3Flat a where a.baseYymm=?1 and a.gocId =?2")
	
	, GMV65	("G65", 	false,	"CLOSE STEP CF+DF",			gocId ->  SetupCfLv4Df.createAlone(gocId), 		"Delete CfLv4Df   a where a.baseYymm=?1 and a.gocId =?2")
	, GMV66	("G66", 	false,	"DELTA STEP CF+DF",			gocId ->  SetupCfLv4Df.createDelta(gocId))
	
//	, GMV71	("G71", 	true,	"Box CF Detail",			gocId ->  MdlRstBox.createDetail(gocId), 		"Delete RstBoxDetail a where a.baseYymm=?1 and a.gocId =?2"	)
	, GMV71	("G71", 	true,	"Box CF Detail",			gocId ->  MdlRstBox.createDetailBeforeEirUpdate(gocId), 		"Delete RstBoxDetail a where a.baseYymm=?1 and a.gocId =?2"	)
	
	, GMV72	("G72", 	true,	"Box Ra",					gocId ->  MdlRstBox.createRa(gocId), 			"Delete RstBoxGoc    a where a.baseYymm=?1 and a.gocId =?2"	)
	, GMV73	("G73", 	false,	"Box TVOG ",				gocId ->  MdlRstBox.createTvog(gocId))
	, GMV74 ("G74", 	false,	"Box Real Cash",			gocId ->  MdlRstBox.createRealCash(gocId))
//	, GMV74 ("G74", 	false,	"Box Real Cash",			gocId ->  MdlRealCash.create(gocId))
	
	, GMV76	("G76", 	true,	"EIR Update",				gocId ->  MdlDfLv4Eir.create(gocId), 			"Delete DfLv4Eir   a where a.baseYymm=?1 and a.gocId =?2")
	, GMV761	("G761", 	false,	"EIR Update",			gocId ->  MdlDfLv4Eir.createAllEir(gocId))
	, GMV762	("G762", 	false,	"EIR Update",			gocId ->  MdlDfLv4Eir.createLrcEir(gocId))
	, GMV763	("G763", 	false,	"EIR Update",			gocId ->  MdlDfLv4Eir.createLicEir(gocId))
	
	, GMV77	("G77", 	false,	"EIR Update",				gocId ->  CurrSysRateUpdate.updateCurrEir(gocId))
	, GMV78	("G78", 	false,	"EIR Update",				gocId ->  MdlRstBox.createDetailAfterEirUpdate(gocId))
	, GMV79	("G79", 	false,	"Box CF",					gocId ->  MdlRstBox.createFromDetail(gocId))

	, GMV81	("G81", 	true,	"Fulfill at Loss Step",		gocId ->  MdlLossStep.create(gocId), 			"Delete RstLossStep a where a.baseYymm=?1 and a.gocId =?2"	)
	, GMV82	("G82", 	true,	"Goc Profit",				gocId ->  MdlRstCsm.create(gocId), 				"Delete RstCsm      a where a.baseYymm=?1 and a.gocId =?2"	)
	
	, GMV87	("G87",		true,	"DAC Calc",					gocId ->  MdlRstDacSplit.create(gocId),			"Delete RstDac a where a.baseYymm=?1 and a.gocId =?2"	  	)
	
	, GMV94	("G94", 	true,	"Roll Fwd ",				gocId ->  MdlRollFwd.create(gocId), 			"Delete RstRollFwd a where a.baseYymm=?1 and a.gocId =?2")
	, GMV95	("G95", 	false,	"Roll Fwd",					gocId ->  MdlRollFwd.createCsm(gocId))
	, GMV96	("G96", 	false,	"Roll Fwd",					gocId ->  MdlRollFwd.createDac(gocId))
	
	, GMV97	("G97", 	true,	"Roll Fwd",					gocId ->  MdlRollFwdLoss.create(gocId), 		"Delete RstRollFwdLoss a where a.baseYymm=?1 and a.gocId =?2")
	
	, GMV98	("G98", 	true,	"Journal Account",			gocId ->  MdlAcctBox.createFromRollFwd(gocId), 	"Delete AcctBoxGoc a where a.baseYymm=?1 and a.gocId =?2"	)
	, GMV99	("G99", 	false,	"Journal Account",			gocId ->  MdlAcctBox.createFromRollFwdLoss(gocId))
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
