package com.orange.clara.cloud.oauthregisterbroker.model;

import javax.persistence.*;

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
@Entity
public class OauthRegServiceInstanceBindings {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "oauth_reg_service_instance_id")
    private OauthRegServiceInstance oauthRegServiceInstance;

    private String appGuid;

    @OneToOne
    @JoinColumn(name = "oauth_client_id")
    private OauthClient oauthClient;

    public OauthRegServiceInstanceBindings() {
    }

    public OauthRegServiceInstanceBindings(String id, OauthRegServiceInstance oauthRegServiceInstance, String appGuid) {
        this.id = id;
        this.oauthRegServiceInstance = oauthRegServiceInstance;
        this.appGuid = appGuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getAppGuid() {
        return appGuid;
    }

    public void setAppGuid(String appGuid) {
        this.appGuid = appGuid;
    }

    public OauthRegServiceInstance getOauthRegServiceInstance() {
        return oauthRegServiceInstance;
    }

    public void setOauthRegServiceInstance(OauthRegServiceInstance oauthRegServiceInstance) {
        this.oauthRegServiceInstance = oauthRegServiceInstance;
        oauthRegServiceInstance.addOauthRegServiceInstanceBinding(this);
    }

    public OauthClient getOauthClient() {
        return oauthClient;
    }

    public void setOauthClient(OauthClient oauthClient) {
        this.oauthClient = oauthClient;
    }
}
