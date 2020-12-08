package com.gof.process;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.gof.dao.InflationDao;
import com.gof.entity.BizInflation;
import com.gof.entity.Inflation;
import com.gof.entity.InflationUd;
import com.gof.util.EsgConstant;

import lombok.extern.slf4j.Slf4j;
/**
 *  <p> ���÷��̼� ��ǥ ������� 
 *  <p> ���α������ ����ϴ� �Һ��� �������� (CPI)�� ���⵿�� ��� ��·��� ���ġ�� ���÷��̼� ��ǥ�� ������.
 *  
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job72_BizInflation {
	public static List<BizInflation> createIfrsBizInflationMA(String bssd) {
		double cpiWeight = EsgConstant.getNumConstant().getOrDefault("IFRS_CPI_WEIGHT", 1.0) ;
		return createBizInflationMA(bssd, "I", cpiWeight);
	}
	
	public static List<BizInflation> createKicsBizInflationMA(String bssd) {
		double cpiWeight = EsgConstant.getNumConstant().getOrDefault("KICS_CPI_WEIGHT", 1.0) ;
		return createBizInflationMA(bssd, "K", cpiWeight);
	}
	
	private static List<BizInflation> createBizInflationMA(String bssd, String bizDv, double cpiWeight) {
		List<BizInflation> rstList = new ArrayList<BizInflation>();
		int avgNum = -1 * Integer.parseInt(EsgConstant.getStrConstant().getOrDefault("CPI_MA_NUM", "24"));  //지정한 월수(24개월)) 간 데이터의 평균임. 없는 데이터는 counting 안함. 
		
		List<InflationUd> infUdList = InflationDao.getInflationUd(bssd);				//CURRENT USER INPUT 
		List<Inflation> infList = InflationDao.getPrecedingInflation(bssd, avgNum); 
		
		double avgSum =0.0;
		int    avgCnt =0;
		
		double infRate =0.0;
		double targetRate =0.0;
		
		if( infUdList.size() > 0 && infUdList.get(0).getKicsTgtIndex() != null && infUdList.get(0).getKicsTgtIndex() != 0.0  ) { 
			InflationUd infUd = infUdList.get(0);
			targetRate = infUd.getKicsTgtIndex();
		}
		
		for (Inflation zz : infList) {
			avgSum = avgSum + zz.getInflation();
			avgCnt  = avgCnt +1;
		}

		log.info("biz inflation interim result : {},{},{},{},{}", bssd, avgCnt, avgSum, cpiWeight);
		
		Inflation inf = infList.get(0);
		infRate =cpiWeight *  avgSum/avgCnt + (1-cpiWeight) * targetRate;
				
		BizInflation rst = new BizInflation();
		rst.setBaseYymm(bssd);
		rst.setApplyBizDv(bizDv);
		rst.setInflationId(cpiWeight<1.0 ? "TGT": inf.getInflationId());
		rst.setInflationIndex(inf.getInflationIndex());
		rst.setInflation(infRate);
		
		rst.setMgmtTargetLowerVal(0.0);
		rst.setMgmtTargetUpperVal(0.0);
		
		rst.setLastModifiedBy("ESG");
		rst.setLastUpdateDate(LocalDateTime.now());

		rstList.add(rst);
		
		log.info("Job (Biz {} Applied Inflation) create {} result.  They are inserted into EAS_BIZ_APPL_INFLATION Table ", bizDv,  rstList.size());
		return rstList;
	}
}



		




