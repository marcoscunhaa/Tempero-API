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

    // ============================
    // LISTAGEM
    // ============================
    public List<Venda> listarTodasVendas() {
        return vendaRepository.findAll();
    }

    public List<Venda> listarPorCategoria(String categoria) {
        if (categoria.equalsIgnoreCase("todas")) {
            return listarTodasVendas();
        }
        return vendaRepository.findByCategoriaIgnoreCase(categoria);
    }

    // ============================
    // CRIAR VENDA
    // ============================
    public Venda salvarVenda(Venda venda) {

        if (venda.getFormaPagamento() == null || venda.getFormaPagamento().isBlank()) {
            throw new RuntimeException("Forma de pagamento é obrigatória.");
        }

        venda.setFormaPagamento(venda.getFormaPagamento().trim().toUpperCase());

        Produto produto = buscarProduto(venda.getProduto());

        int quantidade = venda.getQuantidadeVendida();
        if (quantidade <= 0) {
            throw new RuntimeException("Quantidade vendida inválida.");
        }

        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new RuntimeException("Estoque insuficiente.");
        }

        // Baixa estoque
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        produtoRepository.save(produto);

        // Preenche valores automáticos
        venda.setPrecoCompra(produto.getPrecoCompra());

        if (venda.getDataVenda() == null) {
            venda.setDataVenda(LocalDate.now());
        }

        if (venda.getLucro() == null) {
            venda.setLucro(BigDecimal.ZERO);
        }

        return vendaRepository.save(venda);
    }

    // ============================
    // DELETAR VENDA (repor estoque)
    // ============================
    public void deletarVenda(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada."));

        Produto produto = buscarProduto(venda.getProduto());

        // Repor estoque
        produto.setQuantidadeEstoque(
                produto.getQuantidadeEstoque() + venda.getQuantidadeVendida()
        );
        produtoRepository.save(produto);

        vendaRepository.deleteById(id);
    }

    // ============================
    // EDITAR VENDA (repor → aplicar nova)
    // ============================
    public Venda atualizarVenda(Long id, Venda vendaAtualizada) {

        Venda vendaExistente = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada."));

        Produto produto = buscarProduto(vendaAtualizada.getProduto());

        // 1️⃣ Repor estoque da venda antiga
        produto.setQuantidadeEstoque(
                produto.getQuantidadeEstoque() + vendaExistente.getQuantidadeVendida()
        );

        // 2️⃣ Verificar nova quantidade
        if (produto.getQuantidadeEstoque() < vendaAtualizada.getQuantidadeVendida()) {
            throw new RuntimeException("Estoque insuficiente para editar venda.");
        }

        // 3️⃣ Aplicar nova baixa
        produto.setQuantidadeEstoque(
                produto.getQuantidadeEstoque() - vendaAtualizada.getQuantidadeVendida()
        );
        produtoRepository.save(produto);

        // 4️⃣ Atualizar campos da venda
        vendaExistente.setCategoria(vendaAtualizada.getCategoria());
        vendaExistente.setProduto(vendaAtualizada.getProduto());
        vendaExistente.setMarca(vendaAtualizada.getMarca());
        vendaExistente.setQuantidadeVendida(vendaAtualizada.getQuantidadeVendida());
        vendaExistente.setFormaPagamento(vendaAtualizada.getFormaPagamento().trim().toUpperCase());
        vendaExistente.setPrecoVenda(vendaAtualizada.getPrecoVenda());
        vendaExistente.setPrecoCompra(produto.getPrecoCompra());
        vendaExistente.setLucro(vendaAtualizada.getLucro());
        vendaExistente.setDataVenda(vendaAtualizada.getDataVenda());

        return vendaRepository.save(vendaExistente);
    }

    // ============================
    // RESUMO
    // ============================
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

        BigDecimal lucroBruto = vendas.stream()
                .map(Venda::getLucro)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double margemLucro = totalVendido.compareTo(BigDecimal.ZERO) == 0
                ? 0.0
                : lucroBruto.divide(totalVendido, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();

        return new ResumoVendasDTO(totalVendido, totalComprado, lucroBruto, margemLucro);
    }

    // ============================
    // FUNÇÃO AUXILIAR
    // ============================
    private Produto buscarProduto(String detalhe) {
        Produto produto = produtoRepository.findByDetalheIgnoreCase(detalhe);
        if (produto == null) {
            throw new RuntimeException("Produto não encontrado: " + detalhe);
        }
        return produto;
    }
}
