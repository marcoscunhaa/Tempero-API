package br.com.marcoscunha.TemperoDoSertao.repository;

import br.com.marcoscunha.TemperoDoSertao.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findByCategoriaIgnoreCase(String categoria);
    List<Venda> findByDataVenda(LocalDate dataVenda);
    List<Venda> findByDataVendaBetween(LocalDate inicio, LocalDate fim);
    List<Venda> findByCategoriaIgnoreCaseAndDataVenda(String categoria, LocalDate dataVenda);

}
