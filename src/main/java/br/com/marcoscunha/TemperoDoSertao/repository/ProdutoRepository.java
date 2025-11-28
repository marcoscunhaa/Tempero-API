package br.com.marcoscunha.TemperoDoSertao.repository;

import br.com.marcoscunha.TemperoDoSertao.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByCategoriaIgnoreCase(String categoria);
    List<Produto> findByDetalheContainingIgnoreCase(String detalhe);
    Produto findByDetalheIgnoreCase(String detalhe);
}
