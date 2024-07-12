package org.example;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.example.models.ProductModel;
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
    public static Integer getNumberOfTables(){return Integer.parseInt(configuration.getString("tables.number"));}
    public static List<ProductModel> getFood() {
        try {
            return readLinesFromFile(foodsResource.getFile());
        } catch (Exception io){
            throw new RuntimeException("Cannot read food file");
        }
    }

    public static List<ProductModel> getDrinks() {
        try {
            return readLinesFromFile(drinksResource.getFile());
        } catch (Exception io){
            throw new RuntimeException("Cannot read drinks file");
        }
    }

    private static List<ProductModel> readLinesFromFile(File file){
        List<ProductModel> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                String name = parts[0];
                int price = Integer.parseInt(parts[1]);

                ProductModel product = new ProductModel(name, price, file.getName().replace(".txt", ""));
                result.add(product);
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
