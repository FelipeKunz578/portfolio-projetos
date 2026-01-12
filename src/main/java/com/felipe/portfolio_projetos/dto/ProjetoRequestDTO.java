package com.felipe.portfolio_projetos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.felipe.portfolio_projetos.entity.Projeto;
import com.felipe.portfolio_projetos.enums.StatusProjeto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "DTO para requisição de criação de projeto.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoRequestDTO {

    @Schema(description = "Nome do projeto.", example = "Projeto X")
    private String nome;
    @Schema(description = "Data de início do projeto.")
    private LocalDate dataInicio;
    @Schema(description = "Data prevista de término do projeto.")
    private LocalDate dataPrevisaoTermino;
    @Schema(description = "Orçamento total do projeto.", example = "10000.00")
    private BigDecimal orcamentoTotal;
    @Schema(description = "Descrição do projeto.", example = "Descrição detalhada do Projeto X")
    private String descricao;
    private Long gerenteProjetoId; 


    public Projeto toEntity() {
        Projeto projeto = new Projeto();
        projeto.setNome(this.nome);
        projeto.setDataInicio(this.dataInicio);
        projeto.setDataPrevisaoTermino(this.dataPrevisaoTermino);
        projeto.setOrcamentoTotal(this.orcamentoTotal);
        projeto.setDescricao(this.descricao);
        projeto.setStatus(StatusProjeto.EM_ANALISE);
        projeto.setMembrosEquipe(new java.util.ArrayList<>()); 
        return projeto;
    }
}

