package br.com.debcontrol.dto;

import java.math.BigDecimal;

public record DashboardResumo(
        BigDecimal totalAReceber,
        BigDecimal totalRecebidoMes,
        long clientesDevedores,
        long dividasAtrasadas) {
}
