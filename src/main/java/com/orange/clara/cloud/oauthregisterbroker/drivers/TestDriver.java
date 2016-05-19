package com.orange.clara.cloud.oauthregisterbroker.drivers;

import com.orange.clara.cloud.oauthregisterbroker.exception.DriverException;
import com.orange.clara.cloud.oauthregisterbroker.model.OauthClient;
import com.orange.clara.cloud.oauthregisterbroker.model.ProviderInformation;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Copyright (C) 2016 Orange
 * <p>
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE' in this package distribution
 * or at 'https://opensource.org/licenses/Apache-2.0'.
 * <p>
 * Author: Arthur Halet
 * Date: 16/05/2016
 */
@Component
@Order(2)
public class TestDriver implements Driver {


    @Override
    public OauthClient register(ProviderInformation providerInformation, CloudApplication app, List<String> grantTypes, List<String> scopes, String redirectPath) throws DriverException {
        return new OauthClient(
                UUID.randomUUID().toString(),
                app.getName(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "http://test.com/oauth/token",
                "http://test.com/oauth/authorize",
                "http://test.com/userinfo",
                String.join(",", scopes),
                String.join(",", grantTypes)
        );
    }

    @Override
    public void unregister(ProviderInformation providerInformation, OauthClient oauthClient) throws DriverException {

    }

    @Override
    public String getDriverName() {
        return "test";
    }
}
