package com.felipe.portfolio_projetos.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.GenerationType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.felipe.portfolio_projetos.enums.StatusProjeto;
import com.felipe.portfolio_projetos.enums.RiscoProjeto;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Projeto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false)
    private LocalDate dataInicio;
    @Column(nullable = false)
    private LocalDate dataPrevisaoTermino;
    private LocalDate dataTermino;
    @Column(nullable = false)
    private BigDecimal orcamentoTotal;
    private String descricao;
    @ManyToOne(optional = false)
    private Membro gerenteProjeto;
    @Enumerated(EnumType.STRING)
    private StatusProjeto status;
    @Enumerated(EnumType.STRING)
    private RiscoProjeto risco;
    @ManyToMany
    @JoinTable(
        name = "projeto_membro",
        joinColumns = @JoinColumn(name = "projeto_id"),
        inverseJoinColumns = @JoinColumn(name = "membro_id")
    )
    private List<Membro> membrosEquipe = new ArrayList<>();

}
