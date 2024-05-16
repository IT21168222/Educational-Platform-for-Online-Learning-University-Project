package com.sliit.LearnerService.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import client.CourseClient;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
	@Autowired
    private LoadBalancedExchangeFilterFunction filterFunction;
    @Bean
    public WebClient courseServiceWebClient() {
        return WebClient.builder()
                .baseUrl("http://Course-Management-Service")
                .filter(filterFunction)
                .build();
    }

    @Bean
    public CourseClient courseClient() {
        HttpServiceProxyFactory httpServiceProxyFactory
                = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(courseServiceWebClient()))
                .build();
        return httpServiceProxyFactory.createClient(CourseClient.class);
    }
}
