package com.example.demo.model;

// MELHORIA - Entidade estende AuditableEntity para rastreamento automático
// MELHORIA - Campos createdAt, updatedAt, createdBy, updatedBy adicionados automaticamente

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;
}
