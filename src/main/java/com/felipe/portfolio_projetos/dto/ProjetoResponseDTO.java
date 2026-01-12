package com.felipe.portfolio_projetos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.felipe.portfolio_projetos.entity.Membro;
import com.felipe.portfolio_projetos.entity.Projeto;
import com.felipe.portfolio_projetos.enums.RiscoProjeto;
import com.felipe.portfolio_projetos.enums.StatusProjeto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "DTO para resposta de informações do projeto.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoResponseDTO {

    private Long id;
    @Schema(description = "Nome do projeto.", example = "Projeto X")
    private String nome;
    @Schema(description = "Data de início do projeto.")
    private LocalDate dataInicio;
    @Schema(description = "Data prevista de término do projeto.")
    private LocalDate dataPrevisaoTermino;
    @Schema(description = "Data de término do projeto.")
    private LocalDate dataTermino;
    @Schema(description = "Orçamento total do projeto.", example = "10000.00")
    private BigDecimal orcamentoTotal;
    @Schema(description = "Descrição do projeto.", example = "Descrição detalhada do Projeto X")
    private String descricao;
    private Long gerenteProjetoId;
    @Schema(description = "Status atual do projeto.")
    private StatusProjeto status;
    @Schema(description = "Nível de risco do projeto.")
    private RiscoProjeto risco;
    private List<Long> membrosIds;

    public static ProjetoResponseDTO fromEntity(Projeto projeto) {
        Long gerenteId = projeto.getGerenteProjeto() != null ? projeto.getGerenteProjeto().getId() : null;

        return new ProjetoResponseDTO(
                projeto.getId(),
                projeto.getNome(),
                projeto.getDataInicio(),
                projeto.getDataPrevisaoTermino(),
                projeto.getDataTermino(),
                projeto.getOrcamentoTotal(),
                projeto.getDescricao(),
                gerenteId,
                projeto.getStatus(),
                projeto.getRisco(),
                projeto.getMembrosEquipe()
                    .stream()
                    .map(Membro::getId)
                    .toList()
        );
    }
}
