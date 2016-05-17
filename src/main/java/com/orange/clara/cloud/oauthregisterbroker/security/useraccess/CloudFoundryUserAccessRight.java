package com.orange.clara.cloud.oauthregisterbroker.security.useraccess;

import com.orange.clara.cloud.oauthregisterbroker.exception.UserAccessRightException;
import com.orange.clara.cloud.oauthregisterbroker.model.OauthRegServiceInstance;
import com.orange.clara.cloud.oauthregisterbroker.model.OauthRegServiceInstanceBindings;
import com.orange.clara.cloud.oauthregisterbroker.security.AccessManager;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Copyright (C) 2016 Orange
 * <p>
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE' in this package distribution
 * or at 'https://opensource.org/licenses/Apache-2.0'.
 * <p>
 * Author: Arthur Halet
 * Date: 11/02/2016
 */
public class CloudFoundryUserAccessRight implements UserAccessRight {

    @Autowired
    protected AccessManager accessManager;
    @Autowired()
    @Qualifier("cloudFoundryClientAsUser")
    private CloudFoundryClient cloudFoundryClient;

    @Override
    public Boolean haveAccessToServiceInstance(String serviceInstanceId) throws UserAccessRightException {
        if (accessManager.isUserIsAdmin()) {
            return true;
        }
        return this.cloudFoundryClient.checkUserPermission(serviceInstanceId);
    }

    @Override
    public Boolean haveAccessToServiceInstance(OauthRegServiceInstanceBindings binding) throws UserAccessRightException {
        return this.haveAccessToServiceInstance(binding.getOauthRegServiceInstance());
    }

    @Override
    public Boolean haveAccessToServiceInstance(OauthRegServiceInstance instance) throws UserAccessRightException {
        return this.haveAccessToServiceInstance(instance.getServiceInstanceId());
    }

}
