package br.com.marcoscunha.TemperoDoSertao.service;

import br.com.marcoscunha.TemperoDoSertao.model.Produto;
import br.com.marcoscunha.TemperoDoSertao.model.Reposicao;
import br.com.marcoscunha.TemperoDoSertao.repository.ProdutoRepository;
import br.com.marcoscunha.TemperoDoSertao.repository.ReposicaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReposicaoService {

    private final ReposicaoRepository reposicaoRepository;
    private final ProdutoRepository produtoRepository;

    public ReposicaoService(ReposicaoRepository reposicaoRepository,
                            ProdutoRepository produtoRepository) {
        this.reposicaoRepository = reposicaoRepository;
        this.produtoRepository = produtoRepository;
    }

    // =========================
    // CRIAR REPOSIÇÃO
    // =========================
    @Transactional
    public Reposicao salvarReposicao(Reposicao reposicao) {

        Produto produto = produtoRepository.findById(
                reposicao.getProduto().getId()
        ).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // Estoque
        produto.setQuantidadeEstoque(
                produto.getQuantidadeEstoque() + reposicao.getQuantidade()
        );

        // Preços (último lote vence)
        produto.setPrecoVenda(reposicao.getPrecoVenda());
        produto.setPrecoCompra(reposicao.getPrecoCompra());

        produtoRepository.save(produto);

        reposicao.setProduto(produto);
        return reposicaoRepository.save(reposicao);
    }

    // =========================
    // LISTAGENS
    // =========================
    public List<Reposicao> listarTodos() {
        return reposicaoRepository.findAll();
    }

    public Optional<Reposicao> buscarPorId(Long id) {
        return reposicaoRepository.findById(id);
    }

    public List<Reposicao> buscarPorProduto(Produto produto) {
        return reposicaoRepository.findByProduto(produto);
    }

    // =========================
    // EDITAR REPOSIÇÃO
    // =========================
    @Transactional
    public Reposicao editarReposicao(Long id, Reposicao reposicaoAtualizada) {

        Reposicao reposicaoExistente = reposicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reposição não encontrada"));

        Produto produto = produtoRepository.findById(
                reposicaoExistente.getProduto().getId()
        ).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // Ajuste de estoque
        int diferenca = reposicaoAtualizada.getQuantidade()
                - reposicaoExistente.getQuantidade();

        produto.setQuantidadeEstoque(
                produto.getQuantidadeEstoque() + diferenca
        );

        // Atualiza preços
        produto.setPrecoVenda(reposicaoAtualizada.getPrecoVenda());
        produto.setPrecoCompra(reposicaoAtualizada.getPrecoCompra());

        // Atualiza lote
        reposicaoExistente.setQuantidade(reposicaoAtualizada.getQuantidade());
        reposicaoExistente.setVencimento(reposicaoAtualizada.getVencimento());
        reposicaoExistente.setDataEntrada(reposicaoAtualizada.getDataEntrada());
        reposicaoExistente.setPrecoCompra(reposicaoAtualizada.getPrecoCompra());
        reposicaoExistente.setPrecoVenda(reposicaoAtualizada.getPrecoVenda());

        produtoRepository.save(produto);
        return reposicaoRepository.save(reposicaoExistente);
    }

    // =========================
    // DELETAR REPOSIÇÃO (REVERSO)
    // =========================
    @Transactional
    public void deletar(Long id) {

        Reposicao reposicao = reposicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reposição não encontrada"));

        Produto produto = produtoRepository.findById(
                reposicao.getProduto().getId()
        ).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // 1️⃣ Ajusta estoque
        produto.setQuantidadeEstoque(
                produto.getQuantidadeEstoque() - reposicao.getQuantidade()
        );

        // 2️⃣ Remove o lote
        reposicaoRepository.delete(reposicao);

        // 3️⃣ Busca lote anterior (novo preço vigente)
        Optional<Reposicao> ultimaReposicao =
                reposicaoRepository.findTopByProdutoOrderByDataEntradaDesc(produto);

        if (ultimaReposicao.isPresent()) {
            produto.setPrecoVenda(ultimaReposicao.get().getPrecoVenda());
            produto.setPrecoCompra(ultimaReposicao.get().getPrecoCompra());
        } else {
            // Se não existir mais lote, você decide a regra:
            produto.setPrecoVenda(null);
            produto.setPrecoCompra(null);
        }

        produtoRepository.save(produto);
    }
}
