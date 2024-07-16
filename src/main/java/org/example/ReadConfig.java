package org.example;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.example.entity.Product;
import org.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static  List<Product> foods = new ArrayList<>();
    private static  List<Product> drinks = new ArrayList<>();
    private static ProductRepository productRepository;

    static {
        try{
            configuration = new PropertiesConfiguration("config.properties");
            foodsResource = new ClassPathResource("foods.txt");
            drinksResource = new ClassPathResource("drinks.txt");

        }  catch (ConfigurationException e) {
            throw new RuntimeException("Failed with load config.properties");
        }
    }

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        ReadConfig.productRepository = productRepository;
    }

    public static String getBotToken() {
        return configuration.getString("bot.token");
    }
    public static Integer getNumberOfTables(){return Integer.parseInt(configuration.getString("tables.number"));}
    public static List<Product> getFoodsFromRepository() {
        try {
            if(foods.isEmpty()){
                foods = readLinesFromFile(foodsResource.getFile());
            }
            return foods;
        } catch (Exception io){
            throw new RuntimeException("Cannot read food file");
        }
    }

    public static List<Product> getDrinksFromRepository() {
        try {
            if (drinks.isEmpty()){
                drinks = readLinesFromFile(drinksResource.getFile());
            }
            return drinks;
        } catch (Exception io){
            throw new RuntimeException("Cannot read drinks file");
        }
    }
    private static List<Product> readLinesFromFile(File file){
        List<Product> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                String name = parts[0];
                int price = Integer.parseInt(parts[1]);

                Product currProduct = new Product(name, price, file.getName().replace(".txt", ""));
                productRepository.save(currProduct);
                result.add(currProduct);
            }

            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
