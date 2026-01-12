package com.felipe.portfolio_projetos.controller;

import org.springframework.web.bind.annotation.RestController;

import com.felipe.portfolio_projetos.repository.MembroRepository;
import com.felipe.portfolio_projetos.entity.Membro;
import com.felipe.portfolio_projetos.dto.MembroRequestDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Membro Mock Controller", description = "Endpoints para simular a criação e consulta de membros (Mock).")
@RestController
@RequestMapping("/api/membros-mock")
public class MembroMockController {
    
    private final MembroRepository membroRepository;

    public MembroMockController(MembroRepository membroRepository){
        this.membroRepository = membroRepository;
    }

    @Operation(summary = "Criar Membro Mock", description = "Cria um membro mock para testes. SÓ É POSSÍVEL CRIAR MEMBROS COM NOMES ÚNICOS, E COM CARGO GERENTE OU FUNCIONARIO")
    @PostMapping("/criar")
    public ResponseEntity<Membro> criarMembro(@RequestBody MembroRequestDTO dto) {

        if (membroRepository.existsByNome(dto.getNome())) {
        throw new RuntimeException("Já existe um membro com esse nome");
    }

        Membro membro = new Membro();
        membro.setNome(dto.getNome());
        membro.setRole(dto.getRole());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membroRepository.save(membro));
    }

    @Operation(summary = "Buscar Membro Mock", description = "Busca um membro mock por ID.")
    @GetMapping("/buscar/{id}")
    public Membro buscarMembro(@PathVariable Long id) {
        return membroRepository.findById(id).orElseThrow(() -> new RuntimeException("Membro não encontrado"));
    }

    @Operation(summary = "Listar Membros Mock", description = "Lista todos os membros mock.")
    @GetMapping("/listar")
    public List<Membro> listarMembros() {
        return membroRepository.findAll();
    }
    
}