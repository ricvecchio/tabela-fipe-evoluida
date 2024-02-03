package br.com.alura.fipeveiculos;

import br.com.alura.fipeveiculos.model.DadosMarca;
import br.com.alura.fipeveiculos.service.ConsumoApi;
import br.com.alura.fipeveiculos.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FipeveiculosApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(FipeveiculosApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var consumoApi = new ConsumoApi();
		var json = consumoApi.obterDados("https://parallelum.com.br/fipe/api/v1/carros/marcas/");
//		var json = consumoApi.obterDados("https://www.omdbapi.com/?t=gilmore+girls&apikey=6585022c");
		String jsonFormatado = json.replace("[", "").replace("]", "");
		System.out.println(jsonFormatado);
		ConverteDados conversor = new ConverteDados();
		DadosMarca dados = conversor.obterDados(jsonFormatado, DadosMarca.class);
		System.out.println(dados);
	}
}
