package br.com.alura.fipeveiculos.repository;

import br.com.alura.fipeveiculos.model.DadosMarca;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MarcaRepository extends JpaRepository<DadosMarca, Long> {

//    @Query(value = "Select * FROM Marcas m WHERE m.Marca=:marca AND m.segmento = 'carros'", nativeQuery = true)
    @Query(value = "Select * FROM Marcas WHERE segmento = 'carros'", nativeQuery = true)
    DadosMarca findByMarcaContainingIgnoreCase(String marca);

    @Modifying
    @Transactional
    @Query("Update DadosMarca SET detalheIa=:detalheIa WHERE id=:id")
    public void updateDetalheIa(@Param("id") Long id, @Param("detalheIa") String detalheIa);

    //    Optional<DadosMarca> findByMarcaContainingIgnoreCase(String marca);
}
