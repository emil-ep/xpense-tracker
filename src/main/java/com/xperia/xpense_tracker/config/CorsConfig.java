package com.xperia.xpense_tracker.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfiguration corsConfiguration() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:8080",
                "http://localhost:3001",
                "http://9.20.198.82:3000",
                "http://10.51.3.224:3000",
                "http://9.20.198.82:8085",
                "http://10.51.3.224:8085"
        ));
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("*"));
        cors.setAllowCredentials(true);
        cors.setMaxAge(3600L);
        return cors;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        // wrap your CorsConfiguration in a UrlBasedCorsConfigurationSource
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration());

        CorsFilter corsFilter = new CorsFilter(source);
        FilterRegistrationBean<CorsFilter> reg = new FilterRegistrationBean<>(corsFilter);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);  // run before any other filter
        return reg;
    }
}
