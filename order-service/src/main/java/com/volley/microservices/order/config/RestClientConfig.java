package com.volley.microservices.order.config;

import lombok.RequiredArgsConstructor;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.volley.microservices.order.client.InventoryClient;

import io.micrometer.observation.ObservationRegistry;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {
	
	    @Value("${inventory.service.url}")
	    private String inventoryServiceUrl;
	    
	    private final ObservationRegistry observationRegistry;

	
	
	@Bean
	 public InventoryClient inventoryClient() {
	        RestClient restClient = RestClient.builder()
	        		.baseUrl(inventoryServiceUrl)
	        		.requestFactory(getClientRequestFactory())
	        		.observationRegistry(observationRegistry)
	        		.build();
	        
	        var restClientAdapter = RestClientAdapter.create(restClient);
	        var httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
	        return httpServiceProxyFactory.createClient(InventoryClient.class);

}



	private ClientHttpRequestFactory getClientRequestFactory() {
		 ClientHttpRequestFactorySettings clientHttpRequestFactorySettings = ClientHttpRequestFactorySettings.DEFAULTS
	                .withConnectTimeout(Duration.ofSeconds(3))
	                .withReadTimeout(Duration.ofSeconds(3));
	        return ClientHttpRequestFactories.get(clientHttpRequestFactorySettings);
	}
}