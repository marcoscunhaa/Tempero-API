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

    @GetMapping
    public List<Venda> listarVendas(@RequestParam(defaultValue = "todas") String categoria) {
        return vendaService.listarPorCategoria(categoria);
    }

    @PostMapping
    public ResponseEntity<Venda> criarVenda(@RequestBody Venda venda) {
        Venda vendaSalva = vendaService.salvarVenda(venda);
        return ResponseEntity.ok(vendaSalva);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVenda(@PathVariable Long id) {
        vendaService.deletarVenda(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/resumo")
    public ResumoVendasDTO getResumoVendas() {
        return vendaService.resumoVendas();
    }
}
