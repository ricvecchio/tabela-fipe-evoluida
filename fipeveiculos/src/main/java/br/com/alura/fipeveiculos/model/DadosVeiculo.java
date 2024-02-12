package br.com.alura.fipeveiculos.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosVeiculo(@JsonAlias("Valor") String valor,
                            @JsonAlias("Marca") Integer marca,
                            @JsonAlias("Modelo") String modelo,
                           @JsonAlias("AnoModelo") String ano,
                            @JsonAlias("SiglaCombustivel") String combustivel) {
}