package com.example.demo.dto;

// MELHORIA - DTO para resposta de produto sem campos internos de auditoria
// MELHORIA - Controle exato de quais dados são expostos na API

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
}
