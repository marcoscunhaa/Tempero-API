package br.com.marcoscunha.TemperoDoSertao.service;

import br.com.marcoscunha.TemperoDoSertao.dto.ResumoVendasDTO;
import br.com.marcoscunha.TemperoDoSertao.model.Produto;
import br.com.marcoscunha.TemperoDoSertao.model.Reposicao;
import br.com.marcoscunha.TemperoDoSertao.model.Venda;
import br.com.marcoscunha.TemperoDoSertao.repository.ProdutoRepository;
import br.com.marcoscunha.TemperoDoSertao.repository.ReposicaoRepository;
import br.com.marcoscunha.TemperoDoSertao.repository.VendaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final ReposicaoRepository reposicaoRepository;

    public VendaService(VendaRepository vendaRepository,
                        ProdutoRepository produtoRepository,
                        ReposicaoRepository reposicaoRepository) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
        this.reposicaoRepository = reposicaoRepository;
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

        // Buscar produto no banco
        Produto produto = buscarProduto(venda.getProduto());

        int quantidadeVendida = venda.getQuantidadeVendida();
        if (quantidadeVendida <= 0) {
            throw new RuntimeException("Quantidade vendida inválida.");
        }

        if (produto.getQuantidadeEstoque() < quantidadeVendida) {
            throw new RuntimeException("Estoque insuficiente.");
        }

        // Baixa estoque
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidadeVendida);
        produtoRepository.save(produto);

        // Preenche valores automáticos da venda
        venda.setPrecoCompra(produto.getPrecoCompra());
        if (venda.getDataVenda() == null) {
            venda.setDataVenda(LocalDate.now());
        }
        if (venda.getLucro() == null) {
            venda.setLucro(BigDecimal.ZERO);
        }

        Venda vendaSalva = vendaRepository.save(venda);

        // Atualiza produto com os dados do último lote sempre
        Optional<Reposicao> ultimoLoteOpt = reposicaoRepository.findTopByProdutoIdOrderByDataEntradaDesc(produto.getId());

        if (ultimoLoteOpt.isPresent()) {
            Reposicao ultimoLote = ultimoLoteOpt.get();

            produto.setPrecoCompra(ultimoLote.getPrecoCompra());
            produto.setPrecoVenda(ultimoLote.getPrecoVenda());
            produto.setVencimento(ultimoLote.getVencimento());

            produtoRepository.save(produto);
        }

        return vendaSalva;
    }


    // ============================
    // DELETAR VENDA (repor estoque)
    // ============================
    public void deletarVenda(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada."));

        Produto produto = buscarProduto(venda.getProduto());

        produto.setQuantidadeEstoque(
                produto.getQuantidadeEstoque() + venda.getQuantidadeVendida()
        );

        produtoRepository.save(produto);

        vendaRepository.deleteById(id);
    }

    // ============================
    // EDITAR VENDA
    // ============================
    public Venda atualizarVenda(Long id, Venda vendaAtualizada) {

        Venda vendaExistente = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada."));

        Produto produto = buscarProduto(vendaAtualizada.getProduto());

        // Repor estoque antigo
        produto.setQuantidadeEstoque(
                produto.getQuantidadeEstoque() + vendaExistente.getQuantidadeVendida()
        );

        // Verificar nova quantidade
        if (produto.getQuantidadeEstoque() < vendaAtualizada.getQuantidadeVendida()) {
            throw new RuntimeException("Estoque insuficiente para editar venda.");
        }

        // Aplicar nova baixa
        produto.setQuantidadeEstoque(
                produto.getQuantidadeEstoque() - vendaAtualizada.getQuantidadeVendida()
        );

        produtoRepository.save(produto);

        // Atualizar dados
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
                .map(v -> v.getPrecoVenda().multiply(BigDecimal.valueOf(v.getQuantidadeVendida())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalComprado = vendas.stream()
                .map(v -> v.getPrecoCompra().multiply(BigDecimal.valueOf(v.getQuantidadeVendida())))
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
    // BUSCAR PRODUTO POR DETALHE
    // ============================
    private Produto buscarProduto(String detalhe) {
        Produto produto = produtoRepository.findByDetalheIgnoreCase(detalhe);
        if (produto == null) {
            throw new RuntimeException("Produto não encontrado: " + detalhe);
        }
        return produto;
    }
}
