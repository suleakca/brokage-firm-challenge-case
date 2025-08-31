package com.brokerage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brokerage.entity.Order;
import com.brokerage.entity.OrderStatus;

import java.time.OffsetDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdAndCreateDateBetween(String customerId, OffsetDateTime start, OffsetDateTime end);
    List<Order> findByCustomerId(String customerId);
    List<Order> findByStatus(OrderStatus status);
}