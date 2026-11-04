package br.com.debcontrol.dao;

import br.com.debcontrol.config.ConnectionFactory;
import br.com.debcontrol.exception.DatabaseException;
import br.com.debcontrol.model.Cobranca;
import br.com.debcontrol.model.MeioContato;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CobrancaDAO {
    public Cobranca inserir(Cobranca cobranca) {
        String sql = """
                INSERT INTO cobranca
                    (id_venda, id_funcionario, data_cobranca, meio_contato,
                     resultado, proximo_contato, observacao)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, cobranca.getIdVenda());
            statement.setLong(2, cobranca.getIdFuncionario());
            statement.setTimestamp(3, Timestamp.valueOf(cobranca.getDataCobranca()));
            statement.setString(4, cobranca.getMeioContato().name());
            statement.setString(5, cobranca.getResultado());
            if (cobranca.getProximoContato() == null) statement.setNull(6, Types.DATE);
            else statement.setDate(6, Date.valueOf(cobranca.getProximoContato()));
            statement.setString(7, cobranca.getObservacao());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) cobranca.setIdCobranca(keys.getLong(1));
            }
            return cobranca;
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao registrar a cobrança.", exception);
        }
    }

    public List<Cobranca> listar(String filtro) {
        String search = filtro == null ? "" : filtro.trim();
        String sql = """
                SELECT co.id_cobranca, co.id_venda, co.id_funcionario,
                       cl.nome AS cliente_nome, f.nome AS funcionario_nome,
                       co.data_cobranca, co.meio_contato, co.resultado,
                       co.proximo_contato, co.observacao
                FROM cobranca co
                JOIN venda_fiada v ON v.id_venda = co.id_venda
                JOIN cliente cl ON cl.id_cliente = v.id_cliente
                JOIN funcionario f ON f.id_funcionario = co.id_funcionario
                WHERE (? = '' OR cl.nome LIKE ? OR f.nome LIKE ? OR CAST(co.id_venda AS CHAR) LIKE ?)
                ORDER BY co.data_cobranca DESC, co.id_cobranca DESC
                """;
        List<Cobranca> cobrancas = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            String like = "%" + search + "%";
            statement.setString(1, search);
            statement.setString(2, like);
            statement.setString(3, like);
            statement.setString(4, like);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) cobrancas.add(mapear(result));
            }
            return cobrancas;
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao consultar as cobranças.", exception);
        }
    }

    private Cobranca mapear(ResultSet result) throws SQLException {
        Cobranca cobranca = new Cobranca();
        cobranca.setIdCobranca(result.getLong("id_cobranca"));
        cobranca.setIdVenda(result.getLong("id_venda"));
        cobranca.setIdFuncionario(result.getLong("id_funcionario"));
        cobranca.setClienteNome(result.getString("cliente_nome"));
        cobranca.setFuncionarioNome(result.getString("funcionario_nome"));
        cobranca.setDataCobranca(result.getTimestamp("data_cobranca").toLocalDateTime());
        cobranca.setMeioContato(MeioContato.valueOf(result.getString("meio_contato")));
        cobranca.setResultado(result.getString("resultado"));
        Date proximo = result.getDate("proximo_contato");
        if (proximo != null) cobranca.setProximoContato(proximo.toLocalDate());
        cobranca.setObservacao(result.getString("observacao"));
        return cobranca;
    }
}
