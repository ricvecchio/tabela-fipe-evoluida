package br.com.alura.fipeveiculos.principal;

import br.com.alura.fipeveiculos.model.*;
import br.com.alura.fipeveiculos.repository.MarcaRepository;
import br.com.alura.fipeveiculos.service.ConsultaChatGPT;
import br.com.alura.fipeveiculos.service.ConsumoApi;
import br.com.alura.fipeveiculos.service.ConverteDados;
import ch.qos.logback.core.joran.spi.ElementSelector;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private Optional<DadosMarca> marcaBusca;
    private Modelos modeloLista;
    private DadosMarca marcaEncontrada;

    int opcao = -1;
    String json = null;
    String endereco;
    String enderecoBase;
    Long idMarca;
    String codigoMarca;
    String nomeMarca;
    String detalheMarca;
    String nomeSegmento;
    String codigoModelo;
    String veiculoEncontrado;


    public Principal(MarcaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        while (opcao != 0) {
            var menu = """
                     \n**** TABELA FIPE ****
                     
                     1 - Buscar valores de carros, motos ou caminhões no site (fipe.org.br) 
                     2 - Buscar informações de um veículo pelo nome no ChatGPT 
                     3 - Buscar marcas no site (fipe.org.br) e salvar no banco de dados 
                     4 - Listar marcas e veículos do banco de dados (Marcas/Veículos) 
                     5 - Buscar detalhe da marca no ChatGPT e atualizar no banco de dados
                     6 - Buscar veículos no site pela marca e salvar no banco de dados
                     7 - Buscar marca pelo nome 
                     8 - Buscar veículos pelo nome ou trecho 
                     9 - Buscar marca e filtrar pelo segmento (carros/motos/caminhoes)
                    10 - Buscar veículos pelo valor da tabela fipe
                    11 - Buscar veículos a partir de uma data
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
                    buscarMarcaPorNome();
                    break;
                case 8:
                    buscarVeiculoPorTrechoNome();
                    break;
                case 9:
                    buscarMarcaPorSegmento();
                    break;
                case 10:
                    buscarVeiculoPorValor();
                    break;
                case 11:
                    buscarVeiculosAposUmaData();
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

        enderecoBase = endereco;
        while (json == null) {
            System.out.println("\nEscolha uma marca pelo código:");
            codigoMarca = leitura.nextLine();

            String finalCodigoMarca = codigoMarca;
            var veiculoFiltrado = marcas.stream()
                    .filter(m -> m.getCodigo().equalsIgnoreCase(finalCodigoMarca))
                    .sorted(Comparator.comparing(DadosMarca::getMarca))
                    .collect(Collectors.toList());

            nomeSegmento = veiculoFiltrado.get(0).getSegmento().toLowerCase();
            endereco = URL_BASE + nomeSegmento + "/marcas/" + codigoMarca + "/modelos/";

            json = consumo.obterDados(endereco);
            if (json == null) {
                System.out.println("\nCódigo não encontrado.");
                endereco = enderecoBase;
            } else {
                break;
            }
        }

        modeloLista = conversor.obterDados(json, Modelos.class);
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(DadosSite::nome))
                .forEach(System.out::println);

        Optional<DadosMarca> buscaMarca = repositorio.findByCodigo(codigoMarca);
        if (buscaMarca.isPresent()) {
            nomeMarca = buscaMarca.get().getMarca();
            marcaEncontrada = buscaMarca.get();
            insereVeiculosBancoDeDados();
        }
    }

    public void insereVeiculosBancoDeDados() {

        var quantidadeVeiculos = modeloLista.modelos().stream().count();
        List<Veiculo> listaVeiculos = new ArrayList<>();

        var codigoVeiculo = modeloLista.modelos().stream().map(DadosSite::codigo).collect(Collectors.toList());
        var nomeVeiculo = modeloLista.modelos().stream().map(DadosSite::nome).collect(Collectors.toList());

        for (int i = 0; i < quantidadeVeiculos; i++) {
            List<Veiculo> buscaVeiculo = repositorio.veiculosPorCodigo(codigoVeiculo.get(i));

            if (buscaVeiculo.isEmpty() == true) {

                Veiculo dadosVeiculo = new Veiculo();
                dadosVeiculo.setCodigoModelo(codigoVeiculo.get(i));
                dadosVeiculo.setModelo(nomeVeiculo.get(i));
                dadosVeiculo.setCodigoMarca(codigoMarca);
                dadosVeiculo.setMarca(nomeMarca);
                dadosVeiculo.setSegmento(nomeSegmento);

                listaVeiculos.add(dadosVeiculo);
                marcaEncontrada.setVeiculos(listaVeiculos);
                repositorio.save(marcaEncontrada);
            }
        }
    }

    public void updateDetalheIa() {
        repositorio.updateDetalheIa(idMarca, detalheMarca);
    }

    private void listarMarcasWebESalvar() {
        System.out.println("\nListando Marcas de veículos do site (fipe.org) e salvando no banco de dados: \n");
        var json = consumo.obterDados(endereco);

        List<DadosMarca> marcas = conversor.obterLista(json, DadosMarca.class);

        for (DadosMarca listaMarcas : marcas) {
            Optional<DadosMarca> buscaMarca = repositorio.findByCodigo(listaMarcas.getCodigo());
            System.out.println("Código: " + listaMarcas.getCodigo() + " - Marca: " + listaMarcas.getMarca());
            if (buscaMarca.isPresent()) {
                continue;
            } else {
                listaMarcas.setSegmento(nomeSegmento);
                repositorio.save(listaMarcas);
            }
        }
    }

    private void buscarVeiculoWeb() {
        exibeMenuSegmento();
        listarMarcasWebESalvar();
/*
        Recebe o código da marca digitado e efetua uma busca dos modelos de véiculos ordenando pelo código.
*/
        enderecoBase = endereco;
        json = null;
        while (json == null) {
            System.out.println("\nInforme o código da marca para consulta ou (S) para Encerrar:");
            codigoMarca = leitura.nextLine();
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
        modeloLista = conversor.obterDados(json, Modelos.class);
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(DadosSite::nome))
                .forEach(System.out::println);

        Optional<DadosMarca> buscaMarca = repositorio.findByCodigo(codigoMarca);
        if (buscaMarca.isPresent()) {
            nomeMarca = buscaMarca.get().getMarca();
            marcaEncontrada = buscaMarca.get();
            montaUrlEnderecoAnos();
            buscarValoresAnosFipeEIncluiOuAtualizaTabela();
        }
    }

    private void montaUrlEnderecoAnos() {
        enderecoBase = endereco;
        json = null;
        while (json == null) {
            System.out.println("\nDigite o código do modelo para buscar os valores Fipe:");
            codigoModelo = leitura.nextLine();
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
    }

    private void buscarValoresAnosFipeEIncluiOuAtualizaTabela() {

        List<DadosSite> anos = conversor.obterLista(json, DadosSite.class);
        List<DadosVeiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {

            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);

            DadosVeiculo veiculo = conversor.obterDados(json, DadosVeiculo.class);
            veiculos.add(veiculo);
        }

        List<Veiculo> listaVeiculos = new ArrayList<>();

        for (int y = 0; y < veiculos.size(); y++) {

            Veiculo dadosVeiculo = new Veiculo();
            dadosVeiculo.setCodigoModelo(codigoModelo);
            dadosVeiculo.setCodigoMarca(codigoMarca);
            dadosVeiculo.setMarca(nomeMarca);
            dadosVeiculo.setSegmento(nomeSegmento);
            dadosVeiculo.setModelo(veiculos.get(y).modelo());
            dadosVeiculo.setAno(veiculos.get(y).ano());
            dadosVeiculo.setValor(veiculos.get(y).valor());
            dadosVeiculo.setCombustivel(veiculos.get(y).combustivel());

            Date dataHoraSistema = new Date();
            String formatoDataHora = "dd/MM/yyyy a hh:mm:ss";
            SimpleDateFormat dataHoraFormatada = new SimpleDateFormat(formatoDataHora);
            dadosVeiculo.setDataAtualizacao(dataHoraFormatada.format(dataHoraSistema));

            listaVeiculos.add(dadosVeiculo);

            Long veiculoEncontrado = repositorio.veiculosPorCodigoEAno(codigoModelo, veiculos.get(y).ano());

            if (veiculoEncontrado > 0) {
                repositorio.atualizaDadosVeiculo(codigoModelo, veiculos.get(y).ano(), veiculos.get(y).valor(), dadosVeiculo.getDataAtualizacao());
            } else {
                marcaEncontrada.setVeiculos(listaVeiculos);
                repositorio.save(marcaEncontrada);
            }
        }
        System.out.println("\nTodos os veículos filtrados com avaliações por ano: \n");
        listaVeiculos.forEach(v ->
                System.out.printf("Ano=%s, Valor=%s, Código=%s, Modelo=%s, Combustível=%s, Marca: %s\n",
                        v.getAno(), v.getValor(), v.getCodigoModelo(), v.getModelo(), v.combustivel, v.marca));
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
        listarMarcasWebESalvar();
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
            System.out.println("Não existe registro no banco de dados!");
            exibeMenu();
        }
    }

    private void buscarMarcaPorNome() {
        System.out.println("Escolha uma marca pelo nome: ");
        var nomeMarca = leitura.nextLine();
        marcaBusca = repositorio.findTop1ByMarcaContainingIgnoreCase(nomeMarca);

        if (marcaBusca.isPresent()) {
            System.out.println("Dados da marca: " + marcaBusca.get());
        } else {
            System.out.println("Marca não encontrada!");
        }
    }

    private void buscarVeiculoPorTrechoNome() {
        System.out.println("\nDigite um trecho do veículo para consulta ou (S) para Encerrar:");
        var trechoNomeVeiculo = leitura.nextLine();
        if (trechoNomeVeiculo.equalsIgnoreCase("S")) {
            System.out.println("\n*** Aplicação Encerrada ***");
            System.exit(0);
        }

        List<Veiculo> veiculosEncontrados = repositorio.veiculosPorTrecho(trechoNomeVeiculo);
        if (veiculosEncontrados.isEmpty() == true) {
            System.out.println("Não encontrado nenhum veículo com o trecho: " + trechoNomeVeiculo);
            buscarVeiculoPorTrechoNome();
        } else {
            veiculosEncontrados.forEach(v ->
                    System.out.printf("Código: %s - Veículo: %s - Segmento: %s - Ano: %s - Valor: %s\n",
                            v.getCodigoModelo(), v.getModelo(), v.getSegmento(), v.getAno(), v.getValor()));
        }
    }

    private void buscarMarcaPorSegmento() {
        System.out.println("Qual a marca para busca?");
        var nomeMarca = leitura.nextLine();
        System.out.println("Qual o segmento para busca?");
        var segmentoMarca = leitura.nextLine();
        List<DadosMarca> marcasEncontradas = repositorio.findByMarcaContainingIgnoreCaseAndSegmentoContainingIgnoreCase(nomeMarca, segmentoMarca);
        marcasEncontradas.forEach(m ->
                System.out.println("Marcas: " + m.getMarca() + " Segmento: " + m.getSegmento()));
    }

    private void buscarVeiculoPorValor() {
        System.out.println("Qual o veículo para busca?");
        var nomeVeiculo = leitura.nextLine();
        System.out.println("Qual o valor máximo do veículo?");
        var valorVeiculo = leitura.nextLine();
        List<Veiculo> veiculosEncontrados = repositorio.veiculosPorValores(nomeVeiculo, valorVeiculo);
        System.out.println("Veículos " + nomeVeiculo + " com valores menores que " + valorVeiculo);
        veiculosEncontrados.forEach(v ->
                System.out.println(v.getModelo() + " Valores: " + v.getValor()));
    }

    private void buscarVeiculosAposUmaData() {
        System.out.println("Qual o veículo para busca?");
        var nomeVeiculo = leitura.nextLine();
        System.out.println("Digite o ano limite do veículo:");
        var anoLimite = leitura.nextInt();
        leitura.nextLine();
        List<Veiculo> veiculosAno = repositorio.veiculosPorAno(nomeVeiculo, anoLimite);
        System.out.println("Veículos " + nomeVeiculo + " com ano maior que " + anoLimite + ":");
        veiculosAno.forEach(v ->
                System.out.println(v.getModelo() + " - Valores: " + v.getValor() + " - Ano: " + v.getAno()));
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
                     
                    1 - Marcas/Veículos
                    2 - Veículos
                                    
                    0 - Sair                     """;
            System.out.println(menu);
            opcaoDelete = leitura.nextInt();
            leitura.nextLine();
            switch (opcaoDelete) {
                case 1:
                    repositorio.deleteVeiculoFull();
                    repositorio.deleteDadosMarcaFull();
                    return;
                case 2:
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

      *** EXEMPLOS DE FUNÇÕES .STREAM *** (FILTRA POR VALOR CRIANDO NOVA LISTA (MAP) E TRAZENDO SOMENTE O PRIMEIRO)

                var codigoVeiculo =  modeloLista.modelos().stream()
                        .filter(m -> m.codigo().equals("9680"))
                        .map(m -> m.codigo())
                        .findFirst();

 */
