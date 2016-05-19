package com.orange.clara.cloud.oauthregisterbroker.model;

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
public class ProviderInformation {
    private String username;
    private String password;
    private String authenticationCode;

    public ProviderInformation(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public ProviderInformation(String username, String password, String authenticationCode) {
        this(username, password);
        this.authenticationCode = authenticationCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthenticationCode() {
        return authenticationCode;
    }

    public void setAuthenticationCode(String authenticationCode) {
        this.authenticationCode = authenticationCode;
    }
}
