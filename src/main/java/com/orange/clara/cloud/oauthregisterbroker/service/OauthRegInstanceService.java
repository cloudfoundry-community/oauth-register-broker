package com.orange.clara.cloud.oauthregisterbroker.service;

import com.google.common.collect.Maps;
import com.orange.clara.cloud.oauthregisterbroker.drivers.Driver;
import com.orange.clara.cloud.oauthregisterbroker.model.OauthRegServiceInstance;
import com.orange.clara.cloud.oauthregisterbroker.repo.OauthRegServiceInstanceRepo;
import org.cloudfoundry.community.servicebroker.exception.*;
import org.cloudfoundry.community.servicebroker.model.CreateServiceInstanceRequest;
import org.cloudfoundry.community.servicebroker.model.DeleteServiceInstanceRequest;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.model.UpdateServiceInstanceRequest;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

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
public class OauthRegInstanceService extends AbstractOauthRegInstance implements ServiceInstanceService {

    public final static String PROVIDER_USERNAME_PARAMETER = "provider_username";
    public final static String PROVIDER_PASSWORD_PARAMETER = "provider_password";
    public final static String AUTHENTICATION_CODE_PARAMETER = "authentication_code";

    @Autowired
    @Qualifier("appUri")
    protected String appUri;

    private Logger logger = LoggerFactory.getLogger(OauthRegInstanceService.class);

    @Autowired
    private OauthRegServiceInstanceRepo oauthRegServiceInstanceRepo;


    @Override
    public ServiceInstance createServiceInstance(CreateServiceInstanceRequest request) throws ServiceInstanceExistsException, ServiceBrokerException, ServiceBrokerAsyncRequiredException {
        ServiceInstance serviceInstance = new ServiceInstance(request);
        serviceInstance.withDashboardUrl(appUri);
        serviceInstance.withAsync(false);
        if (this.oauthRegServiceInstanceRepo.exists(request.getServiceInstanceId())) {
            throw new ServiceInstanceExistsException(serviceInstance);
        }
        OauthRegServiceInstance instance = this.generateOauthRegServiceInstance(serviceInstance);
        Driver driver = this.getDriverFromInstance(instance);
        if (!this.manageGlobalProvider.hasGlobalProviderConfig(driver)) {
            this.loadLoginProviderToInstance(request.getParameters(), instance);
        }
        String authenticationCode = this.getParameter(request.getParameters(), AUTHENTICATION_CODE_PARAMETER, null);
        instance.setAuthenticationCode(authenticationCode);
        this.oauthRegServiceInstanceRepo.save(instance);
        return serviceInstance;
    }

    @Override
    public ServiceInstance getServiceInstance(String s) {
        OauthRegServiceInstance instance = this.oauthRegServiceInstanceRepo.findOne(s);
        CreateServiceInstanceRequest request = new CreateServiceInstanceRequest(
                instance.getServiceInstanceId(),
                instance.getPlanId(),
                instance.getOrganizationGuid(),
                instance.getSpaceGuid(),
                false,
                Maps.newHashMap()
        );
        return new ServiceInstance(request);
    }

    @Override
    public ServiceInstance deleteServiceInstance(DeleteServiceInstanceRequest request) throws ServiceBrokerException, ServiceBrokerAsyncRequiredException {
        ServiceInstance serviceInstance = new ServiceInstance(request);
        serviceInstance.withAsync(false);
        serviceInstance.withDashboardUrl(appUri);
        if (!this.oauthRegServiceInstanceRepo.exists(request.getServiceInstanceId())) {
            logger.warn("The service instance '" + request.getServiceInstanceId() + "' doesn't exist. Defaulting to say to cloud controller that instance is deleted.");
        }
        this.oauthRegServiceInstanceRepo.delete(request.getServiceInstanceId());
        return serviceInstance;
    }

    @Override
    public ServiceInstance updateServiceInstance(UpdateServiceInstanceRequest request) throws ServiceInstanceUpdateNotSupportedException, ServiceBrokerException, ServiceInstanceDoesNotExistException, ServiceBrokerAsyncRequiredException {
        ServiceInstance serviceInstance = new ServiceInstance(request);
        serviceInstance.withAsync(false);
        serviceInstance.withDashboardUrl(appUri);
        if (!this.oauthRegServiceInstanceRepo.exists(request.getServiceInstanceId())) {
            throw new ServiceInstanceDoesNotExistException(request.getServiceInstanceId());
        }

        OauthRegServiceInstance instance = this.oauthRegServiceInstanceRepo.findOne(request.getServiceInstanceId());
        Driver driver = this.getDriverFromInstance(instance);
        if (this.manageGlobalProvider.hasGlobalProviderConfig(driver)) {
            return serviceInstance;
        }
        String user = this.getParameter(request.getParameters(), PROVIDER_USERNAME_PARAMETER, null);
        if (user != null) {
            instance.setProviderUsername(user);
        }
        String password = this.getParameter(request.getParameters(), PROVIDER_PASSWORD_PARAMETER, null);
        if (password != null) {
            instance.setProviderPassword(password);
        }
        String authenticationCode = this.getParameter(request.getParameters(), AUTHENTICATION_CODE_PARAMETER, null);
        instance.setAuthenticationCode(authenticationCode);
        this.oauthRegServiceInstanceRepo.save(instance);
        return serviceInstance;
    }

    private void loadLoginProviderToInstance(Map<String, Object> params, OauthRegServiceInstance instance) throws ServiceBrokerException {
        String user = null;
        String password = null;
        try {
            user = this.getParameter(params, PROVIDER_USERNAME_PARAMETER);
            password = this.getParameter(params, PROVIDER_PASSWORD_PARAMETER);
        } catch (ServiceBrokerException e) {
            throw new ServiceBrokerException("Admin didn't set a global provider configuration for this provider " + e.getMessage(), e);
        }

        instance.setProviderUsername(user);
        instance.setProviderPassword(password);
    }

    private OauthRegServiceInstance generateOauthRegServiceInstance(ServiceInstance serviceInstance) {
        return new OauthRegServiceInstance(
                serviceInstance.getServiceInstanceId(),
                serviceInstance.getPlanId(),
                serviceInstance.getOrganizationGuid(),
                serviceInstance.getSpaceGuid(),
                serviceInstance.getDashboardUrl()
        );
    }
}
