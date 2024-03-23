package br.com.alura.fipeveiculos.principal;

import br.com.alura.fipeveiculos.model.*;
import br.com.alura.fipeveiculos.repository.MarcaRepository;
import br.com.alura.fipeveiculos.service.ConsultaChatGPT;
import br.com.alura.fipeveiculos.service.ConsumoApi;
import br.com.alura.fipeveiculos.service.ConverteDados;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private MarcaRepository repositorio;

    private List<DadosMarca> marcas = new ArrayList<>();
    private List<Veiculo> veiculos = new ArrayList<>();

    String endereco;
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
                     
                     1 - Buscar valores de carros, motos ou caminhões no site (fipe.org.br) =>>> OK
                     2 - Buscar informações de um veículo pelo nome no ChatGPT ==============>>> OK
                     3 - Buscar marcas no site (fipe.org.br) e salvar no banco de dados =====>>> OK
                     4 - Listar marcas e veículos do banco de dados (Marcas/Veículos) =======>>> OK
                     5 - Buscar detalhe da marca no ChatGPT e atualizar no banco de dados ===>>> OK
                     6 - Buscar veículos no site pela marca e salvar no banco de dados ======>>> OK
                     7 - Buscar veiculos por trecho
                     8 - Buscar veiculos por categoria
                     9 - 
                    10 - 
                    99 - Deletar banco de dados  
                                    
                    0 - Sair                     """;
            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarVeiculoWeb();
                    break;
                case 2:
                    buscarVeiculoChatGPT();
                    break;
                case 3:
                    buscarMarcasWebESalvarNaTabela();
                    break;
                case 4:
                    consultaDadosMarcasSalvo();
                    break;
                case 5:
                    buscarDetalheMarcaChatGPT();
                    break;
                case 6:
                    buscarVeiculosWebPorMarca();
                    break;
                case 7:
                    break;
                case 99:
                    limparBancoDeDados();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarVeiculosWebPorMarca() {
        consultaDadosMarcasSalvo();
        List<DadosMarca> marcas = repositorio.findAll();
        marcas.stream()
                .sorted(Comparator.comparing(DadosMarca::getMarca))
                .forEach(System.out::println);

        String codigoMarca = null;
        String segmento = null;
        String enderecoBase;
        enderecoBase = endereco;
        String json = null;
        while (json == null) {
            System.out.println("\nEscolha uma marca pelo código:");
            codigoMarca = leitura.nextLine();

            String finalCodigoMarca = codigoMarca;
            var veiculoFiltrado = marcas.stream()
                    .filter(m -> m.getCodigo().equalsIgnoreCase(finalCodigoMarca))
                    .sorted(Comparator.comparing(DadosMarca::getMarca))
                            .collect(Collectors.toList());

            segmento = veiculoFiltrado.get(0).getSegmento().toLowerCase();
            endereco = URL_BASE + segmento + "/marcas/" + codigoMarca + "/modelos/";

            json = consumo.obterDados(endereco);
            if (json == null) {
                System.out.println("\nCódigo não encontrado.");
                endereco = enderecoBase;
            } else {
                break;
            }
        }

        var modeloLista = conversor.obterDados(json, Modelos.class);
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(DadosSite::nome))
                .forEach(System.out::println);

        Optional<DadosMarca> buscaMarca = repositorio.findByCodigo(codigoMarca);
        var nomeMarca = buscaMarca.get().getMarca();

        if (buscaMarca.isPresent()) {
            var marcaEncontrada = buscaMarca.get();
            var quantidadeLista = modeloLista.modelos().stream().count();

            List<Veiculo> listaVeiculos = new ArrayList<>();
            for (int i = 0; i < quantidadeLista; i++) {

                var codigoVeiculo = modeloLista.modelos().stream().map(DadosSite::codigo).collect(Collectors.toList());
                var nomeVeiculo = modeloLista.modelos().stream().map(DadosSite::nome).collect(Collectors.toList());

                Veiculo dadosVeiculo = new Veiculo();
                dadosVeiculo.setCodigoModelo(codigoVeiculo.get(i));
                dadosVeiculo.setModelo(nomeVeiculo.get(i));
                dadosVeiculo.setCodigoMarca(codigoMarca);
                dadosVeiculo.setMarca(nomeMarca);
                dadosVeiculo.setSegmento(segmento);
                listaVeiculos.add(dadosVeiculo);

                marcaEncontrada.setVeiculos(listaVeiculos);
                repositorio.save(marcaEncontrada);
            }
        }
    }

    public void updateDetalheIa() {
        repositorio.updateDetalheIa(idMarca, detalheMarca);
    }

    private void listarESalvarMarcasWeb() {
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
        exibeMenuSegmento();
        var json = consumo.obterDados(endereco);
        var marcas = conversor.obterLista(json, DadosSite.class);
        marcas.stream()
                .sorted(Comparator.comparing(DadosSite::nome))
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
                .sorted(Comparator.comparing(DadosSite::nome))
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
                List<DadosSite> modelosFiltrados = modeloLista.modelos().stream()
                        .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                        .sorted(Comparator.comparing(DadosSite::nome))
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
        List<DadosSite> anos = conversor.obterLista(json, DadosSite.class);
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

    private void buscarMarcasWebESalvarNaTabela() {
        exibeMenuSegmento();
        listarESalvarMarcasWeb();
    }

    private void buscarDetalheMarcaChatGPT() {
        consultaDadosMarcasSalvo();
        System.out.println("\nDigite o ID da marca do veículo para buscar detalhes: ");
        idMarca = Long.valueOf(leitura.nextLine());

        Optional<DadosMarca> buscaMarca = repositorio.findById(Long.valueOf(idMarca));

        if (buscaMarca.isPresent()) {
            String textoIA = "Ano e país da marca " + buscaMarca.get().getMarca();
            detalheMarca = ConsultaChatGPT.obterDadosIA(textoIA).trim();
            updateDetalheIa();
        } else {
            System.out.println("ID da Marca não localizado!");
        }
    }

    private void consultaDadosMarcasSalvo() {
        marcas = repositorio.findAll();
        marcas.stream()
                .sorted(Comparator.comparing(DadosMarca::getMarca))
                .forEach(System.out::println);

        if (marcas.isEmpty() == true) {
            System.out.println("Não existe registro no banco de dados Marcas!");
            exibeMenu();
        }
    }

    private void exibeMenuSegmento() {
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
                    return;
                case 2:
                    endereco = URL_BASE + "motos/marcas/";
                    nomeSegmento = "Motos";
                    return;
                case 3:
                    endereco = URL_BASE + "caminhoes/marcas/";
                    nomeSegmento = "Caminhões";
                    return;
                case 0:
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida");
                    break;
            }
        }
    }

    private void limparBancoDeDados() {
        var opcaoDelete = -1;
        while (opcaoDelete != 0) {
            var menu = """
                     \n**** Digite a Tabela para Limpeza ****
                     
                    1 - Marcas
                    2 - Veículos
                    3 - Ambas
                                    
                    0 - Sair                     """;
            System.out.println(menu);
            opcaoDelete = leitura.nextInt();
            leitura.nextLine();
            switch (opcaoDelete) {
                case 1:
                    repositorio.deleteDadosMarcaFull();
                    return;
                case 2:
                    repositorio.deleteVeiculoFull();
                    return;
                case 3:
                    repositorio.deleteDadosMarcaFull();
                    repositorio.deleteVeiculoFull();
                    return;
                case 0:
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida");
                    break;
            }
        }
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


      *** EXEMPLOS DE FUNÇÕES .STREAM *** (FILTRA POR VALOR CRIANDO NOVA LISTA (MAP) E TRAZENDO SOMENTE O PRIMEIRO)

                var codigoVeiculo =  modeloLista.modelos().stream()
                        .filter(m -> m.codigo().equals("9680"))
                        .map(m -> m.codigo())
                        .findFirst();

                var nomeVeiculo =  modeloLista.modelos().stream()
                        .filter(m -> m.codigo().equals("9680"))
                        .map(m -> m.nome())
                        .findFirst();

 */
