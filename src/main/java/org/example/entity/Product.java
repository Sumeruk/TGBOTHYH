package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int price;
    private String type;

    public Product(String name, int price, String type) {
        this.name = name;
        this.price = price;
        this.type = type;
    }

    public Product() {
    }
}
