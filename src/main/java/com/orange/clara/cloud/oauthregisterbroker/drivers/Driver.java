package com.orange.clara.cloud.oauthregisterbroker.drivers;

import com.orange.clara.cloud.oauthregisterbroker.exception.DriverRegisterException;
import com.orange.clara.cloud.oauthregisterbroker.exception.DriverUnregisterException;
import com.orange.clara.cloud.oauthregisterbroker.model.OauthClient;
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

    OauthClient register(String providerUser, String providerPassword, CloudApplication app, List<String> grantTypes, List<String> scopes, String redirectPath) throws DriverRegisterException;

    void unregister(String providerUser, String providerPassword, OauthClient oauthClient) throws DriverUnregisterException;

    String getDriverName();
}
