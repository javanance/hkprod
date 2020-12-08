package com.gof.process;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gof.dao.TransitionMatrixDao;
import com.gof.entity.CorpCrdGrdPd;
import com.gof.entity.CorpCumPd;
import com.gof.entity.TransitionMatrix;
import com.gof.enums.ECreditGrade;
import com.gof.util.MathUtil;

import lombok.extern.slf4j.Slf4j;

/**
 *  <p> ��� �ſ��޺� �ε��� ���� ���� 
 *  <p> �ſ��򰡻翡�� �����ϴ� ������� (�ſ��ް� �̵� Ȯ��)�� �̿��Ͽ� 1�� �ε���, ���� �ε���, �����ε����� ������.
 *  
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job81_CorporatePd {

	public static List<CorpCrdGrdPd> createCorpPd(String bssd) {
		List<CorpCrdGrdPd> rstList = new ArrayList<CorpCrdGrdPd>();
		CorpCrdGrdPd temp;

		List<TransitionMatrix> tm = TransitionMatrixDao.getDefaultRate(bssd);
//		rst.forEach(s -> log.info("zzz  : {}", s.getFromGrade()));

//		Map<String, Double> volMap = getVolMap(bssd);

		for (TransitionMatrix aa : tm) {
			temp = new CorpCrdGrdPd();

			temp.setBaseYymm(bssd);
			temp.setCrdEvalAgncyCd("01");
			temp.setCrdGrdCd(ECreditGrade.getECreditGrade(aa.getFromGrade()).getLegacyCode());
			temp.setPd(aa.getTmRate());
//			temp.setVol(volMap.getOrDefault(temp.getCrdGrdCd(), 0.0));
		//  temp.setLastModifiedBy("JOB_52");
			temp.setLastModifiedBy(aa.getFromGrade());
			temp.setLastUpdateDate(LocalDateTime.now());

			rstList.add(temp);

		}
		log.info("Job81( Corporate PD Calculation) creates  {} results.  They are inserted into EAS_CORP_CRD_GRD_PD Table", rstList.size());
		rstList.stream().forEach(s->log.debug("Corporate PD Result : {}", s.toString()));
		
		return rstList;
	}
	
	
	
	public static List<CorpCumPd> createCorpCumPd(String bssd) {
		List<CorpCumPd> rstList = new ArrayList<CorpCumPd>();
		CorpCumPd temp;

		List<TransitionMatrix> rst = TransitionMatrixDao.getTM(bssd);
		Collections.sort(rst);
		
		
		double[][] tm = new double[14][15];
		List<ECreditGrade> gradeList = new ArrayList<ECreditGrade>();
		
		int n = 0;
		int m = 0;
		for (TransitionMatrix aa : rst) {
//			log.info("Corp Cum PD : {},{},{}", aa.getFromGradeEnum().getAlias(), aa.getToGradeEnum().getAlias(),aa.getTmRate());
			tm[m][n] = aa.getTmRate();
			n = n + 1;
			if (n == 15) {
				m = m + 1;
				n = 0;
				gradeList.add(aa.getFromGradeEnum());
			}
		}
		
//		for( int i=0; i< m ; i++) {
//			for(int j=0; j<n; j++) {
//				log.info("zzzz : {}", tm[i][j]);
//			}
//		}
		
		double[][] fwdPd = getFwdPd(101, tm);
		int k = 0;
		double cumPd[] = new double[gradeList.size()];
		double tempfwdPd = 0.0;

		for (int i = 0; i < 100; i++) {
			k = 0;
			for (ECreditGrade aa : gradeList) {
				tempfwdPd = fwdPd[i][k];
				cumPd[k] = tempfwdPd + (1 - tempfwdPd) * cumPd[k];		

				temp = new CorpCumPd();

				temp.setBaseYymm(bssd);
				temp.setAgencyCode("01");
				temp.setGradeCode(aa.getLegacyCode());
				temp.setMatCd("M" + String.format("%04d", (i + 1) * 12));
				temp.setCumPd(cumPd[k]);
				temp.setFwdPd(tempfwdPd);
				temp.setVol(0.0);
				temp.setLastModifiedBy("ESG_51");
				temp.setLastUpdateDate(LocalDateTime.now());

				k = k + 1;
				rstList.add(temp);
			}
		}
		
		log.info("Job81( Corporate Cumulative PD Calculation) creates  {} results.  They are inserted into EAS_CORP_CRD_GRD_CUM_PD Table", rstList.size());
//		rstList.stream().forEach(s->log.info("Corporate Cumulative PD Result : {}", s.toString()));
		rstList.stream().forEach(s->log.debug("Corporate Cumulative PD Result : {}", s.toString()));
		
		return rstList;
	}
	
	
	private static double[][] getFwdPd(int yearNum, double[][] tm) {

		double[][] fwdPd = new double[yearNum][tm.length];
		double[][] rstMatrix;
//		double temp = 0.0;

		double[][] tempMatrix;

		tempMatrix = tm;
		rstMatrix = tm;
		
		for (int j = 0; j < tm.length; j++) {
			fwdPd[0][j] = tm[j][tm[0].length - 1];
		}
 		
		for (int i = 1; i < yearNum - 1; i++) {
			rstMatrix = MathUtil.multi(tm, tempMatrix);				
			for (int j = 0; j < tm.length; j++) {
				fwdPd[i][j] = rstMatrix[j][tm[0].length - 1];		
			}
			tempMatrix = rstMatrix;
		}
		// logger.info("tm size : {},{}", tm.length, tm[0].length);
		return fwdPd;
	}

}
