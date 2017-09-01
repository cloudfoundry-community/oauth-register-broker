package com.orange.clara.cloud.oauthregisterbroker.drivers.pingfederate;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.orange.clara.cloud.oauthregisterbroker.config.PingFederateConfig;
import com.orange.clara.cloud.oauthregisterbroker.exception.DriverException;

/**
 * Copyright (C) 2017 AIR FRANCE KLM
 * <p>
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE' in this package distribution
 * or at 'https://opensource.org/licenses/Apache-2.0'.
 * <p>
 * Author: Jerome Djebari
 * Date: 02/05/2017
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PingFederateConnector {
	
	@Autowired
	private String pingApiUri;
    
    private Logger logger = LoggerFactory.getLogger(PingFederateConnector.class);
    private static final String PINGFEDERATE_REPLICATION_ENDPOINT = "/cluster/replicate";
    private static final String PINGFEDERATE_OAUTH_CLIENTS_ENDPOINT = "/oauth/clients";
    private static final String PINGFEDERATE_OAUTH_CLIENTS_ENDPOINT_DELETE = PINGFEDERATE_OAUTH_CLIENTS_ENDPOINT + "/{id}";
    
    public PingFederateConnector() {
        super();
    }
    
    public void register(String user, String password, String clientId, String clientName, String secret, List<String> grantTypes, List<String> redirectUris) throws IOException, DriverException {
    	HttpHeaders headers = getHttpHeaders(user, password);
		
		PingFederateOauthClient client = new PingFederateOauthClient();
		client.setClientId(clientId);
		client.setName(clientName);
        client.setGrantTypes(new HashSet<String>(grantTypes));
        client.setClientAuth(new ClientAuth(secret, "SECRET"));
        client.setBypassApprovalPage(true);
        
        HashSet<String> uris = new HashSet<String>(redirectUris);
        client.setRedirectUris(uris);
        
		HttpEntity<PingFederateOauthClient> requestEntity = new HttpEntity<PingFederateOauthClient>(client, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
        	logger.debug(pingApiUri + PINGFEDERATE_OAUTH_CLIENTS_ENDPOINT);
        	restTemplate.postForObject(pingApiUri + PINGFEDERATE_OAUTH_CLIENTS_ENDPOINT, requestEntity, PingFederateOauthClient.class);
        }
        catch(HttpClientErrorException e) {
        	logger.error(e.getStatusCode().toString() + ":" + e.getResponseBodyAsString());
        	throw new DriverException("Error occured when registering on ping federate, Response : (" + e.getStatusCode() + ") " + e.getResponseBodyAsString());
        }

    }

	public void unregister(String user, String password, String clientId) throws IOException, DriverException {
		HttpHeaders headers = getHttpHeaders(user, password);
			     
	    Map<String, String> params = new HashMap<String, String>();
	    params.put("id", clientId);
	     
	    RestTemplate restTemplate = new RestTemplate();
	    
	    HttpEntity<String> entity = new HttpEntity<String>(headers);
	    try {
	    	logger.debug(pingApiUri + PINGFEDERATE_OAUTH_CLIENTS_ENDPOINT_DELETE);
	    	ResponseEntity<String> result = restTemplate.exchange(pingApiUri + PINGFEDERATE_OAUTH_CLIENTS_ENDPOINT_DELETE, HttpMethod.DELETE, entity, String.class, params);
	    }
	    catch(HttpClientErrorException e) {
        	logger.error(e.getStatusCode().toString() + ":" + e.getResponseBodyAsString());
        	throw new DriverException("Error occured when unregistering on ping federate, Response message : (" + e.getStatusCode() + ") " + e.getResponseBodyAsString());
        }
    }
	
	public void replicate(String user, String password) throws DriverException {
		HttpHeaders headers = getHttpHeaders(user, password);
		
		HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
		
        RestTemplate restTemplate = new RestTemplate();
        try {
        	logger.debug(pingApiUri + PINGFEDERATE_REPLICATION_ENDPOINT);
        	restTemplate.postForObject(pingApiUri + PINGFEDERATE_REPLICATION_ENDPOINT, requestEntity, APiResult.class);
        }
        catch(HttpClientErrorException e) {
        	logger.error(e.getStatusCode().toString() + ":" + e.getResponseBodyAsString());
        	throw new DriverException("Error occured when replicating ping federate configuration, Response message : (" + e.getStatusCode() + ") " + e.getResponseBodyAsString());
        }
	}
	
	public HttpHeaders getHttpHeaders(String user, String password) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Xsrf-Header","PingAccess");
		headers.set(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((user + ":" + password).getBytes()));
		return headers;
	}

}
