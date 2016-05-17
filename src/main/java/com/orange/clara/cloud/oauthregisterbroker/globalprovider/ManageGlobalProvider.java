package com.orange.clara.cloud.oauthregisterbroker.globalprovider;

import com.orange.clara.cloud.oauthregisterbroker.config.GlobalProviderConfig;
import com.orange.clara.cloud.oauthregisterbroker.drivers.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class ManageGlobalProvider {

    @Autowired
    private GlobalProviderConfig globalProviderConfig;

    public String getUsername(Driver driver) {
        return this.globalProviderConfig.getUsername().get(driver.getDriverName());
    }

    public String getPassword(Driver driver) {
        return this.globalProviderConfig.getPassword().get(driver.getDriverName());
    }

    public boolean hasGlobalProviderConfig(Driver driver) {
        return this.globalProviderConfig.getUsername().get(driver.getDriverName()) != null
                && this.globalProviderConfig.getPassword().get(driver.getDriverName()) != null;

    }
}
