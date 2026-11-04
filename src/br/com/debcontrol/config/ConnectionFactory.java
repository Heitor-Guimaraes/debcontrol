package br.com.debcontrol.config;

import br.com.debcontrol.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {
    private static DatabaseConfig config;

    private ConnectionFactory() {
    }

    public static Connection getConnection() {
        try {
            DatabaseConfig atual = config();
            return DriverManager.getConnection(atual.url(), atual.user(), atual.password());
        } catch (SQLException exception) {
            if (String.valueOf(exception.getMessage()).contains("No suitable driver")) {
                throw new DatabaseException(
                        "Driver do MySQL não encontrado. Coloque o Connector/J em lib/mysql-connector-j.jar.",
                        exception);
            }
            throw new DatabaseException(
                    "Não foi possível conectar ao MySQL. Confira o servidor, o banco e config/database.properties.",
                    exception);
        }
    }

    /** Leitura tardia: um erro na configuração vira mensagem na tela, e não falha de inicialização da classe. */
    private static synchronized DatabaseConfig config() {
        if (config == null) config = DatabaseConfig.load();
        return config;
    }

    public static void testConnection() {
        try (Connection connection = getConnection()) {
            if (!connection.isValid(3)) {
                throw new DatabaseException("A conexão com o MySQL não está válida.", null);
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Erro ao validar a conexão com o MySQL.", exception);
        }
    }
}
