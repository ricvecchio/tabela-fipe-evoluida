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
/*
        Recebe o código da marca digitado e efetua uma busca dos modelos de véiculos ordenando pelo código.
*/
        System.out.println("\nInforme o código da marca para consulta:");
        var codigoMarca = leitura.nextLine();
        endereco = endereco + codigoMarca + "/modelos";
        json = consumo.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);
/*
        Efetua uma busca do veículo pelo trecho digitado e cria uma nova lista.
*/
        System.out.println("\nDigite um trecho do nome do veículo para consulta:");
        var nomeVeiculo = leitura.nextLine();
        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());
        System.out.println("\nModelos Filtrados");
        modelosFiltrados.forEach(System.out::println);
/*
        Efetua uma busca da lista de anos disponíveis para o código do veiculo selecionado.
*/
        System.out.println("\nDigite o código do modelo para buscar os valores de avaliação:");
        var codigoModelo = leitura.nextLine();
        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumo.obterDados(endereco);
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
        System.out.println("\nTodos os veículos filtrados com avaliações por ano: ");
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


      *** Imprimindo todas as temporadas usando lambda (ambos estão fazendo a mesma coisa abaixo):
        temporadas.forEach(t -> System.out.println(t));
        temporadas.forEach(System.out::println);

 */
