package com.felipe.portfolio_projetos.enums;

public enum StatusProjeto {
    EM_ANALISE,
    ANALISE_REALIZADA,
    ANALISE_APROVADA,
    INICIADO,
    PLANEJADO,
    EM_ANDAMENTO,
    ENCERRADO,
    CANCELADO;

    public StatusProjeto atualizar() {
        return switch (this) {
            case EM_ANALISE -> ANALISE_REALIZADA;
            case ANALISE_REALIZADA -> ANALISE_APROVADA;
            case ANALISE_APROVADA -> INICIADO;
            case INICIADO -> PLANEJADO;
            case PLANEJADO -> EM_ANDAMENTO;
            case EM_ANDAMENTO -> ENCERRADO;
            case ENCERRADO, CANCELADO ->
                throw new IllegalStateException("Não é possível atualizar o status desse projeto.");
        };
    }

}