package br.com.debcontrol.config;

import br.com.debcontrol.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {
    private static final DatabaseConfig CONFIG = DatabaseConfig.load();

    private ConnectionFactory() {
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(CONFIG.url(), CONFIG.user(), CONFIG.password());
        } catch (SQLException exception) {
            throw new DatabaseException(
                    "Não foi possível conectar ao MySQL. Confira o servidor, o banco e config/database.properties.",
                    exception);
        }
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
