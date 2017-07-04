package com.orange.clara.cloud.oauthregisterbroker.drivers.pingfederate;

public class ClientAuth {

	private String secret;
    private String type;
    
    public ClientAuth() {
		super();
	}
    
    public ClientAuth(String secret, String type) {
		this.secret = secret;
		this.type = type;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
