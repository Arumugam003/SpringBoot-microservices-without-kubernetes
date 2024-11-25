package com.volley.microservices.product.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import com.volley.microservices.product.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
}
