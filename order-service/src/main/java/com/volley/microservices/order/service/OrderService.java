package com.volley.microservices.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.volley.microservices.order.client.InventoryClient;
import com.volley.microservices.order.dto.OrderRequest;
import com.volley.microservices.order.event.OrderPlacedEvent;
import com.volley.microservices.order.model.Order;
import com.volley.microservices.order.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
	
	private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    
    
  
    public void placeOrder(OrderRequest orderRequest) {

        var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
        if (isProductInStock) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price().multiply(BigDecimal.valueOf(orderRequest.quantity())));
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());
            orderRepository.save(order);
            
          
            
           
            // Send the message to Kafka Topic
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
            orderPlacedEvent.setOrderNumber(order.getOrderNumber());
           
            orderPlacedEvent.setEmail(orderRequest.userDetails().email());
            if (orderRequest.userDetails().firstName() == null) {
            	log.info("Firstname is null");
                orderPlacedEvent.setFirstName("Arumugam");
            }else {
            	log.info("Firstname is ", orderRequest.userDetails().firstName());
            	orderPlacedEvent.setFirstName(orderRequest.userDetails().firstName());
            }
            if (orderRequest.userDetails().lastName() == null) {
            	log.info("lastname is null");
                orderPlacedEvent.setLastName("V");
            }else {
            	log.info("lastname is ",orderRequest.userDetails().lastName() );
            	orderPlacedEvent.setLastName(orderRequest.userDetails().lastName());
            }
            //orderPlacedEvent.setFirstName(orderRequest.userDetails().firstName());
           // orderPlacedEvent.setLastName(orderRequest.userDetails().lastName());
            log.info("Start - Sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);
            kafkaTemplate.send("order-placed", orderPlacedEvent);
            log.info("End - Sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);
            
            
        }else{
            throw new RuntimeException("Product with SkuCode " + orderRequest.skuCode() + " is not in stock");
        }

           
    }
}