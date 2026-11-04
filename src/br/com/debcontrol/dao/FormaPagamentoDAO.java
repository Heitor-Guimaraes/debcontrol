package br.com.debcontrol.dao;

import br.com.debcontrol.config.ConnectionFactory;
import br.com.debcontrol.exception.DatabaseException;
import br.com.debcontrol.model.FormaPagamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FormaPagamentoDAO {
    public boolean excluir(long idFormaPagamento) {
        String sql = "DELETE FROM forma_pagamento WHERE id_forma_pagamento = ? "
                + "AND NOT EXISTS (SELECT 1 FROM pagamento p WHERE p.id_forma_pagamento = forma_pagamento.id_forma_pagamento)";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, idFormaPagamento);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new DatabaseException("Não foi possível excluir a forma de pagamento. Verifique se já foi utilizada.", exception);
        }
    }

    public FormaPagamento inserir(FormaPagamento forma) {
        String sql = "INSERT INTO forma_pagamento (nome, descricao, ativo) VALUES (?, ?, ?)";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(statement, forma);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) forma.setIdFormaPagamento(keys.getLong(1));
            }
            return forma;
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao cadastrar a forma de pagamento.", exception);
        }
    }

    public void atualizar(FormaPagamento forma) {
        String sql = "UPDATE forma_pagamento SET nome=?, descricao=?, ativo=? WHERE id_forma_pagamento=?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            preencher(statement, forma);
            statement.setLong(4, forma.getIdFormaPagamento());
            if (statement.executeUpdate() == 0) throw new DatabaseException("Forma de pagamento não encontrada.", null);
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao atualizar a forma de pagamento.", exception);
        }
    }

    public List<FormaPagamento> listar(String filtro, boolean somenteAtivas) {
        String search = filtro == null ? "" : filtro.trim();
        String sql = """
                SELECT id_forma_pagamento, nome, descricao, ativo
                FROM forma_pagamento
                WHERE (? = '' OR nome LIKE ? OR descricao LIKE ?)
                  AND (? = FALSE OR ativo = TRUE)
                ORDER BY nome
                """;
        List<FormaPagamento> formas = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String like = "%" + search + "%";
            statement.setString(1, search);
            statement.setString(2, like);
            statement.setString(3, like);
            statement.setBoolean(4, somenteAtivas);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) formas.add(mapear(result));
            }
            return formas;
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao consultar as formas de pagamento.", exception);
        }
    }

    public Optional<FormaPagamento> buscarPorId(long id) {
        String sql = "SELECT id_forma_pagamento, nome, descricao, ativo FROM forma_pagamento WHERE id_forma_pagamento=?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapear(result)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao buscar a forma de pagamento.", exception);
        }
    }

    public void desativar(long id) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE forma_pagamento SET ativo=FALSE WHERE id_forma_pagamento=?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao desativar a forma de pagamento.", exception);
        }
    }

    private void preencher(PreparedStatement statement, FormaPagamento forma) throws SQLException {
        statement.setString(1, forma.getNome());
        statement.setString(2, forma.getDescricao());
        statement.setBoolean(3, forma.isAtivo());
    }

    private FormaPagamento mapear(ResultSet result) throws SQLException {
        FormaPagamento forma = new FormaPagamento();
        forma.setIdFormaPagamento(result.getLong("id_forma_pagamento"));
        forma.setNome(result.getString("nome"));
        forma.setDescricao(result.getString("descricao"));
        forma.setAtivo(result.getBoolean("ativo"));
        return forma;
    }
}
