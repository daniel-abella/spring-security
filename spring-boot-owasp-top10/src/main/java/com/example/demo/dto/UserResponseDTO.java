package com.example.demo.dto;

// MELHORIA - DTO para resposta que NÃO expõe senha do usuário
// MELHORIA - Previne vazamento de informações sensíveis e estrutura interna do banco

import com.example.demo.model.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String username;
    private Set<Profile> profiles;
}
