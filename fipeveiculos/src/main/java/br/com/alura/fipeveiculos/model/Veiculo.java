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
    @NotNull
    private Long id;
    @NotNull
    @JsonAlias("Modelo")
    public String modelo;
    @JsonAlias("AnoModelo")
    public Integer ano;
    @JsonAlias("Valor")
    public String valor;
    @JsonAlias("Combustivel")
    public String combustivel;
    @NotNull
    @Column(name = "CodigoMarca")
    private String codigoMarca;
    @JsonAlias("Marca")
    public String marca;

    @ManyToOne
    private DadosMarca dadosMarca;

    @NotNull
    public Long getId() {
        return id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }

    @NotNull
    public String getModelo() {
        return modelo;
    }

    public void setModelo(@NotNull String modelo) {
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
                ", Modelo = " + modelo +
                ", Ano = " + ano +
                ", Valor = " + valor +
                ", combustivel = " + combustivel +
                ", Marca = " + marca;
    }
}