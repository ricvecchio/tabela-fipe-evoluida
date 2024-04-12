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

    @Modifying
    @Transactional
    @Query(value = "UPDATE Veiculos SET valor=:valorVeiculo, data_atualizacao=:dataAtualizacao WHERE codigo_modelo=:codigoModelo AND ano=:anoVeiculo", nativeQuery = true)
    public void atualizaDadosVeiculo(String codigoModelo, Integer anoVeiculo, Double valorVeiculo, String dataAtualizacao);

    Optional<DadosMarca> findByCodigo(String codigoMarca);

    Optional<DadosMarca> findTop1ByMarcaContainingIgnoreCase(String nomeMarca);

    List<DadosMarca> findByMarcaContainingIgnoreCaseAndSegmentoContainingIgnoreCase(String nomeVeiculo, String segmentoMarca);

    @Query("SELECT v FROM DadosMarca m JOIN m.veiculos v WHERE v.codigoModelo=:codigoModelo")
    List<Veiculo> veiculosPorCodigo(String codigoModelo);

    @Query(value = "SELECT COUNT(*) FROM Veiculos WHERE codigo_modelo=:codigoModelo AND ano=:ano", nativeQuery = true)
    Long veiculosPorCodigoEAno(String codigoModelo, Integer ano);

    @Query("SELECT v FROM DadosMarca m JOIN m.veiculos v WHERE v.modelo ILIKE %:trechoNomeVeiculo%")
    List<Veiculo> veiculosPorTrecho(String trechoNomeVeiculo);

    @Query("SELECT v FROM DadosMarca m JOIN m.veiculos v WHERE v.modelo ILIKE %:nomeVeiculo% AND v.valor<=:valorVeiculo")
    List<Veiculo> veiculosPorValores(String nomeVeiculo, Double valorVeiculo);

    @Query("SELECT v FROM DadosMarca m JOIN m.veiculos v WHERE v.modelo ILIKE %:nomeVeiculo% AND v.ano>=:anoLimite")
    List<Veiculo> veiculosPorAno(String nomeVeiculo, int anoLimite);

}
