package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade `Role` representa uma role/permission utilizada pelo Spring Security.
 * Exemplo: "ADMIN", "USER", "PRODUCT_READ".
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // nome da role (sem o prefixo ROLE_)
}
