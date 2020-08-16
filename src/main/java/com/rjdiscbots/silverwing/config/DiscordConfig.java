package com.rjdiscbots.silverwing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class DiscordConfig {

    private String token;

    private String patch;

    private boolean update;

    public String getToken() {
        return token;
    }

    public String getPatch() {
        return patch;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPatch(String patch) {
        this.patch = patch;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }
}