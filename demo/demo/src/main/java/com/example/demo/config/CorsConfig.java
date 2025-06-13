package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply CORS to all paths in your application
                .allowedOrigins("http://localhost:3000", "https://your-react-app.com", "http://localhost:80") // <--- IMPORTANT: Replace with your React app's domain(s)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
                .allowedHeaders("*") // Allow all headers from the client
                .allowCredentials(true); // Allow sending cookies, authorization headers etc.
//                .maxAge(3600); // How long the preflight request can be cached (in seconds)
    }
}