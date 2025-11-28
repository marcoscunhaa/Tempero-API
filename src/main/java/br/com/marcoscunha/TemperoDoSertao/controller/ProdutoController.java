package br.com.marcoscunha.TemperoDoSertao.controller;

import br.com.marcoscunha.TemperoDoSertao.model.Produto;
import br.com.marcoscunha.TemperoDoSertao.service.ProdutoService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        List<Produto> produtos = produtoService.listarTodos();
        if (produtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Produto>> buscarPorCategoria(@PathVariable String categoria) {
        List<Produto> produtos = produtoService.buscarPorCategoria(categoria);
        if (produtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/pesquisar")
    public ResponseEntity<List<Produto>> pesquisar(@RequestParam("q") String termo) {
        List<Produto> produtos = produtoService.buscarPorDetalhe(termo);
        if (produtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(produtos);
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody Produto produto) {
        try {
            Produto novoProduto = produtoService.salvar(produto);
            return ResponseEntity.ok(novoProduto);

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity
                    .status(409)
                    .body("Já existe um produto com essa descrição!");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Produto produtoAtualizado) {
        return produtoService.buscarPorId(id)
                .map(produto -> {
                    try {
                        produto.setCategoria(produtoAtualizado.getCategoria());
                        produto.setDetalhe(produtoAtualizado.getDetalhe());
                        produto.setPrecoCompra(produtoAtualizado.getPrecoCompra());
                        produto.setMarca(produtoAtualizado.getMarca());
                        produto.setPrecoVenda(produtoAtualizado.getPrecoVenda());
                        produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
                        produto.setVencimento(produtoAtualizado.getVencimento());

                        Produto atualizado = produtoService.salvar(produto);
                        return ResponseEntity.ok(atualizado);

                    } catch (DataIntegrityViolationException e) {
                        return ResponseEntity
                                .status(409)
                                .body("Já existe um produto com essa descrição!");
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (produtoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
