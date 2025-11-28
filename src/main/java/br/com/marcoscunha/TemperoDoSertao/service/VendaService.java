package br.com.marcoscunha.TemperoDoSertao.service;

import br.com.marcoscunha.TemperoDoSertao.dto.ResumoVendasDTO;
import br.com.marcoscunha.TemperoDoSertao.model.Produto;
import br.com.marcoscunha.TemperoDoSertao.model.Venda;
import br.com.marcoscunha.TemperoDoSertao.repository.ProdutoRepository;
import br.com.marcoscunha.TemperoDoSertao.repository.VendaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class VendaService {
    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;

    public VendaService(VendaRepository vendaRepository,
                        ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<Venda> listarTodasVendas() {
        return vendaRepository.findAll();
    }

    public List<Venda> listarPorCategoria(String categoria) {
        if (categoria.equalsIgnoreCase("todas")) {
            return listarTodasVendas();
        }
        return vendaRepository.findByCategoriaIgnoreCase(categoria);
    }

    public Venda salvarVenda(Venda venda) {

        if (venda.getFormaPagamento() == null || venda.getFormaPagamento().isBlank()) {
            throw new RuntimeException("Forma de pagamento é obrigatória.");
        }
        venda.setFormaPagamento(venda.getFormaPagamento().trim().toUpperCase());
        Produto produto = produtoRepository.findByDetalheIgnoreCase(venda.getProduto());

        if (produto == null) {
            throw new RuntimeException("Produto não encontrado: " + venda.getProduto());
        }

        int quantidade = venda.getQuantidadeVendida();

        if (quantidade <= 0) {
            throw new RuntimeException("Quantidade vendida inválida");
        }

        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new RuntimeException("Estoque insuficiente para esta venda");
        }

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        produtoRepository.save(produto);

        venda.setPrecoCompra(produto.getPrecoCompra());
        venda.setDataVenda(LocalDate.now());
        return vendaRepository.save(venda);
    }

    public void deletarVenda(Long id) {
        vendaRepository.deleteById(id);
    }

    public ResumoVendasDTO resumoVendas() {
        List<Venda> vendas = vendaRepository.findAll();

        BigDecimal totalVendido = vendas.stream()
                .map(v -> v.getPrecoVenda()
                        .multiply(BigDecimal.valueOf(v.getQuantidadeVendida())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalComprado = vendas.stream()
                .map(v -> v.getPrecoCompra()
                        .multiply(BigDecimal.valueOf(v.getQuantidadeVendida())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal lucroBruto = totalVendido.subtract(totalComprado);

        double margemLucro = totalVendido.compareTo(BigDecimal.ZERO) == 0
                ? 0.0
                : lucroBruto.divide(totalVendido, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        return new ResumoVendasDTO(totalVendido, totalComprado, lucroBruto, margemLucro);
    }
}
