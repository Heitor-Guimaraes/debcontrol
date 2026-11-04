package br.com.debcontrol.dao;

import br.com.debcontrol.config.ConnectionFactory;
import br.com.debcontrol.exception.DatabaseException;
import br.com.debcontrol.model.Funcionario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FuncionarioDAO {
    public boolean excluir(long idFuncionario) {
        String sql = "DELETE FROM funcionario WHERE id_funcionario = ? "
                + "AND NOT EXISTS (SELECT 1 FROM venda_fiada v WHERE v.id_funcionario = funcionario.id_funcionario) "
                + "AND NOT EXISTS (SELECT 1 FROM pagamento p WHERE p.id_funcionario = funcionario.id_funcionario) "
                + "AND NOT EXISTS (SELECT 1 FROM cobranca c WHERE c.id_funcionario = funcionario.id_funcionario)";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, idFuncionario);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new DatabaseException("Não foi possível excluir o funcionário. Verifique se possui histórico.", exception);
        }
    }

    public Funcionario inserir(Funcionario funcionario) {
        String sql = "INSERT INTO funcionario (nome, cargo, login, senha_hash, ativo) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(statement, funcionario);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) funcionario.setIdFuncionario(keys.getLong(1));
            }
            return funcionario;
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao cadastrar o funcionário.", exception);
        }
    }

    public void atualizar(Funcionario funcionario) {
        String sql = "UPDATE funcionario SET nome=?, cargo=?, login=?, senha_hash=?, ativo=? WHERE id_funcionario=?";
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            preencher(statement, funcionario);
            statement.setLong(6, funcionario.getIdFuncionario());
            if (statement.executeUpdate() == 0) throw new DatabaseException("Funcionário não encontrado.", null);
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao atualizar o funcionário.", exception);
        }
    }

    public List<Funcionario> listar(String filtro, boolean somenteAtivos) {
        String search = filtro == null ? "" : filtro.trim();
        String sql = """
                SELECT id_funcionario, nome, cargo, login, senha_hash, ativo
                FROM funcionario
                WHERE (? = '' OR nome LIKE ? OR cargo LIKE ? OR login LIKE ?)
                  AND (? = FALSE OR ativo = TRUE)
                ORDER BY nome
                """;
        List<Funcionario> funcionarios = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String like = "%" + search + "%";
            statement.setString(1, search);
            statement.setString(2, like);
            statement.setString(3, like);
            statement.setString(4, like);
            statement.setBoolean(5, somenteAtivos);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) funcionarios.add(mapear(result));
            }
            return funcionarios;
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao consultar os funcionários.", exception);
        }
    }

    public Optional<Funcionario> buscarPorId(long idFuncionario) {
        return buscar("SELECT id_funcionario, nome, cargo, login, senha_hash, ativo FROM funcionario WHERE id_funcionario=?", idFuncionario);
    }

    public Optional<Funcionario> buscarPorLogin(String login) {
        return buscar("SELECT id_funcionario, nome, cargo, login, senha_hash, ativo FROM funcionario WHERE login=?", login);
    }

    public void desativar(long idFuncionario) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE funcionario SET ativo=FALSE WHERE id_funcionario=?")) {
            statement.setLong(1, idFuncionario);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao desativar o funcionário.", exception);
        }
    }

    private Optional<Funcionario> buscar(String sql, Object parametro) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, parametro);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapear(result)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao buscar o funcionário.", exception);
        }
    }

    private void preencher(PreparedStatement statement, Funcionario funcionario) throws SQLException {
        statement.setString(1, funcionario.getNome());
        statement.setString(2, funcionario.getCargo());
        statement.setString(3, funcionario.getLogin());
        statement.setString(4, funcionario.getSenhaHash());
        statement.setBoolean(5, funcionario.isAtivo());
    }

    private Funcionario mapear(ResultSet result) throws SQLException {
        Funcionario funcionario = new Funcionario();
        funcionario.setIdFuncionario(result.getLong("id_funcionario"));
        funcionario.setNome(result.getString("nome"));
        funcionario.setCargo(result.getString("cargo"));
        funcionario.setLogin(result.getString("login"));
        funcionario.setSenhaHash(result.getString("senha_hash"));
        funcionario.setAtivo(result.getBoolean("ativo"));
        return funcionario;
    }
}
