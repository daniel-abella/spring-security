package com.example.demo.config;

import com.example.demo.model.Role;
import com.example.demo.model.Product;
import com.example.demo.model.Profile;
import com.example.demo.model.User;
import com.example.demo.repository.ProfileRepository;
import com.example.demo.repository.RoleRepository;
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
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;

    @Override
    public void run(String... args) throws Exception {
        // --- Inicializar permissões ---
        Role permUser = roleRepository.findByName("USER")
            .orElseGet(() -> roleRepository.save(Role.builder().name("USER").build()));
        Role permAdmin = roleRepository.findByName("ADMIN")
            .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").build()));
        Role permProductRead = roleRepository.findByName("PRODUCT_READ")
            .orElseGet(() -> roleRepository.save(Role.builder().name("PRODUCT_READ").build()));
        Role permProductWrite = roleRepository.findByName("PRODUCT_WRITE")
            .orElseGet(() -> roleRepository.save(Role.builder().name("PRODUCT_WRITE").build()));

        // --- Inicializar perfis (profiles) e relacionar permissões ---
        Profile userProfile = profileRepository.findByName("USER")
            .orElseGet(() -> profileRepository.save(Profile.builder()
                .name("USER")
                .build()));
        userProfile.getRoles().add(permUser);
        userProfile.getRoles().add(permProductRead);
        profileRepository.save(userProfile);

        Profile adminProfile = profileRepository.findByName("ADMIN")
            .orElseGet(() -> profileRepository.save(Profile.builder()
                .name("ADMIN")
                .build()));
        adminProfile.getRoles().add(permAdmin);
        adminProfile.getRoles().add(permUser);
        adminProfile.getRoles().add(permProductRead);
        adminProfile.getRoles().add(permProductWrite);
        profileRepository.save(adminProfile);

        // --- Criar usuários iniciais vinculados a perfis ---
        if (userService.findByUsername("abella").isEmpty()) {
            User u = User.builder()
                .username("abella")
                .password("password")
                .build();
            u.getProfiles().add(userProfile);
            userService.save(u);
        }
        if (userService.findByUsername("vinicius").isEmpty()) {
            User a = User.builder()
                .username("vinicius")
                .password("password")
                .build();
            a.getProfiles().add(adminProfile);
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
