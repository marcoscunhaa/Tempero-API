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

    /**
     * Cria reposição (lote) e reflete imediatamente no produto:
     * - Soma estoque
     * - Atualiza preço de VENDA
     * - Atualiza preço de AQUISIÇÃO
     * - Vencimento fica apenas no lote
     */
    @Transactional
    public Reposicao salvarReposicao(Reposicao reposicao) {

        Long produtoId = reposicao.getProduto().getId();

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // Estoque
        produto.setQuantidadeEstoque(
                produto.getQuantidadeEstoque() + reposicao.getQuantidade()
        );

        // Preços (sempre sobrescrevem)
        produto.setPrecoVenda(reposicao.getPrecoVenda());
        produto.setPrecoCompra(reposicao.getPrecoCompra()); // ou precoAquisicao

        produtoRepository.save(produto);

        // Vínculo correto
        reposicao.setProduto(produto);

        return reposicaoRepository.save(reposicao);
    }

    // Listar todas reposições
    public List<Reposicao> listarTodos() {
        return reposicaoRepository.findAll();
    }

    // Buscar por produto
    public List<Reposicao> buscarPorProduto(Produto produto) {
        return reposicaoRepository.findByProduto(produto);
    }

    // Buscar por ID
    public Optional<Reposicao> buscarPorId(Long id) {
        return reposicaoRepository.findById(id);
    }

    /**
     * Deleta reposição:
     * - Ajusta apenas estoque
     * - NÃO altera preços
     */
    @Transactional
    public void deletar(Long id) {

        Reposicao reposicao = reposicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reposição não encontrada"));

        Produto produto = produtoRepository.findById(
                reposicao.getProduto().getId()
        ).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setQuantidadeEstoque(
                produto.getQuantidadeEstoque() - reposicao.getQuantidade()
        );

        produtoRepository.save(produto);
        reposicaoRepository.delete(reposicao);
    }

    /**
     * Edita reposição:
     * - Ajusta estoque pela diferença
     * - Atualiza preço de VENDA
     * - Atualiza preço de AQUISIÇÃO
     */
    @Transactional
    public Reposicao editarReposicao(Long id, Reposicao reposicaoAtualizada) {

        Reposicao reposicaoExistente = reposicaoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Reposição com id " + id + " não encontrada.")
                );

        Produto produto = produtoRepository.findById(
                reposicaoExistente.getProduto().getId()
        ).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // Estoque
        int quantidadeAnterior = reposicaoExistente.getQuantidade();
        int diferenca = reposicaoAtualizada.getQuantidade() - quantidadeAnterior;

        produto.setQuantidadeEstoque(
                produto.getQuantidadeEstoque() + diferenca
        );

        // Preços
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
}
