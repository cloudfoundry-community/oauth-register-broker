package com.orange.clara.cloud.oauthregisterbroker.service;

import com.google.common.collect.Maps;
import com.orange.clara.cloud.oauthregisterbroker.drivers.Driver;
import com.orange.clara.cloud.oauthregisterbroker.model.OauthClient;
import com.orange.clara.cloud.oauthregisterbroker.model.OauthRegServiceInstance;
import com.orange.clara.cloud.oauthregisterbroker.model.OauthRegServiceInstanceBindings;
import com.orange.clara.cloud.oauthregisterbroker.model.ProviderInformation;
import com.orange.clara.cloud.oauthregisterbroker.repo.OauthClientRepo;
import com.orange.clara.cloud.oauthregisterbroker.repo.OauthRegServiceInstanceBindingsRepo;
import com.orange.clara.cloud.oauthregisterbroker.repo.OauthRegServiceInstanceRepo;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.CloudEntity;
import org.cloudfoundry.community.servicebroker.exception.ServiceBrokerAsyncRequiredException;
import org.cloudfoundry.community.servicebroker.exception.ServiceBrokerException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.cloudfoundry.community.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.cloudfoundry.community.servicebroker.model.ServiceInstanceBinding;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceBindingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
@Service
public class OauthRegInstanceBindingService extends AbstractOauthRegInstance implements ServiceInstanceBindingService {


    public final static String GRANT_TYPES_PARAMETER = "grant_types";
    public final static String SCOPES_PARAMETER = "scopes";
    public final static String REDIRECT_PARAMETER = "callback_url";
    public final static String APP_URIS_PARAMETER = "app_uris";
    @Autowired
    @Qualifier("cloudFoundryClientAsAdmin")
    protected CloudFoundryClient cloudFoundryClient;
    private Logger logger = LoggerFactory.getLogger(OauthRegInstanceService.class);
    @Autowired
    private OauthRegServiceInstanceBindingsRepo oauthRegServiceInstanceBindingsRepo;
    @Autowired
    private OauthRegServiceInstanceRepo oauthRegServiceInstanceRepo;

    @Autowired
    private OauthClientRepo oauthClientRepo;


    @Override
    public ServiceInstanceBinding createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) throws ServiceInstanceBindingExistsException, ServiceBrokerException {
        ServiceInstanceBinding serviceInstanceBinding = new ServiceInstanceBinding(
                request.getBindingId(),
                request.getServiceDefinitionId(),
                Maps.newHashMap(),
                null,
                request.getAppGuid()
        );
        if (request.getAppGuid() == null || request.getAppGuid().isEmpty()) {
            throw new ServiceBrokerException("You can only bind this service to an app (service keys are not allowed)");
        }
        if (this.oauthRegServiceInstanceBindingsRepo.exists(request.getBindingId())) {
            throw new ServiceInstanceBindingExistsException(serviceInstanceBinding);
        }


        List<String> grantTypes = this.stringWithCommaToList(this.getParameter(request.getParameters(), GRANT_TYPES_PARAMETER, "authorization_code"));
        List<String> scopes = this.stringWithCommaToList(this.getParameter(request.getParameters(), SCOPES_PARAMETER, "openid"));
        String redirectPath = this.getParameter(request.getParameters(), REDIRECT_PARAMETER, "");

        OauthRegServiceInstance instance = this.oauthRegServiceInstanceRepo.findOne(request.getServiceInstanceId());
        OauthRegServiceInstanceBindings binding = new OauthRegServiceInstanceBindings(request.getBindingId(), instance, request.getAppGuid());

        CloudApplication application = null;

        if (this.cloudFoundryClient != null) {
            try {
                application = this.cloudFoundryClient.getApplication(UUID.fromString(request.getAppGuid()));
            } catch (Exception e) {
                throw new ServiceBrokerException("Error during binding (getApplication): " + e.getMessage(), e);
            }

        } else {
            application = this.generateCloudApplication(request.getAppGuid(), request.getParameters());
        }
        logger.debug("App found {} with uuid {}", application, request.getAppGuid());
        Driver driver = this.getDriverFromInstance(instance);
        OauthClient oauthClient = null;
        try {
            ProviderInformation providerInformation = new ProviderInformation(this.getProviderUsername(instance, driver), this.getProviderPassword(instance, driver), instance.getAuthenticationCode());
            oauthClient = driver.register(providerInformation, application, grantTypes, scopes, redirectPath);
        } catch (Exception e) {
            if (e.getCause() != null && e.getCause() instanceof HttpClientErrorException) {
                throw new ServiceBrokerException("Error during binding: " + e.getMessage() + "\nbody response: " + ((HttpClientErrorException) e.getCause()).getResponseBodyAsString(), e);
            }
            throw new ServiceBrokerException("Error during binding: " + e.getMessage(), e);
        }
        this.oauthRegServiceInstanceBindingsRepo.save(binding);
        oauthClient.setOauthRegServiceInstanceBindings(binding);
        this.oauthClientRepo.save(oauthClient);
        this.oauthRegServiceInstanceBindingsRepo.save(binding);


        return new ServiceInstanceBinding(
                request.getBindingId(),
                request.getServiceDefinitionId(),
                this.getCredentials(oauthClient),
                null,
                request.getAppGuid()
        );
    }

    @Override
    public ServiceInstanceBinding deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) throws ServiceBrokerException, ServiceBrokerAsyncRequiredException {
        ServiceInstanceBinding serviceInstanceBinding = new ServiceInstanceBinding(
                request.getBindingId(),
                request.getServiceId(),
                Maps.newHashMap(),
                null,
                ""
        );
        if (!this.oauthRegServiceInstanceBindingsRepo.exists(request.getBindingId())) {
            logger.warn("The service instance binding '" + request.getBindingId() + "' doesn't exist. Defaulting to say to cloud controller that binding is deleted.");
            return serviceInstanceBinding;
        }
        OauthRegServiceInstanceBindings binding = this.oauthRegServiceInstanceBindingsRepo.findOne(request.getBindingId());
        serviceInstanceBinding = new ServiceInstanceBinding(
                binding.getId(),
                binding.getOauthRegServiceInstance().getServiceInstanceId(),
                this.getCredentials(binding.getOauthClient()),
                "",
                binding.getAppGuid()
        );
        OauthRegServiceInstance instance = binding.getOauthRegServiceInstance();
        Driver driver = this.getDriverFromInstance(instance);
        try {
            ProviderInformation providerInformation = new ProviderInformation(this.getProviderUsername(instance, driver), this.getProviderPassword(instance, driver), instance.getAuthenticationCode());
            driver.unregister(providerInformation, binding.getOauthClient());
        } catch (Exception e) {
            throw new ServiceBrokerException("Error during unbinding: " + e.getMessage(), e);
        }

        this.oauthClientRepo.delete(binding.getOauthClient());
        this.oauthRegServiceInstanceBindingsRepo.delete(binding);
        return serviceInstanceBinding;
    }

    protected CloudApplication generateCloudApplication(String appGuid, Map<String, Object> params) throws ServiceBrokerException {
        List<String> appUris = null;
        try {
            appUris = this.stringWithCommaToList(this.getParameter(params, APP_URIS_PARAMETER));
        } catch (ServiceBrokerException e) {
            throw new ServiceBrokerException("Admin didn't set a Cloud Foundry user in broker " + e.getMessage(), e);
        }
        CloudApplication app = new CloudApplication(new CloudEntity.Meta(UUID.randomUUID(), new Date(), new Date()), appGuid);
        app.setUris(appUris);
        return app;
    }

    protected Map<String, Object> getCredentials(OauthClient oauthClient) {
        Map<String, Object> credentials = Maps.newHashMap();
        credentials.put("clientId", oauthClient.getClientId());
        credentials.put("clientSecret", oauthClient.getSecret());
        credentials.put("tokenUri", oauthClient.getAccessTokenUri());
        credentials.put("authorizationUri", oauthClient.getUserAuthorizationUri());
        credentials.put("userInfoUri", oauthClient.getUserInfoUri());
        credentials.put("logoutUri", oauthClient.getLogoutUri());
        credentials.put("scopes", this.stringWithCommaToList(oauthClient.getScopes()));
        credentials.put("grantTypes", this.stringWithCommaToList(oauthClient.getGrantTypes()));
        return credentials;
    }
}
