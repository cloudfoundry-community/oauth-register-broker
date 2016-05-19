package com.orange.clara.cloud.oauthregisterbroker.exception;

import com.orange.clara.cloud.oauthregisterbroker.drivers.Driver;

/**
 * Copyright (C) 2016 Orange
 * <p>
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE' in this package distribution
 * or at 'https://opensource.org/licenses/Apache-2.0'.
 * <p>
 * Author: Arthur Halet
 * Date: 19/05/2016
 */
public class DriverException extends Exception {

    public DriverException(Driver driver, String message) {
        super("Error in the driver '" + driver.getDriverName() + "': " + message);
    }

    public DriverException(Driver driver, String message, Throwable cause) {
        super("Error in the driver '" + driver.getDriverName() + "': " + message, cause);
    }

    public DriverException(String message) {
        super(message);
    }

    public DriverException(String message, Throwable cause) {
        super(message, cause);
    }
}
