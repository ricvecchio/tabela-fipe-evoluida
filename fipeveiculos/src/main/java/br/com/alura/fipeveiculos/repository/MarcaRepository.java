package br.com.alura.fipeveiculos.repository;

import br.com.alura.fipeveiculos.model.DadosMarca;
import br.com.alura.fipeveiculos.model.Veiculo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MarcaRepository extends JpaRepository<DadosMarca, Long> {

//    @Query(value = "Select * FROM Marcas m WHERE m.Marca=:marca AND m.segmento = 'carros'", nativeQuery = true)
//    @Query(value = "Select * FROM Marcas WHERE Marca=:marca AND segmento = 'carros'", nativeQuery = true)
//    @Query(value = "Select m FROM Marcas m WHERE m.Marca=:marca AND m.segmento = 'carros'", nativeQuery = true)

    @Modifying
    @Transactional
    @Query("Update DadosMarca SET detalheIa=:detalheIa WHERE id=:id")
    public void updateDetalheIa(@Param("id") Long id, @Param("detalheIa") String detalheIa);

    @Modifying
    @Transactional
    @Query("Delete DadosMarca")
    public void deleteDadosMarcaFull();

    @Modifying
    @Transactional
    @Query("Delete Veiculo")
    public void deleteVeiculoFull();

    Optional<DadosMarca> findByCodigo(String codigoMarca);

    Optional<DadosMarca> findByMarcaContainingIgnoreCase(String nomeMarca);

    @Query("SELECT v FROM DadosMarca m JOIN m.veiculos v WHERE v.modelo ILIKE %:trechoNomeVeiculo%")
    List<Veiculo> veiculosPorTrecho(String trechoNomeVeiculo);

    List<DadosMarca> findByMarcaContainingIgnoreCaseAndSegmentoContainingIgnoreCase(String nomeVeiculo, String segmentoMarca);

    @Query("SELECT v FROM DadosMarca m JOIN m.veiculos v WHERE v.modelo ILIKE %:nomeVeiculo% AND v.valor <= :valorVeiculo")
    List<Veiculo> veiculosPorValores(String nomeVeiculo, double valorVeiculo);

    @Query("SELECT v FROM DadosMarca m JOIN m.veiculos v WHERE v.modelo ILIKE %:nomeVeiculo% AND v.ano >= :anoLimite")
    List<Veiculo> veiculosPorAno(String nomeVeiculo, int anoLimite);
}
