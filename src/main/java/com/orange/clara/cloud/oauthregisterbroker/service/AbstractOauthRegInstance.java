package com.orange.clara.cloud.oauthregisterbroker.service;

import com.google.common.collect.Lists;
import com.orange.clara.cloud.oauthregisterbroker.drivers.Driver;
import com.orange.clara.cloud.oauthregisterbroker.globalprovider.ManageGlobalProvider;
import com.orange.clara.cloud.oauthregisterbroker.model.OauthRegServiceInstance;
import org.cloudfoundry.community.servicebroker.exception.ServiceBrokerException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;
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
public abstract class AbstractOauthRegInstance {

    @Autowired
    protected ManageGlobalProvider manageGlobalProvider;

    @Resource(name = "mapPlanToDriver")
    protected Map<String, Driver> driverMap;


    protected String getParameter(Map<String, Object> parameters, String parameter, String defaultValue) throws ServiceBrokerException {
        if (parameters == null) {
            return defaultValue;
        }
        Object paramObject = parameters.get(parameter);
        if (paramObject == null) {
            return defaultValue;
        }
        return paramObject.toString();
    }

    protected List<String> stringWithCommaToList(String param) {
        String[] params = param.split(",");
        for (int i = 0; i < params.length; i++) {
            params[i] = params[i].trim();
        }
        return Lists.newArrayList(params);
    }

    protected String getParameter(Map<String, Object> parameters, String parameter) throws ServiceBrokerException {
        String param = this.getParameter(parameters, parameter, null);
        if (param == null) {
            throw new ServiceBrokerException("You need to set '" + parameter + "' parameter.");
        }
        return param;
    }

    protected Driver getDriverFromInstance(OauthRegServiceInstance instance) throws ServiceBrokerException {
        Driver driver = this.driverMap.get(instance.getPlanId());
        if (driver == null) {
            throw new ServiceBrokerException("Cannot found driver associated to the chosen plan.");
        }
        return driver;
    }

    protected String getProviderUsername(OauthRegServiceInstance instance, Driver driver) {
        if (this.manageGlobalProvider.hasGlobalProviderConfig(driver)) {
            return this.manageGlobalProvider.getUsername(driver);
        }
        return instance.getProviderUsername();
    }

    protected String getProviderPassword(OauthRegServiceInstance instance, Driver driver) {
        if (this.manageGlobalProvider.hasGlobalProviderConfig(driver)) {
            return this.manageGlobalProvider.getPassword(driver);
        }
        return instance.getProviderPassword();
    }
}
