package com.ndc.bean;

public class NDCRxCUIMapping {

	private String ndcCode;

	private String rxcui;

	private String processed;

	public NDCRxCUIMapping() {
	}

	public NDCRxCUIMapping(String ndcCode, String rxcui, String isProcessed) {
		this.ndcCode = ndcCode;
		this.rxcui = rxcui;
		this.processed = isProcessed;
	}

	public String getNdcCode() {
		return ndcCode;
	}

	public void setNdcCode(String ndcCode) {
		this.ndcCode = ndcCode;
	}

	public String getRxcui() {
		return rxcui;
	}

	public void setRxcui(String rxcui) {
		this.rxcui = rxcui;
	}

	public String getIsProcessed() {
		return processed;
	}

	public void setIsProcessed(String isProcessed) {
		this.processed = isProcessed;
	}

	@Override
	public String toString() {
		return "NDCRxCUIMapping [ndcCode=" + ndcCode + ", rxcui=" + rxcui + ", isProcessed=" + processed + "]";
	}

}
