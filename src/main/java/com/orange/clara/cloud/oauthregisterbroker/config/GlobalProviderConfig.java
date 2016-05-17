package com.orange.clara.cloud.oauthregisterbroker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

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
@ConfigurationProperties("global.provider")
public class GlobalProviderConfig {
    private final Map<String, String> username = new HashMap<>();
    private final Map<String, String> password = new HashMap<>();

    public Map<String, String> getUsername() {
        return username;
    }

    public Map<String, String> getPassword() {
        return password;
    }
}
