package br.com.alura.fipeveiculos.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "veiculos")
public class Veiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonAlias("Marca")
    String marca;
//    @Column(unique = true)
    @JsonAlias("Modelo")
    String modelo;
    @JsonAlias("AnoModelo")
    Integer ano;
    @JsonAlias("Combustivel")
    String combustivel;
    @JsonAlias("Valor")
    String valor;

}