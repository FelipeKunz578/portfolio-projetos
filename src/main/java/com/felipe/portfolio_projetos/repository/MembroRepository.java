package com.felipe.portfolio_projetos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.felipe.portfolio_projetos.entity.Membro;

@Repository
public interface MembroRepository extends JpaRepository<Membro, Long> {
   
    boolean existsByNome(String nome);

}
