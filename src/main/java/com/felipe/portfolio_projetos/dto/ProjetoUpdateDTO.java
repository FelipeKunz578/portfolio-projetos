package com.felipe.portfolio_projetos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "DTO para atualização de informações do projeto.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoUpdateDTO {
    @Schema(description = "Nome do projeto.", example = "Projeto X")
    private String nome;
    @Schema(description = "Descrição do projeto.", example = "Descrição detalhada do Projeto X")
    private String descricao;
    @Schema(description = "Data prevista de término do projeto.")
    private LocalDate dataPrevisaoTermino;
    @Schema(description = "Orçamento total do projeto.", example = "10000.00")
    private BigDecimal orcamentoTotal;
}
