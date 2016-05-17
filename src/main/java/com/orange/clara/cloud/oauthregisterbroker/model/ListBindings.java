package com.orange.clara.cloud.oauthregisterbroker.model;

import org.cloudfoundry.client.lib.domain.CloudApplication;

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
public class ListBindings {
    private CloudApplication cloudApplication;
    private OauthRegServiceInstanceBindings oauthRegServiceInstanceBindings;

    public ListBindings(CloudApplication cloudApplication, OauthRegServiceInstanceBindings oauthRegServiceInstanceBindings) {
        this.cloudApplication = cloudApplication;
        this.oauthRegServiceInstanceBindings = oauthRegServiceInstanceBindings;
    }

    public CloudApplication getCloudApplication() {
        return cloudApplication;
    }

    public void setCloudApplication(CloudApplication cloudApplication) {
        this.cloudApplication = cloudApplication;
    }

    public OauthRegServiceInstanceBindings getOauthRegServiceInstanceBindings() {
        return oauthRegServiceInstanceBindings;
    }

    public void setOauthRegServiceInstanceBindings(OauthRegServiceInstanceBindings oauthRegServiceInstanceBindings) {
        this.oauthRegServiceInstanceBindings = oauthRegServiceInstanceBindings;
    }
}
