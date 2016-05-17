package com.orange.clara.cloud.oauthregisterbroker.config;

import com.orange.clara.cloud.oauthregisterbroker.cloudfoundry.CloudFoundryClientFactory;
import com.orange.clara.cloud.oauthregisterbroker.security.useraccess.CloudFoundryUserAccessRight;
import com.orange.clara.cloud.oauthregisterbroker.security.useraccess.DefaultUserAccessRight;
import com.orange.clara.cloud.oauthregisterbroker.security.useraccess.UserAccessRight;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudOrganization;
import org.cloudfoundry.client.lib.domain.CloudSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.MalformedURLException;

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
@Configuration
public class AppConfig {

    private Logger logger = LoggerFactory.getLogger(AppConfig.class);
    @Value("${CF.TARGET:#{null}}")
    private String uaaUrl;

    @Value("${security.oauth2.client.clientId:#{null}}")
    private String uaaClientId;

    @Value("${security.oauth2.client.clientSecret:#{null}}")
    private String uaaClientSecret;

    @Autowired
    private CloudFoundryClientFactory cloudFoundryClientFactory;

    @Value("${cf.admin.user:#{null}}")
    private String cfAdminUser;
    @Value("${cf.admin.password:#{null}}")
    private String cfAdminPassword;
    @Value("${cloud.controller.url:#{null}}")
    private String cloudControllerUrl;
    @Value("${no.cloudfoundry.access:false}")
    private boolean noCloudFoundryAccess;


    @Bean(name = "cloudFoundryClientAsAdmin")
    public CloudFoundryClient getCloudFoundryClientAsAdmin() throws MalformedURLException {
        if (this.cfAdminUser == null
                || this.cfAdminUser.isEmpty()
                || this.cfAdminPassword == null
                || this.cfAdminPassword.isEmpty()
                || this.cloudControllerUrl == null
                || this.cloudControllerUrl.isEmpty()
                || this.noCloudFoundryAccess) {
            return null;
        }
        CloudOrganization cloudOrganization = this.getOrg();
        CloudSpace cloudSpace = this.getSpace();
        if (cloudOrganization == null || cloudSpace == null) {
            return null;
        }
        logger.debug(String.format("Creating new CloudFoundry client using admin access with org '%s' and space '%s'", cloudOrganization.getName(), cloudSpace.getName()));
        return cloudFoundryClientFactory.createCloudFoundryClient(this.cfAdminUser, this.cfAdminPassword, this.cloudControllerUrl, cloudOrganization.getName(), cloudSpace.getName());
    }

    @Bean
    public CloudFoundryClient getCloudFoundryClientTemp() throws MalformedURLException {
        if (this.cfAdminUser == null
                || this.cfAdminUser.isEmpty()
                || this.cfAdminPassword == null
                || this.cfAdminPassword.isEmpty()
                || this.cloudControllerUrl == null
                || this.cloudControllerUrl.isEmpty()
                || this.noCloudFoundryAccess) {
            return null;
        }
        logger.debug("Creating new CloudFoundry client using admin access");
        return cloudFoundryClientFactory.createCloudFoundryClient(this.cfAdminUser, this.cfAdminPassword, this.cloudControllerUrl);
    }

    @Bean
    public CloudOrganization getOrg() throws MalformedURLException {
        CloudFoundryClient cloudFoundryClient = this.getCloudFoundryClientTemp();
        if (cloudFoundryClient == null) {
            return null;
        }
        return cloudFoundryClient.getOrganizations().get(0);
    }

    @Bean
    public CloudSpace getSpace() throws MalformedURLException {
        CloudFoundryClient cloudFoundryClient = this.getCloudFoundryClientTemp();
        if (cloudFoundryClient == null) {
            return null;
        }
        for (CloudSpace cloudSpace : getCloudFoundryClientTemp().getSpaces()) {
            if (cloudSpace.getOrganization().getMeta().getGuid().equals(this.getOrg().getMeta().getGuid())) {
                return cloudSpace;
            }
        }
        return null;
    }

    @Bean
    public String getUaaUrl() {
        return uaaUrl;
    }

    @Bean
    public String getUaaClientId() {
        return uaaClientId;
    }

    @Bean
    public String getUaaClientSecret() {
        return uaaClientSecret;
    }

    @Bean
    public String cfAdminUser() {
        return cfAdminUser;
    }

    @Bean
    public String cfAdminPassword() {
        return cfAdminPassword;
    }

    @Bean
    public String cloudControllerUrl() {
        return cloudControllerUrl;
    }

    @Bean(name = "userAccessRight")
    @Profile("uaa")
    public UserAccessRight getCloudFoundryUserAccessRight() {
        return new CloudFoundryUserAccessRight();
    }

    @Bean(name = "userAccessRight")
    @Profile("!uaa")
    public UserAccessRight getDefaultUserAccessRight() {
        return new DefaultUserAccessRight();
    }
}

