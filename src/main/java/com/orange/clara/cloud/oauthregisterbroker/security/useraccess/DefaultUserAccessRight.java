package com.orange.clara.cloud.oauthregisterbroker.security.useraccess;

import com.orange.clara.cloud.oauthregisterbroker.exception.UserAccessRightException;
import com.orange.clara.cloud.oauthregisterbroker.model.OauthRegServiceInstance;
import com.orange.clara.cloud.oauthregisterbroker.model.OauthRegServiceInstanceBindings;

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
public class DefaultUserAccessRight implements UserAccessRight {
    @Override
    public Boolean haveAccessToServiceInstance(String serviceInstanceId) {
        return true;
    }

    @Override
    public Boolean haveAccessToServiceInstance(OauthRegServiceInstanceBindings binding) throws UserAccessRightException {
        return true;
    }

    @Override
    public Boolean haveAccessToServiceInstance(OauthRegServiceInstance instance) throws UserAccessRightException {
        return true;
    }
}
