package com.orange.clara.cloud.oauthregisterbroker.drivers;

import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;

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
public abstract class AbstractDriver implements Driver {

    public String createClientId(CloudApplication app) {
        return app.getSpace().getOrganization().getName() + "-" + app.getSpace().getName() + "-" + app.getName();
    }

    public String createClientPassword() {
        return this.randomString(15);
    }

    public String randomString(int number) {
        RandomValueStringGenerator random = new RandomValueStringGenerator(number);
        return random.generate();
    }
}
