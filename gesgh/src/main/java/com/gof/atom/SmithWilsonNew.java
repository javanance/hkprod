package com.gof.atom;

import java.util.Arrays;
import java.util.function.UnaryOperator;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BisectionSolver;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.analysis.solvers.MullerSolver2;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Getter
@Setter
public class SmithWilsonNew extends TermStructure implements UnivariateFunction{
	
	private DVector term;
	private DVector rate;
	private double ufr;
	private double ufrt;
	private double llp;
	private double alpha;
	private DVector zeta;
	
	// Initialization
	public SmithWilsonNew(DVector term, DVector rate, double alpha, double ufr, double llp) {
		this.ufr = ufr;
//		this.ufr = Math.log(1+ufr);
		this.ufrt = 60.0;
		this.llp = llp;
		this.term = term.copy();
		this.rate = rate.copy();
		this.setupAlpha(alpha);
	}
	
	public SmithWilsonNew(DVector term, DVector rate, double ufr, double llp) {
		this.ufr = ufr;
//		this.ufr = Math.log(1+ufr);
		this.ufrt = 60.0;
		this.llp = llp;
		this.term = term.copy();
		this.rate = rate.copy();
		log.info("qqqq : {}");
		this.setupAlpha(this.calcAlpha1());
		Arrays.stream(zeta.getData()).forEach(s-> log.info("zeta : {},{}", s, alpha));
	}
	
	// Wilson Function
	private double calcWilsonWeight(double t, double u, int order) {
		double wilson;
		if (order==0) {
			wilson = Math.exp(-ufr*(t+u)) * (alpha*Math.min(t, u) - Math.exp(-alpha*Math.max(t, u))*Math.sinh(alpha*Math.min(t, u)));
		} else if (order==1) {
			if (t<u)
				wilson = Math.exp(-ufr*t-(alpha+ufr)*u)*(ufr*Math.sinh(alpha*t)-alpha*Math.cosh(alpha*t)-alpha*(ufr*t-1)*Math.exp(alpha*u));
			else
				wilson = Math.exp(-ufr*u-(alpha+ufr)*t)*((alpha+ufr)*Math.sinh(alpha*u)-alpha*ufr*u*Math.exp(alpha*t));		
		} else if (order==2) {
			if (t<u)
				wilson = Math.exp(-ufr*t-(alpha+ufr)*u)*(-(alpha*alpha+ufr*ufr)*Math.sinh(alpha*t)+2*alpha*ufr*Math.cosh(alpha*t)+alpha*ufr*(ufr*t-2)*Math.exp(alpha*u));
			else
				wilson = Math.exp(-ufr*u-(alpha+ufr)*t)*(alpha*ufr*ufr*u*Math.exp(alpha*t)-(alpha+ufr)*(alpha+ufr)*Math.sinh(alpha*u));
		} else {
			throw new RuntimeException("��ȿ���� ���� �����Դϴ�.");
		}
		return wilson;
	}
	
	private DVector calcWilsonWeight(double t, DVector columns, int order) {
		return columns.map(x -> calcWilsonWeight(t, x, order));
	}

	private DMatrix calcWilsonWeight(DVector rows, DVector columns, int order) {
		int n = rows.getDimension();
		DVector[] v = new DVector[n];
		for(int i=0; i<n; i++) {
			v[i] = calcWilsonWeight(rows.getEntry(i), columns, order);
		}	
		return DMatrix.concatenateRowVector(v);
	}
	
	// Calculate Zeta
	private DVector calcZeta() {
		DMatrix InvW = this.calcWilsonWeight(this.term, this.term, 0).inverse();
		DVector m_mu = this.rate.map(x -> 1/(1+x))
								.binaryMap(this.term, (x, y) -> Math.pow(x, y))				//annual compound rate!!!!
								.add(this.term.map(x -> -Math.exp(-this.ufr*x)));
		return InvW.operate(m_mu);
	}
	
//  1bp under continuous compound!!!!
	public double calcAlpha() {
		GoldenSectionSearch optimizer = new GoldenSectionSearch();
		UnaryOperator<Double> fn = a -> {
			SmithWilsonNew sw = new SmithWilsonNew(this.term, this.rate, a.doubleValue(), this.ufr, this.llp);
			// log(1+0.045) - 60�� ����, ���Ӵ��� ���������ݸ�
			return Math.abs(sw.forward(ufrt)-ufr);
//			return 0.0001 - Math.abs(sw.forward(ufrt)-ufr);
//			return Math.abs(0.0001 - Math.abs(sw.forward(Math.max(llp + 40, ufrt))-ufr));
//			return Math.abs(0.0001 - Math.abs(Math.exp(sw.forward(Math.max(llp+40, 60), 0))-Math.exp(ufr)));
//			return Math.abs(0.0001 - Math.abs(Math.exp(sw.forward(ufrt))-Math.exp(ufr)));
		};
//		log.info("zzz : {},{}", aaa);
		return Math.round(optimizer.optimize(fn, 0.001, 1)*1_000_000)/1_000_000;
	}	
	
	public double calcAlpha1() {
		BisectionSolver optimizer = new BisectionSolver(0.000000001);
//		BrentSolver optimizer = new BrentSolver();
//		MullerSolver2 optimizer = new MullerSolver2();
		
		
		double sol =optimizer.solve(10000, this, 0.001, 1);
		log.info("optimizer :  {},{},{},{}", sol, optimizer.getFunctionValueAccuracy(), optimizer.getEvaluations(), optimizer.getStartValue());
		
		return sol;
	}	
	
	// Bond Price
	public double bond(double t, int order) {
		double terms1 = Math.pow(-ufr, order)*Math.exp(-ufr*t);
		double terms2 = calcWilsonWeight(t, term, order).dotProduct(zeta);
		return terms1+terms2;
	}
	
	private void setupAlpha(double alpha) {
		this.alpha = alpha;
		this.zeta = calcZeta();
	}

	@Override
	public double value(double alpha1) {
		SmithWilsonNew sw = new SmithWilsonNew(this.term, this.rate, alpha1, this.ufr, this.llp);
//		log.info("value : {},{},{}", alpha1, sw.forward(ufrt), 0.00009 - Math.abs(sw.forward(ufrt)-ufr));
//		return 0.000099 - Math.abs(sw.forward1M(ufrt + 1/12.0)-(Math.exp(ufr)- 1.0));
		return 0.000099 - Math.abs(sw.forward1M(ufrt - 1/12.0)-ufr);
	}
}