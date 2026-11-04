package br.com.debcontrol.dao;

import br.com.debcontrol.config.ConnectionFactory;
import br.com.debcontrol.dto.ClienteResumo;
import br.com.debcontrol.dto.DashboardResumo;
import br.com.debcontrol.exception.DatabaseException;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RelatorioDAO {
    public DashboardResumo carregarDashboard() {
        String sql = """
                SELECT
                    COALESCE((SELECT SUM(v.valor_total) FROM venda_fiada v
                              WHERE v.status <> 'CANCELADA'), 0)
                    - COALESCE((SELECT SUM(p.valor_pago) FROM pagamento p), 0) AS total_receber,
                    COALESCE((SELECT SUM(p.valor_pago) FROM pagamento p
                              WHERE YEAR(p.data_pagamento) = YEAR(CURRENT_DATE())
                                AND MONTH(p.data_pagamento) = MONTH(CURRENT_DATE())), 0) AS recebido_mes,
                    (SELECT COUNT(*) FROM vw_saldo_cliente WHERE saldo_devedor > 0) AS clientes_devedores,
                    (SELECT COUNT(*) FROM (
                        SELECT v.id_venda
                        FROM venda_fiada v
                        LEFT JOIN (
                            SELECT id_venda, SUM(valor_pago) AS total_pago
                            FROM pagamento GROUP BY id_venda
                        ) p ON p.id_venda = v.id_venda
                        WHERE v.status IN ('ABERTA', 'PARCIAL', 'ATRASADA')
                          AND v.data_vencimento < CURRENT_DATE()
                          AND v.valor_total - COALESCE(p.total_pago, 0) > 0
                    ) atrasadas) AS dividas_atrasadas
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return new DashboardResumo(
                    result.getBigDecimal("total_receber"),
                    result.getBigDecimal("recebido_mes"),
                    result.getLong("clientes_devedores"),
                    result.getLong("dividas_atrasadas"));
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao carregar o resumo financeiro.", exception);
        }
    }

    public List<ClienteResumo> listarClientesDevedores() {
        String sql = """
                SELECT c.id_cliente, c.nome, c.telefone, s.saldo_devedor,
                       EXISTS(
                           SELECT 1
                           FROM venda_fiada va
                           LEFT JOIN (
                               SELECT id_venda, SUM(valor_pago) AS pago
                               FROM pagamento GROUP BY id_venda
                           ) pa ON pa.id_venda = va.id_venda
                           WHERE va.id_cliente = c.id_cliente
                             AND va.status IN ('ABERTA', 'PARCIAL', 'ATRASADA')
                             AND va.data_vencimento < CURRENT_DATE()
                             AND va.valor_total - COALESCE(pa.pago, 0) > 0
                       ) AS possui_atraso
                FROM cliente c
                JOIN vw_saldo_cliente s ON s.id_cliente = c.id_cliente
                WHERE s.saldo_devedor > 0
                ORDER BY s.saldo_devedor DESC
                """;
        List<ClienteResumo> clientes = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                clientes.add(new ClienteResumo(
                        result.getLong("id_cliente"),
                        result.getString("nome"),
                        result.getString("telefone"),
                        result.getBigDecimal("saldo_devedor"),
                        result.getBoolean("possui_atraso")));
            }
            return clientes;
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao consultar os clientes devedores.", exception);
        }
    }

    public BigDecimal totalRecebido(LocalDate inicio, LocalDate fim) {
        String sql = "SELECT COALESCE(SUM(valor_pago), 0) FROM pagamento WHERE data_pagamento >= ? AND data_pagamento < ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(inicio.atStartOfDay()));
            statement.setTimestamp(2, Timestamp.valueOf(fim.plusDays(1).atStartOfDay()));
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getBigDecimal(1);
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao calcular os recebimentos do período.", exception);
        }
    }
}
