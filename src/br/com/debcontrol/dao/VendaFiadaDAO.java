package br.com.debcontrol.dao;

import br.com.debcontrol.config.ConnectionFactory;
import br.com.debcontrol.exception.DatabaseException;
import br.com.debcontrol.model.StatusVenda;
import br.com.debcontrol.model.VendaFiada;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VendaFiadaDAO {
    private static final String SELECT_BASE = """
            SELECT v.id_venda, v.id_cliente, c.nome AS cliente_nome,
                   v.id_funcionario, f.nome AS funcionario_nome,
                   v.data_venda, v.data_vencimento, v.descricao_compra,
                   v.valor_total, COALESCE(SUM(p.valor_pago), 0) AS valor_pago,
                   v.valor_total - COALESCE(SUM(p.valor_pago), 0) AS saldo,
                   v.status, v.observacao
            FROM venda_fiada v
            JOIN cliente c ON c.id_cliente = v.id_cliente
            JOIN funcionario f ON f.id_funcionario = v.id_funcionario
            LEFT JOIN pagamento p ON p.id_venda = v.id_venda
            """;

    private static final String GROUP_ORDER = """
            GROUP BY v.id_venda, v.id_cliente, c.nome, v.id_funcionario, f.nome,
                     v.data_venda, v.data_vencimento, v.descricao_compra,
                     v.valor_total, v.status, v.observacao
            ORDER BY v.data_venda DESC, v.id_venda DESC
            """;

    public VendaFiada inserir(VendaFiada venda) {
        String sql = """
                INSERT INTO venda_fiada
                    (id_cliente, id_funcionario, data_venda, data_vencimento,
                     descricao_compra, valor_total, status, observacao)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, venda.getIdCliente());
            statement.setLong(2, venda.getIdFuncionario());
            statement.setTimestamp(3, Timestamp.valueOf(venda.getDataVenda()));
            statement.setDate(4, Date.valueOf(venda.getDataVencimento()));
            statement.setString(5, venda.getDescricaoCompra());
            statement.setBigDecimal(6, venda.getValorTotal());
            statement.setString(7, venda.getStatus().name());
            statement.setString(8, venda.getObservacao());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) venda.setIdVenda(keys.getLong(1));
            }
            return venda;
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao registrar a venda fiada.", exception);
        }
    }

    public Optional<VendaFiada> buscarPorId(long idVenda) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            return consultarPorId(connection, idVenda);
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao buscar a venda.", exception);
        }
    }

    /** Bloqueia a venda até o fim da transação, serializando pagamentos antes de consultar o saldo. */
    public Optional<VendaFiada> buscarParaPagamento(Connection connection, long idVenda) {
        try (PreparedStatement lock = connection.prepareStatement(
                "SELECT id_venda FROM venda_fiada WHERE id_venda = ? FOR UPDATE")) {
            lock.setLong(1, idVenda);
            try (ResultSet row = lock.executeQuery()) {
                if (!row.next()) return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao bloquear a venda para pagamento.", exception);
        }
        return consultarPorId(connection, idVenda);
    }

    private Optional<VendaFiada> consultarPorId(Connection connection, long idVenda) {
        String sql = SELECT_BASE + " WHERE v.id_venda = ? " + GROUP_ORDER;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, idVenda);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapear(result)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao buscar a venda.", exception);
        }
    }

    public List<VendaFiada> listar(String filtro, boolean apenasPendentes) {
        atualizarAtrasadas();
        String search = filtro == null ? "" : filtro.trim();
        String sql = SELECT_BASE + """
                WHERE (? = '' OR c.nome LIKE ? OR f.nome LIKE ? OR CAST(v.id_venda AS CHAR) LIKE ?)
                  AND (? = FALSE OR v.status IN ('ABERTA', 'PARCIAL', 'ATRASADA'))
                """ + GROUP_ORDER;
        List<VendaFiada> vendas = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String like = "%" + search + "%";
            statement.setString(1, search);
            statement.setString(2, like);
            statement.setString(3, like);
            statement.setString(4, like);
            statement.setBoolean(5, apenasPendentes);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) vendas.add(mapear(result));
            }
            return vendas;
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao consultar as vendas.", exception);
        }
    }

    public List<VendaFiada> listarPorCliente(long idCliente) {
        String sql = SELECT_BASE + " WHERE v.id_cliente = ? " + GROUP_ORDER;
        List<VendaFiada> vendas = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, idCliente);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) vendas.add(mapear(result));
            }
            return vendas;
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao consultar o histórico do cliente.", exception);
        }
    }

    public BigDecimal calcularSaldoCliente(long idCliente) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT saldo_devedor FROM vw_saldo_cliente WHERE id_cliente = ?")) {
            statement.setLong(1, idCliente);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getBigDecimal("saldo_devedor") : BigDecimal.ZERO;
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao calcular o saldo do cliente.", exception);
        }
    }

    /** Consulta única para telas que exibem o saldo de vários clientes. */
    public Map<Long, BigDecimal> listarSaldosClientes() {
        Map<Long, BigDecimal> saldos = new HashMap<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id_cliente, saldo_devedor FROM vw_saldo_cliente");
             ResultSet result = statement.executeQuery()) {
            while (result.next()) saldos.put(result.getLong("id_cliente"), result.getBigDecimal("saldo_devedor"));
            return saldos;
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao calcular os saldos dos clientes.", exception);
        }
    }

    public void cancelar(long idVenda, String justificativa) {
        String sql = """
                UPDATE venda_fiada
                SET status = 'CANCELADA', observacao = ?
                WHERE id_venda = ? AND status IN ('ABERTA', 'ATRASADA')
                  AND NOT EXISTS (SELECT 1 FROM pagamento p WHERE p.id_venda = venda_fiada.id_venda)
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, justificativa);
            statement.setLong(2, idVenda);
            if (statement.executeUpdate() == 0) throw new DatabaseException("A venda não pode ser cancelada.", null);
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao cancelar a venda.", exception);
        }
    }

    public void atualizarStatus(Connection connection, long idVenda, StatusVenda status) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE venda_fiada SET status = ? WHERE id_venda = ?")) {
            statement.setString(1, status.name());
            statement.setLong(2, idVenda);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao atualizar o status da venda.", exception);
        }
    }

    public void atualizarAtrasadas() {
        String sql = """
                UPDATE venda_fiada SET status = 'ATRASADA'
                WHERE status IN ('ABERTA', 'PARCIAL') AND data_vencimento < CURRENT_DATE()
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao atualizar vendas atrasadas.", exception);
        }
    }

    private VendaFiada mapear(ResultSet result) throws SQLException {
        VendaFiada venda = new VendaFiada();
        venda.setIdVenda(result.getLong("id_venda"));
        venda.setIdCliente(result.getLong("id_cliente"));
        venda.setClienteNome(result.getString("cliente_nome"));
        venda.setIdFuncionario(result.getLong("id_funcionario"));
        venda.setFuncionarioNome(result.getString("funcionario_nome"));
        venda.setDataVenda(result.getTimestamp("data_venda").toLocalDateTime());
        venda.setDataVencimento(result.getDate("data_vencimento").toLocalDate());
        venda.setDescricaoCompra(result.getString("descricao_compra"));
        venda.setValorTotal(result.getBigDecimal("valor_total"));
        venda.setValorPago(result.getBigDecimal("valor_pago"));
        venda.setSaldo(result.getBigDecimal("saldo"));
        venda.setStatus(StatusVenda.valueOf(result.getString("status")));
        venda.setObservacao(result.getString("observacao"));
        return venda;
    }
}
