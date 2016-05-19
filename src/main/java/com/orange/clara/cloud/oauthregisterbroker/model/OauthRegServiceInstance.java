package com.orange.clara.cloud.oauthregisterbroker.model;

import com.orange.clara.cloud.oauthregisterbroker.security.CryptoConverter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
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
@Entity
public class OauthRegServiceInstance {
    @Id
    private String serviceInstanceId;
    private String planId;
    private String organizationGuid;
    private String spaceGuid;
    private String dashboardUrl;
    @Convert(converter = CryptoConverter.class)
    private String providerUsername;
    @Convert(converter = CryptoConverter.class)
    private String providerPassword;
    @Convert(converter = CryptoConverter.class)
    private String authenticationCode;
    @OneToMany(mappedBy = "oauthRegServiceInstance")
    private List<OauthRegServiceInstanceBindings> oauthRegServiceInstanceBindings;

    public OauthRegServiceInstance() {
        this.oauthRegServiceInstanceBindings = new ArrayList<>();
    }

    public OauthRegServiceInstance(String serviceInstanceId, String planId, String organizationGuid, String spaceGuid, String dashboardUrl) {
        this();
        this.serviceInstanceId = serviceInstanceId;
        this.planId = planId;
        this.organizationGuid = organizationGuid;
        this.spaceGuid = spaceGuid;
        this.dashboardUrl = dashboardUrl;
    }

    public OauthRegServiceInstance(String serviceInstanceId, String planId, String organizationGuid, String spaceGuid, String dashboardUrl, String authenticationCode) {
        this(serviceInstanceId, planId, organizationGuid, spaceGuid, dashboardUrl);
        this.authenticationCode = authenticationCode;
    }

    public void addOauthRegServiceInstanceBinding(OauthRegServiceInstanceBindings oauthRegServiceInstanceBindings) {
        if (this.oauthRegServiceInstanceBindings.contains(oauthRegServiceInstanceBindings)) {
            return;
        }
        this.oauthRegServiceInstanceBindings.add(oauthRegServiceInstanceBindings);
    }

    public void removeOauthRegServiceInstanceBinding(OauthRegServiceInstanceBindings oauthRegServiceInstanceBindings) {
        if (!this.oauthRegServiceInstanceBindings.contains(oauthRegServiceInstanceBindings)) {
            return;
        }
        this.oauthRegServiceInstanceBindings.remove(oauthRegServiceInstanceBindings);
    }

    public String getServiceInstanceId() {
        return this.serviceInstanceId;
    }

    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public String getPlanId() {
        return this.planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getOrganizationGuid() {
        return this.organizationGuid;
    }

    public void setOrganizationGuid(String organizationGuid) {
        this.organizationGuid = organizationGuid;
    }

    public String getSpaceGuid() {
        return this.spaceGuid;
    }

    public void setSpaceGuid(String spaceGuid) {
        this.spaceGuid = spaceGuid;
    }

    public String getDashboardUrl() {
        return this.dashboardUrl;
    }

    public void setDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
    }


    public List<OauthRegServiceInstanceBindings> getOauthRegServiceInstanceBindings() {
        return oauthRegServiceInstanceBindings;
    }

    public void setOauthRegServiceInstanceBindings(List<OauthRegServiceInstanceBindings> oauthRegServiceInstanceBindings) {
        this.oauthRegServiceInstanceBindings = oauthRegServiceInstanceBindings;
    }

    public String getProviderUsername() {
        return providerUsername;
    }

    public void setProviderUsername(String providerUsername) {
        this.providerUsername = providerUsername;
    }

    public String getProviderPassword() {
        return providerPassword;
    }

    public void setProviderPassword(String providerPassword) {
        this.providerPassword = providerPassword;
    }

    public String getAuthenticationCode() {
        return authenticationCode;
    }

    public void setAuthenticationCode(String authenticationCode) {
        this.authenticationCode = authenticationCode;
    }
}
