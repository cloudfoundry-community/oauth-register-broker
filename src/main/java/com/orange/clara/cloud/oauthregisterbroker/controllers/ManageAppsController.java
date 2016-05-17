package com.orange.clara.cloud.oauthregisterbroker.controllers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.orange.clara.cloud.oauthregisterbroker.drivers.Driver;
import com.orange.clara.cloud.oauthregisterbroker.exception.UserAccessRightException;
import com.orange.clara.cloud.oauthregisterbroker.model.OauthRegServiceInstanceBindings;
import com.orange.clara.cloud.oauthregisterbroker.repo.OauthRegServiceInstanceBindingsRepo;
import com.orange.clara.cloud.oauthregisterbroker.security.useraccess.UserAccessRight;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright (C) 2015 Orange
 * <p>
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE' in this package distribution
 * or at 'https://opensource.org/licenses/Apache-2.0'.
 * <p>
 * Author: Arthur Halet
 * Date: 10/12/2015
 */
@Controller
public class ManageAppsController {

    @Resource(name = "mapPlanToDriver")
    protected Map<String, Driver> driverMap;
    @Autowired
    private OauthRegServiceInstanceBindingsRepo bindingsRepo;
    @Autowired
    @Qualifier("userAccessRight")
    private UserAccessRight userAccessRight;
    @Autowired
    @Qualifier("cloudFoundryClientAsAdmin")
    private CloudFoundryClient cloudFoundryClient;

    @RequestMapping("/")
    public String list(Model model) throws IOException, UserAccessRightException {
        model.addAttribute("bindingsMap", this.generateListBindings());
        model.addAttribute("driverMap", driverMap);
        return "listapps";
    }

    public Map<String, List<OauthRegServiceInstanceBindings>> generateListBindings() throws UserAccessRightException {
        Map<String, List<OauthRegServiceInstanceBindings>> bindingsMap = Maps.newHashMap();
        List<OauthRegServiceInstanceBindings> bindingsList = Lists.newArrayList();
        for (OauthRegServiceInstanceBindings binding : bindingsRepo.findAll()) {
            if (!this.userAccessRight.haveAccessToServiceInstance(binding)) {
                continue;
            }
            String applicationName = binding.getAppGuid();
            if (this.cloudFoundryClient != null) {
                CloudApplication application = this.cloudFoundryClient.getApplication(UUID.fromString(binding.getAppGuid()));
                applicationName = application.getName();
            }
            if (bindingsMap.get(applicationName) != null) {
                bindingsMap.get(applicationName).add(binding);
                continue;
            }
            bindingsMap.put(applicationName, Lists.newArrayList());
            bindingsMap.get(applicationName).add(binding);
        }
        return bindingsMap;
    }
}
