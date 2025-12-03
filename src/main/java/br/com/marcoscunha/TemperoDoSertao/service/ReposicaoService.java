package br.com.marcoscunha.TemperoDoSertao.service;

import br.com.marcoscunha.TemperoDoSertao.model.Produto;
import br.com.marcoscunha.TemperoDoSertao.model.Reposicao;
import br.com.marcoscunha.TemperoDoSertao.repository.ProdutoRepository;
import br.com.marcoscunha.TemperoDoSertao.repository.ReposicaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReposicaoService {
    private final ReposicaoRepository reposicaoRepository;
    private final ProdutoRepository produtoRepository;

    public ReposicaoService(ReposicaoRepository reposicaoRepository, ProdutoRepository produtoRepository) {
        this.reposicaoRepository = reposicaoRepository;
        this.produtoRepository = produtoRepository;
    }

    // Salvar reposição e atualizar estoque
    public Reposicao salvarReposicao(Reposicao reposicao) {
        // 1. Salva o lote de reposição
        Reposicao salvo = reposicaoRepository.save(reposicao);

        // 2. Atualiza a quantidade do produto
        Produto produto = reposicao.getProduto();
        int novaQuantidade = produto.getQuantidadeEstoque() + reposicao.getQuantidade();
        produto.setQuantidadeEstoque(novaQuantidade);

        produtoRepository.save(produto);

        return salvo;
    }

    // Listar todas reposições
    public List<Reposicao> listarTodos() {
        return reposicaoRepository.findAll();
    }

    // Buscar por produto
    public List<Reposicao> buscarPorProduto(Produto produto) {
        return reposicaoRepository.findByProduto(produto);
    }

    // Buscar por id
    public Optional<Reposicao> buscarPorId(Long id) {
        return reposicaoRepository.findById(id);
    }

    // Deletar reposição (opcional: descontar do estoque se quiser)
    public void deletar(Long id) {
        Optional<Reposicao> reposicao = reposicaoRepository.findById(id);
        reposicao.ifPresent(r -> {
            Produto produto = r.getProduto();
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - r.getQuantidade());
            produtoRepository.save(produto);

            reposicaoRepository.deleteById(id);
        });
    }
}
