package com.orange.clara.cloud.oauthregisterbroker.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.orange.clara.cloud.oauthregisterbroker.drivers.Driver;
import com.orange.clara.cloud.oauthregisterbroker.drivers.TestDriver;
import com.orange.clara.cloud.oauthregisterbroker.drivers.factory.DriverFactory;
import org.cloudfoundry.community.servicebroker.model.Catalog;
import org.cloudfoundry.community.servicebroker.model.DashboardClient;
import org.cloudfoundry.community.servicebroker.model.Plan;
import org.cloudfoundry.community.servicebroker.model.ServiceDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

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
public class CatalogConfig {

    @Value("${use.plan.test:false}")
    private boolean usePlanTest;

    @Value("${security.oauth2.client.clientSecret:fakeClientSecret}")
    private String clientSecret;

    @Value("${security.oauth2.client.clientId:fakeClientId}")
    private String clientId;

    @Value("#{${use.ssl:false} ? 'https://' : 'http://'}${vcap.application.uris[0]:localhost:8080}")
    private String appUri;

    @Value("${service.definition.id:oauth-register-broker}")
    private String serviceDefinitionId;

    @Autowired
    private DriverFactory driverFactory;

    @Bean
    public Catalog catalog() {
        return new Catalog(Collections.singletonList(
                new ServiceDefinition(
                        serviceDefinitionId,
                        serviceDefinitionId,
                        "Automatically register a Cloud Foundry app to an oauth2 provider",
                        true,
                        false,
                        this.getPlans(),
                        Arrays.asList("oauth", "sso"),
                        getServiceDefinitionMetadata(),
                        null,
                        this.getDashboardClient())));
    }

    private DashboardClient getDashboardClient() {
        //return new DashboardClient(this.clientId, this.clientSecret, this.appUri + "/login");
        return null;
    }

    @Bean
    public Map<String, Driver> mapPlanToDriver() {
        Map<String, Driver> planToDrivers = Maps.newHashMap();
        for (Driver driver : this.driverFactory.getDrivers()) {
            if (driver instanceof TestDriver && !usePlanTest) {
                continue;
            }
            planToDrivers.put(this.serviceDefinitionId + "-plan-" + this.getDriverName(driver), driver);
        }
        return planToDrivers;
    }

    private String getDriverName(Driver driver) {
        return driver.getDriverName();
    }

    @Bean
    public List<Plan> getPlans() {
        List<Plan> plans = Lists.newArrayList();
        for (String planId : this.mapPlanToDriver().keySet()) {
            Driver driver = this.mapPlanToDriver().get(planId);
            String planName = this.getDriverName(driver);
            plans.add(new Plan(planId,
                    planName,
                    "Register app on " + planName,
                    getPlanMetadata(),
                    true));
        }
        return plans;
    }
/* Used by Pivotal CF console */

    private Map<String, Object> getServiceDefinitionMetadata() {
        Map<String, Object> sdMetadata = new HashMap<>();
        sdMetadata.put("displayName", "oauth-register-broker");
        sdMetadata.put("imageUrl", appUri + "/images/logo.png");
        sdMetadata.put("longDescription", "oauth-register-broker");
        sdMetadata.put("providerDisplayName", "Orange");
        sdMetadata.put("documentationUrl", "");
        sdMetadata.put("supportUrl", "");
        return sdMetadata;
    }

    private Map<String, Object> getPlanMetadata() {
        Map<String, Object> planMetadata = new HashMap<>();
        planMetadata.put("costs", getCosts());
        planMetadata.put("bullets", getBullets());
        return planMetadata;
    }

    private List<Map<String, Object>> getCosts() {
        Map<String, Object> costsMap = new HashMap<>();

        Map<String, Object> amount = new HashMap<>();
        amount.put("usd", 0.0);

        costsMap.put("amount", amount);
        costsMap.put("unit", "MONTHLY");

        return Collections.singletonList(costsMap);
    }

    @Bean
    public String appUri() {
        return this.appUri;
    }

    private List<String> getBullets() {
        return Arrays.asList("Register and app to an oauth2 provider");
    }

}
