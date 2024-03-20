package br.com.alura.fipeveiculos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSite(String codigo, String nome) {

}

