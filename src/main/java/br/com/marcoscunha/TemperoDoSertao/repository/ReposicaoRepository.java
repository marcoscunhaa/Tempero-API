package br.com.marcoscunha.TemperoDoSertao.repository;

import br.com.marcoscunha.TemperoDoSertao.model.Produto;
import br.com.marcoscunha.TemperoDoSertao.model.Reposicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReposicaoRepository extends JpaRepository<Reposicao, Long> {
    // opcional: buscar todas reposições de um produto
    List<Reposicao> findByProduto(Produto produto);

    // opcional: buscar lotes vencendo antes de uma data
    List<Reposicao> findByVencimentoBefore(java.time.LocalDate data);
    Optional<Reposicao> findTopByProdutoOrderByDataEntradaDesc(Produto produto);
    Optional<Reposicao> findTopByProdutoIdOrderByDataEntradaDesc(Long produtoId);
}
