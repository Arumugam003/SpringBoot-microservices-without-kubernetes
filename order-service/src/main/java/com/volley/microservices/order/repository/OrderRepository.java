package com.volley.microservices.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volley.microservices.order.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
