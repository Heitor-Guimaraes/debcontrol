package br.com.debcontrol.dao;

import br.com.debcontrol.config.ConnectionFactory;
import br.com.debcontrol.exception.DatabaseException;
import br.com.debcontrol.model.Pagamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagamentoDAO {
    public Pagamento inserir(Connection connection, Pagamento pagamento) {
        String sql = """
                INSERT INTO pagamento
                    (id_venda, id_funcionario, id_forma_pagamento, data_pagamento, valor_pago, observacao)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, pagamento.getIdVenda());
            statement.setLong(2, pagamento.getIdFuncionario());
            statement.setLong(3, pagamento.getIdFormaPagamento());
            statement.setTimestamp(4, Timestamp.valueOf(pagamento.getDataPagamento()));
            statement.setBigDecimal(5, pagamento.getValorPago());
            statement.setString(6, pagamento.getObservacao());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) pagamento.setIdPagamento(keys.getLong(1));
            }
            return pagamento;
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao registrar o pagamento.", exception);
        }
    }

    public List<Pagamento> listar(String filtro) {
        String search = filtro == null ? "" : filtro.trim();
        String sql = """
                SELECT p.id_pagamento, p.id_venda, v.id_cliente, c.nome AS cliente_nome,
                       p.id_funcionario, f.nome AS funcionario_nome,
                       p.id_forma_pagamento, fp.nome AS forma_pagamento_nome,
                       p.data_pagamento, p.valor_pago, p.observacao
                FROM pagamento p
                JOIN venda_fiada v ON v.id_venda = p.id_venda
                JOIN cliente c ON c.id_cliente = v.id_cliente
                JOIN funcionario f ON f.id_funcionario = p.id_funcionario
                JOIN forma_pagamento fp ON fp.id_forma_pagamento = p.id_forma_pagamento
                WHERE (? = '' OR c.nome LIKE ? OR f.nome LIKE ? OR CAST(p.id_venda AS CHAR) LIKE ?)
                ORDER BY p.data_pagamento DESC, p.id_pagamento DESC
                """;
        List<Pagamento> pagamentos = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String like = "%" + search + "%";
            statement.setString(1, search);
            statement.setString(2, like);
            statement.setString(3, like);
            statement.setString(4, like);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) pagamentos.add(mapear(result));
            }
            return pagamentos;
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao consultar os pagamentos.", exception);
        }
    }

    private Pagamento mapear(ResultSet result) throws SQLException {
        Pagamento pagamento = new Pagamento();
        pagamento.setIdPagamento(result.getLong("id_pagamento"));
        pagamento.setIdVenda(result.getLong("id_venda"));
        pagamento.setIdCliente(result.getLong("id_cliente"));
        pagamento.setClienteNome(result.getString("cliente_nome"));
        pagamento.setIdFuncionario(result.getLong("id_funcionario"));
        pagamento.setFuncionarioNome(result.getString("funcionario_nome"));
        pagamento.setIdFormaPagamento(result.getLong("id_forma_pagamento"));
        pagamento.setFormaPagamentoNome(result.getString("forma_pagamento_nome"));
        pagamento.setDataPagamento(result.getTimestamp("data_pagamento").toLocalDateTime());
        pagamento.setValorPago(result.getBigDecimal("valor_pago"));
        pagamento.setObservacao(result.getString("observacao"));
        return pagamento;
    }
}
