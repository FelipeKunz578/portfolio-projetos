package com.felipe.portfolio_projetos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "DTO para informações do membro.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembroResponseDTO {
    private Long id;
    @Schema(description = "Nome do membro.", example = "Pedro")
    private String nome;
    @Schema(description = "Cargo do membro.", example = "GERENTE")
    private String role;

    public static MembroResponseDTO fromEntity(com.felipe.portfolio_projetos.entity.Membro membro) {
        return new MembroResponseDTO(
            membro.getId(),
            membro.getNome(),
            membro.getRole().name()
        );
    }
}
