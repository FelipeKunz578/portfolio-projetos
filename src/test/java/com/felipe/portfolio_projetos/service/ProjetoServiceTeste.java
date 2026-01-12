package com.felipe.portfolio_projetos.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.InjectMocks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import com.felipe.portfolio_projetos.repository.MembroRepository;
import com.felipe.portfolio_projetos.repository.ProjetoRepository;
import com.felipe.portfolio_projetos.entity.Membro;
import com.felipe.portfolio_projetos.entity.Projeto;
import com.felipe.portfolio_projetos.enums.RiscoProjeto;
import com.felipe.portfolio_projetos.enums.Role;
import com.felipe.portfolio_projetos.enums.StatusProjeto;
import com.felipe.portfolio_projetos.dto.ProjetoRequestDTO;
import com.felipe.portfolio_projetos.dto.ProjetoResponseDTO;

@ExtendWith(MockitoExtension.class)
public class ProjetoServiceTeste {

    @Mock
    ProjetoRepository projetoRepository;

    @Mock
    MembroRepository membroRepository;

    @InjectMocks
    ProjetoService projetoService;

    @Test
    void testCriarProjeto_Success() {
        Long gerenteId = 1L;
        Membro gerente = new Membro();
        gerente.setId(gerenteId);
        gerente.setRole(Role.GERENTE);

        ProjetoRequestDTO request = new ProjetoRequestDTO();
        request.setNome("Projeto Teste");
        request.setDescricao("Descrição teste");
        request.setDataInicio(LocalDate.now());
        request.setDataPrevisaoTermino(LocalDate.now().plusMonths(2));
        request.setOrcamentoTotal(BigDecimal.valueOf(50000));
        request.setGerenteProjetoId(gerenteId);

        Projeto projetoSalvo = new Projeto();
        projetoSalvo.setId(1L);
        projetoSalvo.setNome(request.getNome());
        projetoSalvo.setDescricao(request.getDescricao());
        projetoSalvo.setDataInicio(request.getDataInicio());
        projetoSalvo.setDataPrevisaoTermino(request.getDataPrevisaoTermino());
        projetoSalvo.setOrcamentoTotal(request.getOrcamentoTotal());
        projetoSalvo.setGerenteProjeto(gerente);
        projetoSalvo.setRisco(RiscoProjeto.BAIXO);
        projetoSalvo.setStatus(StatusProjeto.PLANEJADO);
        projetoSalvo.setMembrosEquipe(new ArrayList<>());

        when(membroRepository.findById(gerenteId)).thenReturn(Optional.of(gerente));
        when(projetoRepository.countByGerenteProjetoAndStatusNotIn(eq(gerente), anyList())).thenReturn(0L);
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projetoSalvo);

        ProjetoResponseDTO response = projetoService.criarProjeto(request);

        assertNotNull(response);
        assertEquals("Projeto Teste", response.getNome());
        verify(membroRepository).findById(gerenteId);
        verify(projetoRepository).save(any(Projeto.class));
    }

    @Test
    void testCriarProjeto_GerenteNaoEncontrado() {
        Long gerenteId = 1L;
        ProjetoRequestDTO request = new ProjetoRequestDTO();
        request.setGerenteProjetoId(gerenteId);

        when(membroRepository.findById(gerenteId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projetoService.criarProjeto(request);
        });
        assertEquals("Gerente não encontrado", exception.getMessage());
    }

    @Test
    void testCriarProjeto_MembroNaoGerente() {
        Long gerenteId = 1L;
        Membro membro = new Membro();
        membro.setId(gerenteId);
        membro.setRole(Role.FUNCIONARIO);

        ProjetoRequestDTO request = new ProjetoRequestDTO();
        request.setGerenteProjetoId(gerenteId);

        when(membroRepository.findById(gerenteId)).thenReturn(Optional.of(membro));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projetoService.criarProjeto(request);
        });
        assertEquals("Membro não é um gerente", exception.getMessage());
    }

    @Test
    void testListarProjetos() {
        List<Projeto> projetos = new ArrayList<>();
        Projeto projeto = new Projeto();
        projeto.setId(1L);
        projeto.setNome("Projeto 1");
        projetos.add(projeto);

        when(projetoRepository.findAll()).thenReturn(projetos);

        List<ProjetoResponseDTO> response = projetoService.listarProjetos();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Projeto 1", response.get(0).getNome());
        verify(projetoRepository).findAll();
    }

    @Test
    void testBuscarProjeto_Success() {
        Long projetoId = 1L;
        Projeto projeto = new Projeto();
        projeto.setId(projetoId);
        projeto.setNome("Projeto Teste");

        when(projetoRepository.findById(projetoId)).thenReturn(Optional.of(projeto));

        ProjetoResponseDTO response = projetoService.buscarProjeto(projetoId);

        assertNotNull(response);
        assertEquals("Projeto Teste", response.getNome());
        verify(projetoRepository).findById(projetoId);
    }

    @Test
    void testBuscarProjeto_NaoEncontrado() {
        Long projetoId = 1L;
        when(projetoRepository.findById(projetoId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projetoService.buscarProjeto(projetoId);
        });
        assertEquals("Projeto não encontrado", exception.getMessage());
    }

    @Test
    void testCriarProjeto_GerenteComTresProjetosAtivos() {
        Long gerenteId = 1L;

        Membro gerente = new Membro();
        gerente.setId(gerenteId);
        gerente.setRole(Role.GERENTE);

        ProjetoRequestDTO request = new ProjetoRequestDTO();
        request.setNome("Projeto Teste");
        request.setDataInicio(LocalDate.now());
        request.setDataPrevisaoTermino(LocalDate.now().plusMonths(2));
        request.setOrcamentoTotal(BigDecimal.valueOf(50000));
        request.setGerenteProjetoId(gerenteId);

        when(membroRepository.findById(gerenteId))
                .thenReturn(Optional.of(gerente));

        when(projetoRepository.countByGerenteProjetoAndStatusNotIn(
                eq(gerente),
                anyList())).thenReturn(3L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> projetoService.criarProjeto(request));

        assertEquals("Gerente já possui 3 projetos ativos", exception.getMessage());
    }

    @Test
    void testCriarProjeto_DataPrevisaoAntesDaDataInicio() {
        Long gerenteId = 1L;

        Membro gerente = new Membro();
        gerente.setId(gerenteId);
        gerente.setRole(Role.GERENTE);

        ProjetoRequestDTO request = new ProjetoRequestDTO();
        request.setNome("Projeto Inválido");
        request.setDataInicio(LocalDate.now());
        request.setDataPrevisaoTermino(LocalDate.now().minusDays(1));
        request.setOrcamentoTotal(BigDecimal.valueOf(50000));
        request.setGerenteProjetoId(gerenteId);

        when(membroRepository.findById(gerenteId))
                .thenReturn(Optional.of(gerente));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> projetoService.criarProjeto(request));

        assertEquals(
                "Data de previsão de término não pode ser anterior à data de início",
                exception.getMessage());
    }

    @Test
    void testCriarProjeto_RiscoAltoPorOrcamento() {
        Long gerenteId = 1L;

        Membro gerente = new Membro();
        gerente.setId(gerenteId);
        gerente.setRole(Role.GERENTE);

        ProjetoRequestDTO request = new ProjetoRequestDTO();
        request.setNome("Projeto Caro");
        request.setDescricao("Teste risco alto");
        request.setDataInicio(LocalDate.now());
        request.setDataPrevisaoTermino(LocalDate.now().plusMonths(2));
        request.setOrcamentoTotal(BigDecimal.valueOf(600_000));
        request.setGerenteProjetoId(gerenteId);

        Projeto projetoSalvo = new Projeto();
        projetoSalvo.setId(1L);
        projetoSalvo.setRisco(RiscoProjeto.ALTO);

        when(membroRepository.findById(gerenteId))
                .thenReturn(Optional.of(gerente));

        when(projetoRepository.countByGerenteProjetoAndStatusNotIn(
                eq(gerente),
                anyList())).thenReturn(0L);

        when(projetoRepository.save(any(Projeto.class)))
                .thenReturn(projetoSalvo);

        ProjetoResponseDTO response = projetoService.criarProjeto(request);

        assertEquals(RiscoProjeto.ALTO, response.getRisco());
    }

}
