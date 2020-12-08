package com.gof.process;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.query.Query;

import lombok.Getter;

@Getter
public enum EJobAsync {
	
//	job19();		// Delta Cash Flow 생성
	
	
//	  GMV21Async	("21A", 	false,	"NewCont Epv",		s ->  Job13Async_NewcontEpv.createAsync(s))
//	, GMV21			("21", 	false,	"NewCont Epv",		s ->  Job21_NewcontEpv.createNewcontEpv(s))
//	, GMV22			("22", 	false,	"NewCont Rst Flat",	s ->  Job21_NewcontEpv.createNewcontRstFlat(s))
	
;
	private String jobId;
	private boolean deleteThenInsert;
	
	private String desc;
	private Function<ExecutorService, Stream<? extends Object>> fn;
	
	private String deleteQuery;
	
	
	private EJobAsync(String jobId,  boolean isDeleteThenInsert, String desc, Function<ExecutorService, Stream<? extends Object>> fn ) {
		this.jobId = jobId;
		this.deleteThenInsert = isDeleteThenInsert;
		this.desc = desc;
		this.fn = fn;
		
	}
	
	private EJobAsync(String jobId,  boolean isDeleteThenInsert, String desc, Function<ExecutorService, Stream<? extends Object>> fn, String deleteQuery ) {
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
						Query q = session.createQuery(this.getDeleteQuery());
						for(int i=0; i<params.size(); i++) {
							q.setParameter(i, params.get(i));
						}
						return q.executeUpdate();
		};
	}			
}
