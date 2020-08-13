package com.rjdiscbots.tftbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class DiscordConfig {

    private String token;

    private String patch;

    private boolean production;

    public String getToken() {
        return token;
    }

    public String getPatch() {
        return patch;
    }

    public boolean isProduction() {
        return production;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPatch(String patch) {
        this.patch = patch;
    }

    public void setProduction(boolean production) {
        this.production = production;
    }
}