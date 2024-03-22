package br.com.alura.fipeveiculos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DadosModelos {

    protected String modelo;

    private List<DadosSite> modelos;

    public List<DadosSite> getModelos() {
        return modelos;
    }

    public void setModelos(List<DadosSite> modelos) {
        this.modelos = modelos;
    }

}
