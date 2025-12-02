package br.com.marcoscunha.TemperoDoSertao.controller;

import br.com.marcoscunha.TemperoDoSertao.dto.ResumoVendasDTO;
import br.com.marcoscunha.TemperoDoSertao.model.Venda;
import br.com.marcoscunha.TemperoDoSertao.service.VendaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    // ============================
    // LISTAR TODAS AS VENDAS
    // ============================
    @GetMapping
    public ResponseEntity<List<Venda>> listarVendas() {
        return ResponseEntity.ok(vendaService.listarTodasVendas());
    }

    // ============================
    // CRIAR VENDA COM ITENS
    // ============================
    @PostMapping
    public ResponseEntity<Venda> criarVenda(@RequestBody Venda venda) {
        return ResponseEntity.ok(vendaService.salvarVenda(venda));
    }

    // ============================
    // ATUALIZAR VENDA
    // ============================
    @PutMapping("/{id}")
    public ResponseEntity<Venda> atualizarVenda(@PathVariable Long id, @RequestBody Venda venda) {
        return ResponseEntity.ok(vendaService.atualizarVenda(id, venda));
    }

    // ============================
    // DELETAR VENDA (REPOR ESTOQUE)
    // ============================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVenda(@PathVariable Long id) {
        vendaService.deletarVenda(id);
        return ResponseEntity.noContent().build();
    }

    // ============================
    // RESUMO DAS VENDAS
    // ============================
    @GetMapping("/resumo")
    public ResponseEntity<ResumoVendasDTO> getResumoVendas() {
        return ResponseEntity.ok(vendaService.resumoVendas());
    }
}
