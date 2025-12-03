package br.com.marcoscunha.TemperoDoSertao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reposicoes")
public class Reposicao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private LocalDate vencimento;

    @Column(nullable = false)
    private LocalDate dataEntrada;

    @Column(name = "preco_compra", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal precoCompra;

    @Column(name = "preco_venda", precision = 10, scale = 2)
    private java.math.BigDecimal precoVenda;
}
