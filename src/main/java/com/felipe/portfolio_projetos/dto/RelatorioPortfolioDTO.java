package com.felipe.portfolio_projetos.dto;

import com.felipe.portfolio_projetos.enums.StatusProjeto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.math.BigDecimal;

@Schema(description = "DTO para relatório de portfólio de projetos.")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioPortfolioDTO {
    private Map<StatusProjeto, Long> quantidadePorStatus;
    private Map<StatusProjeto, BigDecimal> totalOrcadoPorStatus;
    private double duracaoMediaProjetosEncerrados;
    private Long totalMembrosUnicos;
}
