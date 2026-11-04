package br.com.debcontrol.config;

import br.com.debcontrol.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class DatabaseConfig {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/mercado_fiado?useSSL=false&allowPublicKeyRetrieval=true&connectTimeout=5000";
    private static final String DEFAULT_USER = "root";
    private static final Path CONFIG_PATH = Path.of("config", "database.properties");

    private final String url;
    private final String user;
    private final String password;

    private DatabaseConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public static DatabaseConfig load() {
        Properties properties = new Properties();
        if (Files.exists(CONFIG_PATH)) {
            try (InputStream input = Files.newInputStream(CONFIG_PATH)) {
                properties.load(input);
            } catch (IOException exception) {
                throw new DatabaseException("Não foi possível ler config/database.properties.", exception);
            }
        }

        String url = envOrProperty("FIADO_DB_URL", properties, "db.url", DEFAULT_URL);
        String user = envOrProperty("FIADO_DB_USER", properties, "db.user", DEFAULT_USER);
        String password = envOrProperty("FIADO_DB_PASSWORD", properties, "db.password", "");
        return new DatabaseConfig(url, user, password);
    }

    private static String envOrProperty(String env, Properties properties, String key, String defaultValue) {
        String environmentValue = System.getenv(env);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }
        String value = properties.getProperty(key, defaultValue);
        // Espaços podem fazer parte da senha; os demais parâmetros são normalizados.
        return "db.password".equals(key) ? value : value.trim();
    }

    public String url() { return url; }
    public String user() { return user; }
    public String password() { return password; }
}
