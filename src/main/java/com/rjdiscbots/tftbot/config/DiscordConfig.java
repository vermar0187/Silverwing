package com.rjdiscbots.tftbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class DiscordConfig {

    private String token;

    private boolean production;

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public boolean isProduction() {
        return production;
    }

    public void setProduction(boolean production) {
        this.production = production;
    }
}