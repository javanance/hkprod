package com.gof.process;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.gof.dao.BizStockParamDao;
import com.gof.dao.EsgRandomDao;
import com.gof.dao.IrCurveHisDao;
import com.gof.dao.StdAssetDao;
import com.gof.dao.StockParamHisDao;
import com.gof.entity.BizEsgParam;
import com.gof.entity.BizStockParam;
import com.gof.entity.BizStockParamUd;
import com.gof.entity.EsgMst;
import com.gof.entity.EsgRandom;
import com.gof.entity.IrCurveHis;
import com.gof.entity.IrSce;
import com.gof.entity.StdAssetMst;
import com.gof.enums.EIrModelType;
import com.gof.model.CIR;
import com.gof.model.HullWhite;
import com.gof.model.HullWhite2Factor;
import com.gof.model.Vasicek;

import lombok.extern.slf4j.Slf4j;

/**
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
@Slf4j
public class Job07_BizStockParam {
	
	public static List<BizStockParam> createBizStockParam(String bssd, String bizDv, String localVolYn) {
		List<BizStockParamUd> bizUd  = BizStockParamDao.getBizStockParamUd(bssd, bizDv);

		List<String> bizUdStock = bizUd.stream().map(BizStockParamUd::getStdAsstCd).collect(toList());

		List<BizStockParam> bizParam = bizUd.stream().map(BizStockParamUd::convert).collect(toList());

		if(localVolYn.equals("Y")) {
			bizParam.addAll( StockParamHisDao.getStockParamHis(bssd).stream()
											 .filter(s->s.getParamTypCd().equals("SIGMA"))
											 .filter(s->s.isLocalVol())
											 .filter(s-> !bizUdStock.contains(s.getStdAsstCd()))
											 .map(s-> s.convert(bizDv))
											 .collect(toList())
							);
			
			List<String> bizStock = bizParam.stream().map(s->s.getStdAsstCd()).collect(toList());
			
			bizParam.addAll( StockParamHisDao.getStockParamHis(bssd).stream()
								 .filter(s->s.getParamTypCd().equals("SIGMA"))
								 .filter(s->!s.isLocalVol())
								 .filter(s-> !bizStock.contains(s.getStdAsstCd()))
								 .map(s-> s.convert(bizDv))
								 .collect(toList())
							);
		}
		else {
			bizParam.addAll( StockParamHisDao.getStockParamHis(bssd).stream()
								 .filter(s->s.getParamTypCd().equals("SIGMA"))
								 .filter(s->!s.isLocalVol())
								 .filter(s-> !bizUdStock.contains(s.getStdAsstCd()))
								 .map(s-> s.convert(bizDv))
								 .collect(toList())
							);
		}
		return bizParam;
	}
}

