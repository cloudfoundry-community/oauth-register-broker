package com.orange.clara.cloud.oauthregisterbroker.drivers;

import com.orange.clara.cloud.oauthregisterbroker.exception.DriverException;
import com.orange.clara.cloud.oauthregisterbroker.model.OauthClient;
import com.orange.clara.cloud.oauthregisterbroker.model.ProviderInformation;
import org.cloudfoundry.client.lib.domain.CloudApplication;

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
//authorization_code
public interface Driver {

    OauthClient register(ProviderInformation providerInformation, CloudApplication app, List<String> grantTypes, List<String> scopes, String redirectPath) throws DriverException;

    void unregister(ProviderInformation providerInformation, OauthClient oauthClient) throws DriverException;

    String getDriverName();
}
