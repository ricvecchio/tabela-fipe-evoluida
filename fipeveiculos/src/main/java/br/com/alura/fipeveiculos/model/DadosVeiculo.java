package br.com.alura.fipeveiculos.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosVeiculo(@JsonAlias("AnoModelo") Integer ano,
                      @JsonAlias("Valor") String valor,
                      @JsonAlias("Modelo") String modelo,
                      @JsonAlias("Combustivel") String combustivel,
                      @JsonAlias("Marca") String marca) {
}