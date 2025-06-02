package com.swens.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleFilterGatewayFilterFactory extends AbstractGatewayFilterFactory<RoleFilterGatewayFilterFactory.Config> {

    public RoleFilterGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            List<String> allowedRoles = Arrays.stream(config.requiredRole.split(","))
                    .map(String::toLowerCase)
                    .toList();

            String roleHeader = exchange.getRequest().getHeaders().getFirst("X-ROLE");

            if (roleHeader == null || !allowedRoles.contains(roleHeader.toLowerCase())) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }


    public static class Config {
        private String requiredRole;

        public String getRequiredRole() {
            return requiredRole;
        }

        public void setRequiredRole(String requiredRole) {
            this.requiredRole = requiredRole;
        }
    }
}
