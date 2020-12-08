package com.gof.test;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gof.atom.DVector;
import com.gof.atom.SmithWilsonNew;
import com.gof.atom.TermStructure;
import com.gof.dao.IrCurveHisDao;
import com.gof.entity.IrCurveHis;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class SmithWilsionNewTest {
	
	
	public static void main(String[] args) {
		aaa();
//		forwardTermStructureResult();
//		forwardBucketResult();
	}
	
	private static void aaa() {
		String bssd = "201812";
		List<String> termList = new ArrayList<String>();
		termList.add("M0003");
		termList.add("M0006");
		termList.add("M0009");
		termList.add("M0012");
		termList.add("M0024");
		termList.add("M0036");
		termList.add("M0060");
		termList.add("M0084");
		termList.add("M0120");
		termList.add("M0240");
		List<IrCurveHis> curveHis = IrCurveHisDao.getIrCurveHis(bssd, "1010000").stream().filter(s->termList.contains(s.getMatCd())).collect(toList());
		
		double[] _rate = new double[curveHis.size()];
		double[] _mat = new double[curveHis.size()];
		
		for(int i =0 ; i< curveHis.size(); i++) {
			_mat[i] = (double)curveHis.get(i).getMatNum() / 12.0;
			_rate[i] = curveHis.get(i).getIntRate();
		}
		DVector rate = new DVector(_rate);
		DVector term = new DVector(_mat);
		
//		for(int i =0 ; i< _mat.length; i++) {
//			log.info("zzz : {},{}", _mat[i], _rate[i]);
//			
//		}
//		Arrays.stream(rate.getData()).forEach(s-> log.info("rate : {}", s));
//		Arrays.stream(term.getData()).forEach(s-> log.info("term : {}", s));
		
//		TermStructure sw = new SmithWilsonNew(term, rate, 1, 0.052, 20.0);
		TermStructure sw = new SmithWilsonNew(term, rate,  Math.log(1+0.052), 20.0);
		
//		Arrays.stream(sw.getZeta().getData()).forEach(s-> log.info("zzz : {}", s));
		for(int j =1 ; j< 1213 ; j++) {
			log.info("aaa : {},{},{},{},{}", j/12.0, sw.bond(j/12.0), sw.convertToAnnnual(sw.spot(j/12.0)), sw.convertToAnnnual(sw.forward1M((j-1)/12.0)));
//			log.info("aaa : {},{},{},{}", j/12.0, sw.bond(j/12.0), Math.exp(sw.spot(j/12.0))-1, Math.exp(sw.forward(j/12.0))-1);
		}
//		log.info("aaa : {},{},{},{},{}", 60.0, sw.bond(60.0), sw.spot(60.0), sw.forward(60.0),  Math.log(1+0.052));
		
		
//		
	}
	
	
}
