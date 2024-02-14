package br.com.alura.fipeveiculos.principal;

import br.com.alura.fipeveiculos.model.Dados;
import br.com.alura.fipeveiculos.model.DadosVeiculo;
import br.com.alura.fipeveiculos.service.ConsumoApi;
import br.com.alura.fipeveiculos.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
               
               Digite uma das opções para consultar valores: """;
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

        String jsonFormatado = json.replace("[", "").replace("]", "");
        System.out.println(jsonFormatado);

        //erro... modelos traz cabeçalho tbm junto
        endereco = "https://parallelum.com.br/fipe/api/v1/carros/marcas/59/modelos/5940/anos";
        json = consumo.obterDados(endereco);

        var modelos = conversor.obterLista(json, Dados.class);
        modelos.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);


        System.out.println("Digite um trecho do nome do veículo para consulta:");
        var nomeVeiculo = leitura.nextLine();

        System.out.println("Digite o código do modelo para consultar valores:");
        var codVeiculo = leitura.nextLine();


//      List<DadosVeiculo> veiculos1 = new ArrayList<>();
//
//		for(int i = 1; i <= json.length(); i++){
//            var codigoMarca = leitura.nextLine();
//            endereco = "https://parallelum.com.br/fipe/api/v1/carros/marcas/59/modelos/5940/anos/";
//            endereco = endereco + codigoMarca + "/modelos";
//            json = consumo.obterDados(endereco);
//
//            endereco = "https://parallelum.com.br/fipe/api/v1/carros/marcas/59/modelos/5940/anos/";
//            json = consumo.obterDados(endereco);
//            DadosVeiculo listaVeiculos = conversor.obterDados(json, DadosVeiculo.class);
//            veiculos1.add(listaVeiculos);
//		}
//        veiculos1.forEach(System.out::println);


//
//      ANOTAÇÕES:
//
//      *** Buscando todos os titulos de cada episodio de todas as temporadas ***
//
//     **** FUNÇÃO SEM LAMBDA ****
//
//		for(int i = 0; i < dados.totalTemporadas(); i++){
//          List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for(int j = 0; j < episodiosTemporada.size(); j++){
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//		}
//
//      *** FUNÇÃO USANDO LAMBDA:
//
//      temporadas.forEach(t -> t.episodios().forEach(e ->  System.out.println(e.titulo);
//
//          Leitura do código abaixo:
//          temporadas.forEach      ==> Ler todas as temporadas(coleção) forEach(médodo).
//          (t -> t.episodios()     ==> Para cada temporada(t), percorrer a lista dos episódios.
//          .forEach(e ->  System.out.println(e.titulo) ==> Ler todos os episódios e imprimir o título de cada um.
//
//
//      *** Imprimindo todas as temporadas usando lambda (ambos estão fazendo a mesma coisa abaixo):
//        temporadas.forEach(t -> System.out.println(t));
//        temporadas.forEach(System.out::println);
    }
}
