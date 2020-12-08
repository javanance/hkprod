package com.gof.process;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.gof.conv.MdlInitCsmReins;
import com.gof.conv.MldFvFlat;
import com.gof.model.MdlDfLv4Eir;
import com.gof.model.MdlRstBox;
import com.gof.rollfwd.MdlRollFwd;
import com.gof.setup.SetupCfLv1Goc;
import com.gof.setup.SetupCfLv4Df;
import com.gof.setup.SetupDfCurrRate;
import com.gof.setup.SetupDfInitRate;
import com.gof.setup.SetupDfLv3Flat;
import com.gof.setup.SetupDfWgtRate;
import com.gof.setup.SetupElLv1;
import com.gof.setup.SetupRaLv1;
import com.gof.setup.SetupRstEpvNgoc;
import com.gof.setup.SetupRstLossRcv;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public enum EJobConversion {
	
	  GMV01	("C01", 	true,	"Curr Rate", 					() ->  SetupDfCurrRate.createConversion(),			"Delete DfLv1CurrRate a where a.baseYymm=?1")
	, GMV02	("C02", 	true,	"Curr Rate Ark Sync", 			() ->  SetupDfCurrRate.createArkSyncConversion(),	"Delete DfLv1CurrRate a where a.baseYymm=?1")
	
//	  GMV12	("C12", 	false,	"NewCont 61th & EPV",			() ->  SetupNcontCf.createAll())
//	, GMV13	("C13", 	false,	"NewCont ra",					() ->  SetupNcontRst.createConvertRa())
//	, GMV14	("C14", 	false,	"NewCont tvog",					() ->  SetupNcontRst.createConvertTvog())
//	, GMV15	("C15", 	false,	"NewCont epv",					() ->  SetupNcontRst.createConvertEpv())
//	
//	, GMV16	("C16", 	false,	"NewCont Flat",					() ->  SetupNcontRst.createConversion())
//	, GMV17	("C17", 	false,	"NewCont Goc Mapping",			() ->  SetupGocCont.createConversion())
	
// 	, GMV24	("C24",		false,	"Goc Current CF", 				() ->  SetupCfLv1Goc.createConversion())
 	, GMV24	("C24",		true,	"Goc Current CF", 				() ->  SetupCfLv1Goc.createConversion(),	"Delete CfLv1Goc a where a.baseYymm=?1")
 	, GMV30	("C30", 	false,	"FairValue", 					() ->  MldFvFlat.createConversion())
 	
 	, GMV31	("C31", 	true,	"EL RST",						() ->  SetupElLv1.createConversion(), 	"Delete ElLv1 a where a.baseYymm=?1")
	, GMV32	("C32", 	true,	"RA RST",						() ->  SetupRaLv1.createConversion(), 	"Delete RaLv1 a where a.baseYymm=?1")
//	, GMV33	("C33", 	true,	"LossRec RST",					() ->  SetupLossRcvLv1.createConversion(), 	"Delete LossRecLv1 a where a.baseYymm=?1")
	
	, GMV35	("C35", 	true,	"Curr EPV", 					() ->  SetupRstEpvNgoc.createConversion(), 	"Delete RstEpvNgoc a where a.baseYymm=?1")
	
//	MstCalcUlRefDetail에는 변동에 관한 항목만 찍기 때문에 전환시점 기말 값을 찍을 수가 없음 ㅠ ..> 일단 쿼리로 넣음..
//	LOSS RCV 전환시점 잔액 추가 필요 --> CSM 산출 전  LR_CURR 값을 RST_LOSS_RCV에 적재 
	
	, GMV39	("C39",		true,	"RstLossRcv",					() ->  SetupRstLossRcv.createConversion(), 		"Delete RstLossRcv a where a.baseYymm=?1")
	
	, GMV50	("C50",		true,	"EIR  at conversion ", 			() ->  MdlDfLv4Eir.createConversion(), 	"Delete DfLv4Eir a where a.baseYymm=?1")	
	
	, GMV51	("C51", 	false,	"Init Rate",					() ->  SetupDfInitRate.createConversion())
	, GMV53	("C53", 	true,	"Weighted Rate Historical",		() ->  SetupDfWgtRate.createConversion(), 	"Delete DfLv2WghtHis a where a.baseYymm=?1")
	, GMV55	("C55", 	false,	"Weigthed Rate Determined",		() ->  SetupDfWgtRate.determineAll())

	, GMV59	("C59", 	true,	"Goc Int Flat",					() ->  SetupDfLv3Flat.createConversion(), 	"Delete DfLv3Flat a where a.baseYymm=?1")
	
	, GMV63	("C63",		true,	"CF+DF : Curr Conversion",		() ->  SetupCfLv4Df.createConversion(), 	"Delete CfLv4Df a where a.baseYymm=?1")
	
	
	, GMV75	("C75", 	false,	"Init LossRecovery",			() ->  MdlRstBox.createLossRcvConversion())
	
	, GMV82	("C82", 	true,	"Init Csm ",					() ->  MdlInitCsmReins.createConversion(), 	"Delete RstCsm a where a.baseYymm=?1")
	
	, GMV94	("C94", 	true,	"Init RollFwd ",				() ->  MdlRollFwd.createConversion(), 		"Delete RstRollFwd a where a.baseYymm=?1")
	, GMV95	("C95", 	false,	"Init RollFwd Csm ",			() ->  MdlRollFwd.createCsmConversion())
//	, GMV96	("C96", 	false,	"Init LossRecovery",			() ->  MdlRollFwd.createLossRcvConversion())
//	, GMV97	("C97", 	false,	"Curr Loss Allocation",			() ->  MdlInitLoss.createConversion())
//	, GMV71	("C71", 	false,	"Unit Coverage ",				() ->  MdlRatioCovUnit.create())				//TODO : adddddddddd logic
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
