package com.example.demo.config;

import com.example.demo.model.Permission;
import com.example.demo.model.Product;
import com.example.demo.model.Profile;
import com.example.demo.model.User;
import com.example.demo.repository.ProfileRepository;
import com.example.demo.repository.PermissionRepository;
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
    private final PermissionRepository permissionRepository;
    private final ProfileRepository profileRepository;

    @Override
    public void run(String... args) throws Exception {
        // --- Inicializar permissões ---
        Permission permUser = permissionRepository.findByName("USER")
            .orElseGet(() -> permissionRepository.save(Permission.builder().name("USER").build()));
        Permission permAdmin = permissionRepository.findByName("ADMIN")
            .orElseGet(() -> permissionRepository.save(Permission.builder().name("ADMIN").build()));
        Permission permProductRead = permissionRepository.findByName("PRODUCT_READ")
            .orElseGet(() -> permissionRepository.save(Permission.builder().name("PRODUCT_READ").build()));
        Permission permProductWrite = permissionRepository.findByName("PRODUCT_WRITE")
            .orElseGet(() -> permissionRepository.save(Permission.builder().name("PRODUCT_WRITE").build()));

        // --- Inicializar perfis (profiles) e relacionar permissões ---
        Profile userProfile = profileRepository.findByName("USER")
            .orElseGet(() -> profileRepository.save(Profile.builder()
                .name("USER")
                .build()));
        userProfile.getPermissions().add(permUser);
        userProfile.getPermissions().add(permProductRead);
        profileRepository.save(userProfile);

        Profile adminProfile = profileRepository.findByName("ADMIN")
            .orElseGet(() -> profileRepository.save(Profile.builder()
                .name("ADMIN")
                .build()));
        adminProfile.getPermissions().add(permAdmin);
        adminProfile.getPermissions().add(permUser);
        adminProfile.getPermissions().add(permProductRead);
        adminProfile.getPermissions().add(permProductWrite);
        profileRepository.save(adminProfile);

        // --- Criar usuários iniciais vinculados a perfis ---
        if (userService.findByUsername("user").isEmpty()) {
            User u = User.builder()
                .username("user")
                .password("password")
                .build();
            u.getProfiles().add(userProfile);
            userService.save(u);
        }
        if (userService.findByUsername("admin").isEmpty()) {
            User a = User.builder()
                .username("admin")
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
