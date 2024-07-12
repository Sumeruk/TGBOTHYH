package org.example.models;

import lombok.Data;

@Data
public class ProductModel {
    private String name;
    private int price;
    private String type;

    public ProductModel(String name, int price, String type) {
        this.name = name;
        this.price = price;
        this.type = type;
    }
}
