package br.com.marcoscunha.TemperoDoSertao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResumoVendasDTO {
    private BigDecimal totalVendido;
    private BigDecimal totalComprado;
    private BigDecimal lucroBruto;
    private Double margemLucro;
}
