package com.felipe.portfolio_projetos.dto;

import com.felipe.portfolio_projetos.enums.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "DTO para verificação de cargo do membro.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembroRequestDTO {
    @Schema(description = "Nome do membro.", example = "Pedro")
    private String nome;
    @Schema(description = "Cargo do membro.", example = "GERENTE")  
    private Role role;
}