package br.com.alura.fipeveiculos.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TipoVeiculo(@JsonAlias("codigo") String codigoMarca,
                          @JsonAlias("nome") String nomeMarca){

}
