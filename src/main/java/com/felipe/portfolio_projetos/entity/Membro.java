package com.felipe.portfolio_projetos.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.felipe.portfolio_projetos.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.GenerationType;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Membro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;
    @Enumerated(EnumType.STRING)
    private Role role;
    @ManyToMany(mappedBy = "membrosEquipe")
    @JsonIgnore
    private List<Projeto> projetos;

}
