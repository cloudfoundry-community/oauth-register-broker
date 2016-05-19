package com.orange.clara.cloud.oauthregisterbroker.model;

import com.orange.clara.cloud.oauthregisterbroker.security.CryptoConverter;

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
public class OauthClient {
    @Id
    private String id;
    @Convert(converter = CryptoConverter.class)
    private String secret;
    private String accessTokenUri;
    private String userAuthorizationUri;
    private String userInfoUri;
    private String scopes;
    private String grantTypes;
    private String appName;
    private String clientId;

    @OneToOne
    @JoinColumn(name = "oauth_reg_service_instance_bindings_id")
    private OauthRegServiceInstanceBindings oauthRegServiceInstanceBindings;

    public OauthClient() {

    }

    public OauthClient(String id, String appName, String clientId, String secret, String accessTokenUri, String userAuthorizationUri, String userInfoUri, String scopes, String grantTypes) {
        this.secret = secret;
        this.id = id;
        this.accessTokenUri = accessTokenUri;
        this.userAuthorizationUri = userAuthorizationUri;
        this.userInfoUri = userInfoUri;
        this.scopes = scopes;
        this.grantTypes = grantTypes;
        this.appName = appName;
        this.clientId = clientId;
    }

    public OauthRegServiceInstanceBindings getOauthRegServiceInstanceBindings() {
        return oauthRegServiceInstanceBindings;
    }

    public void setOauthRegServiceInstanceBindings(OauthRegServiceInstanceBindings oauthRegServiceInstanceBindings) {
        this.oauthRegServiceInstanceBindings = oauthRegServiceInstanceBindings;
        this.oauthRegServiceInstanceBindings.setOauthClient(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getAccessTokenUri() {
        return accessTokenUri;
    }

    public void setAccessTokenUri(String accessTokenUri) {
        this.accessTokenUri = accessTokenUri;
    }

    public String getUserAuthorizationUri() {
        return userAuthorizationUri;
    }

    public void setUserAuthorizationUri(String userAuthorizationUri) {
        this.userAuthorizationUri = userAuthorizationUri;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }

    public void setUserInfoUri(String userInfoUri) {
        this.userInfoUri = userInfoUri;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public String getGrantTypes() {
        return grantTypes;
    }

    public void setGrantTypes(String grantTypes) {
        this.grantTypes = grantTypes;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "OauthClient{" +
                "clientId='" + clientId + '\'' +
                ", appName='" + appName + '\'' +
                ", grantTypes='" + grantTypes + '\'' +
                ", scopes='" + scopes + '\'' +
                ", userInfoUri='" + userInfoUri + '\'' +
                ", userAuthorizationUri='" + userAuthorizationUri + '\'' +
                ", accessTokenUri='" + accessTokenUri + '\'' +
                ", secret='" + secret + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    @PreRemove
    private void preRemove() {
        if (this.oauthRegServiceInstanceBindings == null) {
            return;
        }
        this.oauthRegServiceInstanceBindings.setOauthClient(null);
    }
}
