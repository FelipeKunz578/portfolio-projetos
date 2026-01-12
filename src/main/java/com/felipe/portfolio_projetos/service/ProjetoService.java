package com.felipe.portfolio_projetos.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.felipe.portfolio_projetos.repository.MembroRepository;
import com.felipe.portfolio_projetos.repository.ProjetoRepository;
import com.felipe.portfolio_projetos.entity.Membro;
import com.felipe.portfolio_projetos.entity.Projeto;
import com.felipe.portfolio_projetos.enums.RiscoProjeto;
import com.felipe.portfolio_projetos.enums.Role;
import com.felipe.portfolio_projetos.enums.StatusProjeto;
import com.felipe.portfolio_projetos.dto.ProjetoRequestDTO;
import com.felipe.portfolio_projetos.dto.ProjetoResponseDTO;
import com.felipe.portfolio_projetos.dto.ProjetoUpdateDTO;
import com.felipe.portfolio_projetos.dto.RelatorioPortfolioDTO;

@Service
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final MembroRepository membroRepository;

    public ProjetoService(ProjetoRepository projetoRepository, MembroRepository membroRepository) {
        this.projetoRepository = projetoRepository;
        this.membroRepository = membroRepository;
    }

    public ProjetoResponseDTO criarProjeto(ProjetoRequestDTO projetoRequest) {
        Membro gerente = membroRepository.findById(projetoRequest.getGerenteProjetoId())
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));

        if (gerente.getRole() != Role.GERENTE) {
            throw new RuntimeException("Membro não é um gerente");
        }

        if (projetoRequest.getDataPrevisaoTermino().isBefore(projetoRequest.getDataInicio())) {
            throw new RuntimeException("Data de previsão de término não pode ser anterior à data de início");
        }

        long projetosAtivos = projetoRepository.countByGerenteProjetoAndStatusNotIn(
                gerente,
                List.of(StatusProjeto.ENCERRADO, StatusProjeto.CANCELADO));

        if (projetosAtivos >= 3) {
            throw new RuntimeException("Gerente já possui 3 projetos ativos");
        }

        Projeto projeto = projetoRequest.toEntity();
        projeto.setGerenteProjeto(gerente);
        projeto.setRisco(calcularRisco(projeto));
        projeto.setMembrosEquipe(new ArrayList<Membro>());

        Projeto salvo = projetoRepository.save(projeto);

        return ProjetoResponseDTO.fromEntity(salvo);
    }

    public ProjetoResponseDTO atualizarProjeto(Long id, ProjetoUpdateDTO projetoUpdate) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if (projetoUpdate.getNome() != null) {
            projeto.setNome(projetoUpdate.getNome());
        }
        if (projetoUpdate.getDataPrevisaoTermino() != null) {
            projeto.setDataPrevisaoTermino(projetoUpdate.getDataPrevisaoTermino());
        }
        if (projetoUpdate.getOrcamentoTotal() != null) {
            projeto.setOrcamentoTotal(projetoUpdate.getOrcamentoTotal());
        }
        if (projetoUpdate.getDescricao() != null) {
            projeto.setDescricao(projetoUpdate.getDescricao());
        }

        if (projeto.getStatus() == StatusProjeto.ENCERRADO || projeto.getStatus() == StatusProjeto.CANCELADO) {
            throw new RuntimeException("Não é possível atualizar um projeto finalizado");
        }

        projeto.setRisco(calcularRisco(projeto));

        Projeto atualizado = projetoRepository.save(projeto);

        return ProjetoResponseDTO.fromEntity(atualizado);
    }

    public RiscoProjeto calcularRisco(Projeto projeto) {
        long meses = java.time.temporal.ChronoUnit.MONTHS.between(
                projeto.getDataInicio(), projeto.getDataPrevisaoTermino());

        if (projeto.getOrcamentoTotal().compareTo(BigDecimal.valueOf(100000)) <= 0 && meses <= 3) {
            return RiscoProjeto.BAIXO;
        }
        if (projeto.getOrcamentoTotal().compareTo(BigDecimal.valueOf(500000)) > 0 || meses > 6) {
            return RiscoProjeto.ALTO;
        } else {
            return RiscoProjeto.MEDIO;
        }
    }

    public List<ProjetoResponseDTO> listarProjetos() {
        List<Projeto> projetos = projetoRepository.findAll();
        List<ProjetoResponseDTO> projetoDTOs = new ArrayList<>();

        for (Projeto projeto : projetos) {
            projetoDTOs.add(ProjetoResponseDTO.fromEntity(projeto));
        }

        return projetoDTOs;
    }

    public List<ProjetoResponseDTO> buscarProjetoPorGerente(Long gerenteId) {
        Membro gerente = membroRepository.findById(gerenteId)
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));
        List<Projeto> projetos = projetoRepository.findAll().stream()
                .filter(p -> p.getGerenteProjeto().equals(gerente))
                .toList();
        List<ProjetoResponseDTO> projetoDTOs = new ArrayList<>();

        for (Projeto projeto : projetos) {
            projetoDTOs.add(ProjetoResponseDTO.fromEntity(projeto));
        }

        return projetoDTOs;
    }

    public List<ProjetoResponseDTO> buscarProjetosPorStatus(StatusProjeto status) {
        List<Projeto> projetos = projetoRepository.findAll().stream()
                .filter(p -> p.getStatus() == status)
                .toList();
        List<ProjetoResponseDTO> projetoDTOs = new ArrayList<>();

        for (Projeto projeto : projetos) {
            projetoDTOs.add(ProjetoResponseDTO.fromEntity(projeto));
        }

        return projetoDTOs;
    }

    public ProjetoResponseDTO buscarProjeto(Long id) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        return ProjetoResponseDTO.fromEntity(projeto);
    }

    public void deletarProjeto(Long id) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if (projeto.getStatus() == StatusProjeto.INICIADO || projeto.getStatus() == StatusProjeto.EM_ANDAMENTO
                || projeto.getStatus() == StatusProjeto.ENCERRADO) {
            throw new RuntimeException(
                    "Não foi possível deletar o projeto. ELe está iniciado, em andamento ou encerrado.");
        }

        projetoRepository.delete(projeto);
    }

    @Transactional
    public ProjetoResponseDTO avancarStatus(Long projetoId, LocalDate dataTermino) {
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        StatusProjeto proximo = projeto.getStatus().atualizar();

        if (proximo == StatusProjeto.ENCERRADO) {

            if (dataTermino == null) {
                throw new RuntimeException(
                        "Data de término é obrigatória para encerrar o projeto");
            }

            if (dataTermino.isBefore(projeto.getDataInicio())) {
                throw new RuntimeException("Data de término inválida");
            }

            projeto.setDataTermino(dataTermino);
        }

        projeto.setStatus(proximo);

        return ProjetoResponseDTO.fromEntity(projeto);
    }

    @Transactional
    public ProjetoResponseDTO cancelarProjeto(Long id, LocalDate dataTermino) {

        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if (dataTermino.isBefore(projeto.getDataInicio())) {
            throw new RuntimeException("Data de término inválida");
        }

        projeto.setStatus(StatusProjeto.CANCELADO);
        projeto.setDataTermino(dataTermino);

        return ProjetoResponseDTO.fromEntity(projeto);
    }

    @Transactional
    public ProjetoResponseDTO adicionarMembro(Long projetoId, Long membroId) {

        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if (projeto.getStatus() == StatusProjeto.ENCERRADO || projeto.getStatus() == StatusProjeto.CANCELADO) {
            throw new RuntimeException("Não é possível adicionar membros em projeto finalizado");
        }

        if (projeto.getMembrosEquipe().size() >= 10) {
            throw new RuntimeException("Número máximo de membros excedido");
        }

        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new RuntimeException("Membro não encontrado"));

        if (projeto.getMembrosEquipe().contains(membro)) {
            throw new RuntimeException("Membro já está no projeto");
        }

        if (membro.getRole() != Role.FUNCIONARIO) {
            throw new RuntimeException("Apenas funcionários podem ser alocados em projetos");
        }

        long projetosAtivos = projetoRepository.findAll().stream()
                .filter(p -> p.getMembrosEquipe().contains(membro) &&
                        p.getStatus() != StatusProjeto.ENCERRADO &&
                        p.getStatus() != StatusProjeto.CANCELADO)
                .count();

        if (projetosAtivos >= 3) {
            throw new RuntimeException("Membro já participa de 3 projetos ativos");
        }

        projeto.getMembrosEquipe().add(membro);

        return ProjetoResponseDTO.fromEntity(projeto);
    }

    public RelatorioPortfolioDTO gerarRelatorio() {

        List<Projeto> projetos = projetoRepository.findAll();

        RelatorioPortfolioDTO relatorio = new RelatorioPortfolioDTO();

        Map<StatusProjeto, Long> quantidadePorStatus = projetos.stream()
                .collect(Collectors.groupingBy(Projeto::getStatus, Collectors.counting()));

        Map<StatusProjeto, BigDecimal> totalOrcadoPorStatus = projetos.stream()
                .collect(Collectors.groupingBy(Projeto::getStatus,
                        Collectors.mapping(Projeto::getOrcamentoTotal,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        double duracaoMediaProjetosEncerrados = projetos.stream()
                .filter(p -> p.getStatus() == StatusProjeto.ENCERRADO)
                .mapToLong(p -> java.time.temporal.ChronoUnit.DAYS.between(p.getDataInicio(), p.getDataTermino()))
                .average()
                .orElse(0.0);

        Long totalMembrosUnicos = projetos.stream()
                .flatMap(p -> p.getMembrosEquipe().stream())
                .distinct()
                .count();

        relatorio.setQuantidadePorStatus(quantidadePorStatus);
        relatorio.setTotalOrcadoPorStatus(totalOrcadoPorStatus);
        relatorio.setDuracaoMediaProjetosEncerrados(duracaoMediaProjetosEncerrados);
        relatorio.setTotalMembrosUnicos(totalMembrosUnicos);

        return relatorio;

    }

}