package com.volley.microservices.product;

import java.math.BigDecimal;


import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;

import com.volley.microservices.product.dto.ProductRequest;

import io.restassured.RestAssured;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {
	
	   @ServiceConnection
	    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");
	    @LocalServerPort
	    private Integer port;

	    @BeforeEach
	    void setup() {
	        RestAssured.baseURI = "http://localhost";
	        RestAssured.port = port;
	    }

	    static {
	        mongoDBContainer.start();
	    }

	    @Test
	    void shouldCreateProduct() throws Exception {
	        ProductRequest productRequest = getProductRequest();

	        RestAssured.given()
	                .contentType("application/json")
	                .body(productRequest)
	                .when()
	                .post("/api/product")
	                .then()
	                .log().all()
	                .statusCode(201)
	                .body("id", Matchers.notNullValue())
	                .body("name", Matchers.equalTo(productRequest.name()))
	                .body("description", Matchers.equalTo(productRequest.description()))
	                .body("price", Matchers.is(productRequest.price().intValueExact()));
	    }

	    private ProductRequest getProductRequest() {
	        return new ProductRequest("1","iPhone 13", "iPhone 13", null,BigDecimal.valueOf(1200));
	    }

	}