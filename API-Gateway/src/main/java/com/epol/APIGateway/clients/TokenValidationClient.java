package com.epol.APIGateway.clients;

import com.epol.APIGateway.filter.TokenValidationResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface TokenValidationClient {
    @GetExchange(value = "/api/v1/auth/validate")
    TokenValidationResult validateToken(@RequestParam String token);
}
