package org.example;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReadConfig {
    private static final PropertiesConfiguration configuration;
    private static final Resource foodsResource;
    private static final Resource drinksResource;

    static {
        try{
            configuration = new PropertiesConfiguration("config.properties");
            foodsResource = new ClassPathResource("foods.txt");
            drinksResource = new ClassPathResource("drinks.txt");
        }  catch (ConfigurationException e) {
            throw new RuntimeException("Failed with load config.properties");
        }
    }

    public static String getBotToken() {
        return configuration.getString("bot.token");
    }
    public static List<String> getFood() {
        try {
            return readLinesFromFile(foodsResource.getFile());
        } catch (IOException io){
            throw new RuntimeException("Cannot read food file");
        }
    }

    public static List<String> getDrinks() {
        try {
            return readLinesFromFile(drinksResource.getFile());
        } catch (IOException io) {
            throw new RuntimeException("Cannot read food file");
        }
    }

    private static List<String> readLinesFromFile(File file){
        List<String> result = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(file));) {
            while (br.ready()) {
                result.add(br.readLine());
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
