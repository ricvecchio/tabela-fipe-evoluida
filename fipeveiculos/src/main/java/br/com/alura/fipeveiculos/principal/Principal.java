package br.com.alura.fipeveiculos.principal;

import br.com.alura.fipeveiculos.model.Dados;
import br.com.alura.fipeveiculos.service.ConsumoApi;
import br.com.alura.fipeveiculos.service.ConverteDados;

import java.util.Comparator;
import java.util.Scanner;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu() {
        var menu = """
               **** OPÇÕES ****
               Carro 
               Moto
               Caminhão
               
               Digite uma das opções para consultar valores:
                """;
        System.out.println(menu);
        var tipoVeiculo = leitura.nextLine();
        String endereco;

        if (tipoVeiculo.toLowerCase().contains("carr")) {
            endereco = URL_BASE + "carros/marcas/";
        }    else if (tipoVeiculo.toLowerCase().contains("mot")) {
            endereco = URL_BASE + "motos/marcas/";
        }    else {
            endereco = URL_BASE + "caminhoes/marcas/";
        }

        var json = consumo.obterDados(endereco);
        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Informe o código da marca para consulta:");
        var codigoMarca = leitura.nextLine();
        endereco = endereco + codigoMarca + "/modelos";
        json = consumo.obterDados(endereco);

        //ERRO O CODIGO NAO MODELOS NAO EH STRING... SIM NUMERICO
        var modelos = conversor.obterLista(json, Dados.class);
        modelos.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);


//		for(int i = 1; i <= jsonFormatado.length(); i++){
//			DadosMarcas listaMarcas = conversor.obterDados(jsonFormatado, DadosMarcas.class);
//			System.out.println(listaMarcas);
//		}
    }
}
