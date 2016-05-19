package com.orange.clara.cloud.oauthregisterbroker.exception;

import com.orange.clara.cloud.oauthregisterbroker.drivers.Driver;
import com.orange.clara.cloud.oauthregisterbroker.service.OauthRegInstanceService;

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
public class TwoFactorsAuthenticationExpiredException extends DriverException {
    private static final String MESSAGE = String.format("Your username use two factors and the token you gave expired.", OauthRegInstanceService.AUTHENTICATION_CODE_PARAMETER);

    public TwoFactorsAuthenticationExpiredException(Driver driver) {
        super(driver, MESSAGE);
    }

    public TwoFactorsAuthenticationExpiredException(Driver driver, Throwable cause) {
        super(driver, MESSAGE, cause);
    }

    public TwoFactorsAuthenticationExpiredException() {
        super(MESSAGE);
    }

    public TwoFactorsAuthenticationExpiredException(Throwable cause) {
        super(MESSAGE, cause);
    }
}