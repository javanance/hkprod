package com.gof.enums;

public enum ERScript {
	 SM_INTERPOL("AA")
	,SW_ALPHA(" SW.alpha.find <- function ( int, obs.mat, ufr, ufr.t                       #���� �ʼ� ������"
			+ "			                            ,min_alpha = 0.001, max_alpha = 1      #������ ������ ���� --> �⺻ 0.001, 1"
			+ "			                            ,tol = 0.0001, llp = max(obs.mat)      #��������, LLP ����"
			+ "			                            ,bse_dt = NA, real_date_tf = F         #���� ���� ���� ���� �ʿ䵥����"
			+ "			                            ,type = \"cont\"                       #�Է� �ݸ� Ÿ��(����, ���Ӻ���)"
			+ "							 )"
			+ "	{"
			+ "	  #ufr�� ���� ������ ��ȯ"
			+ "	  ufrc <- Int.disc.to.cont(ufr)"
			+ "	  #�ݸ� �����Ͱ� ���Ӻ����� �ƴ� ��� ���Ӻ����� ��ȯ"
			+ "	  if (type == \"cont\" || type == \"CONT\") { "
			+ "		rate <- int"
			+ "	  } else {rate <- Int.disc.to.cont(int)  }"
			+ "	  #���� ���ڸ� ��뿩�� üũ �� ���� ��ȯ"
			+ "	  if(real_date_tf == T & is.na(bse_dt) == F){"
			+ "    #���� ���ڸ� �������� ����ϴ� ���"
			+ "    time.tmp <- e.date(bse_dt, max(obs.mat)*12)"
			+ "    #������ ���ڸ� ����Ͽ� 365�� ���� ���� ���"
			+ "    mat <- time.tmp$DIFF_DATE[which(time.tmp$MONTH_SEQ%in%(12*obs.mat) == T)]/365"
			+ "	  } else {"
			+ "	    #���� ���ڰ� �ƴ� obs.mat�� �״�� ����ϴ� ���"
			+ "	    mat <- obs.mat"
			+ "	  }"
			+ "	  #Initializing"
			+ "	  temp.alpha <- round2((min_alpha + max_alpha)/2,6)"
			+ "	  extend <- round2((max_alpha - min_alpha)/2,6)"
			+ "	  weight <- Weight.sw(mat, mat, ufrc, temp.alpha)"
			+ "	  inv.weight <- solve(weight)"
			+ "	  loss <- Sw.loss(rate, mat, ufrc)"
			+ "	  zeta <- inv.weight%*%loss"
			+ "	  sinh <- sinh(mat*temp.alpha)"
			+ "	  q.mat <- diag(exp(-ufrc*mat))"
			+ "	  kappa <- (1+temp.alpha*mat%*%q.mat%*%zeta) / t(sinh)%*%q.mat%*%zeta"
			+ "	  approaching <- sign(temp.alpha/(1-kappa*exp(temp.alpha*llp))) #1ȸ�� ����"
			+ "	  direction <- sign(approaching*temp.alpha/(1-kappa*exp(temp.alpha*ufr.t))-tol)"
			+ "	  loop_idx <- 20   #���ĸ� ã�� ���ؼ� �� ���̳� �������� ���� ������ ����"
			+ "	  #Storage ����"
			+ "	  alpha.test <- rep(NA, loop_idx) ; alpha.test[1] <- temp.alpha"
			+ "	  kappa.test <- rep(NA, loop_idx) ; kappa.test[1] <- kappa"
			+ "	  dir.test   <- rep(NA, loop_idx) ; dir.test[1]   <- direction"
			+ "	  extend.test   <- rep(NA, loop_idx) ; extend.test[1] <- extend"
			+ "	  min.alpha.test <-rep(NA, loop_idx) ; min.alpha.test[1] <- min_alpha"
			+ "	  max.alpha.test <-rep(NA, loop_idx) ; max.alpha.test[1] <- max_alpha"
			+ "	  #Optimizing"
			+ "	  for (i in 2: loop_idx)  {"
			+ "	    if(direction == -1) { max_alpha <- max_alpha - extend}"
			+ "		else if (direction == 1) {min_alpha <- min_alpha + extend}"
			+ "     temp.alpha <- round2((min_alpha + max_alpha)/2,6)"
			+ "	    extend <- round2((max_alpha - min_alpha)/2,6)"
			+ "	    weight <- Weight.sw(mat, mat, ufrc, temp.alpha)"
			+ "	    inv.weight <- solve(weight)"
			+ "	    zeta <- inv.weight%*%loss"
			+ "	    sinh <- sinh(mat*temp.alpha)"
			+ "	    q.mat <- diag(exp(-ufrc*mat))"
			+ "	    kappa <- (1+temp.alpha*mat%*%q.mat%*%zeta) / t(sinh)%*%q.mat%*%zeta"
			+ "	    direction <- sign(approaching*temp.alpha/(1-kappa*exp(temp.alpha*ufr.t))-tol)"
			+ "	    alpha.test[i] <- temp.alpha"
			+ "	    kappa.test[i] <- kappa"
			+ "	    dir.test[i]   <- direction"
			+ "	    extend.test[i]   <- extend"
			+ "	    min.alpha.test[i] <- min_alpha"
			+ "	    max.alpha.test[i] <- max_alpha"
			+ "	  }"
			+ "	  alpha <- round2((min.alpha.test[loop_idx] + max.alpha.test[loop_idx])/2,6)"
			+ "	  return(alpha)"
			+ "	}"
			)
	,HULL_WHITE_1F("AA")
	,
	
	;
	
	private String script;
	private ERScript(String script) {
		this.script = script;
	}
	public String getScript() {
		return script;
	}
	
}
