package br.com.alura.fipeveiculos.principal;

import br.com.alura.fipeveiculos.model.Dados;
import br.com.alura.fipeveiculos.model.DadosMarca;
import br.com.alura.fipeveiculos.model.DadosVeiculo;
import br.com.alura.fipeveiculos.model.Modelos;
import br.com.alura.fipeveiculos.repository.MarcaRepository;
import br.com.alura.fipeveiculos.service.ConsultaChatGPT;
import br.com.alura.fipeveiculos.service.ConsumoApi;
import br.com.alura.fipeveiculos.service.ConverteDados;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    private MarcaRepository repositorio;

//    private DadosMarca buscaMarca;
//    private Optional<DadosMarca> buscaMarca;

    String endereco;
//    Long id;
    Long idMarca;
    String detalheMarca;
    String nomeSegmento;

    public Principal(MarcaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                     \n**** TABELA FIPE ****
                     
                    1 - Buscar valores de Carros
                    2 - Buscar valores de Motos
                    3 - Buscar valores de Caminhões
                    4 - Listar veículos buscados
                    5 - Buscar veiculos por trecho
                    6 - Buscar veiculos por categoria
                    7 - Buscar informações de um véiculo pelo nome no ChatGPT
                    9 - Buscar Marcas de Veículos Web (fipe.org.br) e Salvar no Banco de Dados 
                    10 - Buscar Marcas de Veículos Salvas
                    11 - Buscar Detalhe da Marca no ChatGPT
                                    
                    0 - Sair                     """;
            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    endereco = URL_BASE + "carros/marcas/";
                    buscarVeiculoWeb();
                    break;
                case 2:
                    endereco = URL_BASE + "motos/marcas/";
                    buscarVeiculoWeb();
                    break;
                case 3:
                    endereco = URL_BASE + "caminhoes/marcas/";
                    buscarVeiculoWeb();
                    break;
                case 7:
                    buscarVeiculoChatGPT();
                    break;
                case 9:
                    buscarMarcasWebESalvarNaTabela();
                    break;
                case 10:
                    consultaDadosMarcasSalvo();
                    break;
                case 11:
                    buscarDetalheMarcaChatGPT();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarDetalheMarcaChatGPT() {
        listarMarcasRepositorio();
        System.out.println("\nDigite o ID da marca do veículo para buscar detalhes: ");
        idMarca = Long.valueOf(leitura.nextLine());

        Optional<DadosMarca> buscaMarca = repositorio.findById(Long.valueOf(idMarca));

        if (buscaMarca.isPresent()) {
            String textoIA = "Ano e país da marca " + buscaMarca.get().getMarca();
            detalheMarca = ConsultaChatGPT.obterDadosIA(textoIA).trim();
            updateDetalheIa_ShouldUpdateDetalheIa();
        } else {
            System.out.println("ID da Marca não localizado!");
        }
    }

    public void listarMarcasRepositorio() {
        List<DadosMarca> marcas = repositorio.findAll();
        marcas.stream()
                .sorted(Comparator.comparing(DadosMarca::getId))
                .forEach(System.out::println);
    }
    public void updateDetalheIa_ShouldUpdateDetalheIa() {
        repositorio.updateDetalheIa(idMarca, detalheMarca);
    }

    private void consultaDadosMarcasSalvo() {
        List<DadosMarca> marcas = repositorio.findAll();
        marcas.stream()
                .sorted(Comparator.comparing(DadosMarca::getId))
                .forEach(System.out::println);
    }

    private void buscarMarcasWebESalvarNaTabela() {
        var segmento = -1;
        while (segmento != 0) {
            var menu = """
                     \n**** Digite a opção do segmento da Marca ****
                     
                    1 - Carros
                    2 - Motos
                    3 - Caminhões
                                    
                    0 - Sair                     """;
            System.out.println(menu);
            segmento = leitura.nextInt();
            leitura.nextLine();
            switch (segmento) {
                case 1:
                    endereco = URL_BASE + "carros/marcas/";
                    nomeSegmento = "Carros";
                    listarMarcasWeb();
                    break;
                case 2:
                    endereco = URL_BASE + "motos/marcas/";
                    nomeSegmento = "Motos";
                    listarMarcasWeb();
                    break;
                case 3:
                    endereco = URL_BASE + "caminhoes/marcas/";
                    nomeSegmento = "Caminhões";
                    listarMarcasWeb();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void listarMarcasWeb() {
        System.out.println("\nLista das marcas de veículos salvas no banco de dados: \n");
        var json = consumo.obterDados(endereco);
        List<DadosMarca> marcas = conversor.obterLista(json, DadosMarca.class);
        for (DadosMarca listaMarcas : marcas) {
            try {
                listaMarcas.setSegmento(nomeSegmento);
                System.out.println(listaMarcas);
                repositorio.save(listaMarcas);
            } catch (DataIntegrityViolationException ex) {
                System.out.println("\nA marca " + listaMarcas.getMarca() + " já existe no banco de dados.");
            }
        }
    }


    private void buscarVeiculoWeb() {
        var json = consumo.obterDados(endereco);
        var marcas = conversor.obterLista(json, Dados.class);
        System.out.println("Teste funcionando = " + marcas);
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
            if (codigoMarca.equalsIgnoreCase("S")) {
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
            if (nomeVeiculo.equalsIgnoreCase("S")) {
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
            if (codigoModelo.equalsIgnoreCase("S")) {
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
        List<DadosVeiculo> veiculos = new ArrayList<>();
        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            DadosVeiculo veiculo = conversor.obterDados(json, DadosVeiculo.class);
            veiculos.add(veiculo);
        }
        System.out.println("\nTodos os veículos filtrados com avaliações por ano: \n");
        veiculos.forEach(System.out::println);
    }

    private void buscarVeiculoChatGPT() {
        System.out.println("Digite o nome do veículo para buscar as informações na IA: ");
        var veiculo = leitura.nextLine();
        String textoIA = "Em um único paragrafo fale da Marca de veículo: " + veiculo;
        var infoVeiculoIA = ConsultaChatGPT.obterDadosIA(textoIA).trim();
        System.out.println(infoVeiculoIA);
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
