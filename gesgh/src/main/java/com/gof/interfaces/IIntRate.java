package com.gof.interfaces;

/**
 * <p> �� Ŭ������ ��� �� Ŭ������ ��ǥ ������ �ǹ���.
 * <p> ������ ����� ������.
 *  
 * @author takion77@gofconsulting.co.kr 
 * @version 1.0
 */
public interface IIntRate {
	public String getBaseYymm();
	public String getIrCurveId();
	public String getMatCd();
	public Double getIntRate();
	public Double getSpread();
	
}
	
	
