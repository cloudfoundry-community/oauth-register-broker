package com.orange.clara.cloud.oauthregisterbroker.drivers;

import com.google.common.collect.Lists;
import com.orange.clara.cloud.oauthregisterbroker.exception.DriverConnectionException;
import com.orange.clara.cloud.oauthregisterbroker.exception.DriverRegisterException;
import com.orange.clara.cloud.oauthregisterbroker.exception.DriverUnregisterException;
import com.orange.clara.cloud.oauthregisterbroker.model.OauthClient;
import com.orange.clara.cloud.oauthregisterbroker.service.OauthRegInstanceService;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.identity.uaa.api.UaaConnectionFactory;
import org.cloudfoundry.identity.uaa.api.client.UaaClientOperations;
import org.cloudfoundry.identity.uaa.api.common.UaaConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Copyright (C) 2016 Orange
 * <p>
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE' in this package distribution
 * or at 'https://opensource.org/licenses/Apache-2.0'.
 * <p>
 * Author: Arthur Halet
 * Date: 15/05/2016
 */
@Component
@Order(1)
public class UaaDriver extends AbstractDriver implements Driver {
    @Autowired
    @Qualifier("getUaaUrl")
    protected String uaaUrl;


    private Logger logger = LoggerFactory.getLogger(OauthRegInstanceService.class);

    private UaaConnection connect(String providerUser, String providerPassword) throws MalformedURLException, DriverConnectionException {
        if (uaaUrl == null) {
            throw new DriverConnectionException("Uaa is not registered in the broker, this driver can't be used");
        }
        ClientCredentialsResourceDetails credentials = new ClientCredentialsResourceDetails();


        credentials.setAccessTokenUri(uaaUrl + "/oauth/token");
        credentials.setClientAuthenticationScheme(AuthenticationScheme.header);
        credentials.setClientId(providerUser);
        credentials.setClientSecret(providerPassword);

        URL uaaHost = new URL(uaaUrl);
        return UaaConnectionFactory.getConnection(uaaHost, credentials);
    }

    @Override
    public OauthClient register(String providerUser, String providerPassword, CloudApplication app, List<String> grantTypes, List<String> scopes, String redirectPath) throws DriverRegisterException {
        UaaConnection connection = null;
        try {
            connection = this.connect(providerUser, providerPassword);
        } catch (Exception e) {
            throw new DriverRegisterException("Error during connection:" + e.getMessage(), e);
        }
        UaaClientOperations clientOperations = connection.clientOperations();
        BaseClientDetails clientDetails = new BaseClientDetails(
                this.createClientId(app),
                "none",
                String.join(",", scopes),
                String.join(",", grantTypes),
                String.join(",", scopes),
                this.createUris(app, redirectPath)
        );
        clientDetails.setClientSecret(this.createClientPassword());
        clientOperations.create(clientDetails);
        return new OauthClient(
                clientDetails.getClientId(),
                clientDetails.getClientSecret(),
                uaaUrl + "/oauth/token",
                uaaUrl + "/oauth/authorize",
                uaaUrl + "/userinfo",
                String.join(",", scopes),
                String.join(",", grantTypes)
        );
    }

    @Override
    public void unregister(String providerUser, String providerPassword, OauthClient oauthClient) throws DriverUnregisterException {
        UaaConnection connection = null;
        try {
            connection = this.connect(providerUser, providerPassword);
        } catch (Exception e) {
            throw new DriverUnregisterException("Error during connection:" + e.getMessage(), e);
        }
        UaaClientOperations clientOperations = connection.clientOperations();
        clientOperations.delete(oauthClient.getId());
    }

    @Override
    public String getDriverName() {
        return "uaa";
    }

    private String createUris(CloudApplication app, String redirectPath) {
        List<String> finalUris = Lists.newArrayList();
        List<String> uris = this.getVerifiedUris(app.getUris());
        for (String uri : uris) {
            finalUris.add(uri + redirectPath);
        }
        return String.join(",", finalUris);
    }
}
