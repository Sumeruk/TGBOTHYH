package org.example;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.stereotype.Component;

@Component
public class ReadConfig {
    private static final PropertiesConfiguration configuration;

    static {
        try{
            configuration = new PropertiesConfiguration("config.properties");
        }  catch (ConfigurationException e) {
            throw new RuntimeException("Failed with load config.properties");
        }
    }

    public static String getBotToken() {
        return configuration.getString("bot.token");
    }
}
