package br.com.alura.fipeveiculos.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
//public record DadosMarca(@JsonAlias("codigo") String codigoMarca,
//                         @JsonAlias("nome") String nomeMarca) {
//}

public record DadosMarca(@JsonAlias("Title") String codigoMarca,
                         @JsonAlias("Year") String nomeMarca) {
}