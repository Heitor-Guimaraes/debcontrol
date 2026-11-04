package br.com.debcontrol.dao;

import br.com.debcontrol.config.ConnectionFactory;
import br.com.debcontrol.exception.DatabaseException;
import br.com.debcontrol.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteDAO {
    public boolean excluir(long idCliente) {
        String sql = "DELETE FROM cliente WHERE id_cliente = ? "
                + "AND NOT EXISTS (SELECT 1 FROM venda_fiada v WHERE v.id_cliente = cliente.id_cliente)";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, idCliente);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new DatabaseException("Não foi possível excluir o cliente. Verifique se possui histórico de vendas.", exception);
        }
    }

    public Cliente inserir(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, cpf, telefone, endereco, ativo) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(statement, cliente);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) cliente.setIdCliente(keys.getLong(1));
            }
            return cliente;
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao cadastrar o cliente.", exception);
        }
    }

    public void atualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome = ?, cpf = ?, telefone = ?, endereco = ?, ativo = ? WHERE id_cliente = ?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            preencher(statement, cliente);
            statement.setLong(6, cliente.getIdCliente());
            if (statement.executeUpdate() == 0) throw new DatabaseException("Cliente não encontrado.", null);
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao atualizar o cliente.", exception);
        }
    }

    public List<Cliente> listar(String filtro, boolean somenteAtivos) {
        String search = filtro == null ? "" : filtro.trim();
        String sql = """
                SELECT id_cliente, nome, cpf, telefone, endereco, ativo
                FROM cliente
                WHERE (? = '' OR nome LIKE ? OR cpf LIKE ? OR telefone LIKE ?)
                  AND (? = FALSE OR ativo = TRUE)
                ORDER BY nome
                """;
        List<Cliente> clientes = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String like = "%" + search + "%";
            statement.setString(1, search);
            statement.setString(2, like);
            statement.setString(3, like);
            statement.setString(4, like);
            statement.setBoolean(5, somenteAtivos);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) clientes.add(mapear(result));
            }
            return clientes;
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao consultar os clientes.", exception);
        }
    }

    public Optional<Cliente> buscarPorId(long idCliente) {
        return buscar("SELECT id_cliente, nome, cpf, telefone, endereco, ativo FROM cliente WHERE id_cliente = ?", idCliente);
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) return Optional.empty();
        return buscar("SELECT id_cliente, nome, cpf, telefone, endereco, ativo FROM cliente WHERE cpf = ?", cpf);
    }

    public void desativar(long idCliente) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE cliente SET ativo = FALSE WHERE id_cliente = ?")) {
            statement.setLong(1, idCliente);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao desativar o cliente.", exception);
        }
    }

    private Optional<Cliente> buscar(String sql, Object parametro) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, parametro);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapear(result)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao buscar o cliente.", exception);
        }
    }

    private void preencher(PreparedStatement statement, Cliente cliente) throws SQLException {
        statement.setString(1, cliente.getNome());
        statement.setString(2, cliente.getCpf());
        statement.setString(3, cliente.getTelefone());
        statement.setString(4, cliente.getEndereco());
        statement.setBoolean(5, cliente.isAtivo());
    }

    private Cliente mapear(ResultSet result) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setIdCliente(result.getLong("id_cliente"));
        cliente.setNome(result.getString("nome"));
        cliente.setCpf(result.getString("cpf"));
        cliente.setTelefone(result.getString("telefone"));
        cliente.setEndereco(result.getString("endereco"));
        cliente.setAtivo(result.getBoolean("ativo"));
        return cliente;
    }
}
