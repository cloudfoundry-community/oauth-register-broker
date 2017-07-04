package com.orange.clara.cloud.oauthregisterbroker.drivers.pingfederate;

public class APiResult {
	
	private String resultId;
	private String message;
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public String getResultId() {
		return resultId;
	}

	public void setResultId(String resultId) {
		this.resultId = resultId;
	}
}
