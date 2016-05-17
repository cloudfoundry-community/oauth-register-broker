package com.orange.clara.cloud.oauthregisterbroker;

import com.orange.clara.cloud.oauthregisterbroker.config.GlobalProviderConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(GlobalProviderConfig.class)
public class OauthRegisterBrokerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OauthRegisterBrokerApplication.class, args);
    }
}
