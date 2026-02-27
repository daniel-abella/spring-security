package com.example.demo.dto;

// MELHORIA - DTO para resposta de login contendo token JWT
// MELHORIA - Tipo "Bearer" como padrão para uso no header Authorization

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private String token;
    
    @Builder.Default
    private String type = "Bearer";
    
    private String username;
}
