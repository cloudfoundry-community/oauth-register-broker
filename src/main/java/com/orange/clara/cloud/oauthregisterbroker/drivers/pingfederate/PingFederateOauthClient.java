package com.orange.clara.cloud.oauthregisterbroker.drivers.pingfederate;

import java.util.HashSet;

public class PingFederateOauthClient {
    
    private HashSet<String> grantTypes;
    private String name;
    private String clientId;
    private HashSet<String> redirectUris;
    private ClientAuth clientAuth;
    private boolean bypassApprovalPage;

    public PingFederateOauthClient() {

    }

	public HashSet<String> getGrantTypes() {
		return grantTypes;
	}

	public void setGrantTypes(HashSet<String> grantTypes) {
		this.grantTypes = grantTypes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public HashSet<String> getRedirectUris() {
		return redirectUris;
	}

	public void setRedirectUris(HashSet<String> redirectUris) {
		this.redirectUris = redirectUris;
	}

	public ClientAuth getClientAuth() {
		return clientAuth;
	}

	public void setClientAuth(ClientAuth clientAuth) {
		this.clientAuth = clientAuth;
	}

	public boolean isBypassApprovalPage() {
		return bypassApprovalPage;
	}

	public void setBypassApprovalPage(boolean bypassApprovalPage) {
		this.bypassApprovalPage = bypassApprovalPage;
	}

    

}
