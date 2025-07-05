package ru.litvak.files_service.conf;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    @Qualifier("user-service")
    @LoadBalanced
    public RestTemplate getUserServiceRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Qualifier("wishlist-service")
    @LoadBalanced
    public RestTemplate getWishListServiceRestTemplate() {
        return new RestTemplate();
    }
}
