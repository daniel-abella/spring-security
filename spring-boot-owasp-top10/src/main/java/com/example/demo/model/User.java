package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


/**
 * Entidade `User` representa um usuário da aplicação.
 *
 * Observação: antes as roles eram armazenadas como String.
 * Agora relacionamos `User` com `Profile` (muitos-para-muitos),
 * onde cada `Profile` possui um conjunto de `Role`.
 * 
 * MELHORIA - Estende AuditableEntity para rastreamento de criação/modificação
 */
@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // Relação muitos-para-muitos entre usuários e perfis (roles agrupadas)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_profiles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "profile_id")
    )
    @Builder.Default
    private Set<Profile> profiles = new HashSet<>();
}
