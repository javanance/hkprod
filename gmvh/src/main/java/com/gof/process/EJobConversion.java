package com.gof.process;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.ark.model.ArkMdlDac;
import com.gof.ark.model.ArkSetupArkBox;
import com.gof.ark.model.ArkSetupCfLv1;
import com.gof.ark.model.ArkSetupFutureCf;
import com.gof.ark.model.ArkSetupFwdEpv;
import com.gof.ark.model.ArkSetupItemRst;
import com.gof.ark.model.ArkSetupReleaseCf;
import com.gof.conv.MdlInitCsm;
import com.gof.conv.MldFvFlat;
import com.gof.model.CurrSysRateUpdate;
import com.gof.model.MdlDfLv4Eir;
import com.gof.model.MdlLossStep;
import com.gof.model.MdlRstBox;
import com.gof.model.MdlRstDacSplit;
import com.gof.rollfwd.MdlRollFwd;
import com.gof.setup.SetupCfLv1Goc;
import com.gof.setup.SetupCfLv4Df;
import com.gof.setup.SetupDfCurrRate;
import com.gof.setup.SetupDfInitRate;
import com.gof.setup.SetupDfLv3Flat;
import com.gof.setup.SetupDfWghtRate;
import com.gof.setup.SetupRaLv1;
import com.gof.setup.SetupRaLv1Ibnr;
import com.gof.setup.SetupRstEpvNgoc;
import com.gof.setup.SetupTvogLv1;

import lombok.Getter;

@Getter
public enum EJobConversion {
	  GMV01	("C01", 	true,	"Curr Rate", 				() ->  SetupDfCurrRate.createConversion(),			"Delete DfLv1CurrRate a where a.baseYymm=?1")
	, GMV02	("C02", 	true,	"Curr Rate Ark Sync", 		() ->  SetupDfCurrRate.createArkSyncConversion(),	"Delete DfLv1CurrRate a where a.baseYymm=?1")
	, GMV09	("C09", 	false,	"Append UnObservables", 	() ->  SetupDfCurrRate.appendConversion())
	
//	, GMV12	("C12", 	false,	"NewCont 61th & EPV",		() ->  SetupNcontCf.createAll())
//	, GMV13	("C13", 	false,	"NewCont ra",				() ->  SetupNcontRst.createConvertRa())
//	, GMV14	("C14", 	false,	"NewCont tvog",				() ->  SetupNcontRst.createConvertTvog())
//	, GMV15	("C15", 	false,	"NewCont epv",				() ->  SetupNcontRst.createConvertEpv())
//	, GMV16	("C16", 	false,	"NewCont Flat",				() ->  SetupNcontRst.createConversion())
//	, GMV17	("C17", 	false,	"NewCont Goc Mapping",		() ->  SetupGocCont.createConversion())

	, GMV22	("C22", 	true,	"All CF ", 					() ->  SetupCfLv1Goc.createConversion(), 	"Delete CfLv1Goc  a where a.baseYymm=?1")
//	, GMV23	("C23", 	false,	"CF LV1",					() ->  ArkSetupCfLv1.createConversion())
	
//	, GMV24	("C24", 	true,	"ARK Forward EPV", 			() ->  ArkSetupFwdEpv.createConversion(),	"Delete ArkFwdEpv a where a.baseYymm=?1")
//	, GMV25	("C25", 	true,	"ARK CF Release", 			() ->  ArkSetupReleaseCf.createConversion(),"Delete ArkReleaseCf a where a.baseYymm=?1")
//	, GMV26	("C26", 	true,	"ARK Future CF", 			() ->  ArkSetupFutureCf.createConversion(),	"Delete ArkFutureCf a where a.baseYymm=?1")
//	
//	, GMV28	("C28", 	true,	"Ark Item Rst", 			() ->  ArkSetupItemRst.create(), 			"Delete ArkItemRst a where a.baseYymm=?1")
//	, GMV29	("C29", 	true,	"Box Rst From Ark",			() ->  ArkSetupArkBox.create(), 	    	"Delete ArkBoxRst a where a.baseYymm=?1")
	
 	, GMV30	("C30", 	true,	"FairValue", 				() ->  MldFvFlat.createConversion(),		"Delete FvFlat a where a.baseYymm=?1")
 	
 	, GMV31	("C31", 	true,	"TVOG RST",					() ->  SetupTvogLv1.createConversion(),		"Delete TvogLv1 a where a.baseYymm=?1")
	, GMV32	("C32", 	true,	"RA RST",					() ->  SetupRaLv1.createConversion(),		"Delete RaLv1 a where a.baseYymm=?1")
	, GMV321	("C321", 	false,	"RA Lv1 IBNR",			() ->  SetupRaLv1Ibnr.createConversion())

	, GMV35	("C35", 	true,	"Curr EPV", 				() ->  SetupRstEpvNgoc.createConversion(),	"Delete RstEpvNgoc a where a.baseYymm=?1")		

//	, GMV39	("C39", 	false,	"DAC Ratio",				() ->  MdlRstDac.createConversion())
//	, GMV40	("C40", 	false,	"DAC RST",					() ->  MdlRstDac.createConversion())
	
	, GMV50	("C50",		false,	"EIR  at conversion ", 		() ->  MdlDfLv4Eir.createConversion())
	, GMV51	("C51", 	false,	"Init Rate",				() ->  SetupDfInitRate.createConversion())
	
	, GMV53	("C53", 	true,	"Weighted Rate Historical",	() ->  SetupDfWghtRate.createConversion(),	"Delete DfLv2WghtHis a where a.baseYymm=?1")
	, GMV55	("C55", 	false,	"Weigthed Rate Determined",	() ->  SetupDfWghtRate.determineAll())
	, GMV59	("C59", 	true,	"Goc Int Flat",				() ->  SetupDfLv3Flat.createConversion(),	"Delete DfLv3Flat a where a.baseYymm=?1")
	
	, GMV63	("C63",		true,	"CF+DF : Curr Conversion",	() ->  SetupCfLv4Df.createConversion(),		"Delete CfLv4Df a where a.baseYymm=?1")
	
	
	, GMV71	("C71", 	true,	"Box CF Detail",			() ->  MdlRstBox.createConversionDetail(), "Delete RstBoxDetail a where a.baseYymm=?1" )
	, GMV72	("C72", 	true,	"Box Ra",					() ->  MdlRstBox.createConversionRa(), 	   "Delete RstBoxGoc    a where a.baseYymm=?1" )
	, GMV73	("C73", 	false,	"Box TVOG ",				() ->  MdlRstBox.createConversionTvog())
	
	, GMV74 ("C74", 	false,	"Box Real Cash",			() ->  MdlRstBox.createConversionRealCash())	
	, GMV79	("C79", 	false,	"Box CF GOC",				() ->  MdlRstBox.createConversionFromDetail())
	
	, GMV81	("C81", 	false,	"Fulfill at Loss Step",		() ->  MdlLossStep.createConversion())
	, GMV82	("C82", 	true,	"Init Csm ",				() ->  MdlInitCsm.createConversion(),		"Delete RstCsm a where a.baseYymm=?1")
	, GMV87	("C87", 	true,	"Init DAC ",				() ->  MdlRstDacSplit.createConversion(),	"Delete RstDac a where a.baseYymm=?1")
	
	, GMV93	("C93", 	true,	"Roll Fwd ",				() ->  MdlRollFwd.createConversion(), 		"Delete RstRollFwd a where a.baseYymm=?1")
//	, GMV97	("C97", 	false,	"Curr Loss Allocation",		() ->  MdlInitLoss.createConversion())
//	, GMV71	("C71", 	false,	"Unit Coverage ",			() ->  MdlRatioCovUnit.create())			//TODO : adddddddddd logic
;
	
	private String jobId;
	private boolean deleteThenInsert;
	
	private String desc;
//	private Function<String, Stream<? extends Object>> fn;
	private Supplier<Stream<? extends Object>> supplier; 		   
	
	private String deleteQuery;
	
	
	private EJobConversion(String jobId,  boolean isDeleteThenInsert, String desc, Supplier<Stream<? extends Object>> supplier ) {
		this.jobId = jobId;
		this.deleteThenInsert = isDeleteThenInsert;
		this.desc = desc;
		this.supplier = supplier;
	}
	
	private EJobConversion(String jobId,  boolean isDeleteThenInsert, String desc, Supplier<Stream<? extends Object>> supplier, String deleteQuery ) {
		this.jobId = jobId;
		this.deleteThenInsert = isDeleteThenInsert;
		this.desc = desc;
		this.supplier = supplier;
		this.deleteQuery = deleteQuery;
	}
	
	public String getJobName() {
		return jobId;
	}
	
	public Supplier<Integer> getDeleter(Session session, List<String> params){
		return   ()-> {
						Query q = session.createQuery(this.getDeleteQuery());
						for(int i=0; i<params.size(); i++) {
							q.setParameter(i+1, params.get(i));
						}
						return q.executeUpdate();
		};
	}			
}
