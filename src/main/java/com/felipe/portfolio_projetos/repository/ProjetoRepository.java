package com.felipe.portfolio_projetos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.felipe.portfolio_projetos.entity.Membro;
import com.felipe.portfolio_projetos.entity.Projeto;
import com.felipe.portfolio_projetos.enums.StatusProjeto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    long countByGerenteProjetoAndStatusNotIn(
            Membro gerente,
            java.util.List<StatusProjeto> status
    );

    Page<Projeto> findByGerenteProjeto(Membro gerente, Pageable pageable);
    Page<Projeto> findByStatus(StatusProjeto status, Pageable pageable);

}
