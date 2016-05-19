package com.orange.clara.cloud.oauthregisterbroker.drivers.github;

import com.orange.clara.cloud.oauthregisterbroker.drivers.AbstractDriver;
import com.orange.clara.cloud.oauthregisterbroker.drivers.Driver;
import com.orange.clara.cloud.oauthregisterbroker.exception.DriverException;
import com.orange.clara.cloud.oauthregisterbroker.exception.DriverRegisterException;
import com.orange.clara.cloud.oauthregisterbroker.exception.DriverUnregisterException;
import com.orange.clara.cloud.oauthregisterbroker.model.OauthClient;
import com.orange.clara.cloud.oauthregisterbroker.model.ProviderInformation;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Copyright (C) 2016 Orange
 * <p>
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE' in this package distribution
 * or at 'https://opensource.org/licenses/Apache-2.0'.
 * <p>
 * Author: Arthur Halet
 * Date: 18/05/2016
 */
@Component
@Order(3)
public class GithubDriver extends AbstractDriver implements Driver {

    @Autowired
    private GithubConnector githubConnector;

    @Override
    public OauthClient register(ProviderInformation providerInformation, CloudApplication app, List<String> grantTypes, List<String> scopes, String redirectPath) throws DriverException {
        List<String> uris = this.getVerifiedUris(app.getUris());
        OauthClient oauthClient = null;
        try {
            oauthClient = this.githubConnector.register(
                    providerInformation.getUsername(),
                    providerInformation.getPassword(),
                    providerInformation.getAuthenticationCode(),
                    this.createClientId(app),
                    uris.get(0),
                    uris.get(0) + redirectPath
            );
        } catch (IOException e) {
            throw new DriverRegisterException(this, e.getMessage());
        }
        oauthClient.setAppName(app.getName());
        oauthClient.setScopes(String.join(",", scopes));
        oauthClient.setGrantTypes("authorization_code");
        oauthClient.setAccessTokenUri(GithubConnector.GITHUB_HOST + "/oauth/token");
        oauthClient.setUserAuthorizationUri(GithubConnector.GITHUB_HOST + "/oauth/authorize");
        oauthClient.setUserInfoUri(GithubConnector.GITHUB_HOST + "/userinfo");
        return oauthClient;
    }

    @Override
    public void unregister(ProviderInformation providerInformation, OauthClient oauthClient) throws DriverException {
        try {
            this.githubConnector.unregister(
                    providerInformation.getUsername(),
                    providerInformation.getPassword(),
                    providerInformation.getAuthenticationCode(),
                    oauthClient.getId()
            );
        } catch (IOException e) {
            throw new DriverUnregisterException(this, e.getMessage());
        }
    }

    @Override
    public String getDriverName() {
        return "github";
    }
}
