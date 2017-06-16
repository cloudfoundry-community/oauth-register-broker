package com.orange.clara.cloud.oauthregisterbroker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Copyright (C) 2016 Orange
 * <p>
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE' in this package distribution
 * or at 'https://opensource.org/licenses/Apache-2.0'.
 * <p>
 * Author: Jerome Djebari
 * Date: 24/06/2017
 */
@Configuration
public class PingFederateConfig {
	
	@Value("${ping.api.url:#{null}}")
	private String pingApiUrl;

	@Bean
    public String pingApiUrl() {
        return pingApiUrl;
    }
	 
}


