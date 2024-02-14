package br.com.alura.fipeveiculos.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Dados(String codigo, String nome, List<DadosVeiculo> veiculos) {

    @Override
    public String toString() {
        return "Cód: " + codigo +
                ", Descrição: " + nome ;
    }
}

