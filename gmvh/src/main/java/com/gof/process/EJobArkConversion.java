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
import com.gof.model.MdlDfLv4Eir;
import com.gof.model.MdlLossStep;
import com.gof.model.MdlRstBox;
import com.gof.rollfwd.MdlRollFwd;
import com.gof.setup.SetupCfLv1Goc;
import com.gof.setup.SetupCfLv4Df;
import com.gof.setup.SetupDfCurrRate;
import com.gof.setup.SetupDfInitRate;
import com.gof.setup.SetupDfLv3Flat;
import com.gof.setup.SetupDfWghtRate;
import com.gof.setup.SetupRaLv1;
import com.gof.setup.SetupRstEpvNgoc;
import com.gof.setup.SetupTvogLv1;

import lombok.Getter;

@Getter
public enum EJobArkConversion {
	  GMV01	("K01", 	true,	"Curr Rate", 				() ->  SetupDfCurrRate.createConversion(),			"Delete DfLv1CurrRate a where a.baseYymm=?1")
	, GMV02	("K02", 	true,	"Curr Rate Ark Sync", 		() ->  SetupDfCurrRate.createArkSyncConversion(),	"Delete DfLv1CurrRate a where a.baseYymm=?1")
	, GMV09	("K09", 	false,	"Append UnObservables", 	() ->  SetupDfCurrRate.appendConversion())
	
	, GMV22	("K22", 	true,	"All CF ", 					() ->  SetupCfLv1Goc.createConversion(), 	"Delete CfLv1Goc  a where a.baseYymm=?1")
	, GMV23	("K23", 	false,	"CF LV1",					() ->  ArkSetupCfLv1.createConversion())
	
	, GMV24	("K24", 	true,	"ARK Forward EPV", 			() ->  ArkSetupFwdEpv.createConversion(),	"Delete ArkFwdEpv a where a.baseYymm=?1")
	, GMV25	("K25", 	true,	"ARK CF Release", 			() ->  ArkSetupReleaseCf.createConversion(),"Delete ArkReleaseCf a where a.baseYymm=?1")
	, GMV26	("K26", 	true,	"ARK Future CF", 			() ->  ArkSetupFutureCf.createConversion(),	"Delete ArkFutureCf a where a.baseYymm=?1")
	
	, GMV28	("K28", 	true,	"Ark Item Rst", 			() ->  ArkSetupItemRst.createConversion(), 	"Delete ArkItemRst a where a.baseYymm=?1")
	, GMV29	("K29", 	true,	"Box Rst From Ark",			() ->  ArkSetupArkBox.createConversion(), 	"Delete ArkBoxRst a where a.baseYymm=?1")
	
 	, GMV30	("K30", 	true,	"FairValue", 				() ->  MldFvFlat.createConversion(),		"Delete FvFlat a where a.baseYymm=?1")
 	
 	, GMV31	("K31", 	true,	"TVOG RST",					() ->  SetupTvogLv1.createConversion(),		"Delete TvogLv1 a where a.baseYymm=?1")
	, GMV32	("K32", 	true,	"RA RST",					() ->  SetupRaLv1.createConversion(),		"Delete RaLv1 a where a.baseYymm=?1")

	, GMV35	("K35", 	true,	"Curr EPV", 				() ->  SetupRstEpvNgoc.createConversion(),	"Delete RstEpvNgoc a where a.baseYymm=?1")		

	, GMV50	("K50",		false,	"EIR  at conversion ", 		() ->  MdlDfLv4Eir.createConversion())
	, GMV51	("K51", 	false,	"Init Rate",				() ->  SetupDfInitRate.createConversion())
	
	, GMV53	("K53", 	true,	"Weighted Rate Historical",	() ->  SetupDfWghtRate.createConversion(),	"Delete DfLv2WghtHis a where a.baseYymm=?1")
	, GMV55	("K55", 	false,	"Weigthed Rate Determined",	() ->  SetupDfWghtRate.determineAll())
	, GMV59	("K59", 	true,	"Goc Int Flat",				() ->  SetupDfLv3Flat.createConversion(),	"Delete DfLv3Flat a where a.baseYymm=?1")
	
	, GMV63	("K63",		true,	"CF+DF : Curr Conversion",	() ->  SetupCfLv4Df.createConversion(),		"Delete CfLv4Df a where a.baseYymm=?1")
	
	, GMV71	("K71", 	true,	"Box CF Detail",			() ->  MdlRstBox.createConversionDetail(), "Delete RstBoxDetail a where a.baseYymm=?1" )
	, GMV72	("K72", 	true,	"Box Ra",					() ->  MdlRstBox.createConversionRa(), 		"Delete RstBoxGoc    a where a.baseYymm=?1"	)
	, GMV73	("K73", 	false,	"Box TVOG ",				() ->  MdlRstBox.createConversionTvog())
	
//	, GMV74	("A74", 	false,	"BoX Real Cash",			() ->  ArkMldBoxRealCash.create())
	, GMV74 ("K74", 	false,	"Box Real Cash",			() ->  MdlRstBox.createConversionRealCash())	
	, GMV75	("K75", 	false,	"Box EPV",					() ->  ArkSetupArkBox.createConversionRstBoxGoc())
	, GMV79	("K79", 	false,	"Box CF GOC",				() ->  MdlRstBox.createConversionFromDetail())
	
	
	, GMV81	("K81", 	false,	"Fulfill at Loss Step",		() ->  MdlLossStep.createConversion())
	, GMV82	("K82", 	true,	"Init Csm ",				() ->  MdlInitCsm.createConversion(),		"Delete RstCsm a where a.baseYymm=?1")
//	, GMV87	("K87", 	true,	"Init DAC ",				() ->  MdlRstDacSplit.createConversion(),	"Delete RstDac a where a.baseYymm=?1")
	, GMV88	("K88", 	true,	"Init DAC ",				() ->  ArkMdlDac.createConversion(),		"Delete RstDac a where a.baseYymm=?1")
	
	, GMV93	("K93", 	true,	"Roll Fwd ",				() ->  MdlRollFwd.createConversion(), 		"Delete RstRollFwd a where a.baseYymm=?1")
;
	
	private String jobId;
	private boolean deleteThenInsert;
	
	private String desc;
//	private Function<String, Stream<? extends Object>> fn;
	private Supplier<Stream<? extends Object>> supplier; 		   
	
	private String deleteQuery;
	
	
	private EJobArkConversion(String jobId,  boolean isDeleteThenInsert, String desc, Supplier<Stream<? extends Object>> supplier ) {
		this.jobId = jobId;
		this.deleteThenInsert = isDeleteThenInsert;
		this.desc = desc;
		this.supplier = supplier;
	}
	
	private EJobArkConversion(String jobId,  boolean isDeleteThenInsert, String desc, Supplier<Stream<? extends Object>> supplier, String deleteQuery ) {
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
