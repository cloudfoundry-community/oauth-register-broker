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
 * Date: 15/05/2016
 */
public class DriverUnregisterException extends DriverException {

    public DriverUnregisterException(Driver driver, String message) {
        super(driver, message);
    }

    public DriverUnregisterException(Driver driver, String message, Throwable cause) {
        super(driver, message, cause);
    }
}
