package br.com.alura.fipeveiculos.repository;

import br.com.alura.fipeveiculos.model.DadosMarca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarcaRepository extends JpaRepository<DadosMarca, Long> {
    Optional<DadosMarca> findByMarcaContainingIgnoreCase(String marca);
}
