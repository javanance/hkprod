package com.gof.process;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.gof.dao.LiqPremiumDao;
import com.gof.entity.BizLiqPremium;
import com.gof.entity.BizLiqPremiumUd;
import com.gof.entity.LiqPremium;
import com.gof.model.LiquidPremiumModel;

/**
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
//@Slf4j
public class Job21_BizLiquidPremium {
	
	public static List<BizLiqPremium> createBizLiqPremium(String bssd, String bizDv, String modelId, String fittingModel) {
		List<BizLiqPremium> rstList = new ArrayList<BizLiqPremium>();
		
		List<LiqPremium> liqPremList    = LiqPremiumDao.getLiqPremium(bssd, modelId);
		List<BizLiqPremiumUd> lpUserRst = LiqPremiumDao.getLiqPremiumUd(bssd);
		
		if(lpUserRst.isEmpty()) {
			LiquidPremiumModel lpModel = new LiquidPremiumModel(fittingModel, liqPremList,"1010000", 0, 20);
			rstList = lpModel.getLiqPremium(bssd).stream().map(s->s.convertTo(bizDv)).collect(toList());
		}
		else{
			rstList = lpUserRst.stream().map(s->s.convertToBizLiqPremium(bssd)).collect(toList());
		}
		
		return rstList;
	}
	
	public static List<BizLiqPremium> createBizLiqPremium(String bssd, String bizDv, String modelId) {
		List<BizLiqPremium> rstList = new ArrayList<BizLiqPremium>();
		
		List<LiqPremium> liqPremList    = LiqPremiumDao.getLiqPremium(bssd, modelId);
		List<BizLiqPremiumUd> lpUserRst = LiqPremiumDao.getLiqPremiumUd(bssd);
		
		if(lpUserRst.isEmpty()) {
			LiquidPremiumModel lpModel = new LiquidPremiumModel("NA", liqPremList,"1010000", 0, 20);
			rstList = lpModel.getLiqPremium(bssd).stream().map(s->s.convertTo(bizDv)).collect(toList());
		}
		else{
			rstList = lpUserRst.stream().map(s->s.convertToBizLiqPremium(bssd)).collect(toList());
		}
		
		return rstList;
	}
}
