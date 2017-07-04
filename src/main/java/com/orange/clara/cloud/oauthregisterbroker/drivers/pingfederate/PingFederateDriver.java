package com.orange.clara.cloud.oauthregisterbroker.drivers.pingfederate;

import com.orange.clara.cloud.oauthregisterbroker.drivers.AbstractDriver;
import com.orange.clara.cloud.oauthregisterbroker.drivers.Driver;
import com.orange.clara.cloud.oauthregisterbroker.exception.DriverException;
import com.orange.clara.cloud.oauthregisterbroker.exception.DriverRegisterException;
import com.orange.clara.cloud.oauthregisterbroker.exception.DriverUnregisterException;
import com.orange.clara.cloud.oauthregisterbroker.model.OauthClient;
import com.orange.clara.cloud.oauthregisterbroker.model.ProviderInformation;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

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
@Order(4)
public class PingFederateDriver extends AbstractDriver implements Driver {

    @Autowired
    private PingFederateConnector pingFederateConnector;
    
    private Logger logger = LoggerFactory.getLogger(PingFederateDriver.class);

    @Override
    public OauthClient register(ProviderInformation providerInformation, CloudApplication app, List<String> grantTypes, List<String> scopes, String redirectPath) throws DriverException {
      	List<String> uris = this.getVerifiedUris(app.getUris());
      	
      	OauthClient oauthClient = new OauthClient();
        oauthClient.setId(app.getMeta().getGuid().toString());
        oauthClient.setClientId(this.createClientId(app));
        oauthClient.setSecret(this.createClientPassword());
        // cannot fetch org details at this moment
        // oauthClient.setAppName(app.getSpace().getOrganization().getName() + "_" + app.getSpace().getName() + "_" + app.getName());
        oauthClient.setAppName("client_" + app.getName() + "_" + app.getSpace().getName());
        oauthClient.setScopes(String.join(",", scopes));
        oauthClient.setGrantTypes(String.join(",", grantTypes).toUpperCase());
        
        /*
         * Creation of full callback urls
         */
        for (int i = 0 ; i < uris.size() ; i++) {
        	uris.set(i, uris.get(i) + redirectPath);
        }
        
        /*
         * Move grant types to upper cases to match PingFederate model
         */
        for (int i = 0 ; i < grantTypes.size() ; i++) {
        	grantTypes.set(i, grantTypes.get(i).toUpperCase());
        }
        
        try {
            this.pingFederateConnector.register(
            		providerInformation.getUsername(),
            		providerInformation.getPassword(),
            		oauthClient.getClientId(),
            		oauthClient.getAppName(),
            		oauthClient.getSecret(),
            		grantTypes,
            		uris
    		);
            this.pingFederateConnector.replicate(providerInformation.getUsername(), providerInformation.getPassword());
        } catch (IOException e) {
            throw new DriverRegisterException(this, e.getMessage());
        }
        
        return oauthClient;
    }

    @Override
    public void unregister(ProviderInformation providerInformation, OauthClient oauthClient) throws DriverException {
        try {
            this.pingFederateConnector.unregister(
                    providerInformation.getUsername(),
                    providerInformation.getPassword(),
                    oauthClient.getClientId()
            );
            this.pingFederateConnector.replicate(providerInformation.getUsername(), providerInformation.getPassword());
        } catch (IOException e) {
            throw new DriverUnregisterException(this, e.getMessage());
        }
    }

    @Override
    public String getDriverName() {
        return "pingfederate";
    }
}
