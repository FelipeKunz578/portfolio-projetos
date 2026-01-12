package com.felipe.portfolio_projetos.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "DTO para informar a data de término do projeto ao cancelar ou encerrar.")
@Data
public class DataTerminoProjetoDTO {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataTermino;
}
