package br.com.alura.fipeveiculos.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DadosMarca {
    private String codigo;
    @JsonAlias("nome") private String marca;
    private String segmento;
    private Integer anoFundada;
    private String detalheIA;

//    public DadosMarca(Dados dados){
//        this.codigo = dados.codigo();
//        this.marca = dados.nome();
//        this.segmento = "carro";
//        this.anoFundada = 1985;
//        this.detalheIA = "Data retornada da IA";
//    }

//    public DadosMarca(List<DadosMarca> d) {
//    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMarca() {
        return marca;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public Integer getAnoFundada() {
        return anoFundada;
    }

    public void setAnoFundada(Integer anoFundada) {
        this.anoFundada = anoFundada;
    }

    public String getDetalheIA() {
        return detalheIA;
    }

    public void setDetalheIA(String detalheIA) {
        this.detalheIA = detalheIA;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    @Override
    public String toString() {
        return "Código = " + codigo +
                ", Marca = " + marca +
                ", Segmento = " + segmento +
                ", Ano = " + anoFundada +
                ", Detalhe = " + detalheIA;
    }

}


