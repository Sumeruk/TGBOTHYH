package org.example.repository;

import org.example.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query(value = "SELECT t.id, t.desk, t.is_completed from orders t where t.is_completed = false",
            nativeQuery = true)
    public List<Order> getNotCompletedOrders();
}
