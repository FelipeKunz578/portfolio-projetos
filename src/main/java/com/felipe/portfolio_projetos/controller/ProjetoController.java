package com.felipe.portfolio_projetos.controller;

import org.springframework.web.bind.annotation.RestController;

import com.felipe.portfolio_projetos.service.ProjetoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.felipe.portfolio_projetos.dto.DataTerminoProjetoDTO;
import com.felipe.portfolio_projetos.dto.ProjetoRequestDTO;
import com.felipe.portfolio_projetos.dto.ProjetoResponseDTO;
import com.felipe.portfolio_projetos.dto.ProjetoUpdateDTO;
import com.felipe.portfolio_projetos.dto.RelatorioPortfolioDTO;
import com.felipe.portfolio_projetos.enums.StatusProjeto;

import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@Tag(name = "Projeto Controller", description = "Endpoints para gerenciar projetos")
@RestController
@RequestMapping("/projetos")
public class ProjetoController {

    private final ProjetoService projetoService;

    public ProjetoController(ProjetoService projetoService) {
        this.projetoService = projetoService;
    }

    @Operation(summary = "Criação de Projeto", description = "Cria um novo projeto no sistema. não pode adicionar membros aqui, só o gerente(com o ID dele). o risco é calculado automaticamente, e a data de termino é posta quando for encerrar ou cancelar. Não é possível criar um projeto com um gerente que já tenha 3 projetos ativos.")
    @PostMapping("/criar")
    public ResponseEntity<ProjetoResponseDTO> criarProjeto(@RequestBody ProjetoRequestDTO projetoRequestDTO) {
        ProjetoResponseDTO projetoResponseDTO = projetoService.criarProjeto(projetoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(projetoResponseDTO);
    }

    @Operation(summary = "Listar Projetos", description = "Lista todos os projetos cadastrados no sistema, mostrando seus detalhes e pessoas que estão cadastradas por ID.")
    @GetMapping("/listar")
    public List<ProjetoResponseDTO> listarProjetos() {
        return projetoService.listarProjetos();
    }

    @Operation(summary = "Buscar Projeto", description = "Busca um projeto por ID, mostrando seus detalhes e os IDs das pessoas que estão cadastradas.")
    @GetMapping("/buscar/{id}")
    public ResponseEntity<ProjetoResponseDTO> buscarProjeto(@PathVariable Long id) {
        ProjetoResponseDTO projetoResponseDTO = projetoService.buscarProjeto(id);
        return ResponseEntity.ok(projetoResponseDTO);
    }

    @Operation(summary = "Atualizar Projeto", description = "Atualiza os detalhes de um projeto existente por ID. Não é possível atualizar o gerente, risco, status(diretamente) ou membros por este endpoint.")
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ProjetoResponseDTO> atualizarProjeto(
            @PathVariable Long id,
            @RequestBody ProjetoUpdateDTO projetoUpdate) {

        ProjetoResponseDTO response = projetoService.atualizarProjeto(id, projetoUpdate);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deletar Projeto", description = "Deleta um projeto do sistema por ID.")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarProjeto(@PathVariable Long id) {
        projetoService.deletarProjeto(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualizar o Status do Projeto", description = "Atualiza os STATUS de um projeto existente por ID por ordem 'em análise → análise realizada → análise aprovada → iniciado → planejado → em andamento → encerrado'. Não é possível atualizar um projeto encerrado ou cancelado. **QUANDO O PROJETO FOR DE 'em andamento' PARA 'encerrado' É NECESSÁRIO INFORMAR A DATA DE TÉRMINO.**")
    @PutMapping("/avancar-status/{id}")
    public ResponseEntity<ProjetoResponseDTO> avancarStatus(
            @PathVariable Long id,
            @RequestBody(required = false) DataTerminoProjetoDTO dto) {

        LocalDate data = dto != null ? dto.getDataTermino() : null;

        ProjetoResponseDTO projetoResponseDTO = projetoService.avancarStatus(id, data);

        return ResponseEntity.ok(projetoResponseDTO);
    }

    @Operation(summary = "Cancelar Projeto", description = "Cancela um projeto existente por ID. É necessário informar a data de término ao cancelar.")
    @PostMapping("/cancelar/{id}")
    public ProjetoResponseDTO cancelar(
            @PathVariable Long id,
            @RequestBody DataTerminoProjetoDTO dto) {

        return projetoService.cancelarProjeto(id, dto.getDataTermino());
    }

    @Operation(summary = "Adicionar Membro ao Projeto", description = "Adiciona um membro existente a um projeto existente por seus IDs. Não é possível adicionar membros a projetos encerrados ou cancelados, nem adicionar gerentes, colocar membros com mais de 3 projetos em ativo. Limite máximo de membros por projeto é 10(contando com o GERENTE).")
    @PostMapping("/{projetoId}/adicionar-membro/{membroId}")
    public ResponseEntity<ProjetoResponseDTO> adicionarMembro(
            @PathVariable Long projetoId,
            @PathVariable Long membroId) {

        ProjetoResponseDTO projetoResponseDTO = projetoService.adicionarMembro(projetoId, membroId);

        return ResponseEntity.ok(projetoResponseDTO);
    }

    @Operation(summary = "Gerar Relatório de Portfólio de Projetos", description = "Gera um relatório resumido do portfólio de projetos, incluindo a quantidade de projetos por status, o total orçado por status, a duração média dos projetos encerrados e o total de membros únicos envolvidos nos projetos.")
    @GetMapping("/relatorio-portfolio")
    public ResponseEntity<RelatorioPortfolioDTO> gerarRelatorio() {
        RelatorioPortfolioDTO relatorio = projetoService.gerarRelatorio();
        return ResponseEntity.ok(relatorio);
    }

    @Operation(summary = "Buscar Projetos por Gerente", description = "Busca todos os projetos gerenciados por um gerente específico, identificado pelo seu ID.")
    @GetMapping("/buscar-gerente/{id}")
    public ResponseEntity<List<ProjetoResponseDTO>> buscarProjetoPorGerente(@PathVariable Long id) {
        List<ProjetoResponseDTO> projetos = projetoService.buscarProjetoPorGerente(id);
        return ResponseEntity.ok(projetos);
    }

    @Operation(summary = "Buscar Projetos por Status", description = "Busca todos os projetos com um status específico. Sendo eles EM_ANALISE, ANALISE_REALIZADA, ANALISE_APROVADA, INICIADO, PLANEJADO, EM_ANDAMENTO, ENCERRADO ou CANCELADO.")
    @GetMapping("/buscar-status/{status}")
    public ResponseEntity<List<ProjetoResponseDTO>> buscarProjetosPorStatus(@PathVariable String status) {
        StatusProjeto statusProjeto = StatusProjeto.valueOf(status.toUpperCase());
        List<ProjetoResponseDTO> projetos = projetoService.buscarProjetosPorStatus(statusProjeto);
        return ResponseEntity.ok(projetos);
    }
    
}