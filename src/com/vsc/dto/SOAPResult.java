package com.vsc.dto;

public class SOAPResult {
	protected String soapMsg = null;
	protected int RESPONSE_CODE = 200;
	protected int ERROR_CODE = 0;
	
	public SOAPResult(){};
	public SOAPResult(String result){
		soapMsg = result;
	}
	public String getSoapResult(){
		return soapMsg;
	}
	public void setSoapResult(String result){
		soapMsg = result;
	}
	public int getERROR_CODE() {
		return ERROR_CODE;
	}
	public void setERROR_CODE(int eRROR_CODE) {
		ERROR_CODE = eRROR_CODE;
	}
	public int getRESPONSE_CODE() {
		return RESPONSE_CODE;
	}
	public void setRESPONSE_CODE(int rESPONSE_CODE) {
		RESPONSE_CODE = rESPONSE_CODE;
	}
}
