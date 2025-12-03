package br.com.marcoscunha.TemperoDoSertao.controller;

import br.com.marcoscunha.TemperoDoSertao.model.Produto;
import br.com.marcoscunha.TemperoDoSertao.model.Reposicao;
import br.com.marcoscunha.TemperoDoSertao.service.ProdutoService;
import br.com.marcoscunha.TemperoDoSertao.service.ReposicaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reposicoes")
public class ReposicaoController {
    private final ReposicaoService reposicaoService;
    private final ProdutoService produtoService;

    public ReposicaoController(ReposicaoService reposicaoService, ProdutoService produtoService) {
        this.reposicaoService = reposicaoService;
        this.produtoService = produtoService;
    }

    // Criar nova reposição
    @PostMapping
    public ResponseEntity<?> criarReposicao(@RequestBody Reposicao reposicao) {
        // Verifica se o produto existe
        Optional<Produto> produtoOpt = produtoService.buscarPorId(reposicao.getProduto().getId());
        if (produtoOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Produto não encontrado!");
        }

        reposicao.setProduto(produtoOpt.get());
        Reposicao salva = reposicaoService.salvarReposicao(reposicao);
        return ResponseEntity.ok(salva);
    }

    // Listar todas reposições
    @GetMapping
    public ResponseEntity<List<Reposicao>> listarTodos() {
        List<Reposicao> reposicoes = reposicaoService.listarTodos();
        if (reposicoes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reposicoes);
    }

    // Buscar reposição por id
    @GetMapping("/{id}")
    public ResponseEntity<Reposicao> buscarPorId(@PathVariable Long id) {
        return reposicaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar reposições por produto
    @GetMapping("/produto/{idProduto}")
    public ResponseEntity<List<Reposicao>> buscarPorProduto(@PathVariable Long idProduto) {
        Optional<Produto> produtoOpt = produtoService.buscarPorId(idProduto);
        if (produtoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Reposicao> reposicoes = reposicaoService.buscarPorProduto(produtoOpt.get());
        if (reposicoes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reposicoes);
    }

    // Deletar reposição
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        reposicaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
