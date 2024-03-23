package br.com.alura.fipeveiculos.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "veiculos")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Veiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "CodigoModelo")
    @JsonAlias("codigo")
    private String codigoModelo;
    @NotNull
    @JsonAlias("nome")
    public String modelo;
    @JsonAlias("AnoModelo")
    public Integer ano;
    @JsonAlias("Valor")
    public String valor;
    @JsonAlias("Combustivel")
    public String combustivel;
    @JsonAlias("Segmento")
    public String segmento;
    @Column(name = "CodigoMarca")
    private String codigoMarca;
    @JsonAlias("Marca")
    public String marca;

    @ManyToOne
    private DadosMarca dadosMarca;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoModelo() {
        return codigoModelo;
    }

    public void setCodigoModelo(String codigoModelo) {
        this.codigoModelo = codigoModelo;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getCombustivel() {
        return combustivel;
    }

    public void setCombustivel(String combustivel) {
        this.combustivel = combustivel;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public String getCodigoMarca() {
        return codigoMarca;
    }
    public void setCodigoMarca(String codigoMarca) {
        this.codigoMarca = codigoMarca;
    }
    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public DadosMarca getDadosMarca() {
        return dadosMarca;
    }

    public void setDadosMarca(DadosMarca dadosMarca) {
        this.dadosMarca = dadosMarca;
    }

    @Override
    public String toString() {
        return "ID = " + id +
                ", Código Modelo = " + codigoModelo +
                ", Modelo = " + modelo +
                ", Ano = " + ano +
                ", Valor = " + valor +
                ", combustivel = " + combustivel +
                ", segmento = " + segmento +
                ", Código Marca = " + codigoMarca +
                ", Marca = " + marca;
    }
}