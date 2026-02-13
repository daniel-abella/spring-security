package com.example.demo.config;

import com.example.demo.model.User;
import com.example.demo.model.Product;
import com.example.demo.service.UserService;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserService userService;
    private final ProductService productService;

    @Override
    public void run(String... args) throws Exception {
        if (userService.findByUsername("user").isEmpty()) {
            User u = User.builder()
                    .username("user")
                    .password("password")
                    .roles("USER")
                    .build();
            userService.save(u);
        }
        if (userService.findByUsername("admin").isEmpty()) {
            User a = User.builder()
                    .username("admin")
                    .password("password")
                    .roles("ADMIN")
                    .build();
            userService.save(a);
        }
        // Seed a sample product so /api/products/1 returns a resource instead of 404
        if (productService.findAll().isEmpty()) {
            Product p = Product.builder()
                .name("Sample Product")
                .description("Produto inicial gerado automaticamente")
                .price(new java.math.BigDecimal("9.90"))
                .build();
            productService.save(p);
        }
    }
}
