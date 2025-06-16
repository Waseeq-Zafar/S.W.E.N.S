package com.swens.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsGlobalConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // ✅ Replace with your actual frontend URLs
        corsConfig.setAllowedOriginPatterns(List.of(
                "https://your-production-frontend.com",
                "http://localhost:5173" // Optional: keep for local dev, remove in prod
        ));

        corsConfig.setAllowCredentials(true); // Allow cookies/auth headers

        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        corsConfig.setExposedHeaders(List.of("Authorization")); // Optional
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L); // 1-hour preflight cache

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
