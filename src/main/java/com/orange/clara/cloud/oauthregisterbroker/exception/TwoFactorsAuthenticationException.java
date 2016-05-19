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
public class TwoFactorsAuthenticationException extends DriverException {
    private static final String MESSAGE = String.format("Your username use two factors you need to update the service with parameter '%s' with an access code before bind and unbind services instance.", OauthRegInstanceService.AUTHENTICATION_CODE_PARAMETER);

    public TwoFactorsAuthenticationException(Driver driver) {
        super(driver, MESSAGE);
    }

    public TwoFactorsAuthenticationException(Driver driver, Throwable cause) {
        super(driver, MESSAGE, cause);
    }

    public TwoFactorsAuthenticationException() {
        super(MESSAGE);
    }

    public TwoFactorsAuthenticationException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
