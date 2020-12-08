package com.gof.test;

import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.CorrelatedRandomVectorGenerator;
import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.NormalizedRandomGenerator;
import org.hibernate.Session;

import com.gof.dao.StdAssetVolDao;
import com.gof.entity.StdAssetCorr;
import com.gof.util.HibernateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestRandomGenerator {
	private static Session session = HibernateUtil.getSessionFactory().openSession();
	
	public static void main(String[] args) {
		String bssd = "201812";
//		StdAssetVolDao.getStdAssetVol("20181228", "EWMA_0.97").forEach(s->log.info("zzz : {},{},{}", s.getBaseDate(), s.getMvId(), s.getMvHisVol()));
//		StdAssetVolDao.getStdAssetCorr("20181228", "EWMA_0.97").forEach(s->log.info("zzz : {},{},{},{}", s.getBaseDate(), s.getMvId(), s.getRefMvId(), s.getMvHisCorr()));
		
//		StdAssetVolDao.getStdAssetCorrByMonth("201812", "EWMA_0.97").forEach(s->log.info("zzz : {},{},{},{}", s.getMvId(), s.getRefMvId(), s.getMvHisCorr()));
		List<StdAssetCorr> avgCorrList =StdAssetVolDao.getStdAssetCorrByMonth("201812", "EWMA_0.97");
		
		int rank =(int) Math.sqrt(avgCorrList.size());
		
		double[][] data = new double[rank][rank];
		int rowIdx =0;
		int colIdx =0;
		int idx =0;
		for(StdAssetCorr aa : avgCorrList) {
			rowIdx = idx / rank ;
			colIdx = idx % rank ;
//			log.info(" di : {},{},{}", idx, rowIdx, colIdx);
//			data[rowIdx][colIdx] = aa.getMvHisCov();
			data[rowIdx][colIdx] = aa.getMvHisCorr();
			idx ++;
			
		}
		
		RealMatrix cov = new Array2DRowRealMatrix(data);
		
		RealMatrix cov1 = cov.scalarMultiply(Math.sqrt(2));
		
		
		log.info("aaa : {}", cov.getRow(2));
		log.info("aaa : {}", cov.getColumn(0));
		log.info("aaa : {},{}", cov.getRowDimension(), cov.getColumnDimension());
				
		NormalizedRandomGenerator generator  = new GaussianRandomGenerator(new MersenneTwister(Long.valueOf(bssd)));
		CorrelatedRandomVectorGenerator gen = new CorrelatedRandomVectorGenerator(cov, 0.01, generator);
		for(int i =0 ; i< 1000; i++) {
//			log.info("random {}: {},{},{},{},{},{},{}", gen.nextVector());
			double[] sce = gen.nextVector();
			log.info("random {}: {},{},{},{},{},{},{}", i, sce[0],sce[1],sce[2],sce[3],sce[4],sce[5],sce[6]);
			
		}
	}
	
	
	
	
	
}
