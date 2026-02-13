package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade `Permission` representa uma permissão/role utilizada pelo Spring Security.
 * Exemplo: "ADMIN", "USER", "PRODUCT_READ".
 */
@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // nome da permissão (sem o prefixo ROLE_)
}
