package br.com.debcontrol.dto;

import java.math.BigDecimal;

public record ClienteResumo(
        long clienteId,
        String nome,
        String telefone,
        BigDecimal saldoDevedor,
        boolean possuiAtraso) {
}
