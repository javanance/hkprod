
DNS.by.kalman <- function (int.ts.cont, obs.mat, dt, initial.paras, returnval  = F)
{
  len.data <- length(int.ts.cont[,1])

  lambda <- initial.paras[1]
  theta  <- initial.paras[2:4]
  kappa  <- initial.paras[5:7]
  s.set  <- initial.paras[8:13]
  eps    <- initial.paras[14]/1000
  s11    <- s.set[1]
  s12    <- s.set[2]
  s13    <- s.set[3]
  s22    <- s.set[4]
  s23    <- s.set[5]
  s33    <- s.set[6]

  #초기화 단계

  coef  <- t(Coef.gen.ns(lambda,obs.mat))

  #Eigen Value, Eigen Vector matrix Generating
  #다음은 금융감독원 로직으로 산출한 결과
  eig.tmp <- matrix(NA, 3,3 )
  eigen.idx <- 4-rank(kappa)
  eig.tmp[,1] <- eigen.idx ; eig.tmp[,3] <- kappa ; eig.tmp[,2] <- seq(1:3)
  eig.tmp.df  <- data.frame(eig.tmp) ; names(eig.tmp.df) <-  c("eig.idx","eig.seq","kappa")

  eig.tmp.df <- eig.tmp.df[with(eig.tmp.df, order(eig.idx)),]
  eigenvec <- matrix(0, 3,3)
  eigenvec[eig.tmp.df$eig.idx[1],eig.tmp.df$eig.seq[1]] <- 1 ; eigenvec[eig.tmp.df$eig.idx[2],eig.tmp.df$eig.seq[2]] <- 1 ; eigenvec[eig.tmp.df$eig.idx[3],eig.tmp.df$eig.seq[3]] <- 1
  t.eigvec <- t(eigenvec) ; inv.eigvec <- solve(eigenvec)

  f.eigval <- matrix(NA, 3,3)

  for (i in 1:3)
  {
    for (j in 1:3)
    {
      ii <- eig.tmp.df$kappa[i]
      jj <- eig.tmp.df$kappa[j]
      f.eigval[i,j] <- ii+jj
    }
  }

  #Phi Generating
  phi1 <- exp(-kappa*dt)            #상태방정식의 Transition 계산
  mat.phi1 <- diag(phi1)            #상태방정식의 Transition Matrix
  phi0 <- (1-phi1)*theta            #상태방정식의 상수항
  t.phi1 <- t(mat.phi1)             #Transition Matrix의 transformation. 단, Transition Matrix가 daig행렬이므로 동일하게 산출됨
  phi1tphi1 <- phi1%*%t(phi1)       #Sigma_hat 산출에 사용

  #Sigma matrix Generating
  sigmamat <- matrix( c(s11, s12, s13, 0, s22, s23, 0,0,s33 ), nrow = 3)

  smat <- inv.eigvec%*%sigmamat%*%t(sigmamat)%*%eigenvec
  vlim <- smat/f.eigval
  vmat <- smat*(1-exp(-f.eigval*dt))/f.eigval
  Q    <- eigenvec%*%vmat%*%t.eigvec              #갱신과정에서 적용되는 변수에 저장

  #Initial State Generating
  h.large <- diag( eps^2 , length(obs.mat))         #관측방정식의 분산 (R)
  initial.state <- theta                          #최초 상태방정식의 값은 Theta로 설정
  initial.sigma <- eigenvec%*%vlim%*%t.eigvec     #최초 상태방정식의 분산 설정

  prev.state <- initial.state                     #칼만필터 반복계산의 변수로 사용하기 위한 변수명 재설정
  prev.sigma <- initial.sigma                     #칼만필터 반복계산의 변수로 사용하기 위한 변수명 재설정


  state <- matrix(NA, len.data+1, 3)              #State 변동을 담기 위한 적재공간 설정
  state[1,] <- initial.state                      #최초 상태를 State의 첫 번째 행에 입력
  ll.idx <- rep(NA, len.data)                     #우도를 담기 위한 적재공간 생성

  #Apply Kalman Filter
  len.idx <- len.data+1
  for ( k in 1: len.data)
  {
    #예측 단계
    int.tmp <- as.vector(int.ts.cont[k,])
    xhat <- prev.state*phi1 + phi0
    sigma.hat <- Q + prev.sigma*phi1tphi1                    # t시점에서의 Omega값
    y.implied <- xhat%*%coef                                 # 모델에 의해서 예측된 t시점에서의 y값
    v <- as.vector(as.double(int.tmp - y.implied))           # y_t - y_t|t-1
    finv <- solve(t(coef)%*%sigma.hat%*%coef + h.large)      # t 시점에서의 Sigma 집합

    #갱신 단계
    next.sigma <- sigma.hat - sigma.hat%*%coef%*%finv%*%t(coef)%*%sigma.hat
    next.state <- xhat + v%*%finv%*%t(coef)%*%sigma.hat


    #우도함수의 산출
    #ll.idx.tmp <- t(v)%*%finv%*%v - log(det(finv))
    ll.idx.tmp <- t(v)%*%finv%*%v

    #산출된 값을 저장
    state[(k+1),] <- next.state
    ll.idx[k] <- ll.idx.tmp
    prev.state <- next.state
    prev.sigma  <- next.sigma
  }

  #Eror Function
  Logik <- (sum(ll.idx[1:len.data]) + length(int.tmp)*len.data*log(2*pi))/2   #우도함수 결과값 계산
  BIC <- 14*log(len.data)+2*Logik
  if ( returnval == F) {return(Logik)
  } else {return(state = next.state ) }
}

#DNS MODEL OPTIMIZE USING KALMAN FILTER
DNS.by.kalman.opt <- function( int.ts.cont, obs.mat, dt, initial.paras, accuracy = 1e-10 , method = "Nelder-Mead"
                               ,min.lambda = 0.005, max.lambda = 2 )

{
  len.data <- length(int.ts.cont[,1])
  cat(format(Sys.time(),usetz = T),"Calibration Start")

  kalman.rslt <- constrOptim(initial.paras, DNS.by.kalman,
                             ui = rbind ( c(1,0,0,0,0,0,0,0,0,0,0,0,0,0),  #Lower Bound Lambda >=min.lambda
                                          c(0,0,0,0,1,0,0,0,0,0,0,0,0,0),  #Lower Bound kappa11>=0
                                          c(0,0,0,0,0,1,0,0,0,0,0,0,0,0),  #Lower Bound kappa22>=0
                                          c(0,0,0,0,0,0,1,0,0,0,0,0,0,0),  #Lower Bound kappa33 >=0
                                          c(0,0,0,0,0,0,0,1,0,0,0,0,0,0),  #Lower Bound s11 >=0
                                          c(0,0,0,0,0,0,0,0,0,0,1,0,0,0),  #Lower Bound s22 >=0
                                          c(0,0,0,0,0,0,0,0,0,0,0,0,1,0),  #Lower Bound s33 >=0
                                          c(-1,0,0,0,0,0,0,0,0,0,0,0,0,0), #Upper Bound -Lambda >= -max.lambda
                                          c(0,0,0,0,0,0,0,-1,0,0,0,0,0,0), #Upper Bound -s11 >= -1
                                          c(0,0,0,0,0,0,0,0,0,0,-1,0,0,0), #Upper Bound -s22 >= -1
                                          c(0,0,0,0,0,0,0,0,0,0,0,0,-1,0)),#Upper Bound -s33 >= -1
                             ci = c(min.lambda,0,0,0,0,0,0,-max.lambda,-1,-1,-1),  #Boundary Values
                             outer.eps = accuracy,
                             method = method, control = list(maxit = 500, trace = F) ,
                             int.ts.cont = int.ts.cont, obs.mat = obs.mat,dt = dt, returnval = F
  )

  cat("\n",format(Sys.time(),usetz = T),"Calibration End")
  #산출된 최적 모수들을 저장
  opt.paras     <- kalman.rslt$par
  opt.ll.value  <- kalman.rslt$value
  opt.BIC       <- 14*log(len.data)+2*opt.ll.value
  opt.lambda    <- opt.paras[1]
  opt.theta     <- opt.paras[2:4]
  opt.kappa     <- opt.paras[5:7]
  opt.s.set     <- opt.paras[8:13]
  opt.eps       <- opt.paras[14]/1000
  state <- DNS.by.kalman(int.ts.cont =int.ts.cont, obs.mat = obs.mat, dt=dt, initial.paras = opt.paras, returnval = T)
  return(list (OPT.PARAS = opt.paras, LOGLIK = opt.ll.value, BIC = opt.BIC,state = state))
}

#DNS Shock Curve Generating
Dns.run <- function(int.full, obs.mat, ufr, ufr.t, int.type = "cont",
                    obs.term = 1/52 , VA = 0.0032,
                    max.lambda = 2, min.lambda = 0.05,
                    accuracy = 1e-10, method = "Nelder-Mead",
                    llp = max(obs.mat), conf.interval = 0.995,bse.dt )
{
  int.ts <- int.full[,-1]

  if (tolower(int.type) == "cont") { rate <- int.ts
  } else {rate <- Int.disc.to.cont(int.ts)
  }

  initial.lambda <- Dns.lambda.initialize (rate, obs.mat, max.lambda = 2, min.lambda = 0.05)
  initial.paras <- Dns.para.initialize(rate, obs.mat, initial.lambda ,dt = obs.term)

  opt.paras <- DNS.by.kalman.opt ( rate, obs.mat, obs.term, initial.paras,accuracy = accuracy , method = "Nelder-Mead", min.lambda = 0.005, max.lambda = 2 )

  params <- opt.paras$OPT.PARAS
  names(params) <- c("LAMBDA", "THETA_L","THETA_S","THETA_C","KAPPA_L","KAPPA_S","KAPPA_C","S11","S12","S13","S22","S23","S33","EPS(*1000)")
  state <- opt.paras$state
  names(state) <- c("STATE_L0","STATE_S0","STATE_C0")

  dns.shock <- DNS.shock.gen (rate, obs.mat ,state ,params, llp ,conf.interval = 0.995)

  shock.int.size <- dns.shock$SHOCK.INT.SIZE
  shock.size <- dns.shock$SHOCK.SIZE

  DNS_CURVE_RSLT <- DNS.sw.curve(rate, obs.mat, ufr, ufr.t,VA, shock.int.size, llp = llp, bse.dt )
  OBS_SHOCK_CURVE <- DNS_CURVE_RSLT$DNS_CURVE_OBS
  SHOCK_CURVE_ALPHA <- DNS_CURVE_RSLT$ALPHA
  BASE_CURVE <-  DNS_CURVE_RSLT$BASE_CURVE ; BASE_VA_CURVE <-  DNS_CURVE_RSLT$BASE_VA_CURVE
  MEAN_CURVE <-  DNS_CURVE_RSLT$MEAN_CURVE ; MEAN_VA_CURVE <-  DNS_CURVE_RSLT$MEANE_VA_CURVE
  UP_CURVE <-  DNS_CURVE_RSLT$UP_CURVE ; UP_VA_CURVE <-  DNS_CURVE_RSLT$UP_VA_CURVE
  DOWN_CURVE <-  DNS_CURVE_RSLT$DOWN_CURVE ; DOWN_VA_CURVE <-  DNS_CURVE_RSLT$DOWN_VA_CURVE
  STEEP_CURVE <-  DNS_CURVE_RSLT$STEEP_CURVE ; STEEP_VA_CURVE <-  DNS_CURVE_RSLT$STEEP_VA_CURVE
  FLAT_CURVE <-  DNS_CURVE_RSLT$FLAT_CURVE ; FLAT_VA_CURVE <-  DNS_CURVE_RSLT$FLAT_VA_CURVE

  return(list(PARAMETERS = params, STATE =  state,  STATE_SHOCK = shock.size, INT.SHOCK = shock.int.size ,
              OBS_SHOCK_CURVE = OBS_SHOCK_CURVE, SHOCK_CURVE_ALPHA = SHOCK_CURVE_ALPHA,
              BASE_CURVE = BASE_CURVE, BASE_VA_CURVE = BASE_VA_CURVE,
              MEAN_CURVE = MEAN_CURVE, MEAN_VA_CURVE = MEAN_VA_CURVE,
              UP_CURVE = UP_CURVE, UP_VA_CURVE = UP_VA_CURVE,
              DOWN_CURVE = DOWN_CURVE, DOWN_VA_CURVE = DOWN_VA_CURVE,
              STEEP_CURVE = STEEP_CURVE, STEEP_VA_CURVE = STEEP_VA_CURVE,
              FLAT_CURVE = FLAT_CURVE, FLAT_VA_CURVE = FLAT_VA_CURVE
              ))
}

#DNS_SHOCK GENERATING FUNCTION
DNS.shock.gen <- function (int.ts.cont, obs.mat ,state ,opt.paras, llp = max(obs.mat),conf.interval = 0.995)
{
  len.data <- length(int.ts.cont[,1])
  opt.lambda    <- opt.paras[1]
  opt.theta     <- opt.paras[2:4]
  opt.kappa     <- opt.paras[5:7]
  opt.s.set     <- opt.paras[8:13]

  opt.coef <- Coef.gen.ns(opt.lambda, obs.mat)

  opt.current.x0 <- state

  kappas <- diag(opt.kappa)
  i.kappas <- diag(1-exp(-opt.kappa))                           # (I-exp(-Kappa*t)), t = 1
  Mean.rev.shock <- i.kappas%*%as.vector(opt.theta - opt.current.x0)     # 평균회귀 충격 산출

  opt.sigma.mat <- matrix( c(opt.s.set[1], opt.s.set[2], opt.s.set[3], 0, opt.s.set[4], opt.s.set[5], 0,0,opt.s.set[6] ), nrow = 3)
  m.matrix <-  solve(kappas)%*%i.kappas%*%opt.sigma.mat         #M matrix 계산 (M)

  #만기에 따른 금리 조정
  obs.matset  <- seq(from = 1, to = llp, by = 1)

  coef.set.tmp <-   Coef.gen.ns(lambda = opt.lambda, maturity = obs.matset)
  weight.coef <- colSums(coef.set.tmp)

  N.matrix <- diag(colSums(coef.set.tmp))%*%m.matrix  #가중치 매트릭스 계산(N)
  #N.matrix <- colSums(coef.set.tmp)*m.matrix         #금융감독원 로직
  NTN <- t(N.matrix)%*%N.matrix                       #NTN 계산

  #PCA 분석을 통한 Shock Size 산출
  eigen.shock <- Eigen.fss(NTN)
  eig.vec <- eigen.shock$vectors  #Eigen Vector 2,3 부호가 금융감독원 엑셀과 부호 반대로 산출됨 --> 금융감독원 로직으로 설정
  eig.val <- eigen.shock$values

  me <- m.matrix%*%eig.vec        #Me Matrix의 1번 열이 Me_1, 2번 열이 Me_2

  #rotation
  rotation.sum1 <- me[,1]%*%weight.coef
  rotation.sum2 <- me[,2]%*%weight.coef

  rotation.tmp <- rotation.sum2/rotation.sum1
  rotation.agl <- atan(rotation.tmp)
  roattion.mat <- matrix(c(cos(rotation.agl), sin(rotation.agl), -sin(rotation.agl), cos(rotation.agl)),ncol = 2)

  #Shock Size Calculation
  SHOCK <- qnorm(conf.interval)*me[,1:2]%*%roattion.mat
  LEVEL.SHOCK <- SHOCK[,1]
  TWIST.SHOCK <- SHOCK[,2]
  MEAN.REV.SHOCK <- Mean.rev.shock

  #Shock int Size Calculation
  coef.int <- Coef.gen.ns (opt.lambda, obs.mat )
  LEVEL.INT.SHOCK <- coef.int%*%LEVEL.SHOCK
  TWIST.INT.SHOCK <- coef.int%*%TWIST.SHOCK
  MEAN.REV.INT.SHOCK <- coef.int%*%MEAN.REV.SHOCK

  SHOCK.SIZE <- matrix(0, 3,3)
  SHOCK.SIZE[,1] <- MEAN.REV.SHOCK ; SHOCK.SIZE[,2] <- LEVEL.SHOCK ; SHOCK.SIZE[,3] <- TWIST.SHOCK
  SHOCK.SIZE <- data.frame(SHOCK.SIZE) ; names(SHOCK.SIZE) <- c("MEAN.REV.SHOCK","LEVEL.SHOCK","TWIST.SHOCK")

  SHOCK.INT.SIZE <- matrix(0,length(obs.mat),3)
  SHOCK.INT.SIZE[,1] <- MEAN.REV.INT.SHOCK ; SHOCK.INT.SIZE[,2] <- LEVEL.INT.SHOCK ; SHOCK.INT.SIZE[,3] <- TWIST.INT.SHOCK
  SHOCK.INT.SIZE <- data.frame(SHOCK.INT.SIZE) ; names(SHOCK.INT.SIZE) <- c("MEAN.REV.SHOCK","LEVEL.SHOCK","TWIST.SHOCK")

  return(list ( SHOCK.INT.SIZE = SHOCK.INT.SIZE, SHOCK.SIZE = SHOCK.SIZE))
}



Antithetic.rand <- function (num_of_scen, num_of_variables = 1, seednum = NA)
{
  #Seednumber가 부여된 경우는 동일한 Seednumber를 사용하도록 설정함
  if(!is.na(seednum)){
    set.seed(as.integer(seednum))
  }

  odd <- num_of_scen%%2
  scen_gen_no <- num_of_scen%/%2 + odd

  z <- matrix(NA, scen_gen_no,num_of_variables)
  w <- matrix(NA, (scen_gen_no*2),num_of_variables)

  if(odd == 0){ #산출되는 시나리오의 갯수가 짝수인 경우
  for (i in 1: num_of_variables)
  {
    z[,i] <-  scale(rnorm(scen_gen_no)) #짝수인 경우 난수의 평균을 0으로 만들어 주기 위해 스케일링함
  }
    for (i in 1: scen_gen_no){
    w[(i*2-1),] <- z[i,]
    w[(i*2),] <- -z[i,]
    }
    value <- w[1:num_of_scen,]          #짝수인 경우 두 개의 짝 시나리오가 부호만 반대로 동일하게 산출된다.

  } else if (odd == 1){                 #산출되는 시나리오의 갯수가 홀수인 경우
  for (i in 1: num_of_variables)
  {
    z[,i] <-  rnorm(scen_gen_no) }
    for (i in 1: scen_gen_no){
      w[(i*2-1),] <- z[i,]
      w[(i*2),] <- -z[i,]
    }
   if (num_of_variables == 1) {            #시나리오의 갯수가 홀수이면서 산출 난수가 1개인 케이스
   value <- w[-(num_of_scen+1)]
   value <- scale(value)                   #홀수인 경우 난수의 평균을 0으로 만들어 주기 위해 스케일링함
   value <-value[,]                        #홀수인 경우 두개의 짝 시나리오가 유사한 수치로 산출된다.
   }

   } else (num_of_variables !=1)           #시나리오의 갯수가 홀수이면서 산출 난수가 여러개인 케이스
    { value <- w[-(num_of_scen+1), ]
      value <- scale(value)
      value <- value[,]
    }
 return(value)
}

#월말 날자 계산 함수
e.date <- function ( base_dt , length.month)
{
  bse_yr <-  substr(base_dt,1,4)
  base.year <- substr(base_dt,1,4)
  base.month <- substr(base_dt,5,6)
  base.date <- substr(base_dt, 7,8)

  month.set <- formatC(seq(1:12),width = 2, flag = "0")
  date.set <- c(31,28,31,30,31,30,31,31,30,31,30,31)

  first.month.set <- month.set[month.set > base.month]
  len.first.month.set <- length(first.month.set)
  first.year.set <- rep(base.year, len.first.month.set)
  first.date.set <- date.set[month.set > base.month]

  dt.set <- matrix(0, length.month, 3) ; dt.set <- data.frame(dt.set)
  names(dt.set) <- c("YEAR","MONTH","DATE")

  if(len.first.month.set>=1) {dt.set[1:len.first.month.set,] <- cbind(first.year.set, first.month.set, first.date.set)}

  idx.mos <- (length.month-len.first.month.set)%/%12


  for (i in 1:idx.mos)
  {
    base.year <- as.double(base.year)+1
    dt.set[(len.first.month.set+1+(i-1)*12):(len.first.month.set+(i)*12),] <- cbind(base.year, month.set, date.set)
  }

  last.month.size <-length.month -  (len.first.month.set+idx.mos*12)
  last.year.set <- rep(as.double(base.year)+1,last.month.size)
  last.month.set <- month.set[1:last.month.size]
  last.date.set <- date.set[1:last.month.size]

  if(last.month.size >=1){
    dt.set[(len.first.month.set+1+(idx.mos)*12):length.month,] <- cbind(last.year.set, last.month.set, last.date.set)}

  #윤년조정
  row.num <- row.names(subset(dt.set, as.double(YEAR)%%4==0 & MONTH == "02", select = DATE))
  if(length(row.num)>=1){  dt.set[row.num,]$DATE <- "29"    }

  #100년 단위 조정
  row.num <- row.names(subset(dt.set, as.double(YEAR)%%100==0 & MONTH == "02", select = DATE))
  if(length(row.num)>=1){  dt.set[row.num,]$DATE <- "28"}

  YYYYMMDD <- paste0(dt.set$YEAR,dt.set$MONTH,dt.set$DATE)
  YYYY_MM_DD <-as.Date(paste(dt.set$YEAR,dt.set$MONTH,dt.set$DATE, sep = "-"))
  DIFF_DATE  <- as.double(YYYY_MM_DD - as.Date(paste(bse_yr,base.month,base.date, sep = "-")))
  MONTH_SEQ <- seq(1:length.month)
  #MAT_CD = paste0("M",formatC(MONTH_SEQ,width = 4, flag = "0"))
  MAT_CD <- Mat.to.mat.code(MONTH_SEQ, len = 4, where = "head")
  return(list (YYYYMMDD = YYYYMMDD, YYYY_MM_DD = YYYY_MM_DD, DIFF_DATE=DIFF_DATE, MONTH_SEQ = MONTH_SEQ, MAT_CD = MAT_CD))
}

#Mat_code Utility
Mat.to.mat.code <- function(maturity,
                            split = "M", len = 4, where = "head")
{
  matseq <- formatC(maturity,digit = (len-1), flag = "0" )
  if (tolower(where) == "tail"){
    mat_code <- paste0(matseq,split)
  }else if (tolower(where) == "head")
  {mat_code <- paste0(split,matseq)
  } else cat(" where varialble shoule be head or tail")
  return(mat_code)
}

#Mat_code Utility
Mat.code.to.mat <- function(mat_code,
                            split = "M", len = 4, where = "head")
{
  if (tolower(where) == "tail"){
    maturity <- substr(mat_code,(nchar(split)+1),(len+nchar(split)) )
  }else if (tolower(where) == "head")
  { maturity <- substr(mat_code,(1+nchar(split)),(len+nchar(split)))
  } else cat("where varialble shoule be head or tail")
  mat <- as.numeric(maturity)
  return(mat)
}

#Int Conversion 함수 설정
Int.disc.to.cont <- function(int_disc)
{ #Discrete 금리를 Contiunuous 금리로 변환
  int_cont <- log(1+int_disc)
  return(int_cont)
}

#Int Conversion 함수 설정
Int.cont.to.disc <- function(int_cont)
{ #Contiunuous 금리를 Discrete 금리로 변환
  int_disc <- exp(int_cont)-1
  return(int_disc)
}

#Round 함수 재정의
round2 <- function(x, digit)
{ #R에서 정의된 round 함수는 0.5를 내림하는 경우가 있기 때문에 round함수 재정의
  value <- round(x + 10*.Machine$double.eps,digit)
  return(value)
  #R에서 double 형의 숫자의 정확도에 문제가 있기 때문에 double 형의 가장 작은 숫자를 더해 숫자를 보정
  #round(1.5) ; round(2.5) 를 확인
  #round(1.5 ) ==  round(2.5)
  #round(1.5 + 10*.Machine$double.eps) ==  round(2.5 + 10*.Machine$double.eps)
}

#Smith Wilson Alpha Finding Function Define
SW.alpha.find <- function ( rate, maturity, ufrc, ufr.t              #산출 필수 데이터
                            ,min.alpha = 0.001, max.alpha = 1        #알파의 구간을 지정 --> 기본 0.001, 1
                            ,tol = 0.0001, llp = 20                  #수렴정도, LLP 지정
)
{
  #Initializing
  temp.alpha <- round2((min.alpha + max.alpha)/2,6)
  extend <- round2((max.alpha - min.alpha)/2,6)
  weight <- Weight.sw(maturity, maturity, ufrc, temp.alpha)
  inv.weight <- solve(weight)
  loss <- Sw.loss(rate, maturity, ufrc)
  zeta <- inv.weight%*%loss
  sinh <- sinh(maturity*temp.alpha)
  q.mat <- diag(exp(-ufrc*maturity))
  kappa <- (1+temp.alpha*maturity%*%q.mat%*%zeta) / t(sinh)%*%q.mat%*%zeta
  approaching <- sign(temp.alpha/(1-kappa*exp(temp.alpha*llp))) #1회만 산출
  direction <- sign(approaching*temp.alpha/(1-kappa*exp(temp.alpha*ufr.t))-tol)


  loop_idx <- 20   #알파를 찾기 위해서 몇 번이나 루프문을 돌릴 것인지 결정

  #Storage 생성
  alpha.test <- rep(NA, loop_idx+1) ; alpha.test[1] <- temp.alpha
  kappa.test <- rep(NA, loop_idx+1) ; kappa.test[1] <- kappa
  dir.test   <- rep(NA, loop_idx+1) ; dir.test[1]   <- direction
  extend.test   <- rep(NA, loop_idx+1) ; extend.test[1] <- extend
  min.alpha.test <-rep(NA, loop_idx+1) ; min.alpha.test[1] <- min.alpha
  max.alpha.test <-rep(NA, loop_idx+1) ; max.alpha.test[1] <- max.alpha

  #Optimizing
  for (i in 2: (loop_idx+1))
  {
    if(direction == -1) { max.alpha <- max.alpha - extend
    } else if (direction == 1) {min.alpha <- min.alpha + extend}
    temp.alpha <- round2((min.alpha + max.alpha)/2,6)
    extend <- round2((max.alpha - min.alpha)/2,6)
    weight <- Weight.sw(maturity, maturity, ufrc, temp.alpha)
    inv.weight <- solve(weight)
    zeta <- inv.weight%*%loss
    sinh <- sinh(maturity*temp.alpha)
    q.mat <- diag(exp(-ufrc*maturity))
    kappa <- (1+temp.alpha*maturity%*%q.mat%*%zeta) / t(sinh)%*%q.mat%*%zeta
    direction <- sign(approaching*temp.alpha/(1-kappa*exp(temp.alpha*ufr.t))-tol)

    alpha.test[i] <- temp.alpha
    kappa.test[i] <- kappa
    dir.test[i]   <- direction
    extend.test[i]   <- extend
    min.alpha.test[i] <- min.alpha
    max.alpha.test[i] <- max.alpha
  }
  alpha <- round2((min.alpha.test[loop_idx] + max.alpha.test[loop_idx])/2,6)
  return(alpha)
}

#Smith-Wilson Weight Function
Weight.sw <- function(obs.mat, proj.mat, ufrc, alpha)
{
  obs.mat2 <- matrix(rep(obs.mat, length(proj.mat)),ncol = length(proj.mat) )
  proj.mat2 <- t(matrix(rep(proj.mat, length(obs.mat)), ncol = length(obs.mat) ))
  pmin <- pmin(obs.mat2,proj.mat2)
  pmax <- pmax(obs.mat2,proj.mat2)
  weight <- exp( -ufrc*(obs.mat2 + proj.mat2)) * (alpha*pmin-0.5*exp(-alpha*pmax)*(exp(alpha*pmin)-exp(-alpha*pmin)))
  return(weight)
}

#Smith-Wilson Base Function Define
##Smith - Wilson Model Loss function
Sw.loss <- function (int.rate, mat, ufrc)
{
  ZCB.sw <- exp (-mat*int.rate)
  sw.loss <- ZCB.sw - exp(-mat*ufrc)
  return(sw.loss)
}


#Smith-Wilson Run
SW.run <- function( obs.int, obs.mat, ufr, ufr.t,           #관측금리, 관측만기, UFR, 수렴시점
                    proj.y = 100, term = 1/12, alpha = NA,  #산출만기(년), 산출주기, alpha(없으면 찾아서 입력하게 됨)
                    min.alpha = 0.001, max.alpha = 1, tol = 0.0001, llp = max(obs.mat), #최소알파, 최대알파, 수렴점에서의 차이
                    bse.dt = NA, real.date.tf = F, int.type = "cont",  #기준일자, 실제일수 사용여부, 관측금리 타입(cont, disc)
                    ts.proj.tf = F, num.diff = T )                     #Time Series하여 Fwd 산출여부, 수치미분여부
{

  #ufr을 연속 복리로 변환
  ufrc <- Int.disc.to.cont(ufr)

  #금리 데이터가 연속복리가 아닌 경우 연속복리로 변환
  if (tolower(int.type) == "cont") { rate <- obs.int
  } else {rate <- Int.disc.to.cont(obs.int)
  }

  #금리 산출 term을 정의
  if(is.numeric(term)  ) { dt <- term               #term이 숫자로 정의되어 있는 경우 term을 그대로 사용
  } else if (tolower(term) == "m") { dt <- 1/12     #term이 문자 M으로 정의되어 있는 경우 dt를 1/12 적용
  } else if (tolower(term) == "q") { dt <- 1/4      #term이 문자 Q로 정의되어 있는 경우 dt를 1/4 적용
  } else if (tolower(term) == "y") { dt <- 1        #term이 문자 Y로 정의되어 있는 경우 dt를 1 적용
  } else cat("Term is wrong")

  #실제 일자 사용여부 체크 후 만기 변환
  #날짜 계산 방법에 따른 보간 일자를 계산
  if(real.date.tf == T & is.na(bse.dt) == F){ #실제 일자를 기준으로 계산하는 경우
    #보간만기
    time.tmp <- e.date(bse.dt, proj.y*2*12)
    PROJ.SET <- time.tmp$DIFF_DATE[time.tmp$MONTH_SEQ%%(12*dt) == 0]/365 #월말의 날자를 계산하여 365로 나눈 일할 방식
    MONTH.SEQ <- time.tmp$MONTH_SEQ

    maturity <- time.tmp$DIFF_DATE[which(time.tmp$MONTH_SEQ%in%(12*obs.mat) == T)]/365
  } else { #실제 일자가 아닌 dt 단위로 날짜를 계산
    #보간만기
    PROJ.SET <- seq(dt, proj.y*2, dt) #dt 단위로 더하는 방식
    MONTH.SEQ <- round2(seq(dt, proj.y*2, dt)*12,0)

    #관측만기
    maturity <- obs.mat
  }
  #보간 만기 및 만기 코드를 설정
  time.set <- data.frame(PROJ.SET, MONTH.SEQ)


  #알파값이 존재하지 않는 경우 알파값을 산출
  if(is.na(alpha)){
    alpha <- SW.alpha.find( rate, maturity, ufrc, ufr.t, min.alpha = min.alpha, max.alpha = max.alpha, tol = tol, llp = llp)
    #알파값이 있는 경우 알파를 그대로 적용
  } else alpha <- alpha

  #SW 결과를 산출
  RSLT <- Smith.Wilson(rate, maturity, proj.y, time.set, ufrc, alpha, ts.proj.tf, num.diff)
  SW_CURVE <- RSLT$SW.RSLT
  TS_PROJ_CURVE<- RSLT$TS.PROJ.DISC

  return(list (SW_CURVE = SW_CURVE,TS_PROJ_CURVE = TS_PROJ_CURVE,ALPHA = alpha))
}

#Smith-Wilson inter-Outer polation Function
Smith.Wilson <- function (rate, maturity, proj.y, time.set, ufrc, alpha, ts.proj.tf = F, num.diff = T)
{
  PROJ.SET <- time.set$PROJ.SET
  MONTH.SEQ <- time.set$MONTH.SEQ

  mat.t <- length(PROJ.SET)
  fwd.cont <- rep(NA, mat.t)
  loss <- Sw.loss(rate, maturity, ufrc)
  weight <- Weight.sw(maturity, maturity, ufrc, alpha)
  weight.inv <- solve(weight)
  zeta <- weight.inv%*%loss
  tempmatrix <- t(Weight.sw(maturity, PROJ.SET, ufrc, alpha))%*%zeta
  price.sw <- as.double(exp(-ufrc*PROJ.SET)+tempmatrix)
  spot.cont <- as.double(-log(price.sw)/PROJ.SET)
  spot.disc <- Int.cont.to.disc(spot.cont)
  tem.length <- length(price.sw)

  #num.diff T 인 경우 수치미분값으로 SW 산출
  #그렇지 T가 아닌 경우 미분식으로 SW 산출
  if(num.diff == T)
  {
  fwd.cont[1] <- spot.cont[1]
  fwd.cont[2:tem.length] <- as.double(log(price.sw[1:(tem.length-1)]/price.sw[2:tem.length])/(PROJ.SET[2:tem.length] - PROJ.SET[1:(tem.length-1)]) )
  fwd.disc <- Int.cont.to.disc(fwd.cont)
  } else
    {tempmatrix2 <- (-ufrc*exp(-ufrc*PROJ.SET)+t(Sw.deriv.wilson(maturity,PROJ.SET, ufrc, alpha))%*%zeta)
    fwd.cont <- -tempmatrix2/price.sw
    fwd.disc <- Int.cont.to.disc(fwd.cont)
    }

  if(ts.proj.tf == T){
    ts.proj.cont <- matrix(NA, (mat.t/2), (mat.t/2))
    for (i in 1: (mat.t/2)) {
      timeset.tmp <- PROJ.SET[(i+1):(mat.t/2+i)] - PROJ.SET[i]
      price.base <- price.sw[i]
      price.tmp <- price.sw[(i+1):(mat.t/2+i)] /price.base
      ts.proj.cont[,i] <- as.double((1/timeset.tmp)*log(1/price.tmp))
    }
    ts.proj.disc <- data.frame(Int.cont.to.disc(ts.proj.cont))
    month_seq <- MONTH.SEQ[1:(mat.t/2)]
    names(ts.proj.disc)[1:(mat.t/2)] <- paste0("TS_DISC_",month_seq,"M_AFT")
    TS.PROJ.DISC <- data.frame(TIME =timeset.tmp, MONTH_SEQ = month_seq,ts.proj.disc)

  }else {TS.PROJ.DISC <- NULL}

  SW.RSLT <- data.frame(TIME = PROJ.SET, MONTH_SEQ = MONTH.SEQ, SPOT_CONT = spot.cont, SPOT_DISC = spot.disc, DISCOUNT = price.sw,FWD_CONT = fwd.cont ,FWD_DISC = fwd.disc)
  SW.RSLT <- SW.RSLT[1:(length(SW.RSLT[,1])/2),]

  return(list (SW.RSLT = SW.RSLT, TS.PROJ.DISC = TS.PROJ.DISC))
}

#Nelson-Siegel Model Coefficients Generating Function
Coef.gen.ns <- function(lambda, maturity){
  Z <- cbind(maturity/maturity,
             (1-exp(-lambda*maturity))/(lambda*maturity),
             (1-exp(-lambda*maturity))/(lambda*maturity) - exp(-lambda*maturity))
  names(Z)
  return (Z)
}

#Eigen Value and Vector Generating Function
#diag matrix의 Eigenvector가 이론상 해당 행렬의 차원 수 까지 산출 될 수 있기 때문에 표준모델의 산출 결과와 일치시키기 위해 작성하였음
#3x3 행렬의 eigenvalue, eigenvector를 산출하기 위한 보조함수임
Eigen.fss <- function(matrix)
{
  determ <- det(matrix)
  eig <- eigen(matrix)
  eig.val  <- eig$values       #Eigen Value는 다른 값이 나올 수 없으므로 그대로 적용
  eig.vec1 <- eig$vectors[,1]  #Eigen Vector 1번을 고정함

  #Eigen Vector의 경우 부호만 바꾸어 여러 개 나올 수 있음. 이에 따라 금융감독원 로직을 적용하여 Eigen Vector를 산출
  NTN.temp2 <- matrix - diag(eig.val[2],3)
  eig2.temp <- c(det(NTN.temp2[2:3,1:2]),det(cbind(NTN.temp2[3,1:2],NTN.temp2[1,1:2])),det(NTN.temp2[1:2,1:2]))
  eig.vec2 <- eig2.temp/sqrt(sum(eig2.temp^2))

  #Eigen Vector의 경우 부호만 바꾸어 여러 개 나올 수 있음. 이에 따라 금융감독원 로직을 적용하여 Eigen Vector를 산출
  NTN.temp3 <- matrix - diag(eig.val[3],3)
  eig3.temp <- c(det(NTN.temp3[2:3,1:2]),det(cbind(NTN.temp3[3,1:2],NTN.temp3[1,1:2])),det(NTN.temp3[1:2,1:2]))
  eig.vec3 <- eig3.temp/sqrt(sum(eig3.temp^2))

  values <- eig.val
  vectors <- cbind(eig.vec1, eig.vec2, eig.vec3)

  #Determinanat가 0 인 경우 해당 함수에서 에러가 발생함. Error 발생 시 Eigen 함수에서 나온 값을 적용함
  if( sum(is.nan(vectors))>0) {vectors <- eig$vectors}

  return(list(values = values, vectors = vectors))
}

#AFNS_INDEP_SHOCK GENERATING FUNCTION
Afns.shock.gen <- function (int.ts.cont, obs.mat ,state ,opt.paras, llp = max(obs.mat),conf.interval = 0.995)
{
  len.data <- length(int.ts.cont[,1])
  opt.lambda    <- opt.paras[1]
  opt.theta     <- opt.paras[2:4]
  opt.kappa     <- opt.paras[5:7]
  opt.s.set     <- opt.paras[8:10]

  opt.coef <- Coef.gen.ns(opt.lambda, obs.mat)

  opt.current.x0 <- state

  kappas <- diag(opt.kappa)
  i.kappas <- diag(1-exp(-opt.kappa))                           # (I-exp(-Kappa*t)), t = 1
  Mean.rev.shock <- i.kappas%*%as.vector(opt.theta - opt.current.x0)     # 평균회귀 충격 산출

  opt.sigma.mat <- diag(opt.s.set)
  m.matrix <-  solve(kappas)%*%i.kappas%*%opt.sigma.mat         #M matrix 계산 (M)

  #만기에 따른 금리 조정
  obs.matset  <- seq(from = 1, to = llp, by = 1)

  coef.set.tmp <-   Coef.gen.ns(lambda = opt.lambda, maturity = obs.matset)
  weight.coef <- colSums(coef.set.tmp)

  N.matrix <- diag(colSums(coef.set.tmp))%*%m.matrix  #가중치 매트릭스 계산(N)
  NTN <- t(N.matrix)%*%N.matrix                       #t(N)N 계산

  #PCA 분석을 통한 Shock Size 산출
  eigen.shock <- Eigen.fss(NTN)
  eig.vec <- eigen.shock$vectors  #Eigen Vector 2,3 부호가 금융감독원 엑셀과 부호 반대로 산출됨 --> 금융감독원 로직으로 설정
  eig.val <- eigen.shock$values

  me <- m.matrix%*%eig.vec        #Me Matrix의 1번 열이 Me_1, 2번 열이 Me_2

  #rotation
  rotation.sum1 <- me[,1]%*%weight.coef
  rotation.sum2 <- me[,2]%*%weight.coef

  rotation.tmp <- rotation.sum2/rotation.sum1
  rotation.agl <- atan(rotation.tmp)
  roattion.mat <- matrix(c(cos(rotation.agl), sin(rotation.agl), -sin(rotation.agl), cos(rotation.agl)),ncol = 2)

  #Shock Size Calculation
  SHOCK <- qnorm(conf.interval)*me[,1:2]%*%roattion.mat
  LEVEL.SHOCK <- SHOCK[,1]
  TWIST.SHOCK <- SHOCK[,2]
  MEAN.REV.SHOCK <- Mean.rev.shock

  #Shock int Size Calculation
  coef.int <- Coef.gen.ns (opt.lambda, obs.mat )
  LEVEL.INT.SHOCK <- coef.int%*%LEVEL.SHOCK
  TWIST.INT.SHOCK <- coef.int%*%TWIST.SHOCK
  MEAN.REV.INT.SHOCK <- coef.int%*%MEAN.REV.SHOCK

  SHOCK.SIZE <- matrix(0, 3,3)
  SHOCK.SIZE[,1] <- MEAN.REV.SHOCK ; SHOCK.SIZE[,2] <- LEVEL.SHOCK ; SHOCK.SIZE[,3] <- TWIST.SHOCK
  SHOCK.SIZE <- data.frame(SHOCK.SIZE) ; names(SHOCK.SIZE) <- c("MEAN.REV.SHOCK","LEVEL.SHOCK","TWIST.SHOCK")

  SHOCK.INT.SIZE <- matrix(0,length(obs.mat),3)
  SHOCK.INT.SIZE[,1] <- MEAN.REV.INT.SHOCK ; SHOCK.INT.SIZE[,2] <- LEVEL.INT.SHOCK ; SHOCK.INT.SIZE[,3] <- TWIST.INT.SHOCK
  SHOCK.INT.SIZE <- data.frame(SHOCK.INT.SIZE) ; names(SHOCK.INT.SIZE) <- c("MEAN.REV.SHOCK","LEVEL.SHOCK","TWIST.SHOCK")

  return(list ( SHOCK.INT.SIZE = SHOCK.INT.SIZE, SHOCK.SIZE = SHOCK.SIZE))
}
#Using Shock level and SW, generating Shock curve
DNS.sw.curve <- function(rate, obs.mat, ufr, ufr.t,
                         VA = 0.0032 , shock.int.size ,
                         llp = max(obs.mat), bse.dt )
{
term <-  1/12
int.type <- "cont"
alpha = NA
real.date.tf <- T
ts.proj.tf <- F
min.alpha <- 0.001
max.alpha <- 1
tol <- 0.0001
proj.y <- 100


BASE_INT <- as.double(rate[length(rate[,1]),]) ; BASE_INT_VA <- BASE_INT + VA
MEAN_REV_INT <- BASE_INT + shock.int.size$MEAN.REV.SHOCK ; MEAN_REV_INT_VA <- MEAN_REV_INT + VA
UP_INT <- BASE_INT + shock.int.size$LEVEL.SHOCK ; UP_INT_VA <- UP_INT + VA
DOWN_INT <- BASE_INT - shock.int.size$LEVEL.SHOCK ; DOWN_INT_VA <- DOWN_INT + VA
STEEP_INT <- BASE_INT + shock.int.size$TWIST.SHOCK ; STEEP_INT_VA <- STEEP_INT + VA
FLAT_INT <- BASE_INT - shock.int.size$TWIST.SHOCK ; FLAT_INT_VA <- FLAT_INT + VA
DNS_CURVE_OBS <- data.frame(BASE_INT,BASE_INT_VA,MEAN_REV_INT, MEAN_REV_INT_VA,UP_INT, UP_INT_VA,DOWN_INT, DOWN_INT_VA,STEEP_INT, STEEP_INT_VA,FLAT_INT,FLAT_INT_VA )


BASE <- SW.run(BASE_INT, obs.mat, ufr, ufr.t, proj.y, term, alpha,min.alpha, max.alpha, tol, llp,bse.dt, real.date.tf, int.type, ts.proj.tf)
BASE.VA <- SW.run(BASE_INT_VA, obs.mat, ufr, ufr.t, proj.y, term, alpha,min.alpha, max.alpha, tol, llp,bse.dt, real.date.tf, int.type, ts.proj.tf)

MEAN <- SW.run(MEAN_REV_INT, obs.mat, ufr, ufr.t, proj.y, term, alpha,min.alpha, max.alpha, tol, llp,bse.dt, real.date.tf, int.type, ts.proj.tf)
MEAN.VA <- SW.run(MEAN_REV_INT_VA, obs.mat, ufr, ufr.t, proj.y, term, alpha,min.alpha, max.alpha, tol, llp,bse.dt, real.date.tf, int.type, ts.proj.tf)

UP <- SW.run(UP_INT, obs.mat, ufr, ufr.t, proj.y, term, alpha,min.alpha, max.alpha, tol, llp,bse.dt, real.date.tf, int.type, ts.proj.tf)
UP.VA <- SW.run(UP_INT_VA, obs.mat, ufr, ufr.t, proj.y, term, alpha,min.alpha, max.alpha, tol, llp,bse.dt, real.date.tf, int.type, ts.proj.tf)

DOWN <- SW.run(DOWN_INT, obs.mat, ufr, ufr.t, proj.y, term, alpha,min.alpha, max.alpha, tol, llp,bse.dt, real.date.tf, int.type, ts.proj.tf)
DOWN.VA <- SW.run(DOWN_INT_VA, obs.mat, ufr, ufr.t, proj.y, term, alpha,min.alpha, max.alpha, tol, llp,bse.dt, real.date.tf, int.type, ts.proj.tf)

STEEP <- SW.run(STEEP_INT, obs.mat, ufr, ufr.t, proj.y, term, alpha,min.alpha, max.alpha, tol, llp,bse.dt, real.date.tf, int.type, ts.proj.tf)
STEEP.VA <- SW.run(STEEP_INT_VA, obs.mat, ufr, ufr.t, proj.y, term, alpha,min.alpha, max.alpha, tol, llp,bse.dt, real.date.tf, int.type, ts.proj.tf)

FLAT <- SW.run(FLAT_INT, obs.mat, ufr, ufr.t, proj.y, term, alpha,min.alpha, max.alpha, tol, llp,bse.dt, real.date.tf, int.type, ts.proj.tf)
FLAT.VA <- SW.run(FLAT_INT_VA, obs.mat, ufr, ufr.t, proj.y, term, alpha,min.alpha, max.alpha, tol, llp,bse.dt, real.date.tf, int.type, ts.proj.tf)

nametag <- c("BASE","MEAN","UP","DOWN","STEEP","FLAT","BASE_VA","MEAN_VA","UP_VA","DOWN_VA","STEEP_VA","FLAT_VA")

ALPHA <- data.frame(BASE$ALPHA, MEAN$ALPHA, UP$ALPHA,DOWN$ALPHA,STEEP$ALPHA,FLAT$ALPHA,
                    BASE.VA$ALPHA,MEAN.VA$ALPHA,UP.VA$ALPHA,DOWN.VA$ALPHA,STEEP.VA$ALPHA,FLAT.VA$ALPHA)
names(ALPHA) <- nametag

BASE_CURVE <- BASE$SW_CURVE   ; BASE_VA_CURVE <- BASE.VA$SW_CURVE
MEAN_CURVE <- MEAN$SW_CURVE   ; MEAN_VA_CURVE <- MEAN.VA$SW_CURVE
UP_CURVE <- UP$SW_CURVE       ; UP_VA_CURVE <- UP.VA$SW_CURVE
DOWN_CURVE <- DOWN$SW_CURVE   ; DOWN_VA_CURVE <- DOWN.VA$SW_CURVE
STEEP_CURVE <- STEEP$SW_CURVE ; STEEP_VA_CURVE <- STEEP.VA$SW_CURVE
FLAT_CURVE <- FLAT$SW_CURVE   ; FLAT_VA_CURVE <- FLAT.VA$SW_CURVE

return(list (DNS_CURVE_OBS = DNS_CURVE_OBS, ALPHA = ALPHA,
             BASE_CURVE = BASE_CURVE, BASE_VA_CURVE = BASE_VA_CURVE, MEAN_CURVE = MEAN_CURVE, MEAN_VA_CURVE = MEAN_VA_CURVE,
             UP_CURVE = UP_CURVE, UP_VA_CURVE = UP_VA_CURVE, DOWN_CURVE = DOWN_CURVE, DOWN_VA_CURVE = DOWN_VA_CURVE,
             STEEP_CURVE = STEEP_CURVE, STEEP_VA_CURVE = STEEP_VA_CURVE, FLAT_CURVE = FLAT_CURVE, FLAT_VA_CURVE = FLAT_VA_CURVE))
}
#SWAP_RATE_GENERATING_FUNCTION
swaprate <- function(swint, swaption.Maturities, swap.Tenors, N = 4)
{
  dt <- 1/N
  swap.mat.len <- length(swaption.Maturities)
  swap.ten.len <- length(swap.Tenors)
  swaption.swapmat.mkt.str <- rep(swaption.Maturities, swap.ten.len)  #스왑션 만기 인덱스
  swaption.swapmat.spl.fin <- rep(NA, length(swaption.swapmat.mkt.str)) #스왑션 테너 + 만기 인덱스

  #스왑션의 최종 종료일자를 계산
  for (i in 1 : swap.mat.len)
  {
    for (j in 1: swap.ten.len)
    {
      swaption.swapmat.spl.fin[((i-1)*swap.ten.len+j)] <- swaption.Maturities[i] + swap.Tenors[j]
    }
  }

  price.str <- rep(NA, (swap.mat.len*swap.ten.len))
  price.fin <- rep(NA, (swap.mat.len*swap.ten.len))

  for (i in 1 : (swap.mat.len*swap.ten.len)) {
    price.str[i] <- as.double(as.vector(subset(swint, as.vector(TIME) == swaption.swapmat.mkt.str[i], select = DISCOUNT)  ))
    price.fin[i] <- as.double(as.vector(subset(swint, as.vector(TIME) == swaption.swapmat.spl.fin[i], select = DISCOUNT)  ))
  }

  sum.price <- rep(NA, (swap.mat.len*swap.ten.len))

  for (i in 1 : (swap.mat.len*swap.ten.len)) {
    sum.price[i] <- sum(as.vector(subset(swint, as.vector(TIME) > swaption.swapmat.mkt.str[i]&as.vector(TIME) <= swaption.swapmat.spl.fin[i], select = DISCOUNT) ))
  }
  swap.rate <-  (price.str- price.fin)/(sum.price/N)

  swaprate <- matrix(swap.rate, nrow = length(swaption.Maturities), dimnames = list(swaption.Maturities, swap.Tenors))
  sump <- matrix(sum.price, nrow = length(swaption.Maturities), dimnames = list(swaption.Maturities, swap.Tenors))

  SWAP.INFO <- data.frame(SWAPTION_MAT = swaption.swapmat.mkt.str,SWAP_TENOR = swaption.swapmat.spl.fin-swaption.swapmat.mkt.str
                          ,swap.rate, sum.price)

  return(list (SWAP_RATE = swaprate, SUM_PRICE = sump, SWAP_INFO= SWAP.INFO))
}

#Swaption 가치 산출 함수
Atm.Eu.swaption <- function (swint, vol.info,swaption.Maturities, swap.Tenors, N = 4, L = 100)
{
  swap.rates <- swaprate(swint, swaption.Maturities, swap.Tenors, N)
  swap.info <- swap.rates$SWAP_INFO
  swaption.price <- rep(NA,length(swap.info$SWAPTION_MAT))
  for ( i in 1 : length(swap.info$SWAPTION_MAT))
  {
    swaption.mat.tmp <- swap.info$SWAPTION_MAT[i]
    swap.tenor.tmp <- swap.info$SWAP_TENOR[i]
    sumprice.tmp <- swap.info$sum.price[i]
    swaprate.tmp <- swap.info$swap.rate[i]
    vol.tmp <- as.double(subset(vol.info, SWAPTION_MAT == swaption.mat.tmp & SWAP_TENOR == swap.tenor.tmp, select = VOL)$VOL)
    d1.tmp <- vol.tmp^2*(swaption.mat.tmp/2)/(vol.tmp*sqrt(swaption.mat.tmp))
    d2.tmp <- -vol.tmp^2*(swaption.mat.tmp/2)/(vol.tmp*sqrt(swaption.mat.tmp))
    nd1.tmp <- pnorm(-d1.tmp)
    nd2.tmp <- pnorm(-d2.tmp)
    swaption.price[i] <- L/N*sumprice.tmp*(swaprate.tmp*nd2.tmp - swaprate.tmp*nd1.tmp)
  }
  BLACK.SWAPTION <- data.frame(swap.info, SWAPTION_PRICE = swaption.price)
  return(BLACK.SWAPTION)
}


#HW1F PARAMS CALIBRATION
Hw.calibration<-function(obs.int,obs.mat,
                         vol.info,swaption.Maturities,swap.Tenors,bse.ym = NA,int.type = "cont",
                         N = 4,L = 100,ufr = 0.045,ufr.t = 60,term = 1/N,llp = max(obs.mat), accuracy = 1e-10){

  proj.y <- max(swaption.Maturities)+max(swap.Tenors)
  alpha <- NA
  min.alpha<-0.001
  max.alpha<-1
  tol<-0.0001

  if (tolower(int.type) == "cont") { rate<-obs.int} else {rate<-Int.disc.to.cont(obs.int) ; int.type<-"cont"}

  #Swap Rate,Price
  swint<-SW.run (rate,obs.mat,ufr,ufr.t,proj.y,term,alpha,min.alpha,max.alpha,tol,llp,NA,F,int.type,F)$SW_CURVE
  swap.info<-swaprate(swint,swaption.Maturities,swap.Tenors,N)$SWAP_INFO
  swption.price <-Atm.Eu.swaption(swint,vol.info ,swaption.Maturities,swap.Tenors,N,L)

  #구간별 알파, 변동성 사용
  set.seed(obs.int[1])
  a.v.sectional<-matrix(NA,length(swaption.Maturities),2)
  initial.para.set <- c(0.02,0.02)
  for (i in 1 : length(swaption.Maturities))
  {
    swap.info.tmp<-subset(swap.info,SWAPTION_MAT == swaption.Maturities[i])
    swption.price.tmp<-subset(swption.price,SWAPTION_MAT == swaption.Maturities[i])
    calib.tmp <-
      constrOptim(initial.para.set,Hw1f.para.opt.full,method = "Nelder-Mead",
      ui = rbind(c(1,0),c(-1,0),c(0,1),c(0,-1)),ci = c(0.0001,-1,0,-1),
      #outer.eps = 100,control = list(maxit = 30),
      control = list(maxit = 200, trace = F, reltol = accuracy),
      swint = swint,swap.info = swap.info.tmp,swption.price = swption.price.tmp,N = N ,L = L)
    a.v.sectional[i,]<- calib.tmp$par
  }

  #단일 알파사용, 변동성만 구간별 추정
  initial.para.set2<-c(mean(a.v.sectional[,1]),a.v.sectional[,2]) #초기 모수값을 구간 추정값으로 설정
  #initial.para.set2<-rep(0.02, 7)
  set.seed(obs.int[1])
  calib.v.only.sec<-
    constrOptim(initial.para.set2,Hw1f.para.opt.v.only.sec,method = "Nelder-Mead",
    ui = rbind(c(1,0,0,0,0,0,0),c(-1,0,0,0,0,0,0),
      c(0,1,0,0,0,0,0),c(0,-1,0,0,0,0,0),c(0,0,1,0,0,0,0),c(0,0,1,0,0,0,0),
      c(0,0,0,1,0,0,0),c(0,0,0,-1,0,0,0),c(0,0,0,0,1,0,0),c(0,0,0,0,-1,0,0),
      c(0,0,0,0,0,1,0),c(0,0,0,0,0,-1,0),c(0,0,0,0,0,0,1),c(0,0,0,0,0,0,1)),
    ci = c(0.0001,-1,0,-1,0,-1,0,-1,0,-1,0,-1,0,-1),
    #outer.eps = 100,control = list(maxit = 30),
    control = list(maxit = 200, trace = F, reltol = accuracy),
    swint = swint,swap.info = swap.info,swption.price = swption.price,N = N,L= L)
  v.only.sectional<-calib.v.only.sec$par

  param.rslt <- data.frame(matrix(NA,1,6))
  names(param.rslt)<-c("BSE_YM","IR_MODEL_TYP","PARAM_CALC_CD","PARAM_TYP_CD","MATURITY","PARAM_VAL")
  A <- "ALPHA";B <- "SIGMA"
  idx <- length(swaption.Maturities)
  #20191211수정
  #param.mat <- c(swaption.Maturities,llp)[-1]
  param.mat <- c(swaption.Maturities)

  #AV_Sectional rslt
  param.rslt[1:(idx*2),3] <- "FULL_LOCAL_CALIB"
  param.rslt[1:(idx),4] <- A
  param.rslt[(idx+1):(idx*2),4] <- B
  param.rslt[(idx+1):(idx*2),6] <- a.v.sectional[,2]
  param.rslt[1:(idx*2),5] <- param.mat
  param.rslt[1:(idx),6] <-  a.v.sectional[,1]

  #V_Sectional rslt
  param.rslt[(idx*2+1):(idx*3+1),3] <- "SIGMA_LOCAL_CALIB"
  param.rslt[(idx*2+1):(idx*3+1),4] <- c(A,rep(B,idx))
  param.rslt[(idx*2+1):(idx*3+1),6] <- v.only.sectional
  param.rslt[(idx*2+1):(idx*3+1),5] <- c(llp,param.mat)
  param.rslt[,1] <- bse.ym
  param.rslt[,2] <- "HW1"
  return(param.rslt)
}

# FULL_LOCAL_CALIB and FULL Calibration and AV Sectional opt function
Hw1f.para.opt.full<-function(swint,swap.info,swption.price,N,L,paraset)
{ #gc()
  swaption.hw1f<-rep(NA,length(swap.info$SWAPTION_MAT))
  swaption.price.hw1<-rep(NA,length(swaption.hw1f))
  for ( i in 1: length(swaption.hw1f))
  {
    t1<-as.double(swap.info$SWAPTION_MAT[i])
    t2<-as.double(swap.info$SWAP_TENOR[i])
    swaption.price.hw1[i]<-swaption.HW.fn (swint,swap.info,t1,t2,paraset,N,L)
  }
  ERROR<-sum(((swption.price$SWAPTION_PRICE - swaption.price.hw1)/swption.price$SWAPTION_PRICE)^2)
  return(ERROR)
}

#SIGMA_LOCAL_CALIB opt Function
Hw1f.para.opt.v.only.sec<-function(swint,swap.info,swption.price,N,L,paraset2)
{ gc()
  swaption.hw1f<-rep(NA,length(swap.info$SWAPTION_MAT))
  swaption.price.hw1<-rep(NA,length(swaption.hw1f))

  for ( i in 1: length(swaption.hw1f))
  {
    t1<-as.double(swap.info$SWAPTION_MAT[i])
    t2<-as.double(swap.info$SWAP_TENOR[i])
    paraset<-c(paraset2[1],paraset2[(which(unique(swap.info$SWAPTION_MAT)==t1)+1)] )
    swaption.price.hw1[i]<-swaption.HW.fn (swint,swap.info,t1,t2,paraset,N,L)
  }
  ERROR<-sum(((swption.price$SWAPTION_PRICE - swaption.price.hw1)/swption.price$SWAPTION_PRICE)^2)
  return(ERROR)
}

#HW1F Model Output layout
Hw1f.output <- function(bse.dt,simulation.output,scen.name = NA ,proj.y = 100,num.of.scen = 1000)
{
  month.seq <- simulation.output$MONTH_SEQ
  num.time <- length(month.seq)
  scen.bucekt <- seq(1:num.of.scen)

  #필요한 변수들을 입력
  discount <- simulation.output$sim.p.t
  spot.cont <- simulation.output$Rt.cont
  spot.disc <- simulation.output$Rt.disc
  fwd.cont <- simulation.output$r.t
  fwd.disc <- simulation.output$Fwd.disc

  layout <- matrix(NA, num.time*num.of.scen,10)
  layout <- data.frame(layout)

  #Layout에 맞추어 변환
  for( i in 1: num.of.scen)
  { #i <- 1
    scen.tmp <- scen.bucekt[i]
    discout.tmp <- discount[,i]
    spot.cont.tmp <- spot.cont[,i]
    spot.disc.tmp <- spot.disc[,i]
    fwd.cont.tmp <- fwd.cont[,i]
    fwd.disc.tmp <- fwd.disc[,i]
    tmp <- data.frame(bse.dt,scen.name, scen.tmp,simulation.output$TIME,month.seq, spot.cont.tmp,spot.disc.tmp, discout.tmp,fwd.cont.tmp, fwd.disc.tmp,stringsAsFactors = F)
    layout[((i-1)*num.time+1):(i*num.time),(1:10)] <- tmp
  }
  layout <- data.frame(layout, stringsAsFactors = F)
  names(layout) <- c("BSE_DT","SCEN_ID","SCEN_NO","TIME","MONTH_SEQ","SPOT_CONT","SPOT_DISC","DISCOUNT","FWD_CONT","FWD_DISC")
  return(layout)
}

#Simpson Integral Function
simpson.integral <- function(FUN, lower, upper, n = 100, ...)
{
  h <- (upper-lower)/n
  xs <- seq(from = lower, to = upper, by = h)

  #
  fun.val <- rep(NA, length(xs))
  for (i in 1 : (n+1))
  {
    fun.val[i] <- FUN(lower+h*(i-1))
  }

  inter.val <- rep(NA, length(xs)-1)
  for ( i in 1 : n)
  {
    inter.val[i] <- FUN(lower + h*(i-1)+h/2)
  }

  value <- (fun.val[1:n] + 4*inter.val + fun.val[2:(n+1)])*h/6
  integrated.value <- sum(value)
  return(integrated.value)
}

#HW1F MODEL PARAMETERS ADJUSTMENT FUNCTION
Hw1f.param.adj <- function (params, proj.y = NA)
{
  if(is.na(proj.y)) {proj.y <- max(params$MAT_CD)}
  #proj.y가 없는 경우 모수코드에 있는 만기 코드를 사용
  time.set <- seq(1/12, proj.y, 1/12)
  time.seq <- seq(1:(proj.y*12))

  #Alpha, Sigma 모수를 테이블에서 분리
  alpha.set <- subset(params, PARAM_TYP_CD == "ALPHA")
  sigma.set <- subset(params, PARAM_TYP_CD == "SIGMA")
  #만기코드별로 정렬
  alpha.set <- alpha.set[order(alpha.set$MAT_CD),]
  sigma.set <- sigma.set[order(sigma.set$MAT_CD),]

  #저장공간 생성
  alpha <- rep() ; sigma <- rep()

  #ALPHA SET
  idx.num <- 1
  for ( i in 1: length(time.set))
  {
    mat.tmp <- time.seq[i]
    mat.idx <- alpha.set[idx.num,]$MAT_CD
    alpha[i] <- alpha.set[idx.num,]$APPL_PARAM_VAL
    if (round2(mat.tmp,2) >= mat.idx & round2(mat.tmp,2) < max(alpha.set$MAT_CD) ){idx.num <- idx.num+1}
  }

  #SIGMA SET
  idx.num <- 1
  for ( i in 1: length(time.set))
  {
    mat.tmp <- time.seq[i]
    mat.idx <- sigma.set[idx.num,]$MAT_CD
    sigma[i] <- sigma.set[idx.num,]$APPL_PARAM_VAL
    if (round2(mat.tmp,2) >= mat.idx & round2(mat.tmp,2) < max(sigma.set$MAT_CD) ){idx.num <- idx.num+1}
  }


  param.set <- data.frame(time.set, time.seq, alpha, sigma)
  names(param.set) <- c("MATURITY","MONTH_SEQ","ALPHA","SIGMA")
  return(param.set)
}

#Hull and White 1F model Simulation Run
Hw1f.simulation.run <- function(bse.ym, obs.int, obs.mat, params, LP = 0,
                                int.type = "cont", num.of.scen = 1000, scen.name = NA,
                                ufr = 0.045, ufr.t = 60, proj.y = 100 , llp = max(obs.mat),
                                sw.aft.hw1.tf = F )
{
  if(tolower(int.type) != "cont")
  { rate <-Int.disc.to.cont(obs.int + LP); int.type <- "cont"
  }else rate <- obs.int + LP

  param.set <- Hw1f.param.adj(params, proj.y)
  simulation.output <- Hw1f.simulation(rate, obs.mat, param.set, bse.ym, num.of.scen, "cont" ,ufr, ufr.t, proj.y, llp )
  hw1f.rslt <- Hw1f.output(bse.ym,simulation.output,scen.name,proj.y,num.of.scen)

  if(sw.aft.hw1.tf == T)
  { num.time <- proj.y *12
    rslt <- data.frame(matrix(NA, num.time*num.of.scen,10))
    for (i in 1: num.of.scen)
    {
      sw.tmp  <- subset(hw1f.rslt, MONTH_SEQ <= (llp*12) & SCEN_NO == i)$SPOT_CONT
      sw.tmp.time  <- subset(hw1f.rslt, MONTH_SEQ <= (llp*12) & SCEN_NO == i)$TIME
      sw.rslt.tmp <- SW.run(sw.tmp, sw.tmp.time, ufr, ufr.t, proj.y, 1/12, NA, int.type = "cont")
      tmp <- data.frame(bse.ym, scen.name, i, sw.rslt.tmp$SW_CURVE, stringsAsFactors = F)
      rslt[((i-1)*num.time+1):(i*num.time),(1:10)] <- tmp
    }
    names(rslt) <- names(hw1f.rslt)
  } else rslt <- hw1f.rslt

  return(rslt)
}

#Hull and White 1F model Simulation Run
Hw1f.simulation.run.rd <- function(bse.ym, obs.int, obs.mat, params, randnum, LP = 0,
                                int.type = "cont", num.of.scen = 1000, scen.name = NA,
                                ufr = 0.045, ufr.t = 60, proj.y = 100 , llp = max(obs.mat),
                                sw.aft.hw1.tf = F )
{
  if(tolower(int.type) != "cont")
  { rate <-Int.disc.to.cont(obs.int + LP); int.type <- "cont"
  }else rate <- obs.int + LP

  param.set <- Hw1f.param.adj(params, proj.y)
  simulation.output <- Hw1f.simulation.rd(rate, obs.mat, param.set, randnum, "cont" ,ufr, ufr.t, proj.y, llp )
  hw1f.rslt <- Hw1f.output(bse.ym,simulation.output,scen.name,proj.y,num.of.scen)

  if(sw.aft.hw1.tf == T)
  { num.time <- proj.y *12
  rslt <- data.frame(matrix(NA, num.time*num.of.scen,10))
  for (i in 1: num.of.scen)
  {
    sw.tmp  <- subset(hw1f.rslt, MONTH_SEQ <= (llp*12) & SCEN_NO == i)$SPOT_CONT
    sw.tmp.time  <- subset(hw1f.rslt, MONTH_SEQ <= (llp*12) & SCEN_NO == i)$TIME
    sw.rslt.tmp <- SW.run(sw.tmp, sw.tmp.time, ufr, ufr.t, proj.y, 1/12, NA, int.type = "cont")
    tmp <- data.frame(bse.ym, scen.name, i, sw.rslt.tmp$SW_CURVE, stringsAsFactors = F)
    rslt[((i-1)*num.time+1):(i*num.time),(1:10)] <- tmp
  }
  names(rslt) <- names(hw1f.rslt)
  } else rslt <- hw1f.rslt

  return(rslt)
}

Hw1f.simulation.rd <- function(obs.int, obs.mat, param.set, randnum , int.type = "cont",
                            ufr = 0.045, ufr.t = 60, proj.y = 100 , llp = max(obs.mat) )
{
  dt <- 1/12
  sim.dt <- proj.y/dt #시뮬레이션 길이 생성

  sw <- SW.run(obs.int, obs.mat, ufr, ufr.t, proj.y, 1/12, NA, 0.001, 1, 0.0001, llp, int.type = int.type)
  swint <- sw$SW_CURVE  #Sw curve 생성
  r0 <- swint$SPOT_CONT[1]
  fwd.rate <- swint$FWD_CONT

  #param.set
  time.set  <- param.set$MATURITY
  alpha.set <- param.set$ALPHA
  sigma.set <- param.set$SIGMA

  #Theta 저장공간 생성
  theta.t <- rep(NA, sim.dt)
  exp.theta.t <- rep(NA, sim.dt)

  for (i in 1: sim.dt)
  {
    spd.tmp <- alpha.set[i] ; sigma.tmp <- sigma.set[i]
#   theta.t[i] <- (fwd.rate[i+1] - fwd.rate[i])/(spd.tmp*dt) + fwd.rate[i] + (sigma.tmp^2)*(1-exp(-2*spd.tmp*dt))/(2*spd.tmp)
    theta.t[i] <- (fwd.rate[i+1] - fwd.rate[i])/(spd.tmp*dt) + fwd.rate[i] + (sigma.tmp^2)*(1-exp(-2*spd.tmp*i*dt))/(2*spd.tmp)
  }

  r.t <- matrix(NA, (sim.dt), length(randnum[1,]))    #Short Rate 적재공간 생성
  r.t[1,] <- r0                                       #가장 첫 Short Rate는  r0로 설정
  exp.r.t <- rep(NA, (sim.dt))
  exp.r.t[1] <- mean(r.t[1,])

  #Short Rate 산출
  for (i in 2: (sim.dt))
  {
    spd.tmp <- alpha.set[i-1] ; sigma.tmp <- sigma.set[i-1]
    r.t[i,] <- r.t[(i-1),] + spd.tmp*(theta.t[i-1] - r.t[(i-1),])*dt + sigma.tmp*sqrt((1-exp(-2*spd.tmp*dt))/(2*spd.tmp))*randnum[(i-1),]
    exp.r.t[i] <- mean(r.t[i,])
  }


  #Discount Rate 산출
  exp.sim.p.t <-   rep(NA, (sim.dt+1))
  sim.p.t <-  matrix(NA, (sim.dt+1), num.of.scen)
  sim.p.t[1,] <- 1
  exp.sim.p.t[1] <- 1

  #Short Rate 기반 Discount Rate 산출
  for (i in 2: (sim.dt+1))
  {
    sim.p.t[i,] <- sim.p.t[i-1,]*exp(-r.t[i-1,]*dt)
  }
  sim.p.t <- sim.p.t[-1,]

  #Short Rate 기반 Spot Rate(Continuous) 산출
  Rt.cont <- matrix(NA, (sim.dt), num.of.scen)
  for (i in 1: (sim.dt))
  {
    Rt.cont[i,] <- -log(sim.p.t[i,])/time.set[i]
  }

  Rt.disc <- Int.cont.to.disc(Rt.cont)
  Fwd.disc <- Int.cont.to.disc(r.t)

  return(list (TIME = swint$TIME,MONTH_SEQ = swint$MONTH_SEQ, r.t = r.t, sim.p.t = sim.p.t, Rt.cont = Rt.cont, Rt.disc = Rt.disc, Fwd.disc=Fwd.disc))
}

#Hull and White Simulation Module
Hw1f.simulation <- function(obs.int, obs.mat, param.set,
                            bse.ym = NA, num.of.scen = 1000, int.type = "cont",
                            ufr = 0.045, ufr.t = 60, proj.y = 100 , llp = max(obs.mat) )
{
  dt <- 1/12

# 1년 추가 시뮬레이션 후 입력은 1년 삭제
  proj.y <- proj.y + 1
  sim.dt <- proj.y/dt #시뮬레이션 길이 생성
  sim.fin.dt <- (proj.y-1)/dt   #최종 입력 길


  sw <- SW.run(obs.int, obs.mat, ufr, ufr.t, proj.y, 1/12, NA, 0.001, 1, 0.0001, llp, int.type = int.type)
  swint <- sw$SW_CURVE  #Sw curve 생성
  r0 <- swint$SPOT_CONT[1]
  fwd.rate <- swint$FWD_CONT[-1]
  randnum <- Antithetic.rand(num.of.scen,sim.dt,bse.ym) #난수 생성

  #param.set
  param.set <- Hw1f.param.adj(params, proj.y)
  time.set  <- param.set$MATURITY[-c(sim.dt)]
  alpha.set <- param.set$ALPHA[-c(sim.dt)]
  sigma.set <- param.set$SIGMA[-c(sim.dt)]

  #Theta 저장공간 생성
  theta.t <- rep(NA, sim.dt)
  exp.theta.t <- rep(NA, sim.dt)

# sigma 20년 이후 고정
#  for (i in 1: sim.dt)
#  {
#    spd.tmp <- alpha.set[i]
#    if(i < 240)
#    {
#    sigma.tmp <- sigma.set[i]
#    }
#    else sigma.tmp <- sigma.set[i]
#    theta.t[i] <- (fwd.rate[i+1] - fwd.rate[i])/(spd.tmp*dt) + fwd.rate[i] + (sigma.tmp^2)*(1-exp(-2*spd.tmp*i*dt))/(2*spd.tmp^2)
#  }

# old version
  for (i in 1: sim.dt)
  {
    spd.tmp <- alpha.set[i] ; sigma.tmp <- sigma.set[i]
    theta.t[i] <- (fwd.rate[i+1] - fwd.rate[i])/(spd.tmp*dt) + fwd.rate[i] + (sigma.tmp^2)*(1-exp(-2*spd.tmp*i*dt))/(2*spd.tmp^2)
  }

  r.t <- matrix(NA, (sim.dt), num.of.scen)       #Short Rate 적재공간 생성
  r.t[1,] <- r0                                  #가장 첫 Short Rate는  r0로 설정
  exp.r.t <- rep(NA, (sim.dt))
  exp.r.t[1] <- mean(r.t[1,])

  #Short Rate 산출

# sigma 20년 이후 고정
#  for (i in 2: (sim.dt))
#  {
#    spd.tmp <- alpha.set[i-1]
#    if(i < 241)
#    {
#    sigma.tmp <- sigma.set[i-1]
#    }
#    else sigma.tmp <- sigma.set[i-1]
#    r.t[i,] <- r.t[(i-1),] + spd.tmp*(theta.t[i-1] - r.t[(i-1),])*dt + sigma.tmp*sqrt(dt)*randnum[,i-1]
#    exp.r.t[i] <- mean(r.t[i,])
#  }

# old version
  for (i in 2: (sim.dt))
  {
    spd.tmp <- alpha.set[i-1] ; sigma.tmp <- sigma.set[i-1]
  #  r.t[i,] <- r.t[(i-1),] + spd.tmp*(theta.t[i-1] - r.t[(i-1),])*dt + sigma.tmp*sqrt((1-exp(-2*spd.tmp*dt))/(2*spd.tmp))*randnum[,i-1]
    r.t[i,] <- r.t[(i-1),] + spd.tmp*(theta.t[i-1] - r.t[(i-1),])*dt + sigma.tmp*sqrt(dt)*randnum[,i-1]
    exp.r.t[i] <- mean(r.t[i,])
  }

#   for (i in 2: (sim.dt))
#   {
#     spd.tmp <- alpha.set[i-1] ; sigma.tmp <- sigma.set[i-1]
#     r.t[i,] <- r.t[(i-1),] + spd.tmp*(theta.t[i-1] - r.t[(i-1),])*dt + sigma.tmp*sqrt(dt)*randnum[,i-1]
#     exp.r.t[i] <- mean(r.t[i,])
#   }



  #Discount Rate 산출
  exp.sim.p.t <-   rep(NA, (sim.dt+1))
  sim.p.t <-  matrix(NA, (sim.dt+1), num.of.scen)
  sim.p.t[1,] <- 1
  exp.sim.p.t[1] <- 1

  #Short Rate 기반 Discount Rate 산출
  for (i in 2: (sim.dt+1))
  {
    sim.p.t[i,] <- sim.p.t[i-1,]*exp(-r.t[i-1,]*dt)
  }
  sim.p.t <- sim.p.t[-1,]

  #Short Rate 기반 Spot Rate(Continuous) 산출
  Rt.cont <- matrix(NA, (sim.dt), num.of.scen)
  for (i in 1: (sim.dt))
  {
    Rt.cont[i,] <- -log(sim.p.t[i,])/time.set[i]
  }

  Rt.disc <- Int.cont.to.disc(Rt.cont)
 Fwd.disc <- Int.cont.to.disc(r.t)

  TIME = swint$TIME[1:sim.fin.dt]

  MONTH_SEQ = swint$MONTH_SEQ[1:sim.fin.dt]

  Fwd.disc <- Fwd.disc[1:sim.fin.dt,]
  sim.p.t  <- sim.p.t[1:sim.fin.dt,]
  Rt.cont  <- Rt.cont[1:sim.fin.dt,]
  Rt.disc  <- Rt.disc[1:sim.fin.dt,]
  r.t      <- r.t[1:sim.fin.dt,]

  return(list (TIME = TIME,MONTH_SEQ = MONTH_SEQ, r.t = r.t, sim.p.t = sim.p.t, Rt.cont = Rt.cont, Rt.disc = Rt.disc, Fwd.disc=Fwd.disc))
}
#AFNS Model C(t,T) Calc Funtion
Afns.ctT <- function(lambda, t1,t2, sigma.mat)
{
  #중간변수 선언
  sigma11 <- sigma.mat[1,1]
  sigma21 <- sigma.mat[2,1]
  sigma31 <- sigma.mat[3,1]

  sigma12 <- sigma.mat[1,2]
  sigma22 <- sigma.mat[2,2]
  sigma32 <- sigma.mat[3,2]

  sigma13 <- sigma.mat[1,3]
  sigma23 <- sigma.mat[2,3]
  sigma33 <- sigma.mat[3,3]

  tT <- (t2-t1)
  inv.lam <- 1/lambda
  inv.lam2 <- inv.lam^2
  inv.lam3 <- inv.lam^3
  decay <- exp(-lambda*tT)
  decay2 <- exp(-2*lambda*tT)
  coef2 <- (1-decay)/tT
  coef22 <- (1-decay2)/tT

  first  <- sigma11^2*(tT^2)/6
  second <- (sigma21^2 + sigma22^2)*(0.5*inv.lam2 - inv.lam3*coef2 + 0.25*inv.lam3*coef22)
  third  <- (sigma31^2 + sigma32^2 + sigma33^2)*(0.5*inv.lam2 + inv.lam2*decay - 0.25*inv.lam*tT*decay2 - 0.75*inv.lam2*decay2 - 2*inv.lam3*coef2 + 0.625*inv.lam3*coef22)
  fourth <- (sigma11*sigma21 + sigma12*sigma22 + sigma13*sigma23)*(0.5*inv.lam*tT + inv.lam2*decay - inv.lam3*coef2)
  fifth  <- (sigma11*sigma31 + sigma12*sigma32 + sigma13*sigma33)*(3*inv.lam2*decay + 0.5*inv.lam*tT + inv.lam*tT*decay - 3*inv.lam3*coef2)
  sixth  <- (sigma21*sigma31 + sigma22*sigma32 + sigma23*sigma33)*(inv.lam2 + inv.lam2*decay - 0.5*inv.lam2*decay2 - 3*inv.lam3*coef2 + 0.75*inv.lam3*coef22)

  CtT <- first+second+third+fourth+fifth+sixth
  return(CtT)
}
#HULL AND WHITE 1F MODEL SWAPTION PRICING FUNCTION
swaption.HW.fn <- function (swint,swap.info,t1, t2, paraset, N = 4, L = 100)
{
  a <- paraset[1]
  v <- paraset[2]
  #HW1F 채권 가격 산출 함수 설정
  ZCB.HW1 <- function (swint,t1,t2,a,v, rt = NA)
  {
    f.mkt.a0 <- as.double(subset(swint, select = SPOT_CONT)[1,1]) #tt1 = 0인 경우 짧은 금리를 사용

    #B(t,T)산출 function 설정
    #t시점에서 T시점까지 hull and White 모델의 B()값을 선언한다
    B.HW1 <- function (t1,t2,a)
    {
      B.Hw<- 1/a*(1-exp(-a*(t2-t1)))
      return(B.Hw)
    }

    A.HW1 <- function (swint,t1,t2,a,v)
    {
      f.mkt.a0 <- as.double(subset(swint, select = SPOT_CONT)[1,1])
      A <- rep(NA, length(t2))
      for ( i in 1: length(t2))
      {
        tt1 <- t1[i] ; tt2 <- t2[i]
        if (tt1 == 0) {
          p1 <- 1 #tt1 = 0인경우 시장가 1
          f.mkt.a <- f.mkt.a0 #tt1 = 0인 경우 짧은 금리를 사용
        } else if (tt1 > 0 ) {
          p1 <- as.double(subset(swint, select =  DISCOUNT,TIME == tt1))
          f.mkt.a <- as.double(subset(swint, TIME == tt1, select = SPOT_CONT))
        }
        p2 <- as.double(subset(swint, select = DISCOUNT, TIME == tt2)[1,1])
        A[i] <- (p2/p1)*exp(B.HW1(tt1,tt2,a)*f.mkt.a-v^2/(4*a)*(1-exp(-2*a*tt1))*(1/a*(1-exp(-a*(tt2-tt1))))^2)
      }
      return(A)
    }

    if(is.na(rt)) {rt <- as.double(subset(swint, select = SPOT_CONT)[1,1])}
    ZCB <- A.HW1(swint,t1,t2,a,v)*exp(-B.HW1(t1,t2,a)*rt)
    return(ZCB)
  }

  #HW1F 스왑션 가치 산출 함수 설정
  #Swaption의 Cash Flow 시점
  Swaption.CF.Time <- function( tt1, tt2, dt = 1/4)
  {
    TT <- tt1 + tt2
    cf.time <- seq(from = (tt1+dt), to = TT, by = dt)
    return(cf.time)
  }

  #CashFlow 발생 시점에서의 Cf Size를 산출
  ci.HW1 <- function(tt1, tt2, dt = 1/4, K)
  {
    cf.t <- Swaption.CF.Time(tt1, tt2, dt)
    ci <- rep(dt*K, length(cf.t))
    ci[length(cf.t)] <- 1 + dt*K
    return(ci)
  }

  #Hw1F 변동성 산출 함수 설정
  V.HW1 <- function (tt1, tt2, a, v)
  {
    cf.time <- Swaption.CF.Time(tt1, tt2)
    cf.dif <- cf.time- tt1
    v.HW <- rep(NA, length(cf.time))
    v.HW <- sqrt(((exp(-a*tt1) - exp(-a*(tt1+cf.dif)))^2)*(v*v)*(exp(2*a*tt1)-1)/(2*a*a*a))
    return(v.HW)
  }

  #r.star Finding Function
  Findrstar.HW1 <- function (r, tt1, tt2, swint)
  { #r <- K
    ci <- ci.HW1(tt1, tt2, K = K)
    cf.time2 <- Swaption.CF.Time(tt1, tt2)
    cf.time1 <- rep(tt1, length(cf.time2))
    temp <- ci*ZCB.HW1(swint, cf.time1,cf.time2,a,v, r)
    Error <- (sum(temp)-1)^2
    return(Error)
  }

  #Swaption Strike 정보를 input
  K <- as.double(subset(swap.info, select = swap.rate, SWAPTION_MAT == t1 &SWAP_TENOR == t2))
  v.hw.tmp <- V.HW1 (t1, t2, a, v)

  #3.2 Swaption 산출을 위한 r.star 산출 (Jamshidian Decomposition)
  r.star <- optim(K, Findrstar.HW1,
                  method ="Brent", upper = 1, lower = -1
                  ,tt1 = t1, tt2 = t2, swint = swint )$par

  #3.3 Swaption 산출을 위한 Setting
  tt2 <- Swaption.CF.Time(t1, t2)
  tt1 <- rep(t1, length(tt2))
  tt0 <- rep(0, length(tt2))
  rt <- rep (r.star, length(tt2))

  ZCB.HW.tmp <- ZCB.HW1(swint,tt0, tt2, a, v)
  ZCB.HW0.tmp <- ZCB.HW1(swint,tt0, tt1, a, v)

  cf.0 <- tt1 #스왑션 만기의 시점
  cf.i <- tt2 #스왑션 Cashflow 시점
  ci <- ci.HW1(t1, t2, dt = 1/N, K)

  #현금흐름 시점에서 채권의 가치(현금흐름의 할인율)
  xi.tmp <- ZCB.HW1(swint,cf.0, cf.i, a, v, r.star)
  # 확률변수
  dip.tmp <- (1/v.hw.tmp)*log(ZCB.HW.tmp/(ZCB.HW0.tmp*xi.tmp)) + (v.hw.tmp/2)
  dim.tmp <- (1/v.hw.tmp)*log(ZCB.HW.tmp/(ZCB.HW0.tmp*xi.tmp)) - (v.hw.tmp/2)
  # 확률누적분포
  ndip.tmp <- pnorm(dip.tmp)
  ndim.tmp <- pnorm(dim.tmp)
  temp <- L*ci*(ZCB.HW.tmp*ndip.tmp-xi.tmp*ZCB.HW0.tmp*ndim.tmp) #현금흐름 시점에서의 Call option의 가격 집합 (ZBC)
  swaption.hw <- sum(temp) #Sum(ZCB) = Swaption
  return(swaption.hw)
}
#GONGSI RATE LM RUN FUNCTION
Gs.int.lm.run <- function(int.indep, int.dep,
                          ma.term.set = c(1,3,6,12,18,24,36), data.length.in.y = NA, fix.indep.variable = NA, fix.avg.mon = NA)
{
  int.indep <- int.indep[order(int.indep$BASE_YYMM),]
  int.dep <- int.dep[order(int.dep$BASE_YYMM),]

  end.yymm <- min(max(int.indep$BASE_YYMM), max(int.dep$BASE_YYMM))
  if(!is.na(data.length.in.y)) {start.yymm <- as.character(((as.double(end.yymm) - data.length.in.y*100 +1)%/%100+1)*100 + (as.double(end.yymm) - data.length.in.y*100 +1)%%100%%12)
  } else start.yymm <- NA

  #종속변수와 독립변수의 갯수와 종류를 선언
  num.of.dep <- length(int.dep) - 1
  name.of.dep <- names(int.dep)[-1]
  num.of.indep <- length(int.indep) - 1
  name.of.indep <- names(int.indep)[-1]

  ma.term.set <- sort(ma.term.set)

  #결과테이블 선언
  RSLT <- data.frame(matrix(NA, 1,7))
  names(RSLT) <- c("BASE_YYMM","DEP_VARIABLE","INDP_VARIABLE","AVG_MON_NUM","REGR_CONSTANT","REGR_COEF","REMARK")

  #종속변수의 종류에 따라 회귀분석 수행
  for ( i in 1 : num.of.dep)
  { name.tmp <- name.of.dep[i]
    int.dep.tmp <- int.dep[,names(int.dep)%in%c("BASE_YYMM",name.of.dep[i])]
    rslt.tmp <- Gs.lm.calc(int.indep, int.dep.tmp, ma.term.set, start.yymm = start.yymm, end.yymm = end.yymm)
    if(!is.na(fix.avg.mon)) {rslt.tmp <- subset(rslt.tmp, AVG_MON_NUM == fix.avg.mon)}
    if(!is.na(fix.indep.variable)) {rslt.tmp <- subset(rslt.tmp, INDEP_VARIABLE == fix.indep.variable)}
    RSLT[i,] <- data.frame(end.yymm,name.tmp,rslt.tmp[order((rslt.tmp$ADJ_RSQ), decreasing = TRUE ),][1,], stringsAsFactors = F)
  }
  return(RSLT)
}

#두 개의 금리를 이용한 회귀분석 함수 작성
Gs.Int.Lm <- function(int.x.ts, int.y.ts, start.yymm = NA, end.yymm = NA)
{
  anal.data.tmp <- merge(int.y.ts,int.x.ts, by.x ="BASE_YYMM", by.y ="BASE_YYMM", all.x = T)
  if(!is.na(start.yymm)){ anal.data.tmp <- subset(anal.data.tmp, BASE_YYMM >= start.yymm) }
  if(!is.na(end.yymm)){ anal.data.tmp <- subset(anal.data.tmp, BASE_YYMM <= end.yymm) }
  anal.data <- anal.data.tmp

  rslt <- data.frame(matrix(NA, 1,3))
  names(rslt) <- c("INTERCEPT","COEFFICIENT","ADJ_RSQ")

  anal.rslt <- lm(anal.data[,2] ~ anal.data[,3])
  adj.rsq <-summary(anal.rslt)$adj.r.squared

  rslt[1:2] <- anal.rslt$coefficients
  rslt[3] <- adj.rsq
  return(rslt)
}

#GONGSI RATE LM CALC FUNCTION
Gs.lm.calc <- function (indep.vars, dep.var, ma.term.set ,start.yymm = NA, end.yymm = NA)
{
  y <- dep.var

  howmany.ma.term <- length(ma.term.set)
  howmany.indep.vars <- length(indep.vars) - 1

  tag.indep <- names(indep.vars)[-1]

  lm.rslt <- data.frame(matrix(NA, howmany.ma.term*howmany.indep.vars, 5))
  names(lm.rslt) <- c("INDEP_VARIABLE","AVG_MON_NUM","INTERCEPT","COEFFICIENT","ADJ_RSQ")

  for ( j in 1 : howmany.indep.vars)
  {
    for ( i in 1 : howmany.ma.term)
    {
      ma.term <- ma.term.set[i]
      indep.vars.ma <- Gs.Int.ma(indep.vars, ma.term)

      indep.tmp <- indep.vars.ma[,names(indep.vars.ma)%in% c("BASE_YYMM",tag.indep[j])]

      lm.rslt[((j-1)*howmany.ma.term+i),1] <- tag.indep[j]
      lm.rslt[((j-1)*howmany.ma.term+i),2] <- ma.term
      lm.rslt[((j-1)*howmany.ma.term+i),3:5] <- Gs.Int.Lm(indep.tmp, y , start.yymm, end.yymm)
    }
  }

  RSLT <- lm.rslt
  return(RSLT)
}

#Pivoted interest rate data moving average function
Gs.Int.ma <- function(int.mos.avg, avg.term)
{
  data.length <- length(int.mos.avg[,1])
  maturity.length <- length(int.mos.avg[1,])
  avg.int <- data.frame(matrix(NA, 1,maturity.length))
  for ( i in avg.term:data.length)
  {
    avg.int[(i-avg.term+1),2:maturity.length] <- colMeans(int.mos.avg[(i-avg.term+1):i,][-1])
  }
  avg.int[,1] <- int.mos.avg$BASE_YYMM[avg.term:data.length]
  names(avg.int) <- names(int.mos.avg)
  return(avg.int)
}

#회귀분석으로 DNS 모델의 초기 모수를 찾기 위한 작업임
Dns.lambda.initialize <- function (rate, obs.mat, max.lambda = 2, min.lambda = 0.05)
{

  #1.2 선형회귀를 활용한 Lambda Optimize Function 설정
  len.data <- length(rate[,1])
  optimize.lambda <- function (lambda)
  {
    lsc.tmp  <- matrix(NA, len.data, 3)
    res.tmp  <- rep(NA, len.data)
    for (i in 1: len.data)
    {
      lambda.coef <- Coef.gen.ns(lambda, obs.mat)[,-1]
      int.tmp     <- as.double(rate[i,])
      linst.tmp   <- lm(int.tmp ~ lambda.coef)
      res.tmp[i]  <- sum(linst.tmp$residuals^2)
      lsc.tmp[i,] <- as.double(linst.tmp$coefficients)
    }
    error <- sum(res.tmp)
    return(error)
  }

  ############################
  #1.3 Optimization Lambda Initial Value /w Optimization
  lambda.opt.rslt <- optim(min.lambda, optimize.lambda,method = "Brent",
                           upper = max.lambda, lower = min.lambda)

  lambda.opt <-lambda.opt.rslt$par #선형회귀로 최적화된 Lambda값 산출
  return(lambda.opt)
}

#회귀분석으로 초기 L, S, C 변수 추정하는 함수 설정
Dns.para.initialize <- function(rate, obs.mat, lambda ,dt = 1/52)
{

  #1.2 선형회귀를 활용한 Lambda Optimize Function 설정
  len.data <- length(rate[,1])

  lsc.tmp  <- matrix(NA, len.data, 3)
  res.tmp  <- rep(NA, len.data)

  #tenor별 Coefficient 값 산출, 첫번째 열은 회귀분석의 상수항으로 포함되므로 포함하지 않음
  lambda.coef <- Coef.gen.ns(lambda,obs.mat)[,-1]
  for (i in 1: len.data)
  {
    int.tmp     <- as.double(rate[i,])
    linst.tmp   <- lm(int.tmp ~ lambda.coef)
    res.tmp[i]  <- sum(linst.tmp$residuals^2)
    lsc.tmp[i,] <- as.double(linst.tmp$coefficients)
  }

  #회귀분석용 데이터 재구성
  XX1 <- lsc.tmp[1:(len.data-1),1]
  YY1 <- lsc.tmp[2:len.data,1]
  XX2 <- lsc.tmp[1:(len.data-1),2]
  YY2 <- lsc.tmp[2:len.data,2]
  XX3 <- lsc.tmp[1:(len.data-1),3]
  YY3 <- lsc.tmp[2:len.data,3]
  #회귀분석 수행
  lm1 <- lm(YY1 ~ XX1)
  lm2 <- lm(YY2 ~ XX2)
  lm3 <- lm(YY3 ~ XX3)

  #회귀분석 결과로 Initial Theta 산출
  initial.theta.l <- lm1$coefficients[1] / (1-lm1$coefficients[2])
  initial.theta.s <- lm2$coefficients[1] / (1-lm2$coefficients[2])
  initial.theta.c <- lm3$coefficients[1] / (1-lm3$coefficients[2])
  #회귀분석 결과로 Initial Kappa 산출
  initial.kappa.11 <- -log(lm1$coefficients[2]) / dt
  initial.kappa.22 <- -log(lm2$coefficients[2]) / dt
  initial.kappa.33 <- -log(lm3$coefficients[2]) / dt
  ############################
  #Applying Kalman Filter
  #Kalman Filter 적용을 위한 최초값 선언
  Initial.paras <- as.vector(c(lambda,                                          #lambda
                               initial.theta.l, initial.theta.s, initial.theta.c,   #theta1, theta2, theta3
                               max(initial.kappa.11,0.0001), max(initial.kappa.22,0.0001), max(initial.kappa.33,0.0001),#kappa1, kappa2, kappa3
                               0.05, 0, 0, 0.05, 0 , 0.05,                      #s11, s12, s13, s22, s23, s33
                               1                                       #epsilon *1000
  ))
  return(Initial.paras)
}

#G2, HW2  Model Optimizing Function
Calibration.g2.hw2 <- function(obs.int, obs.mat, ufr = 0.045, ufr.t = 60, int.type = "cont",
                               vol.info, swaption.Maturities, swap.Tenors, N = 4, L = 100, accuracy = 1e-10 , model = "G2")
{

swint <- SW.run(obs.int, obs.mat, ufr = ufr, ufr.t = ufr.t, int.type = int.type, term = 1/4)$SW_CURVE

Black.swption <- Atm.Eu.swaption(swint, vol.info, swaption.Maturities, swap.Tenors, N = N, L = L)

initial <- c(0.05, 0.03, 0.005, 0.005, 0.2)

set.seed(obs.int[1])
optim.rslt  <- constrOptim(initial, Error.fn.g2 , method = "Nelder-Mead",
                    ui = rbind(c(1,-1,0, 0, 0),c(1,0,0, 0, 0),c(0,1,0, 0, 0), c(0,0,1,0,0), c(0,0,0,1,0), c(0,0,0,0,1), c(0,0,0,0,-1)),
                    ci = c(0,0.001,0.001,0.00001,0.00001,-1, -1)
                    # a-b>=0, a>0.001, b>0.001, sigma>=.00001, eta>=0.00001, rho>=1, (rho <= 1 --> -rho>=-1)
                    ,control = list(reltol = accuracy), Black.swption = Black.swption, value.tf = F,swint = swint
                    )

params <- optim.rslt$par
error <- optim.rslt$value
SWAPTION_VAL <- vol.info ; names(SWAPTION_VAL) <- c("BSE_DT", "SWAPTION_MAT", "SWAP_TENOR", "VALUE")
SWAPTION_VAL$VALUE <- Error.fn.g2(params, Black.swption, swint = swint, value.tf = T)

if(tolower(model) =="hw2") { params <- param.g2.to.hw2(params) }

return(list (PARAMS = params, ERROR = error, SWAPTION_VAL = SWAPTION_VAL))
}

#G2 Error Function
Error.fn.g2 <- function(initial, Black.swption, swint ,value.tf = F){
  a <- initial[1]; b<-initial[2]; sigma<-initial[3]; eta<-initial[4]; rho<-initial[5];
  swaption.val <- rep(NA, length(swap.Tenors)*length(swaption.Maturities))
  for (i in 1: (length(swap.Tenors)*length(swaption.Maturities)))
  {
    t1 <- Black.swption$SWAPTION_MAT[[i]]
    t2 <- Black.swption$SWAPTION_MAT[[i]] + Black.swption$SWAP_TENOR[[i]]
    strike <- Black.swption$swap.rate[[i]]
    swaption.val[i] <- g2.swaption.val (swint, a, b, sigma, eta, rho, t1, t2, strike)
  }
  if(value.tf == F){
  error <- sum(((Black.swption$SWAPTION_PRICE - swaption.val)/Black.swption$SWAPTION_PRICE)^2)
  return(error)}
  else return(swaption.val)
}

#G2, HW2 MODEL SIMULATION FUNCTION
G2.HW2.simulation <- function(obs.int, obs.mat, param.set,
                          bse.ym = NA, num.of.scen = 1000, int.type = "cont",
                          ufr = 0.045, ufr.t = 60, proj.y = 100 , llp = max(obs.mat), model = "G2" )
{
  if(tolower(model) == "g2")
  { params <- param.set
  } else if (tolower(model) =="hw2")
  { params <- param.hw2.to.g2(param.set)
  } else {return("model sholud be G2 or HW2 ") ; break}

  a <- params[[1]]
  b <- params[[2]]
  sigma <- params[[3]]
  eta <- params[[4]]
  rho <- params[[5]]

  dt <- 1/12 #시뮬레이션 TERM 설정
  sim.dt <- proj.y/dt #시뮬레이션 길이 생성

  sw <- SW.run(obs.int, obs.mat, ufr, ufr.t, proj.y, dt, NA, 0.001, 1, 0.0001, llp, int.type = int.type)
  swint <- sw$SW_CURVE  #Sw curve 생성

  #촐레스키 분해로 상관관계를 가지는 난수 생성
  rand <- Chol.rand.2f(num.of.scen, sim.dt, rho, seednum = bse.ym)
  w1 <- rand$W1
  w2 <- rand$W2

  #X, Y 변수 생성
  x <- matrix(0, sim.dt,num.of.scen)
  y <- matrix(0, sim.dt,num.of.scen)

  for (i in 2 : sim.dt)
  {
    dx.tmp <- -a*x[(i-1),]*dt + sigma*w1[(i-1),]
    x[i,] <- x[(i-1),]+dx.tmp

    dy.tmp <- -b*y[(i-1),]*dt + eta*w2[(i-1),]
    y[i,] <- y[(i-1),]+dx.tmp
  }

  #VARPHI(T) 산출
  varphi.t <- swint$FWD_CONT + 0.5*(sigma*sigma)/(a*a)*(B.g2(a,swint$TIME-dt, swint$TIME))^2 +
              0.5*(eta*eta)/(b*b)*(B.g2(b,swint$TIME-dt, swint$TIME))^2 +
              (rho*sigma*eta/(a*b))*B.g2(a,swint$TIME-dt, swint$TIME)*B.g2(b,swint$TIME-dt, swint$TIME)

  Fwd.cont <- x + y + varphi.t
  Fwd.disc <-  Int.cont.to.disc(Fwd.cont)

  #Short Rate Based P(t,t+1)
  P.tT <- exp(-Fwd.cont*dt)

  #Short Rate Based P(0,T)
  P.0T <- P.tT
  for ( i in 2 : sim.dt)
  {
    P.0T[i,] <- P.0T[(i-1),] * P.tT[i,]
  }

  Rt.cont <- -log(P.0T)/swint$TIME
  Rt.disc <- Int.disc.to.cont(Rt.cont)

  return(list (TIME = swint$TIME,MONTH_SEQ = swint$MONTH_SEQ,  P.0T = P.0T, Rt.cont = Rt.cont, Rt.disc = Rt.disc, Fwd.disc=Fwd.disc, Fwd.cont = Fwd.cont))
}

#G2 AND HW2 시뮬레이션 결과를 레이아웃으로 정렬
G2.HW2.output <- function(bse.ym,simulation.output,scen.name = NA ,proj.y = 100,num.of.scen = 1000)
{
  month.seq <- simulation.output$MONTH_SEQ
  num.time <- length(month.seq)
  scen.bucekt <- seq(1:num.of.scen)

  #필요한 변수들을 입력
  discount <- simulation.output$P.0T
  spot.cont <- simulation.output$Rt.cont
  spot.disc <- simulation.output$Rt.disc
  fwd.cont <- simulation.output$Fwd.cont
  fwd.disc <- simulation.output$Fwd.disc

  layout <- matrix(NA, num.time*num.of.scen,10)
  layout <- data.frame(layout)

  #Layout에 맞추어 변환
  for( i in 1: num.of.scen)
  {
    scen.tmp <- scen.bucekt[i]
    discout.tmp <- discount[,i]
    spot.cont.tmp <- spot.cont[,i]
    spot.disc.tmp <- spot.disc[,i]
    fwd.cont.tmp <- fwd.cont[,i]
    fwd.disc.tmp <- fwd.disc[,i]
    tmp <- data.frame(bse.ym,scen.name, scen.tmp,simulation.output$TIME,month.seq, spot.cont.tmp,spot.disc.tmp, discout.tmp,fwd.cont.tmp, fwd.disc.tmp,stringsAsFactors = F)
    layout[((i-1)*num.time+1):(i*num.time),(1:10)] <- tmp
  }
  layout <- data.frame(layout, stringsAsFactors = F)
  names(layout) <- c("BSE_DT","SCEN_ID","SCEN_NO","TIME","MONTH_SEQ","SPOT_CONT","SPOT_DISC","DISCOUNT","FWD_CONT","FWD_DISC")
  return(layout)
}

#CIR Model Parameters Initialization function
Cir.initialize <- function (int.ts , obs.term = 1/12, int.type = "cont")
{
 if(tolower(int.type) != "cont")
 { short.rate.ts.cont <-Int.disc.to.cont(int.ts); int.type <- "cont"
 }else short.rate.ts.cont <- int.ts

 diff.r <- diff(short.rate.ts.cont)
 sqr.r <- sqrt(short.rate.ts.cont)[-length(short.rate.ts.cont)]

 y <- diff.r
 x <- cbind(obs.term/sqr.r, obs.term*sqr.r)
 lm.rslt <- lm(y ~ x-1)

 k <- as.double(-lm.rslt$coefficients[2])
 theta <- as.double(lm.rslt$coefficients[1]/k)
 sigma <- sqrt(var(lm.rslt$residuals)/obs.term)

 if(k < 0 ){ k <- abs(k)}
 if(theta < 0 ){ theta <- abs(theta)}
 if(2*k*theta < sigma*sigma) {k <- sigma^2/abs(theta) ; theta <- abs(theta)}  #2*k*theta > sigma^2

 Cir.initial.para <- c(k, theta, sigma)
 names(Cir.initial.para) <- c("k", "theta", "sigma")
 return(Cir.initial.para)
}

#CIR Model Parameters Object Function
Cir.mle  <- function (int.ts.cont , obs.term = 1/12, params)
{
  k <- params[1]
  theta <- params[2]
  sigma <- params[3]

  y <- int.ts.cont[-1]
  x <- int.ts.cont[-length(int.ts.cont)]

  ct <- (2*k)/(sigma^2*(1-exp(-k*obs.term)))
  q <- 2*k*theta/(sigma^2)-1
  u <- ct*exp(-k*obs.term)*x
  v <- ct*y

  gpdf <- dchisq(2*v, (2*q + 2), (2*u) )
  ppdf <- log(2*ct*gpdf)
  ppdf[is.infinite(ppdf)] <- -7000000
  LL <- sum(-ppdf)

  #Cir Model Constraint 2*k*theta > sigma^2
  if(2*k*theta -sigma*sigma < 0) {LL <- 7000000}

  return(LL)
}

#CIR Model Calibration function
Cir.calib.mle <- function(int.ts, obs.term = 1/12, int.type = "cont", accuracy = 1e-12, initial.paras  = NA)
{
  if(tolower(int.type) != "cont")
  { int.ts.cont <-Int.disc.to.cont(int.ts); int.type <- "cont"
  }else int.ts.cont <- int.ts

  #OLS를 활용한 Initialize

  if(anyNA(initial.paras)) {Cir.initial.para <- Cir.initialize(int.ts.cont, obs.term, "cont")
  }else Cir.initial.para <- initial.paras

  #OLS값을 Initial Value로 설정하고, MLE 추정
  set.seed(1)
  optim.rslt <-
    constrOptim(Cir.initial.para, Cir.mle, method = "Nelder-Mead", int.ts.cont = int.ts.cont, obs.term = obs.term
                ,control = list( reltol = accuracy , maxit = 2000), outer.iterations = 10
                ,ui = rbind(c(1,0,0), c(0,1,0), c(0,0,1), c(0,-1,0),c(-1,0,0))
                ,ci = c(0,0,0,-2,-1)
    )


  #계산 정합성을 위해 한 번 더 Calibration
  optim.rslt <-
    constrOptim(optim.rslt$par, Cir.mle, method = "Nelder-Mead", int.ts.cont = int.ts.cont, obs.term = obs.term
                ,control = list( reltol = accuracy , maxit = 2000), outer.iterations = 10
                ,ui = rbind(c(1,0,0), c(0,1,0), c(0,0,1), c(0,-1,0),c(-1,0,0))
                ,ci = c(0,0,0,-2,-1)
    )

  opt.paras <- optim.rslt$par #Parameters
  LL <- optim.rslt$value      #Logik

  names(opt.paras) <- c("Mean_rev_speed_dr", "Reversion_level_dr", "Sigma_dr")
  return(list(opt.paras = opt.paras, LL = LL ))
}
#SIMULATION RUN FUNCTION
G2.HW2.simulation.run <- function(obs.int, obs.mat, param.set, LP = 0,
                                  bse.ym = NA, num.of.scen = 1000, int.type = "cont", scen.name = NA,
                                  ufr = 0.045, ufr.t = 60, proj.y = 100 , llp = max(obs.mat), model = "G2" )

{
  #금리의 형태를 Cont 형태로 변환
  if(tolower(int.type) != "cont")
    { rate <-Int.disc.to.cont(obs.int + LP); int.type <- "cont"
    }else rate <- obs.int + LP

  simulation.output <- G2.HW2.simulation(obs.int, obs.mat, param.set, bse.ym, num.of.scen, int.type, ufr, ufr.t, proj.y , llp, model)

  scen.rslt <- G2.HW2.output(bse.ym,simulation.output,scen.name = scen.name ,proj.y,num.of.scen)

  return(scen.rslt)
}


#Lp Fitting with Polynomial Function
Lp.fitting.run <- function (lp, obs.mat, llp = 20)
{

   lp.data <- c(0,lp[obs.mat < llp], 0)
   m1 <- c(0,obs.mat[obs.mat < llp], llp )
   m2 <- m1^2
   m3 <- m1^3
   m4 <- m1^4

   LP.RSLT <- data.frame(matrix(NA, 1, 7))
   names(LP.RSLT) <- c("INTERCEPT", "COEF1", "COEF2","COEF3", "COEF4","ADJ_RSQ", "ERROR")

   lm.rlst <- lm(lp.data ~ m1 + m2 + m3 + m4)
   lm.rlst2 <- summary(lm.rlst)

   LP.RSLT[,1:5] <- lm.rlst2$coefficients[,1]
   LP.RSLT[,6] <- lm.rlst2$adj.r.squared
   LP.RSLT[,7] <- sum(lm.rlst$residuals^2)

   s1 <- seq(1/12, llp, 1/12)
   MONTH_SEQ <- seq(1L : as.integer(llp*12L))
   LP.INT <- pmax(LP.RSLT$INTERCEPT + LP.RSLT$COEF1*s1 + LP.RSLT$COEF2*s1^2 + LP.RSLT$COEF3*s1^3 + LP.RSLT$COEF4*s1^4,0)
   LP.INT[length(LP.INT)] <- 0
   LP.INT.RSLT <- data.frame(TIME = s1, MONTH_SEQ = MONTH_SEQ, LP = LP.INT)

  return(list(LP.INT.RSLT = LP.INT.RSLT, LP.RSLT = LP.RSLT))
}

#대조변수법과 Cholesky 분해를 통한 상관관계를 갖는 난수 산출
Chol.rand.2f <- function (num.of.scen, num.of.variables, rho = 0, seednum = NA)
{
  rand.tmp <- Antithetic.rand(num.of.scen*2, num.of.variables, seednum)
  W1 <- t(rand.tmp[1:num.of.scen, 1:num.of.variables])
  tmp <- t(rand.tmp[(num.of.scen+1):(num.of.scen*2), 1:num.of.variables])
  W2 <- W1
  for ( i in 1 : num.of.scen)
  {
    W2[,i] <- rho*W1[,i] + sqrt(1-rho^2)*tmp[,i]
  }
  return(list(W1 = W1 , W2 = W2))
}

#Independent AFNS Model with diffrent eta
Afns.kalman.indep <- function (int.ts.cont, obs.mat, dt, paras, returnval  = F)
{
len.data <- length(int.ts.cont[,1])

lambda<-paras[1]

theta1<-paras[2]
theta2<-paras[3]
theta3<-paras[4]

kappa11<-paras[5]
kappa22<-paras[6]
kappa33<-paras[7]

s11<-paras[8]
s22<-paras[9]
s33<-paras[10]

eta <-paras[11:length(paras)]^2

s.mat <- diag(c(s11, s22, s33)) #matrix(c(s11, 0, 0, 0,s22, 0, 0, 0, s33), ncol = 3)
C <- -Afns.ctT(lambda, 0, obs.mat, s.mat )

kappa <- c(kappa11, kappa22, kappa33)
theta <- c(theta1, theta2, theta3)
#초기화 단계

coef  <- t(Coef.gen.ns(lambda,obs.mat))

eig.tmp <- matrix(NA, 3,3 )
eigen.idx <- 4-rank(kappa)
eig.tmp[,1] <- eigen.idx ; eig.tmp[,3] <- kappa ; eig.tmp[,2] <- seq(1:3)
eig.tmp.df  <- data.frame(eig.tmp) ; names(eig.tmp.df) <-  c("eig.idx","eig.seq","kappa")

eig.tmp.df <- eig.tmp.df[with(eig.tmp.df, order(eig.idx)),]
eigenvec <- matrix(0, 3,3)
eigenvec[eig.tmp.df$eig.idx[1],eig.tmp.df$eig.seq[1]] <- 1 ; eigenvec[eig.tmp.df$eig.idx[2],eig.tmp.df$eig.seq[2]] <- 1 ; eigenvec[eig.tmp.df$eig.idx[3],eig.tmp.df$eig.seq[3]] <- 1
t.eigvec <- t(eigenvec) ; inv.eigvec <- solve(eigenvec)

f.eigval <- matrix(NA, 3,3)

for (i in 1:3)
{
  for (j in 1:3)
  {
    ii <- eig.tmp.df$kappa[i]
    jj <- eig.tmp.df$kappa[j]
    f.eigval[i,j] <- ii+jj
  }
}

#Phi Generating
phi1 <- exp(-kappa*dt)            #상태방정식의 Transition 계산
mat.phi1 <- diag(phi1)            #상태방정식의 Transition Matrix
phi0 <- (1-phi1)*theta            #상태방정식의 상수항
t.phi1 <- t(mat.phi1)             #Transition Matrix의 transformation. 단, Transition Matrix가 daig행렬이므로 동일하게 산출됨
phi1tphi1 <- phi1%*%t(phi1)       #Sigma_hat 산출에 사용

#Sigma matrix Generating
##sigmamat <- diag(s11, s22, s33)
##matrix( c(s11, 0, 0, 0, s22, 0, 0,0,s33 ), nrow = 3)

smat <- inv.eigvec%*%s.mat%*%t(s.mat)%*%eigenvec
vlim <- smat/f.eigval
vmat <- smat*(1-exp(-f.eigval*dt))/f.eigval
Q    <- eigenvec%*%vmat%*%t.eigvec              #갱신과정에서 적용되는 변수에 저장

#Initial State Generating
h.large<-diag(eta)
initial.state <- theta                          #최초 상태방정식의 값은 Theta로 설정
initial.sigma <- eigenvec%*%vlim%*%t.eigvec     #최초 상태방정식의 분산 설정

prev.state <- initial.state                     #칼만필터 반복계산의 변수로 사용하기 위한 변수명 재설정
prev.sigma <- initial.sigma                     #칼만필터 반복계산의 변수로 사용하기 위한 변수명 재설정

state <- matrix(NA, len.data+1, 3)              #State 변동을 담기 위한 적재공간 설정
state[1,] <- initial.state                      #최초 상태를 State의 첫 번째 행에 입력
ll.idx <- rep(NA, len.data)                     #우도를 담기 위한 적재공간 생성

#Apply Kalman Filter
len.idx <- len.data+1
for ( k in 1: len.data)
{
  #예측 단계
  int.tmp <- as.vector(int.ts.cont[k,])
  xhat <- prev.state*phi1 + phi0
  sigma.hat <- Q + prev.sigma*phi1tphi1                    # t시점에서의 Omega값
  y.implied <- xhat%*%coef+C                                 # 모델에 의해서 예측된 t시점에서의 y값
  v <- as.vector(as.double(int.tmp - y.implied))           # y_t - y_t|t-1
  finv <- solve(t(coef)%*%sigma.hat%*%coef + h.large)      # t 시점에서의 Sigma 집합

  #갱신 단계
  next.sigma <- sigma.hat - sigma.hat%*%coef%*%finv%*%t(coef)%*%sigma.hat
  next.state <- xhat + v%*%finv%*%t(coef)%*%sigma.hat

  #우도함수의 산출
  ll.idx.tmp <- t(v)%*%finv%*%v - log(det(finv))

  #산출된 값을 저장
  state[(k+1),] <- next.state
  ll.idx[k] <- ll.idx.tmp
  prev.state <- next.state
  prev.sigma  <- next.sigma
}

#Eror Function
Logik <- (sum(ll.idx[1:len.data]) + length(int.tmp)*len.data*log(2*pi))/2   #우도함수 결과값 계산
BIC <- length(paras)*log(len.data)+2*Logik

if ( returnval == F) {return(Logik)
} else {return(state = next.state ) }
}
#Renjin에서 det함수를 읽을 수 없기 때문에 함수를 새로 작성 2x2, 3x3행렬의 determinant 계산
det.23 <- function(mat)
{
  if(length(unique(dim(mat))) != 1){return("mat must be square") ;break }
  if(unique(dim(mat)) == 2) {det <- mat[1,1]*mat[2,2] - mat[1,2]*mat[2,1]}
  if(unique(dim(mat)) == 3) {det <- sum(c(mat[1,1],-mat[1,2],mat[1,3]) *c(mat[2,2]*mat[3,3]-mat[2,3]*mat[3,2], mat[2,1]*mat[3,3]-mat[2,3]*mat[3,1], mat[2,1]*mat[3,2]-mat[2,2]*mat[3,1]))}
  if(unique(dim(mat)) > 3) {return("can receive 2 and 3 dim matrix")}

  return(det)
}

#G2 Model B(a, t, T) Calc Function
B.g2 <- function(z, t1, t2)
{
  B <- (1 - exp(-z*(t2-t1)))/z
  return(B)
}

#G2 Model V(t, T) Calc Function
V.g2 <- function(a, b, sigma, eta, rho, t1, t2)
{
  timetomat <- t2 - t1
  B1 <- B.g2(a,t1,t2)
  B2 <- B.g2(b, t1, t2)
  B12 <- B.g2((a+b),t1, t2)
  V.g2 <- ((sigma/a)^2) *(timetomat-B1-(a/2)*B1^2) + ((eta/b)^2)*(timetomat-B2-(b/2)*B2^2) + (2*sigma*eta*rho/(a*b))*(timetomat - B1 - B2 - B12)
  return(V.g2)
}

#G2 Model A(t, T) Calc Function
A.g2 <- function(swint, a, b, sigma, eta, rho, t1, t2)
{
  P.mkt.t1 <- as.double(subset(swint, MONTH_SEQ == round2(t1*12,0), select = DISCOUNT))
  P.mkt.t2 <- as.double(subset(swint, MONTH_SEQ == round2(t2*12,0), select = DISCOUNT))
  if (t1 == 0){P.mkt.t1 <-  1}
  if (t2 == 0){P.mkt.t2 <-  1}
  V.tT <- V.g2(a, b, sigma, eta, rho, t1, t2)
  V.0T <- V.g2(a, b, sigma, eta, rho, 0, t2)
  V.0t <- V.g2(a, b, sigma, eta, rho, 0, t1)
  A <- (P.mkt.t2/P.mkt.t1)*exp(0.5*(V.tT - V.0T +V.0t))
  return(A)
}

#G2 Model Mu_x(t, T) Calc Function
Mu.x.g2 <- function(a, b, sigma, eta, rho, s,t1, t2)
{
  tep1 <- (sigma/a)^2
  Mu.x <- (tep1+rho*sigma*eta/(a*b))*(1-exp(-a*(t1-s))) - 0.5*tep1*(exp(-a*(t2-t1))-exp(-a*(t2+t1-2*s)))-rho*sigma*eta/(b*(a+b))*(exp(-b*(t2-t1)) - exp(-b*t2-a*t1+(a+b)*s))
  return(Mu.x)
}

#G2 Model Mu_y(t, T) Calc Function
Mu.y.g2 <- function(a, b, sigma, eta, rho, s, t1, t2)
{
  tep2 <- (eta/b)^2
  Mu.y <- (tep2+rho*sigma*eta/(a*b))*(1-exp(-b*(t1-s))) - 0.5*tep2*(exp(-b*(t2-t1))-exp(-b*(t2+t1-2*s)))-rho*sigma*eta/(a*(a+b))*(exp(-a*(t2-t1)) - exp(-a*t2-b*t1+(a+b)*s))
  return(Mu.y)
}


#Swaption Cash Flow Time Calc Function
Swaption.CF.time.g2 <- function(t1, t2, N = 4)
{
  dtt <- 1/N
  CF.time <- as.double(seq(from = t1 + dtt, to = t2, by = dtt))
  return(CF.time)
}


#Swaption Cash Flow SIZE Calc Function
Swaption.CF.int.g2 <- function(t1, t2, S ,N = 4)
{
  CF.int <- rep(S/N, (t2-t1)*N)
  CF.int[(t2-t1)*N] <- CF.int[(t2-t1)*N]+1
  return(CF.int)
}

#G2 Swaption Valuation Function
g2.swaption.val <- function(swint, a, b, sigma, eta, rho, t1, t2, strike, N = 4, L = 100)
{
  #G2함수값 설정
  Mu.x <- -Mu.x.g2(a, b, sigma, eta, rho, 0,t1, t1)
  Mu.y <- -Mu.y.g2(a, b, sigma, eta, rho, 0,t1, t1)
  Sigma.x <- sigma*sqrt((1-exp(-2*a*t1))/(2*a))
  Sigma.y <- eta*sqrt((1-exp(-2*b*t1))/(2*b))
  Rho.xy <- rho*sigma*eta/((a+b)*Sigma.x*Sigma.y)*(1-exp(-(a+b)*t1))

  #CF의 시점과, CF의 크기 설정
  cf.t <- Swaption.CF.time.g2(t1, t2, N)
  cf.i <- Swaption.CF.int.g2(t1, t2, strike , N)

  #A, Ba, Bb 산출
  size <- length(cf.t)
  A <- rep(NA, size)
  Ba <- B.g2(a, t1, cf.t)
  Bb <- B.g2(b, t1, cf.t)

  for ( i in 1 : size)
  {
    A[i] <- A.g2(swint, a, b, sigma, eta, rho, t1, cf.t[i])
  }

  #X에 대하여 적분하여야 하기 때문에 적분을 위한 함수 설정
  get.innerfn.g2 <- function (x)
  {

    lambda.i <- cf.i*A*exp(-Ba*x)

    Ybar.find <- function(y.bar)
    {
      solved.ybar <- (sum(lambda.i *exp(-as.double(Bb)*y.bar))-1)^2
      return(solved.ybar)
    }

    #Ybar 산출
    y.bar <- optimize(f = Ybar.find, lower = -1, upper = 1, tol = 1e-6)$minimum

    #산출된 y.bar를 기반으로 확률 변수 산출
    h1 <- (y.bar - Mu.y)/(Sigma.y*sqrt(1-Rho.xy*Rho.xy)) - Rho.xy*(x - Mu.x)/(Sigma.x*sqrt(1-Rho.xy*Rho.xy))
    h2 <- rep(NA, size)
    kappa <- rep(NA, size)

    for ( i in 1: size)
    {
      cf.tt <- cf.t[i]
      h2[i] <- h1 + Bb[i]*Sigma.y*sqrt(1- Rho.xy*Rho.xy)
      kappa[i] <- -B.g2(b,t1,cf.tt)*(Mu.y - 0.5*(1-Rho.xy*Rho.xy)*Sigma.y*Sigma.y*B.g2(b,t1,cf.tt)+Rho.xy*Sigma.y*(x - Mu.x)/Sigma.x)
    }

    value <- (exp(-0.5*((x-Mu.x)/Sigma.x)^2)/(Sigma.x*sqrt(2*pi))) * (pnorm(-h1) - sum(lambda.i*exp(kappa)*pnorm(-h2)))

    return(value)
  }

  lower = -Mu.x.g2(a, b, sigma, eta, rho, 0,t1, t2) - 12*sigma*sqrt((1-exp(-2*a*t2))/(2*a))
  upper = -Mu.x.g2(a, b, sigma, eta, rho, 0,t1, t2) + 12*sigma*sqrt((1-exp(-2*a*t2))/(2*a))

  #int.val <- integrate(Vectorize(get.innerfn.g2), lower, upper)$value
  int.val <- simpson.integral(get.innerfn.g2, lower, upper)
  swaption.val <- L*as.double(subset(swint, TIME == t1, select = DISCOUNT))*int.val
  return(swaption.val)
}
#HW2 모델의 모수를 G2 모델의 모수로 변경
param.hw2.to.g2 <- function (param.hw2)
{
  #a > b 조건으로 모수를 추정하여야 함
  a <- param.hw2[[1]]
  b <- param.hw2[[2]]
  sigma1.hw2 <- param.hw2[[3]]
  sigma2.hw2 <- param.hw2[[4]]
  rho.hw2 <- param.hw2[[5]]

  sigma <- (sigma1.hw2^2 + (sigma2.hw2^2)/((a-b)*(a-b)) + 2*rho.hw2*sigma1.hw2*sigma2.hw2/(b-a))^0.5

  eta <- sigma2.hw2/(a-b)

  rho <- (sigma1.hw2*rho.hw2-eta)/sigma

  param.g2 <- c(a, b, sigma, eta, rho)
  return(param.g2)
}

#G2 모델의 모수를 HW2 모델의 모수로 변경
param.g2.to.hw2 <- function (param.g2)
{
  a <- param.g2[[1]]
  b <- param.g2[[2]]
  sigma.g2 <- param.g2[[3]]
  eta.g2 <- param.g2[[4]]
  rho.g2 <- param.g2[[5]]

  sigma1 <- (sigma.g2^2 + eta.g2^2 + 2*rho.g2*sigma.g2*eta.g2)^0.5
  sigma2 <- eta.g2*(a - b)
  rho <- (sigma.g2*rho.g2 + eta.g2)/sigma1

  param.hw2 <- c(a, b, sigma1, sigma2, rho)
  return(param.hw2)
}

#Cir Model Simulation function
Cir.Simulation <- function (rt, params, num.of.scen = 1000 , proj.y = 100, dt = 1/12, int.type = "cont", bse.ym = NA)
{
  k <- params[1]
  theta <- params[2]
  sigma <- params[3]

  if(tolower(int.type) != "cont")
  { r0 <-Int.disc.to.cont(rt); int.type <- "cont"
  }else r0 <- rt

  sim.dt <- proj.y/dt

  randnum <- Antithetic.rand(num.of.scen,sim.dt,bse.ym) #난수 생성

  mat <- seq(dt, proj.y, dt) #만기 설정

  #Short Rate 생성
  r.t <- matrix(NA, (sim.dt+1),num.of.scen)
  r.t[1,] <- r0
  #0의 금리가 나와서는 안되나, 수치적인 방법으로 1달씩 금리를 생성하기 때문에 음의 금리가 생성될 수 있음
  #최저값을 0으로 설정함
  for (i in 2 : (sim.dt+1))
  {
    r.t[i,] <- pmax(r.t[i-1,]*exp(-k*dt) + theta*(1-exp(-k*dt))+ sqrt(r.t[i-1,]*(sigma*sigma/k)*(exp(-k*dt)-exp(-2*k*dt)) + 0.5*(theta*sigma*sigma/k)*(1-exp(-k*dt))^2)*randnum[,(i-1)],0)
  }

  #Short Rate로 채권 가격 생성
  discount <- matrix(NA, (sim.dt+1),num.of.scen)
  discount[1,] <- 1
  for ( i in 2 : (sim.dt+1))
  {
    discount[i,] <- discount[i-1,]*exp(-r.t[i,]*dt)
  }

  #채권 가격으로 Spot Rate 생성
  spot.cont <- matrix(NA, (sim.dt+1),num.of.scen)
  spot.cont[1,] <- r0
  for (i in 2 : (sim.dt+1))
  { mat.tmp <- mat[(i-1)]
  spot.cont[i,] <- -log(discount[i,])/mat.tmp
  }
  #Cont Rate를 Discrete Rate로 변경
  spot.disc <- Int.cont.to.disc(spot.cont)
  fwd.disc <- Int.cont.to.disc(r.t)
  seq <- as.integer(seq(1,proj.y/dt,1))

  return(list (TIME = mat, MONTH_SEQ = seq, r.t = r.t, sim.p.t = discount, Rt.cont = spot.cont, Rt.disc = spot.disc, Fwd.disc=fwd.disc))
}

#CIR MODEL RUN
Cir.run <- function (bse.ym = NA, dt  = 1/12, int.type = "cont", r0 ,
                     num.of.scen = 1000, scen.name = NA, proj.y = 100 ,
                     rev_speed = 0.02, rev_level = 0.02, sigma = 0.01
                    )
{
  params <- c(rev_speed, rev_level, sigma)

  if(tolower(int.type) != "cont")
  { r0 <- Int.disc.to.cont(r0) }

  if(tolower(dt)== "yearly") {obs.term <- 1
  } else if (tolower(dt)== "quarterly") { obs.term <- 0.25
  }else if (tolower(dt)== "monthly") { obs.term <- 1/12
  }else if (tolower(dt)== "daily") { obs.term <- 1/252 }

  sim.rslt <- Cir.Simulation(r0, params, num.of.scen , proj.y, dt, int.type, bse.ym)

  #필요한 변수들을 입력
  discount <- sim.rslt$sim.p.t[-1,]
  spot.cont <- sim.rslt$Rt.cont[-1,]
  spot.disc <- sim.rslt$Rt.disc[-1,]
  fwd.cont <- sim.rslt$r.t[-1,]
  fwd.disc <- sim.rslt$Fwd.disc[-1,]

  layout <- matrix(NA, proj.y/dt*num.of.scen,10)
  layout <- data.frame(layout)
  scen.bucekt <- seq(1:num.of.scen)

  #Layout에 맞추어 변환
  for( i in 1: num.of.scen)
  { #i <- 1
    scen.tmp <- scen.bucekt[i]
    discout.tmp <- discount[,i]
    spot.cont.tmp <- spot.cont[,i]
    spot.disc.tmp <- spot.disc[,i]
    fwd.cont.tmp <- fwd.cont[,i]
    fwd.disc.tmp <- fwd.disc[,i]
    tmp <- data.frame(bse.ym,scen.name, scen.tmp,sim.rslt$TIME,sim.rslt$MONTH_SEQ, spot.cont.tmp,spot.disc.tmp, discout.tmp,fwd.cont.tmp, fwd.disc.tmp,stringsAsFactors = F)
    layout[((i-1)*proj.y/dt+1):(i*proj.y/dt),(1:10)] <- tmp
  }
  layout <- data.frame(layout, stringsAsFactors = F)
  names(layout) <- c("BSE_DT","SCEN_ID","SCEN_NO","TIME","MONTH_SEQ","SPOT_CONT","SPOT_DISC","DISCOUNT","FWD_CONT","FWD_DISC")
  return(list(rslt = layout, params = params))
}

#Vasicek Model Calibration function With Linear Model
Vasi.calib.linear <- function (int.ts , obs.term = 1/12, int.type = "cont")
{
  if(tolower(int.type) != "cont")
  { short.rate.ts.cont <-Int.disc.to.cont(int.ts); int.type <- "cont"
  }else short.rate.ts.cont <- int.ts

  n <- length(short.rate.ts.cont)
  x <- short.rate.ts.cont[-n]
  y <- short.rate.ts.cont[-1]

  sx <- sum(x)
  sy <- sum(y)
  sxx <- sum(x*x)
  syy <- sum(y*y)
  sxy <- sum(x*y)

  alpha.hat <- ((n-1)*sxy-sy*sx)/((n-1)*sxx-sx^2)
  beta.hat <- sum(y - alpha.hat*x) / ((n-1)*(1-alpha.hat))
  vv.hat <- sum((y-alpha.hat*x - beta.hat*(1-alpha.hat))^2)/(n-1)

  a <- as.double(-log(alpha.hat)/obs.term )
  b <- a*beta.hat
  sigma2 <- (vv.hat*2*a)/(1-exp(-2*a*obs.term))
  sigma <- sqrt(sigma2)

  params <- c(a, b, sigma)
  return(params)
}
#Vasicek Model Simulation Function
Vasi.Simulation <- function (rt, params, num.of.scen = 1000 , proj.y = 100, dt = 1/12, int.type = "cont", bse.ym = NA)
{
  a <- params[1]
  b <- params[2]
  sigma <- params[3]

  if(tolower(int.type) != "cont")
  { r0 <-Int.disc.to.cont(rt); int.type <- "cont"
  }else r0 <- rt

  sim.dt <- proj.y/dt

  randnum <- Antithetic.rand(num.of.scen,sim.dt,bse.ym) #난수 생성

  mat <- seq(dt, proj.y, dt) #만기 설정

  #Short Rate 생성
  r.t <- matrix(NA, (sim.dt+1),num.of.scen)
  r.t[1,] <- r0
  for (i in 2 : (sim.dt+1))
  {
    r.t[i,] <- r.t[i-1,]*exp(-a*dt) + (b/a)*(1-exp(-a*dt)) + sigma*sqrt((1-exp(-2*a*dt))/(2*a))*randnum[,(i-1)]
  }

  #Short Rate로 채권 가격 생성
  discount <- matrix(NA, (sim.dt+1),num.of.scen)
  discount[1,] <- 1
  for ( i in 2 : (sim.dt+1))
  {
    discount[i,] <- discount[i-1,]*exp(-r.t[i,]*dt)
  }

  #채권 가격으로 Spot Rate 생성
  spot.cont <- matrix(NA, (sim.dt+1),num.of.scen)
  spot.cont[1,] <- r0
  for (i in 2 : (sim.dt+1))
  { mat.tmp <- mat[(i-1)]
  spot.cont[i,] <- -log(discount[i,])/mat.tmp
  }
  #Cont Rate를 Discrete Rate로 변경
  spot.disc <- Int.cont.to.disc(spot.cont)
  fwd.disc <- Int.cont.to.disc(r.t)
  seq <- as.integer(seq(1,proj.y/dt,1))

  return(list (TIME = mat, MONTH_SEQ = seq, r.t = r.t, sim.p.t = discount, Rt.cont = spot.cont, Rt.disc = spot.disc, Fwd.disc=fwd.disc))
}
#Vasicek Model Run
Vasi.run <- function (bse.ym = NA, dt  = 1/12, int.type = "cont", r0 ,
                      num.of.scen = 1000, scen.name = NA, proj.y = 100 ,
                      rev_speed = 0.02, rev_level = 0.02, sigma = 0.01
                      )
{
  params <- c(rev_speed, rev_level, sigma)

  if(tolower(int.type) != "cont")
  { r0 <- Int.disc.to.cont(r0) }

  if(tolower(dt)== "yearly") {obs.term <- 1
  } else if (tolower(dt)== "quarterly") { obs.term <- 0.25
  }else if (tolower(dt)== "monthly") { obs.term <- 1/12
  }else if (tolower(dt)== "daily") { obs.term <- 1/252 }

  sim.rslt <- Vasi.Simulation(r0, params, num.of.scen , proj.y, dt, int.type, bse.ym)

  #필요한 변수들을 입력
  discount <- sim.rslt$sim.p.t[-1,]
  spot.cont <- sim.rslt$Rt.cont[-1,]
  spot.disc <- sim.rslt$Rt.disc[-1,]
  fwd.cont <- sim.rslt$r.t[-1,]
  fwd.disc <- sim.rslt$Fwd.disc[-1,]

  layout <- matrix(NA, proj.y/dt*num.of.scen,10)
  layout <- data.frame(layout)
  scen.bucekt <- seq(1:num.of.scen)

  #Layout에 맞추어 변환
  for( i in 1: num.of.scen)
  {
    scen.tmp <- scen.bucekt[i]
    discout.tmp <- discount[,i]
    spot.cont.tmp <- spot.cont[,i]
    spot.disc.tmp <- spot.disc[,i]
    fwd.cont.tmp <- fwd.cont[,i]
    fwd.disc.tmp <- fwd.disc[,i]
    tmp <- data.frame(bse.ym,scen.name, scen.tmp,sim.rslt$TIME,sim.rslt$MONTH_SEQ, spot.cont.tmp,spot.disc.tmp, discout.tmp,fwd.cont.tmp, fwd.disc.tmp,stringsAsFactors = F)
    layout[((i-1)*proj.y/dt+1):(i*proj.y/dt),(1:10)] <- tmp
  }
  layout <- data.frame(layout, stringsAsFactors = F)
  names(layout) <- c("BSE_DT","SCEN_ID","SCEN_NO","TIME","MONTH_SEQ","SPOT_CONT","SPOT_DISC","DISCOUNT","FWD_CONT","FWD_DISC")
  return(list(rslt = layout, params = params))
}


#AFNS Shock and Curve Generating RUN
Afns.run <- function(int.full, obs.mat, ufr, ufr.t, int.type = "cont",
                     obs.term = 1/52 , VA = 0.0032,
                     max.lambda = 2, min.lambda = 0.05, accuracy = 1e-12,
                     llp = max(obs.mat), conf.interval = 0.995, bse.dt, opt.paras = NA, para.strc = "KICS")
{
  int.ts <- int.full[,-1]
  #금리 타입에 따라 변환
  if (tolower(int.type) == "cont") { rate <- int.ts
  } else {rate <- Int.disc.to.cont(int.ts)
  }

  #산출 모수가 없는 경우 Optimize
  if(anyNA(opt.paras))
  {
    opt.rlst <- Afns.by.kalman.opt (rate, obs.mat, obs.term, accuracy = accuracy , method = "Nelder-Mead", max.lambda = 2, min.lambda = 0.005, initial.paras = NA, para.strc = para.strc)
    params <- opt.rlst$OPT.PARAS
    state <- opt.rlst$state
  } else
  { #산출된 모수가 있는 경우 해당 모수값을 그대로 사용
    params <- opt.paras

    if(tolower(para.strc) == "kics")
    { #KICS 모수 구조를 적용한 Shock
      state <- Afns.kalman.kics(rate, obs.mat, obs.term, params, returnval = T)
      afns.shock <- Afns.shock.gen.kics (rate, obs.mat ,state ,params, llp ,conf.interval = conf.interval)
    } else
      { #Indpe 모수 구조 적용 Shock
       state <- Afns.kalman.indep (rate, obs.mat, obs.term, params, returnval = T)
       afns.shock <- Afns.shock.gen (rate, obs.mat ,state ,params, llp ,conf.interval = conf.interval)
      }
  }

  shock.int.size <- afns.shock$SHOCK.INT.SIZE
  shock.size <- afns.shock$SHOCK.SIZE

  DNS_CURVE_RSLT <- DNS.sw.curve(rate, obs.mat, ufr, ufr.t,VA, shock.int.size, llp = llp, bse.dt )
  OBS_SHOCK_CURVE <- DNS_CURVE_RSLT$DNS_CURVE_OBS
  SHOCK_CURVE_ALPHA <- DNS_CURVE_RSLT$ALPHA
  BASE_CURVE <-  DNS_CURVE_RSLT$BASE_CURVE ; BASE_VA_CURVE <-  DNS_CURVE_RSLT$BASE_VA_CURVE
  MEAN_CURVE <-  DNS_CURVE_RSLT$MEAN_CURVE ; MEAN_VA_CURVE <-  DNS_CURVE_RSLT$MEANE_VA_CURVE
  UP_CURVE <-  DNS_CURVE_RSLT$UP_CURVE ; UP_VA_CURVE <-  DNS_CURVE_RSLT$UP_VA_CURVE
  DOWN_CURVE <-  DNS_CURVE_RSLT$DOWN_CURVE ; DOWN_VA_CURVE <-  DNS_CURVE_RSLT$DOWN_VA_CURVE
  STEEP_CURVE <-  DNS_CURVE_RSLT$STEEP_CURVE ; STEEP_VA_CURVE <-  DNS_CURVE_RSLT$STEEP_VA_CURVE
  FLAT_CURVE <-  DNS_CURVE_RSLT$FLAT_CURVE ; FLAT_VA_CURVE <-  DNS_CURVE_RSLT$FLAT_VA_CURVE

  return(list(PARAMETERS = params, STATE =  state,  STATE_SHOCK = shock.size, INT.SHOCK = shock.int.size ,
              OBS_SHOCK_CURVE = OBS_SHOCK_CURVE, SHOCK_CURVE_ALPHA = SHOCK_CURVE_ALPHA,
              BASE_CURVE = BASE_CURVE, BASE_VA_CURVE = BASE_VA_CURVE,
              MEAN_CURVE = MEAN_CURVE, MEAN_VA_CURVE = MEAN_VA_CURVE,
              UP_CURVE = UP_CURVE, UP_VA_CURVE = UP_VA_CURVE,
              DOWN_CURVE = DOWN_CURVE, DOWN_VA_CURVE = DOWN_VA_CURVE,
              STEEP_CURVE = STEEP_CURVE, STEEP_VA_CURVE = STEEP_VA_CURVE,
              FLAT_CURVE = FLAT_CURVE, FLAT_VA_CURVE = FLAT_VA_CURVE
  ))
}
#공시이율과 1개월 금리 회귀분석
Gs.int.lm.gongsi <- function(int.indep.a100, int.dep, int.indep.mat, ufr, ufr.t, int.type = "disc")
{
  proj.y <- 1/12

  a100.1m <- data.frame(matrix(NA,length(int.indep.a100[,1]),2))
  names(a100.1m) <- c("BASE_YYMM", "INT_RATE")
  a100.1m[,1] <- int.indep.a100[,1]
  for (i in 1 : length(int.indep.a100[,1]))
  {
    a100.1m[i,2] <- SW.run(as.double(int.indep.a100[i,-1]),int.indep.mat, ufr, ufr.t, proj.y,  int.type = int.type)$SW_CURVE$SPOT_DISC
  }

  mergedata <- merge(a100.1m, int.dep, by = "BASE_YYMM", StingAsFactors = F)

  disc.int.codes <- sort(unique(mergedata$INT_RATE_CD))

  rslt <- data.frame(matrix(NA,  length(disc.int.codes),5))
  names(rslt) <- c("INT_RATE_CD", "INTERCEPT", "COEFFICIENT","ADJ_RSQ", "ERROR")

  for (i in 1: length(disc.int.codes))
  {
    lm.data <-subset(mergedata, INT_RATE_CD == disc.int.codes[i])
    lm.rslt <- lm(lm.data$INT_RATE ~ lm.data$APPL_DISC_RATE)
    lm.rslt2 <- summary(lm.rslt)
    rslt[i,1] <- as.character(disc.int.codes[i])
    rslt[i,2:3] <- lm.rslt$coefficients
    rslt[i,4] <- lm.rslt2$adj.r.squared
    rslt[i,5] <- sum(lm.rslt$residuals^2)
  }

  rslt[rslt$ADJ_RSQ <=0,][,-1] <- NA

  return(rslt)
}

#HW1F PARAMS CALIBRATION FULL
Hw.calibration.full <- function(obs.int,obs.mat,
                         vol.info,swaption.Maturities,swap.Tenors,bse.ym = NA,int.type = "cont",
                         N = 4,L = 100,ufr = 0.045,ufr.t = 60,term = 1/N,llp = max(obs.mat), accuracy = 1e-10){

  proj.y <- max(swaption.Maturities)+max(swap.Tenors)
  alpha <- NA
  min.alpha<-0.001
  max.alpha<-1
  tol<-0.0001

  if (tolower(int.type) == "cont") { rate<-obs.int} else {rate<-Int.disc.to.cont(obs.int) ; int.type<-"cont"}

  #Swap Rate,Price
  swint<-SW.run (rate,obs.mat,ufr,ufr.t,proj.y,term,alpha,min.alpha,max.alpha,tol,llp,NA,F,int.type,F)$SW_CURVE
  swap.info<-swaprate(swint,swaption.Maturities,swap.Tenors,N)$SWAP_INFO
  swption.price <-Atm.Eu.swaption(swint,vol.info ,swaption.Maturities,swap.Tenors,N,L)

  initial.para.set<-c(0.02,0.02)

  set.seed(obs.int[1])

  #Optimize
  calib.full<-
    constrOptim(initial.para.set,Hw1f.para.opt.full,method = "Nelder-Mead",
                ui = rbind(c(1,0),c(-1,0),c(0,1),c(0,-1)),ci = c(0.0001,-1,0,-1),
                #outer.eps = accuracy,
                #control = list(maxit = 30, trace = T),
                control = list(trace = F, reltol = accuracy ),
                swint = swint,swap.info = swap.info,swption.price = swption.price,N = N, L = L)

  opt.full.paras<-calib.full$par

  param.rslt <- data.frame(matrix(NA,1,6))
  names(param.rslt)<-c("BSE_YM","IR_MODEL_TYP","PARAM_CALC_CD","PARAM_TYP_CD","MATURITY","PARAM_VAL")
  A <- "ALPHA";B <- "SIGMA"
  idx <- length(swaption.Maturities)
  param.mat <- c(swaption.Maturities,llp)[-1]

  #Full Calib rslt
  param.rslt[1:2,3] <- "FULL_CALIB"
  param.rslt[1:2,4] <- c(A,B)
  param.rslt[1:2,6]<- opt.full.paras
  param.rslt[1:2,5]<- 100
  param.rslt[,1] <- bse.ym
  param.rslt[,2] <- "HW1"
  return(param.rslt)
  }
#Wilson weight Deriv function
Sw.deriv.wilson <- function(obs.mat, proj.mat, ufrc, alpha){
  deriv <- matrix(NA, length(obs.mat), length(proj.mat))

  for ( i in 1 : length(obs.mat))
  {
    obs.tmp <- obs.mat[i]
    #관측만기가 프로젝션 만기보다 긴 경우
    proj.tmp1 <- proj.mat[proj.mat < obs.tmp]
    deriv[i,(proj.mat %in% proj.tmp1)] <- exp(-ufrc*proj.tmp1-(alpha+ufrc)*obs.tmp)*(ufrc*sinh(alpha*proj.tmp1)-alpha*cosh(alpha*proj.tmp1)-alpha*(ufrc*proj.tmp1-1)*exp(alpha*obs.tmp))

    #프로젝션 만기가 관측만기보다 긴 경우
    proj.tmp2 <- proj.mat[proj.mat >= obs.tmp]
    deriv[i,(proj.mat %in% proj.tmp2)] <- exp(-ufrc*obs.tmp-(alpha+ufrc)*proj.tmp2)*((alpha+ufrc)*sinh(alpha*obs.tmp)-alpha*ufrc*obs.tmp*exp(alpha*proj.tmp2))
  }

  return(deriv)
}

#금리 Smith-Wilson 모형을 이용한 예상 주가 상승률 계산
St.exp.log.return <- function(obs.int, obs.mat, ufr, ufr.t, proj.y, int.type = "cont")
{
  sw <- SW.run(obs.int, obs.mat, ufr, ufr.t, proj.y, term = 1/12, int.type = int.type)
  swint <- sw$SW_CURVE
  discount <- c(1,swint$DISCOUNT)

  #1달 사이에 적용되는 주식의 예상 Return
  exp.log.return.1m <- log(discount[1:(length(discount)-1)] / discount[2:(length(discount))])

  #0 시점에서 해당 만기까지 적용되는 주식의 예상 누적 log Return
  exp.log.return <- rep(NA, length(exp.log.return.1m))
  for (i in 1 : length(exp.log.return.1m))
  {
    exp.log.return[i] <- sum(exp.log.return.1m[1:i])
  }

  #결과정렬
  rslt <- data.frame(swint$TIME, swint$MONTH_SEQ, exp.log.return.1m, exp.log.return)
  names(rslt) <- c("TIME","MONTH_SEQ", "LOG_RETURN_1M", "LOG_RETURN_CUM")
  return(rslt)
}
#단일 모수로 주가 로그노말 모델 시나리오 산출
St.log.sim <- function(s0, drift, sigma, dt = 1/12, num.of.scen = 1000, sim.yr = 100, bse.dt = NA)
{
  sim.yr.dt <- sim.yr/dt

  rand.num <- t(Antithetic.rand(num.of.scen,sim.yr.dt, bse.dt))

  s.t <- matrix(NA, (sim.yr.dt+1), num.of.scen)
  return.t <- s.t[-1,]
  s.t[1,] <- s0
  return.t[1,] <- 0

  for (i in 2 : (sim.yr.dt+1))
  {
    s.t[i,] <- s.t[i-1,] * exp(drift-(0.5*sigma^2)*dt + sigma*sqrt(dt)*rand.num[i-1,] )
    return.t[(i-1),] <- (s.t[i,]/s.t[i-1,])-1
  }

  s.t <- s.t[-1,]

  return(list (STOCK.PRICE = s.t, RETURN = return.t))
}

#가변 변수로 주식 시나리오 산출
St.log.sim.time.series <- function(s0, drift.ts, sigma.ts, dt = 1/12, num.of.scen = 1000, sim.yr = 100, bse.dt = NA)
{
  sim.yr.dt <- sim.yr/dt

  rand.num <- t(Antithetic.rand(num.of.scen,sim.yr.dt, bse.dt))

  s.t <- matrix(NA, (sim.yr.dt+1), num.of.scen)
  return.t <- s.t[-1,]
  s.t[1,] <- s0
  return.t[1,] <- 0

  for (i in 2 : (sim.yr.dt+1))
  {
    drift <- drift.ts[i-1]
    sigma.tmp <- sigma.ts[i-1]
    s.t[i,] <- s.t[i-1,] * exp(drift-(0.5*sigma.tmp^2)*dt + sigma.tmp*sqrt(dt)*rand.num[i-1,])
    return.t[(i-1),] <- (s.t[i,]/s.t[i-1,])-1
  }

  s.t <- s.t[-1,]

  return(list (STOCK.PRICE = s.t, RETURN = return.t))
}

#log-normal 주가 모델 시뮬레이션을 위한 모수 산출
#obs.int : 관측 금리 데이터[1:n array]
#obs.mat : 관측 금리 만기[1:n array]
#volatility.1d : BSE_DT, FNAL_DGS_DT, VOLT로 구성된 m(옵션만기별)개 행을 가진 dataframe --> 만기별로 하나의 변동성만 있어야 함
#BSE_DT : 기준일자, FNAL_DGS_DT : 최종거래일 = 옵션만기일 , VOLT : 내재변동성(σ) 데이터
#ufr : ufr을 "연복리"형태로 입력, ufr.t: sw모델 수렴 시점, proj.y : 산출만기, int.type : ["disc" or "cont"] 금리 타입 입력
St.param.find <- function(obs.int, obs.mat, volatility.1d, ufr = 0.045, ufr.t = 60, proj.y = 100, int.type = "cont" )
{
  #산출주기 "월" 고정
  dt <- 1/12
  max.calc.month <- proj.y/dt

  #만기일자가 기준일자인 항목 제거
  volatility.1d <- subset(volatility.1d, FNAL_DGS_DT != BSE_DT)

  #옵션 만기까지 남은 날짜 계산
  bse.dt.date.form <- as.double(yyyymmdd.to.date(volatility.1d$FNAL_DGS_DT) - yyyymmdd.to.date(volatility.1d$BSE_DT))


  #관측된 변동성 정보의 만기 및 변동성 정보 선언
  option.mat <- bse.dt.date.form
  implied.volatility <- (volatility.1d$VOLT)^2     #DB상의 내재변동성이 σ이기 때문에 σ^2로 변경

  bootstrapped <- St.vol.bootstrap(option.mat, implied.volatility, max.calc.month)

  drifts <- St.exp.log.return(obs.int, obs.mat, ufr, ufr.t, proj.y, int.type = "cont")

  drift.ts <- drifts$LOG_RETURN_1M
  sigma.ts <- bootstrapped$Local_SIGMA

  rslt <- data.frame(drifts$TIME, drifts$MONTH_SEQ, drifts$LOG_RETURN_1M, sigma.ts)
  names(rslt) <- c("TIME", "MONTH_SEQ", "DRIFT.TS", "SIGMA.TS")
  return(rslt)
}
#옵션 내재변동성을 활용한 주가 시나리오 변동성 계산
#Market 변동성을 활용, 해당 시점에 적용되는 Local 변동성 계산
#option.mat : 관측된 옵션의 만기
#implied.volatility : 관측된 옵션의 변동성 수치 ※내재변동성 데이터는 "표준편차" 데이터임. 내재변동성^2를 입력할 것
St.vol.bootstrap <- function (option.mat, implied.volatility, max.calc.month)
{
  #산출 기준을 "월" 단위로 고정
  dt <- 1/12

  #변동성 Bootstrapping 만기 작성
  calc.months <- seq(1,max.calc.month)
  calc.days <- round(calc.months*dt*365)

  # σ_Market^2*T= ∑▒?σ_local^2 (t)*dt?식으로 Bootstrapping
    implied.vol.weight <- implied.volatility*option.mat

    diff.dates <- diff(c(0,option.mat))
    one.d.fwd.vol <- diff(c(0,implied.vol.weight))/diff.dates #Forward Volatility 산출

  #산출 시 fwd.vol이 0미만으로 산출되어 local vol이 음수로 나오는 케이스를 방지하기 위해
  #변동성이 급격하게 커지는 경우를 찾아 제거함
  while(sum(one.d.fwd.vol<0))
  {
    implied.volatility <- implied.volatility[-min(which(one.d.fwd.vol<0))]
    option.mat <- option.mat[-min(which(one.d.fwd.vol<0))]

    implied.vol.weight <- implied.volatility*option.mat

    diff.dates <- diff(c(0,option.mat))
    one.d.fwd.vol <- diff(c(0,implied.vol.weight))/diff.dates
  }

  #Day 단위로 local Volatility 계산
  ond.d.fwd.vol.interp <- rep(NA, max(calc.days))
  idx <- 1
  for (i in 1 : length(ond.d.fwd.vol.interp))
  {
    ond.d.fwd.vol.interp[i] <- one.d.fwd.vol[idx]
    if((i >=option.mat[idx]) & (i < max(option.mat)))
    {idx <- idx+1}
  }

  #Local Volatily를 Market Volatilty로 변환
  VOL.T <- rep(NA,length(calc.days))
  for(i in 1 : length(calc.days))
  {
    VOL.T[i] <- sum(ond.d.fwd.vol.interp[1:calc.days[i]])/calc.days[i]
  }

  rslt <- data.frame(calc.months, VOL.T, ond.d.fwd.vol.interp[calc.days], sqrt(VOL.T), sqrt(ond.d.fwd.vol.interp[calc.days]))
  names(rslt) <- c("MAT_MONTH", "VOLATILITY", "LOCAL_VOLATILITY", "SIGMA", "Local_SIGMA")

  return(rslt)
}
#date형식의 날짜를 yyyymmdd 형식의 날짜로 변환
date.to.yyyymmdd <- function(date.form)
{
  year <- formatC(as.double(format(date.form, "%Y")), width = 2, flag = "0")
  month <- formatC(as.double(format(date.form,"%b")), width = 2, flag = "0")
  day <- formatC(as.double(format(date.form,"%d")), width = 2, flag = "0")

  yyyymmdd <- paste0(year, month, day)
  return(yyyymmdd)
}


#yyyymmdd 형태의 날짜를 date 형식의 날짜로 변환
yyyymmdd.to.date <- function(yyyymmdd)
{
  year <- substr(yyyymmdd,1,4)
  month <- substr(yyyymmdd,5,6)
  day <- substr(yyyymmdd,7,8)
  date.form <- as.Date(paste0(year,"-",month,"-",day))
  return(date.form)
}
#주가 시나리오 시뮬레이션 Run 함수
#s0 : 초기 주가.
#drift.ts : 주가모형의 drift(t).  [double], 월 단위의 drift 입력한다
#sigma.ts : 주가모형의 sigma(t).   [double], 월 단위의 sigma를 입력한다
#num.of.scen : 주가 시나리오 산출 갯수
#sim.yr : 주가 시나리오 산출 년수. 100인 경우 1200개월의 시나리오 산출됨
#bse.dt : 기준일자. 시나리오의 난수를 고정하기 위해 사용함.
#scen.id : 시나리오id. 계산에 들어가지 않고 최종 결과에 시나리오 id로 입력함
St.simulation.run <- function(s0 = 1, drift.ts, sigma.ts, num.of.scen = 200, bse.dt = NA, scen.id = NA)
{
  term <- 1/12
  scenario.length <- min(c(length(drift.ts), length(sigma.ts)))
  sim.yr <- as.integer(scenario.length*term)

  scenario <- St.log.sim.time.series(s0, drift.ts, sigma.ts, dt = term, num.of.scen, sim.yr, bse.dt)


  stock.price <- rep(NA, length(scenario$STOCK.PRICE))
  stock.return.disc <- stock.price

  scen.no <- stock.price

  for (i in 1 : num.of.scen)
  {
    scen.no[(1+(i-1)*scenario.length) : (i*scenario.length)] <- as.integer(i)
    stock.price[(1+(i-1)*scenario.length):(i*scenario.length)] <- scenario$STOCK.PRICE[,i]
    stock.return.disc[(1+(i-1)*scenario.length):(i*scenario.length)] <- scenario$RETURN[,i]
  }

  stock.return.cont <- Int.disc.to.cont(stock.return.disc)

  MONTH_SEQ <- seq(1L,as.integer(scenario.length), by =1L)
  TIME <- round2(MONTH_SEQ/12,6)


  rslt <- data.frame(bse.dt,"ST_SIM",scen.no, TIME, MONTH_SEQ, stock.price, stock.return.disc, stock.return.cont)

  names(rslt) <- c("BSE_DT","SCEN_ID","SCEN_NUM","TIME","MONTH_SEQ", "STOCK_PRICE", "STOCK_RETURN_DISC", "STOCK_RETURN_CONT")

  return(rslt)
}

#AFNS Model Kalman Filter Function With KICS parameter set
Afns.kalman.kics <- function (int.ts.cont, obs.mat, dt, paras, returnval  = F)
{
  len.data <- length(int.ts.cont[,1])

  lambda<-paras[1]

  s11 <- abs(paras[8])

  s21 <- paras[9]
  s22 <- abs(paras[10])
  s31 <- paras[11]
  s32 <- paras[12]
  s33 <- abs(paras[13])
  eta <- paras[14]^2

  s.mat <- matrix(c(s11, s21, s31,0,s22, s32,0, 0, s33), ncol = 3)
  C <- -Afns.ctT(lambda, 0, obs.mat, s.mat)
  kappa <- paras[5:7]
  theta <- paras[2:4]

  #초기화 단계
  coef  <- t(Coef.gen.ns(lambda,obs.mat))

  eig.tmp <- matrix(NA, 3,3 )
  eigen.idx <- 4-rank(kappa)
  eig.tmp[,1] <- eigen.idx ; eig.tmp[,3] <- kappa ; eig.tmp[,2] <- seq(1:3)
  eig.tmp.df  <- data.frame(eig.tmp) ; names(eig.tmp.df) <-  c("eig.idx","eig.seq","kappa")

  eig.tmp.df <- eig.tmp.df[with(eig.tmp.df, order(eig.idx)),]
  eigenvec <- matrix(0, 3,3)
  eigenvec[eig.tmp.df$eig.idx[1],eig.tmp.df$eig.seq[1]] <- 1 ; eigenvec[eig.tmp.df$eig.idx[2],eig.tmp.df$eig.seq[2]] <- 1 ; eigenvec[eig.tmp.df$eig.idx[3],eig.tmp.df$eig.seq[3]] <- 1
  t.eigvec <- t(eigenvec) ; inv.eigvec <- solve(eigenvec)

  f.eigval <- matrix(NA, 3,3)

  for (i in 1:3)
  {
    for (j in 1:3)
    {
      ii <- eig.tmp.df$kappa[i]
      jj <- eig.tmp.df$kappa[j]
      f.eigval[i,j] <- ii+jj
    }
  }

  #Phi Generating
  phi1 <- exp(-kappa*dt)            #상태방정식의 Transition 계산
  mat.phi1 <- diag(phi1)            #상태방정식의 Transition Matrix
  phi0 <- (1-phi1)*theta            #상태방정식의 상수항
  t.phi1 <- t(mat.phi1)             #Transition Matrix의 transformation. 단, Transition Matrix가 daig행렬이므로 동일하게 산출됨
  phi1tphi1 <- phi1%*%t(phi1)       #Sigma_hat 산출에 사용

  smat <- inv.eigvec%*%s.mat%*%t(s.mat)%*%eigenvec
  vlim <- smat/f.eigval
  vmat <- smat*(1-exp(-f.eigval*dt))/f.eigval
  Q    <- eigenvec%*%vmat%*%t.eigvec              #갱신과정에서 적용되는 변수에 저장

  #Initial State Generating
  h.large<-diag(eta, length(obs.mat))
  initial.state <- theta                          #최초 상태방정식의 값은 Theta로 설정
  initial.sigma <- eigenvec%*%vlim%*%t.eigvec     #최초 상태방정식의 분산 설정

  prev.state <- initial.state                     #칼만필터 반복계산의 변수로 사용하기 위한 변수명 재설정
  prev.sigma <- initial.sigma                     #칼만필터 반복계산의 변수로 사용하기 위한 변수명 재설정

  state <- matrix(NA, len.data+1, 3)              #State 변동을 담기 위한 적재공간 설정
  state[1,] <- initial.state                      #최초 상태를 State의 첫 번째 행에 입력
  ll.idx <- rep(NA, len.data)                     #우도를 담기 위한 적재공간 생성

  #Apply Kalman Filter
  len.idx <- len.data+1
  for ( k in 1: len.data)
  {
    #예측 단계
    int.tmp <- as.vector(int.ts.cont[k,])
    xhat <- prev.state*phi1 + phi0
    sigma.hat <- Q + prev.sigma*phi1tphi1                    # t시점에서의 Omega값
    y.implied <- xhat%*%coef+C                                 # 모델에 의해서 예측된 t시점에서의 y값
    v <- as.vector(as.double(int.tmp - y.implied))           # y_t - y_t|t-1
    finv <- solve(t(coef)%*%sigma.hat%*%coef + h.large)      # t 시점에서의 Sigma 집합

    #갱신 단계
    next.sigma <- sigma.hat - sigma.hat%*%coef%*%finv%*%t(coef)%*%sigma.hat
    next.state <- xhat + v%*%finv%*%t(coef)%*%sigma.hat

    #우도함수의 산출
    ll.idx.tmp <- t(v)%*%finv%*%v - log(det(finv))

    #산출된 값을 저장
    state[(k+1),] <- next.state
    ll.idx[k] <- ll.idx.tmp
    prev.state <- next.state
    prev.sigma  <- next.sigma
  }
  names(next.state) <- c("X_L", "X_S", "X_C")

  #Eror Function
  Logik <- (sum(ll.idx[1:len.data]) + length(int.tmp)*len.data*log(2*pi))/2   #우도함수 결과값 계산
  BIC <- length(paras)*log(len.data)+2*Logik

  gc() #속도 개선을 위한 가비지 콜렉션

  if ( returnval == F) {return(Logik)
  } else {return(state = next.state ) }
}
#AFNS_KICS_SHOCK GENERATING FUNCTION
Afns.shock.gen.kics <- function (int.ts.cont, obs.mat ,state ,opt.paras, llp = max(obs.mat),conf.interval = 0.995)
{
  len.data <- length(int.ts.cont[,1])
  opt.lambda    <- opt.paras[1]
  opt.theta     <- opt.paras[2:4]
  opt.kappa     <- opt.paras[5:7]
  opt.s.set     <- opt.paras[8:10]

  s11 <- opt.paras[8]
  s21 <- opt.paras[9]
  s22 <- opt.paras[10]
  s31 <- opt.paras[11]
  s32 <- opt.paras[12]
  s33 <- opt.paras[13]

  opt.coef <- Coef.gen.ns(opt.lambda, obs.mat)

  opt.current.x0 <- state

  kappas <- diag(opt.kappa)
  i.kappas <- diag(1-exp(-opt.kappa))                           # (I-exp(-Kappa*t)), t = 1
  Mean.rev.shock <- i.kappas%*%as.vector(opt.theta - opt.current.x0)     # 평균회귀 충격 산출

  opt.sigma.mat <- matrix(c(s11, s21, s31,0,s22, s32,0, 0, s33), ncol = 3) #AFNS 모수 구조에 따라 설정
  m.matrix <-  solve(kappas)%*%i.kappas%*%opt.sigma.mat         #M matrix 계산 (M)

  #만기에 따른 금리 조정
  obs.matset  <- seq(from = 1, to = llp, by = 1)

  coef.set.tmp <-   Coef.gen.ns(lambda = opt.lambda, maturity = obs.matset)
  weight.coef <- colSums(coef.set.tmp)

  N.matrix <- diag(colSums(coef.set.tmp))%*%m.matrix  #가중치 매트릭스 계산(N)
  NTN <- t(N.matrix)%*%N.matrix                       #t(N)N 계산

  #PCA 분석을 통한 Shock Size 산출
  eigen.shock <- Eigen.fss(NTN)
  eig.vec <- eigen.shock$vectors  #Eigen Vector 2,3 부호가 금융감독원 엑셀과 부호 반대로 산출됨 > 금융감독원 로직으로 설정
  eig.val <- eigen.shock$values

  me <- m.matrix%*%eig.vec        #Me Matrix의 1번 열이 Me_1, 2번 열이 Me_2

  #rotation
  rotation.sum1 <- me[,1]%*%weight.coef
  rotation.sum2 <- me[,2]%*%weight.coef

  rotation.tmp <- rotation.sum2/rotation.sum1
  rotation.agl <- atan(rotation.tmp)
  roattion.mat <- matrix(c(cos(rotation.agl), sin(rotation.agl), -sin(rotation.agl), cos(rotation.agl)),ncol = 2)

  #Shock Size Calculation
  SHOCK <- qnorm(conf.interval)*me[,1:2]%*%roattion.mat
  LEVEL.SHOCK <- SHOCK[,1]
  TWIST.SHOCK <- SHOCK[,2]
  MEAN.REV.SHOCK <- Mean.rev.shock

  #Shock int Size Calculation
  coef.int <- Coef.gen.ns (opt.lambda, obs.mat )
  LEVEL.INT.SHOCK <- coef.int%*%LEVEL.SHOCK
  TWIST.INT.SHOCK <- coef.int%*%TWIST.SHOCK
  MEAN.REV.INT.SHOCK <- coef.int%*%MEAN.REV.SHOCK

  SHOCK.SIZE <- matrix(0, 3,3)
  SHOCK.SIZE[,1] <- MEAN.REV.SHOCK ; SHOCK.SIZE[,2] <- LEVEL.SHOCK ; SHOCK.SIZE[,3] <- TWIST.SHOCK
  SHOCK.SIZE <- data.frame(SHOCK.SIZE) ; names(SHOCK.SIZE) <- c("MEAN.REV.SHOCK","LEVEL.SHOCK","TWIST.SHOCK")

  SHOCK.INT.SIZE <- matrix(0,length(obs.mat),3)
  SHOCK.INT.SIZE[,1] <- MEAN.REV.INT.SHOCK ; SHOCK.INT.SIZE[,2] <- LEVEL.INT.SHOCK ; SHOCK.INT.SIZE[,3] <- TWIST.INT.SHOCK
  SHOCK.INT.SIZE <- data.frame(SHOCK.INT.SIZE) ; names(SHOCK.INT.SIZE) <- c("MEAN.REV.SHOCK","LEVEL.SHOCK","TWIST.SHOCK")

  return(list ( SHOCK.INT.SIZE = SHOCK.INT.SIZE, SHOCK.SIZE = SHOCK.SIZE))
}
#AFNS Model initializer with lambda
Afns.initialize <- function(rate, obs.mat, max.lambda = 2, min.lambda = 0.05, dt = 1/52, para.strc = "KICS")
{
  #1.2 선형회귀를 활용한 Lambda Optimize Function 설정
  len.data <- length(rate[,1])
  optimize.lambda <- function (lambda)
  {
    lsc.tmp  <- matrix(NA, len.data, 3)
    res.tmp  <- rep(NA, len.data)
    for (i in 1: len.data)
    {
      lambda.coef <- Coef.gen.ns(lambda, obs.mat)[,-1]
      int.tmp     <- as.double(rate[i,])
      linst.tmp   <- lm(int.tmp ~ lambda.coef)
      res.tmp[i]  <- sum(linst.tmp$residuals^2)
      lsc.tmp[i,] <- as.double(linst.tmp$coefficients)
    }
    error <- sum(res.tmp)
    return(error)
  }

  #1.3 Optimization Lambda Initial Value /w Optimization
  lambda.opt.rslt <- optim(min.lambda, optimize.lambda,method = "Brent",
                           upper = max.lambda, lower = min.lambda)

  lambda.opt <-lambda.opt.rslt$par #선형회귀로 최적화된 Lambda값 산출

  lsc.tmp  <- matrix(NA, len.data, 3)
  res.tmp  <- rep(NA, len.data)
  res.tmp2  <- matrix(NA, len.data, length(rate[1,]) )


  #tenor별 Coefficient 값 산출, 첫번째 열은 회귀분석의 상수항으로 포함되므로 포함하지 않음
  lambda.coef <- Coef.gen.ns(lambda.opt,obs.mat)[,-1]
  for (i in 1: len.data)
  {
    int.tmp     <- as.double(rate[i,])
    linst.tmp   <- lm(int.tmp ~ lambda.coef)
    res.tmp[i]  <- sum(linst.tmp$residuals^2)
    lsc.tmp[i,] <- as.double(linst.tmp$coefficients)
    res.tmp2[i,]  <- linst.tmp$residuals
  }

  #회귀분석용 데이터 재구성
  XX1 <- lsc.tmp[1:(len.data-1),1]
  YY1 <- lsc.tmp[2:len.data,1]
  XX2 <- lsc.tmp[1:(len.data-1),2]
  YY2 <- lsc.tmp[2:len.data,2]
  XX3 <- lsc.tmp[1:(len.data-1),3]
  YY3 <- lsc.tmp[2:len.data,3]

  #회귀분석 수행
  lm1 <- lm(YY1 ~ XX1)
  lm2 <- lm(YY2 ~ XX2)
  lm3 <- lm(YY3 ~ XX3)

  #회귀분석 결과로 Initial Theta 산출
  theta.l <- lm1$coefficients[1] / (1-lm1$coefficients[2])
  theta.s <- lm2$coefficients[1] / (1-lm2$coefficients[2])
  theta.c <- lm3$coefficients[1] / (1-lm3$coefficients[2])
  #회귀분석 결과로 Initial Kappa 산출
  kappa.11 <- -log(lm1$coefficients[2]) / dt
  kappa.22 <- -log(lm2$coefficients[2]) / dt
  kappa.33 <- -log(lm3$coefficients[2]) / dt

  #회귀분석 결과로 변동성 산출
  if (tolower(para.strc) == "kics")
  { #모수 구조가 kics 구조인 경우
    sigma.mat <- t(chol(var(cbind(lm1$residuals, lm2$residuals, lm3$residuals))))
    eta <- sqrt(mean(res.tmp)) #kics인 경우 단일
    Initial.paras <- as.vector(c(lambda.opt, theta.l, theta.s, theta.c,
                                 max(kappa.11,0.0001), max(kappa.22,0.0001), max(kappa.33,0.0001),
                                 sigma.mat[1,1], sigma.mat[2,1], sigma.mat[2,2], sigma.mat[3,1], sigma.mat[3,2] , sigma.mat[3,3],   #"S11","S21","S22","S31","S32","S33"
                                 eta))
    names(Initial.paras) <- c("LAMBDA", "THETA_L","THETA_S","THETA_C","KAPPA11","KAPPA22","KAPPA33", "S11","S21","S22","S31","S32","S33","ETA")

  } else {
    #모수 구조가 Independent 구조인경우
    sigma.mat <- sqrt(diag(c(var(lm1$residuals), var(lm2$residuals), var(lm3$residuals))))
    eta <- sqrt(diag(var(res.tmp2))) #indep 인 경우 만기 수만큼 산출
    Initial.paras <- as.vector(c(lambda.opt, theta.l, theta.s, theta.c,
                                 max(kappa.11,0.0001), max(kappa.22,0.0001), max(kappa.33,0.0001),
                                 sigma.mat[1,1], sigma.mat[2,2], sigma.mat[3,3],   #"S11","S21","S22","S31","S32","S33"
                                 eta))
    name.tmp <- paste0("ETA_",seq(1:length(eta)))
    names(Initial.paras) <- c("LAMBDA", "THETA_L","THETA_S","THETA_C","KAPPA11","KAPPA22","KAPPA33", "S11","S22","S33",name.tmp)

      }

  return(Initial.paras)
}
#AFNS Kalman Function optimize
Afns.by.kalman.opt <- function(int.ts.cont, obs.mat, dt, accuracy = 1e-12 , method = "Nelder-Mead"
                               , max.lambda = 2, min.lambda = 0.005, initial.paras = NA, para.strc = "KICS")
{
  if(sum(is.na(initial.paras))==1 ){
  initial.paras <- Afns.initialize(int.ts.cont, obs.mat, max.lambda, min.lambda, dt, para.strc)
  }

  if(tolower(para.strc) == "kics"){
    #KICS 모수 구조의 경우 제약조건이 만기에 영향받지 않음
    #lambda constraint
    u1 <- c(1,rep(0,13))
    u2 <- c(-1,rep(0,13))
    #Kappa constraint
    u3 <- c(0,0,0,0,1,0,0,0,0,0,0,0,0,0)
    u4 <- c(0,0,0,0,0,1,0,0,0,0,0,0,0,0)
    u5 <- c(0,0,0,0,0,0,1,0,0,0,0,0,0,0)
    #s11 constraint
    u6 <- c(0,0,0,0,0,0,0,1,0,0,0,0,0,0)
    u7 <- c(0,0,0,0,0,0,0,-1,0,0,0,0,0,0)
    #s22 constraint
    u8 <- c(0,0,0,0,0,0,0,0,0,1,0,0,0,0)
    u9 <- c(0,0,0,0,0,0,0,0,0,-1,0,0,0,0)
    #s33 constraint
    u10 <- c(0,0,0,0,0,0,0,0,0,0,0,0,1,0)
    u11 <- c(0,0,0,0,0,0,0,0,0,0,0,0,-1,0)
    #eta constraint
    u12 <- c(0,0,0,0,0,0,0,0,0,0,0,0,0,1)

    ui <- rbind(u1, u2, u3, u4, u5, u6, u7, u8, u9, u10, u11, u12)
    bound <- c(min.lambda,-max.lambda, 0,0,0, 0,-1, 0,-1, 0,-1, 0)


    cat("\n",format(Sys.time(),usetz = T),"Calibration Start")
    AFNS.rslt <- constrOptim(initial.paras, Afns.kalman.kics, method = method,outer.eps = accuracy,
                             ui =  ui, ci = bound,
                             int.ts.cont = int.ts.cont, obs.mat = obs.mat, dt = dt, returnval = F ,
                             control = list(maxit = 1000, trace = F))
    cat("\n",format(Sys.time(),usetz = T),"Calibration End")
    opt.paras <- AFNS.rslt$par
    opt.ll.value <- AFNS.rslt$value
    BIC <- length(opt.paras)*log(length(int.ts.cont[,1]))+2*opt.ll.value
    state <- Afns.kalman.kics(int.ts.cont, obs.mat, dt, opt.paras, returnval  = T)

  } else {
    const.num <- length(initial.paras)+1-3
    para.num  <- length(initial.paras)

    bound <-  rep(0, const.num)
    bound[1] <- min.lambda
    bound[2] <- -max.lambda

    ui.tmp  <- matrix(0, const.num, para.num)
    ui.tmp[1,1] <- 1     #lambda > min.lambda
    ui.tmp[2,1] <- -1    #-lambda > max.lambda
    for( i in  5 : para.num){
      ui.tmp[i-2,i] <- 1 #s1 ~ s3 >0, eta_n > 0
    }
    cat("\n",format(Sys.time(),usetz = T),"Calibration Start")
    AFNS.rslt <- constrOptim(initial.paras, Afns.kalman.indep, method = method,outer.eps = accuracy,
                             ui =  ui.tmp, ci = bound,
                             int.ts.cont = int.ts.cont, obs.mat = obs.mat, dt = dt, returnval = F ,
                             control = list(maxit = 1000, trace = F))
    cat("\n",format(Sys.time(),usetz = T),"Calibration End")
    opt.paras <- AFNS.rslt$par
    opt.ll.value <- AFNS.rslt$value
    BIC <- length(opt.paras)*log(length(int.ts.cont[,1]))+2*opt.ll.value
    state <- Afns.kalman.kics(int.ts.cont, obs.mat, dt, opt.paras, returnval  = T)
  }
  gc()
  return(list (OPT.PARAS = opt.paras, LOGLIK = opt.ll.value, BIC = BIC, state = state))
}
#외부에서 난수를 입력하는 경우에 주가 시뮬레이션
St.log.sim.time.series.rd <- function(s0, drift.ts, sigma.ts, rd, dt = 1/12, sim.yr = 100)
{
  # s0 : 현재 주가 수준 [integer]
  # drift.ts : 월별 주가 drift 수준으로, 연율로 입력한다. (1,sim.yr/dt) array로 입력
  # sigma.ts : 주가 변동성 수준으로, 연율로 입력한다. (1,sim.yr/dt) array로 입력

  sim.yr.dt <- sim.yr/dt

  rand.num <- rd

  s.t <- matrix(NA, (sim.yr.dt+1), num.of.scen)
  return.t <- s.t[-1,]
  s.t[1,] <- s0
  return.t[1,] <- 0

  for (i in 2 : (sim.yr.dt+1))
  {
    drift <- drift.ts[i-1]
    sigma.tmp <- sigma.ts[i-1]
    s.t[i,] <- s.t[i-1,] * exp(drift-(0.5*sigma.tmp^2)*dt + sigma.tmp*sqrt(dt)*rand.num[i-1,])
    return.t[(i-1),] <- (s.t[i,]/s.t[i-1,])-1
  }

  s.t <- s.t[-1,]

  return(list (STOCK.PRICE = s.t, RETURN = return.t))
}


#주식/주가지수 로그노말 모형 시나리오
St.simulation.run.rd <- function(s0 = 1, drift.ts, sigma.ts, rd, bse.dt = NA, scen.id = NA)
{
  term <- 1/12
  scenario.length <- min(c(length(drift.ts), length(sigma.ts)))
  sim.yr <- as.integer(scenario.length*term)

  scenario <- St.log.sim.time.series.rd(s0, drift.ts, sigma.ts, rd, dt = term, sim.yr)

  stock.price <- rep(NA, length(scenario$STOCK.PRICE))
  stock.return.disc <- stock.price

  scen.no <- stock.price

  for (i in 1 : length(rd[1,]))
  {
    scen.no[(1+(i-1)*scenario.length) : (i*scenario.length)] <- as.integer(i)
    stock.price[(1+(i-1)*scenario.length):(i*scenario.length)] <- scenario$STOCK.PRICE[,i]
    stock.return.disc[(1+(i-1)*scenario.length):(i*scenario.length)] <- scenario$RETURN[,i]
  }

  stock.return.cont <- Int.disc.to.cont(stock.return.disc)

  MONTH_SEQ <- seq(1L,as.integer(scenario.length), by =1L)
  TIME <- round2(MONTH_SEQ/12,6)


  rslt <- data.frame(bse.dt,scen.id,scen.no, TIME, MONTH_SEQ, stock.price, stock.return.disc, stock.return.cont)

  names(rslt) <- c("BSE_DT","SCEN_ID","SCEN_NUM","TIME","MONTH_SEQ", "STOCK_PRICE", "STOCK_RETURN_DISC", "STOCK_RETURN_CONT")

  return(rslt)
}

St.simulation.run.rd.full <- function(s0 = 1, int.disc, mat, atm.put.volt, ufr, ufr.t , proj.y=NA, int.type =NA, rd, bse.dt = NA, scen.id = NA)
{
  St.model.param <- St.param.find(int.disc, mat, atm.put.volt, ufr = ufr, ufr.t = ufr.t, proj.y = 100, int.type = "disc")
  return ( St.simulation.run.rd(s0, St.model.param$DRIFT.TS, St.model.param$SIGMA.TS, rd[i]))
}
#Hull and White bond yield Simulation Module
Hw1f.bond.yield.simulation <- function(obs.int, obs.mat, param.set,
                            bse.ym = NA, num.of.scen = 1000, int.type = "cont",
                            ufr = 0.045, ufr.t = 60, proj.y = 100 , llp = max(obs.mat), duration )
{
dt <- 1/12

duration    <- duration
dur.m       <- ceiling(duration * 12)
dur.y       <- ceiling(duration)
proj.y      <- proj.y + dur.y + 1
sim.dt      <- proj.y/dt
sim.fin.dt  <- (proj.y - dur.y -1)/dt   #final length of term structure
month.seq   <- 1:sim.fin.dt
std.asst.id     <- paste0(c("KTB"),duration,("Y"))
#create Sw curve
sw          <- SW.run(obs.int, obs.mat, ufr, ufr.t, proj.y, 1/12, NA, 0.001, 1, 0.0001, llp, int.type = int.type)
swint       <- sw$SW_CURVE
r0          <- swint$SPOT_CONT[1]
fwd.rate    <- swint$FWD_CONT
fwd.disc    <- swint$FWD_DISC
dis.rate    <- swint$DISCOUNT
spot.cont   <- swint$SPOT_CONT

#create random number
randnum   <- Antithetic.rand(num.of.scen,sim.dt,bse.ym)

#param.set
param.set <- Hw1f.param.adj(params, proj.y)
time.set  <- param.set$MATURITY[-c(sim.dt)]
alpha.set <- param.set$ALPHA[-c(sim.dt)]
sigma.set <- param.set$SIGMA[-c(sim.dt)]

#Theta
theta.t <- rep(NA, sim.dt)

for (i in 1: sim.dt)
{
  spd.tmp    <- alpha.set[i] ; sigma.tmp <- sigma.set[i]
  theta.t[i] <- (fwd.rate[i+1] - fwd.rate[i])/(dt) + spd.tmp*fwd.rate[i] + (sigma.tmp^2)*(1-exp(-2*spd.tmp*i*dt))/(2*spd.tmp)
}

# dicretization functions
# t|T : integral from t to T
###################################
# K(t) =  0|t beta(u)du
# X(t) = 0|t exp(K(u))*sigma(u)dWu
# exp(K(u))*sigma(u) ~ Normal
# Y(t) = 0|t exp(K(t))*theta(t)du
###################################
K_t     <- rep(NA, sim.dt)
X_t     <- matrix(NA, (sim.dt), num.of.scen)
Y_t     <- rep(NA, sim.dt)
K_t[1]  <- alpha.set[1]*dt
X_t[1,] <- exp(K_t[1])*sigma.set[1]*randnum[,1]*dt
Y_t[1]  <- exp(K_t[1])*theta.t[1]*dt

for (i in 2: sim.dt)
{
  K_t[i]  <- K_t[i-1] + alpha.set[i]*dt
  X_t[i,] <- X_t[i-1,] + exp(K_t[i])*sigma.set[i]*randnum[,i]*dt
  Y_t[i]  <- Y_t[i-1] + exp(K_t[i])*theta.t[i]*dt
}

# calculate r(t) short rate
######################################################################
# r(t) = exp(-K(t))[r(0)+ [0|t exp(K(u))*theta(u)du] + exp(-K(t))*X(t)
# F(x) = exp(-K(t))[r(0)+ [0|t exp(K(u))*theta(u)du]
######################################################################

r_t      <- matrix(NA, (sim.dt), num.of.scen)
F_t      <- matrix(NA, (sim.dt), num.of.scen)

for (i in 1: sim.dt)
{
  F_t[i,] <- exp(-K_t[i])*(r0 + Y_t[i])
  r_t[i,] <- F_t[i,] + exp(-K_t[i]) * X_t[i,]
}

r_tt <- matrix(NA, (sim.dt), num.of.scen)    #Short Rate 적재공간 생성
r_tt[1,] <- r0

for (i in 2: sim.dt)
{
  r_tt[i,] <- r_t[i-1,]
}


# Calculate Bond Price
####################################################################
# B(t,T) = exp(-r(t)*C(t,T) - A(t,T))                              #
####################################################################

#############################################################################################################
# Calculate C(t,T)
# 1. C(0,T) = 0|T exp(-K(t))dt
C_t0     <- rep(NA, sim.dt)
C_t0[1]  <- exp(-K_t[1])*dt

for (i in 2:sim.dt)
{
  C_t0[i] <- C_t0[i-1] + exp(-K_t[i])*dt
}

# 2. C(t,T) = exp(K(t))*[ t|T exp(-K(y))dy ]
C_tT     <- rep(NA, sim.dt)

# C_tT[1]  <- exp(-K_t[1])*(C_t0[1+dur.m] - C_t0[1])
C_tT[1]  <- exp(K_t[1])*(C_t0[1+dur.m] - C_t0[1])

for (i in 2:sim.dt)
{
  C_tT[i] <- exp(K_t[i])*(C_t0[i+dur.m] - C_t0[i])
}

##############################################################################################################
#Calculate A(t,T)
# 1.     A(0,T) = 0|T [exp(K(v))a(v)( v|T exp(-K(y)) dy)- (1/12)exp(2K(v))sigma^2(v)(v|T exp(-K(y))dy)^2]dv
# 1-1.   g(t) = v|T exp(-K(y))*dt
# 1-2.   g2(t) = g(t)^2

g1_t  <- matrix(NA, sim.dt-1 , sim.dt-1)
g2_t  <- matrix(NA, sim.dt-1 , sim.dt-1)
g1_tt   <- matrix(NA, sim.dt-1 , sim.dt-dur.m )
g2_tt   <- matrix(NA, sim.dt-1 , sim.dt-dur.m )

# N3 calculate g_t
for (i in 1:sim.dt-1)
{
  for (j in 1:sim.dt-1)
  {
    g1_t[j,i] <-   ifelse( i == j , exp(-K_t[j+1])*dt,
                           ifelse( i >= j+1 , 0,
                                   ifelse( j - i >= dur.m, 0, g1_t[j-1,i]+exp(-K_t[j+1])*dt)))
  }
}

g1_t <- t(g1_t)

# calculate g2_t
for (i in 1:sim.dt-1)
{
  for (j in 1:sim.dt-1)
  {
    g2_t[j,i] <-   g1_t[j,i]^2
  }
}

g1_tt  <- g1_t[,dur.m:(sim.dt-1)]
g2_tt  <- g2_t[,dur.m:(sim.dt-1)]

# 1-3.   L1(t) = t|T [exp(K(v))a(v)( v|T exp(-K(y)) dy)] dv
# 1-3-1. f1(t) = exp(K(v)a(v)
# 1-4.   L2(t) = 0|T [0.5*exp(2K(v))*sigma^2(v)(v|T exp(-K(y))dy)^2]dv
# 1-4-1. f2(t) = 0.5*exp(2*K(t))*sigma^2(t)

f1_t     <- rep(NA, sim.dt-1)
f2_t     <- rep(NA, sim.dt-1)
L1_t     <- matrix(NA, sim.dt-1, (sim.dt-dur.m))
L2_t     <- matrix(NA, sim.dt-1, (sim.dt-dur.m))
L1_t2    <- matrix(NA, (sim.dt-1))
L2_t2    <- matrix(NA, (sim.dt-1))

# calculate f1_t
for (i in 1:sim.dt-1)
{
  f1_t[i] <- exp(K_t[i+1])*theta.t[i+1]*dt
  f2_t[i] <- 0.5*exp(2*K_t[i+1])*sigma.set[i+1]^2*dt
}

# calculate L1_t
for (i in 1:(sim.dt-1))
{
  for (j in 1:(sim.dt-dur.m))
  {
    L1_t[i,j] = g1_tt[i,j]*f1_t[i]
    L2_t[i,j] = g2_tt[i,j]*f2_t[i]
  }
}

L1_t <- L1_t[1:(sim.dt-2),]
L2_t <- L2_t[1:(sim.dt-2),]

for (i in 1:(sim.dt-dur.m))
{
  L1_t2[i]=sum(L1_t[,i])
  L2_t2[i]=sum(L2_t[,i])
}

# 2 A(T) = L1_t2 - L2_t2
A_t     <- rep(NA, (sim.dt-dur.m))
A_tt     <- matrix(NA, (sim.dt-dur.m),num.of.scen)

for (i in 1:(sim.dt-dur.m))
{
  A_t[i] <- L1_t2[i] - L2_t2[i]
  A_tt[i,] <- A_t[i]
}

######################################################################################
# B(t,T) = exp(-r(t)*C(t,T) - A(t,T))
# H(t) = r(t)*C(t,T)
######################################################################################
# r_t      <- matrix(NA, (sim.dt), num.of.scen)
# C_tT     <- rep(NA, sim.dt)

r_t  <- r_t[1:sim.fin.dt,]
C_tT <- C_tT[1:sim.fin.dt]
A_tt <- A_tt[1:sim.fin.dt,]

B_t <- matrix(NA, (sim.fin.dt), num.of.scen)
B_t[1,] <- dis.rate[1]

for (i in 2:sim.fin.dt)
{
  B_t[i,] <- exp(-1*r_t[i-1,]*C_tT[i]-A_tt[i-1,])
}

# calculate yield
# int_rate = - ln(B(t,T))/(T-t)
# yied_rate = int_rate[i-1]/12 - duration(int_rate[i]-int_rate[i-1])
r_tt <- matrix(NA, (sim.fin.dt+1), num.of.scen)
y_tt <- matrix(NA, (sim.fin.dt+1), num.of.scen)

r_tt[1,] <- spot.cont[dur.m]
#r_td[1,] <- fwd.disc[dur.m]
y_tt[1,] <- 0

for (i in 2:(sim.fin.dt+1))
{
  r_tt[i,] <- -1*log(B_t[i-1,])/(duration)
  #r_td[i,] <- exp(r_tt[i,])-1
  y_tt[i,] <- r_tt[i-1,]/12 - duration*(r_tt[i,]-r_tt[i-1,])
  # y_tt[i,] <- r_td[i-1,]/12 - duration*(r_td[i,]-r_td[i-1,])
}

y_tt <- y_tt[-1,]

B_t  <- B_t[1:sim.fin.dt,]
y_tt <- y_tt[1:sim.fin.dt,]

hw_yield_rslt <- Hw1f.bond.output(bse.ym,std.asst.id,month.seq, y_tt,B_t, num.of.scen)
return(hw_yield_rslt)
}

# hw bond yield layout
Hw1f.bond.output <- function(bse.dt, std.asst.id, month.seq, yield.rate ,b.price, num.of.scen = 1000)
{
  #  month.seq <- simulation.output$MONTH_SEQ
  month.seq <- month.seq

  num.time <- length(month.seq)
  scen.bucekt <- seq(1:num.of.scen)

  #Insert variable
  yield.rate <- yield.rate
  bond_p     <- b.price

  layout <- matrix(NA, num.time*num.of.scen,6)
  layout <- data.frame(layout)

  #Layout
  for( i in 1: num.of.scen)
  { #i <- 1
    scen.tmp <- scen.bucekt[i]
    yield.rate.tmp <- yield.rate[,i]
    bond_p.tmp <- bond_p[,i]
    tmp <- data.frame(bse.ym,std.asst.id,scen.tmp,month.seq, yield.rate.tmp,bond_p.tmp,stringsAsFactors = F)
    layout[((i-1)*num.time+1):(i*num.time),(1:6)] <- tmp
  }
  layout <- data.frame(layout, stringsAsFactors = F)
  names(layout) <- c("BASE_YM","STD_ASST_CD", "SCEN_NO","MONTH_SEQ","YIELD_RATE","BOND_PRICE")
  return(layout)
}