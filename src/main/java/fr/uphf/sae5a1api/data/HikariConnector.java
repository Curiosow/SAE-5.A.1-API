package fr.uphf.sae5a1api.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

public class HikariConnector {

    private static HikariConnector INSTANCE;
    private final HikariDataSource dataSource;

    private HikariConnector(String host, String user, String password, String database, int port) {
        INSTANCE = this;
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:postgresql://%s:%d/%s", host, port, database));
        config.setUsername(user);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setDriverClassName("org.postgresql.Driver");
        for (String s : Arrays.asList(
                "useServerPrepStmts", "useLocalSessionState", "rewriteBatchedStatements", "cacheResultSetMetadata",
                "cacheServerConfiguration", "elideSetAutoCommits", "maintainTimeStats")) {
            config.addDataSourceProperty(s, true);
        }
        config.addDataSourceProperty("maintainTimeStats", false);

        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    public static HikariConnector create(String host, String user, String password, String database, int port) throws Exception {
        if(INSTANCE != null)
            throw new Exception("HikariConnector already exist");
        if(host == null || host.length() == 0) throw new IllegalArgumentException("Database host cannot be null/empty");
        if(user == null || user.length() == 0) throw new IllegalArgumentException("Database host cannot be null/empty");
        if(password == null || password.length() == 0) throw new IllegalArgumentException("Database host cannot be null/empty");
        if(database == null || database.length() == 0) throw new IllegalArgumentException("Database host cannot be null/empty");

        return new HikariConnector(host, user, password, database, port);
    }

    public static HikariConnector get() {
        return INSTANCE;
    }

    public void close() {
        if(!this.dataSource.isClosed())
            this.dataSource.close();
    }
}
