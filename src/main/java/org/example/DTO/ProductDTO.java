package org.example.DTO;

import lombok.Data;

@Data
public class ProductDTO {
    private String name;
    private int amount;

    public ProductDTO(String name) {
        this.name = name;
        amount = 0;
    }

    @Override
    public String toString() {
        return name + ' ' + amount ;
    }
}
