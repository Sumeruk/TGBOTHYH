package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int desk;
    @Column(name = "is_completed")
    private boolean isCompleted;

    public Order(int desk, boolean isCompleted) {
        this.desk = desk;
        this.isCompleted = isCompleted;
    }

    public Order() {

    }
}
