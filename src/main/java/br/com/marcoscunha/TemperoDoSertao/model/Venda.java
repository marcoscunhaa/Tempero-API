package br.com.marcoscunha.TemperoDoSertao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "vendas")
public class Venda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dataVenda;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private String produto;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private BigDecimal precoCompra;

    @Column(nullable = false)
    private BigDecimal precoVenda;

    @Column(nullable = false)
    private int quantidadeVendida;

    @Column(nullable = false)
    private String formaPagamento;

    @PrePersist
    public void prePersist() {
        if (dataVenda == null) {
            dataVenda = LocalDate.now();  // Garante data automática
        }
    }

    public BigDecimal getLucro() {
        return precoVenda
                .subtract(precoCompra)
                .multiply(BigDecimal.valueOf(quantidadeVendida));
    }
}
