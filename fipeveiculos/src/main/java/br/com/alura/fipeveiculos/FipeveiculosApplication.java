package br.com.alura.fipeveiculos;

import br.com.alura.fipeveiculos.principal.Principal;
import br.com.alura.fipeveiculos.repository.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FipeveiculosApplication implements CommandLineRunner {

	@Autowired
	private MarcaRepository repositorio;
	public static void main(String[] args) {
		SpringApplication.run(FipeveiculosApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repositorio);
		principal.exibeMenu();

	}
}
