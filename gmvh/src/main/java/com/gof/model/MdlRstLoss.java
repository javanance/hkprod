package com.gof.model;

import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summarizingDouble;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.gof.dao.DfDao;
import com.gof.dao.RatioDao;
import com.gof.dao.RstDao;
import com.gof.entity.DfLv2WghtRate;
import com.gof.entity.MapJournalRollFwd;
import com.gof.entity.MstRollFwd;
import com.gof.entity.RatioCovUnit;
import com.gof.entity.RstCsm;
import com.gof.entity.RstLoss;
import com.gof.entity.RstLossStep;
import com.gof.enums.EAlloDiv;
import com.gof.enums.ECoa;
import com.gof.enums.ELossDiv;
import com.gof.enums.ELossStep;
import com.gof.enums.ERollFwdType;
import com.gof.infra.GmvConstant;
import com.gof.provider.PrvdMst;
import com.gof.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MdlRstLoss {
	private static	String bssd			=GmvConstant.BSSD;
	private static	String stBssd		=GmvConstant.ST_BSSD;
	private static	String vBssd		=GmvConstant.V_BSSD;
	
	private static Map<String, Double> prevCloseMap 	= new HashMap<String, Double>();
	private static Map<String, Double> intRateMap 		= new HashMap<String, Double>(); 
	private static Map<String, Double> covUnitMap 		= new HashMap<String, Double>();

	
	public static Stream<RstLoss> create() {
		return PrvdMst.getGocIdList().stream()
//				.filter(s->s.equals("0401_2019_3"))
				.flatMap(s->create(s));
	}
	public static Stream<RstLoss> create(String gocId) {
		return createAndUpdateReverseClose(gocId);
	}
	
	private static Stream<RstLoss> createAndUpdateReverseClose(String gocId) {

		List<MstRollFwd> rollFwdList = PrvdMst.getMstRollFwdList().stream().filter(s-> s.getCoaMap().get(ECoa.CSM)).collect(toList());
		
		Map<ERollFwdType, ERollFwdType> priorCloseMap = PrvdMst.getPriorCloseStepMap(rollFwdList);

		List<RstCsm> rstCsmList   = RstDao.getRstCsm(bssd, gocId).stream().filter(s->s.getRollFwdType().getLossDiv().equals(ELossDiv.REVERSE_CLOSE)).collect(toList());

		List<RstLoss> rstLossList = createFromCsm(gocId);
		
		double currFace = rstLossList.stream().filter(s->s.getRollFwdType().equals(ERollFwdType.CURR_CLOSE)).mapToDouble(RstLoss::getLossFaceAmt).sum();
		double currTvom = rstLossList.stream().filter(s->s.getRollFwdType().equals(ERollFwdType.CURR_CLOSE)).mapToDouble(RstLoss::getLossTvom).sum();
		double currRa   = rstLossList.stream().filter(s->s.getRollFwdType().equals(ERollFwdType.CURR_CLOSE)).mapToDouble(RstLoss::getLossRa).sum();
		
		ERollFwdType priorClose = priorCloseMap.get(ERollFwdType.MOVE_CLOSE);
		
//		priorCloseMap.entrySet().forEach(s->log.info("zzzzz : {},{},{}", s.getKey(), s.getValue(), priorClose));
		
		double priorFace = rstLossList.stream().filter(s->s.getRollFwdType().equals(priorClose)).mapToDouble(RstLoss::getLossFaceAmt).sum();
		double priorTvom = rstLossList.stream().filter(s->s.getRollFwdType().equals(priorClose)).mapToDouble(RstLoss::getLossTvom).sum();
		double priorRa 	 = rstLossList.stream().filter(s->s.getRollFwdType().equals(priorClose)).mapToDouble(RstLoss::getLossRa).sum();
		
		for(RstCsm aa : rstCsmList) {
			log.info("zzz : {},{}",aa.getRollFwdType());
			
			double lossFace = currFace - priorFace;
			double lossTvom	= currTvom - priorTvom;
			double lossRa 	= currRa   - priorRa;
			
			rstLossList.add(buildClose(bssd, gocId, aa, lossFace, lossTvom, lossRa));
		}
		
		return rstLossList.stream();
	}
	
	private static List<RstLoss> createFromCsm(String gocId) {
		List<RstLoss> rstList = new ArrayList<RstLoss>();
		
		List<RstCsm> rstCsmList   = RstDao.getRstCsm(bssd, gocId).stream().filter(s->s.getRollFwdType().isLossRollFwd()).collect(toList());
		Map<ELossStep, RstLossStep> lossStepMap = RstDao.getRstEpvLossStep(bssd, gocId).stream().collect(toMap(RstLossStep::getLossStep, Function.identity()));
		
		ELossStep lossStep;
		double priorFace=0.0;
		double priorTvom=0.0;
		double priorRa=0.0;
		
		
		for(RstCsm aa :rstCsmList) {
			if(aa.getRollFwdType().getLossDiv().equals(ELossDiv.REVERSE_CLOSE)) {
				
			}
			else if(aa.getRollFwdType().getLossDiv().equals(ELossDiv.CLOSE)) {
				rstList.add(buildClose(bssd, gocId, aa, priorFace, priorTvom, priorRa));
			}
			else if(aa.getRollFwdType().getLossDiv().equals(ELossDiv.NA)) {
				rstList.add(buildClose(bssd, gocId, aa, 0.0, 0.0, 0.0));
			}
			else {
				lossStep = aa.getRollFwdType().isClose()?aa.getLossStep(): aa.getMstCalc().getLossStep();
				
				RstLossStep rstLossStep = lossStepMap.get(lossStep);
				RstLoss tempLoss =  build(bssd, gocId, aa, rstLossStep);
				
				if(tempLoss.getRollFwdType().equals(ERollFwdType.PREV_CLOSE)) {
					MstRollFwd mstRollFwd = PrvdMst.getMstRollFwd(ERollFwdType.PREV_REVERSAL);
					RstLoss prevAlloReverse =  addhoc(bssd, gocId, tempLoss, mstRollFwd, -1.0);
					rstList.add(prevAlloReverse);
				}
				else if(tempLoss.getRollFwdType().equals(ERollFwdType.INIT_CLOSE)) {
					MstRollFwd mstRollFwd = PrvdMst.getMstRollFwd(ERollFwdType.INIT_RECOG);
					RstLoss intiRecogDelta =  addhoc(bssd, gocId, tempLoss, mstRollFwd, 1.0);
					rstList.add(intiRecogDelta);
				}
				
				if(aa.getRollFwdType().getLossDiv().getAlloDiv().equals(EAlloDiv.RATIO)  ) {
					priorFace = tempLoss.getLossFaceAmt();
					priorTvom = tempLoss.getLossTvom();
					priorRa = tempLoss.getLossRa();
				}
				else {
					priorFace = priorFace +  tempLoss.getLossFaceAmt();
					priorTvom = priorTvom + tempLoss.getLossTvom();
					priorRa   = priorRa +tempLoss.getLossRa();
				}
				rstList.add(build(bssd, gocId, aa, rstLossStep));
			}
		}
		return rstList;
	}	
	
	private static RstLoss buildClose(String bssd, String gocId, RstCsm rstCsm, double faceAmt, double tvom, double raAmt) {
		return RstLoss.builder().baseYymm(bssd)
				.gocId(gocId)
				.mstRollFwd(rstCsm.getMstRollFwd())
				.runsetId(rstCsm.getRunsetId())
				.mstCalc(rstCsm.getMstCalc())
				.seq(rstCsm.getSeq())
				.operatorType(rstCsm.getOperatorType())
				.boxAmt(rstCsm.getBoxAmt())
				.deltaCalcCsmAmt(rstCsm.getDeltaCalcCsmAmt())
				.calcCsmAmt(rstCsm.getCalcCsmAmt())
				.lossAmt(faceAmt - tvom + raAmt)
				.lossEpv(faceAmt -tvom)
				.lossFaceAmt(faceAmt)
				.lossTvom(tvom)
				.lossRa(raAmt)
				.remark("")
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
	}
	
	private static RstLoss addhoc(String bssd, String gocId, RstLoss rstLoss, MstRollFwd mstRollFwd, double signAdj) {
		
		return RstLoss.builder().baseYymm(bssd)
				.gocId(gocId)
				.mstRollFwd(mstRollFwd)
				.runsetId(rstLoss.getRunsetId())
				.mstCalc(rstLoss.getMstCalc())
				.seq(mstRollFwd.getRollFwdType().getOrder())
				.operatorType(rstLoss.getOperatorType())
				.boxAmt(rstLoss.getBoxAmt())
				.deltaCalcCsmAmt(rstLoss.getDeltaCalcCsmAmt())
				.calcCsmAmt(rstLoss.getCalcCsmAmt())
				
				.lossAmt(signAdj* rstLoss.getLossAmt())
				.lossEpv(signAdj* rstLoss.getLossEpv())
				.lossFaceAmt(signAdj* rstLoss.getLossFaceAmt())
				.lossTvom(signAdj * rstLoss.getLossTvom())
				.lossRa(signAdj * rstLoss.getLossRa())
				.remark("")
				.lastModifiedBy(GmvConstant.getLastModifier())
				.lastModifiedDate(LocalDateTime.now())
				.build();
	}
	private static RstLoss build(String bssd, String gocId, RstCsm rstCsm, RstLossStep lossStep) {
		double ratioFace = 0.0;
		double ratioTvom = 0.0;
		double ratioRa = 0.0;
		
		ELossDiv lossDiv = rstCsm.getRollFwdType().getLossDiv();
		
		if(lossStep !=null) {
			double denom = lossStep.getEpvAmt() +lossStep.getRaAmt();
			
			ratioFace = denom==0.0? 0.0: lossStep.getCfAmt() / denom;
			ratioTvom = denom==0.0? 0.0: ( lossStep.getCfAmt()-lossStep.getEpvAmt()) / denom;
			ratioRa   = denom==0.0? 0.0: lossStep.getRaAmt() / denom;
		}
		double baseAmt =0.0;
		if(rstCsm.getCalcCsmAmt() ==0) {
			baseAmt = lossDiv.getSignAdj() * lossDiv.getFn().apply(rstCsm);
		}
		else if(rstCsm.getCalcCsmAmt() < 0 ) {
			baseAmt =  lossDiv.getSignAdj() * lossDiv.getFn().apply(rstCsm);
		}
		
//		log.info("zzzzqqqq : {},{},{},{},{}", rstCsm.getRollFwdType(), lossDiv, ratioFace, lossDiv.getSignAdj(), lossDiv.getFn().apply(rstCsm));
		
		double lossAmt  = baseAmt;
		double lossTvom = 0.0;
		double lossRa   = 0.0;
		double lossFace = 0.0;
		double lossEpv  = 0.0;
		
		switch (lossDiv) {
		case LOSS_FACE:
			lossFace =baseAmt ;
			lossEpv = lossAmt - lossRa;
			break;
		case LOSS_TVOM:
			lossTvom =-1.0* baseAmt ;
			lossEpv = lossAmt - lossRa;
			break;
		case LOSS_RA:
			lossRa =baseAmt ;
			lossEpv = lossAmt - lossRa;
			break;
			
		case ALLO:
		case REV_ALLO:
		case DELTA_ALLO:
			lossFace = baseAmt * ratioFace;
			lossTvom = baseAmt * ratioTvom;
			lossRa   = baseAmt * ratioRa;
			
			lossEpv = ratioRa==0.0? 0.0: lossAmt - lossRa;
			break;
			
		case CLOSE:
			lossEpv = 0.0;
		default:
			break;
		}
		
		return RstLoss.builder().baseYymm(bssd)
						.gocId(gocId)
						.mstRollFwd(rstCsm.getMstRollFwd())
						.runsetId(rstCsm.getRunsetId())
						.mstCalc(rstCsm.getMstCalc())
						.seq(rstCsm.getSeq())
						.operatorType(rstCsm.getOperatorType())
						.boxAmt(rstCsm.getBoxAmt())
						.deltaCalcCsmAmt(rstCsm.getDeltaCalcCsmAmt())
						.calcCsmAmt(rstCsm.getCalcCsmAmt())
						.lossAmt(lossAmt)
						.lossEpv(lossEpv)
						.lossFaceAmt(lossFace)
						.lossTvom(lossTvom)
						.lossRa(lossRa)
						.remark("")
						.lastModifiedBy(GmvConstant.getLastModifier())
						.lastModifiedDate(LocalDateTime.now())
						.build();
	}
}