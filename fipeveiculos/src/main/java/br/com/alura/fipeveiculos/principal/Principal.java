package br.com.alura.fipeveiculos.principal;

import br.com.alura.fipeveiculos.model.Dados;
import br.com.alura.fipeveiculos.model.Modelos;
import br.com.alura.fipeveiculos.model.Veiculo;
import br.com.alura.fipeveiculos.service.ConsumoApi;
import br.com.alura.fipeveiculos.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void exibeMenu() {

        var menu = """
                 \n**** OPÇÕES ****
                Carro
                Moto
                Caminhão

                Digite uma das opções para consultar valores: """;
        System.out.println(menu);

        var tipoVeiculo = leitura.nextLine();
        String endereco;

        if (tipoVeiculo.toLowerCase().contains("car")) {
            endereco = URL_BASE + "carros/marcas/";
        }    else if (tipoVeiculo.toLowerCase().contains("mo")) {
            endereco = URL_BASE + "motos/marcas/";
        }    else {
            endereco = URL_BASE + "caminhoes/marcas/";
        }

        var json = consumo.obterDados(endereco);
        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);
/*
        Recebe o código da marca digitado e efetua uma busca dos modelos de véiculos ordenando pelo código.
*/
        String enderecoBase;
        enderecoBase = endereco;
        json = null;
        while (json == null) {
            System.out.println("\nInforme o código da marca para consulta ou (S) para Encerrar:");
            var codigoMarca = leitura.nextLine();
            if(codigoMarca.equalsIgnoreCase("S")){
                System.out.println("\n*** Aplicação Encerrada ***");
                return;
            } else {
                endereco = endereco.concat(codigoMarca).concat("/modelos/");
                json = consumo.obterDados(endereco);
                if (json == null) {
                    System.out.println("\nCódigo não encontrado.");
                    endereco = enderecoBase;
                } else {
                    break;
                }
            }
        }
        var modeloLista = conversor.obterDados(json, Modelos.class);
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);
/*
        Efetua uma busca do veículo pelo trecho digitado e cria uma nova lista.
*/
        json = null;
        while (json == null) {
            System.out.println("\nDigite um trecho do veículo para consulta ou (S) para Encerrar:");
            var nomeVeiculo = leitura.nextLine();
            if(nomeVeiculo.equalsIgnoreCase("S")){
                System.out.println("\n*** Aplicação Encerrada ***");
                return;
            } else {
                List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                        .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                        .collect(Collectors.toList());
                        long tCount = modelosFiltrados.stream().count();
                if (tCount == 0) {
                    System.out.println("\nNenhum veículo encontrado com o trecho informado.");
                } else {
                    System.out.println("\nModelos Filtrados:\n");
                    modelosFiltrados.forEach(System.out::println);
                    break;
                }
            }
        }
/*
        Efetua uma busca da lista de anos disponíveis para o código do veiculo selecionado.
*/
        enderecoBase = endereco;
        json = null;
        while (json == null) {
            System.out.println("\nDigite o código do modelo para buscar os valores de avaliação:");
            var codigoModelo = leitura.nextLine();
            if(codigoModelo.equalsIgnoreCase("S")){
                System.out.println("\n*** Aplicação Encerrada ***");
                return;
            } else {
                endereco = endereco + codigoModelo + "/anos";
                json = consumo.obterDados(endereco);
                if (json == null) {
                    System.out.println("\nCódigo não encontrado.");
                    endereco = enderecoBase;
                } else {
                    break;
                }
            }
        }
        List<Dados> anos = conversor.obterLista(json, Dados.class);
/*
        Cria uma nova lista de veículos, incrementando com os dados dos anos disponíveis.
*/
        List<Veiculo> veiculos = new ArrayList<>();
		for(int i = 0; i < anos.size(); i++){
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
            }
        System.out.println("\nTodos os veículos filtrados com avaliações por ano: \n");
        veiculos.forEach(System.out::println);

    }
}

/*
     ANOTAÇÕES:
     ==========

     *** Buscando todos os titulos de cada episodio de todas as temporadas ***

     **** FUNÇÃO SEM LAMBDA ****

		for(int i = 0; i < dados.totalTemporadas(); i++){
          List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
            for(int j = 0; j < episodiosTemporada.size(); j++){
                System.out.println(episodiosTemporada.get(j).titulo());
            }
		}

      *** FUNÇÃO USANDO LAMBDA: (parametro) -> expressão ***

      temporadas.forEach(t -> t.episodios().forEach(e ->  System.out.println(e.titulo);

          Leitura do código abaixo:
          temporadas.forEach      ==> Ler todas as temporadas(coleção) forEach(médodo).
          (t -> t.episodios()     ==> Para cada temporada(t), percorrer a lista dos episódios.
          .forEach(e ->  System.out.println(e.titulo) ==> Ler todos os episódios e imprimir o título de cada um.

          *** Essa função peek (espiada), mostra o que a função anterior executou, a forma por traz do lambda.
          .peek(e -> System.out.println("Primeiro filtro(N/A) " +  e))

      *** Imprimindo todas as temporadas usando lambda (ambos estão fazendo a mesma coisa abaixo):
        temporadas.forEach(t -> System.out.println(t));
        temporadas.forEach(System.out::println);

 */
