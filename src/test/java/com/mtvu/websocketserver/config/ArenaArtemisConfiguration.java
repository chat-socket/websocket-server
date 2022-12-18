package com.mtvu.websocketserver.config;

import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mvu
 * @project chat-socket
 **/
@Configuration
public class ArenaArtemisConfiguration {

    private final ArtemisProperties artemisProperties;

    public ArenaArtemisConfiguration(ArtemisProperties artemisProperties) {
        this.artemisProperties = artemisProperties;
    }

    @Bean
    public ArtemisConfigurationCustomizer customizer() {
        return configuration -> {
            try {
                configuration.addAcceptorConfiguration(
                    "netty", artemisProperties.getBrokerUrl()
                );
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        };
    }
}
