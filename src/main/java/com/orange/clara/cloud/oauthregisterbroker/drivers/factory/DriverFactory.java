package com.orange.clara.cloud.oauthregisterbroker.drivers.factory;

import com.orange.clara.cloud.oauthregisterbroker.drivers.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service
public class DriverFactory {

    @Autowired
    protected List<Driver> drivers;

    public List<Driver> getDrivers() {
        return drivers;
    }

    public Driver getDriver(String name) {
        for (Driver driver : this.drivers) {
            if (driver.getDriverName().equals(name)) {
                return driver;
            }
        }
        return null;
    }
}
