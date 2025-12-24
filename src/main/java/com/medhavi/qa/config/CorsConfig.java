package com.medhavi.qa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // Default to Vite dev server.
        String origin = System.getenv().getOrDefault("CORS_ORIGIN", "http://localhost:5173");

        CorsConfiguration cfg = new CorsConfiguration();
        cfg.addAllowedOrigin(origin);
        cfg.setAllowCredentials(true);
        cfg.addAllowedHeader("*");
        cfg.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return new CorsFilter(source);
    }
}
